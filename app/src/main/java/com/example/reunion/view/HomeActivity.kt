package com.example.reunion.view

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.databinding.ActivityHomeBinding
import com.example.reunion.view_model.HomeViewModel

class HomeActivity : BaseActivity() {
    private lateinit var mBinding:ActivityHomeBinding
    override fun create(savedInstanceState: Bundle?) {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        mBinding.viewModel = setViewModel(this,HomeViewModel::class.java)
        //mBinding.viewModel!!.checkLogin()
    }
}
