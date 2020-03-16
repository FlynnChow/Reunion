package com.example.reunion.view.adapter

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.reunion.MyApplication
import com.example.reunion.R
import com.example.reunion.base.BaseViewHolder
import com.example.reunion.databinding.ItemCommentMoreBinding
import com.example.reunion.databinding.ItemNewsBinding
import com.example.reunion.databinding.ItemNewsCommentBinding
import com.example.reunion.databinding.ViewReplyBinding
import com.example.reunion.repostory.bean.CommentBean
import com.example.reunion.repostory.bean.NewsBean
import com.example.reunion.view.NewsActivity

class NewsReplyAdapter(private val listener:(Int, CommentBean.Comment?)->Unit):RecyclerView.Adapter<BaseViewHolder<ViewDataBinding>>() {
    companion object{
        val LOAD_REPLY = 0
        val SHOW_EDIT = 1
    }
    private val TYPE_COMMENT = 0
    private val TYPE_BOTTOM = 1

    var comments:ArrayList<CommentBean.Comment>? = null
    var type:Int = 0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ViewDataBinding> {
        return when(viewType){
            TYPE_COMMENT->{
                val mBinding:ViewReplyBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                    R.layout.view_reply,parent,false)
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
        if (comments == null) return 0
        return comments!!.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < comments!!.size) TYPE_COMMENT
        else TYPE_BOTTOM
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ViewDataBinding>, position: Int) {
        if (position < comments!!.size){
            val mBinding = holder.mBinding as ViewReplyBinding
            mBinding.bean = comments!![position]
            val contentText:TextView = mBinding.root.findViewById(R.id.replyContent)
            val htmlStr = "<font color='#007AFF'>@${comments!![position].toUname}:</font>${comments!![position].rComment}"
            contentText.text = Html.fromHtml(htmlStr)
            mBinding.root.setOnClickListener {
                listener.invoke(SHOW_EDIT,comments!![position])
            }
            mBinding.executePendingBindings()
        }else{
            val mBinding = holder.mBinding as ItemCommentMoreBinding
            mBinding.isType = type
            if (type == 0||type == 2){
                val view:View = if (type == 0) mBinding.root.findViewById(R.id.newsMoreLoad)
                else mBinding.root.findViewById(R.id.newsNoMore)
                mBinding.root.setBackgroundColor(MyApplication.resource().getColor(R.color.color_white))
                view.setOnClickListener {
                    listener.invoke(LOAD_REPLY,null)
                }
            }
            mBinding.executePendingBindings()
        }
    }
}