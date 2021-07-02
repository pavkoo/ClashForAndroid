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
import android.view.animation.LinearInterpolator

class SimpleProgressbar @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val STROKE = dip2px(6f)
    private val mPaint: Paint = Paint()
    private val rectF: RectF
    private val mAnim: AnimatorSet
    private var running = false
    @Synchronized
    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val realWidth = width.coerceAtMost(height)
        rectF[STROKE/2.toFloat(), STROKE/2.toFloat(), (realWidth - STROKE/2).toFloat()] =
            (realWidth - STROKE/2).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawArc(rectF, 270f, 70f, false, mPaint)
    }

    fun setRunning(value: Boolean) {
        running = value
        if (running) {
            mAnim.start()
        } else {
            mAnim.cancel()
        }
    }

    fun dip2px(dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    companion object {
        private const val TAG = "SimpleProgressbar"
    }

    init {
        mPaint.style = Paint.Style.STROKE
        mPaint.isAntiAlias = true
        mPaint.isDither = true
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.strokeWidth = STROKE.toFloat()
        mPaint.color = Color.parseColor("#CCCCCC")
        rectF = RectF()
        mAnim = AnimatorSet()
        var alpha = ObjectAnimator.ofFloat(this, "alpha", 0.3f, 1f)
        val rotate = ObjectAnimator.ofFloat(this, "rotation", 0f, 360f)
        rotate.repeatCount = ValueAnimator.INFINITE
        mAnim.playTogether(alpha, rotate)
        mAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationCancel(animation: Animator) {
                super.onAnimationCancel(animation)
                setAlpha(0f)
            }

            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                setAlpha(0f)
            }

            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
            }
        })
        mAnim.duration = 1400
        mAnim.interpolator = LinearInterpolator()
        setAlpha(0f)
    }
}