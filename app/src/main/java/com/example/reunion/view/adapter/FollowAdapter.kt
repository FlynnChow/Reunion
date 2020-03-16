package com.example.reunion.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class FollowAdapter(manager:FragmentManager,private val fragments:ArrayList<Fragment>):FragmentStatePagerAdapter(manager) {
    
    
    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }
}