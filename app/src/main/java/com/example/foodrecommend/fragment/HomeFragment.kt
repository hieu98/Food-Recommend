package com.example.foodrecommend.fragment

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrecommend.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class HomeFragment : Fragment() {
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
                time.text = formatted
                handler.postDelayed(this, 1000)
            }
        })


        return view
    }

}