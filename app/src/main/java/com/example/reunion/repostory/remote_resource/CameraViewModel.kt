package com.example.reunion.repostory.remote_resource

import android.graphics.*
import android.hardware.camera2.CameraCharacteristics
import android.util.Log
import android.util.TypedValue
import androidx.lifecycle.MutableLiveData
import com.example.reunion.MyApplication
import com.example.reunion.R
import com.example.reunion.base.BaseViewModel
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions


class CameraViewModel:BaseViewModel() {
    var mCameraFacing = CameraCharacteristics.LENS_FACING_BACK
    var zoomLevel = MutableLiveData(1)
    var path = ""
    val imageBitmap = MutableLiveData<Bitmap>()
    var distNum = MutableLiveData(0)
    var isLoading = MutableLiveData(true)


    private val accuracyOpts = FirebaseVisionFaceDetectorOptions.Builder()
        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
        .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
        .build()

    private val numOpts = FirebaseVisionFaceDetectorOptions.Builder()
        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
        .build()

    fun initBitmap(){
        if (path.isEmpty())
            return
        val bitmap = BitmapFactory.decodeFile(path)
        val image = FirebaseVisionImage.fromBitmap(bitmap)
        val numDetector = FirebaseVision.getInstance()
            .getVisionFaceDetector(numOpts)

        val loadBitmap = Bitmap.createBitmap(bitmap.width,bitmap.height,Bitmap.Config.ARGB_8888)
        val loadCanvas = Canvas(loadBitmap)
        val grey = ColorMatrix(
            floatArrayOf(
                0.213f, 0.715f, 0.072f, 0f, 0f,
                0.213f, 0.715f, 0.072f, 0f, 0f,
                0.213f, 0.715f, 0.072f, 0f, 0f, 0f, 0f, 0f, 1f, 0f
            )
        )
        val greyPaint = Paint()
        greyPaint.colorFilter = ColorMatrixColorFilter(grey)
        loadCanvas.drawBitmap(bitmap,0f,0f,greyPaint)
        imageBitmap.value = loadBitmap
        isLoading.value = true

        numDetector.detectInImage(image).addOnSuccessListener {
            distNum.value = it.size
            if (it.size==1){
                drawBitmap(image)
            }else{
                isLoading.value = false
            }
        }.addOnFailureListener {
            isLoading.value = false
            distNum.value = -1
            toast.value = "检测失败"
        }
    }

    private fun drawBitmap(image:FirebaseVisionImage){
        val detector = FirebaseVision.getInstance()
            .getVisionFaceDetector(accuracyOpts)

        val bitmap = BitmapFactory.decodeFile(path)
        val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        pointPaint.color = MyApplication.resource().getColor(R.color.draw_point)

        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        linePaint.style = Paint.Style.STROKE
        linePaint.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, MyApplication.resource().displayMetrics)

