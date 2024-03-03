package com.example.tiktokclonekotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.tiktokclonekotlin.adapter.VideoListAdapter
import com.example.tiktokclonekotlin.databinding.ActivitySingleVideoBinding
import com.example.tiktokclonekotlin.model.VideoModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class SingleVideoActivity : AppCompatActivity() {
    lateinit var  binding :ActivitySingleVideoBinding
    lateinit var videoID : String
    lateinit var adapter: VideoListAdapter
    lateinit var uploaderId : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingleVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        videoID = intent.getStringExtra("videoID")!!
        //binding.txtVideoID.text=videoID
        //binding uploaderID send ProfileVideoAdapter
        uploaderId = intent.getStringExtra("uploaderID")!!
        setupViewPager()

        //button close video and back to profile
        binding.btnClose.setOnClickListener{
//            Toast.makeText(this, "CLICKED", Toast.LENGTH_SHORT).show()
            val intent = Intent(this,ProfileActivity::class.java)
            intent.putExtra("profile_user_id", uploaderId)
            startActivity(intent)
            finish()

        }

    }

    private fun setupViewPager() {
        val options = FirestoreRecyclerOptions.Builder<VideoModel>()
            .setQuery(
                Firebase.firestore.collection("videos")
                    .whereEqualTo("videoID",videoID),
                VideoModel::class.java
            ).build()
        adapter = VideoListAdapter(options)
        binding.viewPager.adapter = adapter
    }
    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.stopListening()
    }
}