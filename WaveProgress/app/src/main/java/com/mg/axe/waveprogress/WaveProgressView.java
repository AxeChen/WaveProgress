package com.mg.axe.waveprogress;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by Chen on 2017/6/4.
 * 水波纹进度条
 */

public class WaveProgressView extends View {

    /**
     * 默认波长
     */
    private static final int DEFAULT_PROGRESS_HEIGHT = 300;

    /**
     * 默认波峰和波谷的高度
     */
    private static final int DEFAULT_WAVE_HEIGHT = 20;

    /**
     * 默认的最大的进度
     */
    private static final int DEFAULT_MAX_PROGRESS = 100;

    private int mProgress;
    //进度条的高度
    private int mProgressHeight = DEFAULT_PROGRESS_HEIGHT;
    //波高
    private int mWaveHeight = DEFAULT_WAVE_HEIGHT;

    //进度条的赛贝尔曲线
    private Path mBerzierPath;
    //用于裁剪的Path
    private Path path;

    // 画圆的画笔
    private Paint mCirclePaint;
    // 画文字的笔
    private Paint mTextPaint;
    // 画波浪的笔
    private Paint mWavePaint;

    // 文字的区域
    private Rect mTextRect;

    private ValueAnimator mAnimator;
    private int mMoveX = 0;
    private boolean isStartAnimation = false;

    private boolean isHideProgressText = false;

    public WaveProgressView(Context context) {
        this(context, null);
    }

    public WaveProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
        initPath();
    }

    private void initPath() {
        mBerzierPath = new Path();
        path = new Path();
        path.addCircle(mProgressHeight / 2, mProgressHeight / 2, mProgressHeight / 2, Path.Direction.CCW);
    }

    private void initPaint() {
        mWavePaint = new Paint();
        mWavePaint.setColor(Color.RED);
        mWavePaint.setStyle(Paint.Style.FILL);
        mWavePaint.setAntiAlias(true);

        mCirclePaint = new Paint();
        mCirclePaint.setStyle(Paint.Style.STROKE);// 空心画笔
        mCirclePaint.setColor(Color.RED);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStrokeWidth(5);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(50);
        mTextRect = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //圆形的进度条，正好是正方形的内切圆
        setMeasuredDimension(mProgressHeight, mProgressHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mBerzierPath.reset();
        //画曲线
        mBerzierPath.moveTo(-mProgressHeight + mMoveX, getWaveY());
        for (int i = -mProgressHeight; i < mProgressHeight * 3; i += mProgressHeight) {
            //圆内最长一个波长
            mBerzierPath.rQuadTo(mProgressHeight / 4, mWaveHeight, mProgressHeight / 2, 0);
            mBerzierPath.rQuadTo(mProgressHeight / 4, -mWaveHeight, mProgressHeight / 2, 0);
        }
        mBerzierPath.lineTo(mProgressHeight, mProgressHeight);
        mBerzierPath.lineTo(0, getHeight());
        mBerzierPath.close();

        //裁剪一个圆形的区域
        canvas.clipPath(path);
        canvas.drawPath(mBerzierPath, mWavePaint);

        //画圆
        canvas.drawCircle(mProgressHeight / 2, mProgressHeight / 2, mProgressHeight / 2, mCirclePaint);

        //开启属性动画使波浪浪起来
        if (!isStartAnimation) {
            isStartAnimation = true;
            startAnimation();
        }

        //画文字（画文字可不是直接drawText这么简单，要找基线去画）
        String progress = mProgress + "%";
        if (isHideProgressText) {
            progress = "";
        }
        mTextPaint.getTextBounds(progress, 0, progress.length(), mTextRect);
        canvas.drawText(progress, mProgressHeight / 2 - mTextRect.width() / 2,
                mProgressHeight / 2 + mTextRect.height() / 2, mTextPaint);
    }

    private int getWaveY() {
        float scale = mProgress * 1f / DEFAULT_MAX_PROGRESS * 1f;
        if (scale >= 1) {
            return 0;
        } else {
            int height = (int) (scale * mProgressHeight);
            return mProgressHeight - height;
        }
    }

    private void startAnimation() {
        mAnimator = ValueAnimator.ofInt(0, mProgressHeight);
        mAnimator.setDuration(2000);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mMoveX = (int) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        mAnimator.start();
    }

    /**
     * 设置进度
     *
     * @param progress
     */
    public void setProgress(int progress) {
        this.mProgress = progress;
        postInvalidate();
    }

    /**
     * 设置字体的颜色
     *
     * @param color
     */
    public void setTextColor(int color) {
        mTextPaint.setColor(color);
    }

    /**
     * 设置波浪的颜色
     *
     * @param color
     */
    public void setWaveColor(int color) {
        mWavePaint.setColor(color);
    }

    /**
     * 设置
     *
     * @param color
     */
    public void setBorderColor(int color) {
        mCirclePaint.setColor(color);
    }

    /**
     * 设置隐藏进度文字
     *
     * @param flag
     */
    public void hideProgressText(boolean flag) {
        isHideProgressText = flag;
    }

    //dp to px
    protected int dp2px(int dpval) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpval, getResources().getDisplayMetrics());
    }
}
