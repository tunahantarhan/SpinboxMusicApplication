package com.example.spinboxmusicapplication

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spinboxmusicapplication.databinding.ActivityAdUserManagementBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class AdUserManagementActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdUserManagementBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UsersAdapter
    private val usersList = mutableListOf<User>()
    private val db = FirebaseDatabase.getInstance().getReference("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdUserManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.recyclerView
        adapter = UsersAdapter(usersList, this::disableUser, this::deleteUser)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        binding.userImageView.setOnClickListener {
            val intent = Intent(this, AdProfileActivity::class.java)
            startActivity(intent)
        }

        binding.homepageImageView.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Kullanıcıları Firebase'den çek
        fetchUsers()
    }

    private fun fetchUsers() {
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                usersList.clear()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    if (user != null) {
                        usersList.add(user)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdUserManagementActivity, "Veritabanı hatası: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Kullanıcıyı devre dışı bırakma
    private fun disableUser(user: User) {
        val newStatus = if (user.disabled) true else false
        db.child(user.id).child(user.disabled.toString()).setValue(newStatus)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    // Güncelleme başarılıysa
                    Toast.makeText(this, "Durum güncellendi.", Toast.LENGTH_SHORT).show()

                    // RecyclerView'ı güncelle
                    val position = usersList.indexOf(user)
                    if (position != -1) {
                        usersList[position] = user.copy(disabled = newStatus) // disabled durumu tersine çevir
                        adapter.notifyItemChanged(position)
                    }
                } else {
                    Toast.makeText(this, "Durum güncelleme başarısız", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Kullanıcıyı silme
    private fun deleteUser(user: User) {
        db.child(user.id).removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "${user.email} başarıyla silindi", Toast.LENGTH_SHORT).show()

                    // RecyclerView'dan silinen kullanıcıyı çıkarma
                    val position = usersList.indexOf(user)
                    if (position != -1) {
                        usersList.removeAt(position)
                        adapter.notifyItemRemoved(position)
                    }
                } else {
                    Toast.makeText(this, "Silme işlemi başarısız", Toast.LENGTH_SHORT).show()
                }
            }
    }
}