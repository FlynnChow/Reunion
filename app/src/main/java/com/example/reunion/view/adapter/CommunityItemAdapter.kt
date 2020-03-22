package com.example.reunion.view.adapter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.reunion.R
import com.example.reunion.base.BaseViewHolder
import com.example.reunion.databinding.ItemCommunityBinding
import com.example.reunion.databinding.ItemNewsBinding
import com.example.reunion.databinding.ItemSearchUserBinding
import com.example.reunion.repostory.bean.CommunityBean
import com.example.reunion.repostory.bean.NewsBean
import com.example.reunion.repostory.bean.User
import com.example.reunion.view.MyTopicActivity
import com.example.reunion.view.NewsActivity

class CommunityItemAdapter(private val listener:(CommunityBean)->Unit):RecyclerView.Adapter<BaseViewHolder<ItemCommunityBinding>>() {

    val datas:ArrayList<CommunityBean> = ArrayList()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ItemCommunityBinding> {
        val mBinding:ItemCommunityBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.item_community,parent,false)
        return BaseViewHolder(mBinding)
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ItemCommunityBinding>, position: Int) {
        val mBinding = holder.mBinding
        mBinding.data = datas[position]
        mBinding.root.setOnClickListener {
            listener.invoke(datas[position])
        }
        mBinding.header.setOnClickListener {
            MyTopicActivity.startActivity(mBinding.header.context,datas[position].uId)
        }

        mBinding.executePendingBindings()
    }
}