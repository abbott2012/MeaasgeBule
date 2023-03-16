package com.guoji.mobile.cocobee.broadcast;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Message;

import com.guoji.mobile.cocobee.activity.ConnectAct;
import com.guoji.mobile.cocobee.common.Constant;
import com.guoji.mobile.cocobee.common.ElectricVehicleApp;

/**
 * Created by wzj on 2017/2/19.
 */
public class StaticReceiver extends BroadcastReceiver {

    /**
     * 接收广播
     *
     * @param context context
     * @param intent  intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ElectricVehicleApp.getFlag() && BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
            ConnectAct connectAct = new ConnectAct();
            Message msg = Message.obtain();
            msg.what = Constant.STATE_CONNECTE_LOST;
            connectAct.mBtHandler.sendMessage(msg);
        }
    }
}