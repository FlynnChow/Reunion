package com.example.reunion.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter

class HomeAdapter(manager:FragmentManager, private val fragments:Array<Fragment>):FragmentStatePagerAdapter(manager) {
    override fun getItem(position: Int) = fragments[position]

    override fun getCount() = fragments.size
}