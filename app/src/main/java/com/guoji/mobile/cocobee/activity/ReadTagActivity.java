package com.guoji.mobile.cocobee.activity;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.btreader.BlueToothHelper;
import com.guoji.mobile.cocobee.common.Constant;
import com.guoji.mobile.cocobee.model.Tag;
import com.guoji.mobile.cocobee.response.TagResponse;
import com.guoji.mobile.cocobee.utils.TagComparator;
import com.guoji.mobile.cocobee.utils.XToastUtils;
import com.guo.duoduo.library.RadarScanView;
import com.guo.duoduo.randomtextview.RandomTextView;
import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 标签读取页面
 */
public class ReadTagActivity extends BaseAct implements View.OnClickListener, AdapterView.OnItemClickListener {

    private Context context;
    private ImageButton back_ib;
    private RadarScanView scanView;
    private TextView status_tv;
    private ListView mListView;
    private TagAdapter adapter;
    private BlueToothHelper reader;
    private PopupWindow popupWindow;
    private TextView choose_module_tv, module_tip_tv;
    private TextView tag_num_tv;
    private RelativeLayout choose_module_rl;
    private List<Tag> dataList = new ArrayList<>();
    private View view;
    private Button set_btn, control_btn;
    private RandomTextView randomTextView;
    private int scanType = 0; //0 ,1是查车，2 是发卡
    private Button beep_btn;
    private boolean isSpeak = true;
    private MediaPlayer mp;
    private List<Map<String, Tag>> collectTags = new ArrayList<>();
    private boolean isOpenRF = false;

