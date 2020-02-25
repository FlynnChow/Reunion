package com.example.reunion.repostory.remote_resource

import android.hardware.camera2.CameraCharacteristics
import androidx.lifecycle.MutableLiveData
import com.example.reunion.base.BaseViewModel

class CameraViewModel:BaseViewModel() {
    var mCameraFacing = CameraCharacteristics.LENS_FACING_BACK
    var zoomLevel = MutableLiveData(1)
    var zoomSeekBar = MutableLiveData(0f)
}