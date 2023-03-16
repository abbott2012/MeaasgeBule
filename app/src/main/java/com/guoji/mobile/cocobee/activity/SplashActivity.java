package com.guoji.mobile.cocobee.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.base.BaseActivity;
import com.guoji.mobile.cocobee.activity.guid.GuideActivity;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.view.CountDownProgress;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * ClassName: SplashActivity <br>
 * Description: 启动页面<br>
 * Author: Cyarie <br>
 * Created: 2016/10/28 14:54 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public class SplashActivity extends BaseActivity {

    @BindView(R.id.countdown_view)
    CountDownProgress mCountdownView;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.act_splash;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        if (!isTaskRoot()) {
            finish();
            return;
        }
        Utils.setLanguageConfig(this, true);
        initData();
        mCountdownView.setTimeMillis(5000);
        mCountdownView.setProgressColor(getResources().getColor(R.color.color_333333));
        mCountdownView.start();
        mCountdownView.setOnFinishListener(new CountDownProgress.OnFinishListener() {
            @Override
            public void onFinish() {
                next();
            }
        });
    }

    private void initData() {
        SharedPreferences sharedPreferences = getSharedPreferences("bluetooth", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("status", false);
        editor.commit();

    }


    @OnClick(R.id.countdown_view)
    public void onClick() {
        mCountdownView.setClickable(false);
        next();
    }

    private void next() {
        mCountdownView.stop();
        final Intent intent;
        Boolean isFirst = Utils.getUserIsFirst();
        if (isFirst) {//第一次进入本应用
            intent = new Intent(this, GuideActivity.class);
        } else {//不是第一次进入本应用
            intent = new Intent(this, MainActivity1.class);
        }
        startActivity(intent);

    }


    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onDestroy() {
        mCountdownView.stop();
        super.onDestroy();
    }

}
