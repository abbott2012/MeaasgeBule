package com.guoji.mobile.cocobee.activity;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.btreader.BlueToothHelper;
import com.guoji.mobile.cocobee.common.Constant;
import com.guoji.mobile.cocobee.utils.XToastUtils;

import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 天线信息页面
 * Created by _H_JY on 16/12/9.
 */
public class AntennaInfoAct extends BaseAct implements View.OnClickListener {

    private Context context;
    private ImageButton back_ib;
    private BlueToothHelper reader;
    private Button read_tx_info_btn;
    private boolean mReading = false;
    private SweetAlertDialog sDialog;
    private MediaPlayer mp;

    private EditText tx_one_v_et, tx_one_grain_et, tx_one_rssi_et;
    private EditText tx_two_v_et, tx_two_grain_et, tx_two_rssi_et;
    private EditText tx_three_v_et, tx_three_grain_et, tx_three_rssi_et;
    private EditText tx_four_v_et, tx_four_grain_et, tx_four_rssi_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_antennainfo);

        context = this;

        mp = MediaPlayer.create(AntennaInfoAct.this, R.raw.beep);


        initView();
    }


    private void initView() {
        back_ib = (ImageButton) findViewById(R.id.back_ib);
        read_tx_info_btn = (Button) findViewById(R.id.read_tx_info_btn);

        /*天线1*/
        tx_one_v_et = (EditText) findViewById(R.id.tx_one_v_et);
        tx_one_grain_et = (EditText) findViewById(R.id.tx_one_grain_et);
        tx_one_rssi_et = (EditText) findViewById(R.id.tx_one_rssi_et);

        /*天线2*/
        tx_two_v_et = (EditText) findViewById(R.id.tx_two_v_et);
        tx_two_grain_et = (EditText) findViewById(R.id.tx_two_grain_et);
        tx_two_rssi_et = (EditText) findViewById(R.id.tx_two_rssi_et);

        /*天线3*/
        tx_three_v_et = (EditText) findViewById(R.id.tx_three_v_et);
        tx_three_grain_et = (EditText) findViewById(R.id.tx_three_grain_et);
        tx_three_rssi_et = (EditText) findViewById(R.id.tx_three_rssi_et);

        /*天线4*/
        tx_four_v_et = (EditText) findViewById(R.id.tx_four_v_et);
        tx_four_grain_et = (EditText) findViewById(R.id.tx_four_grain_et);
        tx_four_rssi_et = (EditText) findViewById(R.id.tx_four_rssi_et);

        back_ib.setOnClickListener(this);
        read_tx_info_btn.setOnClickListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        reader = BlueToothHelper.getInstance(context, handler);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_ib:
                finish();
                break;

            case R.id.read_tx_info_btn:
                readInfo();
                break;
        }
    }


    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (sDialog != null && sDialog.isShowing()) {
                sDialog.dismiss();
            }
            switch (msg.what) {
                case BlueToothHelper.MSG_READ_TX_INFO_OK:
                    mReading = false;
                    if (mp != null) {
                        mp.start();
                    }
                    Map<String, Object> dataMap = (Map<String, Object>) msg.obj;
                    String[] version = (String[]) dataMap.get("versionArray");
                    String[] grain = (String[]) dataMap.get("grainArray");
                    int[] rssi = (int[]) dataMap.get("rssiArray");

                    tx_one_v_et.setText(version[0]);
                    tx_one_grain_et.setText(grain[0]);
                    if (rssi[0] == 1) {
                        tx_one_rssi_et.setText(getResources().getString(R.string.none));
                    } else {
                        tx_one_rssi_et.setText("" + rssi[0]);
                    }


                    tx_two_v_et.setText(version[1]);
                    tx_two_grain_et.setText(grain[1]);
                    if (rssi[1] == 1) {
                        tx_two_rssi_et.setText(getResources().getString(R.string.none));
                    } else {
                        tx_two_rssi_et.setText("" + rssi[1]);
                    }

                    tx_three_v_et.setText(version[2]);
                    tx_three_grain_et.setText(grain[2]);
                    if (rssi[2] == 1) {
                        tx_three_rssi_et.setText(getResources().getString(R.string.none));
                    } else {
                        tx_three_rssi_et.setText("" + rssi[2]);
                    }

                    tx_four_v_et.setText(version[3]);
                    tx_four_grain_et.setText(grain[3]);
                    if (rssi[3] == 1) {
                        tx_four_rssi_et.setText(getResources().getString(R.string.none));
                    } else {
                        tx_four_rssi_et.setText("" + rssi[3]);
                    }


                    break;

                case BlueToothHelper.MSG_READ_TX_INFO_FAIL:
                    mReading = false;
                    XToastUtils.showShortToast(getResources().getString(R.string.read_fail));
                    break;

                case BlueToothHelper.MSG_NO_BLUETOOTH_SOCKET:
                    mReading = false;
                    XToastUtils.showShortToast(getResources().getString(R.string.blueth_dismiss));
                    break;
            }
        }
    };


    private void readInfo() {

        if (!isConnected()) {
            XToastUtils.showShortToast(getResources().getString(R.string.no_connect_read));
            return;
        }
        if (!mReading) {
            mReading = true;
            sDialog = new SweetAlertDialog(AntennaInfoAct.this, SweetAlertDialog.PROGRESS_TYPE);
            sDialog.setTitleText(getResources().getString(R.string.read_tx_info));
            sDialog.getProgressHelper().setBarColor(Color.parseColor("#4169e1"));
            sDialog.setCancelable(true);
            sDialog.setCanceledOnTouchOutside(true);
            sDialog.show();
            if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC) {
                reader.readTXInfo();
            } else {
                reader.bleSendCmd(Constant.BLE_READ_TX_INFO, BlueToothHelper.READ_TX_INFO_COMMAND, true); //第三个参数是是否分包接收
            }

        }

    }

    private boolean isConnected() {

        SharedPreferences sp = getSharedPreferences(Constant.SP_CONNECT_STATUS, Context.MODE_PRIVATE);
        boolean status = sp.getBoolean(Constant.CONNECT_STATUS, false);

        if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC && status && (reader.getSocket() == null || !reader.getSocket().isConnected())) { //如果当前标记已连接，但是socket连接实际已经断了
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(Constant.CONNECT_STATUS, false);
            editor.commit();
            status = false;
        }


        return status;
    }


    @Override
    protected void onDestroy() {

        if (mp != null) {
            mp.release();
            mp = null;
        }
        super.onDestroy();
    }
}
