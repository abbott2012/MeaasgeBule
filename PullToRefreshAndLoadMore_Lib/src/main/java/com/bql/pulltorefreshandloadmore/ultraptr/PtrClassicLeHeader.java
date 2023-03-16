package com.bql.pulltorefreshandloadmore.ultraptr;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.bql.pulltorefreshandloadmore.R;
import com.bql.pulltorefreshandloadmore.ultraptr.indicator.PtrIndicator;

/**
 * ClassName: PtrClassicLeHeader <br>
 * Description: 乐摇Go刷新头部<br>
 * Author: Cyarie <br>
 * Created: 2016/11/8 18:39 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public class PtrClassicLeHeader extends FrameLayout implements PtrUIHandler {

    private BounceLoadingView mBounceLoadingView;

    public PtrClassicLeHeader(Context context) {
        super(context);
        initViews();
    }

    public PtrClassicLeHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public PtrClassicLeHeader(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews();
    }

    protected void initViews() {
        View header = LayoutInflater.from(getContext()).inflate(R.layout.le_yao_go_refresh_header, this);
        mBounceLoadingView = (BounceLoadingView) header.findViewById(R.id.view_le_refresh);
        mBounceLoadingView.addBitmap(R.drawable.refesh_icon);
        mBounceLoadingView.setDuration(800);
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mBounceLoadingView != null) {
            mBounceLoadingView.stop();
        }
    }

    @Override
    public void onUIReset(PtrFrameLayout frame) {
        if (mBounceLoadingView != null) {
            mBounceLoadingView.reset();
        }
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        if (mBounceLoadingView != null) {
            mBounceLoadingView.reset();
        }
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        mBounceLoadingView.start();
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        if (mBounceLoadingView != null) {
            mBounceLoadingView.stop();
        }
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
        final int mOffsetToRefresh = frame.getOffsetToRefresh();
        final int currentPos = ptrIndicator.getCurrentPosY();
        final int lastPos = ptrIndicator.getLastPosY();
//        CLog.e("mOffsetToRefresh=" + mOffsetToRefresh + "----currentPos=" + currentPos + "------lastPos=" + lastPos);
        //下拉刷新
        if (currentPos < mOffsetToRefresh && lastPos >= mOffsetToRefresh) {
            if (isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE) {
                //                crossRotateLineFromBottomUnderTouch(frame);
                //                if (mRotateView != null) {
                //                    mRotateView.clearAnimation();
                //                    mRotateView.startAnimation(mReverseFlipAnimation);
                //                }
            }
        }

        //释放刷新
        else if (currentPos > mOffsetToRefresh && lastPos <= mOffsetToRefresh) {

            if (isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE) {
                //                crossRotateLineFromTopUnderTouch(frame);
                //                if (mRotateView != null) {
                //                    mRotateView.clearAnimation();
                //                    mRotateView.startAnimation(mFlipAnimation);
                //                }
            }
        }
    }
}
