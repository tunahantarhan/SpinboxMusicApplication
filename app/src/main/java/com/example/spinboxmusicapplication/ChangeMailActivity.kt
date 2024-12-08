package com.example.spinboxmusicapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spinboxmusicapplication.databinding.ActivityChangeMailBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Suppress("DEPRECATION")
class ChangeMailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangeMailBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        binding = ActivityChangeMailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        val currentUser = firebaseAuth.currentUser
        val currentUserEmail = currentUser?.email
        val button = binding.button

        button.setOnClickListener{
            val newEmail = binding.newEmailEnter.text.toString()
            val password = binding.changeMailPasswordEnter.text.toString()

            val reauthItems = EmailAuthProvider.getCredential(currentUserEmail!!, password)

            if (newEmail.isNotEmpty() && password.isNotEmpty()) {
                currentUser.reauthenticate(reauthItems).addOnSuccessListener {
                    currentUser.updateEmail(newEmail).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val database = FirebaseDatabase.getInstance().reference
                            database.child("users").child(currentUser.uid).child("email").setValue(newEmail)
                                .addOnCompleteListener { dbTask ->
                                    if (dbTask.isSuccessful) {
                                        // İşlem başarılı
                                        Toast.makeText(this, "E-posta başarılı bir şekilde güncellendi", Toast.LENGTH_LONG).show()

                                        // Oturumdan çık ve yeniden giriş yap
                                        val intent = Intent(this, SignInActivity::class.java)
                                        startActivity(intent)
                                        firebaseAuth.signOut()
                                        finish()
                                    } else {
                                        Toast.makeText(this, "E-posta veritabanı güncellemesi başarısız oldu", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            Toast.makeText(this, "E-posta güncelleme başarısız oldu", Toast.LENGTH_LONG).show()
                        }
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Kimlik doğrulama başarısız oldu", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Lütfen tüm bilgilerin girildiğinden emin olunuz.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}