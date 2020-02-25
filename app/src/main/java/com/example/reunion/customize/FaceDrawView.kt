package com.example.reunion.customize

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.example.reunion.R
import com.google.firebase.ml.vision.common.FirebaseVisionPoint

class FaceDrawView @JvmOverloads constructor(context: Context,attributeSet: AttributeSet? = null,defStyle:Int = 0):View(context,attributeSet,defStyle) {

    private var mPaint: Paint = Paint()
    private var mBgPaint: Paint = Paint()
    private var faceList:ArrayList<FirebaseVisionPoint>? = null
    private var mWidth = 0
    private var mHeight = 0
    val path = Path()

    init {
        mPaint.color = context.resources.getColor(R.color.mainColor)
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = 20f
        mBgPaint.color = context.resources.getColor(R.color.testColor)
    }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawRect(0f,0f,width.toFloat(),height.toFloat(),mBgPaint)
        if (faceList!=null){
            path.reset()
            for (index in 0 until faceList!!.size){
                Log.d("测试$index","${faceList!![index].x}  ${faceList!![index].y}  ${mWidth}  ${mHeight}")
                if (index == 0)
                    path.moveTo((1-faceList!![index].x/640)*width,faceList!![index].y*height/480)
                else
                    path.lineTo((1-faceList!![index].x/640)*width,faceList!![index].y*height/480)
            }
            path.close()
            canvas?.drawPath(path,mPaint)
        }
    }

    fun setFaceList(faceList:ArrayList<FirebaseVisionPoint>){
        mWidth = width
        mHeight = height
        this.faceList?.clear()
        this.faceList = null
        this.faceList = faceList
        invalidate()
    }
}