package com.example.reunion.view

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.base.BaseViewModel
import com.example.reunion.databinding.ActivityCommunitySendBinding
import com.example.reunion.databinding.DialogLoadingBinding
import com.example.reunion.repostory.bean.CommunityBean
import com.example.reunion.repostory.local_resource.PictureHelper
import com.example.reunion.view.adapter.SendTopicAdapter
import com.example.reunion.view_model.CommunitySendViewModel
import kotlinx.android.synthetic.main.cust_city_select.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CommunitySendActivity : BaseActivity() {

    private val mViewModel by lazy {
        setViewModel(this,CommunitySendViewModel::class.java)
    }

    private val loadDialog by lazy {
        initLoadDialog()
    }

    private val adapter = SendTopicAdapter()

    private lateinit var mBinding:ActivityCommunitySendBinding

    private lateinit var loadDialogBinding: DialogLoadingBinding


    override fun create(savedInstanceState: Bundle?) {
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_community_send)
        mBinding.lifecycleOwner = this
        mBinding.activity = this
        mBinding.viewModel = mViewModel

        loadDialogBinding = DataBindingUtil.inflate(layoutInflater,R.layout.dialog_loading,null,false)
        loadDialogBinding.text = resources.getString(R.string.compressing)
        loadDialogBinding.lifecycleOwner = this

        initView()
        initViewModel()
        initLocate()
    }

    private fun initView(){
        val layoutManager = GridLayoutManager(this,3)
        adapter.openPhotoListener = {
            openPhoto()
        }
        mBinding.recyclerView.adapter = adapter
        mBinding.recyclerView.layoutManager = layoutManager
    }

    private fun initViewModel(){
        mViewModel.showDialog.observe(this, Observer {
            if (it)
                loadDialog.show()
            else
                loadDialog.dismiss()
        })
        mViewModel.onCompress.observe(this, Observer {
            if (it)
                onCompressPicture()
        })
        mViewModel.responseData.observe(this, Observer {
            startCommunity(it)
        })
    }

    fun onBack(view: View) = onBackPressed()

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
        dialog.dismiss()
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    private fun openPhoto(){
        PictureHelper.instance.openPhoto(this,9)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PictureHelper.REQUEST_DEFAULT && resultCode == Activity.RESULT_OK){
            val uris = PictureHelper.instance.obtainUrisFromPhoto(data)
            adapter.uris = uris as ArrayList<Uri>
            adapter.notifyDataSetChanged()

            mBinding.recyclerView.scrollToPosition(adapter.itemCount - 1)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun initLocate(){
        var mLocationClient: AMapLocationClient? = null
        val mLocationListener = AMapLocationListener {
            if (it.longitude != 0.0||it.latitude != 0.0){
                mViewModel.locateMessage.value = "${it.province}·${it.city}"
                mLocationClient?.stopLocation()
                mLocationClient?.onDestroy()
            }
        }
        mLocationClient = AMapLocationClient(this)
        mLocationClient.setLocationListener(mLocationListener)

        var mLocationOption: AMapLocationClientOption? = null
        mLocationOption = AMapLocationClientOption()
        mLocationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        mLocationOption.interval = 1000

        mLocationClient.setLocationOption(mLocationOption)
        mLocationClient.startLocation()
    }

    private fun onCompressPicture(){
        GlobalScope.launch(Dispatchers.IO) {
            val paths = PictureHelper.instance.obtainPathsFromUris(adapter.uris)
            val topicPaths = PictureHelper.instance.compressImagesFromPaths(
                this@CommunitySendActivity,paths,size = 512)
            launch(Dispatchers.Main) {
                mViewModel.picturePaths.value = topicPaths
                mViewModel.onStartSend()
            }
        }
    }

    private fun startCommunity(data:CommunityBean){
        startActivity(Intent(this,CommunityActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra("data",data)
        })
        onBackPressed()
    }

}
