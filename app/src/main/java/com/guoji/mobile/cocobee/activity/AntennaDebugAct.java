package com.guoji.mobile.cocobee.activity;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.btreader.BlueToothHelper;
import com.guoji.mobile.cocobee.common.Constant;
import com.guoji.mobile.cocobee.model.Tag;
import com.guoji.mobile.cocobee.utils.LogUtil;
import com.guoji.mobile.cocobee.utils.Tools;
import com.guoji.mobile.cocobee.utils.XToastUtils;

import java.util.HashSet;

/**
 * 天线安装调试页面
 * Created by marktrace on 16/11/23.
 */
public class AntennaDebugAct extends BaseAct implements View.OnClickListener {

    private Context context;
    private ImageButton back_ib;
    private boolean status;
    private Button start_read_btn, stop_read_btn;
    private EditText tag_id_et, tx_one_rssi_et, tx_one_times_et, tx_two_rssi_et, tx_two_times_et, tx_three_rssi_et, tx_three_times_et, tx_four_rssi_et, tx_four_times_et;
    private BlueToothHelper reader;// Blue Tooth reader
    private int tx_one_times, tx_two_times, tx_three_times, tx_four_times;
    private MediaPlayer mp;
    private int readType = 0;
    private TextView tx_info_tv;

    private HashSet<String> txOneSet = new HashSet<>();
    private HashSet<String> txTwoSet = new HashSet<>();
    private HashSet<String> txThreeSet = new HashSet<>();
    private HashSet<String> txFourSet = new HashSet<>();


    private EditText one_read_tag_num_et, two_read_tag_num_et, three_read_tag_num_et, four_read_tag_num_et;
    private EditText tag_total_num_et;

