package com.example.spinboxmusicapplication

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spinboxmusicapplication.databinding.ActivityEditProductBinding
import com.google.firebase.database.FirebaseDatabase

@Suppress("DEPRECATION")
class EditProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProductBinding
    private val db = FirebaseDatabase.getInstance().getReference("products")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Gelen ürünü al
        val product = intent.getSerializableExtra("product") as Product
        if (product != null) {
            // Mevcut ürün bilgilerini editText'lere set et
            val artist = product.artist
            binding.titleEdit.setText(product.title)
            binding.cdPriceEdit.setText(product.cdPrice.toString())
            binding.lpPriceEdit.setText(product.lpPrice.toString())
            binding.stockEdit.setText(product.stock.toString())
        }

        // Kaydet butonuna tıklandığında veritabanını güncelle
        binding.saveButton.setOnClickListener {
            val updatedProduct = Product(
                title = binding.titleEdit.text.toString(),
                cdPrice = binding.cdPriceEdit.text.toString().toDouble(),
                lpPrice = binding.lpPriceEdit.text.toString().toDouble(),
                stock = binding.stockEdit.text.toString().toInt(),
                genre = product?.genre ?: "",
                artist = product?.artist ?: "",
                country = product?.country ?: "",
                year = product?.year ?: 0,
                rating = product?.rating ?: 0.0,
                imageUrl = product?.imageUrl ?: "",
            )

            // Firebase'deki ilgili ürünü güncelle
            db.child(updatedProduct.title).setValue(updatedProduct)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Ürün başarıyla güncellendi", Toast.LENGTH_SHORT).show()
                        finish()  // Güncelleme sonrası activity'yi kapat
                    } else {
                        Toast.makeText(this, "Güncelleme başarısız: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
