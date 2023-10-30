package com.example.tiktokclonekotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.activity.ComponentActivity
import com.example.tiktokclonekotlin.databinding.ActivityLoginBinding
import com.example.tiktokclonekotlin.model.UserModel
import com.example.tiktokclonekotlin.util.ToastResponseMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : ComponentActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var btnGoToSignupActivity: SignupActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.goToSignupButton.setOnClickListener{
            startActivity(Intent(this,SignupActivity::class.java))
            finish()
        }
        binding.btnSubmit.setOnClickListener{
            login()
        }
    }
    fun login(){
        val email = binding.txtEmail.text.toString()
        val pass = binding.txtPassword.text.toString()
        if(!Patterns.EMAIL_ADDRESS.matcher((email)).matches()){
            binding.txtEmail.setError("Email not valid")
            return
        }
        if (pass.length<6){
            binding.txtPassword.setError("Minimum 6 character")
            return
        }
        loginWithFirebase(email,pass)
    }

    fun setInProgress(inProgress: Boolean){
        if(inProgress){
            binding.progressBar.visibility= View.VISIBLE
            binding.btnSubmit.visibility = View.GONE
        } else {
            binding.progressBar.visibility= View.GONE
            binding.btnSubmit.visibility = View.VISIBLE
        }
    }
    fun loginWithFirebase(email: String, password: String) {
        setInProgress(true)
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
            email,
            password
        ).addOnSuccessListener {
            ToastResponseMessage.showToast(applicationContext,"Login successfully")
            setInProgress(false)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }.addOnFailureListener{
            ToastResponseMessage.showToast(applicationContext,it.localizedMessage?:it.stackTraceToString())
            setInProgress(false)
        }
    }
}