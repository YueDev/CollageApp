package com.example.collageapp.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.NinePatchDrawable
import android.util.AttributeSet
import android.view.View
import kotlin.math.max

class NinePatchView : View {


    private val bitmapPaint = Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
    }

    private val bitmapMatrix = Matrix()
    private var bitmap: Bitmap? = null

    private var ninePatchDrawable: NinePatchDrawable? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    fun setNinePatch(ninePatchDrawable: NinePatchDrawable) {
        this.ninePatchDrawable = ninePatchDrawable
        invalidate()
    }
    fun setBitmap(bitmap: Bitmap) {
        post {
            this.bitmap = bitmap
            val viewW = measuredWidth.toFloat()
            val viewH = measuredHeight.toFloat()
            val bitmapWidth = bitmap.width.toFloat()
            val bitmapHeight = bitmap.height.toFloat()

            val scaleX: Float = viewW / bitmapWidth
            val scaleY: Float = viewH / bitmapHeight

            val bitmapScale = scaleX.coerceAtMost(scaleY)
            val scaledWidth: Float = bitmapWidth * bitmapScale
            val scaledHeight: Float = bitmapHeight * bitmapScale

            val dx = (viewW - scaledWidth) / 2.0f
            val dy = (viewH - scaledHeight) / 2.0f
            bitmapMatrix.reset()
            bitmapMatrix.setScale(bitmapScale, bitmapScale)
            bitmapMatrix.postTranslate(dx, dy)
            invalidate()
        }
    }



    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        bitmap?.let { bm ->
            ninePatchDrawable?.also { nine ->
                drawNinePatch(canvas, bm, nine, bitmapMatrix, bitmapPaint)
            } ?: run {
                canvas.drawBitmap(bm, bitmapMatrix, bitmapPaint)
            }
        }
    }

    private fun drawNinePatch(
        canvas: Canvas,
        bitmap: Bitmap,
        drawable: NinePatchDrawable,
        matrix: Matrix,
        paint: Paint
    ) {

        val bitmapWidth = bitmap.getWidth()
        val bitmapHeight = bitmap.getHeight()
        val initSaveCount = canvas.save()
        //先把画布映射到bitmap大小
        canvas.concat(matrix)

        //计算缩放
        val bitmapSize = max(bitmapWidth.toDouble(), bitmapHeight.toDouble()).toFloat()
        val scale: Float = max(
            bitmapSize / drawable.getIntrinsicWidth(), bitmapSize / drawable.getIntrinsicWidth()
        )

        //设置缩放 获取点9的显示区域
        drawable.setBounds(
            0, 0, (bitmapWidth / scale).toInt(), (bitmapHeight / scale).toInt()
        )
        val drawablePadding = Rect()
        drawable.getPadding(drawablePadding)
        val showRect = RectF(drawable.getBounds())
        showRect.left += drawablePadding.left.toFloat()
        showRect.top += drawablePadding.top.toFloat()
        showRect.right -= drawablePadding.right.toFloat()
        showRect.bottom -= drawablePadding.bottom.toFloat()

        //计算bitmap的matrix 让bitmap fill到showRect里边
        //这段太长了 不用看 gpt写的
        val showRectWidth: Float = showRect.width()
        val showRectHeight: Float = showRect.height()
        val scaleX = showRectWidth / bitmapWidth
        val scaleY = showRectHeight / bitmapHeight
        val bitmapScale = max(scaleX.toDouble(), scaleY.toDouble()).toFloat()
        val scaledWidth = bitmapWidth * bitmapScale
        val scaledHeight = bitmapHeight * bitmapScale
        val dx = (showRectWidth - scaledWidth) / 2.0f
        val dy = (showRectHeight - scaledHeight) / 2.0f
        val bitmapMatrix = Matrix()
        bitmapMatrix.setScale(bitmapScale, bitmapScale)
        bitmapMatrix.postTranslate(showRect.left + dx, showRect.top + dy)

        //开始绘制
        canvas.scale(scale, scale)

        //绘制背景色和bitmap
        val clipSaveCount = canvas.save()
        canvas.clipRect(showRect)
        canvas.drawColor(Color.RED)
        canvas.drawBitmap(bitmap, bitmapMatrix, paint)
        canvas.restoreToCount(clipSaveCount)
        drawable.setFilterBitmap(true)
        drawable.getPaint().isAntiAlias = true

        //绘制点9
        drawable.draw(canvas)
        canvas.restoreToCount(initSaveCount)
    }
}