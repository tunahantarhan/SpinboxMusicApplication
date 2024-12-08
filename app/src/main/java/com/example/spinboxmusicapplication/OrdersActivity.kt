package com.example.spinboxmusicapplication

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class OrdersActivity : AppCompatActivity() {

    private lateinit var ordersRecyclerView: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private val orders = mutableListOf<Order>()
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        ordersRecyclerView = findViewById(R.id.ordersRecyclerView)
        ordersRecyclerView.layoutManager = LinearLayoutManager(this)
        orderAdapter = OrderAdapter(orders)
        ordersRecyclerView.adapter = orderAdapter

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
}