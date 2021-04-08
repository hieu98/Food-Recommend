package com.example.foodrecommend.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.example.foodrecommend.R
import com.example.foodrecommend.adapter.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout

class UserFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user, container, false)
        val viewPager = view?.findViewById<ViewPager>(R.id.viewPager)
        val tabs = view?.findViewById<TabLayout>(R.id.tabs)
        val adapter = ViewPagerAdapter(requireActivity().supportFragmentManager )
        adapter.addFragment(ProfileFragment(),"Profile")
        adapter.addFragment(SaveFragment(),"Save")
        adapter.addFragment(UploadFragment(),"UpLoad")
        viewPager?.adapter = adapter
        tabs?.setupWithViewPager(viewPager)

        return view
    }
}