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
import com.example.reunion.databinding.ItemNewsBinding
import com.example.reunion.repostory.bean.NewsBean
import com.example.reunion.repostory.bean.TopicBean
import com.example.reunion.view.NewsActivity

class TopicItemAdapter(private val listener:((TopicBean)->Unit)? = null):RecyclerView.Adapter<BaseViewHolder<ItemHomeBinding>>() {
    val list:ArrayList<TopicBean> = ArrayList()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ItemHomeBinding> {
        val mBinding:ItemHomeBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.item_home,parent,false)
        return BaseViewHolder(mBinding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ItemHomeBinding>, position: Int) {
        val mBinding = holder.mBinding
        mBinding.bean = list[position]
        mBinding.root.setOnClickListener {
            listener?.invoke(list[position])
        }
        mBinding.executePendingBindings()
    }
}