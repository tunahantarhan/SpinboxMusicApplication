package com.example.spinboxmusicapplication

import CartItem
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.example.spinboxmusicapplication.databinding.ActivityCartBinding
import com.google.gson.Gson
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var cartList: MutableList<CartItem> // CartItem veri modelini kullanacağız

    // FirebaseAuth ve FirebaseDatabase referanslarını tanımlıyoruz
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // Firebase referanslarını başlatıyoruz
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()

        // RecyclerView ve Adapter'ı ayarlıyoruz
        cartRecyclerView = findViewById(R.id.cartRecyclerView)
        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        cartList = mutableListOf()
        cartAdapter = CartAdapter(cartList)
        cartRecyclerView.adapter = cartAdapter

        // Sepet verilerini yüklüyoruz
        loadCartItems()
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

    private fun loadCartItems() {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            val cartRef = firebaseDatabase.getReference("cart").child(userId)

            cartRef.addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
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
                    binding.cartTotalPrice.text = "Toplam: ${totalPrice} ₺"
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@CartActivity, "Veriler yüklenirken bir hata oluştu", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "Lütfen giriş yapın", Toast.LENGTH_SHORT).show()
        }
    }}