    private int one_read_tag_num = 0, two_read_tag_num = 0, three_read_tag_num = 0, four_read_tag_num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_antennadebug);
        context = this;

        reader = BlueToothHelper.getInstance(context, handler);
        mp = MediaPlayer.create(AntennaDebugAct.this, R.raw.beep);
        initView();
    }

    private void initView() {
        back_ib = (ImageButton) findViewById(R.id.back_ib);
        start_read_btn = (Button) findViewById(R.id.start_read_btn);
        stop_read_btn = (Button) findViewById(R.id.stop_read_btn);
        tag_id_et = (EditText) findViewById(R.id.tag_id_et);
        tx_one_rssi_et = (EditText) findViewById(R.id.tx_one_rssi_et);
        tx_one_times_et = (EditText) findViewById(R.id.tx_one_times_et);
        tx_two_rssi_et = (EditText) findViewById(R.id.tx_two_rssi_et);
        tx_two_times_et = (EditText) findViewById(R.id.tx_two_times_et);
        tx_three_rssi_et = (EditText) findViewById(R.id.tx_three_rssi_et);
        tx_three_times_et = (EditText) findViewById(R.id.tx_three_times_et);
        tx_four_rssi_et = (EditText) findViewById(R.id.tx_four_rssi_et);
        tx_four_times_et = (EditText) findViewById(R.id.tx_four_times_et);
        one_read_tag_num_et = (EditText) findViewById(R.id.one_read_tag_num_et);
        two_read_tag_num_et = (EditText) findViewById(R.id.two_read_tag_num_et);
        three_read_tag_num_et = (EditText) findViewById(R.id.three_read_tag_num_et);
        four_read_tag_num_et = (EditText) findViewById(R.id.four_read_tag_num_et);
        tag_total_num_et = (EditText) findViewById(R.id.tag_total_num_et);
        tx_info_tv = (TextView) findViewById(R.id.tx_info_tv);

        back_ib.setOnClickListener(this);
        tx_info_tv.setOnClickListener(this);

        start_read_btn.setOnClickListener(this);
        stop_read_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_ib:
                finish();
                break;

            case R.id.start_read_btn:
                startRead();
                break;

            case R.id.stop_read_btn:
                stopRead();
                break;

            case R.id.tx_info_tv:
                stopRead(); //跳转到其他页面时，如果当前页面正在读卡，则停止
                startActivity(new Intent(context, AntennaInfoAct.class));
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        reader = BlueToothHelper.getInstance(context, handler);
    }

    private void startRead() {
        SharedPreferences sp = getSharedPreferences(Constant.SP_CONNECT_STATUS, Context.MODE_PRIVATE);
        status = sp.getBoolean(Constant.CONNECT_STATUS, false);

        if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC && status && (reader.getSocket() == null || !reader.getSocket().isConnected())) { //如果当前标记已连接，但是socket连接实际已经断了
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(Constant.CONNECT_STATUS, false);
            editor.commit();
            status = false;
        }

        if (!status) {
            XToastUtils.showShortToast(getResources().getString(R.string.no_connect_set));
            return;
        }

        String tagidString = tag_id_et.getText().toString().trim();
        if (!TextUtils.isEmpty(tagidString) && tagidString.length() < 12) {
            XToastUtils.showShortToast(getResources().getString(R.string.label_length_error));
            return;
        }
        if (reader != null && reader.isDebugTXFlag()) {
            XToastUtils.showShortToast(getResources().getString(R.string.reading_now));
            return;
        }
        XToastUtils.showShortToast(getResources().getString(R.string.start_read));
        //清除计数次数
        tx_one_times = 0;
        tx_two_times = 0;
        tx_three_times = 0;
        tx_four_times = 0;
        one_read_tag_num = 0;
        two_read_tag_num = 0;
        three_read_tag_num = 0;
        four_read_tag_num = 0;

        txOneSet.clear();
        txTwoSet.clear();
        txThreeSet.clear();
        txFourSet.clear();

        one_read_tag_num_et.setText("");
        two_read_tag_num_et.setText("");
        three_read_tag_num_et.setText("");
        four_read_tag_num_et.setText("");

        tx_one_rssi_et.setText("");
        tx_one_times_et.setText("");
        tx_two_rssi_et.setText("");
        tx_two_times_et.setText("");
        tx_three_rssi_et.setText("");
        tx_three_times_et.setText("");
        tx_four_rssi_et.setText("");
        tx_four_times_et.setText("");

        reader.setDebugTXFlag(true);
        /**
         * 需要在原来四个字节的基础上增加6个字节的标签，6个字节兼容了4个字节
         */
        if (TextUtils.isEmpty(tagidString)) {
            readType = 1; //巡逻模式
            LogUtil.I("巡逻模式");
            if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC) {
                reader.debugTX(BlueToothHelper.DEBUG_TX_COMMAND);
//                reader.debugTX(BlueToothHelper.GET_SIX_TAG_LABEL);

            } else {
                reader.bleDebugTx(BlueToothHelper.DEBUG_TX_COMMAND);
//                reader.bleDebugTx(BlueToothHelper.GET_SIX_TAG_LABEL);
            }

        } else { //搜索标签模式
            readType = 2;
            byte[] tagid = Tools.hexStringToByteArray(tagidString);

            //4个字节
            byte[] command = new byte[]{0x0A, (byte) 0xFF, 0x06, 0x18, tagid[0], tagid[1], tagid[2], tagid[3], 0x00};

            //6个字节
//            byte[] command = new byte[]{0x0A, (byte) 0xFF, 0x02, 0x72, tagid[0], tagid[1], tagid[2], tagid[3], (byte) 0x83};

            byte[] a = new byte[1];
            for (int i = 0; i < command.length - 1; i++) {
                a[0] += command[i];
            }
            command[8] = (byte) ((~a[0]) + 1);

            if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC) {
                reader.debugTX(command);
            } else {
                reader.bleDebugTx(command);
            }

        }


    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BlueToothHelper.MSG_DEBUG_TX_OK:
                    //获取标签编号
                    Tag tag = (Tag) msg.obj;
                    String tagid = tag.getTagId();
                    int totalNum = tag.getTotalTagNum();

                    if (totalNum == -1) {
                        tag_total_num_et.setText(getResources().getString(R.string.this_not_support));
                    } else {
                        tag_total_num_et.setText("" + totalNum);
                    }

                    String targetId = tag_id_et.getText().toString().trim().toUpperCase();
                    if (readType == 1) { //巡逻
                        tag_id_et.setText(tagid);
                        if (mp != null) {
                            mp.start();
                        }

                        switch (tag.getPosition()) {
                            case 1: //天线1
                                txOneSet.add(tagid);
                                one_read_tag_num = txOneSet.size();
                                one_read_tag_num_et.setText("" + one_read_tag_num);

                                tx_one_rssi_et.setText(tag.getRssi() + "");
                                tx_one_times++;
                                tx_one_times_et.setText(tx_one_times + "");
                                break;
                            case 2://天线2
                                txTwoSet.add(tagid);
                                two_read_tag_num = txTwoSet.size();
                                two_read_tag_num_et.setText("" + two_read_tag_num);

                                tx_two_rssi_et.setText(tag.getRssi() + "");
                                tx_two_times++;
                                tx_two_times_et.setText(tx_two_times + "");
                                break;
                            case 3://天线3
                                txThreeSet.add(tagid);
                                three_read_tag_num = txThreeSet.size();
                                three_read_tag_num_et.setText("" + three_read_tag_num);

                                tx_three_rssi_et.setText(tag.getRssi() + "");
                                tx_three_times++;
                                tx_three_times_et.setText(tx_three_times + "");
                                break;

                            case 4://天线4
                                txFourSet.add(tagid);
                                four_read_tag_num = txFourSet.size();
                                four_read_tag_num_et.setText("" + four_read_tag_num);

                                tx_four_rssi_et.setText(tag.getRssi() + "");
                                tx_four_times++;
                                tx_four_times_et.setText(tx_four_times + "");
                                break;
                        }
                    } else {
                        if (tag.getTagId() != null && tag.getTagId().equals(targetId)) {
                            if (mp != null) {
                                mp.start();
                            }
                            switch (tag.getPosition()) {
                                case 1: //天线1
                                    txOneSet.add(tagid);
                                    one_read_tag_num = txOneSet.size();
                                    one_read_tag_num_et.setText("" + one_read_tag_num);
                                    tx_one_rssi_et.setText(tag.getRssi() + "");
                                    tx_one_times++;
                                    tx_one_times_et.setText(tx_one_times + "");
                                    break;
                                case 2://天线2
                                    txTwoSet.add(tagid);
                                    two_read_tag_num = txTwoSet.size();
                                    two_read_tag_num_et.setText("" + two_read_tag_num);
                                    tx_two_rssi_et.setText(tag.getRssi() + "");
                                    tx_two_times++;
                                    tx_two_times_et.setText(tx_two_times + "");
                                    break;
                                case 3://天线3
                                    txThreeSet.add(tagid);
                                    three_read_tag_num = txThreeSet.size();
                                    three_read_tag_num_et.setText("" + three_read_tag_num);
                                    tx_three_rssi_et.setText(tag.getRssi() + "");
                                    tx_three_times++;
                                    tx_three_times_et.setText(tx_three_times + "");
                                    break;

                                case 4://天线4
                                    txFourSet.add(tagid);
                                    four_read_tag_num = txFourSet.size();
                                    four_read_tag_num_et.setText("" + four_read_tag_num);
                                    tx_four_rssi_et.setText(tag.getRssi() + "");
                                    tx_four_times++;
                                    tx_four_times_et.setText(tx_four_times + "");
                                    break;
                            }
                        }
                    }


                    break;


                case BlueToothHelper.MSG_NO_BLUETOOTH_SOCKET:
                    XToastUtils.showShortToast(getResources().getString(R.string.blueth_dismiss));
                    break;

                case BlueToothHelper.MSG_DEBUG_TX_FAIL:
                    break;
            }
        }
    };


    private void stopRead() {

        if (reader != null && reader.isDebugTXFlag()) {
            reader.setDebugTXFlag(false);

            if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC) {
                reader.stopDebugTx();
            } else {
                reader.bleSendCmd(Constant.BLE_STOP_DEBUG_TX, BlueToothHelper.STOP_DEBUG_TX_COMMAND, false);
            }

            XToastUtils.showShortToast(getResources().getString(R.string.has_stop_read));

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tag_id_et.setText("");
                }
            }, 500);


        }

    }


    @Override
    protected void onDestroy() {


        stopRead();

        if (mp != null) {
            mp.release();
            mp = null;
        }

        super.onDestroy();
    }
}
