package com.example.reunion.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.reunion.R
import com.example.reunion.base.BaseFragment
import com.example.reunion.databinding.ViewLoginStartBinding
import com.example.reunion.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.view_login_start.*

class LoginStartFragment: BaseFragment() {
    private lateinit var mBinding:ViewLoginStartBinding
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mBinding.viewModel = setViewModel(activity!!,LoginViewModel::class.java)
        mBinding.activity = activity as LoginActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater,R.layout.view_login_start,container,false)
        mBinding.lifecycleOwner = this
        return mBinding.root
    }
}