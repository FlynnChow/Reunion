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
import com.example.reunion.databinding.ItemCommentMoreBinding
import com.example.reunion.databinding.ItemNewsBinding
import com.example.reunion.databinding.ItemNewsCommentBinding
import com.example.reunion.repostory.bean.CommentBean
import com.example.reunion.repostory.bean.NewsBean
import com.example.reunion.view.NewsActivity

class NewsCommentAdapter(private val listener:(Int,CommentBean.Comment?)->Unit):RecyclerView.Adapter<BaseViewHolder<ViewDataBinding>>() {
    companion object{
        val LOAD_COMMENT = 0
        val LOAD_REPLY = 1
    }
    private val TYPE_COMMENT = 0
    private val TYPE_BOTTOM = 1

    val comments = ArrayList<CommentBean.Comment>()
    var type:Int = 0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ViewDataBinding> {
        return when(viewType){
            TYPE_COMMENT->{
                val mBinding:ItemNewsCommentBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                    R.layout.item_news_comment,parent,false)
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
        return if (position < comments.size) TYPE_COMMENT
        else TYPE_BOTTOM
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ViewDataBinding>, position: Int) {
        if (position < comments.size){
            val mBinding = holder.mBinding as ItemNewsCommentBinding
            mBinding.bean = comments[position]
            mBinding.root.setOnClickListener {
                listener.invoke(LOAD_REPLY,comments[position])
            }
            mBinding.executePendingBindings()
        }else{
            val mBinding = holder.mBinding as ItemCommentMoreBinding
            mBinding.isType = type
            if (type == 0||type == 2){
                val view:View = if (type == 0) mBinding.root.findViewById(R.id.newsMoreLoad)
                else mBinding.root.findViewById(R.id.newsNoMore)
                view.setOnClickListener {
                    listener.invoke(LOAD_COMMENT,null)
                }
            }
            mBinding.executePendingBindings()
        }
    }
}