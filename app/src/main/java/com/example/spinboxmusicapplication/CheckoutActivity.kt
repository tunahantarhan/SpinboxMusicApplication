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
            completeOrder()
            Toast.makeText(applicationContext, "Siparişiniz alınmıştır", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Load cart data
        loadCartSummary()
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

        // Sepetteki ürünleri al
        cartRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Sipariş verisini oluştur
                    val orderDate = System.currentTimeMillis() // Sipariş tarihi
                    val orderMap = mutableMapOf<String, Any>()
                    val productMap = mutableMapOf<String, Any>()

                    for (itemSnapshot in snapshot.children) {
                        val title = itemSnapshot.child("title").getValue(String::class.java)
                        val price = itemSnapshot.child("price").getValue(Double::class.java)
                        val quantity = itemSnapshot.child("quantity").getValue(Int::class.java) ?: 1

                        title?.let {
                            productMap["$title"] = mapOf(
                                "productTitle" to title,
                                "price" to price,
                                "quantity" to quantity,
                                "orderDate" to orderDate
                            )
                        }
                    }

                    // Siparişi orders koleksiyonuna ekle
                    if (productMap.isNotEmpty()) {
                        ordersRef.child(orderDate.toString()).setValue(productMap).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Siparişi başarılı şekilde ekledikten sonra cart'ı temizle
                                cartRef.removeValue().addOnCompleteListener { removeTask ->
                                    if (removeTask.isSuccessful) {
                                        Log.d("CheckoutActivity", "Cart successfully cleared.")
                                    } else {
                                        Log.e("CheckoutActivity", "Failed to clear cart: ${removeTask.exception?.message}")
                                    }
                                }
                            } else {
                                Log.e("CheckoutActivity", "Failed to add order: ${task.exception?.message}")
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
}

