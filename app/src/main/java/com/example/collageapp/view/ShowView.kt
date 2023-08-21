package com.example.collageapp.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.example.collageapp.util.SizeUtil

class ShowView : View {


    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = resources.displayMetrics.density * 1
        color = Color.CYAN
    }


    private val matrix = Matrix()

    private var bitmap: Bitmap? = null
    private val rectList = mutableListOf<Rect>()

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    fun setData(bitmap: Bitmap, bounds: List<Rect>) {
        post {
            this.bitmap = bitmap
            rectList.addAll(bounds)
            val src = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
            val dst = RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
            matrix.setRectToRect(src, dst, Matrix.ScaleToFit.CENTER)
            invalidate()
        }
    }



    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) return
        bitmap?.let {
            canvas.concat(matrix)
            canvas.drawBitmap(it, 0f, 0f, paint)
            rectList.forEach { rect ->
                canvas.drawRect(rect, paint)
            }
        }
    }

}