package com.lmntrx.iavi

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthProvider
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {
    private val CAPTURE_IMAGE_INTENT:Int=0
    lateinit private var firebaseAuth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firebaseAuth=FirebaseAuth.getInstance()
        val captureButton: Button =captureButton
        captureButton.setOnClickListener(View.OnClickListener {
            capture()
        })

        val signInButton=signInButton
        signInButton.setOnClickListener(View.OnClickListener {
            if(firebaseAuth.currentUser==null) {
                firebaseSignIn()
            }
            else{
                val snackbar:Snackbar= Snackbar.make(parent_layout,"Signed In Long Press To Sign Out",Snackbar.LENGTH_LONG)
                snackbar.show()
            }
        })
        signInButton.setOnLongClickListener {
            if(firebaseAuth.currentUser!=null){
                firebaseAuth.signOut()

                signInButton.text="Sign In"
                signInButton.setBackgroundColor(0x000055)
            }
            else{
                val snackbar:Snackbar= Snackbar.make(parent_layout," Already Signed Out",Snackbar.LENGTH_SHORT)
                snackbar.show()
            }
            true
        }
        val intent= intent
        try {
            val retakeRequest:Boolean=intent.extras.get("capture") as Boolean
            if(retakeRequest){
                capture()
            }
        }
        catch (e:Exception){
            Toast.makeText(this,"No Retake Request Received",Toast.LENGTH_SHORT)
        }



    }

    private fun capture(){
        val i= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(i,CAPTURE_IMAGE_INTENT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==CAPTURE_IMAGE_INTENT){
            if (resultCode== Activity.RESULT_OK){
                val capturedImageBuldleData=data?.extras?.get("data")
                val capturedImage:Bitmap=capturedImageBuldleData as Bitmap
                val intent=Intent(this,PreviewActivity::class.java)
                val intentBundle:Bundle= Bundle()
                val stream=ByteArrayOutputStream()
                capturedImage.compress(Bitmap.CompressFormat.PNG,100,stream)
                val streamToByteArray:ByteArray=stream.toByteArray()
                intentBundle.putByteArray("image",streamToByteArray)
                intent.putExtras(intentBundle)
                startActivity(intent)
                Toast.makeText(this,"Image Captured Successfully",Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this,"Error Capturing Image",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser:FirebaseUser?=firebaseAuth.currentUser
        if (currentUser!=null){
            Toast.makeText(this,"Logged in as "+currentUser,Toast.LENGTH_SHORT)
            signInButton.text="Signed in as "+currentUser.displayName
            signInButton.setBackgroundColor(0x550000)
        }

    }

    fun firebaseSignIn(){
        startActivity(Intent(this,SigninActivity::class.java))


    }


}
