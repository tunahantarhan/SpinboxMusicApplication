package com.example.spinboxmusicapplication

import Album
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spinboxmusicapplication.databinding.ActivityAlbumsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class AlbumsActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AlbumAdapter
    private val albumList = mutableListOf<Album>()
    private lateinit var binding: ActivityAlbumsBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAlbumsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase ve Firestore başlatıldı
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        database = FirebaseDatabase.getInstance().getReference("products")

        // Drawer setup
        drawerLayout = binding.drawerLayout
        val navView = binding.navView

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // RecyclerView ve Adapter'ı ayarlıyoruz
        recyclerView = binding.recyclerView
        adapter = AlbumAdapter(albumList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Firebase Realtime Database'e bağlantı
        val myRef = FirebaseDatabase.getInstance().getReference("products")

        myRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                // Veritabanından gelen verileri listeye ekleyin
                albumList.clear() // Listeyi temizle
                for (albumSnapshot in snapshot.children) {
                    val album = albumSnapshot.getValue(Album::class.java)
                    album?.let { albumList.add(it) }
                }

                // RecyclerView için adapter'e yeni veriyi gönderin
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Hata durumu
                Toast.makeText(this@AlbumsActivity, "Veri alınırken hata oluştu: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        binding.

        // Navigation Drawer işlemleri
        navView.setNavigationItemSelectedListener {
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
