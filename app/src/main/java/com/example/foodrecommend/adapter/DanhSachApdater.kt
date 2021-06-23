package com.example.foodrecommend.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrecommend.R
import com.example.foodrecommend.data.CongThuc
import com.example.foodrecommend.data.Rate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class DanhSachApdater(
    var listener: OnItemClickListener,
    var list: List<CongThuc>,
    var listRate: List<Rate>,
    var context: Context
) : RecyclerView.Adapter<DanhSachApdater.ViewHolder>() {

    private lateinit var mAuth: FirebaseAuth
    var databaseReference: DatabaseReference? = null
    var database: FirebaseDatabase? = null
    private lateinit var arrayData: ArrayList<String>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DanhSachApdater.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_danhsachmon_new, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: DanhSachApdater.ViewHolder, position: Int) {
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference?.child("profile")?.child(mAuth.currentUser?.uid!!)
        val item = list[position]
        arrayData = ArrayList()
        mAuth = FirebaseAuth.getInstance()
        Picasso.get().load(item.image).resize(100, 100).into(holder.img)
        holder.tenmon.text = item.ten
        holder.nguoidang.text = item.nguoidang
        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usid = "" + snapshot.child("useridReal").value.toString()
                if (listRate.isNotEmpty()) {
                    for (i in listRate.indices) {
                        if (item.itemId == listRate[i].itemId && usid == listRate[i].userId) {
                            holder.rate.rating = listRate[i].rate.toFloat()
                            Log.v("Rate-item", listRate[i].rate)
                            break
                        } else {
                            holder.rate.rating = 0.0f
                        }
                    }
                } else {
                    holder.rate.rating = 0.0f
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.v("cancel", error.toString())
            }

        })
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

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val img: ImageView = view.findViewById(R.id.imgv_dsct)
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

    interface OnItemClickListener {
        fun OnItemClick(position: Int)
    }
}