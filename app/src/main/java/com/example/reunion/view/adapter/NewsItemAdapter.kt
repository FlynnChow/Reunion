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
import com.example.reunion.databinding.ItemNewsBinding
import com.example.reunion.repostory.bean.NewsBean
import com.example.reunion.view.NewsActivity

class NewsItemAdapter:RecyclerView.Adapter<BaseViewHolder<ItemNewsBinding>>() {

    val newsList:ArrayList<NewsBean.News> = ArrayList()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ItemNewsBinding> {
        val mBinding:ItemNewsBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.item_news,parent,false)
        return BaseViewHolder(mBinding)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ItemNewsBinding>, position: Int) {
        val mBinding = holder.mBinding
        mBinding.bean = newsList[position]
        mBinding.root.setOnClickListener {
            if (newsList[position] != null){
                val intent = Intent(mBinding.root.context,NewsActivity::class.java)
                val bundle = Bundle()
                bundle.putParcelable("news",newsList[position])
                intent.putExtras(bundle)
                mBinding.root.context.startActivity(intent)
            }
        }
        mBinding.executePendingBindings()
    }
}