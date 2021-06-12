package com.github.kr328.clash.common.ucss.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class SimpleProgressbar extends View {
    private static final String TAG = "SimpleProgressbar";
    private final int STROKE = dip2px(10);

    private final Paint mPaint;
    private final RectF rectF;
    private final AnimatorSet mAnim;
    private boolean running = false;

    public SimpleProgressbar(Context context) {
        this(context, null);
    }

    public SimpleProgressbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleProgressbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(STROKE);
        mPaint.setColor(Color.parseColor("#eee"));
        rectF = new RectF();

        mAnim = new AnimatorSet();
        ObjectAnimator alpha = ObjectAnimator.ofFloat(this, "alpha", 0.3f, 1);
        ObjectAnimator rotate = ObjectAnimator.ofFloat(this, "rotation", 0, 360);
        rotate.setRepeatCount(ValueAnimator.INFINITE);
        mAnim.playTogether(alpha, rotate);

        mAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                SimpleProgressbar.this.setAlpha(0);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                SimpleProgressbar.this.setAlpha(0);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });

        setAlpha(0);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec,
                                          int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int realWidth = Math.min(width, height);

        rectF.set(STROKE, STROKE, realWidth - STROKE, realWidth - STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(rectF, 0, 45, false, mPaint);
    }

    public void setRunning(boolean value) {
        this.running = value;
        if (this.running) {
            mAnim.start();
        } else {
            mAnim.cancel();
        }
    }

    public int dip2px(float dpValue) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
