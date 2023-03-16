package com.guoji.mobile.cocobee.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.btreader.BlueToothHelper;
import com.guoji.mobile.cocobee.common.Constant;
import com.guoji.mobile.cocobee.common.ElectricVehicleApp;
import com.guoji.mobile.cocobee.utils.XToastUtils;

/**
 * 连接蓝牙设备页面
 * Created by _H_JY on 2016/6/18.
 */
public class ConnectAct extends BaseAct {
    private static final String TAG = "ConnectAct";

    private static Button start = null; //手动连接设备按钮
    private static TextView status = null; //标题栏连接状态
    private static ImageView pic = null;
    private ImageView back_btn = null;
    private TextView device_info_tip;
    private Context context;

    static String deviceName, deviceAddress; //静态变量，防止跳转到其他页面后信息消失

    //Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    //For the BlueTooth helper
    private static BlueToothHelper mBtHelper = null;
    private static Animation ani = null;

    private static boolean startFlag = false;


    private SharedPreferences sharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_connect);
        context = this;

        mBtHelper = BlueToothHelper.getInstance(context, mBtHandler);

        sharedPreferences = getSharedPreferences(Constant.SP_CONNECT_STATUS, Context.MODE_PRIVATE);

        back_btn = (ImageView) super.findViewById(R.id.back_ib);
        pic = (ImageView) super.findViewById(R.id.main_pic);
        status = (TextView) super.findViewById(R.id.status);
        start = (Button) super.findViewById(R.id.btConnectDevice);
        device_info_tip = (TextView) super.findViewById(R.id.device_info_tip);

