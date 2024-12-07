package com.example.spinboxmusicapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import Album
import android.content.Intent

class AlbumAdapter(private val albumList: List<Album>) :
    RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val shopDetailButton = itemView.findViewById<ImageView>(R.id.albumDetailIcon)
        val albumTitle: TextView = itemView.findViewById(R.id.albumTitle)
        val cdPrice: TextView = itemView.findViewById(R.id.cdPrice)
        val lpPrice: TextView = itemView.findViewById(R.id.lpPrice)
        val albumArtist: TextView = itemView.findViewById(R.id.albumArtist) // TextView for artist
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.album_item, parent, false)
        return AlbumViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album = albumList[position]

        holder.albumTitle.text = album.title
        holder.cdPrice.text = "CD: ${album.cdPrice?.toString() ?: "Bilinmiyor"} ₺"
        holder.lpPrice.text = "LP: ${album.lpPrice?.toString() ?: "Bilinmiyor"} ₺"
        holder.albumArtist.text = album.artist

        // Tıklama olayını ekleyelim
        holder.shopDetailButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, ShopDetailActivity::class.java)
            intent.putExtra("title", album.title)
            intent.putExtra("artist", album.artist)
            intent.putExtra("cdPrice", album.cdPrice)
            intent.putExtra("lpPrice", album.lpPrice)
            intent.putExtra("year", album.year)
            intent.putExtra("country", album.country)
            intent.putExtra("rating", album.rating)
            intent.putExtra("genre", album.genre)
            intent.putExtra("stock", album.stock)

            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = albumList.size
}
