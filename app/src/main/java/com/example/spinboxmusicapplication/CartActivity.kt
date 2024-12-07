package com.example.spinboxmusicapplication

import CartItem
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.example.spinboxmusicapplication.databinding.ActivityCartBinding
import com.google.gson.Gson
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

@Suppress("DEPRECATION")
class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var cartList: MutableList<CartItem>
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase referanslarını başlatıyoruz
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()

        drawerLayout  = binding.drawerLayout
        val navView = binding.navView

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener{
            when(it.itemId){
                R.id.navHome -> {
                    startActivity(Intent(this, MainActivity::class.java))
                }
                R.id.navLogOut -> {
                    firebaseAuth.signOut()
                    finish()
                    startActivity(Intent(this, SignInActivity::class.java))
                    finish()
                }
            }
            true
        }

        // RecyclerView ve Adapter'ı ayarlıyoruz
        cartRecyclerView = findViewById(R.id.cartRecyclerView)
        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        cartList = mutableListOf()

        binding.navigationDrawerImageView.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START) // Drawer açılıyor
        }

        binding.cartClearIcon.setOnClickListener{
            clearCartContents()
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

        cartAdapter = CartAdapter(cartList, { cartItem ->
            handleAddItem(cartItem)
        }, { cartItem ->
            handleRemoveItem(cartItem)
        })
        cartRecyclerView.adapter = cartAdapter

        // Sepet verilerini yüklüyoruz
        loadCartItems()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.spinbox_brown)


        fetchCurrencyDataUsd().start()
        fetchCurrencyDataEur().start()
    }

    // CartItem'ı azaltma ve kaldırma işlemi
    private fun handleRemoveItem(cartItem: CartItem) {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            val cartRef = firebaseDatabase.getReference("cart").child(userId).child(cartItem.title)

            if (cartItem.quantity > 1) {
                // Quantity'yi 1 azaltma
                cartRef.child("quantity").setValue(cartItem.quantity - 1)
            } else {
                // Quantity 1 ise, öğeyi sepetten kaldırma
                cartRef.removeValue()
            }
        }
    }

    //CartItem'ı arttırma işlemi
    private fun handleAddItem(cartItem: CartItem) {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            val cartRef = firebaseDatabase.getReference("cart").child(userId).child(cartItem.title)
            cartRef.child("quantity").setValue(cartItem.quantity + 1)
        }
    }

    // Sepet öğelerini yükleme
    private fun loadCartItems() {
        val userId = firebaseAuth.currentUser?.uid
        val cartRef = firebaseDatabase.getReference("cart").child(userId.toString())

        cartRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cartList.clear()  // Sepet temizleniyor
                var totalPrice = 0.0 // Toplam fiyat değişkenini başlatıyoruz

                for (cartSnapshot in snapshot.children) {
                    val title = cartSnapshot.child("title").getValue(String::class.java) ?: ""
                    val artist = cartSnapshot.child("artist").getValue(String::class.java) ?: ""
                    val price = cartSnapshot.child("price").getValue(Double::class.java) ?: 0.0
                    val quantity = cartSnapshot.child("quantity").getValue(Long::class.java) ?: 0

                    // CartItem oluşturuluyor
                    val cartItem = CartItem(title, artist ,price, quantity)
                    cartList.add(cartItem)

                    // Her ürünün toplam fiyatını ekliyoruz
                    totalPrice += price * quantity
                }

                // RecyclerView güncelleniyor
                cartAdapter.notifyDataSetChanged()

                // Toplam fiyat TextView'e yazdırılıyor
                binding.cartTotalPrice.text = "${totalPrice.toString()} ₺"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CartActivity, "Veriler yüklenirken bir hata oluştu", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun clearCartContents() {
        val userId = firebaseAuth.currentUser?.uid
        val cartRef = firebaseDatabase.getReference("cart").child(userId.toString())

        // Firebase'den tüm cart verilerini sil
        cartRef.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Sepet başarıyla temizlendi", Toast.LENGTH_SHORT).show()

                // RecyclerView ve toplam fiyat güncelle
                cartList.clear()
                cartAdapter.notifyDataSetChanged()
                binding.cartTotalPrice.text = "Toplam: 0.0 ₺"
            } else {
                Toast.makeText(this, "Sepeti temizlerken bir hata oluştu", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //WEB API --> USD/TRY kurunu çeker.
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

                val usdTryRate = request.rates["TRY"]  // Get the TRY rate
                if (usdTryRate != null) {
                    currencyValueUsd(usdTryRate)
                }
            } else {
                currencyUsdTry.text = "Bağlantı Hatası"
            }
        }
    }

    //WEB API --> EUR/TRY kurunu çeker.
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

                val eurTryRate = request.rates["TRY"]  // Get the TRY rate
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