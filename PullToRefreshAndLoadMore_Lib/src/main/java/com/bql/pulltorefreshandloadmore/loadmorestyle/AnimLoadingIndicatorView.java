package com.bql.pulltorefreshandloadmore.loadmorestyle;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.View;

import com.bql.pulltorefreshandloadmore.R;
import com.bql.pulltorefreshandloadmore.loadmorestyle.indicator.BallBeatIndicator;
import com.bql.pulltorefreshandloadmore.loadmorestyle.indicator.BallClipRotateIndicator;
import com.bql.pulltorefreshandloadmore.loadmorestyle.indicator.BallClipRotateMultiIndicator;
import com.bql.pulltorefreshandloadmore.loadmorestyle.indicator.BallClipRotatePulseIndicator;
import com.bql.pulltorefreshandloadmore.loadmorestyle.indicator.BallGridBeatIndicator;
import com.bql.pulltorefreshandloadmore.loadmorestyle.indicator.BallGridPulseIndicator;
import com.bql.pulltorefreshandloadmore.loadmorestyle.indicator.BallPulseIndicator;
import com.bql.pulltorefreshandloadmore.loadmorestyle.indicator.BallPulseRiseIndicator;
import com.bql.pulltorefreshandloadmore.loadmorestyle.indicator.BallPulseSyncIndicator;
import com.bql.pulltorefreshandloadmore.loadmorestyle.indicator.BallRotateIndicator;
import com.bql.pulltorefreshandloadmore.loadmorestyle.indicator.BallScaleIndicator;
import com.bql.pulltorefreshandloadmore.loadmorestyle.indicator.BallScaleMultiIndicator;
import com.bql.pulltorefreshandloadmore.loadmorestyle.indicator.BallScaleRippleIndicator;
import com.bql.pulltorefreshandloadmore.loadmorestyle.indicator.BallScaleRippleMultiIndicator;
import com.bql.pulltorefreshandloadmore.loadmorestyle.indicator.BallSpinFadeLoaderIndicator;
import com.bql.pulltorefreshandloadmore.loadmorestyle.indicator.BallTrianglePathIndicator;
import com.bql.pulltorefreshandloadmore.loadmorestyle.indicator.BallZigZagDeflectIndicator;
import com.bql.pulltorefreshandloadmore.loadmorestyle.indicator.BallZigZagIndicator;
import com.bql.pulltorefreshandloadmore.loadmorestyle.indicator.BaseIndicatorController;
import com.bql.pulltorefreshandloadmore.loadmorestyle.indicator.CubeTransitionIndicator;
import com.bql.pulltorefreshandloadmore.loadmorestyle.indicator.LineScaleIndicator;
import com.bql.pulltorefreshandloadmore.loadmorestyle.indicator.LineScalePartyIndicator;
import com.bql.pulltorefreshandloadmore.loadmorestyle.indicator.LineScalePulseOutIndicator;
import com.bql.pulltorefreshandloadmore.loadmorestyle.indicator.LineScalePulseOutRapidIndicator;
import com.bql.pulltorefreshandloadmore.loadmorestyle.indicator.LineSpinFadeLoaderIndicator;
import com.bql.pulltorefreshandloadmore.loadmorestyle.indicator.PacmanIndicator;
import com.bql.pulltorefreshandloadmore.loadmorestyle.indicator.SemiCircleSpinIndicator;
import com.bql.pulltorefreshandloadmore.loadmorestyle.indicator.SquareSpinIndicator;
import com.bql.pulltorefreshandloadmore.loadmorestyle.indicator.TriangleSkewSpinIndicator;

