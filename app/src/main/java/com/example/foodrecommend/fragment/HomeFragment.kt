package com.example.foodrecommend.fragment

import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.foodrecommend.activity.CongThucActivity
import com.example.foodrecommend.R
import com.example.foodrecommend.adapter.RecipeAdapter
import com.example.foodrecommend.data.RecycleviewData
import com.squareup.picasso.Picasso
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeFragment : Fragment(), AdapterView.OnItemClickListener {

    private lateinit var listdata:List<RecycleviewData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val time = view.findViewById<TextView>(R.id.tv_tg)
        val temperature = view.findViewById<TextView>(R.id.tv_tt)
        val session = view.findViewById<TextView>(R.id.txt_thoigian)
        val imgsession = view.findViewById<ImageView>(R.id.imgv_tg)
        val weather = view.findViewById<TextView>(R.id.txt_thoitiet)
        val imgweather = view.findViewById<ImageView>(R.id.imgv_tt)
        val recyclerViewHome = view.findViewById<ListView>(R.id.recyclerviewhome)
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("HH:mm")
                val formatted = current.format(formatter)
                val c : java.util.Calendar = java.util.Calendar.getInstance()
                val timeofday = c.get(Calendar.HOUR_OF_DAY)
                if (timeofday in 5..11){
                    session.text = "Sáng"
                    Picasso.get().load(R.drawable.morning).into(imgsession)
                }else if(timeofday in 12..17){
                    session.text = "Chiều"
                    Picasso.get().load(R.drawable.sun).into(imgsession)
                }else if(timeofday in 18..21){
                    session.text = "Tối"
                    Picasso.get().load(R.drawable.moon).into(imgsession)
                }else if (timeofday >= 22 || timeofday < 5){
                    session.text = "Đêm"
                    Picasso.get().load(R.drawable.latenight).into(imgsession)
                }
                time.text = formatted
                handler.postDelayed(this, 1000)
            }
        })

        val list = getList()
        listdata = getList()
//        recyclerViewHome.adapter = DanhSachApdater(list,this)
        recyclerViewHome.adapter = this.context?.let { RecipeAdapter(it,list) }
        recyclerViewHome.onItemClickListener = this

        return view
    }

    private fun getList() : List<RecycleviewData>{
        val list = ArrayList<RecycleviewData>()
        val item1 = RecycleviewData(R.drawable.comtam, "Cơm tấm sườn bì","Đây là món ăn " +
                ".......................................................................................",
                "Trần Năng Hiếu","Sáng","Lạnh",0.0f)
        val item2 = RecycleviewData(R.drawable.comtam, "Cơm tấm sườn không bì","Đây là món ăn " +
                ".......................................................................................",
            "Trần Năng Hiếu","Sáng","Lạnh",3.5f)
        list += item1
        list += item2
        return list
    }

//    override fun OnItemClick(position: Int) {
//        val intent = Intent(context, CongThucActivity::class.java)
//        val a = getList()[position].ten
//        intent.putExtra("ten mon",a)
//        startActivity(intent)
//    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item :RecycleviewData = listdata[position]
        val intent = Intent(context, CongThucActivity::class.java)
        intent.putExtra("ten mon",item.ten)
        intent.putExtra("rating",item.rate)
        startActivity(intent)
    }


}