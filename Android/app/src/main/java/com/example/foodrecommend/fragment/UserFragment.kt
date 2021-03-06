package com.example.foodrecommend.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton
import com.example.foodrecommend.activity.LoadingActivity
import com.example.foodrecommend.R
import com.example.foodrecommend.activity.MainActivity
import com.example.foodrecommend.activity.YourFoodActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private var databaseReference: DatabaseReference? = null
    private var database: FirebaseDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user, container, false)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("profile")
        val inten = Intent(context, YourFoodActivity::class.java)

        val email = view.findViewById<TextView>(R.id.email)
        val name = view.findViewById<TextView>(R.id.name)
        val img = view.findViewById<CircleImageView>(R.id.img_user_avatar)
        val btn = view.findViewById<CircularProgressButton>(R.id.logout_btn)
        val btn_your = view.findViewById<Button>(R.id.your_monan)

        val bundle = arguments
        if (bundle?.getString("login google") == "login google") {

            val currentUser = mAuth.currentUser
            val currentUserDb = databaseReference?.child(currentUser?.uid!!)

            currentUserDb?.child("name")?.setValue(currentUser?.displayName)
            currentUserDb?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val usid = "" + snapshot.child("useridReal").value.toString()
                    if (usid != "null") {
                        inten.putExtra("uid", usid)
                    } else {
                        databaseReference?.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (usid == "null") {
                                    val countUser = snapshot.childrenCount
                                    currentUserDb.child("useridReal").setValue(countUser)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.v("L???i kh??ng th??m id user", "L???i kh??ng th??m id user")
                            }
                        })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.v("error", error.message)
                }

            })

            name.text = currentUser?.displayName
            email.text = currentUser?.email
            Picasso.get().load(currentUser?.photoUrl).into(img)

        } else {
            val user = mAuth.currentUser
            val userref = databaseReference?.child(user?.uid!!)
            email.text = user?.email

            userref?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    name.text = snapshot.child("name").value.toString()
                    val usid = "" + snapshot.child("useridReal").value.toString()
                    if (usid != "null") {
                        inten.putExtra("uid", usid)
                        Log.v("usid", usid)
                    } else {
                        Log.v("s???a uid", "true")
                        databaseReference?.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (usid == "null") {
                                    val countUser = snapshot.childrenCount
                                    userref.child("useridReal").setValue(countUser)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.v("L???i kh??ng th??m id user", "L???i kh??ng th??m id user")
                            }
                        })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }

        btn.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(context, LoadingActivity::class.java)
            startActivity(intent)
        }

        btn_your.setOnClickListener {
            startActivity(inten)
        }
        return view
    }

}