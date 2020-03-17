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
import com.example.reunion.databinding.ItemSearchUserBinding
import com.example.reunion.repostory.bean.NewsBean
import com.example.reunion.repostory.bean.User
import com.example.reunion.view.NewsActivity

class SearchUserAdapter(private val listener:(String)->Unit):RecyclerView.Adapter<BaseViewHolder<ItemSearchUserBinding>>() {

    val users:ArrayList<User.Data> = ArrayList()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ItemSearchUserBinding> {
        val mBinding:ItemSearchUserBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.item_search_user,parent,false)
        return BaseViewHolder(mBinding)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ItemSearchUserBinding>, position: Int) {
        val mBinding = holder.mBinding
        mBinding.user = users[position]
        mBinding.root.setOnClickListener {
            listener.invoke(users[position].uId)
        }
        mBinding.executePendingBindings()
    }
}