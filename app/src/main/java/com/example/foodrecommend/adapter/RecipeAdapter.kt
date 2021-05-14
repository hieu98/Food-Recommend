package com.example.foodrecommend.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.example.foodrecommend.R
import com.example.foodrecommend.data.CongThuc
import com.squareup.picasso.Picasso

class RecipeAdapter (var context: Context, var list: List<CongThuc>) : BaseAdapter(){
    override fun getItem(p0: Int): Any {
        return list.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view = View.inflate(context,R.layout.item_danhsachmon_new,null)
        val item :CongThuc = list[p0]
        val img : ImageView = view.findViewById(R.id.imgv_dsct)
        val tenmon = view.findViewById<TextView>(R.id.txttenmon_dsct)
        val nguoidang = view.findViewById<TextView>(R.id.txtnguoidang_dsct)
        val rate = view.findViewById<RatingBar>(R.id.rate)
//        val thoigian = view.findViewById<TextView>(R.id.txtthoigian_dsct)
//        val thoitiet = view.findViewById<TextView>(R.id.txtthoitiet_dsct)

        tenmon.text = item.ten
        nguoidang.text = item.nguoidang
//        rate.rating = item.rate
//        thoigian.text = item.thoigian
//        thoitiet.text = item.thoitiet
        Picasso.get().load(item.image).fit().centerCrop().into(img)

        return view
    }
}
