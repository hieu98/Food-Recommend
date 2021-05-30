package com.example.foodrecommend.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ScrollView
import android.widget.Toast
import com.example.foodrecommend.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth :FirebaseAuth
    var databaseReference : DatabaseReference ?= null
    var database :FirebaseDatabase ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val scrollView = findViewById<ScrollView>(R.id.srclv)
        scrollView.isVerticalScrollBarEnabled = false

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("profile")

        register()

    }

    private fun register(){
        cirRegisterButton.setOnClickListener {
           if (TextUtils.isEmpty(editTextName.text.toString())){
               editTextName.error = "Please enter Name"
               return@setOnClickListener
           }else if (TextUtils.isEmpty(editTextEmail.text.toString())){
               editTextEmail.error = "Please enter Email"
               return@setOnClickListener
           }else if (TextUtils.isEmpty(editTextPassword.text.toString())){
               editTextPassword.error = "Please enter Password"
               return@setOnClickListener
           }else if (TextUtils.isEmpty(editTextPhone.text.toString())){
               editTextPhone.error = "Please enter Phone Number"
               return@setOnClickListener
           }
            mAuth.createUserWithEmailAndPassword(editTextEmail.text.toString(),editTextPassword.text.toString())
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        val currentUser = mAuth.currentUser
                        val currentUserDb = databaseReference?.child(currentUser?.uid!!)
                        currentUserDb?.child("name")?.setValue(editTextName.text.toString())
                        databaseReference?.addValueEventListener(object  :ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                    val countUser = snapshot.childrenCount
                                    currentUserDb?.child("useridReal")?.setValue(countUser)
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }
                        })

                        Toast.makeText(this,"Registration Success !",Toast.LENGTH_LONG).show()
                        finish()

                    }else{
                        Toast.makeText(this,"Registration failed, please try again !",Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    fun onLoginClick(view : View){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.stay)
    }
}