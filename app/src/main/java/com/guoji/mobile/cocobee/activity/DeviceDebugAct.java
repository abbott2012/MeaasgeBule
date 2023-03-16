package com.guoji.mobile.cocobee.activity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bql.utils.CheckUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.btreader.BlueToothHelper;
import com.guoji.mobile.cocobee.callback.StringComCallback;
import com.guoji.mobile.cocobee.callback.StringDialogCallback;
import com.guoji.mobile.cocobee.common.Constant;
import com.guoji.mobile.cocobee.common.JsonResult;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.model.Collector;
import com.guoji.mobile.cocobee.model.Transponder;
import com.guoji.mobile.cocobee.model.TriadInfo;
import com.guoji.mobile.cocobee.utils.Tools;
import com.guoji.mobile.cocobee.utils.XToastUtils;
import com.lzy.okgo.OkGo;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Response;


/**
 * 设备安装调试页面
 * Created by _H_JY on 2017/3/17.
 */
public class DeviceDebugAct extends BaseAct {

    protected static final int CMD_STOP = 112;

    private EditText device_id_et, device_ip_et, device_port_et, mEditTriad, device_imei_et, device_num_et;
    private TextView collector_info_tv;

    private Context context;

    private ImageView deviceid_clear_iv, deviceip_clear_iv, deviceport_clear_iv;
    private ImageView lanip_clear_iv, lanfwport_clear_iv, lanfwip_clear_iv, lanwg_clear_iv, lanzwym_clear_iv, lanport_clear_iv;

    // button
    private Button read_device_btn; //读设备信息
    private Button set_params_btn; //设置参数
    private TextView read_systime_tv;
    private TextView set_systime_tv;
    private TextView systime_tv;
    private TextView net_state_tv;
    private EditText gprs_num_et;
    private TextView read_gprs_tv;


    private Button upload_info_btn = null;
    private TextView tvClearTriad, tvReadTriad, tvSetReadTriad, read_imei, read_factorynum, tx_debug, read_cache_tagnum_tv;
    private TextView read_gprs_connect_tv, read_lan_connect_tv;
    private EditText tx_one_zy_et, tx_one_rssi_et, tx_two_zy_et, tx_two_rssi_et, tx_three_zy_et, tx_three_rssi_et, tx_four_zy_et, tx_four_rssi_et;
    private EditText lan_ip_et, lan_port_et, lan_zwym_et, lan_wg_et, lan_fwip_et, lan_fwport_et;
    private EditText gprs_connect_et, lan_connect_et; //gprs连接状态，lan连接状态
    private EditText filter_et, wx_tagnum_et, yx_tagnum_et, gl_tagnum_et, stopTime_et, direction_time_et; //去重过滤

    private CheckBox beep_cb, dhcp_cb, open_direction_filter;

    private boolean mScanning = false;
    private boolean mSetting = false;

    private BlueToothHelper reader;// Blue Tooth reader
    SharedPreferences sharedPreferences = null;
    private MediaPlayer mp;

    private SweetAlertDialog sDialog;
    private ImageButton back_ib;
    private byte[] SET_PARAMS_COMMAND = null;

