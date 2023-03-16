package com.guoji.mobile.cocobee.activity;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bql.utils.CheckUtils;
import com.guo.duoduo.library.RadarScanView;
import com.guo.duoduo.randomtextview.RandomTextView;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.btreader.BlueToothHelper;
import com.guoji.mobile.cocobee.common.Constant;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.model.Tag;
import com.guoji.mobile.cocobee.model.TagRecord;
import com.guoji.mobile.cocobee.utils.BaseConversionUtil;
import com.guoji.mobile.cocobee.utils.TagComparator;
import com.guoji.mobile.cocobee.utils.Tools;
import com.guoji.mobile.cocobee.utils.XToastUtils;
import com.lzy.okgo.OkGo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author _H_JY
 * @date 16/11/1
 * 标签搜索页面，配合2.0/4.0蓝牙设备搜索标签
 */
public class SearchTagTempActivity extends BaseAct implements View.OnClickListener, AdapterView.OnItemClickListener {

    private Context context;
    private ImageView back_ib;
    private RadarScanView scanView;
    private TextView status_tv;
    private ListView mListView;
    private TagAdapter adapter;
    private Timer timer, clearTimer;
    private BlueToothHelper reader;
    private PopupWindow popupWindow;
    private TextView control_scan_tv, clear_record_tv, bukong_data_tv, choose_module_tv, module_tip_tv;
    private TextView tag_num_tv;
    private RelativeLayout control_scan_rl, clear_record_rl, bukong_data_rl, choose_module_rl;
    private List<Tag> dataList = new ArrayList<>();
    private View view, controlView;
    private Button set_btn, control_btn;
    private RandomTextView randomTextView;
    private int scanType = 1; //1是巡逻，2 是布控搜索
    private Button beep_btn;
    private boolean isSpeak = true;
    private MediaPlayer mp;
    private List<Map<String, Tag>> collectTags = new ArrayList<>();
    private List<TagRecord> netWorkTags = new ArrayList<>();
    private boolean isOpenTagDataOutput = false;

    private int controlFlag = 0;//0正在扫描,1未扫描

    private List<TagRecord> bkTags = new ArrayList<>();
    private final String RECORD_FILE_NAME = "/input_record_file.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_searchtag);
        context = this;
        mp = MediaPlayer.create(SearchTagTempActivity.this, R.raw.beep);
        reader = BlueToothHelper.getInstance(context, handler);

