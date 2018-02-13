package com.lmntrx.iavi

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_sign_up_with_email.*

class SignUpWithEmail : AppCompatActivity() {
    lateinit var mauth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_with_email)
        mauth=FirebaseAuth.getInstance()
        val signUpButton=signUpConfirmButton
        signUpButton.setOnClickListener {
            signUp()
        }
    }
    private fun signUp(){
        Toast.makeText(this,"Signing Up........",Toast.LENGTH_SHORT).show()
        val email=emailEditText.text.toString()
        val password=passwordEditText.text.toString()
        val confirmpassword=confirmPasswordEditText.text.toString()
        if (password == confirmpassword){
            try {
                mauth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                    if (it.isSuccessful){
                        Snackbar.make(signUpOuter,"Signed Up Successfully",Snackbar.LENGTH_SHORT).show()
                        signUpComplete(email,password)
                    }
                    else{
                        Snackbar.make(signUpOuter,"Signed Up Failed",Snackbar.LENGTH_SHORT).show()
                        Log.w("SignUp",it.exception)
                    }
                }
            }
            catch (e:FirebaseAuthWeakPasswordException){
                Snackbar.make(signUpOuter,"Password Is Too Weak",Snackbar.LENGTH_SHORT).show()
            }

        }
        else{
            Toast.makeText(this,"Password Mismatch",Toast.LENGTH_SHORT).show()
        }


    }
    private fun signUpComplete(email:String,password:String){
        mauth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
            if (it.isSuccessful){
                val displayname=nameEditText.text.toString()
                val profileUpdate=UserProfileChangeRequest.Builder().setDisplayName(displayname).build()
                val currentUser=mauth.currentUser
                if (currentUser!=null){
                    currentUser.updateProfile(profileUpdate).addOnCompleteListener {
                        if (it.isSuccessful){
                            startActivity(Intent(this,MainActivity::class.java))
                        }
                        else{
                            Toast.makeText(this,"Error Occured On Profile Creation",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}
