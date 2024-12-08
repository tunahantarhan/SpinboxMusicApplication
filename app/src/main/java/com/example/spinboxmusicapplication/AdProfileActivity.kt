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
import com.example.spinboxmusicapplication.databinding.ActivityAdProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class AdProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAdProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        val currentUser = firebaseAuth.currentUser
        val currentUserEmail = currentUser?.email
        val userEmailTextView = findViewById<TextView>(R.id.userEmailTextView)
        userEmailTextView.text = "$currentUserEmail"

        val buttonAdminMainActivity = binding.adminMainActivityButton
        buttonAdminMainActivity.setOnClickListener(){
            startActivity(Intent(this, AdMainActivity::class.java))
        }

        val buttonChangeMail = binding.changeMailButton
        buttonChangeMail.setOnClickListener(){
            val intent = Intent(this, ChangeMailActivity::class.java)
            startActivity(intent)
        }

        val buttonChangePassword = binding.changePasswordButton
        buttonChangePassword.setOnClickListener(){
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        binding.deleteUserButton.setOnClickListener {
            val alertBox = AlertDialog.Builder(this@AdProfileActivity)
                .setMessage("Hesabınız silinecektir. Emin misiniz?")
                .setCancelable(false)
                .setPositiveButton("Evet") { dialog, _ ->
                    val database = FirebaseDatabase.getInstance().reference
                    val  db = FirebaseFirestore.getInstance()

                    //Firestore'dan silme
                    db.collection("users").document(currentUser!!.uid).delete()
                    //Firebase'den silme
                    database.child("users").child(currentUser!!.uid).removeValue()
                    //Auth arasından silme
                    currentUser?.delete()?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this@AdProfileActivity, "Hesabınız silindi.", Toast.LENGTH_LONG).show()
                            val intent = Intent(this@AdProfileActivity, SignInActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@AdProfileActivity, "Hesabınız silinemedi.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Hayır") { dialog, _ ->
                    dialog.cancel()
                }
            alertBox.create().show()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}