        val copyBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(copyBitmap)
        detector.detectInImage(image)
            .addOnSuccessListener{
                for (face in it){
                    val path = Path()
                    var contour = face.getContour(FirebaseVisionFaceContour.FACE)
                    linePaint.color = MyApplication.resource().getColor(R.color.draw_face)
                    for (index in 0 until contour.points.size){
                        if (index == 0)
                            path.moveTo(contour.points[index].x,contour.points[index].y)
                        else
                            path.lineTo(contour.points[index].x,contour.points[index].y)
                    }
                    path.close()
                    canvas.drawPath(path,linePaint)

                    path.reset()
                    linePaint.color = MyApplication.resource().getColor(R.color.draw_eyebrow)
                    contour = face.getContour(FirebaseVisionFaceContour.LEFT_EYEBROW_TOP)
                    for (index in 0 until contour.points.size){
                        if (index == 0)
                            path.moveTo(contour.points[index].x,contour.points[index].y)
                        else
                            path.lineTo(contour.points[index].x,contour.points[index].y)
                    }
                    contour = face.getContour(FirebaseVisionFaceContour.LEFT_EYEBROW_BOTTOM)
                    for (index in (contour.points.size-1) downTo 0){
                        path.lineTo(contour.points[index].x,contour.points[index].y)
                    }
                    path.close()
                    canvas.drawPath(path,linePaint)

                    path.reset()
                    linePaint.color = MyApplication.resource().getColor(R.color.draw_eyebrow)
                    contour = face.getContour(FirebaseVisionFaceContour.RIGHT_EYEBROW_TOP)
                    for (index in 0 until contour.points.size){
                        if (index == 0)
                            path.moveTo(contour.points[index].x,contour.points[index].y)
                        else
                            path.lineTo(contour.points[index].x,contour.points[index].y)
                    }
                    contour = face.getContour(FirebaseVisionFaceContour.RIGHT_EYEBROW_BOTTOM)
                    for (index in (contour.points.size-1) downTo 0){
                        path.lineTo(contour.points[index].x,contour.points[index].y)
                    }
                    path.close()
                    canvas.drawPath(path,linePaint)

                    path.reset()
                    linePaint.color = MyApplication.resource().getColor(R.color.draw_eyes)
                    contour = face.getContour(FirebaseVisionFaceContour.LEFT_EYE)
                    for (index in 0 until contour.points.size){
                        if (index == 0)
                            path.moveTo(contour.points[index].x,contour.points[index].y)
                        else
                            path.lineTo(contour.points[index].x,contour.points[index].y)
                    }
                    path.close()
                    canvas.drawPath(path,linePaint)

                    path.reset()
                    linePaint.color = MyApplication.resource().getColor(R.color.draw_eyes)
                    contour = face.getContour(FirebaseVisionFaceContour.RIGHT_EYE)
                    for (index in 0 until contour.points.size){
                        if (index == 0)
                            path.moveTo(contour.points[index].x,contour.points[index].y)
                        else
                            path.lineTo(contour.points[index].x,contour.points[index].y)
                    }
                    path.close()
                    canvas.drawPath(path,linePaint)

                    path.reset()
                    linePaint.color = MyApplication.resource().getColor(R.color.draw_lips)
                    contour = face.getContour(FirebaseVisionFaceContour.LOWER_LIP_TOP)
                    for (index in 0 until contour.points.size){
                        if (index == 0)
                            path.moveTo(contour.points[index].x,contour.points[index].y)
                        else
                            path.lineTo(contour.points[index].x,contour.points[index].y)
                    }
                    contour = face.getContour(FirebaseVisionFaceContour.LOWER_LIP_BOTTOM)
                    for (index in 0 until contour.points.size){
                        path.lineTo(contour.points[index].x,contour.points[index].y)
                    }
                    path.close()
                    canvas.drawPath(path,linePaint)

                    path.reset()
                    linePaint.color = MyApplication.resource().getColor(R.color.draw_lips)
                    contour = face.getContour(FirebaseVisionFaceContour.UPPER_LIP_TOP)
                    for (index in 0 until contour.points.size){
                        if (index == 0)
                            path.moveTo(contour.points[index].x,contour.points[index].y)
                        else
                            path.lineTo(contour.points[index].x,contour.points[index].y)
                    }
                    contour = face.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM)
                    for (index in 0 until contour.points.size){
                        path.lineTo(contour.points[index].x,contour.points[index].y)
                    }
                    path.close()
                    canvas.drawPath(path,linePaint)

                    path.reset()
                    linePaint.color = MyApplication.resource().getColor(R.color.draw_nose)
                    contour = face.getContour(FirebaseVisionFaceContour.NOSE_BRIDGE)
                    if (contour.points.size>0){
                        path.moveTo(contour.points[0].x,contour.points[0].y)
                        contour = face.getContour(FirebaseVisionFaceContour.NOSE_BOTTOM)
                        for (index in 0 until contour.points.size){
                            path.lineTo(contour.points[index].x,contour.points[index].y)
                        }
                        path.close()
                        canvas.drawPath(path,linePaint)
                    }

                    contour = face.getContour(FirebaseVisionFaceContour.NOSE_BRIDGE)
                    if (contour.points.size >0)
                        canvas.drawLine(contour.points[0].x,contour.points[0].y,contour.points[1].x,contour.points[1].y,linePaint)

                    contour = face.getContour(FirebaseVisionFaceContour.ALL_POINTS)
                    for (point in contour.points){
                        canvas.drawCircle(point.x,point.y,TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.7f, MyApplication.resource().displayMetrics),pointPaint)
                    }
                }
                imageBitmap.value = copyBitmap
                isLoading.value = false
            }
            .addOnFailureListener{
                isLoading.value = false
                distNum.value = -1
                toast.value = "检测失败"
            }
    }

    fun setTextViewText(num:Int):String{
        return when(num){
            1 -> MyApplication.resource().getString(R.string.face_text_success)
            -1 -> MyApplication.resource().getString(R.string.face_text_error)
            0 -> MyApplication.resource().getString(R.string.face_text_inaccurate)
            else -> "检测出${num}张人脸，请尝试剪切"
        }
    }

    fun setButtonText(num:Int):String{
        return when(num){
            1 -> MyApplication.resource().getString(R.string.face_button_success)
            0,-1 -> MyApplication.resource().getString(R.string.face_button_inaccurate)
            else -> MyApplication.resource().getString(R.string.face_button_cut)
        }
    }
}