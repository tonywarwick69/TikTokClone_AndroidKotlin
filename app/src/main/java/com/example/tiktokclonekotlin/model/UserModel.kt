package com.example.tiktokclonekotlin.model

data class UserModel(
    var userId : String= "",
    var email : String= "",
    var username : String= "",
    var profilePic : String= "",
    var followerList : MutableList<String> = mutableListOf(),
    var followingList : MutableList<String> = mutableListOf()


    )
