package com.example.reunion.view

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.databinding.ActivityLoginBinding
import com.example.reunion.repostory.local_resource.UserHelper
import com.example.reunion.util.NormalUtil
import com.example.reunion.util.PictureEngine
import com.example.reunion.view_model.LoginViewModel
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.view_user_protocol.*

class LoginActivity : BaseActivity() {
    private lateinit var mBinding:ActivityLoginBinding

    private lateinit var startFragment:LoginStartFragment
    private lateinit var phoneFragment: LoginPhoneFragment
    override fun create(savedInstanceState: Bundle?) {
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_login)
        mBinding.viewModel = setViewModel(this,LoginViewModel::class.java)
        mBinding.activity = this
        mBinding.lifecycleOwner = this
        initFragment()
    }

    private fun initFragment(){
        startFragment = LoginStartFragment()
        phoneFragment = LoginPhoneFragment()
        showStartFragment()
    }

    fun showStartFragment(view: View? = null){
        supportFragmentManager.beginTransaction().replace(R.id.loginFragment,startFragment).commit()
        mBinding.viewModel!!.currentPage.value = 0
    }

    fun showPhoneFragment(view: View? = null){
        supportFragmentManager.beginTransaction().replace(R.id.loginFragment,phoneFragment).commit()
        mBinding.viewModel!!.currentPage.value = 2
    }

    fun onBackClick(view: View? = null){
        val pageValue = mBinding.viewModel!!.currentPage.value
        when(pageValue){
            0->{
                finish()
            }
            1->{
                mBinding.viewModel!!.showStartView()
                mBinding.viewModel!!.mobileNumber.value = ""
            }
            2->{
               showStartFragment()
                mBinding.viewModel!!.showPhoneView()
            }
        }
    }

    fun onGetVerificationCode(view:View){
        if (NormalUtil.isMobile(mBinding.viewModel!!.mobileNumber.value)){
            showPhoneFragment()
        }else{
            toast("请输入正确的手机号码")
        }
    }

    fun onPhoneLoginClick(view: View? = null){
        if (mBinding.viewModel!!.vCode.value == null|| mBinding.viewModel!!.vCode.value!!.isEmpty()){
            toast("请输入验证码")
        }else{
            mBinding.viewModel!!.onPhoneLogin()
        }
    }

    override fun onBackPressed() {
        onBackClick()
    }

    fun onCheckBoxClick(view: View){
        loginAgree.performClick()
    }

    fun getAuthorizeButtonColor(checked:Boolean)=
        if (checked)
            resources.getColor(R.color.authorizeLoginOK)
        else
            resources.getColor(R.color.authorizeLoginNo)

    fun getRetryColor(checked:Boolean)=
        if (checked)
            resources.getColor(R.color.retryOK)
        else
            resources.getColor(R.color.retryNo)

    fun getPhoneButtonResource(checked:Boolean)=
        (if (checked)
            resources.getDrawable(R.drawable.login_phone_button_ok)
        else
            resources.getDrawable(R.drawable.login_phone_button_no))!!

    fun getLoginButtonResource(checked:Boolean,isRetry:Boolean)=
        (if (checked&&!isRetry)
            resources.getDrawable(R.drawable.login_phone_button_ok)
        else
            resources.getDrawable(R.drawable.login_phone_button_no))!!

    fun getVCodeButtonResource(checked:Boolean)=
        (if (checked)
            resources.getDrawable(R.drawable.login_phone_button_ok)
        else
            resources.getDrawable(R.drawable.login_phone_button_no))!!

    fun getAuthorizeButtonResource(checked:Boolean)=
        (if (checked)
            resources.getDrawable(R.drawable.login_authorize_button_ok)
        else
            resources.getDrawable(R.drawable.login_authorize_button_no))!!
}
