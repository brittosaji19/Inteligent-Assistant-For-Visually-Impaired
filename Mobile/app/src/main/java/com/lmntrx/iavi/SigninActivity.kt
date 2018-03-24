package com.lmntrx.iavi

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_sign_up_with_email.*
import kotlinx.android.synthetic.main.activity_signin.*

class SigninActivity : AppCompatActivity() {
    private val RC_SIGNIN:Int=123
    lateinit private var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        val googleSignInOptions: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.client_id)).requestEmail().build()
        val googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        mAuth= FirebaseAuth.getInstance()
        val signInWithGoogleButton = googleSignInButton
        signInWithGoogleButton.setOnClickListener(View.OnClickListener {
            signInWithGoogle(googleSignInClient)
        })

        val signInWithEmailButton=signInWithEmailButton
        signInWithEmailButton.setOnClickListener {
            val email=signInEmailEditText.text.toString()
            val password=signInPasswordEditText.text.toString()
            signInWithEmail(email,password)
        }

        val signUpWithEmailTextView=signUpTextView
        signUpWithEmailTextView.setOnClickListener {
            startActivity(Intent(this,SignUpWithEmail::class.java))
        }
    }

    fun signInWithGoogle(signInClient:GoogleSignInClient){
        Toast.makeText(this,"Starting Google SignIn",Toast.LENGTH_SHORT).show()
        val signInIntent=signInClient.signInIntent
        startActivityForResult(signInIntent,RC_SIGNIN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==RC_SIGNIN){
            val task:Task<GoogleSignInAccount>
            task=GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account:GoogleSignInAccount=task.getResult(ApiException::class.java)
                firebaseSignInWithGoogle(account)
                Toast.makeText(this,"Signed In Succesfully",Toast.LENGTH_SHORT).show()
            }
            catch (e:Exception){
                Toast.makeText(this,"SignIn Failed",Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun firebaseSignInWithGoogle(account:GoogleSignInAccount){
        Toast.makeText(this,"Logging In As "+ account.id,Toast.LENGTH_SHORT).show()
        val credential:AuthCredential=GoogleAuthProvider.getCredential(account.idToken,null)
        val authHandler=mAuth.signInWithCredential(credential)
        authHandler.addOnCompleteListener(this, OnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(this,"Logged In",Toast.LENGTH_SHORT).show()
                val user=mAuth.currentUser
            }
            else{
                Toast.makeText(this,"Authentication Failed",Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun signInWithEmail(email:String,password:String){
        Toast.makeText(this,"Sign In With Email",Toast.LENGTH_SHORT).show()
        //Log.d("DetailsCheck","Email : "+email+" Password : "+password)
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
            if (it.isSuccessful){
                startActivity(Intent(this,MainActivity::class.java))
            }
            else{
                Toast.makeText(this,"Sign In Failed Please Check Your Credentials",Toast.LENGTH_SHORT).show()
            }
        }
    }
}
