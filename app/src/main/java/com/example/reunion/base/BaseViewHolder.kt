package com.example.reunion.base

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

class BaseViewHolder<T:ViewDataBinding>:RecyclerView.ViewHolder {
    lateinit var mBinding:T
    constructor(binding:T):super(binding.root){
        mBinding = binding
    }
}