//        //注册广播监听蓝牙断开连接
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
//        this.registerReceiver(mReceiver, filter);


        start.setOnClickListener(new OnClickListener() {
            public void onClick(View v) { //手动连接按钮点击事件
                if (startFlag) {
                    if (mBtHelper != null) {
                        if (mBtHelper.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC) { //经典蓝牙
                            mBtHelper.close();
                        } else if (mBtHelper.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_LE) { //低功耗蓝牙
                            mBtHelper.closeBLE();
                        }
                    }
                    startFlag = false;
                    device_info_tip.setVisibility(View.INVISIBLE);
                    XToastUtils.showShortToast(getResources().getString(R.string.disconnect));

                    start.setText(getResources().getString(R.string.connect_device));
                    pic.startAnimation(ani);
                    sharedPreferences = getSharedPreferences(Constant.SP_CONNECT_STATUS, Context.MODE_PRIVATE);
                    Editor editor = sharedPreferences.edit();
                    editor.putBoolean(Constant.CONNECT_STATUS, false);
                    editor.commit();
                } else {
                    if (mBluetoothAdapter != null) {
                        if (mBluetoothAdapter.isEnabled()) {//finding device dialog.
                            Intent serverIntent = new Intent(ConnectAct.this, DeviceListAct.class);
                            startActivityForResult(serverIntent, Constant.REQUEST_BLUETOOTH_CONNECT);
                        } else {
                            XToastUtils.showShortToast(getResources().getString(R.string.bluetooth_not_open));
                        }
                    } else {
                        XToastUtils.showShortToast(getResources().getString(R.string.bluetooth_not_enabel));
                    }
                }
            }
        });

        back_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ConnectAct.this.finish();
            }
        });

        //Get local Blue tooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //If the adapter is null, then Blue tooth is not supported
        if (mBluetoothAdapter == null) {
            XToastUtils.showShortToast(getResources().getString(R.string.bluetooth_not_enabel));
            finish();
            return;
        }


        ani = new AlphaAnimation(0f, 1f);
        ani.setDuration(1500);
        ani.setRepeatMode(Animation.REVERSE);
        ani.setRepeatCount(Animation.INFINITE);
        pic.setAnimation(ani);

    }


    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, Constant.REQUEST_BLUETOOTH_ENABLE);
        } else {
            mBtHelper = BlueToothHelper.getInstance(this, mBtHandler);

        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mBtHelper != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mBtHelper.getState() == BlueToothHelper.STATE_NONE) {
                // Start the Bluetooth helper
                mBtHelper.open();
                start.setEnabled(true);
            }
            if (startFlag = isConnected()) {
                setConnectedStatus();
            } else {
                sharedPreferences = getSharedPreferences(Constant.SP_CONNECT_STATUS, Context.MODE_PRIVATE);
                Editor editor = sharedPreferences.edit();
                editor.putBoolean(Constant.CONNECT_STATUS, false);
                editor.commit();
                start.setVisibility(View.VISIBLE);
                start.setText(ElectricVehicleApp.getApp().getResources().getString(R.string.connect_device));
                //auto_connect_btn.setText(R.string.auto_connect_device);
            }
        } else {
            sharedPreferences = getSharedPreferences(Constant.SP_CONNECT_STATUS, Context.MODE_PRIVATE);
            Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constant.CONNECT_STATUS, false);
            editor.commit();
            start.setVisibility(View.VISIBLE);

            start.setText(ElectricVehicleApp.getApp().getResources().getString(R.string.connect_device));
        }

    }


    private boolean isConnected() {

        SharedPreferences sp = getSharedPreferences(Constant.SP_CONNECT_STATUS, Context.MODE_PRIVATE);
        boolean status = sp.getBoolean(Constant.CONNECT_STATUS, false);

        if (mBtHelper.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC && status && (mBtHelper.getSocket() == null || !mBtHelper.getSocket().isConnected())) { //如果当前标记已连接，但是socket连接实际已经断了
            Editor editor = sp.edit();
            editor.putBoolean(Constant.CONNECT_STATUS, false);
            editor.commit();
            status = false;
        }

        return status;
    }


    @Override
    public synchronized void onPause() {
        super.onPause();
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //ILog.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case Constant.REQUEST_BLUETOOTH_ENABLE:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    mBtHelper = BlueToothHelper.getInstance(this, mBtHandler);
                } else {
                    // User did not enable Bluetooth or an error occured
                    Log.d(TAG, "Blue tooth not enabled");
                    XToastUtils.showShortToast(ElectricVehicleApp.getApp().getResources().getString(R.string.bluetooth_not_open));
                    finish();
                }
                break;
            case Constant.REQUEST_BLUETOOTH_CONNECT:
                // When DeviceListAct returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras().getString(DeviceListAct.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

                    deviceName = device.getName();
                    deviceAddress = device.getAddress();

                    if (device.getType() == BluetoothDevice.DEVICE_TYPE_CLASSIC) { //经典蓝牙（2.0）
                        // Attempt to connect to the device
                        mBtHelper.connect(device, context);
                    } else if (device.getType() == BluetoothDevice.DEVICE_TYPE_LE) { //低功耗蓝牙（4.0）
                        //判断手机是否支持蓝牙4.0
                        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                            XToastUtils.showShortToast(ElectricVehicleApp.getApp().getResources().getString(R.string.device_not_blueth));
                            return;
                        }
                        mBtHelper.connect(context, device);
                    } else if (device.getType() == BluetoothDevice.DEVICE_TYPE_UNKNOWN) {
                        XToastUtils.showShortToast(ElectricVehicleApp.getApp().getResources().getString(R.string.device_unknow));
                    } else {

                    }

                }
                break;
        }

    }


    @Override
    public void onDestroy() {
//        if (!isConnected()) {//非连接状态
//            this.unregisterReceiver(mReceiver);
//        }
        super.onDestroy();
    }

    /**
     * The Handler that gets information back from the BluetoothHelper.
     */
    public final Handler mBtHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BlueToothHelper.STATE_CONNECTED:
                            //if (mBtHelper.ping()) {
                            XToastUtils.showShortToast(ElectricVehicleApp.getApp().getResources().getString(R.string.connect_success));
                            //set the connected status
                            setConnectedStatus();

                            /*********************************************************/
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
//                                    Intent in = new Intent(ConnectAct.this, QueryActivity.class);
//                                    startActivity(in);
                                    finish();
                                    //连接成功后，程序注册了一个监听连接断开的常驻广播，这里不要调用finish()否则该唯一广播会被销毁导致无法监听
                                }
                            }, 1000);

                            break;
                        case BlueToothHelper.STATE_CONNECTING:
                            status.setText(ElectricVehicleApp.getApp().getResources().getString(R.string.connecting));
                            XToastUtils.showShortToast(ElectricVehicleApp.getApp().getResources().getString(R.string.connecting));
                            break;

                        case BlueToothHelper.STATE_NONE:
                            start.setText(ElectricVehicleApp.getApp().getResources().getString(R.string.connect_device));
                            status.setText(ElectricVehicleApp.getApp().getResources().getString(R.string.no_connect));
                            startFlag = false;
                            device_info_tip.setVisibility(View.INVISIBLE);
                            pic.startAnimation(ani);
                            break;
                    }
                    break;

                case Constant.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    //String mConnectedDeviceName = msg.getData().getString(Constant.DEVICE_NAME);
                    //showToast(getString(R.string.toast_connected)+ mConnectedDeviceName);
                    break;

                case Constant.STATE_CONNECTE_ERROR:
                    XToastUtils.showShortToast(ElectricVehicleApp.getApp().getResources().getString(R.string.connect_error));
                    setReadyStatus();
                    break;
                case Constant.STATE_CONNECTE_LOST:
                    XToastUtils.showShortToast(ElectricVehicleApp.getApp().getResources().getString(R.string.blueth_disconnect));
                    if (context != null) {
                        setReadyStatus();
                    } else {
                        setDisconnectStatus();
                    }
                    break;

            }
        }
    };

    /**
     * Set the Connection of BlueTooth with READY
     */
    private void setReadyStatus() {
        //save the ERROR status into config file.
        sharedPreferences = ElectricVehicleApp.getApp().getSharedPreferences(Constant.SP_CONNECT_STATUS, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constant.CONNECT_STATUS, false);
        editor.commit();

        device_info_tip.setVisibility(View.INVISIBLE);
        start.setVisibility(View.VISIBLE);
        start.setText(ElectricVehicleApp.getApp().getResources().getString(R.string.connect_device));
        status.setText(ElectricVehicleApp.getApp().getResources().getString(R.string.no_connect));
        pic.startAnimation(ani);
        startFlag = false;
        if (mBtHelper != null) {
            if (mBtHelper.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC) { //经典蓝牙
                mBtHelper.close();
            } else if (mBtHelper.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_LE) { //低功耗蓝牙
                mBtHelper.closeBLE();
            }
        }

    }

    /**
     * Set the Connection of BlueTooth with READY
     */
    private void setDisconnectStatus() {
        //save the ERROR status into config file.
        sharedPreferences = ElectricVehicleApp.getApp().getSharedPreferences(Constant.SP_CONNECT_STATUS, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constant.CONNECT_STATUS, false);
        editor.commit();

        startFlag = false;
        if (mBtHelper != null) {
            if (mBtHelper.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC) { //经典蓝牙
                mBtHelper.close();
            } else if (mBtHelper.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_LE) { //低功耗蓝牙
                mBtHelper.closeBLE();
            }
        }

    }

    /**
     * Set the Connection of BlueTooth with CONNECTED
     */
    private void setConnectedStatus() {

        start.setVisibility(View.VISIBLE);
        start.setText(ElectricVehicleApp.getApp().getResources().getString(R.string.disconnect));
        status.setText(ElectricVehicleApp.getApp().getResources().getString(R.string.has_connect));

        device_info_tip.setVisibility(View.VISIBLE);
        device_info_tip.setText(ElectricVehicleApp.getApp().getResources().getString(R.string.device_name) + deviceName + "\n" + ElectricVehicleApp.getApp().getResources().getString(R.string.device_mac) + deviceAddress);

        pic.clearAnimation();
        startFlag = true;

        //save the CONNECTED status into config file.
        Editor editor = sharedPreferences.edit();
        editor.putBoolean("status", true);
        editor.commit();

        //添加6个字节的
//        if (mBtHelper != null) {
//            if (mBtHelper.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC) { //经典蓝牙
//                mBtHelper.openTagDataOutputOfSixByte();
//            } else if (mBtHelper.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_LE) { //低功耗蓝牙
//                mBtHelper.bleSendCmd(Constant.OPEN_TAG_DATA_OUTPUT, BlueToothHelper.GET_SIX_TAG_LABEL, false);
//            }
//        }

    }


    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                Message msg = Message.obtain();
                msg.what = Constant.STATE_CONNECTE_LOST;
                mBtHandler.sendMessage(msg);
            }

        }
    };


}
