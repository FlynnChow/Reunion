package com.example.reunion.view

import android.content.Intent
import android.graphics.Color
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
import com.example.reunion.databinding.DialogBubbleBinding
import com.example.reunion.databinding.FragmentMessageBinding
import com.example.reunion.util.StringDealerUtil
import com.example.reunion.view.adapter.MyTopicAdapter
import com.xujiaji.happybubble.BubbleDialog
import com.xujiaji.happybubble.BubbleLayout


class MessageFragment:BaseFragment() {
    private lateinit var mBinding:FragmentMessageBinding
    private lateinit var mDialogBinding:DialogBubbleBinding

    val textView: TextView by lazy { LayoutInflater.from(activity).inflate(R.layout.text_view,null) as TextView }

    val dialog by lazy {
        initDialog()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_message,container,false)
        mBinding.lifecycleOwner = this
        mBinding.fragment = this

        mDialogBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_bubble,container,false)
        mDialogBinding.item1.setOnClickListener {
            dialog.dismiss()
            showInsertFriend()
        }
        mDialogBinding.item2.setOnClickListener {
            dialog.dismiss()
        }
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
    }

    private fun initView(){
        val adapter = MyTopicAdapter(activity!!,arrayListOf(
            SystemMessageFragment(),
            ImMessageFragment(),
            UserFragment.getInstance("friend")
        ))
        mBinding.mViewpager.adapter = adapter

        TabLayoutMediator(mBinding.homeTabLayout,mBinding.mViewpager){ tab, position->
            tab.text = when(position){
                0 -> resources.getString(R.string.message_system)
                1 -> resources.getString(R.string.message_im)
                else -> resources.getString(R.string.message_friend)
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
                        resources.getString(R.string.message_friend) ->{
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

    fun showDialog(view:View){
        dialog.show()
    }

    private fun initDialog():BubbleDialog{
        val bl = BubbleLayout(activity)
        bl.bubbleColor = activity?.resources?.getColor(R.color.bubble_color)?: Color.BLACK
        bl.shadowColor = activity?.resources?.getColor(R.color.bubble_color)?: Color.BLACK

        return BubbleDialog(activity)
            .addContentView<BubbleDialog>(mDialogBinding.root)
            .setClickedView<BubbleDialog>(mBinding.showDialog)
            .setPosition<BubbleDialog>(BubbleDialog.Position.BOTTOM)
            .calBar<BubbleDialog>(true)
            .setTransParentBackground<BubbleDialog>()
            .setBubbleLayout<BubbleDialog>(bl)
    }

    private fun showInsertFriend(){
        startActivity(Intent(activity,SearchUserActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        })
    }

}