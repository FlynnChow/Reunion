package com.example.reunion.view.adapter

import android.content.Intent
import android.net.Uri
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
import com.example.reunion.databinding.ItemTopSendBinding
import com.example.reunion.repostory.bean.NewsBean
import com.example.reunion.view.NewsActivity

class SendTopicAdapter:RecyclerView.Adapter<BaseViewHolder<ItemTopSendBinding>>() {

    var uris:ArrayList<Uri> = ArrayList()

    var openPhotoListener:(()->Unit)? = null

    var maxPicture = 9


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ItemTopSendBinding> {
        val mBinding:ItemTopSendBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.item_top_send,parent,false)
        return BaseViewHolder(mBinding)
    }

    override fun getItemCount(): Int {
        if (uris.size <maxPicture)
            return uris.size + 1
        else
            return uris.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ItemTopSendBinding>, position: Int) {
        val mBinding = holder.mBinding
        if (position == uris.size){
            mBinding.isShow = true
            mBinding.topicSendImg.setImageResource(R.drawable.ripple_insert_picture)
            mBinding.root.setOnClickListener {
                openPhotoListener?.invoke()
            }
        }else{
            mBinding.root.setOnClickListener {  }
            mBinding.uri = uris[position]
            mBinding.isShow = false
        }
        mBinding.executePendingBindings()
    }
}