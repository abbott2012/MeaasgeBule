package com.guoji.mobile.cocobee.activity.register;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.base.BaseActivity;
import com.guoji.mobile.cocobee.common.Path;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/5/5.
 */

public class UserManagerActivity extends BaseActivity {
    @BindView(R.id.ll_back_pro)
    ImageView mLlBackPro;
    @BindView(R.id.webview)
    WebView mWebview;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_user_manager;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        initWebView();
    }

    private void initWebView() {
        mWebview.getSettings().setJavaScriptEnabled(true);//开启JavaScript支持
//        String url = "file:///android_asset/agreement.html";
        String url = Path.SERVER_BASIC_PATH + "agreement.html";
        loadLocalHtml(url);
    }

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    public void loadLocalHtml(String url) {
        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //重写此方法，用于捕捉页面上的跳转链接
                if ("http://start/".equals(url)) {
                    //在html代码中的按钮跳转地址需要同此地址一致
                    finish();
                }
                return true;
            }
        });
        mWebview.loadUrl(url);
    }

    @OnClick(R.id.ll_back_pro)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_back_pro:
                finish();
                break;
        }
    }
}
