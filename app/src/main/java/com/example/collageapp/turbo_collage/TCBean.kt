package com.example.collageapp.turbo_collage

//各种数据bean
data class TCBitmap(val uuid: String, val width: Int, val height: Int)

enum class TCJoin {
    TCLeftRightJoin,
    TCUpDownJoin
}

data class TCRectF(val left: Float, val top: Float, val right: Float, val bottom: Float)


data class TCRect(val left: Double, val top: Double, val width: Double, val height: Double) {

    fun getRectF(): TCRectF {
        return TCRectF(left.toFloat(), top.toFloat(), (left + width).toFloat(), (top + height).toFloat())
    }
}

class TCCollageItem(var uuid: String = "", var ratioRect: TCRect) {

    fun isEmptyUUID(): Boolean {
        return uuid.trim().isEmpty()
    }


    fun getRatioMaxBound(canvasWidth: Double, canvasHeight: Double): Double {
        val w = canvasWidth * this.ratioRect.width
        val h = this.ratioRect.height * canvasHeight
        return w.coerceAtLeast(h)
    }
}