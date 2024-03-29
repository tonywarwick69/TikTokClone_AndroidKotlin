package com.example.tiktokclonekotlin.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.tiktokclonekotlin.MainActivity
import com.example.tiktokclonekotlin.ProfileActivity
import com.example.tiktokclonekotlin.R
import com.example.tiktokclonekotlin.databinding.VideoItemRowBinding
import com.example.tiktokclonekotlin.model.UserModel
import com.example.tiktokclonekotlin.model.VideoModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class VideoListAdapter( options : FirestoreRecyclerOptions<VideoModel>
): FirestoreRecyclerAdapter<VideoModel,VideoListAdapter.VideoViewHolder>(options )
{

    inner class VideoViewHolder(private val binding: VideoItemRowBinding) : RecyclerView.ViewHolder(binding.root){
        fun bindVideo(videoModel: VideoModel){
            //bindUserData get user data from VideoModel contain uploaderId
            Firebase.firestore.collection("users")
                .document(videoModel.uploaderID)
                .get().addOnSuccessListener{
                    val userModel = it?.toObject(UserModel::class.java)
                    userModel?.apply {
                        binding.usernameView.text = username
                        //bind profile pic
                        val requestOptions = RequestOptions.placeholderOf(R.drawable.profile_icon)
                            .circleCrop()

                        Glide.with(binding.profileIcon)
                            .load(profilePic)
                            .apply(requestOptions)
                            .into(binding.profileIcon)

                        //onClick user profile on video navigate to that user profile
                        binding.profileLink.setOnClickListener{
                            val intent = Intent(binding.profileLink.context, ProfileActivity::class.java)
                            intent.putExtra("profile_user_id", userId)
                            binding.profileLink.context.startActivity(intent)
                            //finish()
                        }
                    }
                }

            binding.captionView.text = videoModel.title
            binding.progressBar.visibility = View.VISIBLE

            //bind video
            binding.videoView.apply {
                binding.progressBar.visibility = View.GONE
                setVideoPath(videoModel.url)
                setOnPreparedListener {
                    it.start()
                    it.isLooping = true

                }
                setOnClickListener{
                    if(isPlaying){
                        pause()
                        binding.btnPauseVideo.visibility= View.VISIBLE
                    } else {
                        start()
                        binding.btnPauseVideo.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = VideoItemRowBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int, model: VideoModel) {
        holder.bindVideo(model)
    }

}