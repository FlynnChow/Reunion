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
import com.example.reunion.databinding.ItemHomeBinding
import com.example.reunion.databinding.ItemImMessageBinding
import com.example.reunion.databinding.ItemNewsBinding
import com.example.reunion.databinding.ItemUserMessageBinding
import com.example.reunion.repostory.bean.ImMessageBean
import com.example.reunion.repostory.bean.ImMessageIndex
import com.example.reunion.repostory.bean.NewsBean
import com.example.reunion.repostory.bean.TopicBean
import com.example.reunion.view.NewsActivity

class UserMessageAdapter(
    private val listener:(ImMessageIndex,Int)->Unit
):RecyclerView.Adapter<BaseViewHolder<ItemUserMessageBinding>>() {
    val list:ArrayList<ImMessageIndex> = ArrayList()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ItemUserMessageBinding> {
        val mBinding:ItemUserMessageBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.item_user_message,parent,false)
        return BaseViewHolder(mBinding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ItemUserMessageBinding>, position: Int) {
        val mBinding = holder.mBinding
        mBinding.data = list[position]
        mBinding.root.setOnClickListener {
            listener.invoke(list[position],0)
        }
        mBinding.root.setOnLongClickListener {
            listener.invoke(list[position],1)
            true
        }
        mBinding.executePendingBindings()
    }
}