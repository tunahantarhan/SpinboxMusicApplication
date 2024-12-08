package com.example.spinboxmusicapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spinboxmusicapplication.databinding.ProductItemBinding

class ProductsAdapter(
    private val products: List<Product>,
    private val onEdit: (Product) -> Unit,
    private val onDelete: (Product) -> Unit
) : RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.binding.productTitle.text = product.title
        holder.binding.productCdPrice.text = "CD Price: ${product.cdPrice}"
        holder.binding.productLpPrice.text = "LP Price: ${product.lpPrice}"
        holder.binding.productStock.text = "Stock: ${product.stock}"

        // Düzenle butonu
        holder.binding.editProductButton.setOnClickListener {
            onEdit(product)
        }

        // Silme butonu
        holder.binding.deleteProductButton.setOnClickListener {
            onDelete(product)
        }
    }

    override fun getItemCount(): Int = products.size

    class ProductViewHolder(val binding: ProductItemBinding) : RecyclerView.ViewHolder(binding.root)
}
