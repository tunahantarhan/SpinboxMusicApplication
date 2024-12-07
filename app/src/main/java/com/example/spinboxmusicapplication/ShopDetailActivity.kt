package com.example.spinboxmusicapplication

import Album
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.spinboxmusicapplication.databinding.ActivityShopDetailBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.delay
import java.io.InputStreamReader
import java.net.URL
import java.util.zip.Inflater
import javax.net.ssl.HttpsURLConnection
import android.annotation.SuppressLint
import androidx.activity.enableEdgeToEdge
import com.google.firebase.database.*

class ShopDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShopDetailBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_detail)
        binding = ActivityShopDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase ve Firestore başlatıldı
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Drawer setup
        drawerLayout = binding.drawerLayout
        val shopDetailNavView = binding.shopDetailNavView

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Intent'ten verileri al
        val title = intent.getStringExtra("title")
        val artist = intent.getStringExtra("artist")
        val cdPrice = intent.getDoubleExtra("cdPrice", 0.0)
        val lpPrice = intent.getDoubleExtra("lpPrice", 0.0)
        val year = intent.getIntExtra("year", 0)
        val country = intent.getStringExtra("country")
        val rating = intent.getDoubleExtra("rating", 0.0)
        val genre = intent.getStringExtra("genre")
        val stock = intent.getIntExtra("stock", 0) // Stoğu al


        // Verileri UI'ya bağla
        findViewById<TextView>(R.id.shopDetailAlbumTitle).text = title
        findViewById<TextView>(R.id.shopDetailAlbumArtist).text = artist
        findViewById<TextView>(R.id.shopDetailCdPrice).text = "CD: $cdPrice ₺"
        findViewById<TextView>(R.id.shopDetailLpPrice).text = "LP: $lpPrice ₺"
        findViewById<TextView>(R.id.shopDetailAlbumYear).text = "$year"
        findViewById<TextView>(R.id.shopDetailAlbumCountry).text = country
        findViewById<TextView>(R.id.shopDetailAlbumRating).text = "Puan: $rating / 5"
        findViewById<TextView>(R.id.shopDetailAlbumGenre).text = "Tür: $genre"
        findViewById<TextView>(R.id.shopDetailStock).text = "Bu üründen son $stock adet kaldı!" // Stoğu göster

        // Navigation Drawer işlemleri
        shopDetailNavView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navHome -> {
                    startActivity(Intent(this, MainActivity::class.java))
                }
                R.id.navLogOut -> {
                    firebaseAuth.signOut()
                    startActivity(Intent(this, SignInActivity::class.java))
                    finish()
                }
            }
            true
        }

        // Drawer Başlatma
        binding.navigationDrawerImageView.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START) // Drawer açılıyor
        }

        // User Profile Navigation
        binding.userImageView.setOnClickListener {
            val user = firebaseAuth.currentUser
            user?.let {
                val uid = user.uid
                firestore.collection("users").document(uid).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val role = document.get("role").toString()
                            when (role) {
                                "admin" -> startActivity(Intent(this, AdProfileActivity::class.java))
                                "user" -> startActivity(Intent(this, ProfileActivity::class.java))
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                    }
            }
        }

        // Currency Data Fetch
        fetchCurrencyDataUsd().start()
        fetchCurrencyDataEur().start()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    // Fetch USD/TRY currency data
    private fun fetchCurrencyDataUsd(): Thread {
        return Thread {
            val currencyUsdTry = binding.tryUsd
            val link = URL("https://open.er-api.com/v6/latest/usd")
            val connection = link.openConnection() as HttpsURLConnection

            if (connection.responseCode == 200) {
                val inputSystem = connection.inputStream
                val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                val request = Gson().fromJson(inputStreamReader, RequestUsd::class.java)
                inputSystem.close()
                inputStreamReader.close()

                val usdTryRate = request.rates["TRY"]
                if (usdTryRate != null) {
                    currencyValueUsd(usdTryRate)
                }
            } else {
                currencyUsdTry.text = "Bağlantı Hatası"
            }
        }
    }

    // Fetch EUR/TRY currency data
    private fun fetchCurrencyDataEur(): Thread {
        return Thread {
            val currencyEurTry = binding.tryEur
            val link = URL("https://open.er-api.com/v6/latest/eur")
            val connection = link.openConnection() as HttpsURLConnection

            if (connection.responseCode == 200) {
                val inputSystem = connection.inputStream
                val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                val request = Gson().fromJson(inputStreamReader, RequestEur::class.java)
                inputSystem.close()
                inputStreamReader.close()

                val eurTryRate = request.rates["TRY"]
                currencyValueEur(eurTryRate)
            } else {
                currencyEurTry.text = "Bağlantı Hatası"
            }
        }
    }

    private fun currencyValueUsd(usdTryRate: Double) {
        runOnUiThread {
            binding.tryUsd.text = String.format("$ = $usdTryRate")
        }
    }

    private fun currencyValueEur(eurTryRate: Double?) {
        runOnUiThread {
            binding.tryEur.text = String.format("€ = $eurTryRate")
        }
    }
}