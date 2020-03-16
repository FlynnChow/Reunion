package com.example.reunion.view.adapter

import android.content.Intent
import android.os.Bundle
import android.text.Html
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
import com.example.reunion.databinding.*
import com.example.reunion.repostory.bean.*
import com.example.reunion.view.NewsActivity

class SystemMessageAdapter(private val listener:(Int,SystemMessageBean)->Unit):RecyclerView.Adapter<BaseViewHolder<ItemSystemMessageBinding>>() {
    val list:ArrayList<SystemMessageBean> = ArrayList()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ItemSystemMessageBinding> {
        val mBinding:ItemSystemMessageBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.item_system_message,parent,false)
        return BaseViewHolder(mBinding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ItemSystemMessageBinding>, position: Int) {
        val mBinding = holder.mBinding
        mBinding.data = list[position]
        mBinding.content.text = Html.fromHtml(list[position].content)
        mBinding.root.setOnClickListener {
            if (!list[position].isRead){
                listener.invoke(0,list[position])
                list[position].isRead = true
                notifyItemChanged(position)
            }
        }
        mBinding.root.setOnLongClickListener {
            listener.invoke(1,list[position])
            true
        }
        mBinding.executePendingBindings()
    }
}