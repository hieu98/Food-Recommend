package com.example.foodrecommend

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ScrollView

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val scrollView = findViewById<ScrollView>(R.id.srclv)
        scrollView.isVerticalScrollBarEnabled = false
    }
    fun onLoginClick(view : View){
        val intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left,R.anim.stay)
    }
}