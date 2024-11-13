package com.example.spinboxmusicapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.spinboxmusicapplication.databinding.ActivityAdMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Suppress("DEPRECATION")
class AdMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance() // Firestore'u da başlatmayı unutmayın.

        // firebaseAuth başlatıldıktan sonra currentUser'ı güncelleyin
        val currentUser = firebaseAuth.currentUser

        binding = ActivityAdMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Kullanıcı görüntüleme ve çıkış işlemleri
        binding.userImageView.setOnClickListener {
            val intent = Intent(this, AdProfileActivity::class.java)
            startActivity(intent)
        }

        binding.homepageImageView.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
