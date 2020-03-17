package com.example.reunion.view.adapter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.reunion.R
import com.example.reunion.base.BaseViewHolder
import com.example.reunion.databinding.*
import com.example.reunion.repostory.bean.CommunityBean
import com.example.reunion.repostory.bean.NewsBean
import com.example.reunion.repostory.bean.TopicBean
import com.example.reunion.view.NewsActivity

class CommunityCommentAdapter(private val listener:(Int,CommunityBean.Comment?)->Unit):RecyclerView.Adapter<BaseViewHolder<ViewDataBinding>>() {
    companion object{
        const val LOAD_COMMENT = 0
        const val SHOW_EDIT = 1
        private const val TYPE_COMMENT_TOP = 1
        private const val TYPE_COMMENT = 2
        private const val TYPE_COMMENT_BOTTOM = 3
        private const val TYPE_LOADING = 0
    }

    val comments = ArrayList<CommunityBean.Comment>()
    var type:Int = 0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ViewDataBinding> {
        return when(viewType){
            TYPE_COMMENT_TOP->{
                val mBinding:ItemCommunityCommentTopBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                    R.layout.item_community_comment_top,parent,false)
                BaseViewHolder(mBinding)
            }
            TYPE_COMMENT_BOTTOM->{
                val mBinding: ItemCommunityCommentBottomBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                    R.layout.item_community_comment_bottom,parent,false)
                BaseViewHolder(mBinding)
            }
            TYPE_COMMENT->{
                val mBinding:ItemCommunityCommentBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                    R.layout.item_community_comment,parent,false)
                BaseViewHolder(mBinding)
            }
            else->{
                val mBinding:ItemCommentMoreBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                    R.layout.item_comment_more,parent,false)
                BaseViewHolder(mBinding)
            }
        }
    }

    override fun getItemCount(): Int {
        return comments.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return when(position){
            comments.size -> TYPE_LOADING
            0 -> TYPE_COMMENT_TOP
            comments.size - 1 -> TYPE_COMMENT_BOTTOM
            else -> TYPE_COMMENT
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ViewDataBinding>, position: Int) {
        when(holder.mBinding){
            is ItemCommunityCommentTopBinding ->{
                val mBinding = holder.mBinding as ItemCommunityCommentTopBinding
                mBinding.bean = comments[position]
                mBinding.root.setOnClickListener {
                    listener.invoke(SHOW_EDIT,comments[position])
                }
                mBinding.executePendingBindings()
            }
            is ItemCommunityCommentBottomBinding ->{
                val mBinding = holder.mBinding as ItemCommunityCommentBottomBinding
                mBinding.bean = comments[position]
                mBinding.root.setOnClickListener {
                    listener.invoke(SHOW_EDIT,comments[position])
                }
                mBinding.executePendingBindings()
            }
            is ItemCommunityCommentBinding ->{
                val mBinding = holder.mBinding as ItemCommunityCommentBinding
                mBinding.bean = comments[position]
                mBinding.root.setOnClickListener {
                    listener.invoke(SHOW_EDIT,comments[position])
                }
                mBinding.executePendingBindings()
            }
            is ItemCommentMoreBinding ->{
                val mBinding = holder.mBinding as ItemCommentMoreBinding
                mBinding.isType = type
                if (type == 0||type == 2){
                    val view: View = if (type == 0) mBinding.root.findViewById(R.id.newsMoreLoad)
                    else mBinding.root.findViewById(R.id.newsNoMore)
                    view.setOnClickListener {
                        listener.invoke(LOAD_COMMENT,null)
                    }
                }
                mBinding.executePendingBindings()
            }
        }
    }
}