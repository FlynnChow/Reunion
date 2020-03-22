package com.example.reunion.view.adapter

import android.app.Activity
import androidx.fragment.app.*
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.reunion.MyApplication
import com.example.reunion.R

class HomeAdapter(activity: Fragment,private val fragments:ArrayList<Fragment>):FragmentStateAdapter(activity) {
    private val createdIds = HashSet<Long>()
    var ids:ArrayList<Long>? = null

    override fun getItemCount(): Int {
        if (ids == null) return 0
        return ids!!.size
    }



    val titleList by lazy { ArrayList<String>().apply {
        add(MyApplication.resource().getString(R.string.home_follow))
        add(MyApplication.resource().getString(R.string.home_recommend))
        add(MyApplication.resource().getString(R.string.home_nearby))
        add(MyApplication.resource().getString(R.string.home_find))
        add(MyApplication.resource().getString(R.string.home_claim))
        add(MyApplication.resource().getString(R.string.home_news))
    } }

    override fun createFragment(position: Int): Fragment {
        val id = ids!![position]
        createdIds.add(id)
        return when(id){
            0L -> fragments[0]
            1L -> fragments[1]
            2L -> fragments[2]
            3L -> fragments[3]
            4L -> fragments[4]
            else -> fragments[5]
        }
    }

    override fun getItemId(position: Int): Long {
        return ids!![position]
    }

    override fun containsItem(itemId: Long): Boolean {
        return createdIds.contains(itemId)
    }
}