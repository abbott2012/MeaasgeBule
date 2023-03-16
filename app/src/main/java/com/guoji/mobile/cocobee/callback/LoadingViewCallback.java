package com.guoji.mobile.cocobee.callback;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.View;

import com.bql.statetypelayout.AnimationStateTypeLayout;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.common.ElectricVehicleApp;
import com.guoji.mobile.cocobee.utils.Utils;
import com.lzy.okgo.request.BaseRequest;

import okhttp3.Call;
import okhttp3.Response;


/**
 * ClassName: LoadingViewCallback <br>
 * Description: 结合下拉刷新操作的回调<br>
 * Author: Cyarie <br>
 * Created: 2016/7/21 17:29 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public abstract class LoadingViewCallback<T> extends JsonCallback<T> {

    private AnimationStateTypeLayout mStateTypeLayout;//不同状态类型动画效果的RelativeLayout

    //    private String loadingContent;//加载文字

    //    private Drawable emptyDrawable;//空 图片

    private Drawable errorNetDrawable;//网络 错误 图片

    private Drawable errorServerDrawable;//服务器 错误 图片

    private String emptyTitle;//空标题

    private String emptyContent;//空内容

    //    private String errorTitle;//错误标题

    private String errorContent;//错误 内容

    private String errorLeftButtonText;//错误左边按钮文字

    private String errorRightButtonText;//错误右边按钮文字

    private String emptyButtonText;//空按钮文字
    //
    //    private String errorServerText;//服务器异常按钮文字

    private View.OnClickListener mOnLeftClickListener;//左边按钮监听

    private View.OnClickListener mOnRightClickListener;//右边按钮监听

    public boolean isPullToRefresh;//是否下拉刷新操作

    //Activity传入的参数
    private Context mContext;

    //Fragment传入的参数
    private View mView;


    /**
     * 构造函数 适用于Activity
     *
     * @param context
     * @param isPullToRefresh 是否刷新操作
     */
    public LoadingViewCallback(Context context, boolean isPullToRefresh) {
        super(context);
        this.isPullToRefresh = isPullToRefresh;
        this.mContext = context;
    }


    /**
     * 构造函数 适用于Fragment
     *
     * @param view            The root view for the fragment's layout
     * @param isPullToRefresh 是否刷新操作
     */
    public LoadingViewCallback(Context context, View view, boolean isPullToRefresh) {
        super(context);
        this.isPullToRefresh = isPullToRefresh;
        this.mView = view;
    }


    @Override
    public void onBefore(BaseRequest request) {
        super.onBefore(request);
        errorNetDrawable = ElectricVehicleApp.getApp().getResources().getDrawable(R.drawable.no_network);
        errorServerDrawable = ElectricVehicleApp.getApp().getResources().getDrawable(R.drawable.no_network);
        errorContent = ElectricVehicleApp.getApp().getString(R.string.net_error);
        errorLeftButtonText = ElectricVehicleApp.getApp().getString(R.string.re_load);
        emptyButtonText = emptyBtnText();
        errorRightButtonText = ElectricVehicleApp.getApp().getString(R.string.errorButtonRightPlaceHolder);

        mOnLeftClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                errorLeftOrEmptyBtnClick(view);
            }
        };

        mOnRightClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                errorRightBtnClick();
            }
        };
        initStateLayout();
        if (!isPullToRefresh) {
            if (isShowLoading()) {
                showLoading();
            }
        }
    }


    @Override
    public void onSuccess(T t, Call call, Response response) {
        if (isShowContent())
            showContent();
        onResultSuccess(t, response, this);
    }

    public boolean isShowLoading() {
        return true;
    }

    public boolean isShowContent() {
        return true;
    }

    /**
     * 加载失败 网络未连接 服务器异常等情况 点击按钮后的操作  /空数据刷新操作
     */
    public abstract void errorLeftOrEmptyBtnClick(View v);

    /**
     * 错误视图右边按钮点击
     */
    public void errorRightBtnClick() {
        Intent intent = null;
        //判断手机系统的版本  即API大于10 就是3.0或以上版本
        if (android.os.Build.VERSION.SDK_INT > 10) {
            intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
        } else {
            intent = new Intent();
            ComponentName component = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
            intent.setComponent(component);
            intent.setAction("android.intent.action.VIEW");
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ElectricVehicleApp.getApp().startActivity(intent);
    }

    /**
     * 带AnimationStateTypeLayout的回调
     */
    public abstract void onResultSuccess(T t, @Nullable Response response, LoadingViewCallback callback);


    @Override
    public void onError(Call call, Response response, Exception e) {
        super.onError(call, response, e);
        if (response != null && response.code() >= 400 && response.code() <= 599) {
            if (!isPullToRefresh)
                showServerError(ElectricVehicleApp.getApp().getString(R.string.server_error));
            return;
        }

        if (!isPullToRefresh) {
            if (e != null) {
                if (Utils.isNetConnected()) {
//                    showServerError(ElectricVehicleApp.getApp().getString(R.string.server_error));
                    if (e.getMessage().startsWith("Failed to connect")) {
                        showServerError(ElectricVehicleApp.getApp().getString(R.string.server_error));
                    } else {
                        showServerError(e.getMessage());
                    }
                } else {
                    showNetError();
                }

            }
        }
    }


    /**
     * 初始化状态布局
     */

    public void initStateLayout() {
        if (mStateTypeLayout == null) {
            if (mView != null) {
                mStateTypeLayout = (AnimationStateTypeLayout) mView.findViewById(R.id.stateLayout);
            } else if (mContext != null) {
                mStateTypeLayout = (AnimationStateTypeLayout) ((Activity) mContext).findViewById(R.id.stateLayout);
            }
            if (mStateTypeLayout == null) {
                throw new NullPointerException("Do you have defined a AnimationStateTypeLayout layout in your xml?");
            }

        }
    }


    /**
     * 显示加载视图
     */
    public void showLoading() {
        if (mStateTypeLayout != null) {
            mStateTypeLayout.showLoading(/*loadingContent*/);
        }
    }


    /**
     * 显示空视图
     */
    public void showEmpty() {
        if (mStateTypeLayout != null) {
            mStateTypeLayout.showEmpty(emptyDrawable(), null, emptyContent(), showEmptyButton() ? emptyButtonText : null, mOnLeftClickListener);
        }
    }


    /**
     * 显示空视图
     */
    public void showEmpty(String text) {
        if (mStateTypeLayout != null) {
            mStateTypeLayout.showEmpty(emptyDrawable(), null, text, showEmptyButton() ? emptyButtonText : null, mOnLeftClickListener);
        }
    }


    /**
     * 设置错误视图
     */
    //    public void showError() {
    //        if (mStateTypeLayout != null) {
    //            mStateTypeLayout.showError(errorDrawable, null, errorContent, errorLeftButtonText, errorRightButtonText, mOnLeftClickListener, mOnRightClickListener);
    //        }
    //    }

    /**
     * 设置网络错误视图
     */
    public void showNetError() {
        if (mStateTypeLayout != null) {
            mStateTypeLayout.showError(errorNetDrawable, null, ElectricVehicleApp.getApp().getString(R.string.net_error), errorLeftButtonText, null, mOnLeftClickListener, null);
        }
    }


    /**
     * 设置服务器错误视图
     */
    public void showServerError(String s) {
        if (mStateTypeLayout != null) {
            mStateTypeLayout.showError(errorServerDrawable, null, s, errorLeftButtonText, null, mOnLeftClickListener, null);
        }
    }

    /**
     * 显示内容
     */
    public void showContent() {
        if (mStateTypeLayout != null) {
            mStateTypeLayout.showContent();
        }
    }

    /**
     * 隐藏所有
     */
    public void hideAllView() {
        if (mStateTypeLayout != null) {
            mStateTypeLayout.hideEmptyView();
            mStateTypeLayout.hideErrorView();
            mStateTypeLayout.hideLoadingView();
            mStateTypeLayout.hideContentView();
        }
    }

    /**
     * 是否显示空视图按钮  默认不显示
     *
     * @return
     */
    public boolean showEmptyButton() {
        return false;
    }

    /**
     * 是否显示服务器异常刷新按钮  默认显示
     *
     * @return
     */
    public boolean showErrorButton() {
        return true;
    }


    /**
     * 空图片 若需另行设置 请重写该方法
     *
     * @return
     */
    public Drawable emptyDrawable() {
        return ElectricVehicleApp.getApp().getResources().getDrawable(R.drawable.common_content_empty);
    }

    /**
     * 空内容文本 若需另行设置 请重写该方法
     *
     * @return
     */
    public String emptyContent() {
        return ElectricVehicleApp.getApp().getString(R.string.emptyContentPlaceholder);
    }

    public String emptyBtnText() {
        return ElectricVehicleApp.getApp().getString(R.string.re_load);
    }

}
