package com.example.reunion.view

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.customize.tab.TabLayout
import com.example.reunion.customize.tab.TabLayoutMediator
import com.example.reunion.databinding.ActivityMyTopicBinding
import com.example.reunion.databinding.DialogMyTopicBinding
import com.example.reunion.databinding.ViewRecyclerViewBinding
import com.example.reunion.repostory.local_resource.UserHelper
import com.example.reunion.view.adapter.MyTopicAdapter
import com.example.reunion.view.adapter.TopicItemAdapter
import com.example.reunion.view_model.MyTopicViewModel
import com.github.nuptboyzhb.lib.SuperSwipeRefreshLayout

class MyTopicActivity : BaseActivity() {

    private lateinit var mBinding:ActivityMyTopicBinding
    private lateinit var mDialogBinding:DialogMyTopicBinding
    val textView: TextView by lazy { LayoutInflater.from(this).inflate(R.layout.text_view,null) as TextView }

    private val mViewModel by lazy { setViewModel(this,MyTopicViewModel::class.java) }

    private val dialog by lazy {
        initDialog()
    }

    private val peopleFragment = MyTopicRecyFragment("people")
    private val bodyFragment = MyTopicRecyFragment("body")

    override fun create(savedInstanceState: Bundle?) {
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_my_topic)
        mBinding.viewModel = mViewModel
        mBinding.activity = this
        mBinding.lifecycleOwner = this

        mDialogBinding = DataBindingUtil.inflate(layoutInflater,R.layout.dialog_my_topic,null,false)
        mDialogBinding.activity = this
        mDialogBinding.viewModel = mViewModel
        mDialogBinding.lifecycleOwner = this

        initView()
        initData()
    }

    private fun initData(){
        val uid:String? = intent.getStringExtra("uid")
        if (uid !=null && uid.isNotEmpty()){
            mViewModel.initUserMessage(uid)
        }else if (UserHelper.isLogin()){
            UserHelper.getUser()?.apply {
                mViewModel.user = UserHelper.getUser()
                mViewModel.uid.value = uId
                mViewModel.nickName.value = uName
                mViewModel.header.value = uHeadPortrait
                mViewModel.signature.value = uSignature
                mViewModel.initFollowState()
                mViewModel.loadPeopleData()
                mViewModel.loadBodyData()
            }
        }else{
            toast("Need login your user number")
            finish()
        }
    }

    private fun initView(){

        val adapter = MyTopicAdapter(this,arrayListOf(
            peopleFragment,
            bodyFragment
        ))
        mBinding.mViewpager.adapter = adapter

        TabLayoutMediator(mBinding.homeTabLayout,mBinding.mViewpager){ tab, position->
            tab.text = when(position){
                0 -> resources.getString(R.string.my_topic_people)
                else -> resources.getString(R.string.my_topic_body)
            }
        }.attach()

        mBinding.homeTabLayout.getTabAt(0)?.customView = textView.apply {
            text = mBinding.homeTabLayout.getTabAt(0)?.text
        }
        mBinding.homeTabLayout.addOnTabSelectedListener(object :TabLayout.BaseOnTabSelectedListener<TabLayout.Tab>{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.customView = textView.apply {
                    text = tab?.text
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.customView = null
            }
            override fun onTabReselected(var1: TabLayout.Tab?) {}
        })
    }

    private fun initDialog(): Dialog {
        val dialog =  Dialog(this,R.style.CustomizeDialog)
        dialog.setContentView(mDialogBinding.root)

        val window = dialog.window
        val layoutParams = window?.attributes
        layoutParams?.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams?.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams?.gravity = Gravity.BOTTOM
        window?.setWindowAnimations(R.style.DialogAnim)
        dialog.show()
        dialog.dismiss()
        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }


    fun onBack(view:View){
        onBackPressed()
    }

    fun onClickShowDialog(view:View){
        dialog.show()
    }

    fun onClickFollow(view:View){
        dialog.dismiss()
        mViewModel.onFollow()
    }

    fun onClickSendMessage(view:View){
        startActivity(Intent(this,ImActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra("user",mViewModel.user)
        })
        finish()
        dialog.dismiss()
    }

    fun onClickMoreMessage(view:View){
        dialog.dismiss()
        startActivity(Intent(this,MoreMessageActivity::class.java).apply {
            putExtra("userBean",mViewModel.user)
        })
    }

    fun onClickCancelDialog(view:View){
        dialog.dismiss()
    }

}
