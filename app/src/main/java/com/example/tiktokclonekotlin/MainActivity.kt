package com.example.tiktokclonekotlin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.example.tiktokclonekotlin.databinding.ActivityMainBinding
import com.example.tiktokclonekotlin.util.ToastResponseMessage
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity()  {
//    lateinit var btnSignupActivity : Button
    lateinit var binding: ActivityMainBinding
    //btn_signOut

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        binding.btnSignOut.setOnClickListener{
//            startActivity(Intent(this, SignupActivity::class.java))
//            finish()
//        }
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
                    ToastResponseMessage.showToast(this,"PROFILE")
                }
            }
            false
        }
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