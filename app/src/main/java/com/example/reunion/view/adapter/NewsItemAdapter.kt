package com.example.reunion.view.adapter

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
import com.example.reunion.databinding.ItemNewsBinding
import com.example.reunion.repostory.bean.NewsBean

class NewsItemAdapter:RecyclerView.Adapter<BaseViewHolder<ItemNewsBinding>>() {

    val newsList:ArrayList<NewsBean.News> = ArrayList()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ItemNewsBinding> {
        val mBinding:ItemNewsBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.item_news,parent,false)
        mBinding.root.setOnClickListener {

        }
        return BaseViewHolder(mBinding)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ItemNewsBinding>, position: Int) {
        val mBinding = holder.mBinding
        mBinding.bean = newsList[position]
        mBinding.executePendingBindings()
    }
}