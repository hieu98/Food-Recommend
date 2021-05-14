package com.example.foodrecommend.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrecommend.R
import com.example.foodrecommend.data.NguyenLieu

class NguyenLieuAdapter (var list: List<NguyenLieu>, var context: Context) : RecyclerView.Adapter<NguyenLieuAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NguyenLieuAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_show_nguyenlieu,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: NguyenLieuAdapter.ViewHolder, position: Int) {
        val item = list[position]
        holder.soLuong.text = item.soLuong
        holder.tenNguyenLieu.text = item.tenNguyenLieu
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount() = list.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tenNguyenLieu = view.findViewById<TextView>(R.id.txt_nguyenlieu)
        val soLuong = view.findViewById<TextView>(R.id.txt_soluong)
    }


}