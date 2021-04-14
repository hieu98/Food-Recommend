package com.example.foodrecommend

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth

class LoadingActivity : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser

        Handler().postDelayed({
            if(user == null){
                val signInIntent = Intent(this, LoginActivity::class.java)
                startActivity(signInIntent)
                finish()
            }else{
                val userIntent = Intent(this,MainActivity::class.java)
                val a = intent.getBooleanExtra("loginGG",false)
                if(a){
                    userIntent.putExtra("login google",true)
                }
                startActivity(userIntent)
                finish()
            }
        },2000)
    }
}