package com.guoji.mobile.cocobee.callback;

import android.content.Context;
import android.graphics.Color;


import com.lzy.okgo.request.BaseRequest;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * ClassName: DialogCallback <br>
 * Description: 对话框Callback<br>
 * Author: Cyarie <br>
 * Created: 2016/7/20 14:44 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public abstract class DialogCallback<T> extends JsonCallback<T> {

    private SweetAlertDialog dialog;//进度对话框


    public DialogCallback(Context context, String loadingText) {
        super(context);
        dialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitleText(loadingText);
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
    public void onAfter(T t, Exception e) {
        super.onAfter(t, e);
        //网络请求结束后关闭对话框
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

}
