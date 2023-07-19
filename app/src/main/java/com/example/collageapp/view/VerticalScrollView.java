package com.example.collageapp.view;

import android.animation.TimeAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * Created by Yue on 2022/11/3.
 */
public class VerticalScrollView extends View {

    private Bitmap mBitmap;
    private final Paint mPaint = new Paint();

    private boolean mIsCanDraw;

    private boolean mIsAnimation;

    //静止状态下的图片matrix
    private Matrix mMatrix;

    //动画状态下的两个rect
    private final RectF mRect1 = new RectF();
    private final RectF mRect2 = new RectF();

    private TimeAnimator mAnimator;

    //动画当前的y，即图片的上边距
    private long mCurrentY = 0;
    private long mTotalY = 0;
    //动画用，图片缩放的比例
    private float mScale = 1.0f;

    public VerticalScrollView(Context context) {
        super(context);
        init();
    }

    public VerticalScrollView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VerticalScrollView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!mIsCanDraw) return;
        if (mBitmap == null) return;

        if (mIsAnimation) {
            canvas.save();
            canvas.translate(0f, -mCurrentY);
            canvas.drawBitmap(mBitmap, null, mRect1, mPaint);
            canvas.drawBitmap(mBitmap, null, mRect2, mPaint);
            canvas.restore();
        } else {
            canvas.drawBitmap(mBitmap, mMatrix, mPaint);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimator();
    }


    //停止动画
    public void stopAnimator() {
        if (mAnimator != null) mAnimator.cancel();
    }

    //暂停动画
    public void pauseAnimator() {
        if (mAnimator != null) mAnimator.pause();
    }

    //开始动画
    public void startAnimator() {
        if (mAnimator != null) mAnimator.start();
    }


    public void setData(Bitmap bitmap) {
        if (bitmap == null) return;
        post(() -> {
            mBitmap = bitmap;
            int w = getMeasuredWidth();
            int h = getMeasuredHeight();
            //如果bitmap比view宽的话，不做动画，直接静止显示图片
            float bitmapRatio = (float) mBitmap.getWidth() / mBitmap.getHeight();
            float viewRatio = (float) w / h;
            if (bitmapRatio >= viewRatio) {
                mIsAnimation = false;

                float centerX = w / 2f;
                float centerY = h / 2f;
                float dx = centerX - mBitmap.getWidth() / 2f;
                float dy = centerY - mBitmap.getHeight() / 2f;

                float scale = (float) h / mBitmap.getHeight();

                mMatrix = new Matrix();
                mMatrix.postTranslate(dx, dy);
                mMatrix.postScale(scale, scale, centerX, centerY);
                mIsCanDraw = true;
                invalidate();
            } else {
                mIsCanDraw = true;
                mIsAnimation = true;
                mScale = (float) getMeasuredWidth() / mBitmap.getWidth();
                mCurrentY = 0;
                mAnimator = new TimeAnimator();
                mRect1.set(0f, 0f, getMeasuredWidth(), mBitmap.getHeight() * mScale);
                mRect2.set(0f, mBitmap.getHeight() * mScale, getMeasuredWidth(), mBitmap.getHeight() * mScale * 2);
                int rem = (int) (mBitmap.getHeight() * mScale);
                mAnimator.setTimeListener((animation, totalTime, deltaTime) -> {
                    //时间是毫秒  pause会使totalTime归0，所以这里记录下totalY
                    mTotalY += deltaTime;
                    mCurrentY = mTotalY / 32;
                    mCurrentY %= rem;
                    postInvalidate();
                });
                mAnimator.start();

            }
        });

    }


}
