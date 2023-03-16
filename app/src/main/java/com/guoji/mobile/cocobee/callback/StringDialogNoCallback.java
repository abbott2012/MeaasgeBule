package com.guoji.mobile.cocobee.callback;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.Nullable;

import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by _H_JY on 2017/3/12.
 */
public abstract class StringDialogNoCallback extends StringCallback {

    private SweetAlertDialog dialog;

    public StringDialogNoCallback(Activity activity, String tip) {
        dialog = new SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitleText(tip);
        dialog.getProgressHelper().setBarColor(Color.parseColor("#4169e1"));
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void onBefore(BaseRequest request) {
        super.onBefore(request);
        //网络请求前显示对话框
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    @Override
    public void onAfter(@Nullable String s, @Nullable Exception e) {
        super.onAfter(s, e);
        //网络请求结束后关闭对话框
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
