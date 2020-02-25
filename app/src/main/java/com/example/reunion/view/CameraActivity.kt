package com.example.reunion.view

import android.Manifest
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
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

       cameraHelper = CameraHelper(this, cameraView)

        takePicture.setOnClickListener {
            cameraHelper.takePicture{

            }
        }

        exchangeCamera.setOnClickListener {
            cameraHelper.exchangeCamera()
        }
    }


    override fun insertMustPermission(): ArrayList<String>? {
        return ArrayList<String>().apply {
            add(Manifest.permission.CAMERA)
        }
    }

    override fun onResume() {
        super.onResume()
        cameraHelper.mCameraFacing = mViewModel.mCameraFacing
        if (allPermissionsGranted()){
            cameraHelper.startCamera()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraHelper.onDestory()
    }


    override fun onStop() {
        mViewModel.mCameraFacing = cameraHelper.mCameraFacing
        super.onStop()
    }

    override fun permissionCheckPass() = cameraHelper.startCamera()

}
