package com.example.spinboxmusicapplication

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.spinboxmusicapplication.databinding.ActivityWelcomeScreenBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar

class WelcomeScreenActivity: AppCompatActivity(){

    private lateinit var binding: ActivityWelcomeScreenBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        binding = ActivityWelcomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greetingText = binding.greetingTextView
        if (currentHour in 6..11){
            greetingText.text = "Günaydın!"
        }
        else if (currentHour in 12..17){
            greetingText.text = "İyi günler!"
        }
        else{
            greetingText.text = "İyi Akşamlar!"
        }

        //butona tıklayınca giriş yapma ekranına gitsin
        binding.button.setOnClickListener{
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        //kayıt olun yazısına tıklayınca kayıt olma ekranına gitsin
        binding.textView.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}