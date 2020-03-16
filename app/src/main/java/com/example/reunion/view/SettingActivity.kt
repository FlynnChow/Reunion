package com.example.reunion.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.bigkoo.pickerview.view.OptionsPickerView
import com.bigkoo.pickerview.view.TimePickerView
import com.example.reunion.MyApplication
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.databinding.ActivitySettingBinding
import com.example.reunion.databinding.FragmentSettingAdviceBinding
import com.example.reunion.repostory.local_resource.PictureHelper
import com.example.reunion.repostory.local_resource.UserHelper
import com.example.reunion.util.NormalUtil
import com.example.reunion.util.ViewUtil
import com.example.reunion.view.setting_view.*
import com.example.reunion.view_model.SettingViewModel
import com.lljjcoder.Interface.OnCityItemClickListener
import com.lljjcoder.bean.CityBean
import com.lljjcoder.bean.DistrictBean
import com.lljjcoder.bean.ProvinceBean
import com.lljjcoder.citywheel.CityConfig
import com.lljjcoder.style.citypickerview.CityPickerView
import kotlinx.coroutines.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess


class SettingActivity : BaseActivity() {
    companion object{
        val MODE_DEFAULT = 0
        val MODE_USER = 1
    }
    private val mViewModel by lazy { setViewModel(this,SettingViewModel::class.java) }
    private lateinit var mBinding:ActivitySettingBinding
    private val account by lazy { AccountStFragment() }
    private val advice by lazy { AdviceStFragment() }
    private val help by lazy { HelpStFragment() }
    private val homeManager by lazy { HomeManagerStFragment() }
    private val message by lazy { MessageStFragment() }
    private val normal by lazy { NormalStFragment() }
    private val start by lazy { StartStFragment() }
    private val mCityPicker by lazy { CityPickerView() }
    private lateinit var mSexPicker: OptionsPickerView<*>
    private lateinit var mTimePicker: TimePickerView
    private var mode = 0

    private lateinit var logoutDialog:Dialog
    private lateinit var clearCacheDialog:Dialog
    private lateinit var clearImDialog:Dialog
    private lateinit var clearSystemDialog:Dialog



    override fun create(savedInstanceState: Bundle?) {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_setting)
        mBinding.viewModel = mViewModel

        mViewModel.initUser()
        val mode = intent.getIntExtra("launchMode",0)
        if (mode == 0)
            showStartFragment()
        else{
            this.mode = mode
            showMessageFragment()
        }

        mViewModel.onBack.observe(this, androidx.lifecycle.Observer {
            if(it)
                onBackClick()
        })
        initPicker()

