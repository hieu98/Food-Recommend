package com.example.foodrecommend.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.foodrecommend.R
import com.example.foodrecommend.fragment.AddFragment
import com.example.foodrecommend.fragment.HomeFragment
import com.example.foodrecommend.fragment.UserFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ismaeldivita.chipnavigation.ChipNavigationBar


class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private var databaseReference: DatabaseReference? = null
    var database: FirebaseDatabase? = null
    var serverResponseCode = 0

    private val fragment1: Fragment = HomeFragment()
    private val fragment2: Fragment = AddFragment()
    private val fragment3: Fragment = UserFragment()
    private var active = fragment1

    var callback: ((String) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference

        val realid = intent.getStringExtra("useridReal")
        val bun = Bundle()
        bun.putString("realid", realid)
        fragment1.arguments = bun

        supportFragmentManager.beginTransaction().remove(fragment3).commit()
        supportFragmentManager.beginTransaction().remove(fragment2).commit()
        supportFragmentManager.beginTransaction().remove(fragment1).commit()

        val menu = findViewById<ChipNavigationBar>(R.id.menu_bottom)
        supportFragmentManager.beginTransaction().add(R.id.fram, fragment3, "3").commit()
        supportFragmentManager.beginTransaction().add(R.id.fram, fragment2, "2").commit()
        supportFragmentManager.beginTransaction().add(R.id.fram, fragment1, "1").commit()
        supportFragmentManager.beginTransaction().hide(fragment2).commit()
        supportFragmentManager.beginTransaction().hide(fragment3).commit()

        menu.setItemSelected(R.id.home, true)
        menu.setOnItemSelectedListener { id ->
            when (id) {
                R.id.home -> {
                    supportFragmentManager.beginTransaction().hide(active).detach(fragment1)
                        .attach(fragment1).show(fragment1).commit()
                    active = fragment1
                }
                R.id.add -> {
                    supportFragmentManager.beginTransaction().hide(active).detach(fragment2)
                        .attach(fragment2).show(fragment2).commit()
                    active = fragment2
                }
                R.id.user -> {
                    supportFragmentManager.beginTransaction().hide(active).detach(fragment3)
                        .attach(fragment3).show(fragment3).commit()
                    active = fragment3
                }
            }
        }

        val checkLoginGG = intent.getBooleanExtra("login google", false)

        if (checkLoginGG){
            val bundle = Bundle()
            bundle.putString("login google","login google")
            fragment3.arguments = bundle
        }

    }


}