package com.example.reunion.view

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
import com.example.reunion.databinding.FragmentHomeBinding
import com.example.reunion.view.adapter.HomeAdapter
import com.example.reunion.view_model.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment:BaseFragment() {
    private lateinit var mBinding:FragmentHomeBinding
    private val mViewModel: HomeViewModel by lazy {
        setViewModel(activity as BaseActivity, HomeViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_home,container,false)
        mBinding.lifecycleOwner = this
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mBinding.viewModel = mViewModel
        initFragments()
        homeViewpager.setCurrentItem(mViewModel.currentIndex,false)
    }

    private fun initFragments(){
        val followFragment = FollowFragment()
        val recommendFragment = RecommendFragment()
        val nearbyFragment = NearbyFragment()
        val findFragment = FindFragment()
        val newsFragment = NewsFragment()
        val fragments:Array<Fragment> = arrayOf(followFragment,recommendFragment,nearbyFragment,findFragment,newsFragment)
        val adapter = HomeAdapter(fragmentManager!!,fragments)
        homeViewpager.adapter = adapter
        homeTabLayout.setViewPager(homeViewpager)
        homeTabLayout.setTitles(
            resources.getString(R.string.home_follow),
            resources.getString(R.string.home_recommend),
            resources.getString(R.string.home_nearby),
            resources.getString(R.string.home_find),
            resources.getString(R.string.home_news))

        if (homeViewpager.currentItem != mViewModel.currentIndex){
            homeViewpager.currentItem = mViewModel.currentIndex
        }
    }

    override fun onDestroyView() {
        mViewModel.currentIndex = homeViewpager.currentItem
        super.onDestroyView()
    }
}