package com.guoji.mobile.cocobee.activity;

import android.app.Activity;
import android.os.Bundle;

import com.guoji.mobile.cocobee.common.AppManager;
import com.guoji.mobile.cocobee.common.ElectricVehicleApp;
import com.guoji.mobile.cocobee.model.User;
import com.guoji.mobile.cocobee.utils.Utils;

/**
 * 基Activity
 * Created by _H_JY on 2017/3/17.
 */
public abstract class BaseAct extends Activity {

    protected ElectricVehicleApp app;
    protected User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppManager.getAppManager().addActivity(this);
        app = ElectricVehicleApp.getApp();
        user = Utils.getUserLoginInfo();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }

}