    //    private EditText police_num_et;
    private final int DEVICE_ID_LENGTH = 15;
    private int flag;
    private String poid;
    private byte beepSwitch = 0x00; //默认是关
    private byte dhcpSwitch = 0x00; //默认是关
    private byte drctSwitch = 0x00; //默认是关
    private byte[] filterWindowByte = new byte[]{(byte) 0x78, 0x00}; //去重窗口过滤默认值120秒
    private byte[] stopTimeWindowByte = new byte[]{(byte) 0xB3, 0x00}; //停留时间默认值180秒
    private byte[] drctTimeWindowByte; //停留时间默认值180秒


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_devicedebug);
        context = this;
        back_ib = (ImageButton) findViewById(R.id.back_ib);
        // Init the reader
        reader = BlueToothHelper.getInstance(this, handler);
        mp = MediaPlayer.create(DeviceDebugAct.this, R.raw.beep);
        // Init share Preference
        sharedPreferences = null;
        sharedPreferences = getSharedPreferences("bluetooth", Context.MODE_PRIVATE); //存放蓝牙连接状态的文件

        device_id_et = (EditText) findViewById(R.id.device_id_et);
        device_ip_et = (EditText) findViewById(R.id.device_ip_et);
        device_port_et = (EditText) findViewById(R.id.device_port_et);
        mEditTriad = (EditText) findViewById(R.id.mEditTriad);
        device_imei_et = (EditText) findViewById(R.id.device_imei_et);
        device_num_et = (EditText) findViewById(R.id.device_num_et);
        collector_info_tv = (TextView) findViewById(R.id.collector_info_tv);
        tvClearTriad = (TextView) findViewById(R.id.tvClearTriad);
        tvReadTriad = (TextView) findViewById(R.id.tvReadTriad);
        tvSetReadTriad = (TextView) findViewById(R.id.tvSetReadTriad);
        read_imei = (TextView) findViewById(R.id.read_imei_btn);
        read_factorynum = (TextView) findViewById(R.id.read_factorynum_btn);
        tx_debug = (TextView) findViewById(R.id.debug_tx);
        tx_one_rssi_et = (EditText) findViewById(R.id.tx_one_rssi_et);
        tx_one_zy_et = (EditText) findViewById(R.id.tx_one_zy_et);
        tx_two_rssi_et = (EditText) findViewById(R.id.tx_two_rssi_et);
        tx_two_zy_et = (EditText) findViewById(R.id.tx_two_zy_et);
        tx_three_rssi_et = (EditText) findViewById(R.id.tx_three_rssi_et);
        tx_three_zy_et = (EditText) findViewById(R.id.tx_three_zy_et);
        tx_four_rssi_et = (EditText) findViewById(R.id.tx_four_rssi_et);
        tx_four_zy_et = (EditText) findViewById(R.id.tx_four_zy_et);
        filter_et = (EditText) findViewById(R.id.filter_et);
        stopTime_et = (EditText) findViewById(R.id.stop_time_et);
        direction_time_et = (EditText) findViewById(R.id.direction_time_et);
        beep_cb = (CheckBox) findViewById(R.id.beep_cb);
        open_direction_filter = (CheckBox) findViewById(R.id.open_direction_filter);
        systime_tv = (TextView) findViewById(R.id.system_time_tv);
        read_systime_tv = (TextView) findViewById(R.id.read_systime_btn);
        set_systime_tv = (TextView) findViewById(R.id.set_systime_btn);
        net_state_tv = (TextView) findViewById(R.id.net_state_tv);
        gprs_num_et = (EditText) findViewById(R.id.gprs_num_et);
        read_gprs_tv = (TextView) findViewById(R.id.read_gprs_tv);
        lan_ip_et = (EditText) findViewById(R.id.lan_ip_et);
        lan_port_et = (EditText) findViewById(R.id.lan_port_et);
        lan_zwym_et = (EditText) findViewById(R.id.lan_zwym_et);
        lan_wg_et = (EditText) findViewById(R.id.lan_wg_et);
        lan_fwip_et = (EditText) findViewById(R.id.lan_fwip_et);
        lan_fwport_et = (EditText) findViewById(R.id.lan_fwport_et);
        gprs_connect_et = (EditText) findViewById(R.id.gprs_connect_et);
        lan_connect_et = (EditText) findViewById(R.id.lan_connect_et);
        read_gprs_connect_tv = (TextView) findViewById(R.id.read_gprs_connect_tv);
        read_lan_connect_tv = (TextView) findViewById(R.id.read_lan_connect_tv);
        read_cache_tagnum_tv = (TextView) findViewById(R.id.read_cache_tagnum_tv);
        wx_tagnum_et = (EditText) findViewById(R.id.wx_tagnum_et);
        yx_tagnum_et = (EditText) findViewById(R.id.yx_tagnum_et);
        gl_tagnum_et = (EditText) findViewById(R.id.gl_tagnum_et);
        dhcp_cb = (CheckBox) findViewById(R.id.dhcp_cb);

        device_ip_et.setText("10.10.100.42");
        device_port_et.setText("4600");
        filter_et.setText("180");
        stopTime_et.setText("180");
        direction_time_et.setText("180");

        open_direction_filter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) { //开
                    drctSwitch = 0x01;
                } else { //关
                    drctSwitch = 0x00;
                }
            }
        });
        beep_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) { //开
                    beepSwitch = 0x01;
                } else { //关
                    beepSwitch = 0x00;
                }
            }
        });

        dhcp_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) { //开
                    dhcpSwitch = 0x01;
                } else { //关
                    dhcpSwitch = 0x00;
                }
            }
        });

        //用于清除输入的图标
        deviceid_clear_iv = (ImageView) findViewById(R.id.deviceid_clear_iv);
        deviceip_clear_iv = (ImageView) findViewById(R.id.deviceip_clear_iv);
        deviceport_clear_iv = (ImageView) findViewById(R.id.deviceport_clear_iv);
        lanip_clear_iv = (ImageView) findViewById(R.id.lanip_clear_iv);
        lanport_clear_iv = (ImageView) findViewById(R.id.lanport_clear_iv);
        lanzwym_clear_iv = (ImageView) findViewById(R.id.lanzwym_clear_iv);
        lanwg_clear_iv = (ImageView) findViewById(R.id.lanwg_clear_iv);
        lanfwip_clear_iv = (ImageView) findViewById(R.id.lanfwip_clear_iv);
        lanfwport_clear_iv = (ImageView) findViewById(R.id.lanfwport_clear_iv);

        lan_ip_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    lanip_clear_iv.setVisibility(View.VISIBLE);
                } else {
                    lanip_clear_iv.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        lan_port_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    lanport_clear_iv.setVisibility(View.VISIBLE);
                } else {
                    lanport_clear_iv.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        lan_zwym_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    lanzwym_clear_iv.setVisibility(View.VISIBLE);
                } else {
                    lanzwym_clear_iv.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        lan_wg_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    lanwg_clear_iv.setVisibility(View.VISIBLE);
                } else {
                    lanwg_clear_iv.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        lan_fwip_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    lanfwip_clear_iv.setVisibility(View.VISIBLE);
                } else {
                    lanfwip_clear_iv.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        lan_fwport_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    lanfwport_clear_iv.setVisibility(View.VISIBLE);
                } else {
                    lanfwport_clear_iv.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        tx_debug.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, AntennaDebugAct.class));
            }
        });


        read_cache_tagnum_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnected()) {
                    XToastUtils.showShortToast(getResources().getString(R.string.no_connect_read));
                }

                if (!mScanning) {
                    mScanning = true;
                    onShowLoadingDialog();
                    if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC) {
                        reader.readCacheTagNum();
                    } else { //BLE
                        reader.bleSendCmd(Constant.BLE_READ_CACHE_TAG_NUM, BlueToothHelper.READ_CACHE_TAG_NUM_COMMAND, false);
                    }

                }
            }
        });


        read_gprs_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnected()) {
                    XToastUtils.showShortToast(getResources().getString(R.string.no_connect_read));
                    return;
                }

                if (!mScanning) {
                    mScanning = true;
                    onShowLoadingDialog();
                    if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC) {
                        reader.readGPRSNum();
                    } else {
                        reader.bleSendCmd(Constant.BLE_READ_GPRS_NUM, BlueToothHelper.READ_GPRS_NUM_COMMAND, false);
                    }
                }
            }
        });

        //清除三元组的信息
        tvClearTriad.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditTriad.getText().clear();
            }
        });

        //读取三元组
        tvReadTriad.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConnected()) {
                    XToastUtils.showShortToast(getResources().getString(R.string.no_connect_read));
                    return;
                }
                if (!mScanning) {
                    mScanning = true;
                    onShowLoadingDialog();
                    if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC) {
                        reader.onReadTriad();
                    } else {
                        reader.bleSendCmd(Constant.READ_TRIAD_PARAM, BlueToothHelper.READ_TRIAD_PARAM, true);
                    }
                }
            }
        });
        //设置三元组信息
        tvSetReadTriad.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //设置三元组信息
                if (!isConnected()) {
                    XToastUtils.showShortToast(getResources().getString(R.string.no_connect_set));
                    return;
                }
                String mEditTriadInfo = mEditTriad.getText().toString().trim();
                if (!TextUtils.isEmpty(mEditTriadInfo)) {
                    JsonReader jsonReader = new JsonReader(new StringReader(mEditTriadInfo));
                    jsonReader.setLenient(true);
                    TriadInfo mTriadInfo = new Gson().fromJson(jsonReader, TriadInfo.class);
                    if (mTriadInfo != null) {
                        byte[] mProductKeyByte = Tools.getBytes(mTriadInfo.getProductKey(), 16);
                        byte[] mDeviceNameByte = Tools.getBytes(mTriadInfo.getDeviceName(), 32);
                        byte[] mDeviceSecretByte = Tools.getBytes(mTriadInfo.getDeviceSecret(), 40);
                        int totalByteSize = mProductKeyByte.length + mDeviceNameByte.length + mDeviceSecretByte.length + 5;
                        int byteLength = mProductKeyByte.length + mDeviceNameByte.length + mDeviceSecretByte.length + 2;
                        byte[] mTriadByte = new byte[totalByteSize];
                        mTriadByte[0] = 0x0A;
                        mTriadByte[1] = (byte) 0xFF;
                        mTriadByte[2] = Tools.int2byte(byteLength)[0];
                        mTriadByte[3] = 0x68;
                        int start = 4;
                        //参数信息
                        for (int i = 0; i < mProductKeyByte.length; i++) {
                            mTriadByte[start++] = mProductKeyByte[i];
                        }
                        for (int i = 0; i < mDeviceNameByte.length; i++) {
                            mTriadByte[start++] = mDeviceNameByte[i];
                        }
                        for (int i = 0; i < mDeviceSecretByte.length; i++) {
                            mTriadByte[start++] = mDeviceSecretByte[i];
                        }
                        String mCheckSumByte = Tools.sendRcvByteNum(mTriadByte);
                        mTriadByte[start] = Tools.hexStringToByte(mCheckSumByte)[0];
                        String sendParam = Tools.bytesToHexString1(mTriadByte);
                        Log.e("xy", "sendParam:" + sendParam);
                        if (!mScanning) {
                            mScanning = true;
                            onShowLoadingDialog();
                            if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC) { //蓝牙2.0
                                reader.setParams(mTriadByte);
                            } else { //蓝牙4.0
                                reader.bleSetParams(Constant.SET_TRIAD_PARAM, mTriadByte);
                            }
                        }
                    }
                }
            }
        });

        read_imei.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConnected()) {
                    XToastUtils.showShortToast(getResources().getString(R.string.no_connect_read));
                    return;
                }
                if (!mScanning) {
                    mScanning = true;
                    onShowLoadingDialog();
                    if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC) {
                        reader.readImei();
                    } else {
                        reader.bleSendCmd(Constant.BLE_READ_IMEI, BlueToothHelper.READ_IMEI_COMMAND, true);
                    }
                }
            }
        });


        //出厂编号
        read_factorynum.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConnected()) {
                    XToastUtils.showShortToast(getResources().getString(R.string.no_connect_read));
                    return;
                }

                if (!mScanning) {
                    mScanning = true;
                    onShowLoadingDialog();
                    if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC) {
                        reader.readFactoryNum();
                    } else {
                        reader.bleSendCmd(Constant.BLE_READ_FACTORY_NUM, BlueToothHelper.READ_FACNUM_COMMAND, true);
                    }

                }


            }
        });


        read_systime_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConnected()) {
                    XToastUtils.showShortToast(getResources().getString(R.string.no_connect_read));
                    return;
                }
                if (!mScanning) {
                    mScanning = true;
                    onShowLoadingDialog();
                    if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC) {
                        reader.readSysTime();
                    } else {
                        reader.bleSendCmd(Constant.BLE_READ_SYS_TIME, BlueToothHelper.READ_SYS_TIME_COMMAND, false);
                    }

                }
            }
        });


        read_gprs_connect_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnected()) {
                    XToastUtils.showShortToast(getResources().getString(R.string.no_connect_read));
                    return;
                }
                if (!mScanning) {
                    mScanning = true;
                    onShowLoadingDialog();
                    if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC) {
                        reader.readGPRSCntState();
                    } else {
                        reader.bleSendCmd(Constant.BLE_READ_GPRS_CONNECT_STATE, BlueToothHelper.READ_GPRS_CONNECT_STATE_COMMAND, true);
                    }

                }
            }
        });


        read_lan_connect_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnected()) {
                    XToastUtils.showShortToast(getResources().getString(R.string.no_connect_read));
                    return;
                }
                if (!mScanning) {
                    mScanning = true;
                    onShowLoadingDialog();
                    if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC) {
                        reader.readLANCntState();
                    } else {
                        reader.bleSendCmd(Constant.BLE_READ_LAN_CONNECT_STATE, BlueToothHelper.READ_LAN_CONNECT_STATE_COMMAND, true);
                    }

                }
            }
        });


        set_systime_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnected()) {
                    XToastUtils.showShortToast(getResources().getString(R.string.no_connect_read));
                    return;
                }

                if (!mSetting) {
                    mSetting = true;
                    onShowLoadingDialog();
                    if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC) {
                        reader.setSysTime();
                    } else {
                        reader.bleSendCmd(Constant.BLE_SET_SYS_TIME, reader.getSysTimeBytes(), true);
                    }

                }


            }
        });


        device_id_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence text, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(text)) {
                    deviceid_clear_iv.setVisibility(View.VISIBLE);
                } else {
                    deviceid_clear_iv.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        device_ip_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence text, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(text)) {
                    deviceip_clear_iv.setVisibility(View.VISIBLE);
                } else {
                    deviceip_clear_iv.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        device_port_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence text, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(text)) {
                    deviceport_clear_iv.setVisibility(View.VISIBLE);
                } else {
                    deviceport_clear_iv.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        deviceid_clear_iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                device_id_et.setText("");
            }
        });
        deviceip_clear_iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                device_ip_et.setText("");
            }
        });
        deviceport_clear_iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                device_port_et.setText("");
            }
        });


        lanip_clear_iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                lan_ip_et.setText("");
            }
        });

        lanport_clear_iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                lan_port_et.setText("");
            }
        });


        lanzwym_clear_iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                lan_zwym_et.setText("");
            }
        });


        lanwg_clear_iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                lan_wg_et.setText("");
            }
        });


        lanfwip_clear_iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                lan_fwip_et.setText("");
            }
        });


        lanfwport_clear_iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                lan_fwport_et.setText("");
            }
        });


        back_ib.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        read_device_btn = (Button) super.findViewById(R.id.input_read_tag); //读取标签按钮
        set_params_btn = (Button) super.findViewById(R.id.input_save_data); //保存信息按钮


        read_device_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnected()) {
                    XToastUtils.showShortToast(getResources().getString(R.string.no_connect_read));
                    return;
                }

                if (!mScanning) {
                    mScanning = true;
                    onShowLoadingDialog();
                    if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC) { //蓝牙2.0
                        reader.readDeviceInfo();
                    } else if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_LE) { //蓝牙4.0
                        //                        reader.bleSendCmd(Constant.BLE_READ_DEVICE_INFO, BlueToothHelper.SCAN_DEVICE_COMMAND, true);
                        reader.bleSendCmd(Constant.BLE_READ_DEVICE_INFO, BlueToothHelper.SCAN_DEVICE_COMMANDS, true);
                    }
                }
            }
        });
        // 设置参数信息
        set_params_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnected()) {
                    XToastUtils.showShortToast(getResources().getString(R.string.no_connect_set));
                    return;
                }


                if (TextUtils.isEmpty(device_id_et.getText().toString().trim())) {
                    XToastUtils.showShortToast(getResources().getString(R.string.enter_device_id));
                    return;
                }
                if (device_id_et.getText().toString().trim().length() < DEVICE_ID_LENGTH) {
                    XToastUtils.showShortToast(getResources().getString(R.string.device_id_length_less));
                    return;
                }

                if (TextUtils.isEmpty(device_ip_et.getText().toString().trim())) {
                    XToastUtils.showShortToast(getResources().getString(R.string.ip_is_empty));
                    return;
                }

                if (TextUtils.isEmpty(device_port_et.getText().toString().trim())) {
                    XToastUtils.showShortToast(getResources().getString(R.string.port_is_empty));
                    return;
                }


                if (Integer.valueOf(device_port_et.getText().toString().trim()) < 0 || Integer.valueOf(device_port_et.getText().toString().trim()) > 65535) {
                    XToastUtils.showShortToast(getResources().getString(R.string.port_rang));
                    return;
                }

                String tx_one_zy_str = tx_one_zy_et.getText().toString().trim();
                if (!TextUtils.isEmpty(tx_one_zy_str)) {
                    if (Integer.valueOf(tx_one_zy_str) < 0 || Integer.valueOf(tx_one_zy_str) > 31) {
                        XToastUtils.showShortToast(getResources().getString(R.string.zy_rang));
                        return;
                    }
                }

                String tx_two_zy_str = tx_two_zy_et.getText().toString().trim();
                if (!TextUtils.isEmpty(tx_two_zy_str)) {
                    if (Integer.valueOf(tx_two_zy_str) < 0 || Integer.valueOf(tx_two_zy_str) > 31) {
                        XToastUtils.showShortToast(getResources().getString(R.string.zy_rang));
                        return;
                    }
                }

                String tx_three_zy_str = tx_three_zy_et.getText().toString().trim();
                if (!TextUtils.isEmpty(tx_three_zy_str)) {
                    if (Integer.valueOf(tx_three_zy_str) < 0 || Integer.valueOf(tx_three_zy_str) > 31) {
                        XToastUtils.showShortToast(getResources().getString(R.string.zy_rang));
                        return;
                    }
                }

                String tx_four_zy_str = tx_four_zy_et.getText().toString().trim();
                if (!TextUtils.isEmpty(tx_four_zy_str)) {
                    if (Integer.valueOf(tx_four_zy_str) < 0 || Integer.valueOf(tx_four_zy_str) > 31) {
                        XToastUtils.showShortToast(getResources().getString(R.string.zy_rang));
                        return;
                    }
                }

                String tx_one_rssi_str = tx_one_rssi_et.getText().toString().trim();
                if (!TextUtils.isEmpty(tx_one_rssi_str)) {
                    if (Integer.valueOf(tx_one_rssi_str) < -128 || Integer.valueOf(tx_one_rssi_str) > 0) {
                        XToastUtils.showShortToast(getResources().getString(R.string.rssi_rang));
                        return;
                    }
                }


                String tx_two_rssi_str = tx_two_rssi_et.getText().toString().trim();
                if (!TextUtils.isEmpty(tx_two_rssi_str)) {
                    if (Integer.valueOf(tx_two_rssi_str) < -128 || Integer.valueOf(tx_two_rssi_str) > 0) {
                        XToastUtils.showShortToast(getResources().getString(R.string.rssi_rang));
                        return;
                    }
                }


                String tx_three_rssi_str = tx_three_rssi_et.getText().toString().trim();
                if (!TextUtils.isEmpty(tx_three_rssi_str)) {
                    if (Integer.valueOf(tx_three_rssi_str) < -128 || Integer.valueOf(tx_three_rssi_str) > 0) {
                        XToastUtils.showShortToast(getResources().getString(R.string.rssi_rang));
                        return;
                    }
                }


                String tx_four_rssi_str = tx_four_rssi_et.getText().toString().trim();
                if (!TextUtils.isEmpty(tx_four_rssi_str)) {
                    if (Integer.valueOf(tx_four_rssi_str) < -128 || Integer.valueOf(tx_four_rssi_str) > 0) {
                        XToastUtils.showShortToast(getResources().getString(R.string.rssi_rang));
                        return;
                    }
                }


                String filterStr = filter_et.getText().toString().trim();
                if (!TextUtils.isEmpty(filterStr)) {
                    if (Integer.valueOf(filterStr) < 0 || Integer.valueOf(filterStr) > 65526) {
                        XToastUtils.showShortToast(getResources().getString(R.string.quchong_rang));
                        return;
                    }
                }

                String stopTimeStr = stopTime_et.getText().toString().trim();
                if (!TextUtils.isEmpty(stopTimeStr)) {
                    if (Integer.valueOf(stopTimeStr) < 0 || Integer.valueOf(stopTimeStr) > 65535) {
                        XToastUtils.showShortToast(getResources().getString(R.string.stop_time_rang));
                        return;
                    }
                }
                String drctTimeStr = direction_time_et.getText().toString().trim();
                if (!TextUtils.isEmpty(drctTimeStr)) {
                    if (Integer.valueOf(drctTimeStr) < 0 || Integer.valueOf(drctTimeStr) > 255) {
                        XToastUtils.showShortToast(getResources().getString(R.string.stop_time_filter));
                        return;
                    }
                }


                String lanIp = lan_ip_et.getText().toString().trim();
                if (TextUtils.isEmpty(lanIp)) {
                    XToastUtils.showShortToast(getResources().getString(R.string.local_ip_empty));
                    return;
                }

                String lanPort = lan_port_et.getText().toString().trim();
                if (TextUtils.isEmpty(lanPort)) {
                    XToastUtils.showShortToast(getResources().getString(R.string.local_port_empty));
                    return;
                }
                if (!TextUtils.isEmpty(lanPort)) {
                    if (Integer.valueOf(lanPort) < 0 || Integer.valueOf(lanPort) > 65536) {
                        XToastUtils.showShortToast(getResources().getString(R.string.port_rang));
                        return;
                    }
                }


                String lanZwym = lan_zwym_et.getText().toString().trim();
                if (TextUtils.isEmpty(lanZwym)) {
                    XToastUtils.showShortToast(getResources().getString(R.string.sub_net_mask_empty));
                    return;
                }

                String lanWg = lan_wg_et.getText().toString().trim();
                if (TextUtils.isEmpty(lanWg)) {
                    XToastUtils.showShortToast(getResources().getString(R.string.gateway_empty));
                    return;
                }

                String lanFwIp = lan_fwip_et.getText().toString().trim();
                if (TextUtils.isEmpty(lanFwIp)) {
                    XToastUtils.showShortToast(getResources().getString(R.string.plat_ip_2_empty));
                    return;
                }

                String lanFwPort = lan_fwport_et.getText().toString().trim();
                if (TextUtils.isEmpty(lanFwPort)) {
                    XToastUtils.showShortToast(getResources().getString(R.string.plat_port_2_empty));
                    return;
                }
                if (!TextUtils.isEmpty(lanFwPort)) {
                    if (Integer.valueOf(lanFwPort) < 0 || Integer.valueOf(lanFwPort) > 65536) {
                        XToastUtils.showShortToast(getResources().getString(R.string.port_rang));
                        return;
                    }
                }


                if (!mSetting) {
                    mSetting = true;
                    sDialog = new SweetAlertDialog(DeviceDebugAct.this, SweetAlertDialog.PROGRESS_TYPE);
                    sDialog.setTitleText(getResources().getString(R.string.setting_params));
                    sDialog.getProgressHelper().setBarColor(Color.parseColor("#4169e1"));
                    sDialog.setCancelable(true);
                    sDialog.setCanceledOnTouchOutside(true);
                    sDialog.show();

                    byte[] ip = new byte[32];
                    byte[] realIp = device_ip_et.getText().toString().trim().getBytes();


                    //lan信息
                    String[] lanIpNumStr = lanIp.split("\\."); //"."需要加转义字符
                    byte[] lanIpB = new byte[4];
                    for (int i = 0; i < 4; i++) {
                        lanIpB[i] = (byte) Integer.parseInt(lanIpNumStr[i]);
                    }

                    //lan信息
                    String[] lanZwymNumStr = lanZwym.split("\\.");
                    byte[] lanZwymB = new byte[4];
                    for (int i = 0; i < 4; i++) {
                        lanZwymB[i] = (byte) Integer.parseInt(lanZwymNumStr[i]);
                    }

                    //lan信息
                    String[] lanWgNumStr = lanWg.split("\\.");
                    byte[] lanWgB = new byte[4];
                    for (int i = 0; i < 4; i++) {
                        lanWgB[i] = (byte) Integer.parseInt(lanWgNumStr[i]);
                    }

                    //lan信息
                    byte[] lanFwIpB = lanFwIp.getBytes();


                    byte[] lanPortB = Tools.int2byte(Integer.valueOf(lanPort));
                    byte[] lanFwPortB = Tools.int2byte(Integer.valueOf(lanFwPort));


                    //获取增益
                    byte[] grain1 = new byte[4];
                    if (TextUtils.isEmpty(tx_one_zy_str)) {
                        grain1[0] = 0x1F;
                    } else {
                        grain1 = Tools.int2byte(Integer.valueOf(tx_one_zy_str));
                    }

                    byte[] grain2 = new byte[4];
                    if (TextUtils.isEmpty(tx_two_zy_str)) {
                        grain2[0] = 0x1F;
                    } else {
                        grain2 = Tools.int2byte(Integer.valueOf(tx_two_zy_str));
                    }

                    byte[] grain3 = new byte[4];
                    if (TextUtils.isEmpty(tx_three_zy_str)) {
                        grain3[0] = 0x1F;
                    } else {
                        grain3 = Tools.int2byte(Integer.valueOf(tx_three_zy_str));
                    }


                    byte[] grain4 = new byte[4];
                    if (TextUtils.isEmpty(tx_four_zy_str)) {
                        grain4[0] = 0x1F;
                    } else {
                        grain4 = Tools.int2byte(Integer.valueOf(tx_four_zy_str));
                    }


                    byte[] grain = new byte[4];
                    grain[0] = grain1[0];
                    grain[1] = grain2[0];
                    grain[2] = grain3[0];
                    grain[3] = grain4[0];


                    //获取rssi
                    byte[] rssi1 = new byte[4];
                    if (TextUtils.isEmpty(tx_one_rssi_str)) {
                        rssi1[0] = (byte) 0x80;
                    } else {
                        rssi1 = Tools.int2byte(Integer.valueOf(tx_one_rssi_str));
                    }


                    byte[] rssi2 = new byte[4];
                    if (TextUtils.isEmpty(tx_two_rssi_str)) {
                        rssi2[0] = (byte) 0x80;
                    } else {
                        rssi2 = Tools.int2byte(Integer.valueOf(tx_two_rssi_str));
                    }

                    byte[] rssi3 = new byte[4];
                    if (TextUtils.isEmpty(tx_three_rssi_str)) {
                        rssi3[0] = (byte) 0x80;
                    } else {
                        rssi3 = Tools.int2byte(Integer.valueOf(tx_three_rssi_str));
                    }


                    byte[] rssi4 = new byte[4];
                    if (TextUtils.isEmpty(tx_four_rssi_str)) {
                        rssi4[0] = (byte) 0x80;
                    } else {
                        rssi4 = Tools.int2byte(Integer.valueOf(tx_four_rssi_str));
                    }


                    if (!TextUtils.isEmpty(filterStr)) {
                        filterWindowByte = Tools.int2byte(Integer.valueOf(filterStr));
                    }
                    if (!TextUtils.isEmpty(stopTimeStr)) {
                        stopTimeWindowByte = Tools.int2byte(Integer.valueOf(stopTimeStr));
                    }
                    if (!TextUtils.isEmpty(drctTimeStr)) {
                        drctTimeWindowByte = Tools.int2byte(Integer.valueOf(drctTimeStr));
                    }
                    byte[] rssi = new byte[4];
                    rssi[0] = rssi1[0];
                    rssi[1] = rssi2[0];
                    rssi[2] = rssi3[0];
                    rssi[3] = rssi4[0];


                    for (int i = 0; i < 32; i++) {
                        if (i < realIp.length) {
                            ip[i] = realIp[i];
                        } else {
                            ip[i] = 0x00;
                        }
                    }

                    byte[] realFwIpB = new byte[32];
                    for (int i = 0; i < 32; i++) {
                        if (i < lanFwIpB.length) {
                            realFwIpB[i] = lanFwIpB[i];
                        } else {
                            realFwIpB[i] = 0x00;
                        }

                    }

                    byte[] device_id = device_id_et.getText().toString().trim().getBytes(); //把字符中转换为字符数组
                    byte[] port = Tools.int2byte(Integer.valueOf(device_port_et.getText().toString().trim()));

                    if (SET_PARAMS_COMMAND != null) {
                        if (SET_PARAMS_COMMAND.length == 187) {
                            SET_PARAMS_COMMAND[0] = 0x0A;
                            SET_PARAMS_COMMAND[2] = (byte) 0xB8;
                            SET_PARAMS_COMMAND[3] = 0x13;
                            SET_PARAMS_COMMAND[4] = 0x55;
                            SET_PARAMS_COMMAND[8] = beepSwitch;
                            SET_PARAMS_COMMAND[10] = filterWindowByte[0];
                            SET_PARAMS_COMMAND[11] = filterWindowByte[1];
                            SET_PARAMS_COMMAND[30] = dhcpSwitch;

                            int start = 12;//设备id
                            for (int i = 0; i < device_id.length; i++) {
                                SET_PARAMS_COMMAND[start++] = device_id[i];
                            }

                            SET_PARAMS_COMMAND[start] = 0x00;
                            SET_PARAMS_COMMAND[start + 1] = stopTimeWindowByte[0];
                            SET_PARAMS_COMMAND[start + 2] = stopTimeWindowByte[1];


                            //lan ip信息
                            start = 31;
                            for (int i = 0; i < lanIpB.length; i++) {
                                SET_PARAMS_COMMAND[start++] = lanIpB[i];
                            }


                            //lan子网掩码
                            start = 35;
                            for (int i = 0; i < lanZwymB.length; i++) {
                                SET_PARAMS_COMMAND[start++] = lanZwymB[i];
                            }

                            //lan网关
                            start = 39;
                            for (int i = 0; i < lanWgB.length; i++) {
                                SET_PARAMS_COMMAND[start++] = lanWgB[i];
                            }

                            //lan 端口号
                            SET_PARAMS_COMMAND[43] = lanPortB[0];
                            SET_PARAMS_COMMAND[44] = lanPortB[1];


                            start = 45; //ip地址
                            for (int i = 0; i < ip.length; i++) {
                                SET_PARAMS_COMMAND[start++] = ip[i];
                            }

                            SET_PARAMS_COMMAND[start++] = port[0];
                            SET_PARAMS_COMMAND[start] = port[1];


                            //lan服务器ip
                            start = 79;
                            for (int i = 0; i < realFwIpB.length; i++) {
                                SET_PARAMS_COMMAND[start++] = realFwIpB[i];
                            }

                            //lan服务器端口
                            SET_PARAMS_COMMAND[111] = lanFwPortB[0];
                            SET_PARAMS_COMMAND[112] = lanFwPortB[1];


                            start = 172; //插入rssi
                            for (int i = 0; i < rssi.length; i++) {
                                SET_PARAMS_COMMAND[start++] = rssi[i];
                            }

                            //插入增益
                            for (int i = 0; i < grain.length; i++) {
                                SET_PARAMS_COMMAND[start++] = grain[i];
                            }
                            SET_PARAMS_COMMAND[182] = drctSwitch;
                            if (drctTimeWindowByte != null) {
                                SET_PARAMS_COMMAND[183] = drctTimeWindowByte[0];
                            }

                            byte[] a = new byte[1];
                            for (int i = 0; i < SET_PARAMS_COMMAND.length - 1; i++) {
                                a[0] += SET_PARAMS_COMMAND[i];
                            }
                            a[0] = (byte) ((~a[0]) + 1);
                            SET_PARAMS_COMMAND[186] = a[0];

                        } else {
                            linkCommand(device_id, ip, port, rssi, grain, lanIpB, lanPortB, lanZwymB, lanWgB, realFwIpB, lanFwPortB);
                        }

                    } else {

                        linkCommand(device_id, ip, port, rssi, grain, lanIpB, lanPortB, lanZwymB, lanWgB, realFwIpB, lanFwPortB);

                    }
                    //拼接数据包
                    if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC) { //蓝牙2.0
                        reader.setParams(SET_PARAMS_COMMAND);
                    } else { //蓝牙4.0
                        reader.bleSetParams(Constant.BLE_SET_PARAMS, SET_PARAMS_COMMAND);
                    }
                }
            }
        });


        // --上传数据---------------------------------------------------------
        upload_info_btn = (Button) super.findViewById(R.id.input_uplo_dasta);
        upload_info_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 上传数据，先从本地数据库中的表查询所有数据添加到一个集合中,
                 * 然后把集合中的数据转换成json格式数据再提交到后台
                 */
                //                uploadCheck(); //上传方法
            }
        });

        reader.setScanListener(new BlueToothHelper.ScanListener() {

                                   @Override
                                   public void scanned(final Transponder trp) {
                                       runOnUiThread(new Runnable() {
                                           @Override
                                           public void run() {
                                               if (sDialog != null && sDialog.isShowing()) {
                                                   sDialog.dismiss();
                                               }
                                               if (trp != null) {
                                                   device_id_et.setText(trp.getDevice_id());
                                                   device_ip_et.setText(trp.getIp());
                                                   device_port_et.setText(trp.getPort());
                                                   SET_PARAMS_COMMAND = trp.getData();
                                                   tx_one_zy_et.setText(trp.getDeviceGrains()[0]);
                                                   tx_two_zy_et.setText(trp.getDeviceGrains()[1]);
                                                   tx_three_zy_et.setText(trp.getDeviceGrains()[2]);
                                                   tx_four_zy_et.setText(trp.getDeviceGrains()[3]);


                                                   tx_one_rssi_et.setText(trp.getDeviceRssis()[0] + "");
                                                   tx_two_rssi_et.setText(trp.getDeviceRssis()[1] + "");
                                                   tx_three_rssi_et.setText(trp.getDeviceRssis()[2] + "");
                                                   tx_four_rssi_et.setText(trp.getDeviceRssis()[3] + "");

                                                   filter_et.setText(trp.getFilterNum() + "");
                                                   stopTime_et.setText(trp.getStopTimeNum() + "");
                                                   direction_time_et.setText(trp.getDrctTimeNum() + "");

                                                   lan_ip_et.setText(trp.getLanIp());
                                                   lan_port_et.setText(trp.getLanPort());
                                                   lan_zwym_et.setText(trp.getLanZwym());
                                                   lan_wg_et.setText(trp.getLanWg());
                                                   lan_fwip_et.setText(trp.getLanFwIp());
                                                   lan_fwport_et.setText(trp.getLanFwPort());


                                                   if (trp.getBeepFlag() == 0) { //关闭状态
                                                       beep_cb.setChecked(false);
                                                       beepSwitch = 0x00;
                                                   } else {
                                                       beep_cb.setChecked(true);
                                                       beepSwitch = 0x01;
                                                   }

                                                   if (trp.getDrctFlag() == 0) { //关闭状态
                                                       open_direction_filter.setChecked(false);
                                                       drctSwitch = 0x00;
                                                   } else {
                                                       open_direction_filter.setChecked(true);
                                                       drctSwitch = 0x01;
                                                   }

                                                   if (trp.getDhcpFlag() == 0) { //关闭状态
                                                       dhcp_cb.setChecked(false);
                                                       dhcpSwitch = 0x00;
                                                   } else {
                                                       dhcp_cb.setChecked(true);
                                                       dhcpSwitch = 0x01;
                                                   }
                                                   //网络连接状态
                                                   if (trp.getNetState() == 0xA1) {
                                                       net_state_tv.setText(getResources().getString(R.string.wireless_has_connect));
                                                   } else if (trp.getNetState() == 0xA0) {
                                                       net_state_tv.setText(getResources().getString(R.string.not_connect));
                                                   } else if (trp.getNetState() == 0xA2) {
                                                       net_state_tv.setText(getResources().getString(R.string.wire_has_connect));
                                                   } else if (trp.getNetState() == 0xA3) {
                                                       net_state_tv.setText(getResources().getString(R.string.wire_wireless_has_connect));
                                                   } else {
                                                       net_state_tv.setText(getResources().getString(R.string.no_support));
                                                   }
                                                   if (!TextUtils.isEmpty(trp.getVersion()) && !TextUtils.isEmpty(trp.getTime())) {
                                                       collector_info_tv.setText(getResources().getString(R.string.collector_info) + "（ V " + trp.getVersion() + "      " + trp.getTime() + " )");
                                                   } else if (!TextUtils.isEmpty(trp.getVersion())) {
                                                       collector_info_tv.setText(getResources().getString(R.string.collector_info) + "（ V " + trp.getVersion() + " ）");
                                                   } else if (!TextUtils.isEmpty(trp.getTime())) {
                                                       collector_info_tv.setText(getResources().getString(R.string.collector_info) + "（ " + trp.getTime() + " ）");
                                                   } else {
                                                       collector_info_tv.setText(getResources().getString(R.string.collector_info));
                                                   }

                                                   if (mp != null) {
                                                       mp.start();
                                                   }

                                               } else {
                                                   XToastUtils.showShortToast(getResources().getString(R.string.read_device_fail));
                                               }
                                               mSetting = false;
                                               mScanning = false;
                                           }
                                       });
                                   }

                                   @Override
                                   public void begin() {
                                       runOnUiThread(new Runnable() {
                                           @Override
                                           public void run() {
                                               mScanning = true;
                                               onShowLoadingDialog();
                                           }
                                       });
                                   }
                               }

        );

    }

    /**
     * 显示加载框
     */
    private void onShowLoadingDialog() {
        sDialog = new SweetAlertDialog(DeviceDebugAct.this, SweetAlertDialog.PROGRESS_TYPE);
        sDialog.setTitleText(getResources().getString(R.string.reading));
        sDialog.getProgressHelper().setBarColor(Color.parseColor("#4169e1"));
        sDialog.setCancelable(true);
        sDialog.setCanceledOnTouchOutside(true);
        sDialog.show();
    }


    private void linkCommand(byte[] device_id, byte[] ip, byte[] port, byte[] rssi, byte[] grain, byte[] lanIpB, byte[] lanPortB,
                             byte[] lanZwymB, byte[] lanWgB, byte[] lanFwIpB, byte[] lanFwPortB) {
        byte[] SET_PARAMS_COMMAND_PART1 = new byte[]{0x0A, 0x00, (byte) 0xB8, 0x13,
                0x55,
                0x03,
                0x00, 0x00,
                0x01,
                0x01,
                0x0A, 0x00,
                device_id[0], device_id[1], device_id[2], device_id[3], device_id[4], device_id[5], device_id[6], device_id[7],
                device_id[8], device_id[9], device_id[10], device_id[11], device_id[12], device_id[13], device_id[14],
                0x00, 0x00, 0x00,
                0x01,
                lanIpB[0], lanIpB[1], lanIpB[2], lanIpB[3],
                lanZwymB[0], lanZwymB[1], lanZwymB[2], lanZwymB[3],
                lanWgB[0], lanWgB[1], lanWgB[2], lanWgB[3],
                lanPortB[0], lanPortB[1]
        };


        byte[] SET_PARAMS_COMMAND_PART2 = new byte[SET_PARAMS_COMMAND_PART1.length + ip.length];
        System.arraycopy(SET_PARAMS_COMMAND_PART1, 0, SET_PARAMS_COMMAND_PART2, 0, SET_PARAMS_COMMAND_PART1.length);
        System.arraycopy(ip, 0, SET_PARAMS_COMMAND_PART2, SET_PARAMS_COMMAND_PART1.length, ip.length);


        byte[] other_byte = new byte[109];
        other_byte[0] = port[0];
        other_byte[1] = port[1];
        for (int i = 2; i < other_byte.length - 1; i++) {
            other_byte[i] = 0x00;
        }

        //lan服务器ip  32个字节
        for (int i = 0; i < lanFwIpB.length; i++) {
            other_byte[i + 2] = lanFwIpB[i];
        }


        //lan服务器端口
        other_byte[34] = lanFwPortB[0];
        other_byte[35] = lanFwPortB[1];

        //赋值rssi
        other_byte[95] = rssi[0];
        other_byte[96] = rssi[1];
        other_byte[97] = rssi[2];
        other_byte[98] = rssi[3];

        //赋值增益
        other_byte[99] = grain[0];
        other_byte[100] = grain[1];
        other_byte[101] = grain[2];
        other_byte[102] = grain[3];

        //拼接命令
        SET_PARAMS_COMMAND = new byte[SET_PARAMS_COMMAND_PART2.length + other_byte.length + 1]; //加一个校验位

        System.arraycopy(SET_PARAMS_COMMAND_PART2, 0, SET_PARAMS_COMMAND, 0, SET_PARAMS_COMMAND_PART2.length);
        System.arraycopy(other_byte, 0, SET_PARAMS_COMMAND, SET_PARAMS_COMMAND_PART2.length, other_byte.length);


        byte[] a = new byte[1];
        for (int i = 0; i < SET_PARAMS_COMMAND.length - 1; i++) {
            a[0] += SET_PARAMS_COMMAND[i];
        }

        a[0] = (byte) ((~a[0]) + 1);

        SET_PARAMS_COMMAND[SET_PARAMS_COMMAND.length - 1] = a[0];
    }

    public void uploadCheck() {

        if (TextUtils.isEmpty(device_id_et.getText().toString().trim())) { //标签为空
            XToastUtils.showShortToast(getResources().getString(R.string.enter_device_id));
            return;
        }

        if (device_id_et.getText().toString().trim().length() < DEVICE_ID_LENGTH) {
            XToastUtils.showShortToast(getResources().getString(R.string.device_id_length_less));
            return;
        }

        String port = device_port_et.getText().toString().trim();

        if (!CheckUtils.isEmpty(port) && (Integer.valueOf(port) < 0 || Integer.valueOf(port) > 65535)) {
            XToastUtils.showShortToast(getResources().getString(R.string.port_rang));
            return;
        }


        Map<String, String> params = new HashMap<String, String>();
        params.put("eqno", device_id_et.getText().toString().trim());
        params.put("orgids", user.getOrgids());

        OkGo.post(Path.EQU_WHETHER_EXIST_PATH).tag(this).params(params).execute(new StringComCallback() {
            @Override
            public void onSuccess(String result, Call call, Response response) {
                if (!TextUtils.isEmpty(result)) {
                    JsonResult jr = new Gson().fromJson(result, new TypeToken<JsonResult>() {
                    }.getType());
                    if (jr != null && jr.isFlag() && jr.getStatusCode() == 200) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(getResources().getString(R.string.notify));
                        builder.setMessage(getResources().getString(R.string.notify_content));
                        builder.setNegativeButton(getResources().getString(R.string.no), null);
                        builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                upload();
                            }
                        });

                        builder.create().show();
                    } else if (jr != null && jr.getStatusCode() == 400) { //设备不存在,直接上传
                        upload();
                    } else {
                        XToastUtils.showShortToast(getResources().getString(R.string.sys_problem));
                    }
                } else {
                    XToastUtils.showShortToast(getResources().getString(R.string.sys_problem));
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                XToastUtils.showShortToast(getResources().getString(R.string.sys_problem));
            }

        });


    }


    private void upload() {

        String filterTime = filter_et.getText().toString().trim();

        final Collector collector = new Collector();
        collector.setCollectorId(device_id_et.getText().toString().trim());

        String deviceIp = device_ip_et.getText().toString().trim();
        String devicePort = device_port_et.getText().toString().trim();
        if (CheckUtils.isEmpty(deviceIp)) {
            deviceIp = (String) device_ip_et.getHint();
        }
        if (CheckUtils.isEmpty(devicePort)) {
            devicePort = (String) device_port_et.getHint();
        }
        collector.setIp(deviceIp);
        collector.setPort(devicePort);
        collector.setImei(device_imei_et.getText().toString().trim());
        collector.setNumber(device_num_et.getText().toString().trim());
        //        collector.setOrgid(orgid); //组织机构主键
        //        collector.setOrgid(user.getOrgid()); //组织机构主键

        if (CheckUtils.isEmpty(filterTime)) {
            filterTime = (String) filter_et.getHint();
        }
        collector.setLibrepeat(filterTime);//去重过滤时间


        String jsonStr = new Gson().toJson(collector);
        Map<String, String> params = new HashMap<String, String>();
        params.put("json", jsonStr);
        params.put("orgids", user.getOrgids());

        OkGo.post(Path.UPLOAD_DEVICE_INFO_PATH).tag(this).params(params).execute(new StringDialogCallback(DeviceDebugAct.this, getResources().getString(R.string.upload_now)) {
            @Override
            public void onSuccess(String result, Call call, Response response) {
                if (!TextUtils.isEmpty(result)) {
                    JsonResult jr = new Gson().fromJson(result, new TypeToken<JsonResult>() {
                    }.getType());
                    if (jr != null && jr.isFlag() && jr.getStatusCode() == 200) {
                        String collectorId = collector.getCollectorId();
                        XToastUtils.showShortToast(getResources().getString(R.string.upload_success));
                        if (flag == Constant.DWGL) { //如果是从点位管理页面跳转过来
                            if (!TextUtils.isEmpty(poid)) {
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("id", poid);
                                OkGo.post(Path.CHANGE_POINT_STATE).tag(this).params(map).execute(new StringComCallback() {
                                    @Override
                                    public void onSuccess(String result, Call call, Response response) {

                                    }
                                });
                            }
                        }
                        startActivity(new Intent(context, EquipPosLookAct.class).putExtra("collectorId", collectorId).putExtra("flag", Constant.AFTER_UPLOAD_DEVICE_INFO_SUCCESS));
                    } else {
                        XToastUtils.showShortToast(getResources().getString(R.string.upload_fail));
                    }
                } else {
                    XToastUtils.showShortToast(getResources().getString(R.string.upload_fail));
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                XToastUtils.showShortToast(getResources().getString(R.string.upload_fail));
            }
        });
    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (sDialog != null && sDialog.isShowing()) {
                sDialog.dismiss();
            }
            switch (msg.what) {
                case BlueToothHelper.MSG_SET_PARAMS_OK:
                    XToastUtils.showShortToast(getResources().getString(R.string.set_params_success));
                    mSetting = false;
                    if (mp != null) {
                        mp.start();
                    }
                    break;

                case BlueToothHelper.MSG_SET_PARAMS_FAIL:
                    mSetting = false;
                    XToastUtils.showShortToast(getResources().getString(R.string.set_params_fail));
                    break;

                case BlueToothHelper.READ_TRIAD_PARAM_OK:
                    //三元组信息获取成功
                    if (mp != null) {
                        mp.start();
                    }
                    mScanning = false;
                    TriadInfo mTriadInfo = (TriadInfo) msg.obj;
                    if (mTriadInfo != null) {
                        mEditTriad.setText(mTriadInfo.toString());
                    }
                    break;
                case BlueToothHelper.READ_TRIAD_PARAM_FAIL:
                    //三元组信息获取失败
                    mScanning = false;
                    XToastUtils.showShortToast(getResources().getString(R.string.read_fail));
                    break;

                case BlueToothHelper.SET_TRIAD_PARAM_OK:
                    //设置三元组信息成功
                    if (mp != null) {
                        mp.start();
                    }
                    mScanning = false;
                    String hitMessage = (String) msg.obj;
                    if (!TextUtils.isEmpty(hitMessage)) {
                        XToastUtils.showShortToast(hitMessage);
                    }
                    break;
                case BlueToothHelper.SET_TRIAD_PARAM_FAIL:
                    //设置三元组信息失败
                    mScanning = false;
                    XToastUtils.showShortToast(getResources().getString(R.string.set_fail));
                    break;

                case BlueToothHelper.MSG_READ_IMEI_OK:
                    if (mp != null) {
                        mp.start();
                    }
                    mScanning = false;
                    String imei = msg.obj.toString();
                    device_imei_et.setText(imei);
                    break;

                case BlueToothHelper.MSG_READ_IMEI_FAIL:
                    mScanning = false;
                    XToastUtils.showShortToast(getResources().getString(R.string.read_fail));
                    break;

                case BlueToothHelper.MSG_READ_FACNUM_OK:
                    if (mp != null) {
                        mp.start();
                    }
                    mScanning = false;
                    String factory_num = msg.obj.toString();
                    device_num_et.setText(factory_num);
                    break;

                case BlueToothHelper.MSG_READ_FACUNM_FAIL:
                    mScanning = false;
                    XToastUtils.showShortToast(getResources().getString(R.string.read_fail));
                    break;


                case BlueToothHelper.MSG_READ_GPRS_OK:
                    if (mp != null) {
                        mp.start();
                    }
                    mScanning = false;
                    int gprsNum = (int) msg.obj;
                    gprs_num_et.setText("" + gprsNum);
                    break;

                case BlueToothHelper.MSG_READ_GPRS_FAIL:
                    mScanning = false;
                    XToastUtils.showShortToast(getResources().getString(R.string.read_fail));
                    break;

                case BlueToothHelper.MSG_READ_SYSTIME_OK:
                    if (mp != null) {
                        mp.start();
                    }
                    mScanning = false;
                    String time = msg.obj.toString();
                    systime_tv.setText(time);
                    break;


                case BlueToothHelper.MSG_READ_SYSTIME_FAIL:
                    mScanning = false;
                    XToastUtils.showShortToast(getResources().getString(R.string.read_fail));
                    break;

                case BlueToothHelper.MSG_SET_SYSTIME_OK:
                    if (mp != null) {
                        mp.start();
                    }
                    mSetting = false;
                    XToastUtils.showShortToast(getResources().getString(R.string.set_success));
                    break;

                case BlueToothHelper.MSG_SET_SYSTIME_FAIL:
                    mSetting = false;
                    XToastUtils.showShortToast(getResources().getString(R.string.set_fail));
                    break;

                case BlueToothHelper.MSG_NO_BLUETOOTH_SOCKET:
                    XToastUtils.showShortToast(getResources().getString(R.string.blueth_dismiss));
                    break;


                case BlueToothHelper.MSG_READ_GPRS_CONNECT_STATE_OK:
                    mScanning = false;
                    if (mp != null) {
                        mp.start();
                    }
                    Map<String, Object> result = (Map<String, Object>) msg.obj;
                    String ip = (String) result.get("gprsCntIp");
                    String port = (String) result.get("gprsCntPort");
                    int state = (int) result.get("gprsCntState");

                    if (state == 1) { //已经建立连接
                        gprs_connect_et.setText(ip + ":" + port + " " + getResources().getString(R.string.has_connect));
                    } else { //0表示未连接
                        gprs_connect_et.setText(ip + ":" + port + " " + getResources().getString(R.string.not_connect));
                    }

                    break;


                case BlueToothHelper.MSG_READ_GPRS_CONNECT_STATE_FAIL:
                    mScanning = false;
                    Toast.makeText(context, getResources().getString(R.string.read_fail), Toast.LENGTH_SHORT).show();
                    break;


                case BlueToothHelper.MSG_READ_LAN_CONNECT_STATE_OK:
                    mScanning = false;
                    if (mp != null) {
                        mp.start();
                    }
                    result = (Map<String, Object>) msg.obj;
                    ip = (String) result.get("lanCntIp");
                    port = (String) result.get("lanCntPort");
                    state = (int) result.get("lanCntState");

                    if (state == 1) { //已经建立连接
                        lan_connect_et.setText(ip + ":" + port + " " + getResources().getString(R.string.has_connect));
                    } else { //0表示未连接
                        lan_connect_et.setText(ip + ":" + port + " " + getResources().getString(R.string.not_connect));
                    }
                    break;

                case BlueToothHelper.MSG_DEVICE_UNSUPPORT_LAN:
                    mScanning = false;
                    lan_connect_et.setText("          :   " + getResources().getString(R.string.no_support));
                    break;

                case BlueToothHelper.MSG_READ_LAN_CONNECT_STATE_FAIL:
                    mScanning = false;
                    XToastUtils.showShortToast(getResources().getString(R.string.read_fail));
                    break;


                case BlueToothHelper.MSG_READ_CACHE_TAG_NUM_OK:
                    mScanning = false;
                    if (mp != null) {
                        mp.start();
                    }
                    if (msg.arg1 == 200) { //只有无线
                        int tagCacheNum = (int) msg.obj;
                        wx_tagnum_et.setText("" + tagCacheNum);
                        yx_tagnum_et.setText(getResources().getString(R.string.none));
                        gl_tagnum_et.setText(getResources().getString(R.string.none));
                    } else if (msg.arg1 == 500) {
                        int[] tagCacheNum = (int[]) msg.obj;
                        wx_tagnum_et.setText("" + tagCacheNum[0]);
                        yx_tagnum_et.setText("" + tagCacheNum[1]);
                        gl_tagnum_et.setText("" + tagCacheNum[2]);
                    }
                    break;

                case BlueToothHelper.MSG_READ_CACHE_TAG_NUM_FAIL:
                    mScanning = false;
                    XToastUtils.showShortToast(getResources().getString(R.string.read_fail));
                    break;

                default:
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();

        // register broadcast listeners
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter);

        reader = BlueToothHelper.getInstance(context, handler);

    }


    @Override
    protected void onStop() {
        try {
            this.unregisterReceiver(mReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStop();
    }


    private boolean isConnected() {

        SharedPreferences sp = getSharedPreferences(Constant.SP_CONNECT_STATUS, Context.MODE_PRIVATE);
        boolean status = sp.getBoolean(Constant.CONNECT_STATUS, false);

        if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC && status && (reader.getSocket() == null || !reader.getSocket().isConnected())) { //如果当前标记已连接，但是socket连接实际已经断了
            Editor editor = sp.edit();
            editor.putBoolean(Constant.CONNECT_STATUS, false);
            editor.commit();
            status = false;
        }


        return status;
    }

    @Override
    protected void onDestroy() {


        // setReadyStatus();

        if (mp != null) {
            mp.release();
            mp = null;
        }


        if (sDialog != null && sDialog.isShowing()) {
            sDialog.dismiss();
        }

        OkGo.getInstance().cancelTag(this);

        super.onDestroy();


    }


    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) { //蓝牙连接丢失监听广播，若丢失读标签按钮不可按
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                Toast.makeText(context, getResources().getString(R.string.blueth_dismiss), Toast.LENGTH_SHORT).show();
                read_device_btn.setEnabled(false);
                Message msg = handler.obtainMessage();
                msg.what = CMD_STOP;
                handler.sendMessage(msg);

                setReadyStatus();
            }
        }
    };

    /**
     * Set the Connection of BlueTooth with READY
     */
    private void setReadyStatus() {
        // save the READY status into config file.
        Editor editor = sharedPreferences.edit();
        editor.putBoolean("status", false);
        editor.commit();

        // Stop the Blue tooth helper.
        if (reader != null) {
            reader.close();
        }
    }


}
