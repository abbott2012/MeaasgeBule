package com.guoji.mobile.cocobee.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.me.MessageActivity;
import com.guoji.mobile.cocobee.common.AppManager;
import com.guoji.mobile.cocobee.utils.Utils;

import cn.jpush.android.api.JPushInterface;

/**
 * 极光推送信息详情页面
 * Created by _H_JY on 2017/3/22.
 */
public class ShowJPushMsgAct extends AppCompatActivity {


    private TextView content_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_showjpushmsg);

        AppManager.getAppManager().addActivity(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        content_tv = (TextView) findViewById(R.id.content_tv);
        toolbar.setNavigationIcon(R.drawable.ic_close);


        Intent intent = getIntent();
        if (null != intent) {
            Bundle bundle = getIntent().getExtras();
            if (null != bundle) {
                String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
                String content = bundle.getString(JPushInterface.EXTRA_ALERT);
                toolbar.setTitle(title);
                content_tv.setText(content);
            }
        }


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isBackground(ShowJPushMsgAct.this)) {
                    startActivity(new Intent(ShowJPushMsgAct.this, MessageActivity.class));
                }
                finish();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }
}