    private int controlFlag = 1;
    private boolean isStopFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_searchtag);
        context = this;
        mp = MediaPlayer.create(ReadTagActivity.this, R.raw.beep);
        reader = BlueToothHelper.getInstance(context, handler);

        if (isConnected()) { //如果已经连接，设置模式
            reader.bleSendCmd(Constant.BLE_SET_MODE_ONE, BlueToothHelper.SET_MODE_TWO, false);
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
        back_ib = (ImageButton) findViewById(R.id.back_ib);
        mListView = (ListView) findViewById(R.id.listView);
        status_tv = (TextView) findViewById(R.id.status_tv);
        set_btn = (Button) findViewById(R.id.setting_btn);
        scanView = (RadarScanView) findViewById(R.id.scanView);
        randomTextView = (RandomTextView) findViewById(R.id.random_textview);
        beep_btn = (Button) findViewById(R.id.beep_btn);
        module_tip_tv = (TextView) findViewById(R.id.module_tip_tv);
        tag_num_tv = (TextView) findViewById(R.id.tag_num_tv);
        control_btn = (Button) findViewById(R.id.control_btn);
        module_tip_tv.setText("查车");
        if (isSpeak) {
            beep_btn.setBackgroundResource(R.drawable.speakenable);
        } else {
            beep_btn.setBackgroundResource(R.drawable.speakdisable);
        }

        back_ib.setOnClickListener(this);
        set_btn.setOnClickListener(this);
        beep_btn.setOnClickListener(this);
        control_btn.setOnClickListener(this);


        adapter = new TagAdapter(ReadTagActivity.this, dataList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);

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
                if (!isConnected()) {
                    Toast.makeText(context, "未连接设备，无法操作", Toast.LENGTH_SHORT).show();
                    status_tv.setText("状态：未连接");
                    return;
                }
                isStopFlag = true;//先停止取数据设置完成再开启
                stopReadRF();
                adapter.clear();
                adapter.notifyDataSetChanged();
                if (scanType == 0 || scanType == 2) { //查车
                    module_tip_tv.setText("【发卡】");
                    tag_num_tv.setText("");
                    scanType = 1;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SystemClock.sleep(1000);
                            reader.bleSendCmd(Constant.BLE_SET_MODE_ONE, BlueToothHelper.SET_MODE_ONE, false);
                        }
                    }).start();
                } else {//发卡
                    module_tip_tv.setText("【查车】");
                    tag_num_tv.setText("");
                    scanType = 2;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SystemClock.sleep(1000);
                            reader.bleSendCmd(Constant.BLE_SET_MODE_TWO, BlueToothHelper.SET_MODE_TWO, false);
                        }
                    }).start();
                }
                randomTextView.getKeyWords().clear();

                if (adapter != null) {
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                }
                popupWindow.dismiss();
                break;


            case R.id.control_btn:
                if (!isConnected()) {
                    Toast.makeText(context, "未连接设备，无法操作", Toast.LENGTH_SHORT).show();
                    status_tv.setText("状态：未连接");
                    return;
                }

                btnSearch();
                break;
        }
    }

    //停止读卡
    private void stopReadRF() {
        reader.bleSendCmd(Constant.BLE_CLOSE_RF, BlueToothHelper.CLOSE_RF, false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(500);
                reader.bleSendCmd(Constant.BLE_CLOSE_RF, BlueToothHelper.CLOSE_RF, false);
            }
        }).start();
    }


    private void btnSearch() {
        if (controlFlag == 0) { //停止读取
            randomTextView.getKeyWords().clear();
            startScan();
            reader.bleSendCmd(Constant.BLE_OPEN_RF, BlueToothHelper.OPEN_RF, false);

        } else if (controlFlag == 1) { //读取
            stopScan();
            stopReadRF();
            Toast.makeText(context, "已停止", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!isConnected()) {
            Toast.makeText(context, "未连接设备，无法读取标签", Toast.LENGTH_SHORT).show();
            status_tv.setText("状态：未连接");
            stopScan();
        } else {
            status_tv.setText("状态：已连接");
            if (controlFlag == 0) {//停止扫描的
                stopScan();
            } else {//扫描的
                startScan();
            }
        }
    }

    private void stopScan() {
        controlFlag = 0;
        control_btn.setBackgroundResource(R.drawable.ic_start_scan);
        scanView.stopScanAnimation();
        isStopFlag = true;
        isOpenRF = false;
    }

    private void startScan() {
        controlFlag = 1;
        control_btn.setBackgroundResource(R.drawable.ic_stop_scan);
        isStopFlag = false;
        isOpenRF = true;
        scanView.startScanAnimation();
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case BlueToothHelper.MSG_NO_BLUETOOTH_SOCKET:
                    Toast.makeText(context, "蓝牙Socket已丢失，请重新连接设备", Toast.LENGTH_SHORT).show();
                    break;
                case BlueToothHelper.MSG_SET_MODE_OK:
                    if (isConnected()) {
                        openRF();
                    } else {
                        XToastUtils.showShortToast("未连接设备,无法操作");
                    }
                    break;
                case BlueToothHelper.MSG_SET_MODE_FAIL:
                    XToastUtils.showShortToast("模式设置失败");
                    if (isOpenRF) {//在读卡时设置失败,继续取数据
                        isStopFlag = false;
                        getData();
                    }
                    break;
                case BlueToothHelper.MSG_OPEN_RF_OK://打开读卡成功
                    if (isConnected()) { //如果已经连接
                        startScan();
                        //打开读卡成功,取数据
                        getData();
                    } else {
                        XToastUtils.showShortToast("未连接设备,无法操作");
                    }
                    break;
                case BlueToothHelper.MSG_OPEN_RF_FAIL://打开读卡失败
                    XToastUtils.showShortToast("打开读卡失败");
                    break;
                case BlueToothHelper.MSG_CLOSE_RF_OK://关闭读卡成功
                    stopScan();
                    break;
                case BlueToothHelper.MSG_CLOSE_RF_FAIL://关闭读卡失败
                    break;
                case BlueToothHelper.MSG_GET_DATA_OK://获取读卡数据成功

                    List<Tag> tags = (List<Tag>) msg.obj;
                    for (int i = 0; i < tags.size(); i++) {
                        if (!dataList.contains(tags.get(i))) {
                            dataList.add(tags.get(i));
                            Collections.sort(dataList, new TagComparator());
                            adapter.notifyDataSetInvalidated();
                        } else {
                            for (int j = 0; j < dataList.size(); j++) {
                                if (tags.get(i).getTagId().equals(dataList.get(j).getTagId())) {
                                    dataList.remove(j);
                                    dataList.add(tags.get(i));
                                    Collections.sort(dataList, new TagComparator());
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }

                    }

                    //显示总数
                    tag_num_tv.setText("标签总数：" + dataList.size());
                    if (isSpeak) {
                        mp.start(); //播放声音
                    }
                    if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_LE) {
                        reader.setIsNextTagDataCloudEnter(true);
                        reader.setIsFirstPackage(true);
                    }
                    getData();
                    break;
                case BlueToothHelper.MSG_GET_DATA_NONE://未读取到数据
                    getData();
                    break;
                case BlueToothHelper.MSG_GET_DATA_FAIL://获取读卡数据失败
                    XToastUtils.showShortToast("读卡失败");
                    break;
            }
        }
    };

    //打开读卡
    private void openRF() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(500);
                reader.bleSendCmd(Constant.BLE_OPEN_RF, BlueToothHelper.OPEN_RF, false);//设置模式成功打开读卡
            }
        }).start();
    }

    //获取数据
    private void getData() {
        if (!isStopFlag && isConnected()) {//已连接且开始扫描
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(1000);
                    if (!isStopFlag) {
                        reader.bleSendCmd(Constant.GET_DATA, BlueToothHelper.GET_COMMENT_DATA, true);
                    }
                }
            }).start();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        /*跳转到详情界面，停止扫描*/
        stopScan();
        stopReadRF();

        TagResponse tagResponse = new TagResponse();
        tagResponse.setLno(dataList.get(position).getTagId());
        tagResponse.setScanType(scanType);

        //申请定位权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //进入到这里代表没有获取定位权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //已经禁止提示了
                Toast.makeText(this, "您已禁止该权限，需要重新开启", Toast.LENGTH_SHORT).show();
                return;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 11);
            }
        }
        Intent intent = new Intent(context, TagDetailActivity.class);
        intent.putExtra("tagResponse", tagResponse);
        startActivity(intent);

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
                view = inflater.inflate(R.layout.lv_item_read_tag, null);
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.tag_id_tv.setText("(" + (position + 1) + ") 标签ID： " + tag.getTagId());

            return view;
        }
    }


    public class ViewHolder {
        private TextView tag_id_tv;

        public ViewHolder(View view) {
            this.tag_id_tv = (TextView) view.findViewById(R.id.tag_id_tv);
        }
    }

    private void showPopupWindow(View parent) {
        if (popupWindow == null) {
            view = LayoutInflater.from(this).inflate(
                    R.layout.option_layout_tag, null);
            view.setFocusable(true); // 这个很重要
            view.setFocusableInTouchMode(true);

            choose_module_rl = (RelativeLayout) view.findViewById(R.id.option4_rl);
            choose_module_tv = (TextView) view.findViewById(R.id.option4_tv);

            popupWindow = new PopupWindow(view, 600, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        choose_module_tv.setText("模式【查车<->发卡】");

        choose_module_rl.setOnClickListener(this);

        popupWindow.setAnimationStyle(R.style.popwin_anim_style);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        backgroundAlpha(0.5f);
        // 添加pop窗口关闭事件
        popupWindow.setOnDismissListener(new popupDismissListener());
        // 重写onKeyListener
        view.setOnKeyListener(new View.OnKeyListener() {
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
        popupWindow.showAsDropDown(parent, 0, 25);

    }

    public class popupDismissListener implements PopupWindow.OnDismissListener {
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
    protected void onDestroy() {
        if (reader != null) {
            reader.setScanFlag(false);
            isStopFlag = true;
            if (isConnected() && isOpenRF) {
                reader.bleSendCmd(Constant.BLE_CLOSE_RF, BlueToothHelper.CLOSE_RF, false);
                isOpenRF = false;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(500);
                    reader.bleSendCmd(Constant.BLE_CLOSE_DEVICE, BlueToothHelper.CLOSE_DEVICE, false);
                }
            }).start();
        }
        if (mp != null) {
            mp.release();
            mp = null;
        }

        OkGo.getInstance().cancelTag(this);
        super.onDestroy();

    }
}
