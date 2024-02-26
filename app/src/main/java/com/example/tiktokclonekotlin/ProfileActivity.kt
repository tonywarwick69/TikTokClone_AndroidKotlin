package com.example.tiktokclonekotlin

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.tiktokclonekotlin.databinding.ActivityProfileBinding
import com.example.tiktokclonekotlin.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class ProfileActivity : AppCompatActivity() {

    lateinit var profileUserID : String
    private lateinit var binding: ActivityProfileBinding
    lateinit var currentUserID : String
    lateinit var profileUserModel: UserModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        profileUserID = intent.getStringExtra("profile_user_id")!!
        currentUserID = FirebaseAuth.getInstance().currentUser?.uid!!
        if(profileUserID == currentUserID){
            binding.btnLogOut.text= "Logout"
            binding.btnLogOut.setOnClickListener {
                logOut()
            }
        } else {
            //other user profile
        }
        getUserProfile()


    }

    private fun logOut() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this,LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun getUserProfile() {
        Firebase.firestore.collection("users")
            .document(profileUserID)
            .get()
            .addOnSuccessListener {
                profileUserModel = it.toObject(UserModel::class.java)!!
                setUI()

            }
    }

    private fun setUI() {
        profileUserModel.apply {
            Glide.with(binding.profilePic)
                .load(profilePic)
                .apply { RequestOptions().placeholder(R.drawable.account_circle_icon) }
                .into(binding.profilePic)
            binding.txtProfileUsername.text="@"+username
            binding.progressBar.visibility= View.INVISIBLE
            binding.txtFollowings.text = followingList.toString()
            binding.txtFollowers.text = followerList.size.toString()
            Firebase.firestore.collection("videos")
                .whereEqualTo("uploaderID",profileUserID)
                .get().addOnSuccessListener {
                    binding.txtPosts.text = it.size().toString()
                }
        }
    }


}