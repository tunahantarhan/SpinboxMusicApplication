package com.example.spinboxmusicapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spinboxmusicapplication.databinding.ActivitySignUpBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

@Suppress("DEPRECATION")
class SignUpActivity : AppCompatActivity(){

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        //Kullanıcı "Kayıt Ol" butonuna tıkladığında:
        binding.button.setOnClickListener{
            val email = binding.emailEnter.text.toString() //emailEnter kısmına girilen email'i alır.
            val pass = binding.passwordEnter.text.toString() //passwordEnter kısmına girilen password'ü alır.
            val confirmPass = binding.confirmPassEnter.text.toString() //confirmPassEnter kısmına tekrar girilen password'ü alır.

            //Tüm kutucuklar doluysa ve girilen şifreler uyuşuyorsa:
            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()){
                if (pass != confirmPass){
                    Toast.makeText(this, "Lütfen girilen şifrelerin aynı olduğundan emin olunuz.", Toast.LENGTH_SHORT).show()
                    //LENGTH_SHORT --> 2 saniye, LENGTH_LONG --> 3.5 saniye
                }
                else if (pass == confirmPass){
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val currentUser = firebaseAuth.currentUser
                            val db = FirebaseFirestore.getInstance()
                            val userData = hashMapOf(
                                "role" to "user"
                            )

                            db.collection("users").document(currentUser!!.uid).set(userData).addOnSuccessListener{
                                val intent = Intent(this, SignInActivity::class.java)
                                startActivity(intent)
                            }.addOnFailureListener { e ->
                                Toast.makeText(this, "User data kayıt hatası: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                        else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                            //Eğer işlem başarılı bir şekilde gerçekleşmezse, sorun ne ise onu geribildirim gönderir.
                        }
                    }
                }
            }
            else {
                Toast.makeText(this, "Lütfen tüm bilgilerin girildiğinden emin olunuz.", Toast.LENGTH_SHORT).show()
            }
        }

        //Kullanıcı zaten kayıtlıysa giriş yapmasını belirten yazıya tıklayınca:
        binding.textView.setOnClickListener{
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }
}