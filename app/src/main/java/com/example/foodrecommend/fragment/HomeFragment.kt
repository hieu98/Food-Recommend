package com.example.foodrecommend.fragment

import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrecommend.activity.RecipeActivity
import com.example.foodrecommend.R
import com.example.foodrecommend.adapter.DanhSachApdater
import com.example.foodrecommend.data.CachLam
import com.example.foodrecommend.data.CongThuc
import com.example.foodrecommend.data.NguyenLieu
import com.example.foodrecommend.data.Rate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeFragment : Fragment(),DanhSachApdater.OnItemClickListener {

    private lateinit var listdata: ArrayList<CongThuc>
    private lateinit var listRate: ArrayList<Rate>
    private lateinit var recipeAdapter : DanhSachApdater

    private lateinit var mAuth : FirebaseAuth
    private var databaseReference : DatabaseReference?= null
    private var database : FirebaseDatabase?= null
    private var storage : FirebaseStorage?= null
    private var storageReference : StorageReference?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference
        databaseReference = database?.reference

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val time = view.findViewById<TextView>(R.id.tv_tg)
        val temperature = view.findViewById<TextView>(R.id.tv_tt)
        val session = view.findViewById<TextView>(R.id.txt_thoigian)
        val imgsession = view.findViewById<ImageView>(R.id.imgv_tg)
        val weather = view.findViewById<TextView>(R.id.txt_thoitiet)
        val imgweather = view.findViewById<ImageView>(R.id.imgv_tt)
        val listgoiy = view.findViewById<RecyclerView>(R.id.listgoiy)
        val listmonmoi = view.findViewById<RecyclerView>(R.id.listmonmoi)
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("HH:mm")
                val formatted = current.format(formatter)
                val c : java.util.Calendar = java.util.Calendar.getInstance()
                val timeofday = c.get(Calendar.HOUR_OF_DAY)
                if (timeofday in 5..11){
                    session.text = "Sáng"
                    Picasso.get().load(R.drawable.morning).into(imgsession)
                }else if(timeofday in 12..17){
                    session.text = "Chiều"
                    Picasso.get().load(R.drawable.sun).into(imgsession)
                }else if(timeofday in 18..21){
                    session.text = "Tối"
                    Picasso.get().load(R.drawable.moon).into(imgsession)
                }else if (timeofday >= 22 || timeofday < 5){
                    session.text = "Đêm"
                    Picasso.get().load(R.drawable.latenight).into(imgsession)
                }
                time.text = formatted
                handler.postDelayed(this, 1000)
            }
        })

        listdata = ArrayList()
        listRate = ArrayList()
        recipeAdapter = DanhSachApdater(this,listdata,listRate,context!!)
        listgoiy.setHasFixedSize(true)
        listgoiy.isNestedScrollingEnabled =false
        listgoiy.adapter = recipeAdapter
        listgoiy.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)

        listmonmoi.setHasFixedSize(true)
        listmonmoi.isNestedScrollingEnabled =false
        listmonmoi.adapter = recipeAdapter
        listmonmoi.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)

        getData()
        getRate()

        return view
    }

    private fun getData() {
        var userId :String
        var ten : String
        var nguoidang :String
        var ngaydang :String
        var anhbia :String
        var gioithieu :String
        var itemId :String
        var congThuc :CongThuc
        listdata.clear()
        databaseReference!!.child("Công Thức").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children){
                    ten = "" + data.child("Tên Món Ăn").value.toString()
                    nguoidang = "" +data.child("Người đăng").value.toString()
                    ngaydang = "" +data.child("Ngày đăng").value.toString()
                    anhbia = "" +data.child("Ảnh bìa").value.toString()
                    gioithieu = "" +data.child("Giới thiệu món ăn").value.toString()
                    itemId = "" +data.child("ItemId").value.toString()
                    userId = "" +data.child("UserId").value.toString()

                    congThuc = CongThuc(anhbia,ten,gioithieu,ngaydang,nguoidang,itemId,userId)
                    listdata.add(congThuc)

                    recipeAdapter.notifyDataSetChanged()
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
                    recipeAdapter.notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.v("cancel",error.toString())
            }
        })
    }

    override fun OnItemClick(position: Int) {
        val item :CongThuc = listdata[position]
        val intent = Intent(context, RecipeActivity::class.java)
        intent.putExtra("mon an",item)
        startActivity(intent)
    }

}