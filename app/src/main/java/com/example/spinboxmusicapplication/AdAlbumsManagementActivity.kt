package com.example.spinboxmusicapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spinboxmusicapplication.databinding.ActivityAdAlbumsManagementBinding
import com.example.spinboxmusicapplication.databinding.ProductItemBinding
import com.google.firebase.database.*

class AdAlbumsManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdAlbumsManagementBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductsAdapter
    private val productsList = mutableListOf<Product>()
    private val db = FirebaseDatabase.getInstance().getReference("products")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdAlbumsManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.recyclerView
        adapter = ProductsAdapter(productsList, this::editProduct, this::deleteProduct)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Firebase'den ürünleri çek
        fetchProducts()
    }

    private fun fetchProducts() {
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productsList.clear()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    if (product != null) {
                        productsList.add(product)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdAlbumsManagementActivity, "Veritabanı hatası: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Ürünü düzenleme işlemi
    private fun editProduct(product: Product) {
        val intent = Intent(this, EditProductActivity::class.java)
        intent.putExtra("product", product)
        startActivity(intent)
    }

    // Ürünü silme işlemi
    private fun deleteProduct(product: Product) {
        db.child(product.title).removeValue()
            .addOnCompleteListener {
                Toast.makeText(this, "${product.title} silindi", Toast.LENGTH_SHORT).show()
            }
    }
}
