package com.example.reunion.view.adapter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.reunion.R
import com.example.reunion.base.BaseViewHolder
import com.example.reunion.databinding.ItemFindOneBinding
import com.example.reunion.repostory.bean.FaceBean

class FindOneAdapter:RecyclerView.Adapter<BaseViewHolder<ItemFindOneBinding>>() {

    var listener:((String)->Unit)? = null
    val list:ArrayList<FaceBean> = ArrayList()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ItemFindOneBinding> {
        val mBinding:ItemFindOneBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.item_find_one,parent,false)
        return BaseViewHolder(mBinding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ItemFindOneBinding>, position: Int) {
        val mBinding = holder.mBinding
        mBinding.bean = list[position]
        mBinding.root.setOnClickListener {
            listener?.invoke(list[position].uid.toString())
        }
        mBinding.executePendingBindings()
    }


}