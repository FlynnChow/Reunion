package com.example.reunion.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.base.BaseFragment
import com.example.reunion.databinding.FragmentLoginPhoneBinding
import com.example.reunion.view_model.LoginViewModel

class LoginPhoneFragment:BaseFragment() {
    private lateinit var mBinding: FragmentLoginPhoneBinding
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
        mBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_login_phone,container,false)
        mBinding.lifecycleOwner = this
        return mBinding.root
    }
}