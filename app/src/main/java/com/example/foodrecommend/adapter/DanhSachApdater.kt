package com.example.foodrecommend.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrecommend.R
import com.example.foodrecommend.data.RecycleviewData
import com.squareup.picasso.Picasso

class DanhSachApdater(private val list: List<RecycleviewData>,
                      private val listener: OnItemClickListener) : RecyclerView.Adapter<DanhSachApdater.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DanhSachApdater.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_danhsachmon,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: DanhSachApdater.ViewHolder, position: Int) {
        val item = list[position]
        Picasso.get().load(item.image).resize(100, 100).into(holder.img);
//        holder.img.setImageResource(item.image)
        holder.tenmon.text = item.ten
        holder.nguoidang.text = item.nguoidang
        holder.thoigian.text = item.thoigian
        holder.thoitiet.text = item.thoitiet

    }

    override fun getItemCount() = list.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view),View.OnClickListener {
        val img : ImageView = view.findViewById(R.id.imgv_dsct)
        val tenmon = view.findViewById<TextView>(R.id.txttenmon_dsct)
        val nguoidang = view.findViewById<TextView>(R.id.txtnguoidang_dsct)
        val thoigian = view.findViewById<TextView>(R.id.txtthoigian_dsct)
        val thoitiet = view.findViewById<TextView>(R.id.txtthoitiet_dsct)
        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.OnItemClick(position)
            }
        }
    }
    interface OnItemClickListener{
        fun OnItemClick(position: Int)
    }

}