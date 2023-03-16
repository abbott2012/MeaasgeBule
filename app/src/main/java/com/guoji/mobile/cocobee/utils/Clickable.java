package com.guoji.mobile.cocobee.utils;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.common.ElectricVehicleApp;

public class Clickable extends ClickableSpan {
    private final View.OnClickListener mListener;

    public Clickable(View.OnClickListener l) {
        mListener = l;
    }

    /**
     * 重写父类点击事件
     */
    @Override
    public void onClick(View v) {
        mListener.onClick(v);
    }

    /**
     * 重写父类updateDrawState方法  我们可以给TextView设置字体颜色,背景颜色等等...
     */
    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(ElectricVehicleApp.getApp().getResources().getColor(R.color.color_3270ed));
    }
}