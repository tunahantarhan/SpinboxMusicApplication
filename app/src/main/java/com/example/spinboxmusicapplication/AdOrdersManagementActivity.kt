package com.example.spinboxmusicapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spinboxmusicapplication.databinding.ActivityAdOrdersManagementBinding
import com.google.firebase.database.*
import java.util.Locale

@Suppress("DEPRECATION")
class AdOrdersManagementActivity : AppCompatActivity() {

    private lateinit var ordersRecyclerView: RecyclerView
    private lateinit var binding: ActivityAdOrdersManagementBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var toggle: ActionBarDrawerToggle
    private val orders = mutableListOf<Order>()
    private lateinit var firebaseDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad_orders_management)
        binding = ActivityAdOrdersManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.userImageView.setOnClickListener {
            val intent = Intent(this, AdProfileActivity::class.java)
            startActivity(intent)
        }
        binding.homepageImageView.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        ordersRecyclerView = findViewById(R.id.ordersRecyclerView)
        ordersRecyclerView.layoutManager = LinearLayoutManager(this)
        orderAdapter = OrderAdapter(orders)
        ordersRecyclerView.adapter = orderAdapter


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.spinbox_brown)

        loadAllOrders()
    }

    private fun loadAllOrders() {
        firebaseDatabase = FirebaseDatabase.getInstance()
        val ordersRef = firebaseDatabase.getReference("orders")

        ordersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orders.clear()
                for (userSnapshot in snapshot.children) { // Her kullanıcı
                    for (orderDateSnapshot in userSnapshot.children) { // Her tarih
                        for (orderSnapshot in orderDateSnapshot.children) { // Her sipariş
                            val order = orderSnapshot.getValue(Order::class.java)
                            order?.let {
                                orders.add(it)
                            }
                        }
                    }
                }
                orderAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AdOrdersManagement", "Failed to load orders: ${error.message}")
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }
}
