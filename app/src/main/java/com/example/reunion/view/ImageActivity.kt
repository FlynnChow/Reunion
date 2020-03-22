package com.example.reunion.view

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.transition.ChangeBounds
import android.transition.ChangeImageTransform
import android.transition.ChangeTransform
import android.transition.TransitionSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.base.BaseRemoteResource
import com.example.reunion.databinding.ActivityImageBinding
import com.example.reunion.util.StringDealerUtil
import com.example.reunion.view.adapter.ImageAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Request
import java.io.*
import kotlin.math.abs
import kotlin.math.sqrt


class ImageActivity : BaseActivity() {
    companion object{
        @JvmStatic
        fun onShowImage(activity: Activity, view:ImageView,urls:ArrayList<String>,target: Int){
            val pair = Pair<View,String>(view,"image")
            val compat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pair)
            activity.startActivity(Intent(activity,ImageActivity::class.java).apply {
                putExtra("urls",urls)
                putExtra("target",target)
            },compat.toBundle())
        }
        @JvmStatic
        fun onShowImage(activity: Activity,view: ImageView,url: String){
            onShowImage(activity,view, arrayListOf(url),0)
        }
    }

    private var maxSize = 0
    private lateinit var mBinding:ActivityImageBinding
    private val adapter = ImageAdapter()

    override fun create(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_image)

        mBinding.viewPager.adapter = adapter
        initIntoAnim()

        val urls = intent.getStringArrayListExtra("urls")?:return
        val target = intent.getIntExtra("target",0)
        maxSize = urls.size
        setIndicator(target)

        val metric = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metric)
        val width = metric.widthPixels
        val height = metric.heightPixels
        val hToW = (height*4f/5f)/width.toFloat()
        scaleWidth = width*4f/5f
        displayWidth = width.toFloat()
        displayHeight = height.toFloat()
        GlobalScope.launch {
            launch {
                initImage(urls[target],hToW,target)
                for (index in 0 until urls.size){
                    if (index != target)
                        initImage(urls[index],hToW,null)
                }
            }
        }

        mBinding.viewPager.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                setIndicator(position)
            }
        })

        mBinding.save.setOnClickListener {
            mBinding.save.isClickable = false
            val url = urls[mBinding.viewPager.currentItem]
            val saveDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath+ File.separator+"Save"+ File.separator
            val saveFile =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath+ File.separator+"Save"+ File.separator +
                        StringDealerUtil.getStringToMD5(url)+".jpg"
            val dir = File(saveDir)
            if (!dir.exists())
                dir.mkdirs()
            val file = File(saveFile)
            if (!file.exists()){
                GlobalScope.launch {
                    try {
                        val request: Request = Request.Builder()
                            .url(url)
                            .build()
                        val response = BaseRemoteResource.client.newCall(request).execute()
                        if (response.isSuccessful){
                            val inStream = response.body?.byteStream()?:return@launch
                            val inBuffStream = BufferedInputStream(inStream)
                            val buff = ByteArray(2048)
                            val outStream = FileOutputStream(saveFile)
                            val outBuffStream = BufferedOutputStream(outStream)
                            while (true){
                                val len = inBuffStream.read(buff)
                                if (len == -1) break
                                outBuffStream.write(buff,0,len)
                            }
                            GlobalScope.launch(Dispatchers.Main){
                                toast("已保存至$saveFile")
                                MediaStore.Images.Media.insertImage(
                                    contentResolver,
                                    file.absolutePath, file.name, null);
                                sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.parse("file://$saveFile")))
                                sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.fromFile(file)))
                            }
                        }
                    }catch (e:java.lang.Exception){
                        toast("下载失败")
                    }finally {
                        GlobalScope.launch(Dispatchers.Main){
                            mBinding.save.isClickable = true
                        }
                    }
                }
            }else{
                toast("图片已存在")
            }
        }
    }

    private fun initIntoAnim(){
        ViewCompat.setTransitionName(mBinding.viewPager, "image")

        val set = TransitionSet()
        set.addTransition(ChangeBounds())
        set.addTransition(ChangeImageTransform())
        set.addTransition(ChangeTransform())

        window.sharedElementEnterTransition = set
        window.sharedElementExitTransition = set
    }

    suspend fun initImage(url:String,hToW:Float,target:Int? = null){
        Glide.with(this@ImageActivity).asBitmap().load(url).into(object :
            SimpleTarget<Bitmap>() {
            override fun onResourceReady(
                resource: Bitmap,
                transition: Transition<in Bitmap>?
            ) {
                val bw = resource.width
                val bh = resource.height
                val bs = bh/bw.toFloat()
                val image = ImageAdapter.Image()
                if (bs < hToW){
                    image.type = ImageAdapter.Image.NORMAL_IMAGE
                    image.bitmap = resource
                }else{
                    image.type = ImageAdapter.Image.BIG_IMAGE
                    image.bitmap = try {
                        loadBigImage(resource)
                    }catch (e:Exception){
                        image.type = ImageAdapter.Image.NORMAL_IMAGE
                        resource
                    }
                }
                adapter.images.add(image)
                if (target!=null){
                    mBinding.viewPager.currentItem = target
                }
                adapter.notifyDataSetChanged()
            }
        })
    }

    fun loadBigImage(bitmap:Bitmap):Bitmap{
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val isBm: InputStream = ByteArrayInputStream(baos.toByteArray())

        val decoder = BitmapRegionDecoder.newInstance(isBm, true)

        val imgWidth = decoder.width
        val imgHeight = decoder.height

        val opts = BitmapFactory.Options()

        val sum = imgHeight / 3000

        val redundant = imgHeight % 3000

        val bitmapList: ArrayList<Bitmap> = ArrayList()

        if (sum == 0) {
            //直接加载
            return bitmap
        } else {
            //说明需要切分图片
            val mRect = Rect()
            for (i in 0 until sum) {
                //需要注意：mRect.set(left, top, right, bottom)的第四个参数，
                //也就是图片的高不能大于这里的4096
                mRect.set(0, i * 3000, imgWidth, (i + 1) * 3000)
                val bm = decoder.decodeRegion(mRect, opts)
                bitmapList.add(bm)
            }

            //将多余的不足3000的部分作为尾部拼接
            if (redundant > 0) {
                mRect.set(0, sum * 3000, imgWidth, imgHeight)
                val bm = decoder.decodeRegion(mRect, opts)
                bitmapList.add(bm)
            }

            val bigBitmap =
                Bitmap.createBitmap(imgWidth, imgHeight, Bitmap.Config.ARGB_8888)
            val bigCanvas = Canvas(bigBitmap)

            val paint = Paint()
            var iHeight = 0f

            //将之前的bitmap取出来拼接成一个bitmap
            //将之前的bitmap取出来拼接成一个bitmap
            for (i in bitmapList.indices) {
                var bmp: Bitmap? = bitmapList[i]
                bigCanvas.drawBitmap(bmp?:throw java.lang.Exception(), 0f, iHeight, paint)
                iHeight += bmp.height
                bmp.recycle()
                bmp = null
            }

            return bigBitmap
        }
    }


    private var fingerSpacing = 0f
    private var scaleWidth = 0f
    private var displayWidth = 0f
    private var displayHeight = 0f
    private var lastScale = 0f
    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        event?.apply {
            when(event.action){
                MotionEvent.ACTION_DOWN ->{
                    fingerSpacing = 0f
                    lastScale = adapter.getScale(mBinding.viewPager.currentItem)
                }
                MotionEvent.ACTION_MOVE->{
                    if (event.pointerCount >1){
                        if (fingerSpacing == 0f){
                            fingerSpacing = getSpacing(event)
                            adapter.setPivot(mBinding.viewPager.currentItem,
                                (abs((event.getX(0)+event.getX(1))/2)/mBinding.viewPager.width),
                                (abs((event.getY(0)+event.getY(1))/2)/mBinding.viewPager.height))
                        }else{
                            val currentFingerSpacing = getSpacing(event)
                            val distance = currentFingerSpacing - fingerSpacing
                            val scale = lastScale + distance / scaleWidth
                            adapter.setScale(mBinding.viewPager.currentItem,scale)
                        }
                        return true
                    }
                }
                MotionEvent.ACTION_UP->{

                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun getSpacing(ev:MotionEvent):Float{
        val x = ev.getX(0) - ev.getX(1)
        val y = ev.getY(0) - ev.getY(1)
        return sqrt(x*x+y*y)
    }

    private fun setIndicator(position:Int){
        mBinding.indicator.post {
            val text = "${position+1} / $maxSize"
            mBinding.indicator.text = text
        }
    }


}
