package com.example.tiktokclonekotlin.util

import android.content.Context
import android.widget.Toast

object ToastResponseMessage {
    fun showToast(context : Context, message: String){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show()
    }
}