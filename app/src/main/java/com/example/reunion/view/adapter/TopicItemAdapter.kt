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

class TopicItemAdapter:RecyclerView.Adapter<BaseViewHolder<ItemHomeBinding>>() {

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
        mBinding.executePendingBindings()
    }

    init {
        for (index in 0 .. 10){
            list.add(TopicBean().apply {
                nickName = "测试名字"
                sTitle = "测试标题"
                pictures = arrayListOf("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=1361433311,1491821341&fm=26&gp=0.jpg")
                header = "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2142586472,3495055421&fm=26&gp=0.jpg"
            })
        }
        notifyDataSetChanged()
    }
}