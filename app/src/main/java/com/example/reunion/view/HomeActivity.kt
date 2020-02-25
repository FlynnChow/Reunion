package com.example.reunion.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.databinding.ActivityHomeBinding
import com.example.reunion.repostory.local_resource.UserHelper
import com.example.reunion.view_model.HomeViewModel
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity() {
    val LOGIN_USER = 100
    val SETTING_REQUEST = 101
    private lateinit var mBinding:ActivityHomeBinding
    private lateinit var fragments:Array<Fragment>
    private var lastFragmentIndex = 0
    override fun create(savedInstanceState: Bundle?) {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        mBinding.lifecycleOwner = this
        mBinding.viewModel = setViewModel(this,HomeViewModel::class.java)
        mBinding.viewModel!!.updateUser()
        mBinding.viewModel!!.checkLogin()//登录检查
        initBottomNavigation()//初始化fragment
        getSystemService(Context.INPUT_METHOD_SERVICE)
    }

    private fun initBottomNavigation(){
        val homeFragment = HomeFragment()
        val communityFragment = CommunityFragment()
        val messageFragment = MessageFragment()
        val mineFragment = MineFragment()
        fragments = arrayOf(homeFragment,communityFragment,messageFragment, mineFragment)
        supportFragmentManager.beginTransaction().replace(R.id.replaceView,homeFragment).show(homeFragment).commit()
        navigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.menuHome-> switchFragment(0)
                R.id.menuCommunity-> switchFragment(1)
                R.id.menuMessage-> switchFragment(2)
                R.id.menuMine-> switchFragment(3)
                else-> false
            }
        }
    }

    private fun switchFragment(newFragmentIndex:Int):Boolean{
        if (lastFragmentIndex != newFragmentIndex) {
            val transient = supportFragmentManager.beginTransaction()
            transient.hide(fragments[lastFragmentIndex])
            if (!fragments[newFragmentIndex].isAdded)
                transient.add(R.id.replaceView,fragments[newFragmentIndex])
            transient.show(fragments[newFragmentIndex]).commitAllowingStateLoss()
            lastFragmentIndex = newFragmentIndex
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK){
            when(requestCode){
                LOGIN_USER->{
                    mBinding.viewModel!!.updateUser()
                }
                SETTING_REQUEST->{
                    mBinding.viewModel!!.updateFragment()
                    mBinding.viewModel!!.updateUser()
                }
            }
        }

    }

    fun onClickUser(view:View? = null){
        if (UserHelper.isLogin()){
            val intent = Intent(this,SettingActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivityForResult(intent,SETTING_REQUEST)
        }else{
            val intent = Intent(this,LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivityForResult(intent,LOGIN_USER)
        }
    }

    fun startSettingActivity(view: View?){
        startActivityForResult(Intent(this,SettingActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP) },SETTING_REQUEST)
    }
}
