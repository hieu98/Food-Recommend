package com.example.foodrecommend

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import com.example.foodrecommend.fragment.AddFragment
import com.example.foodrecommend.fragment.HomeFragment
import com.example.foodrecommend.fragment.SearchFragment
import com.example.foodrecommend.fragment.UserFragment
import com.google.firebase.auth.FirebaseAuth
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import kotlinx.android.synthetic.main.fragment_add.*

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth
    private val fragment1: Fragment = HomeFragment()
    private val fragment2: Fragment = AddFragment()
    private val fragment3: Fragment = UserFragment()
    private val fragment4: Fragment = SearchFragment()
    var active = fragment1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser

        val menu = findViewById<ChipNavigationBar>(R.id.menu_bottom)
        supportFragmentManager.beginTransaction().add(R.id.fram, fragment4, "4").commit()
        supportFragmentManager.beginTransaction().add(R.id.fram, fragment3, "3").commit()
        supportFragmentManager.beginTransaction().add(R.id.fram, fragment2, "2").commit()
        supportFragmentManager.beginTransaction().add(R.id.fram, fragment1, "1").commit()
        supportFragmentManager.beginTransaction().hide(fragment2).commit()
        supportFragmentManager.beginTransaction().hide(fragment3).commit()
        supportFragmentManager.beginTransaction().hide(fragment4).commit()
        supportFragmentManager.beginTransaction().hide(active).detach(fragment1).attach(fragment1).show(fragment1).commit()
        menu.setItemSelected(R.id.home,true)
        menu.setOnItemSelectedListener { id->
            when(id){
                R.id.home -> {
                    supportFragmentManager.beginTransaction().hide(active).detach(fragment1).attach(fragment1).show(fragment1).commit()
                    active = fragment1
                }
                R.id.search-> {
                    supportFragmentManager.beginTransaction().hide(active).detach(fragment4).attach(fragment4).show(fragment4).commit()
                    active = fragment4
                }
                R.id.add -> {
                    supportFragmentManager.beginTransaction().hide(active).detach(fragment2).attach(fragment2).show(fragment2).commit()
                    active = fragment2
                }
                R.id.user -> {
                    supportFragmentManager.beginTransaction().hide(active).detach(fragment3).attach(fragment3).show(fragment3).commit()
                    active = fragment3
                }
            }
        }


        val checkLoginGG = intent.getBooleanExtra("login google",false)
        if (checkLoginGG){
            val bundle = Bundle()
            bundle.putString("login google","login google")
            fragment3.arguments = bundle
        }

    }

}