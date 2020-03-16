package com.example.reunion.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.bigkoo.pickerview.view.OptionsPickerView
import com.bigkoo.pickerview.view.TimePickerView
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.databinding.ActivitySendTopicBinding
import com.example.reunion.databinding.DialogLoadingBinding
import com.example.reunion.databinding.DialogScheduleBinding
import com.example.reunion.repostory.bean.TopicBean
import com.example.reunion.repostory.local_resource.PictureHelper
import com.example.reunion.repostory.server.UploadServer
import com.example.reunion.view.adapter.SendTopicAdapter
import com.example.reunion.view_model.SendTopicViewModel
import com.lljjcoder.Interface.OnCityItemClickListener
import com.lljjcoder.bean.CityBean
import com.lljjcoder.bean.DistrictBean
import com.lljjcoder.bean.ProvinceBean
import com.lljjcoder.citywheel.CityConfig
import com.lljjcoder.style.citypickerview.CityPickerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SendTopicActivity : BaseActivity() {
    private lateinit var mBinding:ActivitySendTopicBinding
    private lateinit var loadDialogBinding: DialogLoadingBinding
    private lateinit var progressBinding: DialogScheduleBinding

    private val adapter = SendTopicAdapter()
    private lateinit var mBinder: UploadServer.UpLoadBinder

    private lateinit var mTimePicker: TimePickerView
    private val mCityPicker by lazy { CityPickerView() }
    private lateinit var mAgePicker: OptionsPickerView<*>

    private val minWaitTime = 1000

    private val mViewModel by lazy { setViewModel(this,SendTopicViewModel::class.java) }

    private val loadDialog by lazy {
        initLoadDialog()
    }

    private val progressDialog by lazy {
        initProgressDialog()
    }

    private val serviceConnection = object :ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {

        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mBinder = service as UploadServer.UpLoadBinder
        }

    }

    override fun create(savedInstanceState: Bundle?) {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_send_topic)
        mBinding.lifecycleOwner = this
        mBinding.viewModel = mViewModel
        mBinding.activity = this

        loadDialogBinding = DataBindingUtil.inflate(layoutInflater,R.layout.dialog_loading,null,false)
        loadDialogBinding.text = resources.getString(R.string.compressing)
        loadDialogBinding.lifecycleOwner = this

        progressBinding = DataBindingUtil.inflate(layoutInflater,R.layout.dialog_schedule,null,false)
        progressBinding.switchBackground.setOnClickListener {
            switchBackground()
        }

        initView()
        initObserve()
        initService()
    }

    private fun initLoadDialog(): Dialog {
        val dialog =  Dialog(this,R.style.CustomizeDialog)
        dialog.setContentView(loadDialogBinding.root)
        val window = dialog.window
        val layoutParams = window?.attributes
        layoutParams?.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams?.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams?.gravity = Gravity.CENTER
        window?.setWindowAnimations(R.style.DialogAnim)
        dialog.show()
        dialog.hide()
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    private fun initProgressDialog(): Dialog {
        val dialog =  Dialog(this,R.style.CustomizeDialog)
        dialog.setContentView(progressBinding.root)
        val window = dialog.window
        val layoutParams = window?.attributes
        layoutParams?.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams?.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams?.gravity = Gravity.CENTER
        window?.setWindowAnimations(R.style.DialogAnim)
        dialog.show()
        dialog.hide()
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    @SuppressLint("SimpleDateFormat")
    private fun initView(){
        mViewModel.type.value = intent.getStringExtra("type")?:"people"
        if("people".equals(mViewModel.type)){
            mViewModel.title.value = resources.getString(R.string.send_find_people_title)
            mViewModel.time.value = resources.getString(R.string.send_find_people_time)
            mViewModel.arae.value = resources.getString(R.string.send_find_people_area)
            mViewModel.age.value = resources.getString(R.string.send_find_people_age)
        }else{
            mViewModel.title.value = resources.getString(R.string.send_find_body_title)
            mViewModel.time.value = resources.getString(R.string.send_find_body_time)
            mViewModel.arae.value = resources.getString(R.string.send_find_body_area)
            mViewModel.age.value = resources.getString(R.string.send_find_body_age)
        }

        val layoutManager = GridLayoutManager(this,3)
        adapter.openPhotoListener = {
            openPhoto()
        }
        mBinding.recyclerView.adapter = adapter
        mBinding.recyclerView.layoutManager = layoutManager

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
                val format = SimpleDateFormat("yyyy-MM-dd")
                mViewModel.topicTime.value = format.format(time)
            })
            .setTitleText("选择${mViewModel.time.value}")
            .setSubmitColor(resources.getColor(R.color.picker_color))
            .setCancelColor(resources.getColor(R.color.picker_color))
            .setDate(endDate)
            .setRangDate(startDate,endDate)
            .build()

        mCityPicker.init(this)
        val config = CityConfig.Builder().confirTextColor("#FF6868").build()
        mCityPicker.setConfig(config)
        mCityPicker.setOnCityItemClickListener(object : OnCityItemClickListener(){
            override fun onSelected(
                province: ProvinceBean?,
                city: CityBean?,
                district: DistrictBean?
            ) {
                val build = StringBuilder()
                build.append(province?.name?:"")
                    .append(",")
                    .append(city?.name?:"")
                    .append(",")
                    .append(district?.name?:"")
                mViewModel.topicArea.value = build.toString()
                mViewModel.province.value = province?.name
                mViewModel.city.value = city?.name
                mViewModel.district.value = district?.name
            }
        })

        val item: ArrayList<ProvinceBean> = ArrayList()
        for (index in 0 .. 100){
            item.add(ProvinceBean().apply { name = index.toString() + "岁" })
        }
        mAgePicker = OptionsPickerBuilder(this,
            OnOptionsSelectListener { options,_,_,_->
                mViewModel.topicAgeView.value = item[options].name
                mViewModel.topicAge.value = options
            })
            .setTitleText("选择${mViewModel.age.value}")
            .setSubmitColor(resources.getColor(R.color.picker_color))
            .setCancelColor(resources.getColor(R.color.picker_color))
            .build<Any>().also {
                it.setPicker(item as List<ProvinceBean>)
            }
    }

    private fun initObserve(){
        mViewModel.onCompress.observe(this, androidx.lifecycle.Observer {
            if (it)
                compressPictures()
        })

        mViewModel.showLoadDialog.observe(this, androidx.lifecycle.Observer {
            if (it)
                loadDialog.show()
            else
                loadDialog.dismiss()
        })

        mViewModel.requestBuilder.observe(this, androidx.lifecycle.Observer {
            progressDialog.show()
            mBinder.startUploadPicture(mViewModel.topicPaths,it){progress,state,bean->
                onUploadState(progress,state,bean)
            }
            mBinder.setTitle(mViewModel.title.value?:"发布话题")
        })
    }

    private fun initService(){
        val intent = Intent(this, UploadServer::class.java)
        startService(intent)
        bindService(intent,serviceConnection,BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        unbindService(serviceConnection)
        super.onDestroy()
    }

    private fun openPhoto(){
        PictureHelper.instance.openPhoto(this,9)
    }

    fun onBack(view: View){
        onBackPressed()
        stopService(Intent(this,
            UploadServer::class.java))
    }

    fun onSelectTime(view: View){
        mTimePicker.show()
    }

    fun onSelectArea(view: View){
        mCityPicker.showCityPicker()
    }

    fun onSelectAge(view: View){
        mAgePicker.show()
    }

    private fun compressPictures(){
        mViewModel.showLoadDialog.value = true
        val time = System.currentTimeMillis()
        GlobalScope.launch(Dispatchers.IO) {
            val paths = PictureHelper.instance.obtainPathsFromUris(adapter.uris)
            val topicPaths = PictureHelper.instance.compressImagesFromPaths(
                this@SendTopicActivity,paths,size = 1024)
            launch(Dispatchers.Main) {
                mViewModel.topicPaths = topicPaths
                //太快结束太闪
                val duration = System.currentTimeMillis() - time
                if (duration < minWaitTime)
                    delay(minWaitTime - duration)

                mViewModel.showLoadDialog.value = false
                mViewModel.onUploading()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PictureHelper.REQUEST_DEFAULT && resultCode == Activity.RESULT_OK){
            val uris = PictureHelper.instance.obtainUrisFromPhoto(data)
            adapter.uris = uris as ArrayList<Uri>
            adapter.notifyDataSetChanged()
            mViewModel.topicUris = uris

            mBinding.recyclerView.scrollToPosition(adapter.itemCount - 1)
        }else if(requestCode == MapActivity.REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val array = data?.getStringArrayExtra("locateArray")?: arrayOf("")
            mViewModel.arrayLocate = array
            mViewModel.markLocate.value = true
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun switchBackground(){
        mBinder.switchBackground()
        progressDialog.hide()
        finish()
    }

    private fun onUploadState(progress:Int,state:Int,data:TopicBean?){
        GlobalScope.launch(Dispatchers.Main) {
            progressBinding.progressBar.progress = progress
            if (state == UploadServer.UPLOAD_SUCCESS){
                progressDialog.dismiss()
                toast("上传成功")
                startActivity(Intent(this@SendTopicActivity,TopicActivity::class.java).apply {
                    putExtra("data",data)
                })
                finish()
            }else if (state == UploadServer.UPLOAD_FAILD){
                progressDialog.dismiss()
                toast("上传发生失败")
            }
        }
    }

    fun startMap(view:View){
        startActivityForResult(Intent(this,MapActivity::class.java),MapActivity.REQUEST_CODE)
    }


}
