package com.example.foodrecommend.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton
import com.example.foodrecommend.LoadingActivity
import com.example.foodrecommend.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_user.*

class UserFragment : Fragment() {

    private lateinit var mAuth :FirebaseAuth
    var databaseReference : DatabaseReference?= null
    var database : FirebaseDatabase?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user, container, false)
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("profile")
        val email = view.findViewById<TextView>(R.id.email)
        val name = view.findViewById<TextView>(R.id.name)
        val img = view.findViewById<CircleImageView>(R.id.img_user_avatar)
        val btn = view.findViewById<CircularProgressButton>(R.id.logout_btn)
        val bundle = arguments
        if (bundle?.getString("login google") == "login google"){
            val currentUser = mAuth.currentUser
            name.text = currentUser?.displayName
            email.text = currentUser?.email
            Picasso.get().load(currentUser?.photoUrl).into(img)
        }else {
            val user = mAuth.currentUser
            val userref = databaseReference?.child(user?.uid!!)
            email.text = user?.email
            userref?.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    name.text = snapshot.child("name").value.toString()
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

        btn.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(context,LoadingActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun loadProfileGG(){
        val currentUser = mAuth.currentUser
        name.text = currentUser?.displayName
        email.text = currentUser?.email
        Picasso.get().load(currentUser?.photoUrl).into(img_user_avatar)
    }

    private fun loadProfile() {
        val user = mAuth.currentUser
        val userref = databaseReference?.child(user?.uid!!)

        email.text = "a"

        userref?.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                name.text = snapshot.child("name").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}