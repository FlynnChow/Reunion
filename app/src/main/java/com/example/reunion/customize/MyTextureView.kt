package com.example.reunion.customize

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.TextureView


class MyTextureView @JvmOverloads constructor(context: Context,attributeSet: AttributeSet? = null,style:Int = 0):TextureView(context,attributeSet,style) {
    private var mRatioWidth = 0
    private var mRatioHeight = 0

    var currentWidth = 0
    var currentHeight = 0

    fun setAspectRatio(width: Int, height: Int) {
        require(!(width < 0 || height < 0)) { "Size cannot be negative." }
        mRatioWidth = width
        mRatioHeight = height
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        if (0 == mRatioWidth || 0 == mRatioHeight) {
            currentWidth = width
            currentHeight = height
        } else {
            if (width < height * mRatioWidth / mRatioHeight) {
                currentWidth = width
                currentHeight = width * mRatioHeight / mRatioWidth
            } else {
                currentWidth = height * mRatioWidth / mRatioHeight
                currentHeight = height
            }
//            if (width/height != mRatioWidth / mRatioHeight){
//                currentWidth = width * mRatioHeight / mRatioWidth
//                currentHeight = height
//            }
        }
        setMeasuredDimension(currentWidth, currentHeight)
    }
}