package com.example.reunion.view

import android.R.attr
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.customize.CityListSelectActivity
import com.example.reunion.databinding.ActivityFaceImageBinding
import com.example.reunion.repostory.local_resource.PictureHelper
import com.example.reunion.repostory.remote_resource.CameraViewModel
import com.lljjcoder.style.citylist.bean.CityInfoBean
import kotlinx.android.synthetic.main.activity_face_image.*
import java.io.File


class FaceImageActivity : BaseActivity() {
    private lateinit var mBinding: ActivityFaceImageBinding

    private val mViewModel: CameraViewModel by lazy {
        setViewModel(this, CameraViewModel::class.java)
    }
    override fun create(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_face_image)
        mBinding.mViewModel = mViewModel
        mBinding.activity = this
        mBinding.lifecycleOwner = this

        val path = intent.getStringExtra("picture")?:""
        mViewModel.path = path
        faceImageView.post {
            mViewModel.initBitmap()
        }
    }

    fun back(view:View){
        onBackPressed()
    }

    fun selectArea(view:View){
        val intent = Intent(this, CityListSelectActivity::class.java)
        startActivityForResult(intent, CityListSelectActivity.CITY_SELECT_RESULT_FRAG)
    }

    override fun onBackPressed() {
        val intent = Intent(this,CameraActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }

    fun onButtonClick(view:View){
        when(mViewModel.distNum.value?:0){
            1 -> {
                startActivity(Intent(this,FindOneActivity::class.java).apply {
                    putExtra("picture",mViewModel.path)
                })
                finish()
            }
            0,-1 -> onBackPressed()
            else -> {
                val uri = Uri.fromFile(File(mViewModel.path))
                PictureHelper.instance.cropImage(this,uri)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PictureHelper.REQUEST_CROUP&&resultCode == Activity.RESULT_OK){
            val uri = PictureHelper.instance.obtainCropUri(data)
            val oriPath = PictureHelper.instance.obtainPathFromUri(uri)?:""
            mViewModel.path = oriPath
            mViewModel.initBitmap()
        }
    }
}
