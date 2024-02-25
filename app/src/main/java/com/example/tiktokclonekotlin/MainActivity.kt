package com.example.tiktokclonekotlin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.example.tiktokclonekotlin.adapter.VideoListAdapter
import com.example.tiktokclonekotlin.databinding.ActivityMainBinding
import com.example.tiktokclonekotlin.model.VideoModel
import com.example.tiktokclonekotlin.util.ToastResponseMessage
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore


class MainActivity : AppCompatActivity()  {
//    lateinit var btnSignupActivity : Button
    lateinit var binding: ActivityMainBinding
    lateinit var adapter: VideoListAdapter
    //btn_signOut

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignOut.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this,LoginActivity::class.java))
            finish()

        }
        binding.bottomNavMenu.setOnItemReselectedListener{menuItem ->
            when(menuItem.itemId){
                R.id.bottom_menu_home->{
                    ToastResponseMessage.showToast(this,"HOME")
                }
                R.id.bottom_menu_addVideo->{
                    startActivity(Intent(this,VideoUploadActivity::class.java))
                    finish()
                }
                R.id.bottom_menu_profile->{
                    startActivity(Intent(this,ProfileActivity::class.java))
                    finish()
                }
            }
            false
        }
        setupViewPager()
    }
    private fun setupViewPager(){
        val options = FirestoreRecyclerOptions.Builder<VideoModel>()
            .setQuery(
                Firebase.firestore.collection("videos"),
                VideoModel::class.java
            ).build()
        adapter = VideoListAdapter(options)
        binding.viewPager.adapter = adapter

    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }


//    override fun onClick(v: View?) {
//        when(v?.id){
//            R.id.btn_goToSignupPage ->{
//                startActivity(Intent(this@MainActivity, SignupActivity::class.java))
//            }
//        }
//    }
}

//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    TikTokCloneKotlinTheme {
//        Greeting("Android")
//    }
//}