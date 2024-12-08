package com.example.spinboxmusicapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spinboxmusicapplication.databinding.UserItemBinding

class UsersAdapter(
    private val users: List<User>,
    private val onDisable: (User) -> Unit,
    private val onDelete: (User) -> Unit
) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.binding.userEmail.text = "E-posta : ${user.email}"
        holder.binding.userRole.text = "Rol : ${user.role}"
        holder.binding.userStatus.text = if (user.disabled) "Hesap Durumu : Devre Dışı" else "Hesap Durumu : Aktif"

        // Durum güncellemesi
        holder.binding.disableButton.setOnClickListener {
            onDisable(user)
        }

        // Silme butonu
        holder.binding.deleteButton.setOnClickListener {
            onDelete(user)
        }
    }

    override fun getItemCount(): Int = users.size

    class UserViewHolder(val binding: UserItemBinding) : RecyclerView.ViewHolder(binding.root)
}