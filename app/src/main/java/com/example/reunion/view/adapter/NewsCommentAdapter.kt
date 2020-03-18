package com.example.reunion.view.adapter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.marginTop
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.reunion.MyApplication
import com.example.reunion.R
import com.example.reunion.base.BaseViewHolder
import com.example.reunion.databinding.*
import com.example.reunion.repostory.bean.CommentBean
import com.example.reunion.repostory.bean.NewsBean
import com.example.reunion.view.NewsActivity

class NewsCommentAdapter(private val listener:(Int,CommentBean.Comment?)->Unit):RecyclerView.Adapter<BaseViewHolder<ViewDataBinding>>() {
    companion object{
        const val LOAD_COMMENT = 0
        const val LOAD_REPLY = 1
        private const val TYPE_COMMENT = 1
        private const val TYPE_COMMENT_MARGIN = 2
        private const val TYPE_LOADING = 0
    }

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
            TYPE_COMMENT_MARGIN->{
                val mBinding:ItemNewsCommentMarginBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                    R.layout.item_news_comment_margin,parent,false)
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
        return if (comments.size == position){
            TYPE_LOADING
        }else if(comments.size == 1||position==0){
            TYPE_COMMENT_MARGIN
        }else{
            TYPE_COMMENT
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ViewDataBinding>, position: Int) {
        when(holder.mBinding){
            is ItemNewsCommentBinding ->{
                val mBinding = holder.mBinding as ItemNewsCommentBinding
                mBinding.bean = comments[position]
                mBinding.root.setOnClickListener {
                    listener.invoke(LOAD_REPLY,comments[position])
                }
                when (position) {
                    comments.size -1 -> {
                        mBinding.itemView.setBackgroundResource(R.drawable.ripple_rect_comment_bottom)
                    }
                    else -> {
                        mBinding.itemView.setBackgroundResource(R.drawable.ripple_rect_comment)
                    }
                }

                mBinding.executePendingBindings()
            }
            is ItemNewsCommentMarginBinding->{
                val mBinding = holder.mBinding as ItemNewsCommentMarginBinding
                mBinding.bean = comments[position]
                mBinding.root.setOnClickListener {
                    listener.invoke(LOAD_REPLY,comments[position])
                }
                when {
                    comments.size == 1 -> {
                        mBinding.itemView.setBackgroundResource(R.drawable.ripple_rect_comment_round)
                    }
                    position == 0 -> {
                        mBinding.itemView.setBackgroundResource(R.drawable.ripple_rect_comment_top)
                    }
                }

                mBinding.executePendingBindings()
            }
            is ItemCommentMoreBinding ->{
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
}