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
import com.example.reunion.databinding.ItemFaceBinding
import com.example.reunion.databinding.ItemNewsBinding
import com.example.reunion.repostory.bean.FaceBean
import com.example.reunion.repostory.bean.NewsBean
import com.example.reunion.view.NewsActivity

class FaceItemAdapter:RecyclerView.Adapter<BaseViewHolder<ItemFaceBinding>>() {

    val list:ArrayList<FaceBean> = ArrayList()
    var deleteSize = 0
    var listener:((Int)->Unit)? = null

    private var isShowDelete = false

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ItemFaceBinding> {
        val mBinding:ItemFaceBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.item_face,parent,false)
        return BaseViewHolder(mBinding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ItemFaceBinding>, position: Int) {
        val mBinding = holder.mBinding
        mBinding.isShow = isShowDelete
        mBinding.root.setOnClickListener {
            list[position].flagDelete = !list[position].flagDelete
            if (list[position].flagDelete)
                deleteSize += 1
            else
                deleteSize -= 1
            listener?.invoke(deleteSize)
            notifyItemChanged(position)
        }
        mBinding.data = list[position]
        mBinding.executePendingBindings()
    }

    fun setShowDelete(show:Boolean){
        isShowDelete = show
        if (show){
            //什么都不做
        }else{
            deleteSize = 0
            for (item in list){
                item.flagDelete = false
            }
        }
        listener?.invoke(deleteSize)
        notifyDataSetChanged()
    }

    fun getDeleteItem():ArrayList<FaceBean>{
        val deletes = ArrayList<FaceBean>()
        for (item in deletes){
            if (item.flagDelete)
                deletes.add(item)
        }
        return deletes
    }

    fun remoteDeleteItem(){
        for (index in list.size-1 downTo 0){
            if (list[index].flagDelete){
                list.remove(list[index])
                deleteSize -= 1
            }
        }
        listener?.invoke(deleteSize)
        notifyDataSetChanged()
    }

    fun onAllSelect(all:Boolean){
        for (item in list){
            item.flagDelete = all
        }
        if (all)
            listener?.invoke(list.size)
        else
            listener?.invoke(0)
        notifyDataSetChanged()
    }

    init {
        for (index in 1 .. 5){
            val bean = FaceBean()
            bean.url = "http://img1.imgtn.bdimg.com/it/u=178224941,13016582&fm=26&gp=0.jpg"
            list.add(bean)
            notifyDataSetChanged()
        }
    }
}