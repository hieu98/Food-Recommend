package com.example.foodrecommend.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.example.foodrecommend.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class LoadingActivity : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth
    private var databaseReference : DatabaseReference?= null
    private var database : FirebaseDatabase?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference?.child("profile")

        Handler().postDelayed({
            if(user == null){
                val signInIntent = Intent(this, LoginActivity::class.java)
                startActivity(signInIntent)
                finish()
            }else{
                val userIntent = Intent(this, MainActivity::class.java)
                val a = intent.getBooleanExtra("loginGG",false)
                if(a){
                    userIntent.putExtra("login google",true)
                }
                databaseReference?.child(user.uid)?.addValueEventListener(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val realid = snapshot.child("useridReal").value.toString()
                        userIntent.putExtra("useridReal",realid)
                        startActivity(userIntent)
                        finish()
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.v("error",error.toString())
                    }

                })
            }
        },2000)
    }
}