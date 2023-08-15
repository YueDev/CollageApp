package com.example.collageapp.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.example.collageapp.bean.SegmentResult

class SegmentView : View {

    private val colorPaint = Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
    }

    private val bitmapPaint = Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
    }


    private var segmentResult: SegmentResult? = null

    private val matrix = Matrix()

    private val colorMatrix = ColorMatrix()

    //0绘制people  1绘制hear
    var drawType = 0
        set(value) {
            field = value
            invalidate()
        }


    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    fun setData(result: SegmentResult) {
        post {
            val srcRect = RectF(
                0f,
                0f,
                result.srcBitmap.width.toFloat(),
                result.srcBitmap.height.toFloat()
            )
            val dstRect = RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
            matrix.reset()
            matrix.setRectToRect(srcRect, dstRect, Matrix.ScaleToFit.CENTER)
            segmentResult = result
            invalidate()
        }
    }

    fun setRGB(r: Float, g: Float, b: Float) {
        val array = floatArrayOf(
            r, 0f, 0f, 0f, 0f,
            0f, g, 0f, 0f, 0f,
            0f, 0f, b, 0f, 0f,
            0f, 0f, 0f, 0.5f, 0f,
        )
        colorMatrix.set(array)
        invalidate()
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) return
        segmentResult?.let {
            if (drawType == 0) {
                //draw people
                canvas.drawBitmap(it.peopleBitmap, matrix, bitmapPaint)
            } else if (drawType == 1) {
                //draw bg
                canvas.drawBitmap(it.srcBitmap, matrix, bitmapPaint)
                //draw hear
                val colorMatrixColorFilter = ColorMatrixColorFilter(colorMatrix)
                colorPaint.setColorFilter(colorMatrixColorFilter)
                canvas.drawBitmap(it.hearBitmap, matrix, colorPaint)
            }
        }
    }

}