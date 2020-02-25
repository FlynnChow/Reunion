package com.example.reunion.view

import android.Manifest
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.databinding.ActivityCameraBinding
import com.example.reunion.repostory.local_resource.CameraHelper
import com.example.reunion.repostory.remote_resource.CameraViewModel
import kotlinx.android.synthetic.main.activity_camera.*

class CameraActivity : BaseActivity() {
    private lateinit var mBinding:ActivityCameraBinding
    private lateinit var cameraHelper:CameraHelper
    private val mViewModel:CameraViewModel by lazy {
        setViewModel(this,CameraViewModel::class.java)
    }
    override fun create(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_camera)
        mBinding.lifecycleOwner = this

       cameraHelper = CameraHelper(this, cameraView,mBinding.faceDrawView!!)

        takePicture.setOnClickListener {
            cameraHelper.takePicture{

            }
        }

        exchangeCamera.setOnClickListener {
            cameraHelper.exchangeCamera()
            cameraSeekBar?.progress = 0
        }

        cameraHelper.addFaceListener {
            faceDrawView?.updateFaceList(it)
        }

        cameraSeekBar?.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                cameraHelper.setZoom(progress/100f)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }


    override fun insertMustPermission(): ArrayList<String>? {
        return ArrayList<String>().apply {
            add(Manifest.permission.CAMERA)
        }
    }

    override fun onResume() {
        super.onResume()
        cameraHelper.mCameraFacing = mViewModel.mCameraFacing
        cameraHelper.zoomLevel = mViewModel.zoomLevel.value!!
        if (allPermissionsGranted()){
            cameraHelper.startCamera()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraHelper.onDestroy()
    }


    override fun onStop() {
        mViewModel.mCameraFacing = cameraHelper.mCameraFacing
        mViewModel.zoomLevel.value = cameraHelper.zoomLevel
        super.onStop()
    }

    override fun permissionCheckPass() = cameraHelper.startCamera()

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ev?.apply {
            cameraHelper.onTouch(this){
                cameraSeekBar?.progress = (it*100).toInt()
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}
