package com.example.reunion.view

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.base.BaseFragment
import com.example.reunion.customize.tab.TabLayout
import com.example.reunion.customize.tab.TabLayoutMediator
import com.example.reunion.databinding.FragmentHomeBinding
import com.example.reunion.view.adapter.HomeAdapter
import com.example.reunion.view_model.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment:BaseFragment() {
    private lateinit var mBinding:FragmentHomeBinding

    val textView:TextView by lazy { LayoutInflater.from(context).inflate(R.layout.text_view,null) as TextView }

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
        mBinding.activity = activity as HomeActivity
        initFragments()
        homeViewpager.setCurrentItem(mViewModel.currentIndex,false)
    }

    private fun initFragments(){
        val adapter = HomeAdapter(this,mViewModel.fragmentList)
        homeViewpager.adapter = adapter
        TabLayoutMediator(homeTabLayout,homeViewpager){ tab, position->
            val id = adapter.ids!![position]
            tab.text = when(id){
                0L -> adapter.titleList[0]
                1L -> adapter.titleList[1]
                2L -> adapter.titleList[2]
                3L -> adapter.titleList[3]
                4L -> adapter.titleList[4]
                else -> adapter.titleList[5]
            }
        }.attach()


        homeTabLayout.getTabAt(1)?.customView = textView.apply { text = homeTabLayout.getTabAt(1)?.text }
        homeTabLayout.addOnTabSelectedListener(object :TabLayout.BaseOnTabSelectedListener<TabLayout.Tab>{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.customView = textView.apply { text = tab?.text }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.customView = null
            }
            override fun onTabReselected(var1: TabLayout.Tab?) {}
        })

        mViewModel.isHomeUpdate.observe(this, Observer {
            homeViewpager.invalidateItemDecorations()
            adapter.ids = mViewModel.ids
            adapter.notifyDataSetChanged()
        })

        if (homeViewpager.currentItem != mViewModel.currentIndex){
            homeViewpager.currentItem = mViewModel.currentIndex
        }

        mViewModel.updateFragment()
    }

    override fun onDestroyView() {
        mViewModel.currentIndex = homeViewpager.currentItem
        super.onDestroyView()
    }
}