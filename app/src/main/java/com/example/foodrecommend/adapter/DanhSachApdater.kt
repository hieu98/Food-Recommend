package com.example.foodrecommend.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrecommend.R
import com.example.foodrecommend.data.CongThuc
import com.example.foodrecommend.data.Rate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class DanhSachApdater( var listener: OnItemClickListener, var list: List<CongThuc>,var listRate :List<Rate>,var context: Context) : RecyclerView.Adapter<DanhSachApdater.ViewHolder>(){

    private lateinit var mAuth : FirebaseAuth
    private var storage : FirebaseStorage?= null
    private var storageReference : StorageReference?= null
    private var rateItem : Float? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DanhSachApdater.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_danhsachmon_new,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: DanhSachApdater.ViewHolder, position: Int) {
        val item = list[position]
        mAuth = FirebaseAuth.getInstance()
//        if (item.userId != mAuth.currentUser.uid.toString()){
            Picasso.get().load(item.image).resize(100,100).into(holder.img)
            holder.tenmon.text = item.ten
            holder.nguoidang.text = item.nguoidang
            if(listRate.size != 0){
                for (i in 0 until listRate.size){
                    if (item.itemId == listRate[i].itemId && mAuth.currentUser.uid.toString() == listRate[i].userId){
                        holder.rate.rating = listRate[i].rate.toFloat()
                        Log.v("Rate-item",listRate[i].rate.toString())
                        break
                    }else {
                        holder.rate.rating = 0.0f
                    }
                }
            }else{
                holder.rate.rating = 0.0f
            }
//        }


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
        val rate = view.findViewById<RatingBar>(R.id.rate)
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

}