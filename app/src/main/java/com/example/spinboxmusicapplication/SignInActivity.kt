package com.example.spinboxmusicapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spinboxmusicapplication.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

@Suppress("DEPRECATION")
class SignInActivity : AppCompatActivity(){

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textView.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener{
            val email = binding.emailEnter.text.toString() //emailEnter kısmına girilen email'i alır.
            val pass = binding.passwordEnter.text.toString() //passwordEnter kısmına girilen password'ü alır.

            //Tüm kutucuklar doluysa ve girilen şifreler uyuşuyorsa:
            if (email.isNotEmpty() && pass.isNotEmpty()){
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java) //Başarılı bir şekilde giriş yaparsa MainActivity'ye yönlendirilir.
                        startActivity(intent)
                        finish() //Geri tuşu ile login ekranına dönülmemesi için.
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show() //Eğer işlem başarılı bir şekilde gerçekleşmezse, sorun ne ise onu geribildirim gönderir.
                    }
                }
            }
            else {
                Toast.makeText(this, "Lütfen tüm bilgilerin girildiğinden emin olunuz.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        //En son giriş yapan kullanıcı çıkış yapmamışsa her uygulamaya girişte tekrar hesaba giriş yapılmasını önler.
        if (firebaseAuth.currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}