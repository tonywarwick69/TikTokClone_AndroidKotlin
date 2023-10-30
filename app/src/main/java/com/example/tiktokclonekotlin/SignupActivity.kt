package com.example.tiktokclonekotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.tiktokclonekotlin.databinding.ActivitySignupBinding
import com.example.tiktokclonekotlin.model.UserModel
import com.example.tiktokclonekotlin.util.ToastResponseMessage

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class SignupActivity : ComponentActivity() {
    lateinit var binding : ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnSubmit.setOnClickListener({
            signUp()
        })
        binding.goToLoginButton.setOnClickListener{
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }
    }
    fun signUp(){
        val email = binding.txtEmail.text.toString()
        val pass = binding.txtPassword.text.toString()
        val confirmPass = binding.txtConfirmPassword.text.toString()
        if(!Patterns.EMAIL_ADDRESS.matcher((email)).matches()){
            binding.txtEmail.setError("Email not valid")
            return
        }
        if (pass.length<6){
            binding.txtPassword.setError("Minimum 6 character")
            return
        }
        if(pass!=confirmPass){
            binding.txtConfirmPassword.setError("Password and Confirm Password not match")
            return
        }
        signUpWithFirebase(email,pass)
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
    fun signUpWithFirebase(email: String, password: String) {
        setInProgress(true)
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                it.user?.let {user ->
                    //if signup success then get firebase userID by user.uid, email, and username = subString of the name before '@' symbol
                    val userModel = UserModel(user.uid,email,email.substringBefore("@"))
                    Firebase.firestore.collection("users")
                        .document(user.uid)
                        .set(userModel as Map<String, Any>)
                        .addOnSuccessListener    {
                                ToastResponseMessage.showToast(applicationContext,"Account created successfully")
                                setInProgress(false)
                                startActivity(Intent(this@SignupActivity, MainActivity::class.java))
                                finish()
                            }.addOnFailureListener{
                            ToastResponseMessage.showToast(applicationContext,it.localizedMessage?:it.stackTraceToString())
                            setInProgress(false)

                        }
                }
            }
    }
}
