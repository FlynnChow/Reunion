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

class SearchUserAdapter:RecyclerView.Adapter<BaseViewHolder<ItemSearchUserBinding>>() {

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
        mBinding.executePendingBindings()
    }

    init {
        val names = arrayOf("A","CCC","DDD","EEE","GGG","SSS","OK","帽子","太远","多少","明明","草拟","妈的","日单","伙计","屁股","123气死")

        val users = ArrayList<User.Data>()
        for (name in names){
            val user = User.Data().apply {
                uName = name
                uHeadPortrait = "http://img1.imgtn.bdimg.com/it/u=1938295758,4042523261&fm=26&gp=0.jpg"
            }
            users.add(user)
        }
        this.users.addAll(users)
        notifyDataSetChanged()
    }
}