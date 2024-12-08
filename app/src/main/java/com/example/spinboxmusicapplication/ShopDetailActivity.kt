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
import android.util.Log
import androidx.activity.enableEdgeToEdge
import com.google.firebase.database.*

class ShopDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShopDetailBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseDatabase: FirebaseDatabase
    private var selectedPrice: Double = 0.0

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_detail)
        binding = ActivityShopDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase and Firestore başlatma
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()

        // Drawer kurulumu
        drawerLayout = binding.drawerLayout
        val shopDetailNavView = binding.shopDetailNavView

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val title = intent.getStringExtra("title")
        val artist = intent.getStringExtra("artist")
        val cdPrice = intent.getDoubleExtra("cdPrice", 0.0)
        val lpPrice = intent.getDoubleExtra("lpPrice", 0.0)
        val year = intent.getIntExtra("year", 0)
        val country = intent.getStringExtra("country")
        val rating = intent.getDoubleExtra("rating", 0.0)
        val genre = intent.getStringExtra("genre")
        val stock = intent.getIntExtra("stock", 0)

        findViewById<TextView>(R.id.shopDetailAlbumTitle).text = title
        findViewById<TextView>(R.id.shopDetailAlbumArtist).text = artist
        findViewById<TextView>(R.id.shopDetailAlbumYear).text = "$year"
        findViewById<TextView>(R.id.shopDetailAlbumCountry).text = country
        findViewById<TextView>(R.id.shopDetailAlbumRating).text = "Puan: $rating / 5.0"
        findViewById<TextView>(R.id.shopDetailAlbumGenre).text = "Tür: $genre"
        findViewById<TextView>(R.id.shopDetailStock).text = "Bu üründen son $stock adet kaldı!"
        findViewById<TextView>(R.id.shopDetailCdPrice).text = "CD: $cdPrice ₺"
        findViewById<TextView>(R.id.shopDetailLpPrice).text = "LP: $lpPrice ₺"

        // Fiyat seçme işlemi
        binding.priceOptionGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.shopDetailCdPrice -> {
                    selectedPrice = cdPrice // CD fiyatını seçtik
                    Log.d("ShopDetail", "CD Price selected: $selectedPrice")  // Fiyatı log'la
                }
                R.id.navOrders -> {
                    startActivity(Intent(this, OrdersActivity::class.java))
                }
                R.id.shopDetailLpPrice -> {
                    selectedPrice = lpPrice // LP fiyatını seçtik
                    Log.d("ShopDetail", "LP Price selected: $selectedPrice")  // Fiyatı log'la
                }
            }
        }

        // Eğer stok 0 ise butonu devre dışı bırak
        if (stock <= 0) {
            binding.shopDetailAddToCartButton.isEnabled = false
            binding.shopDetailAddToCartButton.setText("Stokta Yok")
            Toast.makeText(this, "Bu ürün şu anda stokta yok.", Toast.LENGTH_SHORT).show()
        } else {
            // Stok varsa, "Sepete Ekle" işlemini etkinleştir
            binding.shopDetailAddToCartButton.setOnClickListener {
                if (selectedPrice == 0.0) {
                    Toast.makeText(applicationContext, "Lütfen bir fiyat seçin.", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d("ShopDetail", "Price before addToCart: $selectedPrice")
                    addToCart(title ?: "", artist ?: "", selectedPrice)
                }
            }
        }


        // Navigation Drawer Yönlendirmeleri
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

        //Drawer başlatma
        binding.navigationDrawerImageView.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.cartImageView.setOnClickListener{
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }

        binding.userImageView.setOnClickListener {
            val user = firebaseAuth.currentUser
            user?.let {
                val uid = user.uid
                val database = FirebaseDatabase.getInstance().getReference("users")

                database.child(uid).get().addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        val role = snapshot.child("role").value.toString()
                        when (role) {
                            "admin" -> {
                                startActivity(Intent(this, AdProfileActivity::class.java))
                            }
                            "user" -> {
                                startActivity(Intent(this, ProfileActivity::class.java))
                            }
                            else -> {
                                Toast.makeText(this, "Bilinmeyen rol.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Kullanıcı verisi bulunamadı.", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener { e ->
                    e.printStackTrace()
                    Toast.makeText(this, "Veritabanı hatası: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Currency fetch logic
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

    // Sepete Ekleme İşlemi

    // Sepete ürün ekleme işlemi
    private fun addToCart(productTitle: String, artist: String, price: Double) {
        Log.d("ShopDetail", "Price in addToCart: $price")
        val userId = firebaseAuth.currentUser?.uid
        val cartRef = firebaseDatabase.getReference("cart").child(userId.toString()).child(productTitle)

        cartRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                // Eğer ürün sepette yoksa
                if (currentData.value == null) {
                    currentData.value = mapOf(
                        "title" to productTitle,
                        "artist" to artist,
                        "price" to price,
                        "quantity" to 1
                    )
                } else {
                    // Eğer ürün zaten sepette varsa, quantity artırılırken fiyatı da ekle
                    val cartItem = currentData.value as Map<String, Any>
                    val quantity = (cartItem["quantity"] as Long).toInt()
                    val existingPrice = (cartItem["price"] as? Double) ?: 0.0

                    // Burada price'ı doğru şekilde hesaplayarak, sepete ekle
                    currentData.value = cartItem.toMutableMap().apply {
                        // quantity artırılırken eski fiyatla birlikte toplam fiyatı güncelle
                        put("quantity", quantity + 1)
                        put("price", price * (quantity + 1)) // price'ı quantity ile çarpıyoruz
                    }
                }
                return Transaction.success(currentData)
            }

            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                if (committed) {
                    Toast.makeText(applicationContext, "Ürün sepete eklendi!", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("AddToCartError", "Transaction failed: ${error?.message}")
                    Toast.makeText(applicationContext, "Bir hata oluştu. Lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}