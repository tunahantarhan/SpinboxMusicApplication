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
import com.example.spinboxmusicapplication.databinding.ActivityProfileBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import java.util.zip.Inflater

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        val currentUser = firebaseAuth.currentUser
        val currentUserEmail = currentUser?.email
        val userEmailTextView = binding.userEmailTextView
        userEmailTextView.text = "$currentUserEmail"



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
            val alertBox = AlertDialog.Builder(this@ProfileActivity)
                .setMessage("Hesabınız silinecektir. Emin misiniz?")
                .setCancelable(false)
                .setPositiveButton("Evet") { dialog, _ ->
                    val db = FirebaseFirestore.getInstance()
                    db.collection("users").document(currentUser!!.uid).delete()
                    currentUser?.delete()?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this@ProfileActivity, "Hesabınız silindi.", Toast.LENGTH_LONG).show()
                            val intent = Intent(this@ProfileActivity, SignInActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@ProfileActivity, "Hesabınız silinemedi.", Toast.LENGTH_SHORT).show()
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