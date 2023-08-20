package com.example.collageapp.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class ShowView : View {

    private val colorPaint = Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
    }

    private val bitmapPaint = Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
    }


    private val matrix = Matrix()


    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    fun setData() {

    }



    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) return
    }

}