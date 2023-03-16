package com.guoji.mobile.cocobee.activity.base;

import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.Toolbar;
import android.view.InflateException;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bql.utils.CheckUtils;
import com.bql.utils.StatusBarUtils;
import com.bql.utils.SystemBarUtils;
import com.guoji.mobile.cocobee.R;

/**
 * ClassName: BaseActivity <br>
 * Description: 基类Toolbar Activity<br>
 * Author: Cyarie <br>
 * Created: 2016/7/15 15:50 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public abstract class BaseToolbarActivity extends BaseActivity {

    protected Toolbar mToolbar;
    private TextView mMTvRight;

    /**
     * 初始化ToolBar
     *
     * @param title 标题
     * @param left  左边文字
     */
    public void initToolbarWithLeftText(CharSequence title, CharSequence left) {
        initToolbar(title, left, "", 0, 0, 0, 0);
    }

    /**
     * 初始化ToolBar
     *
     * @param title         标题
     * @param drawableResId 左边图片
     */
    public void initToolbarWithLeftDrawable(CharSequence title, int drawableResId) {
        initToolbar(title, "", "", drawableResId, 0, 0, 0);
    }

    /**
     * 初始化ToolBar
     *
     * @param title         标题
     * @param drawableResId 右边图片
     */
    public void initToolbarWithRightDrawable(CharSequence title, int drawableResId) {
        initToolbar(title, "", "", 0, drawableResId, 0, 0);
    }


    /**
     * 初始化ToolBar
     *
     * @param title 标题
     * @param right 右边文字
     */
    public void initToolbarWithRightText(CharSequence title, CharSequence right) {
        initToolbar(title, "", right, 0, 0, 0, 0);
    }

    /**
     * 初始化ToolBar
     *
     * @param title 标题
     * @param left  左边文字
     * @param right 右边文字
     */
    public void initToolbarWithAllText(CharSequence title, CharSequence left, CharSequence right) {
        initToolbar(title, left, right, 0, 0, 0, 0);
    }


    /**
     * 初始化ToolBar
     *
     * @param title 标题
     */
    public void initToolbar(CharSequence title) {
        initToolbar(title, "", "", 0, 0, 0, 0);
    }

    /**
     * 初始化ToolBar
     *
     * @param title           标题
     * @param left            左边文字
     * @param right           右边文字
     * @param leftDrawableID  左边图片
     * @param rightDrawableID 右边图片
     */
    public void initToolbar(CharSequence title, CharSequence left, CharSequence right, @DrawableRes int leftDrawableID, @DrawableRes int rightDrawableID, @ColorRes int colorRes, @ColorInt int titleColor) {
        //状态栏与应用同色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar == null)
            throw new InflateException("You must define a toolbar in your layout");
        TextView mTvTitle = (TextView) mToolbar.findViewById(R.id.tv_toolbar_title);
        TextView mTvLeft = (TextView) mToolbar.findViewById(R.id.tv_toolbar_left);
        mMTvRight = (TextView) mToolbar.findViewById(R.id.tv_toolbar_right);
        if (mTvTitle != null)
            mTvTitle.setText(title);
        if (titleColor != 0)
            mTvTitle.setTextColor(titleColor);

        if (mTvLeft != null) {
            mTvLeft.setText(left);
            if (!CheckUtils.isEmpty(left)) {
                mTvLeft.setEnabled(true);
            } else {
                mTvLeft.setVisibility(View.GONE);
                mTvLeft.setEnabled(false);
            }

        }
        LinearLayout leftContainer = (LinearLayout) mToolbar.findViewById(R.id.ll_left_container);
        leftContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftContainerClick();
            }
        });


        if (mMTvRight != null) {
            mMTvRight.setText(right);
            if (!CheckUtils.isEmpty(right)) {
                mMTvRight.setEnabled(true);
                mMTvRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnRightTextClick();
                    }
                });
            } else {
                mMTvRight.setVisibility(View.GONE);
                mMTvRight.setEnabled(false);
            }
        }
        ImageView mIvRight = (ImageView) mToolbar.findViewById(R.id.iv_toolbar_right_img);
        if (rightDrawableID == 0) {
            mIvRight.setVisibility(View.GONE);
        } else {
            mIvRight.setVisibility(View.VISIBLE);
            mIvRight.setBackgroundResource(rightDrawableID);
            mIvRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnRightImageClick();
                }
            });
        }
        ImageView mIvLeft = (ImageView) mToolbar.findViewById(R.id.iv_toolbar_left_img);
        if (leftDrawableID == 0) {
            mIvLeft.setVisibility(View.GONE);
        } else {
            mIvLeft.setVisibility(View.VISIBLE);
            mIvLeft.setBackgroundResource(leftDrawableID);
            mIvLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnLeftImageClick();
                }
            });
        }

        if (colorRes != 0)
            mToolbar.setBackgroundResource(colorRes);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (isImmersiveStatusBar()) {
            SystemBarUtils.immersiveStatusBar(this, 0);
            SystemBarUtils.setPadding(this, mToolbar);
        }
        if (isStatusDarkMode()) {
            //状态栏黑色
            StatusBarUtils.setStatusBarDarkIcon(this, true);
        } else {
            //状态栏白色
            StatusBarUtils.setStatusBarDarkIcon(this, false);
        }
    }


    /**
     * 左边文字点击事件
     */
    public void btnLeftTextClick() {

    }


    /**
     * 左边图片点击事件
     */
    public void btnLeftImageClick() {
        finish();
    }


    /**
     * 右边图片点击事件
     */
    public void btnRightImageClick() {

    }


    /**
     * 右边边文字点击事件
     */
    public void btnRightTextClick() {

    }

    /**
     * 状态栏是否黑色,默认false
     *
     * @return
     */
    @Override
    protected boolean isStatusDarkMode() {
        return false;
    }

    /**
     * 左边箭头文字Layout点击事件
     */
    public void leftContainerClick() {
        finish();
    }


    /**
     * 是否沉浸式状态栏 默认 true
     *
     * @return
     */
    public boolean isImmersiveStatusBar() {
        return true;
    }


    public TextView getMTvRight() {
        return mMTvRight;
    }
}
