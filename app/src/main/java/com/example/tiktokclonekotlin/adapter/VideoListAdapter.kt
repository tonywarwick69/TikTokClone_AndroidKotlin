package com.example.tiktokclonekotlin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tiktokclonekotlin.databinding.VideoItemRowBinding
import com.example.tiktokclonekotlin.model.VideoModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class VideoListAdapter( options : FirestoreRecyclerOptions<VideoModel>
): FirestoreRecyclerAdapter<VideoModel,VideoListAdapter.VideoViewHolder>(options )
{

    inner class VideoViewHolder(private val binding: VideoItemRowBinding) : RecyclerView.ViewHolder(binding.root){
        fun bindVideo(videoModel: VideoModel){
            binding.videoView.apply {
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