        if (isConnected()) { //如果已经连接，开启标签输出
            if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC) { //2.0
//                reader.openTagDataOutput();
                reader.openTagDataOutputOfSixByte();
            } else { //BLE
//                reader.bleSendCmd(Constant.OPEN_TAG_DATA_OUTPUT, BlueToothHelper.OPEN_TAG_OUTPUT_COMMAND, false);
                reader.bleSendCmd(Constant.OPEN_TAG_DATA_OUTPUT, BlueToothHelper.START_SET_SIX_BYTE_OF_SEARCH, false);

            }
            isOpenTagDataOutput = true;
        }
        initView();
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


    private void initView() {
        back_ib = (ImageView) findViewById(R.id.back_ib);
        mListView = (ListView) findViewById(R.id.listView);
        status_tv = (TextView) findViewById(R.id.status_tv);
        set_btn = (Button) findViewById(R.id.setting_btn);
        scanView = (RadarScanView) findViewById(R.id.scanView);
        randomTextView = (RandomTextView) findViewById(R.id.random_textview);
        beep_btn = (Button) findViewById(R.id.beep_btn);
        module_tip_tv = (TextView) findViewById(R.id.module_tip_tv);
        tag_num_tv = (TextView) findViewById(R.id.tag_num_tv);
        control_btn = (Button) findViewById(R.id.control_btn);

        if (isSpeak) {
            beep_btn.setBackgroundResource(R.drawable.speakenable);
        } else {
            beep_btn.setBackgroundResource(R.drawable.speakdisable);
        }

        back_ib.setOnClickListener(this);
        set_btn.setOnClickListener(this);
        beep_btn.setOnClickListener(this);
        control_btn.setOnClickListener(this);

        adapter = new TagAdapter(SearchTagTempActivity.this, dataList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isConnected()) {
            XToastUtils.showShortToast(getString(R.string.unconnected_device_not_search));
            status_tv.setText(getString(R.string.state_unconnected));
            control_btn.setBackgroundResource(R.drawable.ic_start_scan);
            stopScan();
        } else {
            status_tv.setText(getString(R.string.state_connected));
            control_btn.setBackgroundResource(R.drawable.ic_stop_scan);
            startScan();
        }
    }

    /*开启计时器*/
    public void startTimer() {
        timer = new Timer();
        timer.schedule(new MyTimerTask(), 0, 500); //每3秒刷新一次UI
    }

    /*开启计时器*/
    public void startClearTimer() {
        clearTimer = new Timer();
        clearTimer.schedule(new ClearTimerTask(), 0, 1000); //每3秒检查一次
    }


    /*停止计时器*/
    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /*停止计时器*/
    public void stopClearTimer() {
        if (clearTimer != null) {
            clearTimer.cancel();
            clearTimer = null;
        }
    }

    /*计时器任务*/
    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            handler.sendEmptyMessage(BlueToothHelper.MSG_SHOW_SEARCH_RESULT);
        }
    }

    /**
     * 计时器任务(每3秒检查数据集合里面的标签读到时间是否超过3秒,超过就清除)
     */
    class ClearTimerTask extends TimerTask {
        @Override
        public void run() {
            handler.sendEmptyMessage(BlueToothHelper.MSG_CLEAR_TASK);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_ib:
                finish();
                break;
            case R.id.setting_btn:
                showPopupWindow(view);
                break;
            case R.id.option2_rl: //清空数据
                if (adapter != null) {
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                }
                popupWindow.dismiss();
                break;

            case R.id.option3_rl: //布控数据
                control_btn.setBackgroundResource(R.drawable.ic_start_scan);
                stopScan();
                startActivityForResult(new Intent(context, BuKongAct.class)
                        .putExtra("netWorkTags", (Serializable) netWorkTags), 1000);
                popupWindow.dismiss();
                break;
            case R.id.beep_btn:
                if (isSpeak) {
                    isSpeak = false;
                    beep_btn.setBackgroundResource(R.drawable.speakdisable);
                } else {
                    isSpeak = true;
                    beep_btn.setBackgroundResource(R.drawable.speakenable);
                }
                break;
            case R.id.option4_rl:
                if (scanType == 1) { //巡逻
                    if (bkTags == null || bkTags.size() == 0) {
                        initBTRecordData();
                        if (netWorkTags.size() == 0) {
                            XToastUtils.showShortToast(getString(R.string.no_bukong_data));
                        } else {
                            bkTags.clear();
                            bkTags.addAll(netWorkTags);
                            setSearch();
                        }
                        popupWindow.dismiss();
                        return;
                    }
                    module_tip_tv.setText(getString(R.string.search_title));
                    tag_num_tv.setText("");
                    scanType = 2;
                    control_btn.setBackgroundResource(R.drawable.ic_start_scan);
                    stopScan();
                    /*如果已与设备建立连接，进入标签搜索页面后开启标签数据输出*/
                    if (isConnected()) {
                        if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC) {
//                            reader.filter();
                            reader.setSixByteFilter();
                        } else {
//                            reader.bleSendCmd(Constant.BLE_FILTER_TAG, BlueToothHelper.FILTER_COMMAND, false);
                            reader.bleSendCmd(Constant.BLE_FILTER_TAG, BlueToothHelper.START_SET_SIX_BYTE_OF_SEARCH, false);
                        }
                    }
                    control_btn.setBackgroundResource(R.drawable.ic_stop_scan);
                    startScan();
                } else {
                    module_tip_tv.setText(getString(R.string.patrol_search));
                    tag_num_tv.setText("");
                    scanType = 1;
                    control_btn.setBackgroundResource(R.drawable.ic_start_scan);
                    stopScan();
                    /*如果已与设备建立连接，进入标签搜索页面后开启标签数据输出*/
                    if (isConnected()) {
                        if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC) {
//                            reader.openTagDataOutput();
                            reader.openTagDataOutputOfSixByte();
                        } else {
//                            reader.bleSendCmd(Constant.OPEN_TAG_DATA_OUTPUT, BlueToothHelper.OPEN_TAG_OUTPUT_COMMAND, false);
                            reader.bleSendCmd(Constant.OPEN_TAG_DATA_OUTPUT, BlueToothHelper.START_SET_SIX_BYTE_OF_SEARCH, false);
                        }
                        isOpenTagDataOutput = true;
                    }
                    control_btn.setBackgroundResource(R.drawable.ic_stop_scan);
                    startScan();
                }
                randomTextView.getKeyWords().clear();
                if (adapter != null) {
                    adapter.clear();
                }
                popupWindow.dismiss();
                break;

            case R.id.control_btn:
                if (!isConnected()) {
                    XToastUtils.showShortToast(getString(R.string.unconnected_device));
                    status_tv.setText(getString(R.string.state_unconnected));
                    return;
                }
                btnSearch();
                break;
            default:
                break;
        }
    }

    //获取蓝牙记录数据，数据保存在本地.txt文件,并且保存在内存bkTags集合中
    private void initBTRecordData() {
        File file = new File(Path.RECORD_FILE_PATH + RECORD_FILE_NAME);
        StringBuffer sb = new StringBuffer();
        String line = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            //BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"gbk"));
            try {
                while ((line = br.readLine()) != null && !line.trim().equals("")) {
                    sb.append(line);
                    String[] temp = line.split(",");
                    TagRecord tagRecord = new TagRecord(temp[0], temp[1]);
                    boolean flag = false;
                    for (int i = 0; i < netWorkTags.size(); i++) {
                        if (tagRecord.getLno().equals(netWorkTags.get(i).getLno())) {
                            flag = true;
                        }
                    }
                    if (flag == false) {
                        tagRecord.setIsLocalRecord(true); //本地记录的标识
                        netWorkTags.add(tagRecord);
                    }
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void btnSearch() {
        if (controlFlag == 1) { //未扫描,开始扫描
            randomTextView.getKeyWords().clear();
            control_btn.setBackgroundResource(R.drawable.ic_stop_scan);
            startScan();
        } else if (controlFlag == 0) { //正在扫描,停止扫描
            control_btn.setBackgroundResource(R.drawable.ic_start_scan);
            stopScan();
            XToastUtils.showShortToast(getString(R.string.stop));
        }
    }

    /**
     * 开始扫描
     */
    private void startScan() {
        controlFlag = 0;
        reader.setScanFlag(false); //先停止已有线程
        reader.setScanFlag(true);
        scanView.startScanAnimation();
        if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC) {
            reader.scanTag();
        } else {
            reader.setBleCmdType(Constant.BLE_SCAN_TAG_TEMP);
        }
        if (scanType == 2) { //搜索
            startTimer();
            startClearTimer();
        } else if (scanType == 1) {//巡逻
            stopTimer();
            stopClearTimer();
        }
    }

    /**
     * 停止扫描
     */
    private void stopScan() {
        controlFlag = 1;
        reader.setScanFlag(false);
        scanView.stopScanAnimation();
        stopTimer(); //停止计时器
        stopClearTimer();
    }


    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BlueToothHelper.MSG_CLEAR_TASK:
                    long currentTimeMillis = System.currentTimeMillis();
                    Iterator<Tag> iterator = dataList.iterator();
                    while (iterator.hasNext()) {
                        Tag tag = iterator.next();
                        if ((currentTimeMillis - tag.getTime()) > 30000) {
                            //集合移除
                            iterator.remove();
                            //雷达移除
                            randomTextView.removeKeyWord(tag.getTagId());
                        }
                    }
                    adapter.notifyDataSetChanged();
                    tag_num_tv.setText(getString(R.string.tag_total) + dataList.size());
                    break;
                case BlueToothHelper.MSG_READ_TAG_OK:
                    Tag tag = (Tag) msg.obj;
                    boolean isNetWorkTag = false;
                    String flag = "";
                    String cno = "";
                    for (int i = 0; i < netWorkTags.size(); i++) {
                        if (tag.getTagId().equals(netWorkTags.get(i).getLno())) {
                            isNetWorkTag = true;
                            TagRecord tagRecord = netWorkTags.get(i);
                            flag = tagRecord.getFlag();
                            cno = tagRecord.getCno();
                            break;
                        }
                    }
                    if (isNetWorkTag) {
                        tag.setIsNetWorkTag(true);
                        tag.setFlag(flag);
                        tag.setCno(cno);
                    }
                    if (scanType == 1) { //巡逻模式
                        if (!dataList.contains(tag)) {
                            dataList.add(tag);
                            Collections.sort(dataList, new TagComparator());
                            adapter.notifyDataSetInvalidated();
                        } else {
                            for (int i = 0; i < dataList.size(); i++) {
                                if (tag.getTagId().equals(dataList.get(i).getTagId())) {
                                    dataList.remove(i);
                                    dataList.add(tag);
                                    Collections.sort(dataList, new TagComparator());
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }
                        //显示总数
                        tag_num_tv.setText(getString(R.string.tag_total) + dataList.size());
                        if (isSpeak) {
                            int absRssi = Math.abs(tag.getRssi());
                            play(absRssi); //播放声音
                        }
                    } else if (scanType == 2) { //布控搜索数据
                        //scanTimes++;
                        for (int i = 0; i < bkTags.size(); i++) {
                            if (tag.getTagId().equals(bkTags.get(i).getLno())) { //如果有信息跟布控集合里面的数据相等

                                /*发现目标标签，整理并收集数据*/
                                if (collectTags != null && collectTags.size() > 0) { //收集集合里面有数据
                                    boolean isUpdateTag = false;
                                    for (int j = 0; j < collectTags.size(); j++) {
                                        Map<String, Tag> mapTag = collectTags.get(j);
                                        Tag tag1 = mapTag.get("tag1");
                                        Tag tag2 = mapTag.get("tag2");
                                        if (tag1 != null && tag1.getTagId().equals(tag.getTagId())) { //更新的操作
                                            isUpdateTag = true; //是更新操作
                                            if (tag2 == null) {
                                                mapTag.put("tag2", tag);
                                            } else { //tag2 != null
                                                if (Math.abs(tag1.getRssi()) < Math.abs(tag2.getRssi())) { //tag1的信号强度比较强
                                                    mapTag.put("tag2", tag);
                                                } else { //tag2的信号强度比较强
                                                    mapTag.put("tag1", tag);
                                                }
                                            }
                                            collectTags.remove(j);
                                            collectTags.add(mapTag);
                                        }
                                    }

                                    if (!isUpdateTag) { //如果不是更新操作，直接添加
                                        Map<String, Tag> map = new HashMap<>();
                                        map.put("tag1", tag);
                                        map.put("tag2", null);
                                        collectTags.add(map);
                                    }
                                } else { //收集集合里面没有数据时，直接添加
                                    Map<String, Tag> map = new HashMap<>();
                                    map.put("tag1", tag);
                                    map.put("tag2", null);
                                    collectTags.add(map);
                                }
                                break;
                            }
                        }
                    }
                    if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_LE) {
                        reader.setIsNextTagDataCloudEnter(true);
                        reader.setIsFirstPackage(true);
                    }
                    break;
                case BlueToothHelper.MSG_READ_TAG_FAIL:
                    if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_LE) {
                        reader.setIsNextTagDataCloudEnter(true);
                        reader.setIsFirstPackage(true);
                    }
                    break;

                case BlueToothHelper.MSG_SHOW_SEARCH_RESULT:
                    if (collectTags != null && collectTags.size() > 0) {
                        //  dataList.clear(); //填充数据前先清空集合
                        for (int i = 0; i < collectTags.size(); i++) {
                            Map<String, Tag> map = collectTags.get(i); //一个Map就是一组数据
                            Tag tag1 = map.get("tag1");
                            Tag tag2 = map.get("tag2");
                            if (tag1 != null) {
                                Tag dataTag = new Tag();
                                if (tag2 != null) { //收集到两个通道，两个通道可能相同，可能不同
                                    int absTag1Rssi = Math.abs(tag1.getRssi());
                                    int absTag2Rssi = Math.abs(tag2.getRssi());
                                    int absRssi = (int) (((absTag1Rssi + absTag2Rssi) * 1.0f) / 2);
                                    dataTag.setRssi(-absRssi);
                                    dataTag.setTagId(tag1.getTagId());
                                    dataTag.setIsNetWorkTag(tag1.isNetWorkTag());
                                    dataTag.setFlag(tag1.getFlag());
                                    dataTag.setTime(tag1.getTime());

                                    dataTag.setLengthFactor((absRssi * 1.0f) / 100); //设置长度系数

                                    switch (tag1.getPosition()) {
                                        case 1: //第一个通道是东
                                            int angle = 0;
                                            if (tag2.getPosition() == 1) { //东
                                                angle = 0; //方向一致(东)，angle=0
                                            } else if (tag2.getPosition() == 2) { //南
                                                angle = 270 + (int) (90 * ((absTag2Rssi * 1.0f) / (absTag1Rssi + absTag2Rssi)));
                                            } else if (tag2.getPosition() == 3) { //西
                                                angle = absTag1Rssi < absTag2Rssi ? 0 : 180;
                                            } else if (tag2.getPosition() == 4) { //北
                                                angle = (int) (90 * ((absTag1Rssi * 1.0f) / (absTag1Rssi + absTag2Rssi)));
                                            }
                                            dataTag.setAngle(angle);
                                            break;

                                        case 2://第一个通道是南
                                            angle = 0;
                                            if (tag2.getPosition() == 1) { //东
                                                angle = 270 + (int) (90 * ((absTag1Rssi * 1.0f) / (absTag1Rssi + absTag2Rssi)));
                                            } else if (tag2.getPosition() == 2) { //南
                                                angle = 270;
                                            } else if (tag2.getPosition() == 3) { //西
                                                angle = 180 + (int) (90 * ((absTag2Rssi * 1.0f) / (absTag1Rssi + absTag2Rssi)));
                                            } else if (tag2.getPosition() == 4) { //北
                                                angle = absTag1Rssi < absTag2Rssi ? 270 : 90;
                                            }
                                            dataTag.setAngle(angle);
                                            break;

                                        case 3://第一个通道是西
                                            angle = 0;
                                            if (tag2.getPosition() == 1) { //东
                                                angle = absTag1Rssi < absTag2Rssi ? 180 : 0;
                                            } else if (tag2.getPosition() == 2) { //南
                                                angle = 180 + (int) (90 * ((absTag1Rssi * 1.0f) / (absTag1Rssi + absTag2Rssi)));
                                            } else if (tag2.getPosition() == 3) { //西
                                                angle = 180;
                                            } else if (tag2.getPosition() == 4) { //北
                                                angle = 90 + (int) (90 * ((absTag2Rssi * 1.0f) / (absTag1Rssi + absTag2Rssi)));
                                            }
                                            dataTag.setAngle(angle);
                                            break;

                                        case 4://第一个通道是北
                                            angle = 0;
                                            if (tag2.getPosition() == 1) { //东
                                                angle = (int) (90 * ((absTag2Rssi * 1.0f) / (absTag1Rssi + absTag2Rssi)));
                                            } else if (tag2.getPosition() == 2) { //南
                                                angle = absTag1Rssi < absTag2Rssi ? 90 : 270;
                                            } else if (tag2.getPosition() == 3) { //西
                                                angle = 90 + (int) (90 * ((absTag1Rssi * 1.0f) / (absTag1Rssi + absTag2Rssi)));
                                            } else if (tag2.getPosition() == 4) { //北
                                                angle = 90;
                                            }
                                            dataTag.setAngle(angle);
                                            break;
                                        default:
                                            break;
                                    }
                                } else { //tag2为null，只有一个通道，直接就表示正东、正南、正北、正西
                                    tag1.setLengthFactor((Math.abs(tag1.getRssi()) * 1.0f) / 100); //长度系数
                                    switch (tag1.getPosition()) {
                                        case 1: //东
                                            tag1.setAngle(0); //角度
                                            break;
                                        case 2: //南
                                            tag1.setAngle(270);
                                            break;

                                        case 3: //西
                                            tag1.setAngle(180);
                                            break;

                                        case 4: //北
                                            tag1.setAngle(90);
                                            break;
                                        default:
                                            break;
                                    }
                                    dataTag = tag1;
                                }

                                for (int j = 0; j < dataList.size(); j++) {
                                    if (CheckUtils.equalsString(dataTag.getTagId(), dataList.get(j).getTagId())) {
                                        dataList.remove(j);
                                        break;
                                    }
                                }
                                dataList.add(dataTag);
                            }

                        }

                        collectTags.clear(); //赋值给datalist完毕后，清空收集集合里面的内容

                        if (dataList != null && dataList.size() > 0) { //如果收集到数据

                            randomTextView.getKeyWords().clear();//显示前先清空雷达视图

                            int dataListLen = dataList.size();
                            int[] angles = new int[dataListLen];
                            float[] lenFactors = new float[dataListLen];
                            for (int l = 0; l < dataListLen; l++) {
                                Tag tempTag = dataList.get(l);
                                randomTextView.addKeyWord(tempTag.getTagId());
                                angles[l] = tempTag.getAngle(); //角度
                                lenFactors[l] = tempTag.getLengthFactor(); //长度系数
                            }

                            //显示雷达点位
                            randomTextView.show(angles, lenFactors);
                            //更新列表
                            Collections.sort(dataList, new TagComparator());
                            adapter.notifyDataSetChanged();
                            tag_num_tv.setText(getString(R.string.tag_total) + dataList.size());

                            if (isSpeak) {
                                int absRssi = dataList.get(0).getRssi();
                                play(Math.abs(absRssi));
                            }
                        }
                    }
                    break;

                case BlueToothHelper.MSG_NO_BLUETOOTH_SOCKET:
                    XToastUtils.showShortToast(getString(R.string.socket_lost));
                    break;
                default:
                    break;
            }
        }
    };


    public void play(int absRssi) {
        if (mp != null) {
            if (absRssi <= 25) {
                mp.setVolume(1.0f, 1.0f);
            } else if (absRssi > 25 && absRssi <= 35) {
                mp.setVolume(0.7f, 0.7f);
            } else if (absRssi > 35 && absRssi <= 40) {
                mp.setVolume(0.5f, 0.5f);
            } else if (absRssi > 40 && absRssi <= 50) {
                mp.setVolume(0.25f, 0.25f);
            } else if (absRssi > 50 && absRssi <= 70) {
                mp.setVolume(0.15f, 0.15f);
            } else if (absRssi > 70 && absRssi <= 80) {
                mp.setVolume(0.1f, 0.1f);
            } else {
                mp.setVolume(0.02f, 0.02f);
            }
            mp.start();
        }


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        boolean isNetWorkTag = false;
        TagRecord tagRecord = null;
        for (int i = 0; i < netWorkTags.size(); i++) {
            if (dataList.get(position).getTagId().equals(netWorkTags.get(i).getLno())) {
                isNetWorkTag = true;
                tagRecord = netWorkTags.get(i);
                break;
            }
        }
        if (isNetWorkTag) {
            /*跳转到详情界面，停止扫描*/
            control_btn.setBackgroundResource(R.drawable.ic_start_scan);
            stopScan();
            Intent intent = new Intent(context, TagDetailAct.class);
            intent.putExtra("tagRecord", tagRecord);
            startActivity(intent);
        } else {
            XToastUtils.showShortToast(getString(R.string.no_detail_info));
        }
    }


    public class TagAdapter extends BaseAdapter {

        private Context context;
        private List<Tag> list = new ArrayList<>();
        private LayoutInflater inflater;

        public TagAdapter(Context context, List<Tag> list) {
            this.context = context;
            this.list = list;
            inflater = LayoutInflater.from(context);
        }


        public void clear() {
            list.clear();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {

            Tag tag = list.get(position);
            ViewHolder viewHolder;
            if (view == null) {
                view = inflater.inflate(R.layout.lv_item_tag_info, null);
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.tag_id_tv.setText("(" + (position + 1) + getString(R.string.tags_id) + tag.getTagId());

            Log.e("xy", "tagId:" + tag.getTagId());
            int tagText = BaseConversionUtil.hexStringToAlgorism(tag.getTagId());
            Log.e("xy", "tagText:" + tagText);


            viewHolder.rssi_tv.setText("RSSI：" + tag.getRssi() + "dBm");
            //温度
            viewHolder.temp_tv.setText(getString(R.string.temp) + tag.getTemp() + "℃");

            if (tag.isNetWorkTag()) {
                if ("1".equals(tag.getFlag())) { //车辆标签
                    viewHolder.low_voltage_tv.setVisibility(View.VISIBLE);
                    viewHolder.low_voltage_tv.setText(getString(R.string.car_tag));
                    if (!TextUtils.isEmpty(tag.getCno())) {
                        viewHolder.cno_tv.setVisibility(View.VISIBLE);
                        viewHolder.cno_tv.setText(getString(R.string.car_num) + tag.getCno());
                    } else {
                        viewHolder.cno_tv.setVisibility(View.GONE);
                    }

                } else if ("2".equals(tag.getFlag())) {
                    viewHolder.low_voltage_tv.setVisibility(View.VISIBLE);
                    viewHolder.low_voltage_tv.setText(getString(R.string.person_tag));
                    viewHolder.cno_tv.setVisibility(View.GONE);
                }
            } else {
                viewHolder.low_voltage_tv.setVisibility(View.GONE);
                viewHolder.cno_tv.setVisibility(View.GONE);
            }

            return view;
        }
    }


    public class ViewHolder {
        private TextView temp_tv;
        private TextView tag_id_tv;
        private TextView low_voltage_tv;
        private TextView rssi_tv;
        private TextView cno_tv;

        public ViewHolder(View view) {
            this.tag_id_tv = (TextView) view.findViewById(R.id.tag_id_tv);
            this.low_voltage_tv = (TextView) view.findViewById(R.id.low_voltage_warn_tv);
            this.rssi_tv = (TextView) view.findViewById(R.id.rssi_tv);
            this.cno_tv = (TextView) view.findViewById(R.id.car_no_tv);
            this.temp_tv = (TextView) view.findViewById(R.id.temp_tv);

        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return false;
    }


    private void showPopupWindow(View parent) {
        if (popupWindow == null) {
            view = LayoutInflater.from(this).inflate(
                    R.layout.option_layout, null);
            view.setFocusable(true); // 这个很重要
            view.setFocusableInTouchMode(true);

            control_scan_rl = (RelativeLayout) view.findViewById(R.id.option1_rl);
            controlView = view.findViewById(R.id.view1);
            control_scan_tv = (TextView) view.findViewById(R.id.option1_tv);

            clear_record_rl = (RelativeLayout) view.findViewById(R.id.option2_rl);
            clear_record_tv = (TextView) view.findViewById(R.id.option2_tv);

            bukong_data_rl = (RelativeLayout) view.findViewById(R.id.option3_rl);
            bukong_data_tv = (TextView) view.findViewById(R.id.option3_tv);

            choose_module_rl = (RelativeLayout) view.findViewById(R.id.option4_rl);
            choose_module_tv = (TextView) view.findViewById(R.id.option4_tv);

            popupWindow = new PopupWindow(view, 600, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        control_scan_rl.setVisibility(View.GONE);
        controlView.setVisibility(View.GONE);

        if (reader != null) {
            if (reader.isScanFlag()) {
                control_scan_tv.setText(getString(R.string.stop_scan));
            } else {
                control_scan_tv.setText(getString(R.string.start_scan));
            }
            control_scan_rl.setOnClickListener(this);
        }

        clear_record_tv.setText(getString(R.string.clear_record));
        bukong_data_tv.setText(getString(R.string.bukong_data));
        choose_module_tv.setText(getString(R.string.mode_search_patrol));

        clear_record_rl.setOnClickListener(this);
        bukong_data_rl.setOnClickListener(this);
        choose_module_rl.setOnClickListener(this);

        popupWindow.setAnimationStyle(R.style.popwin_anim_style);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        backgroundAlpha(0.5f);
        // 添加pop窗口关闭事件
        popupWindow.setOnDismissListener(new popupDismissListener());
        // 重写onKeyListener
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    popupWindow.dismiss();
                    backgroundAlpha(1f);
                    return true;
                }
                return false;
            }
        });
        popupWindow.update();
        //popupWindow.showAtLocation(parent, Gravity.RELATIVE_LAYOUT_DIRECTION, 0, 0);
        popupWindow.showAsDropDown(parent, 0, 25);

    }

    public class popupDismissListener implements PopupWindow.OnDismissListener {
        @Override
        public void onDismiss() {
            backgroundAlpha(1f);
        }
    }

    private void backgroundAlpha(float value) { //设置窗体透明度
        Window window = this.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = value;
        window.setAttributes(lp);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == 2444) {
            bkTags = (List<TagRecord>) data.getSerializableExtra("bkList");
            tag_num_tv.setText("");
            randomTextView.getKeyWords().clear();
            if (!isConnected()) {
                XToastUtils.showShortToast(getString(R.string.unconnected_device_not_search));
                return;
            }
            setSearch();

        }
    }

    /**
     * 布控数据的添加搜索
     */
    private void setSearch() {
        int size = bkTags.size();
        System.out.println("#############totalCount:" + size);
        int totalPakage = (size - 1) / 62 + 1;
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int count = 0; count < totalPakage; count++) {
                    int commendSize;
                    if (count < totalPakage - 1) {
                        commendSize = 62;
                    } else {
                        commendSize = size % 62;
                    }
                    int finalCount = count;

                    SystemClock.sleep(300);
                    sendFilterCommend(commendSize, finalCount);
                    System.out.println("#############count: " + commendSize + "***** " + finalCount);
                }
            }
        }).start();

        scanType = 2;
        module_tip_tv.setText(getString(R.string.search_title));
        tag_num_tv.setText("");
        dataList.clear(); //清空所有数据
        adapter.notifyDataSetChanged();

        firstSearch(totalPakage);

    }

    /**
     * 发送过滤标签指令
     *
     * @param size
     * @param count
     */
    private void sendFilterCommend(int size, int count) {
        byte[] cmd = new byte[7 + size * 4];
        cmd[0] = 0x0A;  //sof
        cmd[1] = (byte) 0xFF; //addr
        cmd[2] = Tools.int2byte(4 + size * 4)[0]; //len
        cmd[3] = 0x2A;
        cmd[4] = 0x02;
        //        cmd[5] = 0x00;
        cmd[5] = Tools.int2byte(count)[0];
        int cmdIndex = 6;
        for (int i = 0; i < size; i++) {
            //            byte[] tagid = Tools.hexStringToByteArray(bkTags.get(i + count * 62).getLno());
            byte[] tagid = Tools.hexStringToByte(bkTags.get(i + count * 62).getLno());
            for (int j = 0; j < 4; j++) {
                cmd[cmdIndex++] = tagid[j];
            }
        }

        cmd[cmdIndex] = Tools.calCheck(cmd);

        //        control_btn.setBackgroundResource(R.drawable.ic_start_scan);
        stopScan();

        if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC) {
            reader.filterTagNumber(cmd);
        } else {
            reader.bleFilterTagNum(cmd);
        }
    }

    /**
     * 第一次切换搜索发送
     *
     * @param count
     */
    private void firstSearch(int count) {
        control_btn.setBackgroundResource(R.drawable.ic_start_scan);
        stopScan();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(500 * count);
                /*如果已与设备建立连接，进入标签搜索页面后开启标签数据输出*/
                if (isConnected()) {
                    if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC) {
//                        reader.filter();
                        reader.setSixByteFilter();
                    } else {
//                        reader.bleSendCmd(Constant.BLE_FILTER_TAG, BlueToothHelper.FILTER_COMMAND, false);
                        reader.bleSendCmd(Constant.OPEN_TAG_DATA_OUTPUT, BlueToothHelper.START_SET_SIX_BYTE_OF_SEARCH, false);


                    }
                }
                startScan();
            }
        }).start();
        control_btn.setBackgroundResource(R.drawable.ic_stop_scan);
        randomTextView.getKeyWords().clear();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (reader != null) {
            stopScan();
            /*如果已与设备建立连接，进入标签搜索页面后开启标签数据输出*/
            deviceStop();
        }
        if (mp != null) {
            mp.release();
            mp = null;
        }
        stopTimer();
        startClearTimer();
        OkGo.getInstance().cancelTag(this);
        super.onDestroy();

    }

    /**
     * 停止设备读卡
     */
    private void deviceStop() {
        if (isConnected() && isOpenTagDataOutput) {
            if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC) {
//                reader.closeTagDataOutput();
                reader.closeSixByteTagDataOutput();
            } else {
//                reader.bleSendCmd(Constant.BLE_CLOSE_TAG_DATA_OUTPUT, BlueToothHelper.CLOSE_TAG_OUTPUT_COMMAND, false);
                reader.bleSendCmd(Constant.BLE_CLOSE_TAG_DATA_OUTPUT, BlueToothHelper.STOP_SET_SIX_BYTE_OF_SEARCH, false);

            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(50);
                    if (isConnected()) {
                        if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC) {
//                            reader.closeTagDataOutput();
                            reader.closeSixByteTagDataOutput();
                        } else {
//                            reader.bleSendCmd(Constant.BLE_CLOSE_TAG_DATA_OUTPUT, BlueToothHelper.CLOSE_TAG_OUTPUT_COMMAND, false);
                            reader.bleSendCmd(Constant.BLE_CLOSE_TAG_DATA_OUTPUT, BlueToothHelper.STOP_SET_SIX_BYTE_OF_SEARCH, false);
                        }
                    }
                }
            }).start();
            isOpenTagDataOutput = false;
        }
    }
}
