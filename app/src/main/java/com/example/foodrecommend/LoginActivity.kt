package com.example.foodrecommend

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ScrollView

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val scrollView = findViewById<ScrollView>(R.id.srclview)
        scrollView.isVerticalScrollBarEnabled = false
    }
    fun onLoginClick(view :View){
        val intent = Intent(this,RegisterActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right,R.anim.stay)
    }
}