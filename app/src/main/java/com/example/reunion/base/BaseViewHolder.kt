package com.example.reunion.base

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

class BaseViewHolder<T:ViewDataBinding>:RecyclerView.ViewHolder {
    lateinit var mBinding:T
    var type = -1
    constructor(binding:T):super(binding.root){
        mBinding = binding
    }

    constructor(binding:T,type:Int):super(binding.root){
        mBinding = binding
        this.type = type
    }
}