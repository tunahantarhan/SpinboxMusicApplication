package com.example.spinboxmusicapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spinboxmusicapplication.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

@Suppress("DEPRECATION")
class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var usersDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        usersDatabase = FirebaseDatabase.getInstance().getReference("users")

        // Kullanıcı "Kayıt Ol" butonuna tıkladığında:
        binding.button.setOnClickListener {
            val email = binding.emailEnter.text.toString()
            val pass = binding.passwordEnter.text.toString()
            val confirmPass = binding.confirmPassEnter.text.toString()

            // Tüm kutucuklar doluysa ve girilen şifreler uyuşuyorsa:
            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass != confirmPass) {
                    Toast.makeText(
                        this,
                        "Lütfen girilen şifrelerin aynı olduğundan emin olunuz.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val currentUser = firebaseAuth.currentUser
                            if (currentUser != null) {
                                val uid = currentUser.uid

                                // User data'yı 'users' koleksiyonuna kaydetme ve cart'ı null olarak başlatma
                                val userData = hashMapOf(
                                    "email" to email,
                                    "role" to "user",
                                )

                                usersDatabase.child(uid).setValue(userData)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            this,
                                            "Kayıt başarılı, giriş ekranına yönlendiriliyorsunuz.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        val intent = Intent(this, SignInActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        e.printStackTrace()
                                        Toast.makeText(
                                            this,
                                            "Kullanıcı verisi kaydedilemedi: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(
                    this,
                    "Lütfen tüm bilgilerin girildiğinden emin olunuz.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Kullanıcı zaten kayıtlıysa giriş yapmasını belirten yazıya tıklayınca:
        binding.textView.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }
}
