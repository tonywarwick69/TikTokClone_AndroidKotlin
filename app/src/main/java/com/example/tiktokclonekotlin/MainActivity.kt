package com.example.tiktokclonekotlin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.ComponentActivity


class MainActivity : ComponentActivity(), View.OnClickListener  {
    lateinit var btnSignupActivity : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnSignupActivity =findViewById(R.id.btn_goToSignupPage)
        btnSignupActivity.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_goToSignupPage ->{
                startActivity(Intent(this@MainActivity, SignupActivity::class.java))
            }
        }
    }
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