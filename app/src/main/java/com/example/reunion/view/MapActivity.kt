package com.example.reunion.view

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.LinearInterpolator
import androidx.databinding.DataBindingUtil
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapView
import com.amap.api.maps.model.*
import com.amap.api.maps.model.animation.Animation
import com.amap.api.maps.model.animation.RotateAnimation
import com.amap.api.maps.model.animation.ScaleAnimation
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.databinding.ActivityMapBinding
import com.example.reunion.databinding.DialogMapHelpBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MapActivity : BaseActivity() {
    companion object{
        const val REQUEST_CODE = 3012
    }
    private lateinit var mBinding:ActivityMapBinding
    private lateinit var mHelpBinding:DialogMapHelpBinding

    private lateinit var mapView:MapView

    private var mMap:AMap? = null

    private var polygon:Polygon? = null

    private var markers:Queue<Marker> = LinkedList()

    private val markerMap = HashMap<Marker,LatLng>()

    private val dialog by lazy {
        initLoadDialog()
    }

    override fun create(savedInstanceState: Bundle?) {

        val window: Window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_map)
        mBinding.activity = this
        mBinding.lifecycleOwner = this

        mHelpBinding = DataBindingUtil.inflate(layoutInflater,R.layout.dialog_map_help,null,false)
        mHelpBinding.activity = this

        mapView = mBinding.map
        mapView.onCreate(savedInstanceState)

        if (allPermissionsGranted()){
            initMap()
        }
    }

    private fun initMap(){
        if(mMap == null){
            mMap = mapView.map
        }

        //蓝点定位
        val myLocationStyle = MyLocationStyle()
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW)
        myLocationStyle.interval(1000)
        mMap?.myLocationStyle = myLocationStyle
        mMap?.isMyLocationEnabled = true

        val uiSt = mMap?.uiSettings
        uiSt?.isZoomControlsEnabled = false
        uiSt?.isMyLocationButtonEnabled = false
        mMap?.showIndoorMap(true) //室内
        mMap?.moveCamera(CameraUpdateFactory.zoomTo(18f))
        mMap?.setOnMapClickListener {
            mapOnClick(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun permissionCheckPass() {
        initMap()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    private fun mapOnClick(latLng: LatLng){
        onMark(latLng)
    }

    private fun onMark(latLng: LatLng){
        val marker: Marker =
            mMap!!.addMarker(MarkerOptions().position(latLng).title("范围标记").snippet("用于确定需要寻找对象的范围"))
        markerMap[marker] = latLng
        val animation: Animation = RotateAnimation(marker.rotateAngle+180, marker.rotateAngle+360, 0f, 0f, 0f)
        val duration = 400L
        animation.setDuration(duration)
        animation.setInterpolator(BounceInterpolator())
        marker.setAnimation(animation)
        marker.startAnimation()

        markers.offer(marker)
        if (markers.size >3){
            val oldMarker = markers.poll()
            val exitAnim: Animation = ScaleAnimation(1f,0.5f,0.25f,0f)
            exitAnim.setInterpolator(AccelerateInterpolator())
            oldMarker?.setAnimation(exitAnim)
            oldMarker?.startAnimation()
            if (oldMarker != null)
                markerMap.remove(oldMarker)
            oldMarker?.remove()
        }
        if (markers.size >= 3){
            polygon?.remove()
            val options = PolygonOptions()
            for (mark in markers){
                options.add(markerMap[mark])
            }
            options.strokeWidth(15f)
            options.strokeColor(resources.getColor(R.color.map_stroke))
            options.fillColor(resources.getColor(R.color.map_stroke))
            polygon = mMap!!.addPolygon(options)
        }
    }

    fun onShowHelp(view:View){
        dialog.show()
    }

    private fun getMarkString(marker: Marker):String{
        val latLng = markerMap[marker]?:return ""
        return "${latLng.longitude},${latLng.latitude}"
    }

    fun onSave(view:View){
        if(markers.size <3){
            toast("至少需要3个标记才能确定范围\n请在确定${3-markers.size}个标记")
        }else{
            val date = Intent()
            val saves = markers as LinkedList<Marker>
            val arrays = arrayOf(getMarkString(saves[0]),getMarkString(saves[1]),getMarkString(saves[2]))
            date.putExtra("locateArray",arrays)
            setResult(Activity.RESULT_OK,date)
            finish()
        }
    }

    private fun initLoadDialog(): Dialog {
        val dialog =  Dialog(this,R.style.CustomizeDialog)
        dialog.setContentView(mHelpBinding.root)
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

    fun onBack(view:View){
        onBackPressed()
    }

    fun onCancel(view:View){
        dialog.dismiss()
    }

    fun onMyLocate(view:View? = null){
        val myLocationStyle = MyLocationStyle()
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE)
        mMap?.myLocationStyle = myLocationStyle
        mMap?.isMyLocationEnabled = true
    }
}
