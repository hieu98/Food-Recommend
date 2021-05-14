package com.example.foodrecommend.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodrecommend.R
import com.example.foodrecommend.adapter.CachLamAdapter
import com.example.foodrecommend.adapter.DanhSachApdater
import com.example.foodrecommend.adapter.NguyenLieuAdapter
import com.example.foodrecommend.data.CachLam
import com.example.foodrecommend.data.CongThuc
import com.example.foodrecommend.data.Image
import com.example.foodrecommend.data.NguyenLieu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_cong_thuc.*
import kotlinx.android.synthetic.main.item_show_nguyenlieu.view.*
import java.util.*
import kotlin.collections.ArrayList

class CongThucActivity : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth
    private var databaseReference : DatabaseReference?= null
    private var database : FirebaseDatabase?= null
    private var storage : FirebaseStorage?= null
    private var storageReference : StorageReference?= null

    var rate : Float = 0.0f

    private lateinit var listNguyenLieu: ArrayList<NguyenLieu>
    private lateinit var listCachLam: ArrayList<CachLam>
    private lateinit var nguyenLieuAdapter: NguyenLieuAdapter
    private lateinit var cachLamAdapter: CachLamAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cong_thuc)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference
        databaseReference = database?.reference

        val userid = mAuth.currentUser.uid

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val a = intent.getSerializableExtra("mon an") as CongThuc
        rate = intent.getFloatExtra("rate",0.0f)

        supportActionBar?.title = a.ten
        txtGTmonan.text = a.gioithieu
        Picasso.get().load(a.image).into(imgv_anhbiamonan)
        ratingbar.rating = rate
        ratingbar.stepSize = .5f
        ratingbar.setOnRatingBarChangeListener{ratingbar,rating,fromUser ->
            rate = rating
            val cal = Calendar.getInstance()
            val dataref = databaseReference?.child("Rate")?.child(userid + a.itemId)
            dataref?.child("rate")?.setValue(rate)
            dataref?.child("userId")?.setValue(userid)
            dataref?.child("itemId")?.setValue(a.itemId)
            Toast.makeText(this,"Rating: $rating",Toast.LENGTH_LONG).show()
        }

        listCachLam = ArrayList()
        listNguyenLieu = ArrayList()
        listCachLam = ArrayList()
        nguyenLieuAdapter = NguyenLieuAdapter(listNguyenLieu,this)
        cachLamAdapter = CachLamAdapter(listCachLam,this)

        lv_nguyenlieu.setHasFixedSize(true)
        lv_nguyenlieu.isNestedScrollingEnabled =false
        lv_nguyenlieu.adapter =  nguyenLieuAdapter
        lv_nguyenlieu.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)

        lv_cachlam.setHasFixedSize(true)
        lv_cachlam.isNestedScrollingEnabled =false
        lv_cachlam.adapter =  cachLamAdapter
        lv_cachlam.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        getListNLvsListCL(a)


    }

    private fun getListNLvsListCL(a :CongThuc){
        var soLuong :String
        var tenNguyenLieu:String
        var stt :String
        var buoc :String
        var imgbuoc :String
        listNguyenLieu.clear()
        databaseReference!!.child("Công Thức").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children){
                    if ("" + data.child("Tên Món Ăn").value.toString() == a.ten
                        && "" +data.child("Người đăng").value.toString() == a.nguoidang
                        && "" +data.child("Ngày đăng").value.toString() == a.ngaydang
                        && "" +data.child("Ảnh bìa").value.toString() == a.image
                        && "" +data.child("Giới thiệu món ăn").value.toString() == a.gioithieu){
                        for (i in data.child("Nguyên Liệu").children){
                            soLuong = i.child("soLuong").value.toString()
                            tenNguyenLieu = i.child("tenNguyenLieu").value.toString()
                            listNguyenLieu.add(NguyenLieu(tenNguyenLieu,soLuong))

                            nguyenLieuAdapter.notifyDataSetChanged()
                        }
                        for (i in data.child("Cách Làm").children){
                            stt = i.child("stt").value.toString()
                            buoc = i.child("buoc").value.toString()
                            imgbuoc = i.child("imageBuoc").value.toString()
                            listCachLam.add(CachLam(stt,buoc,imgbuoc))

                            cachLamAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.v("cancel",error.toString())
            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (rate != 0.0f){
                    onBackPressed()
                }else{
                    showDialog()
                }
                return true
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Rate")
        builder.setMessage("Bạn có muốn đánh giá món ăn này ?")
        builder.setPositiveButton("Không") { dialogInterface: DialogInterface, i: Int ->
            onBackPressed()
        }
        builder.setNegativeButton("Có") { dialogInterface: DialogInterface, i: Int -> }
        builder.show()
    }
}