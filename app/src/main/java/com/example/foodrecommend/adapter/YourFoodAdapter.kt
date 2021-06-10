package com.example.foodrecommend.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrecommend.R
import com.example.foodrecommend.activity.LoadingActivity
import com.example.foodrecommend.data.CongThuc
import com.example.foodrecommend.data.Rate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class YourFoodAdapter( var listener: OnItemClickListener, var list: List<CongThuc>,var listRate :List<Rate>,var context: Context) : RecyclerView.Adapter<YourFoodAdapter.ViewHolder>(){

    private lateinit var mAuth : FirebaseAuth
    var databaseReference : DatabaseReference?= null
    var database : FirebaseDatabase?= null
    private lateinit var arrayData :ArrayList<String>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YourFoodAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_danhsachmoncuaban,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: YourFoodAdapter.ViewHolder, position: Int) {
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference?.child("Công thức")
        val item = list[position]
        arrayData = ArrayList()
        mAuth = FirebaseAuth.getInstance()
        Picasso.get().load(item.image).resize(100,100).into(holder.img)
        holder.tenmon.text = item.ten
        holder.nguoidang.text = item.nguoidang
        holder.btnsua.setOnClickListener {

        }
        holder.btnxoa.setOnClickListener {
            showDialog()
            databaseReference?.addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.children){
                        if (data.child("ItemId").value == item.itemId ){
                            data.ref.removeValue()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
//        holder.thoigian.text = item.thoigian
//        holder.thoitiet.text = item.thoitiet


    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount() = list.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view),View.OnClickListener {
        val img : ImageView = view.findViewById(R.id.imgv_dsct)
        val tenmon = view.findViewById<TextView>(R.id.txttenmon_dsct)
        val nguoidang = view.findViewById<TextView>(R.id.txtnguoidang_dsct)
        val btnxoa = view.findViewById<Button>(R.id.btn_xoamon)
        val btnsua = view.findViewById<Button>(R.id.btn_suamon)
        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.OnItemClick(position)
            }
        }
//        val thoigian = view.findViewById<TextView>(R.id.txtthoigian_dsct)
//        val thoitiet = view.findViewById<TextView>(R.id.txtthoitiet_dsct)
    }
    interface OnItemClickListener{
        fun OnItemClick(position: Int)
    }

    private fun showDialog(){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete")
        builder.setMessage("Bạn có muốn xóa món ăn này ?")
        builder.setPositiveButton("Không") { dialogInterface: DialogInterface, i: Int ->
            val intent = Intent(context, LoadingActivity::class.java)
            intent.putExtra("add data",true)
            context.applicationContext.startActivity(intent)
        }
        builder.setNegativeButton("Có") { dialogInterface: DialogInterface, i: Int -> }
        builder.show()
    }
}