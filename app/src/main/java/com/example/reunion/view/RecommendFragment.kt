package com.example.reunion.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.reunion.R
import com.example.reunion.base.BaseFragment
import com.example.reunion.databinding.FragmentRecommendBinding
import com.example.reunion.view_model.NewsViewModel

class RecommendFragment:BaseFragment() {
    private lateinit var mBinding:FragmentRecommendBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_recommend,container,false)
        mBinding.lifecycleOwner = this
        return mBinding.root
    }

}