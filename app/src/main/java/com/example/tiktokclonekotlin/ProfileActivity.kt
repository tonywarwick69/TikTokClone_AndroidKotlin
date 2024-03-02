package com.example.tiktokclonekotlin

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.tiktokclonekotlin.databinding.ActivityProfileBinding
import com.example.tiktokclonekotlin.model.UserModel
import com.example.tiktokclonekotlin.util.ToastResponseMessage
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import java.net.URI

class ProfileActivity : AppCompatActivity() {

    lateinit var profileUserID : String
    private lateinit var binding: ActivityProfileBinding
    lateinit var currentUserID : String
    lateinit var profileUserModel: UserModel
    lateinit var photoLauncher : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        profileUserID = intent.getStringExtra("profile_user_id")!!
        currentUserID = FirebaseAuth.getInstance().currentUser?.uid!!
        //Init photolauncher activity
        photoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
            if(result.resultCode == RESULT_OK){
                //upload photo
                uploadPhotoToFirestore(result.data?.data!!)
            }
        }

        if(profileUserID == currentUserID){
//            binding.btnProfile.visibility = View.VISIBLE
            binding.btnProfile.text= "Logout"
            binding.btnProfile.setOnClickListener {
                logOut()
            }
            binding.profilePic.setOnClickListener{
                checkPermissionAndPickPhoto()
            }
        } else {
            //other user profile
//            binding.btnProfile.visibility = View.INVISIBLE
            binding.btnProfile.text = "Follow"
            binding.btnProfile.setOnClickListener {
                followUnfollowUser()
            }
        }
        getUserProfile()

        //Nav Menu
        binding.bottomNavMenu.setOnItemReselectedListener{menuItem ->
            when(menuItem.itemId){
                R.id.bottom_menu_home->{
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                R.id.bottom_menu_addVideo->{
                    startActivity(Intent(this,VideoUploadActivity::class.java))
                    finish()
                }
                R.id.bottom_menu_profile->{
                    val intent = Intent(this,ProfileActivity::class.java)
                    intent.putExtra("profile_user_id",FirebaseAuth.getInstance().currentUser?.uid)
                    startActivity(intent)
                    finish()
                }
            }
            false
        }

    }
    /*
    var followerList those whose are following you are followers
    so when a usr press Follow another user on TikTok app it will that userId to followerList
    and since this user already in the list now btnProfile will change to unfollow mean to delete that userId from followerList

    var followingList those whose you are following are followings

     */
    fun followUnfollowUser(){
        Firebase.firestore.collection("users")
            .document(currentUserID)
            .get()
            .addOnSuccessListener {
                val currentUserModel = it.toObject(UserModel::class.java)!!
                if(profileUserModel.followerList.contains(currentUserID)){
                    //unfollow user
                    profileUserModel.followerList.remove(currentUserID)
                    currentUserModel.followingList.remove(profileUserID)
                    binding.btnProfile.text="Follow"
                } else {
                    //follow user
                    profileUserModel.followerList.add(currentUserID)
                    currentUserModel.followingList.add(profileUserID)
                    binding.btnProfile.text="Unfollow"
                }
                //Update both the user that you just following and your following list
                updateUserData(profileUserModel)
                updateUserData(currentUserModel)
                //setUI()

            }
    }
    fun updateUserData(model: UserModel){
        Firebase.firestore.collection("users")
            .document(model.userId)
            .set(model)
            .addOnSuccessListener {
                getUserProfile()
            }

    }
    fun uploadPhotoToFirestore(photoURI: Uri){
            binding.progressBar.visibility = View.VISIBLE
            val photoRef = FirebaseStorage.getInstance().reference
                .child("profilePic/"+ currentUserID)
            photoRef.putFile(photoURI)
                .addOnSuccessListener {
                    photoRef.downloadUrl.addOnSuccessListener {
                            downloadUrl ->
                        //video model store in firebase firestore
                        postToFirestore(downloadUrl.toString())
                    }
                }
    }
    fun postToFirestore(url: String){
        Firebase.firestore.collection("users")
            .document(currentUserID)
            .update("profilePic",url)
            .addOnSuccessListener {
                setUI()
            }
    }
    private fun checkPermissionAndPickPhoto(){
        var readExternalPhoto : String = ""
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            readExternalPhoto= android.Manifest.permission.READ_MEDIA_IMAGES
        } else{
            readExternalPhoto= android.Manifest.permission.READ_EXTERNAL_STORAGE
        }
        if (ContextCompat.checkSelfPermission(this,readExternalPhoto)== PackageManager.PERMISSION_GRANTED){
            openPhotoPicker()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(readExternalPhoto),
                100
            )
        }
    }
    private fun openPhotoPicker(){
        var intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        intent.type= "image/*"
        photoLauncher.launch(intent)
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
                .circleCrop()
                .into(binding.profilePic)
            binding.txtProfileUsername.text="@"+username
            if(profileUserModel.followerList.contains(currentUserID))
                binding.btnProfile.text="Unfollow"

            binding.progressBar.visibility= View.INVISIBLE
            binding.txtFollowings.text = followingList.size.toString()
            binding.txtFollowers.text = followerList.size.toString()
            Firebase.firestore.collection("videos")
                .whereEqualTo("uploaderID",profileUserID)
                .get().addOnSuccessListener {
                    binding.txtPosts.text = it.size().toString()
                }
        }
    }


}