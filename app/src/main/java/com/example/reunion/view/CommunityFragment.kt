package com.example.reunion.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.reunion.R
import com.example.reunion.base.BaseFragment
import com.example.reunion.databinding.FragmentCommunityBinding

class CommunityFragment:BaseFragment() {
    private lateinit var mBinding:FragmentCommunityBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_community,container,false)
        mBinding.lifecycleOwner = this
        return mBinding.root
    }

}