/**
 * ClassName: AnimLoadingIndicatorView <br>
 * Description: 加载动画的View<br>
 * Author: Cyarie <br>
 * Created: 2016/4/15 11:02 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public class AnimLoadingIndicatorView extends View {

    //indicators
    public static final int BallPulse = 0;
    public static final int BallGridPulse = 1;
    public static final int BallClipRotate = 2;
    public static final int BallClipRotatePulse = 3;
    public static final int SquareSpin = 4;
    public static final int BallClipRotateMulti = 5;
    public static final int BallPulseRise = 6;
    public static final int BallRotate = 7;
    public static final int CubeTransition = 8;
    public static final int BallZigZag = 9;
    public static final int BallZigZagDeflect = 10;
    public static final int BallTrianglePath = 11;
    public static final int BallScale = 12;
    public static final int LineScale = 13;
    public static final int LineScaleParty = 14;
    public static final int BallScaleMulti = 15;
    public static final int BallPulseSync = 16;
    public static final int BallBeat = 17;
    public static final int LineScalePulseOut = 18;
    public static final int LineScalePulseOutRapid = 19;
    public static final int BallScaleRipple = 20;
    public static final int BallScaleRippleMulti = 21;
    public static final int BallSpinFadeLoader = 22;
    public static final int LineSpinFadeLoader = 23;
    public static final int TriangleSkewSpin = 24;
    public static final int Pacman = 25;
    public static final int BallGridBeat = 26;
    public static final int SemiCircleSpin = 27;


    @IntDef(flag = true,
            value = {
                    BallPulse,
                    BallGridPulse,
                    BallClipRotate,
                    BallClipRotatePulse,
                    SquareSpin,
                    BallClipRotateMulti,
                    BallPulseRise,
                    BallRotate,
                    CubeTransition,
                    BallZigZag,
                    BallZigZagDeflect,
                    BallTrianglePath,
                    BallScale,
                    LineScale,
                    LineScaleParty,
                    BallScaleMulti,
                    BallPulseSync,
                    BallBeat,
                    LineScalePulseOut,
                    LineScalePulseOutRapid,
                    BallScaleRipple,
                    BallScaleRippleMulti,
                    BallSpinFadeLoader,
                    LineSpinFadeLoader,
                    TriangleSkewSpin,
                    Pacman,
                    BallGridBeat,
                    SemiCircleSpin
            })


    public @interface Indicator {
    }

    //Sizes (with defaults in DP)
    public static final int DEFAULT_SIZE = 45;

    //attrs
    private int mIndicatorId;
    private int mIndicatorColor;

    private Paint mPaint;

    private BaseIndicatorController mIndicatorController;

    private boolean mHasAnimation;


    public AnimLoadingIndicatorView(Context context) {
        super(context);
        init(context, null);
    }

    public AnimLoadingIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AnimLoadingIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    /**
     * 初始化
     *
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AnimLoadingIndicatorView);
        mIndicatorId = a.getInt(R.styleable.AnimLoadingIndicatorView_indicator, BallPulse);
        mIndicatorColor = a.getColor(R.styleable.AnimLoadingIndicatorView_indicator_color, Color.WHITE);
        a.recycle();
        //设置画笔paint
        mPaint = new Paint();
        mPaint.setColor(mIndicatorColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        applyIndicator();
    }

    /**
     * 设置Indicator
     *
     * @param indicatorId
     */
    public void setIndicatorId(int indicatorId) {
        mIndicatorId = indicatorId;
        applyIndicator();
    }

    /**
     * 设置Indicator Color
     *
     * @param color
     */
    public void setIndicatorColor(int color) {
        mIndicatorColor = color;
        mPaint.setColor(mIndicatorColor);
        this.invalidate();
    }


    /**
     * 使用Indicator
     */
    private void applyIndicator() {
        switch (mIndicatorId) {
            case BallPulse:
                mIndicatorController = new BallPulseIndicator();
                break;
            case BallGridPulse:
                mIndicatorController = new BallGridPulseIndicator();
                break;
            case BallClipRotate:
                mIndicatorController = new BallClipRotateIndicator();
                break;
            case BallClipRotatePulse:
                mIndicatorController = new BallClipRotatePulseIndicator();
                break;
            case SquareSpin:
                mIndicatorController = new SquareSpinIndicator();
                break;
            case BallClipRotateMulti:
                mIndicatorController = new BallClipRotateMultiIndicator();
                break;
            case BallPulseRise:
                mIndicatorController = new BallPulseRiseIndicator();
                break;
            case BallRotate:
                mIndicatorController = new BallRotateIndicator();
                break;
            case CubeTransition:
                mIndicatorController = new CubeTransitionIndicator();
                break;
            case BallZigZag:
                mIndicatorController = new BallZigZagIndicator();
                break;
            case BallZigZagDeflect:
                mIndicatorController = new BallZigZagDeflectIndicator();
                break;
            case BallTrianglePath:
                mIndicatorController = new BallTrianglePathIndicator();
                break;
            case BallScale:
                mIndicatorController = new BallScaleIndicator();
                break;
            case LineScale:
                mIndicatorController = new LineScaleIndicator();
                break;
            case LineScaleParty:
                mIndicatorController = new LineScalePartyIndicator();
                break;
            case BallScaleMulti:
                mIndicatorController = new BallScaleMultiIndicator();
                break;
            case BallPulseSync:
                mIndicatorController = new BallPulseSyncIndicator();
                break;
            case BallBeat:
                mIndicatorController = new BallBeatIndicator();
                break;
            case LineScalePulseOut:
                mIndicatorController = new LineScalePulseOutIndicator();
                break;
            case LineScalePulseOutRapid:
                mIndicatorController = new LineScalePulseOutRapidIndicator();
                break;
            case BallScaleRipple:
                mIndicatorController = new BallScaleRippleIndicator();
                break;
            case BallScaleRippleMulti:
                mIndicatorController = new BallScaleRippleMultiIndicator();
                break;
            case BallSpinFadeLoader:
                mIndicatorController = new BallSpinFadeLoaderIndicator();
                break;
            case LineSpinFadeLoader:
                mIndicatorController = new LineSpinFadeLoaderIndicator();
                break;
            case TriangleSkewSpin:
                mIndicatorController = new TriangleSkewSpinIndicator();
                break;
            case Pacman:
                mIndicatorController = new PacmanIndicator();
                break;
            case BallGridBeat:
                mIndicatorController = new BallGridBeatIndicator();
                break;
            case SemiCircleSpin:
                mIndicatorController = new SemiCircleSpinIndicator();
                break;
        }
        mIndicatorController.setTarget(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureDimension(dp2px(DEFAULT_SIZE), widthMeasureSpec);
        int height = measureDimension(dp2px(DEFAULT_SIZE), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureDimension(int defaultSize, int measureSpec) {
        int result = defaultSize;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(defaultSize, specSize);
        } else {
            result = defaultSize;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawIndicator(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!mHasAnimation) {
            mHasAnimation = true;
            applyAnimation();
        }
    }

    @Override
    public void setVisibility(int v) {
        if (getVisibility() != v) {
            super.setVisibility(v);
            if (v == GONE || v == INVISIBLE) {
                mIndicatorController.setAnimationStatus(BaseIndicatorController.AnimStatus.END);
            } else {
                mIndicatorController.setAnimationStatus(BaseIndicatorController.AnimStatus.START);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mIndicatorController.setAnimationStatus(BaseIndicatorController.AnimStatus.CANCEL);
    }


    void drawIndicator(Canvas canvas) {
        mIndicatorController.draw(canvas, mPaint);
    }

    void applyAnimation() {
        mIndicatorController.initAnimation();
    }

    private int dp2px(int dpValue) {
        return (int) getContext().getResources().getDisplayMetrics().density * dpValue;
    }
}
