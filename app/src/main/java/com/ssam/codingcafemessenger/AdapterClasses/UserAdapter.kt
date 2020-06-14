package com.ssam.codingcafemessenger.AdapterClasses

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.ssam.codingcafemessenger.MessageChatActivity
import com.ssam.codingcafemessenger.ModelClasses.Chat
import com.ssam.codingcafemessenger.ModelClasses.Users
import com.ssam.codingcafemessenger.R
import com.ssam.codingcafemessenger.VisitUserProfileActivity
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(val mContext: Context?, val mUsers: List<Users>, val isChatCheck: Boolean) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    var lastMsg: String = ""

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userNameTxt = itemView.findViewById<TextView>(R.id.username)
        var profileImageView = itemView.findViewById<CircleImageView>(R.id.profile_image)
        var onlineImageView = itemView.findViewById<CircleImageView>(R.id.image_online)
        var offlineImageView = itemView.findViewById<CircleImageView>(R.id.image_offline)
        var lastMessageTxt = itemView.findViewById<TextView>(R.id.message_last)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_search_item_layout, viewGroup, false)
        return UserAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = mUsers[position]
        holder.userNameTxt.text = user!!.username
        Picasso.get().load(user.profile).placeholder(R.drawable.profile).into(holder.profileImageView)

        if(isChatCheck){
            retrieveLastMessage(user.uid, holder.lastMessageTxt)
        } else {
            holder.lastMessageTxt.visibility = View.GONE
        }

        if(isChatCheck){
            if(user.status == "online"){
                holder.onlineImageView.visibility = View.VISIBLE
                holder.offlineImageView.visibility = View.GONE
            } else {
                holder.onlineImageView.visibility = View.GONE
                holder.offlineImageView.visibility = View.VISIBLE
            }
        } else {
            holder.onlineImageView.visibility = View.GONE
            holder.offlineImageView.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            val options = arrayOf<CharSequence>(
                "Send Message",
                "Visit Profile"
            )
            val builder = AlertDialog.Builder(mContext)
            builder.setTitle("What do you want?")
            builder.setItems(options, DialogInterface.OnClickListener { dialog, position ->
                if(position == 0){
                    val intent = Intent(mContext, MessageChatActivity::class.java)
                    intent.putExtra("visit_id", user.uid)
                    mContext!!.startActivity(intent)
                }
                if(position == 1){
                    val intent = Intent(mContext, VisitUserProfileActivity::class.java)
                    intent.putExtra("visit_id", user.uid)
                    mContext!!.startActivity(intent)
                }
            })
            builder.show()
        }
    }

    private fun retrieveLastMessage(chatUserId: String, lastMessageTxt: TextView?) {
        lastMsg = "defaultMsg"

        val firebaseUsers = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        reference.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                for(dataSnapshot in p0.children){
                    val chat = dataSnapshot.getValue(Chat::class.java)
                    if(firebaseUsers != null && chat != null){
                        if(chat.receiver == firebaseUsers!!.uid && chat.sender == chatUserId ||
                                chat.receiver == chatUserId && chat.sender == firebaseUsers!!.uid){
                            lastMsg = chat.message
                        }
                    }
                }
                when(lastMsg){
                    "defaultMsg" -> lastMessageTxt!!.text = "no Message"
                    "sent you an images" -> lastMessageTxt!!.text = "image sent."
                    else -> lastMessageTxt!!.text = lastMsg
                }
                lastMsg = "defaultMsg"
            }

        })
    }

}