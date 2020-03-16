package com.example.reunion.view.adapter

import android.content.Intent
import android.os.Bundle
import android.os.UserHandle
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
import com.example.reunion.databinding.ItemHomeBinding
import com.example.reunion.databinding.ItemImMessageBinding
import com.example.reunion.databinding.ItemNewsBinding
import com.example.reunion.databinding.ItemUserMessageBinding
import com.example.reunion.repostory.bean.ImMessageBean
import com.example.reunion.repostory.bean.ImMessageIndex
import com.example.reunion.repostory.bean.NewsBean
import com.example.reunion.repostory.bean.TopicBean
import com.example.reunion.repostory.local_resource.UserHelper
import com.example.reunion.view.NewsActivity

class ImAdapter(
    private val listener:(ImMessageBean)->Unit
):RecyclerView.Adapter<BaseViewHolder<ItemImMessageBinding>>() {
    val list:ArrayList<ImMessageBean> = ArrayList()
    private val uid = UserHelper.getUser()?.uId?:""

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ItemImMessageBinding> {
        val mBinding:ItemImMessageBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.item_im_message,parent,false)
        return BaseViewHolder(mBinding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ItemImMessageBinding>, position: Int) {
        val mBinding = holder.mBinding
        val data = list[position]
        data.setMine(uid)
        if (position == list.size - 1){
            data.setTimeVisible(0)
        }else{
            data.setTimeVisible(list[position + 1].time?:0)
        }
        mBinding.sendError.setOnClickListener {
            listener.invoke(data)
        }
        mBinding.data = data
        mBinding.executePendingBindings()
    }
}