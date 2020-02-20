package com.example.reunion.view.setting_view

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.base.BaseFragment
import com.example.reunion.databinding.FragmentNewsBinding
import com.example.reunion.databinding.FragmentSettingAccountBinding
import com.example.reunion.databinding.FragmentSettingHomeManagerBinding
import com.example.reunion.view.SettingActivity
import com.example.reunion.view.adapter.NewsAdapter
import com.example.reunion.view_model.HomeViewModel
import com.example.reunion.view_model.NewsViewModel
import com.example.reunion.view_model.SettingViewModel
import kotlinx.android.synthetic.main.fragment_news.*

class HomeManagerStFragment:BaseFragment() {
    private lateinit var mBinding:FragmentSettingHomeManagerBinding
    private val mViewModel: SettingViewModel by lazy {
        setViewModel(activity as BaseActivity, SettingViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting_home_manager,container,false)
        mBinding.lifecycleOwner = this
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mBinding.activity = activity as SettingActivity
        mBinding.viewModel = mViewModel
    }
}