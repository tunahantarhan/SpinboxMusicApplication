package com.example.spinboxmusicapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spinboxmusicapplication.databinding.ActivityChangeMailBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

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

            if (newEmail.isNotEmpty() && password.isNotEmpty()){
                currentUser.reauthenticate(reauthItems).addOnSuccessListener{
                    currentUser.updateEmail(newEmail).addOnCompleteListener{
                        val intent = Intent(this, SignInActivity::class.java)
                        startActivity(intent)

                        Toast.makeText(this, "E-posta başarılı bir şekilde güncellendi", Toast.LENGTH_LONG).show()
                        firebaseAuth.signOut()
                        finish()
                    }.addOnFailureListener{
                        Toast.makeText(this, "E-posta güncelleme başarısız oldu", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(this, "Lütfen tüm bilgilerin girildiğinden emin olunuz.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}