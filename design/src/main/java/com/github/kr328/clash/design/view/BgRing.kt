package com.github.kr328.clash.design.view

import android.animation.*
import android.graphics.RectF
import android.view.View.MeasureSpec
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class BgRing @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val mPaint: Paint = Paint()
    private val rectF: RectF
    @Synchronized
    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val realWidth = width.coerceAtMost(height)
        rectF[0.toFloat(), 0.toFloat(), realWidth.toFloat()] =
            realWidth.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawArc(rectF, 0f, 360f, true, mPaint)
    }

    init {
        mPaint.style = Paint.Style.FILL_AND_STROKE
        mPaint.isAntiAlias = true
        mPaint.isDither = true
        mPaint.strokeCap = Paint.Cap.SQUARE
        mPaint.color = Color.parseColor("#252525")
        rectF = RectF()
    }
}