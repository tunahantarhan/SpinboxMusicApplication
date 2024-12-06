package com.example.spinboxmusicapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import Album

class AlbumAdapter(private val albumList: List<Album>) :
    RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val albumImage: ImageView = itemView.findViewById(R.id.albumImage)
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
        holder.cdPrice.text = "CD: ${album.cdPrice} ₺"
        holder.lpPrice.text = "LP: ${album.lpPrice} ₺"
        holder.albumArtist.text = album.artist

        // Glide ile fotoğrafı alma
        Glide.with(holder.itemView.context)
            .load(album.imageUrl)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.baseline_favorite_border_24)
            .into(holder.albumImage)
    }

    override fun getItemCount(): Int = albumList.size
}