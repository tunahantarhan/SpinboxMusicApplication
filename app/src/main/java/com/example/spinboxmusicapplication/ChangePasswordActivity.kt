package com.example.spinboxmusicapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spinboxmusicapplication.databinding.ActivityChangePasswordBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

@Suppress("DEPRECATION")
class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        val currentUser = firebaseAuth.currentUser
        val currentUserEmail = currentUser?.email
        val button = binding.button


        button.setOnClickListener{
            val newPassword = binding.newPasswordEnter.text.toString()
            val confirmationNewPassword =  binding.confirmNewPasswordEnter.text.toString()
            val currentPassword = binding.currentPasswordEnter.text.toString()

            val reauthItems = EmailAuthProvider.getCredential(currentUserEmail!!, currentPassword)

            if (newPassword.isNotEmpty() && currentPassword.isNotEmpty()){
                if (newPassword == confirmationNewPassword){
                    currentUser.reauthenticate(reauthItems).addOnSuccessListener{
                        currentUser.updatePassword(newPassword).addOnCompleteListener{
                            firebaseAuth.signOut()
                            val intent = Intent(this, SignInActivity::class.java)
                            startActivity(intent)
                            Toast.makeText(this, "Şifre başarılı bir şekilde güncellendi", Toast.LENGTH_LONG).show()
                            finish()
                        }.addOnFailureListener{
                            Toast.makeText(this, "Şifre güncelleme başarısız oldu", Toast.LENGTH_LONG).show()
                        }
                    }.addOnFailureListener{
                        Toast.makeText(this, "Doğrulama başarısız oldu", Toast.LENGTH_LONG).show()
                    }
                }
                else{
                    Toast.makeText(this, "Yeni şifreniz ile tekrarının aynı olduğuna emin olunuz.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Lütfen tüm bilgilerin girildiğinden emin olunuz.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}