        initDialog()
    }

    private fun initDialog(){
        logoutDialog = ViewUtil.createNormalDialog(this,
            "是否确定退出登录？","确定",{
                logoutDialog.dismiss()
                if (UserHelper.isLogin()){
                    UserHelper.logout()
                    mViewModel.header.value = ""
                    toast("退出账号成功")
                }else{
                    toast("已经退出登录")
                }
            },{
                logoutDialog.dismiss()
            })

        clearCacheDialog = ViewUtil.createNormalDialog(this,
            "是否清空应用的所有缓存？应用缓存已占用了 ${NormalUtil.getFormatDirSize(File(PictureHelper.getCachePath()))} 大小的空间。",
            "清空缓存",{
                clearCacheDialog.dismiss()
                NormalUtil.deleteDirOrFile(PictureHelper.getCachePath())
                toast("清空完成")
            },{
                clearCacheDialog.dismiss()
            })

        clearImDialog = ViewUtil.createNormalDialog(this,
            "是否清空本地所有的聊天记录？清空后无法再恢复。",
            "清空记录",{
                clearImDialog.dismiss()
                sendBroadcast(Intent("reunion.message.im.clear"))
                mViewModel.clearMessage()
            },{
                clearImDialog.dismiss()
            })

        clearSystemDialog = ViewUtil.createNormalDialog(this,
            "是否清空本地所有的系统消息？清空后无法再恢复。",
            "清空消息",{
                clearSystemDialog.dismiss()
                sendBroadcast(Intent("reunion.message.sys.clear"))
                mViewModel.clearSysMessage()
            },{
                clearSystemDialog.dismiss()
            })
    }


    @SuppressLint("SimpleDateFormat")
    private fun initPicker(){
        mCityPicker.init(this)
        val config = CityConfig.Builder().confirTextColor("#FF6868").build()
        mCityPicker.setConfig(config)
        mCityPicker.setOnCityItemClickListener(object :OnCityItemClickListener(){
            override fun onSelected(
                province: ProvinceBean?,
                city: CityBean?,
                district: DistrictBean?
            ) {
                mViewModel.province.value = province?.name?:""
                mViewModel.city.value = city?.name?:""
                mViewModel.district.value = district?.name?:""
            }
        })

        val item: ArrayList<ProvinceBean> = ArrayList()
        item.apply {
            add(ProvinceBean().apply { name = resources.getString(R.string.sex_0) })
            add(ProvinceBean().apply { name = resources.getString(R.string.sex_1) })
            add(ProvinceBean().apply { name = resources.getString(R.string.sex_2) })
        }
        mSexPicker = OptionsPickerBuilder(this,
            OnOptionsSelectListener { options,_,_,_->
                mViewModel.sex.value = options
            })
            .setTitleText("选择性别")
            .setSubmitColor(resources.getColor(R.color.picker_color))
            .setCancelColor(resources.getColor(R.color.picker_color))
            .build<Any>().also {
            it.setPicker(item as List<ProvinceBean>)
        }

        val date = Date()
        var dataFormat = SimpleDateFormat("yyyy")
        val year = dataFormat.format(date).toInt()
        dataFormat = SimpleDateFormat("MM")
        val month = dataFormat.format(date).toInt()
        dataFormat = SimpleDateFormat("dd")
        val day = dataFormat.format(date).toInt()
        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()
        startDate.set(1900,0,0)
        endDate.set(year,month-1,day)
        mTimePicker = TimePickerBuilder(this,
            OnTimeSelectListener { time, _ ->
                val format = SimpleDateFormat("yyyy年MM月dd日")
                mViewModel.birthday.value = format.format(time)
            })
            .setTitleText("选择生日")
            .setSubmitColor(resources.getColor(R.color.picker_color))
            .setCancelColor(resources.getColor(R.color.picker_color))
            .setDate(endDate)
            .setRangDate(startDate,endDate)
            .build()
    }

    fun onBackClick(view: View? = null){
        onBackPressed()
    }

    fun exitApplication(view:View){
        val activityManager =
            MyApplication.app.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appTaskList = activityManager.appTasks
        for (appTask in appTaskList) {
            appTask.finishAndRemoveTask()
        }
        Process.killProcess(Process.myPid())
        exitProcess(0)
    }

    override fun onBackPressed() {
        when(mode){
            MODE_DEFAULT -> {
                when(mViewModel.currentPage){
                    SettingViewModel.PAGE_START -> {
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                    SettingViewModel.PAGE_ADVICE -> showHelpFragment()
                    SettingViewModel.PAGE_HOME_MANAGER ->{
                        mViewModel.saveHomePage()
                        showStartFragment()
                    }
                    SettingViewModel.PAGE_MESSAGE->{
                        mViewModel.saveUser()
                        showStartFragment()
                    }
                    else -> showStartFragment()
                }
            }
            MODE_USER ->{
                setResult(Activity.RESULT_OK)
                mViewModel.saveHomePage()
                finish()
            }
        }
    }

    private fun showStartFragment() = runBlocking{
        delay(100)
        supportFragmentManager.beginTransaction().replace(R.id.settingView,start).commit()
        mViewModel.currentPage = SettingViewModel.PAGE_START
    }

    fun showAccountFragment(view: View? = null) = runBlocking{
        if (!UserHelper.isLogin()){
            toast("用户未登录")
            return@runBlocking
        }
        delay(100)
        supportFragmentManager.beginTransaction().replace(R.id.settingView,account).commit()
        mViewModel.currentPage = SettingViewModel.PAGE_ACCOUNT
    }

    fun showAdviceFragment(view: View? = null) = runBlocking{
        delay(100)
        supportFragmentManager.beginTransaction().replace(R.id.settingView,advice).commit()
        mViewModel.currentPage = SettingViewModel.PAGE_ADVICE
    }

    fun showHelpFragment(view: View? = null) = runBlocking{
        delay(100)
        supportFragmentManager.beginTransaction().replace(R.id.settingView,help).commit()
        mViewModel.currentPage = SettingViewModel.PAGE_HELP
    }

    fun showManagerFragment(view: View? = null) = runBlocking{
        mViewModel.initHomePage()
        delay(100)
        supportFragmentManager.beginTransaction().replace(R.id.settingView,homeManager).commit()
        mViewModel.currentPage = SettingViewModel.PAGE_HOME_MANAGER
    }

    fun showMessageFragment(view: View? = null) = runBlocking{
        if (!UserHelper.isLogin()){
            toast("用户未登录")
            return@runBlocking
        }
        mViewModel.initUser()
        delay(100)
        supportFragmentManager.beginTransaction().replace(R.id.settingView,message).commit()
        mViewModel.currentPage = SettingViewModel.PAGE_MESSAGE
    }

    fun showNormalFragment(view: View? = null) = runBlocking{
        delay(100)
        supportFragmentManager.beginTransaction().replace(R.id.settingView,normal).commit()
        mViewModel.currentPage = SettingViewModel.PAGE_NORMAL
    }

    fun pickArea(view:View) = mCityPicker.showCityPicker()

    fun pickTime(view:View) = mTimePicker.show()

    fun pickSex(view:View) = mSexPicker.show()

    fun onChangeName(view:View){
        val intent = Intent(this,EditStActivity::class.java)
        intent.putExtra("content",mViewModel.name.value)
        intent.putExtra("mode",EditStActivity.MODE_NAME)
        startActivityForResult(intent,EditStActivity.MODE_NAME)
    }

    fun onChangeRealName(view:View){
        val intent = Intent(this,EditStActivity::class.java)
        intent.putExtra("content",mViewModel.realName.value)
        intent.putExtra("mode",EditStActivity.MODE_REAL_NAME)
        startActivityForResult(intent,EditStActivity.MODE_REAL_NAME)
    }

    fun onChangeSignature(view:View){
        val intent = Intent(this,EditStActivity::class.java)
        intent.putExtra("content",mViewModel.signature.value)
        intent.putExtra("mode",EditStActivity.MODE_SIGNATURE)
        startActivityForResult(intent,EditStActivity.MODE_SIGNATURE)
    }

    fun onChangeHeader(view: View?=null){
        PictureHelper.instance.openPhoto(this)
    }

    fun onClickFeedBackPicture(view:View){
        PictureHelper.instance.openPhoto(this,requestCode = AdviceStFragment.ADAVICE_PHOTO)
    }

    fun onClickSendAdvice(view:View){
        mViewModel.onSendAdvice()
    }

    fun onClickLogout(view:View){
        logoutDialog.show()
    }

    fun onClickClearCache(view:View){
        clearCacheDialog.show()
    }

    fun onClickClearIm(view:View){
        clearImDialog.show()
    }

    fun onClickClearSystem(view:View){
        clearSystemDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            when(requestCode){
                EditStActivity.MODE_SIGNATURE ->{
                    val content = data?.getStringExtra("content")?:""
                    mViewModel.signature.value = content
                }
                EditStActivity.MODE_NAME ->{
                    val content = data?.getStringExtra("content")?:""
                    mViewModel.name.value = content
                }
                EditStActivity.MODE_REAL_NAME ->{
                    val content = data?.getStringExtra("content")?:""
                    mViewModel.realName.value = content
                }
                PictureHelper.REQUEST_DEFAULT->{
                    val uri = PictureHelper.instance.obtainUriFromPhoto(data)
                    PictureHelper.instance.cropCircleImage(this@SettingActivity,uri)
                }
                PictureHelper.REQUEST_CROUP->{
                    val uri = PictureHelper.instance.obtainCropUri(data)
                    val oriPath = PictureHelper.instance.obtainPathFromUri(uri)
                    GlobalScope.launch(Dispatchers.IO) {
                        val comPath = PictureHelper.instance.compressImage(this@SettingActivity,oriPath)
                        launch(Dispatchers.Main) {
                            mViewModel.path.value = comPath
                            mBinding.viewModel!!.uploadHeader()
                        }
                    }
                }
                AdviceStFragment.ADAVICE_PHOTO ->{
                    val uri = PictureHelper.instance.obtainUriFromPhoto(data)
                    mViewModel.fdUri.value = uri
                }
            }
        }
    }
}
