package com.example.reunion.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.base.BaseFragment
import com.example.reunion.databinding.ViewLoginPhoneBinding
import com.example.reunion.viewmodel.LoginViewModel

class LoginPhoneFragment:BaseFragment() {
    private lateinit var mBinding: ViewLoginPhoneBinding
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mBinding.viewModel = setViewModel(activity as BaseActivity,LoginViewModel::class.java)
        mBinding.activity = activity as LoginActivity
        mBinding.viewModel!!.onGetVCode()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater,R.layout.view_login_phone,container,false)
        mBinding.lifecycleOwner = this
        return mBinding.root
    }
}