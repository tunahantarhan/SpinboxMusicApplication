package com.example.spinboxmusicapplication

import CartItem
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spinboxmusicapplication.databinding.ActivityCartBinding
import com.example.spinboxmusicapplication.databinding.ActivityCheckoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlin.toString

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
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
                R.id.navOrders -> {
                    startActivity(Intent(this, OrdersActivity::class.java))
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

        binding.navigationDrawerImageView.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START) // Drawer açılıyor
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

        binding.checkoutOrderButton.setOnClickListener{
            val creditCardNumber = binding.checkoutCreditCardLayoutEnter
            val creditCardMonth = binding.checkoutCreditCardMonthLayoutEnter
            val creditCardYear = binding.checkoutCreditCardYearLayoutEnter
            val cvv = binding.checkoutCreditCardCvvLayoutEnter

            val isValid = validateCreditCardInput(
                creditCardNumber.text.toString(),
                creditCardMonth.text.toString(),
                creditCardYear.text.toString(),
                cvv.text.toString()
            )

            if (isValid) {
                completeOrder()
                Toast.makeText(applicationContext, "Siparişiniz alınmıştır", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            else {
                Toast.makeText(this, "Lütfen tüm alanların uygun girildiğinden emin olunuz.", Toast.LENGTH_SHORT).show()
            }
        }

        loadCartSummary()

        fetchCurrencyDataUsd().start()
        fetchCurrencyDataEur().start()
    }

    fun validateCreditCardInput(cardNumber: String, month: String, year: String, cvv: String): Boolean {
        if (cardNumber.length != 16 || !cardNumber.all { it.isDigit() }) {
            return false
        }

        if (month.length != 2 || month.toInt() !in 1..12) {
            return false
        }

        if (year.length != 2 || !year.all { it.isDigit() }) {
            return false
        }

        if (cvv.length != 3 || !cvv.all { it.isDigit() }) {
            return false
        }

        return true
    }

    private fun loadCartSummary() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val cartRef = FirebaseDatabase.getInstance().getReference("cart").child(userId)

        cartRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalPrice = 0.0
                for (itemSnapshot in snapshot.children) {
                    val price = itemSnapshot.child("price").getValue(Double::class.java) ?: 0.0
                    val quantity = itemSnapshot.child("quantity").getValue(Int::class.java) ?: 1
                    totalPrice += price * quantity
                }
                updateTotalPrice(totalPrice)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("CheckoutActivity", "Failed to load cart: ${error.message}")
            }
        })
    }

    private fun updateTotalPrice(totalPrice: Double) {
        // Sepet toplamını göstereceğiniz TextView'i güncelleyin
        binding.checkoutCartTotalPriceText.text = "$totalPrice ₺"
    }

    private fun completeOrder() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val cartRef = firebaseDatabase.getReference("cart").child(userId)
        val ordersRef = FirebaseDatabase.getInstance().getReference("orders").child(userId)
        val productsRef = firebaseDatabase.getReference("products")

        // Sepetteki ürünleri al
        cartRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val orderDate = System.currentTimeMillis() // Sipariş tarihi
                    val orderMap = mutableMapOf<String, Any>()
                    val productMap = mutableMapOf<String, Any>()

                    for (itemSnapshot in snapshot.children) {
                        val title = itemSnapshot.child("title").getValue(String::class.java)
                        val price = itemSnapshot.child("price").getValue(Double::class.java)
                        val quantity = itemSnapshot.child("quantity").getValue(Int::class.java) ?: 1

                        title?.let {
                            productMap[it] = mapOf(
                                "productTitle" to title,
                                "price" to price,
                                "quantity" to quantity,
                                "orderDate" to orderDate
                            )

                            // Stok güncelle
                            productsRef.child(it).child("stock").addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(productSnapshot: DataSnapshot) {
                                    val currentStock = productSnapshot.getValue(Int::class.java) ?: 0
                                    val newStock = currentStock - quantity
                                    if (newStock >= 0) {
                                        productsRef.child(it).child("stock").setValue(newStock)
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    Log.d("CheckoutActivity", "Stok güncellendi: $it -> $newStock")
                                                } else {
                                                    Log.e("CheckoutActivity", "Stok güncelleme ERROR: ${task.exception?.message}")
                                                }
                                            }
                                    } else {
                                        Log.w("CheckoutActivity", "Yetersiz stok: $it")
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.e("CheckoutActivity", "Stok okuma HATA: ${error.message}")
                                }
                            })
                        }
                    }

                    // Siparişi orders koleksiyonuna ekle
                    if (productMap.isNotEmpty()) {
                        ordersRef.child(orderDate.toString()).setValue(productMap).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Cart'ı temizle
                                cartRef.removeValue().addOnCompleteListener { removeTask ->
                                    if (removeTask.isSuccessful) {
                                        Log.d("CheckoutActivity", "Cart başarıyla temizlendi")
                                    } else {
                                        Log.e("CheckoutActivity", "Cart temizleme ERROR: ${removeTask.exception?.message}")
                                    }
                                }
                            } else {
                                Log.e("CheckoutActivity", "Sipariş ekleme ERROR: ${task.exception?.message}")
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("CheckoutActivity", "Failed to complete order: ${error.message}")
            }
        })
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


