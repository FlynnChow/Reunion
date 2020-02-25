package com.example.reunion.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
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
import com.example.reunion.repostory.local_resource.PictureHelper
import com.example.reunion.repostory.local_resource.UserHelper
import com.example.reunion.view.setting_view.*
import com.example.reunion.view_model.SettingViewModel
import com.lljjcoder.Interface.OnCityItemClickListener
import com.lljjcoder.bean.CityBean
import com.lljjcoder.bean.DistrictBean
import com.lljjcoder.bean.ProvinceBean
import com.lljjcoder.citywheel.CityConfig
import com.lljjcoder.style.citypickerview.CityPickerView
import kotlinx.coroutines.*
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
        initPicker()
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
            }
        }
    }
}
