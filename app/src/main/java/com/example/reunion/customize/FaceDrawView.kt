package com.example.reunion.customize

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import com.example.reunion.R
import com.google.firebase.ml.vision.common.FirebaseVisionPoint

class FaceDrawView @JvmOverloads constructor(context: Context,attributeSet: AttributeSet? = null,defStyle:Int = 0):View(context,attributeSet,defStyle) {

    private var mPaint: Paint = Paint()
    private var faceList:ArrayList<RectF>? = null

    init {
        mPaint.color = context.resources.getColor(R.color.mainColor)
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, context.resources.displayMetrics)
    }
    override fun onDraw(canvas: Canvas?) {
        faceList?.let {
            Log.d("测试","在花花：${it.size}")
            for (face in it){
                canvas?.drawRect(face, mPaint)
            }
        }
        super.onDraw(canvas)
    }

    fun updateFaceList(faceList:ArrayList<RectF>){
        this.faceList = faceList
        invalidate()
    }


    private var mRatioWidth = 0
    private var mRatioHeight = 0

    fun setAspectRatio(width: Int, height: Int) {
        require(!(width < 0 || height < 0)) { "Size cannot be negative." }
        mRatioWidth = width
        mRatioHeight = height
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var width = MeasureSpec.getSize(widthMeasureSpec)
        var height = MeasureSpec.getSize(heightMeasureSpec)
        if (0 != mRatioWidth || 0 != mRatioHeight) {
            Log.d("测试","debug")
            width = mRatioWidth
            height = mRatioHeight
        }
        setMeasuredDimension(width, height)
    }
}