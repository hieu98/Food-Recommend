package com.example.foodrecommend.activity

import android.content.*
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.foodrecommend.R
import com.example.foodrecommend.fragment.AddFragment
import com.example.foodrecommend.fragment.HomeFragment
import com.example.foodrecommend.fragment.SearchFragment
import com.example.foodrecommend.fragment.UserFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import okhttp3.*
import okhttp3.internal.notifyAll
import java.io.*


class MainActivity : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth
    private var databaseReference : DatabaseReference?= null
    var database : FirebaseDatabase?= null
    private var fileName = "ex.txt"
    private var filepath = "save"
    var serverResponseCode = 0

    private val fragment1: Fragment = HomeFragment()
    private val fragment2: Fragment = AddFragment()
    private val fragment3: Fragment = UserFragment()
    private val fragment4: Fragment = SearchFragment()
    private var active = fragment1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
//        val user = mAuth.currentUser?.uid
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference

//        val file : File
//        val contextWrapper = ContextWrapper(application)
//        val direc :File = contextWrapper.getDir(filepath, MODE_PRIVATE)
//        file = File(direc,fileName)

//        var datasend="1 1 3.5\n" +
//                "1 2 5\n" +
//                "1 3 5\n" +
//                "1 10 3.5\n" +
//                "2 7 3.5\n" +
//                "2 8 1\n" +
//                "2 9 1.5\n" +
//                "3 2 5\n" +
//                "3 6 5\n" +
//                "3 7 0.5\n" +
//                "3 10 0\n" +
//                "4 1 1\n" +
//                "4 4 2.5\n" +
//                "4 6 3\n" +
//                "4 8 4.5\n" +
//                "5 1 5\n" +
//                "5 3 3.5\n" +
//                "5 5 3\n" +
//                "5 7 3.5\n" +
//                "6 2 3.5\n" +
//                "6 3 3\n" +
//                "6 5 1.5\n" +
//                "7 3 3.5\n" +
//                "7 5 3.5"

        var datasend = "1"
//                var datasend="1 1 3.5\n" +
//                "2 2 3.5\n" +
//                "3 3 5\n" +
//                "4 4 1\n" +
//                "5 5 5\n" +
//                "6 6 3.5\n" +
//                "7 7 3.5"
        val realid = intent.getStringExtra("useridReal")

        databaseReference?.child("Rate")?.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children ){
                    datasend = ""+data.child("userId").value.toString()+" "+data.child("itemId").value.toString()+" "+data.child("rate").value.toString()+ "\n"
                    val intent = Intent("message")
                    intent.putExtra("senddata",datasend)
                    LocalBroadcastManager.getInstance(this@MainActivity).sendBroadcast(intent)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        LocalBroadcastManager.getInstance(this).registerReceiver(object :BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                datasend += intent?.getStringExtra("senddata").toString()

            }
        }, IntentFilter("message"))

        Log.v("data send main",datasend)
        val okHttpClient = OkHttpClient()
        val formBody = FormBody.Builder().add("uid", realid!!).add("data" , datasend).build()
        val request = Request.Builder().url("http://192.168.0.101:3000/").post(formBody).build()
        okHttpClient.newCall(request).enqueue(object  : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Log.v("okhttp error","Network not found")
                    e.printStackTrace()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val testOk = response.body?.string()
                val bun = Bundle()
                bun.putString("data get",testOk)
                fragment1.arguments = bun
                Log.v("testOk",testOk!!)
            }
        })
        
        val menu = findViewById<ChipNavigationBar>(R.id.menu_bottom)
        supportFragmentManager.beginTransaction().add(R.id.fram, fragment4, "4").commit()
        supportFragmentManager.beginTransaction().add(R.id.fram, fragment3, "3").commit()
        supportFragmentManager.beginTransaction().add(R.id.fram, fragment2, "2").commit()
        supportFragmentManager.beginTransaction().add(R.id.fram, fragment1, "1").commit()
        supportFragmentManager.beginTransaction().hide(fragment2).commit()
        supportFragmentManager.beginTransaction().hide(fragment3).commit()
        supportFragmentManager.beginTransaction().hide(fragment4).commit()
//        supportFragmentManager.beginTransaction().hide(active).detach(fragment1).attach(fragment1).show(fragment1).commit()
        menu.setItemSelected(R.id.home,true)
        menu.setOnItemSelectedListener { id->
            when(id){
                R.id.home -> {
                    supportFragmentManager.beginTransaction().hide(active).detach(fragment1).attach(fragment1).show(fragment1).commit()
                    active = fragment1
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