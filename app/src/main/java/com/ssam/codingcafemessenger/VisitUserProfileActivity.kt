package com.ssam.codingcafemessenger

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.ssam.codingcafemessenger.ModelClasses.Users
import kotlinx.android.synthetic.main.activity_visit_user_profile.*

class VisitUserProfileActivity : AppCompatActivity() {

    private var userVisitId: String = ""
    var user: Users ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_user_profile)

        userVisitId = intent.getStringExtra("visit_id")
        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(userVisitId)
        ref.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    user = p0.getValue(Users::class.java)
                    username_display.text = user!!.username
                    Picasso.get().load(user!!.profile).into(profile_display)
                    Picasso.get().load(user!!.cover).into(cover_display)
                }
            }
        })

        facebook_display.setOnClickListener {
            val uri = Uri.parse(user!!.facebook)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        instagram_display.setOnClickListener {
            val uri = Uri.parse(user!!.instagram)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        website_display.setOnClickListener {
            val uri = Uri.parse(user!!.website)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        send_msg_btn.setOnClickListener {
            val intent = Intent(this, MessageChatActivity::class.java)
            intent.putExtra("visit_id", user!!.uid)
            startActivity(intent)
        }
    }
}
