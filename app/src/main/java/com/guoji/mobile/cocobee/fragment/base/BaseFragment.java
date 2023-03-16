package com.guoji.mobile.cocobee.fragment.base;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bql.utils.CheckUtils;
import com.bql.utils.EventManager;
import com.bql.utils.StatusBarUtils;
import com.bql.utils.SystemBarUtils;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.utils.KeyboardUtils;
import com.lzy.okgo.OkGo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * ClassName: BaseFragment <br>
 * Description: 基类Toolbar Fragment<br>
 * See {@link SupportFragment} Fragment管理
 * Author: Cyarie <br>
 * Created: 2016/7/15 15:57 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public abstract class BaseFragment extends SupportFragment {

    protected Toolbar mToolbar;

    private View mRootView;

    private Unbinder mUnbinder;

    protected TextView mTvTitle;//标题
    protected TextView mTvRight;
    protected TextView mTvLeft;
    // 是否可见
    private boolean mVisible = false;
    // 是否是第一次可见
    private boolean mFirstVisible = false;
    private Bundle mSavedInstanceState;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (registerEventBus())
            EventBus.getDefault().register(this);
        if (getContentViewLayoutID() == 0) {
            throw new IllegalArgumentException("You must return a right contentView layout resource Id");
        }
        mRootView = inflater.inflate(getContentViewLayoutID(), container, false);
        mUnbinder = ButterKnife.bind(this, mRootView);
        return mRootView;
    }


    @Override
    public void onPause() {
        super.onPause();
        KeyboardUtils.hideKeyboard(_mActivity);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (registerEventBus()) {
            EventBus.getDefault().unregister(this);
        }
        if (mToolbar != null) {
            mToolbar = null;
        }
        OkGo.getInstance().cancelTag(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSavedInstanceState = savedInstanceState;
    }

    /**
     * 配合viewPager使用，调用setUserVisibleHint
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (lazyLoadMode()) {
            if (getUserVisibleHint()) {
                mVisible = true;
                onVisible();
            } else {
                mVisible = false;
                onInvisible();
            }
        }
    }

    /**
     * 可见
     */
    private void onVisible() {
        lazyLode();
    }


    /**
     * 懒加载 fragment可见的时候 且为第一次可见 进行初始化
     */
    private void lazyLode() {
        if (mVisible && mFirstVisible) {
            mFirstVisible = false;
            initLazyViewsAndEvents(mSavedInstanceState);
        }
    }

    /**
     * 不可见
     */
    private void onInvisible() {

    }

    /**
     * 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged.
     *
     * @param hidden
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (lazyLoadMode()) {
            if (!hidden) {
                mVisible = true;
                onVisible();
            } else {
                mVisible = false;
                onInvisible();
            }
        }
    }

    /**
     * 懒加载 Fragment可见时进行加载 {@link #lazyLoadMode()==true}时调用 进行懒加载
     */
    protected void initLazyViewsAndEvents(@Nullable Bundle savedInstanceState) {

    }

    /**
     * 懒加载模式  如果要进行懒加载 该方法返回true 并重写{@link #initLazyViewsAndEvents(Bundle savedInstanceState)}方法 在该方法进行数据的初始化
     *
     * @return
     */
    protected boolean lazyLoadMode() {
        return false;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolbarHere();
        initToolbarHere(savedInstanceState);
    }

    //动画结束后才加载数据 没有动画 则默认在onActivityCreated()方法初始化 解决复杂页面卡顿的问题
    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        if (lazyLoadMode()) {
            mFirstVisible = true;
            mSavedInstanceState = savedInstanceState;
            lazyLode();
        } else {
            initViewsAndEvents(savedInstanceState);
        }

    }

    /**
     * init all views and add events
     */
    protected abstract void initViewsAndEvents(Bundle savedInstanceState);


    /**
     * init toolbar
     */
    protected abstract void initToolbarHere();

    /**
     * init toolbar with savedInstanceState
     */
    protected void initToolbarHere(Bundle savedInstanceState) {

    }

    /**
     * bind layout resource file
     *
     * @return id of layout resource
     */
    protected abstract int getContentViewLayoutID();


    /**
     * 是否注册EventBus  默认不注册
     *
     * @return
     */
    public boolean registerEventBus() {
        return false;
    }

    @Subscribe
    public void onEventMainThread(EventManager eventManager) {
        if (null != eventManager) {
            onHandleEvent(eventManager);
        }
    }

    /**
     * handle something when event coming
     *
     * @return
     */
    protected void onHandleEvent(EventManager eventManager) {

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
     * @param drawableResId 左边图片
     * @param right         右边文字
     */
    public void initToolbarWithLeftDrawableRightText(CharSequence title, int drawableResId, CharSequence right) {
        initToolbar(title, "", right, drawableResId, 0, 0, 0);
    }

    /**
     * 初始化ToolBar
     *
     * @param title         标题
     * @param drawableResId 左边图片
     */
    public void initToolbarWithLeftTextAndRightDrawable(CharSequence title, CharSequence left, int drawableResId) {
        initToolbar(title, left, "", 0, drawableResId, 0, 0);
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
     * @param title         标题
     * @param left          左边文件
     * @param drawableResId
     */
    public void initToolbarWithLeftTextRigthDrawable(CharSequence title, CharSequence left, int drawableResId) {
        initToolbar(title, left, "", 0, drawableResId, 0, 0);
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
     */
    public void initToolbarWithLeftText(CharSequence title, CharSequence left) {
        initToolbar(title, left, "", 0, 0, 0, 0);

    }

    /**
     * 初始化ToolBar
     *
     * @param title 标题
     * @param left  左边文字
     * @param right 右边文字
     */
    public void initToolbarWithLefRightText(CharSequence title, CharSequence left, CharSequence right) {
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
     */
    public void initToolbarTranslucent(CharSequence left, String right) {
        initToolbar("", left, right, 0, 0, R.color.transparent, 0);
    }

    public void initToolbarColor(CharSequence title, CharSequence left, String right, @ColorRes int colorRes) {
        initToolbar(title, left, right, 0, 0, colorRes, 0);
    }


    /**
     * 绑定Toolbar
     */
    public void bindToolbar() {
        mToolbar = (Toolbar) getView().findViewById(R.id.toolbar);
        if (mToolbar == null)
            throw new InflateException("You must define a toolbar in your layout");
        if (isHideToolbarLayout()) {
            mToolbar.setVisibility(View.GONE);
            return;
        } else {
            mToolbar.setVisibility(View.VISIBLE);
        }
        if (isImmersiveStatusBar() && !isHideToolbarLayout() && !isAppBarLayout()) {
            SystemBarUtils.immersiveStatusBar(_mActivity, 0);
            SystemBarUtils.setPadding(_mActivity, mToolbar);
        }

        if (isImmersiveStatusBar() && !isHideToolbarLayout() && isAppBarLayout()) {
            SystemBarUtils.immersiveStatusBar(_mActivity, 0);
            SystemBarUtils.setHeightAndPadding(_mActivity, mToolbar);
        }

        if (isStatusDarkMode()) {
            //状态栏黑色
            StatusBarUtils.setStatusBarDarkIcon(_mActivity, true);
        }
    }


    /**
     * 沉浸状态栏 针对没有toolbar的情况
     *
     * @param view
     */
    public void immersiveStatusBar(View view) {
        if (isImmersiveStatusBar() && !isHideToolbarLayout() && !isAppBarLayout()) {
            SystemBarUtils.immersiveStatusBar(_mActivity, 0);
            SystemBarUtils.setPadding(_mActivity, view);
        }
        if (isStatusDarkMode()) {
            //状态栏黑色
            StatusBarUtils.setStatusBarDarkIcon(_mActivity, true);
        }
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
        bindToolbar();
        if (mToolbar == null)
            throw new InflateException("You must define a toolbar in your layout.If you have define a Toolbar in your layout,Please ensure that your super method hasToolbarLayout() return true");
        mTvTitle = (TextView) mToolbar.findViewById(R.id.tv_toolbar_title);
        mTvLeft = (TextView) mToolbar.findViewById(R.id.tv_toolbar_left);
        mTvRight = (TextView) mToolbar.findViewById(R.id.tv_toolbar_right);
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
//        if (CheckUtils.isEmpty(mTvLeft.getText().toString())) {
//            leftContainer.setVisibility(View.GONE);
//        }
        if (inVisibleLeftDrawable()){
            invisibleLeftDrawable();
        }

        if (mTvRight != null) {
            mTvRight.setText(right);
            if (!CheckUtils.isEmpty(right)) {
                mTvRight.setEnabled(true);
                mTvRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnRightTextClick();
                    }
                });
            } else {
                mTvRight.setVisibility(View.GONE);
                mTvRight.setEnabled(false);
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
    }

    //隐藏左边的图标,默认false
    public boolean inVisibleLeftDrawable() {
        return false;
    }

    protected ImageView mIvArrow;

    /**
     * 隐藏左边返回键
     */
    public void invisibleLeftDrawable() {
        LinearLayout linearLayout = (LinearLayout) mToolbar.findViewById(R.id.ll_left_container);
        linearLayout.setVisibility(View.GONE);
    }


    /**
     * 左边文字点击事件
     */
    public void btnLeftTextClick() {

    }

    /**
     * 搜索栏点击事件
     */
    public void searchBarClick() {

    }

    /**
     * 清除按钮点击事件
     */
    public void btnClearClick() {

    }

    /**
     * 右边边文字点击事件
     */
    public void btnRightTextClick() {

    }


    /**
     * 左边图片点击事件
     */
    public void btnLeftImageClick() {

    }


    /**
     * 右边图片点击事件
     */
    public void btnRightImageClick() {

    }


    /**
     * 左边箭头文字Layout点击事件
     */
    public void leftContainerClick() {
        _mActivity.onBackPressed();
    }


    /**
     * 是否沉浸式状态栏 默认 true
     *
     * @return
     */
    public boolean isImmersiveStatusBar() {
        return true;
    }

    /**
     * 是否隐藏Toolbar 默认 false
     *
     * @return
     */
    public boolean isHideToolbarLayout() {
        return false;
    }


    /**
     * 是否是design包下的Toolbar 默认 false
     *
     * @return
     */
    public boolean isAppBarLayout() {
        return false;
    }

    /**
     * 状态栏是否黑色模式 默认false
     *
     * @return
     */
    public boolean isStatusDarkMode() {
        return false;
    }


    /**
     * 是否动态注入皮肤 默认返回false true 解决Fragment跳转不会变色的BUG
     *
     * @return
     */
    public boolean isInjectSkinView() {
        return false;
    }


}
