package com.example.foodrecommend.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrecommend.R
import com.example.foodrecommend.data.NguyenLieu

class FixNguyenLieuAdapter(var list: MutableList<NguyenLieu>, var context: Context) :RecyclerView.Adapter<FixNguyenLieuAdapter.ViewHolder>() {

    var callback : ((List<NguyenLieu>)-> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_add_nguyenlieu, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.soLuong.setText(item.soLuong)
        holder.tenNguyenLieu.setText(item.tenNguyenLieu)
        holder.xoaNguyenlieu.setOnClickListener {
            list.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position,list.size)
            callback?.invoke(list)
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount() = list.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tenNguyenLieu = view.findViewById<EditText>(R.id.edtext_nguyenlieu)
        val soLuong = view.findViewById<EditText>(R.id.edtext_soluong)
        val xoaNguyenlieu = view.findViewById<Button>(R.id.xoa_nguyenlieu1)
    }
}