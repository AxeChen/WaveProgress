package com.mg.axe.waveprogress;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
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
    private static final int DEFAULT_RADIUS = 100;

    /**
     * 默认波峰和波谷的高度
     */
    private static final int DEFAULT_WAVE_HEIGHT = 5;

    /**
     * 默认的最大的进度
     */
    private static final int DEFAULT_MAX_PROGRESS = 100;

    /**
     * 默认边框宽度
     */
    private static final int DEFAULT_BORDER_WIDTH = 2;

    /**
     * 默认的进度字体大小
     */
    private static final int DEFAULT_TEXT_SIZE = 16;

    //进度
    private int mProgress;
    //半径
    private int mRadius = DEFAULT_RADIUS;
    //进度条的高度
    private int mProgressHeight;
    //文字的大小
    private int mTextSize;
    //波高
    private int mWaveHeight;
    //文字颜色
    private int mTextColor;
    //波浪的颜色
    private int mWaveColor;
    //圆形边框的颜色
    private int mBorderColor;
    //圆形边框的宽度
    private int borderWidth;
    //是否隐藏进度文字
    private boolean isHideProgressText = false;
    //进度条的贝塞尔曲线
    private Path mBerzierPath;
    //用于裁剪的Path
    private Path mCirclePath;
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

    public WaveProgressView(Context context) {
        this(context, null);
    }

    public WaveProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttrs(attrs);
        initPaint();
        initPath();
    }

    private void getAttrs(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.WaveProgressView);
        mRadius = ta.getDimensionPixelSize(R.styleable.WaveProgressView_radius, DEFAULT_RADIUS);
        mProgressHeight = mRadius * 2;
        mTextColor = ta.getColor(R.styleable.WaveProgressView_textColor, Color.BLACK);
        mWaveColor = ta.getColor(R.styleable.WaveProgressView_waveColor, Color.RED);
        mBorderColor = ta.getColor(R.styleable.WaveProgressView_borderColor, Color.RED);
        borderWidth = ta.getDimensionPixelOffset(R.styleable.WaveProgressView_borderWidth, dp2px(DEFAULT_BORDER_WIDTH));
        mTextSize = ta.getDimensionPixelSize(R.styleable.WaveProgressView_textSize, sp2px(DEFAULT_TEXT_SIZE));
        mWaveHeight = ta.getDimensionPixelSize(R.styleable.WaveProgressView_waveHeight, dp2px(DEFAULT_WAVE_HEIGHT));
        mProgress = ta.getInteger(R.styleable.WaveProgressView_progress, 0);
        isHideProgressText = ta.getBoolean(R.styleable.WaveProgressView_hideText, false);
        ta.recycle();
    }

    private void initPath() {
        mBerzierPath = new Path();
        mCirclePath = new Path();
        mCirclePath.addCircle(mRadius, mRadius, mRadius, Path.Direction.CCW);
    }

    private void initPaint() {
        mWavePaint = new Paint();
        mWavePaint.setColor(mWaveColor);
        mWavePaint.setStyle(Paint.Style.FILL);
        mWavePaint.setAntiAlias(true);

        mCirclePaint = new Paint();
        mCirclePaint.setStyle(Paint.Style.STROKE);// 空心画笔
        mCirclePaint.setColor(mBorderColor);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStrokeWidth(borderWidth);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextRect = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //圆形的进度条，正好是正方形的内切圆(这边暂时没有考虑padding的影响)
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
        canvas.clipPath(mCirclePath);
        canvas.drawPath(mBerzierPath, mWavePaint);

        //画圆
        canvas.drawCircle(mRadius, mRadius, mRadius, mCirclePaint);

        //开启属性动画使波浪浪起来(这里只需要启动一次)
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
        canvas.drawText(progress, mRadius - mTextRect.width() / 2,
                mRadius + mTextRect.height() / 2, mTextPaint);
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

    /**
     * 获取当前进度
     *
     * @return
     */
    public int getProgress() {
        return mProgress;
    }

    //dp to px
    protected int dp2px(int dpval) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpval, getResources().getDisplayMetrics());
    }

    //sp to px
    protected int sp2px(int dpval) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dpval, getResources().getDisplayMetrics());
    }
}
