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
import com.example.reunion.databinding.ItemCommunityBinding
import com.example.reunion.databinding.ItemNewsBinding
import com.example.reunion.databinding.ItemSearchUserBinding
import com.example.reunion.repostory.bean.CommunityBean
import com.example.reunion.repostory.bean.NewsBean
import com.example.reunion.repostory.bean.User
import com.example.reunion.view.NewsActivity

class CommunityItemAdapter:RecyclerView.Adapter<BaseViewHolder<ItemCommunityBinding>>() {

    val datas:ArrayList<CommunityBean> = ArrayList()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ItemCommunityBinding> {
        val mBinding:ItemCommunityBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.item_community,parent,false)
        return BaseViewHolder(mBinding)
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ItemCommunityBinding>, position: Int) {
        val mBinding = holder.mBinding
        mBinding.data = datas[position]
        mBinding.executePendingBindings()
    }

    init {
        val names = arrayOf("A","CCC","DDD","EEE","GGG","SSS","OK","帽子","太远","多少","明明","草拟","妈的","日单","伙计","屁股","123气死")

        for (mName in names){
            val data = CommunityBean().apply {
                nickName = mName
                content = "啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊，嗷嗷嗷嗷嗷嗷嗷嗷哦嗷嗷嗷嗷哦啊，恶龙咆哮～"
                header = "http://img1.imgtn.bdimg.com/it/u=1938295758,4042523261&fm=26&gp=0.jpg"
                images = arrayListOf("https://dss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=2618535490,1016593534&fm=26&gp=0.jpg")
                comments = arrayListOf(
                    CommunityBean.Comment().apply {
                        nickName = mName
                        comment = "哈哈哈哈哈哈哈哈www"
                    },
                    CommunityBean.Comment().apply {
                        nickName = mName
                        toName = "小强"
                        comment = "哈哈哈哈哈哈哈哈www"
                    },
                    CommunityBean.Comment().apply {
                        nickName = mName
                        comment = "哈哈哈哈哈哈哈哈www"
                    }
                )
            }
            datas.add(data)
        }
        notifyDataSetChanged()
    }
}