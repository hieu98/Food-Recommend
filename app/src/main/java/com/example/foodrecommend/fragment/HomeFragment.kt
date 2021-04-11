package com.example.foodrecommend.fragment

import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrecommend.CongThucActivity
import com.example.foodrecommend.R
import com.example.foodrecommend.adapter.DanhSachApdater
import com.example.foodrecommend.data.RecycleviewData
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeFragment : Fragment(), DanhSachApdater.OnItemClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val time = view.findViewById<TextView>(R.id.tv_tg)
        val temperature = view.findViewById<TextView>(R.id.tv_tt)
        val session = view.findViewById<TextView>(R.id.tv_tg_lable)
        val weather = view.findViewById<TextView>(R.id.tv_tt_lable)
        val recyclerViewHome = view.findViewById<RecyclerView>(R.id.recyclerviewhome)
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("hh:mm")
                val formatted = current.format(formatter)
                val c : Calendar = Calendar.getInstance()
                val timeofday = c.get(Calendar.HOUR_OF_DAY)
                if (timeofday in 5..11){
                    session.text = "Sáng"
                }else if( timeofday in 12..17){
                    session.text = "Chiều"
                }else if( timeofday in 18..22){
                    session.text = "Tối"
                }else if (timeofday > 22 && timeofday <5){
                    session.text = "Đêm"
                }
                time.text = formatted
                handler.postDelayed(this, 1000)
            }
        })
        val list = getList()
        recyclerViewHome.adapter = DanhSachApdater(list,this)
        recyclerViewHome.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL,false)

        recyclerViewHome.setHasFixedSize(true)

        return view
    }

    private fun getList() : List<RecycleviewData>{
        val list = ArrayList<RecycleviewData>()
        val item1 = RecycleviewData(R.drawable.comtam, "Cơm tấm sườn bì","Đây là món ăn " +
                ".......................................................................................",
                "Trần Năng Hiếu","Sáng","Lạnh")
        val item2 = RecycleviewData(R.drawable.comtam, "Cơm tấm sườn không bì","Đây là món ăn " +
                ".......................................................................................",
            "Trần Năng Hiếu","Sáng","Lạnh")
        list += item1; list += item2
        return list
    }

    override fun OnItemClick(position: Int) {
        val intent = Intent(context, CongThucActivity::class.java)
        val a = getList()[position].ten
        intent.putExtra("ten mon",a)
        startActivity(intent)
    }


}