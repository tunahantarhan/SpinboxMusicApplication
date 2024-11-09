package com.example.spinboxmusicapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("CustomSplashScreen")
@Suppress("DEPRECATION")
class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val logoImageView = findViewById<ImageView>(R.id.logoImageView)
        (logoImageView.drawable as? AnimatedVectorDrawable)?.start()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        ) //Statüs bar gizleme işlemi.

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, WelcomeScreenActivity::class.java)
            startActivity(intent)
            finish()
        }, 1500) //1,5 saniye geç başlayacak, splash screen süresi olacak bu.
    }
}
