package com.example.spinboxmusicapplication

import CartItem
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CartAdapter(private val cartList: MutableList<CartItem>, private val onAddItemClick: (CartItem) -> Unit, private val onRemoveItemClick: (CartItem) -> Unit) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartList[position]
        holder.bind(cartItem)
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val albumTitleTextView: TextView = itemView.findViewById(R.id.cartAlbumTitle)
        private val albumArtistTextView: TextView = itemView.findViewById(R.id.cartAlbumArtist)
        private val albumPriceTextView: TextView = itemView.findViewById(R.id.cartAlbumPrice)
        private val albumQuantityTextView: TextView = itemView.findViewById(R.id.cartAlbumQuantity)

        private val addIcon: ImageView = itemView.findViewById(R.id.albumAddIcon)
        private val removeIcon: ImageView = itemView.findViewById(R.id.albumRemoveIcon)

        fun bind(cartItem: CartItem) {
            albumTitleTextView.text = cartItem.title
            albumArtistTextView.text = cartItem.artist
            albumQuantityTextView.text = "Adet: ${cartItem.quantity}"
            albumPriceTextView.text = "Fiyat: ${cartItem.price * cartItem.quantity} ₺"

            addIcon.setOnClickListener{
                onAddItemClick(cartItem)
            }

            removeIcon.setOnClickListener {
                onRemoveItemClick(cartItem) // Tıklama olayını tetikliyoruz
            }
        }
    }
}