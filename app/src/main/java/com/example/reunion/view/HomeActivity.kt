package com.example.reunion.view

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.databinding.ActivityHomeBinding
import com.example.reunion.repostory.local_resource.UserHelper
import com.example.reunion.view_model.HomeViewModel
import com.lljjcoder.style.citylist.utils.CityListLoader
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
        mBinding.activity = this
        mBinding.viewModel = setViewModel(this,HomeViewModel::class.java)
        mBinding.viewModel!!.updateUser()
        mBinding.viewModel!!.checkLogin()//登录检查
        initBottomNavigation()//初始化fragment

        insertView.post {
            insertView.visibility = View.GONE
        }
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

    fun onCloseSendWindow(view:View?){
        if (mBinding.viewModel!!.isShowSendWondow){
            mBinding.viewModel!!.isShowSendWondow = false
            changeSendVisible()
        }
    }

    fun onResetSendWindow(){
        if (mBinding.viewModel!!.isShowSendWondow){
            mBinding.viewModel!!.isShowSendWondow = false
            insertView.visibility = View.GONE
            showSend.setImageResource(R.drawable.fabu_4)
            sendLine.setBackgroundResource(R.drawable.menu_line)
            val animation0 = ObjectAnimator.ofFloat(showSend,"rotation",45f,0f)
            animation0.duration = 100
            animation0.start()
        }
    }

    fun onClickSend(view: View?){
        mBinding.viewModel!!.isShowSendWondow = !mBinding.viewModel!!.isShowSendWondow
        changeSendVisible()
    }

    private fun changeSendVisible(){
        if (mBinding.viewModel!!.isShowSendWondow){
            insertView.visibility = View.VISIBLE
            showSend.setImageResource(R.drawable.fabu_red)
            sendLine.setBackgroundResource(R.drawable.menu_line_checked)
            val set = AnimatorSet()
            val animation0 = ObjectAnimator.ofFloat(showSend,"rotation",0f,150f)
            animation0.duration = 200
            animation0.interpolator = AccelerateInterpolator()

            val animation1 = ObjectAnimator.ofFloat(showSend,"rotation",150f,135f)
            animation1.duration = 100
            animation1.interpolator = DecelerateInterpolator()

            val animation2 = ObjectAnimator.ofFloat(findFace,"translationY",(insertView.bottom - findFace.top).toFloat(),-findFace.height/8f,0f)
            animation2.duration = 300
            animation2.interpolator = AccelerateDecelerateInterpolator()

            val animation3 = ObjectAnimator.ofFloat(sendHelp,"translationY",(insertView.bottom - sendHelp.top).toFloat(),-sendHelp.height/7f,0f)
            animation3.duration = 350
            animation3.interpolator = AccelerateDecelerateInterpolator()

            val animation4 = ObjectAnimator.ofFloat(sendFindBd,"translationY",(insertView.bottom - sendFindBd.top).toFloat(),-sendHelp.height/6f,0f)
            animation4.duration = 400
            animation4.interpolator = AccelerateDecelerateInterpolator()

            set.play(animation0).with(animation2).with(animation3).with(animation4).before(animation1)
            set.start()
        }else{
            showSend.setImageResource(R.drawable.fabu_4)
            sendLine.setBackgroundResource(R.drawable.menu_line)
            val set = AnimatorSet()
            val animation0 = ObjectAnimator.ofFloat(showSend,"rotation",135f,-20f)
            animation0.duration = 200
            animation0.interpolator = AccelerateDecelerateInterpolator()

            val animation1 = ObjectAnimator.ofFloat(showSend,"rotation",-20f,0f)
            animation1.duration = 100
            animation1.interpolator = DecelerateInterpolator()

            val animation2 = ObjectAnimator.ofFloat(findFace,"translationY",0f,(insertView.bottom - findFace.top).toFloat())
            animation2.duration = 300
            animation2.interpolator = AccelerateDecelerateInterpolator()

            val animation3 = ObjectAnimator.ofFloat(sendHelp,"translationY",0f,(insertView.bottom - sendHelp.top).toFloat())
            animation3.duration = 300
            animation3.interpolator = AccelerateDecelerateInterpolator()

            val animation4 = ObjectAnimator.ofFloat(sendFindBd,"translationY",0f,(insertView.bottom - sendFindBd.top).toFloat())
            animation4.duration = 300
            animation4.interpolator = AccelerateDecelerateInterpolator()

            set.play(animation0).with(animation2).with(animation3).with(animation4).before(animation1)
            set.addListener(object : Animator.AnimatorListener{
                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {}
                override fun onAnimationEnd(animation: Animator?) {
                    insertView.visibility = View.GONE
                }
            })
            set.start()
        }
    }

    fun startSettingActivity(view: View?){
        startActivityForResult(Intent(this,SettingActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP) },SETTING_REQUEST)
        onResetSendWindow()
    }

    fun startCameraActivity(view: View?){
        startActivity(Intent(this,CameraActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP) })
        onResetSendWindow()
    }

    fun startFaceManagerActivity(view: View?){
        startActivity(Intent(this,FaceManager::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP) })
        onResetSendWindow()
    }
}
