package com.example.tiktokclonekotlin.adapter

import android.content.Intent
import android.provider.MediaStore.Video
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tiktokclonekotlin.ProfileActivity
import com.example.tiktokclonekotlin.SingleVideoActivity
import com.example.tiktokclonekotlin.databinding.ProfileVideoItemRowBinding
import com.example.tiktokclonekotlin.model.VideoModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth

class ProfileVideoAdapter(options: FirestoreRecyclerOptions<VideoModel>)
    : FirestoreRecyclerAdapter<VideoModel,ProfileVideoAdapter.VideoViewHolder>(options){
    //RecyclerView is view of a vertical list that you scroll them from top to bottom and from bottom back to top
    inner class VideoViewHolder(private val binding: ProfileVideoItemRowBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(video: VideoModel){
            Glide.with(binding.imvThumbNail)
                .load(video.url)
                .into(binding.imvThumbNail)
            //onCLick video display that video
            binding.imvThumbNail.setOnClickListener{
                val intent = Intent(binding.imvThumbNail.context, SingleVideoActivity::class.java)
                intent.putExtra("videoID", video.videoID)
                intent.putExtra("uploaderID",video.uploaderID)
                binding.imvThumbNail.context.startActivity(intent)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ProfileVideoItemRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int, model: VideoModel) {
        holder.bind(model)
    }
}