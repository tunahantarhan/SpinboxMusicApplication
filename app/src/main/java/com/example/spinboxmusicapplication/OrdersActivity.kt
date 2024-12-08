package com.example.spinboxmusicapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.example.spinboxmusicapplication.databinding.ActivityOrdersBinding
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection


@Suppress("DEPRECATION")
class OrdersActivity : AppCompatActivity() {

    private lateinit var ordersRecyclerView: RecyclerView
    private lateinit var binding: ActivityOrdersBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var toggle: ActionBarDrawerToggle
    private val orders = mutableListOf<Order>()
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)
        binding = ActivityOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        ordersRecyclerView = findViewById(R.id.ordersRecyclerView)
        ordersRecyclerView.layoutManager = LinearLayoutManager(this)
        orderAdapter = OrderAdapter(orders)
        ordersRecyclerView.adapter = orderAdapter

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.spinbox_brown)


        fetchCurrencyDataUsd().start()
        fetchCurrencyDataEur().start()

        loadOrders()
    }

    private fun loadOrders() {
        val userId = firebaseAuth.currentUser?.uid ?: return
        val ordersRef = firebaseDatabase.getReference("orders").child(userId)

        ordersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orders.clear()
                for (orderSnapshot in snapshot.children) {
                    for (orderDateSnapshot in orderSnapshot.children) {
                        val order = orderDateSnapshot.getValue(Order::class.java)
                        order?.let {
                            orders.add(it)
                        }
                    }
                }
                orderAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("OrdersActivity", "Failed to load orders: ${error.message}")
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
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
