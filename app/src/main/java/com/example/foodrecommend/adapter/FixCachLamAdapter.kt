package com.example.foodrecommend.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrecommend.R
import com.example.foodrecommend.data.CachLam
import com.squareup.picasso.Picasso
import org.greenrobot.eventbus.EventBus

class FixCachLamAdapter(var list: MutableList<CachLam>, var context: Context) :RecyclerView.Adapter<FixCachLamAdapter.ViewHolder>() {

    var callback : ((ArrayList<CachLam>) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_add_cachlam, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.stt.text = item.stt
        holder.cachlam.setText(item.buoc)
        Picasso.get().load(item.imageBuoc).resize(150, 150).into(holder.imgcachlam)

        holder.imgcachlam.setOnClickListener{

        }

        holder.btnxoa.setOnClickListener {
            list.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position,list.size)
            callback?.invoke(list as ArrayList<CachLam>)
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
        val stt = view.findViewById<TextView>(R.id.txtSTT)
        val cachlam = view.findViewById<EditText>(R.id.edtext_cachlam)
        val imgcachlam = view.findViewById<ImageView>(R.id.img_add)
        val btnxoa = view.findViewById<Button>(R.id.xoa_cachlam)
    }
}
