package com.example.tiktokclonekotlin

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.tiktokclonekotlin.databinding.ActivityVideoUploadBinding
import com.example.tiktokclonekotlin.model.VideoModel
import com.example.tiktokclonekotlin.util.ToastResponseMessage
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage

class VideoUploadActivity : AppCompatActivity() {
    lateinit var binding: ActivityVideoUploadBinding
    private var selectedVideoURI : Uri? =null
    lateinit var videoLauncher : ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.uploadView.setOnClickListener{
            binding.postView.visibility= View.VISIBLE
            checkPermissionAndOpenVideoPicker()

        }
        binding.btnCancelPost.setOnClickListener{
            finish()

        }
        videoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
            if(result.resultCode == RESULT_OK){
                selectedVideoURI = result.data?.data
                showPostView()
                //test if videoLauncher able to receive video URI
                //ToastResponseMessage.showToast(this,"Video received with URI: "+selectedVideoURI.toString())
            }
        }
        binding.btnPostVideo.setOnClickListener{
            postVideo()

        }

    }
    private fun postVideo(){
        if(binding.etCaption.text.toString().isEmpty()){
            binding.etCaption.setError("Write something for the caption")
            return;
        }
        setInProgress(true)
        selectedVideoURI?.apply {
            //video files will be stored in firebase cloud storage
            val videoRef =FirebaseStorage.getInstance().reference
                .child("videos/"+ this.lastPathSegment)
            videoRef.putFile(this)
                .addOnSuccessListener {
                    videoRef.downloadUrl.addOnSuccessListener {
                        downloadUrl ->
                        //video model store in firebase firestore
                        postToFirestore(downloadUrl.toString())
                    }
                }

        }
    }
    private fun postToFirestore(url: String){
        // "!!" stand for check not null
        val videoModel = VideoModel(
            FirebaseAuth.getInstance().currentUser?.uid!! + "_"+ Timestamp.now().toString(),
            binding.etCaption.text.toString(),
            url,
            FirebaseAuth.getInstance().currentUser?.uid!!,
            Timestamp.now()
        )
        Firebase.firestore.collection("videos")
            .document(videoModel.videoID)
            .set(videoModel)
            .addOnSuccessListener {
                setInProgress(false)
                ToastResponseMessage.showToast(applicationContext,"Video uploaded successfully!")
            }
            .addOnFailureListener{
                setInProgress(true)
                ToastResponseMessage.showToast(applicationContext,"Video failed to upload")
            }
    }
    private fun setInProgress(inProgress: Boolean){
        if(inProgress){
            binding.progressBar.visibility = View.VISIBLE
            binding.btnPostVideo.visibility = View.GONE
        } else{
            binding.progressBar.visibility = View.GONE
            binding.btnPostVideo.visibility = View.VISIBLE
        }
    }
    private fun showPostView(){
        selectedVideoURI?.let{
            binding.postView.visibility = View.VISIBLE
            binding.uploadView.visibility = View.GONE
            Glide.with(binding.postThumbnailView).load(it).into(binding.postThumbnailView)
        }
    }
    private fun checkPermissionAndOpenVideoPicker(){
        var readExternalVideo : String = ""
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            readExternalVideo= android.Manifest.permission.READ_MEDIA_VIDEO
        } else{
            readExternalVideo= android.Manifest.permission.READ_EXTERNAL_STORAGE
        }
        if (ContextCompat.checkSelfPermission(this,readExternalVideo)== PackageManager.PERMISSION_GRANTED){
            openVideoPicker()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(readExternalVideo),
                100
            )
        }
    }
    private fun openVideoPicker(){
        var intent = Intent(Intent.ACTION_PICK,MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        intent.type= "video/*"
        videoLauncher.launch(intent)
    }
}