package com.example.foodrecommend.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrecommend.R
import com.example.foodrecommend.data.CachLam
import com.squareup.picasso.Picasso
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class CachLamAdapter(var list: List<CachLam>, var context: Context) :
    RecyclerView.Adapter<CachLamAdapter.ViewHolder>() {

    var callback: ((String) -> Int)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CachLamAdapter.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_show_cachlam, parent, false)
        EventBus.getDefault().register(this)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.stt.text = item.stt
        holder.cachlam.text = item.buoc
        Picasso.get().load(item.imageBuoc).resize(150, 150).into(holder.imgcachlam)
        holder.cachlam.setOnClickListener {
            var a: Int = callback?.invoke("A")!!
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
        val stt = view.findViewById<TextView>(R.id.txtSTT_show_cachlam)
        val cachlam = view.findViewById<TextView>(R.id.txt_show_cachlam)
        val imgcachlam = view.findViewById<ImageView>(R.id.img_show_cachlam)
    }

}