package com.example.spinboxmusicapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spinboxmusicapplication.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Suppress("DEPRECATION")
class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Kayıt ekranına yönlendirme
        binding.textView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Giriş yap butonuna tıklandığında
        binding.button.setOnClickListener {
            val email = binding.emailEnter.text.toString()
            val pass = binding.passwordEnter.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        user?.let {
                            val uid = user.uid
                            val database = FirebaseDatabase.getInstance().getReference("users")

                            database.child(uid).get().addOnSuccessListener { snapshot ->
                                val disabled = snapshot.child("disabled").value as? Boolean
                                if (snapshot.exists() && disabled != true) {
                                    val email = snapshot.child("email").value.toString()
                                    val role = snapshot.child("role").value.toString()
                                    Toast.makeText(
                                        this,
                                        "Giriş başarılı. Rol: ${role.uppercase()}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else if (snapshot.child("disabled").value == true) {
                                    Toast.makeText(this, "Bu kullanıcı devre dışı bırakılmış.", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this, "Kullanıcı verisi bulunamadı.", Toast.LENGTH_SHORT).show()
                                }
                            }.addOnFailureListener {
                                Toast.makeText(this, "Veritabanı hatası: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, task.exception?.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Lütfen tüm bilgilerin girildiğinden emin olunuz.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Oturum açılmışsa çıkış yap, tekrar giriş yapılmasını zorla
        if (firebaseAuth.currentUser != null) {
            firebaseAuth.signOut()
        }
    }
}
