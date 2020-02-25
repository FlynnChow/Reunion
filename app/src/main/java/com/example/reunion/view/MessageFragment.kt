package com.example.reunion.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.reunion.R
import com.example.reunion.base.BaseFragment
import com.example.reunion.databinding.FragmentMessageBinding

class MessageFragment:BaseFragment() {
    private lateinit var mBinding:FragmentMessageBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_message,container,false)
        mBinding.lifecycleOwner = this
        return mBinding.root
    }

}