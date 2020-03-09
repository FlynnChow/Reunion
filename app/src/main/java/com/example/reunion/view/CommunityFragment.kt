package com.example.reunion.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.example.reunion.R
import com.example.reunion.base.BaseFragment
import com.example.reunion.customize.tab.TabLayout
import com.example.reunion.customize.tab.TabLayoutMediator
import com.example.reunion.databinding.FragmentCommunityBinding
import com.example.reunion.view.adapter.MyTopicAdapter

class CommunityFragment:BaseFragment() {
    private lateinit var mBinding:FragmentCommunityBinding
    val textView: TextView by lazy { LayoutInflater.from(activity).inflate(R.layout.text_view,null) as TextView }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_community,container,false)
        mBinding.lifecycleOwner = this
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
    }

    private fun initView(){
        val adapter = MyTopicAdapter(activity!!,arrayListOf(
            CommunityRecyFragment("follow"),
            CommunityRecyFragment("community"),
            CommunityRecyFragment("mine")
        ))
        mBinding.mViewpager.adapter = adapter

        TabLayoutMediator(mBinding.homeTabLayout,mBinding.mViewpager){ tab, position->
            tab.text = when(position){
                0 -> resources.getString(R.string.community_follow)
                1 -> resources.getString(R.string.community_community)
                else -> resources.getString(R.string.community_mine)
            }
        }.attach()

        mBinding.homeTabLayout.getTabAt(0)?.customView = textView.apply {
            text = mBinding.homeTabLayout.getTabAt(0)?.text
        }
        mBinding.homeTabLayout.addOnTabSelectedListener(object :
            TabLayout.BaseOnTabSelectedListener<TabLayout.Tab>{
            override fun onTabSelected(tab: TabLayout.Tab?) {

                tab?.customView = textView.apply {
                    text = tab?.text
                    when(tab?.text.toString()){
                        resources.getString(R.string.community_community) ->{
                            mBinding.homeTabLayout.setSelectedTabIndicator(R.drawable.community_tab_indicator)
                        }
                        else ->{
                            mBinding.homeTabLayout.setSelectedTabIndicator(R.drawable.home_tab_indicator)
                        }
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.customView = null
            }
            override fun onTabReselected(var1: TabLayout.Tab?) {}
        })
    }
}