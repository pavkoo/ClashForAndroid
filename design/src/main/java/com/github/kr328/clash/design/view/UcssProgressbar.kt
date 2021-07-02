package com.github.kr328.clash.design.view

import android.animation.*
import android.graphics.RectF
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator

class UcssProgressbar @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var STROKE = dip2px(3f)
    private val bgColor = Color.parseColor("#66ffffff")
    private val mPaint: Paint = Paint()
    private val rectF: RectF
    private val mAnim: AnimatorSet
    private var start = false

    @Synchronized
    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val realWidth = width.coerceAtMost(height)
        rectF[STROKE / 2.toFloat(), STROKE / 2.toFloat(), (realWidth - STROKE / 2).toFloat()] =
            (realWidth - STROKE / 2).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mPaint.color = bgColor
        canvas.drawArc(rectF, 0f, 360f, false, mPaint)
        mPaint.color = Color.WHITE
        canvas.drawArc(rectF, 0f, 125f, false, mPaint)
    }

    fun setStart(value: Boolean) {
        start = value
        visibility = if (start) {
            mAnim.start()
            VISIBLE
        } else {
            mAnim.cancel()
            GONE
        }
    }

    fun dip2px(dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    init {
        mPaint.style = Paint.Style.STROKE
        mPaint.isAntiAlias = true
        mPaint.isDither = true
        mPaint.strokeWidth = STROKE.toFloat()
        rectF = RectF()
        mAnim = AnimatorSet()
        visibility = GONE
        val rotate = ObjectAnimator.ofFloat(this, "rotation", 0f, 360f)
        rotate.repeatCount = ValueAnimator.INFINITE
        mAnim.playTogether(rotate)
        mAnim.duration = 700
        mAnim.interpolator = LinearInterpolator()
    }
}