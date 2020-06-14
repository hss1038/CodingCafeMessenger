package com.ssam.codingcafemessenger.Fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.ssam.codingcafemessenger.ModelClasses.Users
import com.ssam.codingcafemessenger.R
import kotlinx.android.synthetic.main.fragment_settings.view.*

/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : Fragment() {

    var userReference : DatabaseReference ?= null
    var firebaseUser : FirebaseUser?= null
    val RequestCode = 438
    var imageUri : Uri ?= null
    var storageRef : StorageReference ?= null
    var coverChecker : String ?= ""
    var socialChecker : String ?= ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        userReference = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        storageRef = FirebaseStorage.getInstance().reference.child("User Images")



        userReference!!.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val user = p0.getValue(Users::class.java)

                    if(context != null){
                        view.username_settings.text = user!!.username
                        Picasso.get().load(user.profile).into(view.profile_image_settings)
                        Picasso.get().load(user.cover).into(view.cover_image_settings)
                    }
                }
            }
        })

        view.profile_image_settings.setOnClickListener {
            pickImage()
        }

        view.cover_image_settings.setOnClickListener {
            coverChecker = "cover"
            pickImage()
        }

        view.set_facebook.setOnClickListener {
            socialChecker = "facebook"
            setSocialLinks()
        }

        view.set_instagram.setOnClickListener {
            socialChecker = "instagram"
            setSocialLinks()
        }

        view.set_website.setOnClickListener {
            socialChecker = "website"
            setSocialLinks()
        }

        return view
    }

    private fun setSocialLinks() {
        val builder = AlertDialog.Builder(context, R.style.Theme_AppCompat_DayNight_Dialog_Alert)
        if(socialChecker == "website"){
            builder.setTitle("Write URL:")
        } else {
            builder.setTitle("Write username:")
        }

        val editText = EditText(context)
        if(socialChecker == "website"){
            editText.hint = "e.g www.google.com"
        } else {
            editText.hint = "현우진"
        }
        builder.setView(editText)

        builder.setPositiveButton("Create", DialogInterface.OnClickListener{
            dialog, which ->
            val str = editText.text.toString()
            if(str == ""){
                Toast.makeText(context, "Please write something...", Toast.LENGTH_SHORT).show()
            } else {
                saveSocialLink(str)
            }
        })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener{
                dialog, which ->
            dialog.cancel()
        })

        builder.show()
    }

    private fun saveSocialLink(str: String) {
        val mapSocial = HashMap<String, Any>()

        when(socialChecker){
            "facebook" -> {
                mapSocial["facebook"] = "https://m.facebook.com/$str"
            }
            "instagram" -> {
                mapSocial["instagram"] = "https://m.instagram.com/$str"
            }
            "website" -> {
                mapSocial["website"] = "https://$str"
            }
        }

        userReference!!.updateChildren(mapSocial).addOnCompleteListener {task ->
               if(task.isSuccessful){
                   Toast.makeText(context, "saved Successfully...", Toast.LENGTH_SHORT).show()
               }
        }

    }

    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, RequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RequestCode && resultCode == Activity.RESULT_OK && data!!.data != null){
            imageUri = data.data
            Toast.makeText(context, "uploading...", Toast.LENGTH_SHORT).show()
            uploadImageToDatabase()
        }
    }

    private fun uploadImageToDatabase() {
        val progressBar = ProgressDialog(context)
        progressBar.setMessage("image is uploading, please wait....")
        progressBar.show()

        if(imageUri != null){
            val fileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")

            var uploadTask : StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)

            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{task ->
                if(task.isSuccessful){
                    task.exception?.let{
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()
                    if(coverChecker == "cover"){
                        val mapCoverImg = HashMap<String, Any>()
                        mapCoverImg["cover"] = url
                        userReference!!.updateChildren(mapCoverImg)
                        coverChecker = ""
                    } else {
                        val mapProfileImg = HashMap<String, Any>()
                        mapProfileImg["profile"] = url
                        userReference!!.updateChildren(mapProfileImg)
                        coverChecker = ""
                    }
                    progressBar.dismiss()
                }
            }
        }
    }
}
