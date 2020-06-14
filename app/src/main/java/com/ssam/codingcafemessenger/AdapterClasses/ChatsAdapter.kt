package com.ssam.codingcafemessenger.AdapterClasses

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.ssam.codingcafemessenger.ModelClasses.Chat
import com.ssam.codingcafemessenger.R
import com.ssam.codingcafemessenger.ViewFullImageActivity
import de.hdodenhof.circleimageview.CircleImageView

class ChatsAdapter(val mContext: Context, val mChatList: List<Chat>, val imageUrl: String) :
    RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {

    var firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profile_image = itemView.findViewById<CircleImageView>(R.id.profile_image)
        var show_text_message = itemView.findViewById<TextView>(R.id.show_text_message)
        var left_image_view = itemView.findViewById<ImageView>(R.id.left_image_view)
        var text_seen = itemView.findViewById<TextView>(R.id.text_seen)
        var right_image_view = itemView.findViewById<ImageView>(R.id.right_image_view)

    }

    override fun getItemViewType(position: Int): Int {

        return if (mChatList[position].sender.equals(firebaseUser!!.uid)) {
            1  // message_item_right
        } else {
            0  // message_item_left
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == 1) {
            val view =
                LayoutInflater.from(mContext).inflate(R.layout.message_item_right, parent, false)
            ViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(mContext).inflate(R.layout.message_item_left, parent, false)
            ViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return mChatList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = mChatList[position]
        Picasso.get().load(imageUrl).into(holder.profile_image)

        // images Messages
        if (chat.message.equals("sent you an image.") && !chat.url.equals("")) {
            //image message - right side
            if (chat.sender.equals(firebaseUser!!.uid)) {
                holder.show_text_message.visibility = View.GONE
                holder.right_image_view.visibility = View.VISIBLE
                Picasso.get().load(chat.url).into(holder.right_image_view)

                holder.right_image_view!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "View Full Image",
                        "Delete Image",
                        "Cancel"
                    )

                    var builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("What do you want? ")

                    builder.setItems(options, DialogInterface.OnClickListener{
                        dialog, which ->
                        if(which == 0){
                            val intent = Intent(mContext, ViewFullImageActivity::class.java)
                            intent.putExtra("url", chat.url)
                            mContext.startActivity(intent)
                        } else if(which == 1){
                            deleteSentMessage(position, holder)
                        }
                    })
                    builder.show()
                }

            } else if (!chat.sender.equals(firebaseUser!!.uid)) {  //image message - left side
                holder.show_text_message.visibility = View.GONE
                holder.left_image_view.visibility = View.VISIBLE
                Picasso.get().load(chat.url).into(holder.left_image_view)

                holder.left_image_view!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "View Full Image",
                        "Cancel"
                    )

                    var builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("What do you want? ")

                    builder.setItems(options, DialogInterface.OnClickListener{
                            dialog, which ->
                        if(which == 0){
                            val intent = Intent(mContext, ViewFullImageActivity::class.java)
                            intent.putExtra("url", chat.url)
                            mContext.startActivity(intent)
                        }
                    })
                    builder.show()
                }

            }
        } else {  // text message
            holder.show_text_message.text = chat.message

            if(firebaseUser!!.uid == chat.sender){
                holder.show_text_message!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "Delete Message",
                        "Cancel"
                    )

                    var builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("What do you want? ")

                    builder.setItems(options, DialogInterface.OnClickListener{
                            dialog, which ->
                        if(which == 0){
                            deleteSentMessage(position, holder)
                        }
                    })
                    builder.show()
                }
            }
        }

        // sent and seen message
        if (position == mChatList.size - 1) {
            if (chat.isseen) {
                holder.text_seen.text = "Seen"
                if (chat.message.equals("sent you an image.") && chat.url.equals("")) {
                    val lp: RelativeLayout.LayoutParams? =
                        holder.text_seen.layoutParams as RelativeLayout.LayoutParams
                    lp!!.setMargins(0, 245, 10, 0)
                    holder.text_seen.layoutParams = lp
                } else {
                    holder.text_seen.text = "Sent"
                    if (chat.message.equals("sent you an image.") && chat.url.equals("")) {
                        val lp: RelativeLayout.LayoutParams? =
                            holder.text_seen.layoutParams as RelativeLayout.LayoutParams
                        lp!!.setMargins(0, 245, 10, 0)
                        holder.text_seen.layoutParams = lp
                    }
                }
            } else {
                holder.text_seen.visibility = View.GONE
            }

        }
    }

    //Todo 확인해 볼것... 17:20
    private fun deleteSentMessage(position: Int, holder: ChatsAdapter.ViewHolder){
        val ref = FirebaseDatabase.getInstance().reference.child("Chats")
            .child(mChatList.get(position).messageId)
            .removeValue()
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Toast.makeText(holder.itemView.context, "Deleted.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(holder.itemView.context, "Failed, Not Deleted.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}