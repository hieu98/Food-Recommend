package com.example.foodrecommend.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodrecommend.R
import com.example.foodrecommend.adapter.DanhSachApdater
import com.example.foodrecommend.data.CongThuc
import com.example.foodrecommend.data.Rate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_your_food.*

class YourFoodActivity : AppCompatActivity(),DanhSachApdater.OnItemClickListener {

    private lateinit var mAuth : FirebaseAuth
    private var databaseReference : DatabaseReference?= null
    private var database : FirebaseDatabase?= null

    private lateinit var danhsachAdapter : DanhSachApdater
    private lateinit var list :ArrayList<CongThuc>
    private lateinit var listRate :ArrayList<Rate>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_your_food)

        supportActionBar?.title = "Món ăn của bạn"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference

        list = ArrayList()
        listRate = ArrayList()
        danhsachAdapter = DanhSachApdater(this,list,listRate,this)
        recyclerview.setHasFixedSize(true)
        recyclerview.adapter = danhsachAdapter
        recyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)

        val username = mAuth.currentUser?.uid
        getData(username)
        getRate()

    }

    private fun getData(user:String?) {
        var userId :String
        var ten : String
        var nguoidang :String
        var ngaydang :String
        var anhbia :String
        var gioithieu :String
        var itemId :String
        var congThuc :CongThuc
        list.clear()
        databaseReference!!.child("Công Thức").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children){
                    ten = "" + data.child("Tên Món Ăn").value.toString()
                    nguoidang = "" +data.child("Người đăng").value.toString()
                    ngaydang = "" +data.child("Ngày đăng").value.toString()
                    anhbia = "" +data.child("Ảnh bìa").value.toString()
                    gioithieu = "" +data.child("Giới thiệu món ăn").value.toString()
                    itemId = "" +data.child("ItemId").value.toString()
                    userId = "" +data.child("UserId").value.toString()

                    if (userId == user){
                        congThuc = CongThuc(anhbia,ten,gioithieu,ngaydang,nguoidang,itemId,userId)
                        list.add(congThuc)
                        danhsachAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.v("cancel",error.toString())
            }

        })
    }

    private fun getRate(){
        var userId : String
        var itemId :String
        var rate :String
        databaseReference?.child("Rate")?.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot1: DataSnapshot) {
                for (data in snapshot1.children){
                    userId = "" + data.child("userId").value.toString()
                    itemId = "" + data.child("itemId").value.toString()
                    rate = "" + data.child("rate").value.toString()

                    listRate.add(Rate(userId,itemId,rate))
                    Log.v("rate",listRate.toString())
                    danhsachAdapter.notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.v("cancel",error.toString())
            }
        })
    }

    override fun OnItemClick(position: Int) {
        val item :CongThuc = list[position]
        val intent = Intent(this, RecipeActivity::class.java)
        intent.putExtra("mon an",item)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }
}