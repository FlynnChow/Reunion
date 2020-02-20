package com.example.reunion.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.base.BaseFragment
import com.example.reunion.databinding.FragmentMineBinding
import com.example.reunion.view_model.HomeViewModel

class MineFragment:BaseFragment() {
    private lateinit var mBinding:FragmentMineBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_mine,container,false)
        mBinding.lifecycleOwner = this
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mBinding.viewModel = setViewModel(activity as BaseActivity,HomeViewModel::class.java)
        mBinding.activity = activity as HomeActivity
    }
}