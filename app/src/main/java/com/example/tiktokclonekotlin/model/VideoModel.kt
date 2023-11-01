package com.example.tiktokclonekotlin.model

import android.icu.text.CaseMap.Title
import com.google.firebase.Timestamp

data class VideoModel(
    var videoID : String = "",
    var title : String = "",
    var url : String = "",
    var uploaderID : String = "",
    var createdTime : Timestamp = Timestamp.now()
)
