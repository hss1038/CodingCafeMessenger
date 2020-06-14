package com.ssam.codingcafemessenger

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    lateinit var mAuth : FirebaseAuth
    lateinit var refUsers : DatabaseReference
    var firebaseUserID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setSupportActionBar(toolbar_register)
        supportActionBar!!.title = "Register"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        toolbar_register.setNavigationOnClickListener {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        mAuth = FirebaseAuth.getInstance()

        register_btn.setOnClickListener {
            registerUser()
        }

    }

    private fun registerUser() {
        val username = username_register.text.toString()
        val email = email_register.text.toString()
        val password = password_register.text.toString()

        if(username == ""){
            Toast.makeText(this, "please write username", Toast.LENGTH_SHORT).show()
        } else if(email == ""){
            Toast.makeText(this, "please write email", Toast.LENGTH_SHORT).show()
        } else if(password == ""){
            Toast.makeText(this, "please write password", Toast.LENGTH_SHORT).show()
        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        firebaseUserID = mAuth.currentUser!!.uid
                        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUserID)

                        val userHashMap = HashMap<String, Any>()
                        userHashMap["uid"] = firebaseUserID
                        userHashMap["username"] = username
                        userHashMap["profile"] = "https://firebasestorage.googleapis.com/v0/b/chat-d728f.appspot.com/o/profile.png?alt=media&token=f722ca9d-b4cc-41fa-8dcc-b8fcebd16932"
                        userHashMap["cover"] = "https://firebasestorage.googleapis.com/v0/b/chat-d728f.appspot.com/o/cover.jpg?alt=media&token=2f056905-efe2-4ca1-a2e4-e65c70eea45a"
                        userHashMap["status"] = "offline"
                        userHashMap["search"] = username.toLowerCase()
                        userHashMap["facebook"] = "https://m.facebook.com"
                        userHashMap["instagram"] = "https://m.Instagram.com"
                        userHashMap["website"] = "https://m.google.com"

                        refUsers.updateChildren(userHashMap)
                            .addOnCompleteListener { task ->
                                if(task.isSuccessful){
                                    val intent = Intent(this, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Error Message : " + task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}
