package com.example.reunion.view.adapter

import android.app.Activity
import android.view.ViewGroup
import androidx.fragment.app.*
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.reunion.MyApplication
import com.example.reunion.R
import com.example.reunion.base.BaseViewHolder
import com.example.reunion.base.BaseViewModel
import com.example.reunion.databinding.ViewRecyclerViewBinding
import com.example.reunion.databinding.ViewReplyBinding

class MyTopicAdapter(activity: FragmentActivity,private val fragments:ArrayList<Fragment>):FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }


}