package com.lmntrx.iavi

import android.content.Intent
import android.graphics.BitmapFactory
import android.opengl.Visibility
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_preview.*

class PreviewActivity : AppCompatActivity() {
    lateinit var mAuth: FirebaseAuth
    lateinit private var storageReference:StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)
        val receivedIntent=intent
        mAuth= FirebaseAuth.getInstance()
        storageReference=FirebaseStorage.getInstance().reference
        val imageAsByteArray:ByteArray=receivedIntent.getByteArrayExtra("image")
        val image=BitmapFactory.decodeByteArray(imageAsByteArray,0,imageAsByteArray.size)
        val previewImageView=previewImageView
        val uploadSpinner=uploadSpinner
        uploadSpinner.visibility=View.INVISIBLE
        previewImageView.setImageBitmap(image)
        val retakeButton=retakeButton
        val confirmButton=confirmButton
        retakeButton.setOnClickListener(View.OnClickListener {
            val retakeintent=Intent(this,MainActivity::class.java)
            retakeintent.putExtra("capture",true)
            startActivity(retakeintent)
        })
        confirmButton.setOnClickListener(View.OnClickListener {
            uploadSpinner.visibility=View.VISIBLE
            confirmButton.text="Uploading"
            uploadToFirebase(imageAsByteArray,uploadSpinner,confirmButton)
        })
    }

    fun uploadToFirebase(imageAsByteArray: ByteArray,uploadSpinner:View,confirmButton:Button){
        if(mAuth.currentUser!=null) {
            val uploadFileReference = storageReference.child("imageBucket/" + mAuth.currentUser?.displayName + ".png")
            val uploadTask: UploadTask = uploadFileReference.putBytes(imageAsByteArray)
            uploadTask.addOnSuccessListener {
                Toast.makeText(this, "URL : " + it.downloadUrl, Toast.LENGTH_SHORT).show()
                uploadSpinner.visibility = View.INVISIBLE
                confirmButton.text = "Open File On Server"

            }

            uploadTask.addOnFailureListener {
                Toast.makeText(this, "Error Uploading File", Toast.LENGTH_SHORT).show()
                uploadSpinner.visibility = View.INVISIBLE
            }
        }else{
            val snackbar:Snackbar= Snackbar.make(confirmButton.rootView,"Please Sign In To Upload",Snackbar.LENGTH_SHORT)
            snackbar.show()
            uploadSpinner.visibility = View.INVISIBLE
            confirmButton.text="Try Again"
        }
    }


}
