package com.guoji.mobile.cocobee.fragment;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.BindCarAct;
import com.guoji.mobile.cocobee.activity.ConnectAct;
import com.guoji.mobile.cocobee.activity.DeviceDebugAct;
import com.guoji.mobile.cocobee.activity.PointManageListAct;
import com.guoji.mobile.cocobee.activity.SearchTagAct;
import com.guoji.mobile.cocobee.activity.ShowMapAct;
import com.guoji.mobile.cocobee.adapter.QueryAdapter;
import com.guoji.mobile.cocobee.btreader.BlueToothHelper;
import com.guoji.mobile.cocobee.common.Constant;
import com.guoji.mobile.cocobee.fragment.base.BaseFragment;
import com.guoji.mobile.cocobee.model.User;
import com.guoji.mobile.cocobee.response.QueryRecResponse;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.view.ListLineDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * 管理的fragment
 * Created by Administrator on 2017/4/17.
 */

public class ManagerFragment extends BaseFragment implements QueryAdapter.OnAdapterClick {

    @BindView(R.id.recycle_view)
    RecyclerView mRecycleView;
    private User mUser;

    //第一层RecycleView的list
    private List<QueryRecResponse> mList = new ArrayList();
    String[] titles;
    List<List<QueryRecResponse.QueryResponse>> mLists = new ArrayList<>();
    int[] images;
    String[] texts;
    //实时查询的list
    private List<QueryRecResponse.QueryResponse> mList1;
    //信息查询的list
    private List<QueryRecResponse.QueryResponse> mList2;
    //搜索
    private List<QueryRecResponse.QueryResponse> mList3;

    //蓝牙
    private BlueToothHelper reader;// Blue Tooth reader

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BlueToothHelper.MSG_CARRIER_TEST_OK://发送载波指令成功
                    Toast.makeText(_mActivity, "指令发送成功", Toast.LENGTH_SHORT).show();
                    break;

                case BlueToothHelper.MSG_CARRIER_TEST_FIAL://发送载波指令失败
                    Toast.makeText(_mActivity, "指令发送失败", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;

            }
        }
    };


    public static ManagerFragment getInstance() {
        ManagerFragment settingFragment = new ManagerFragment();
        return settingFragment;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        mUser = Utils.getUserLoginInfo();
        // Init the reader
        reader = BlueToothHelper.getInstance(_mActivity, handler);
        surePic();
        for (int i = 0; i < titles.length; i++) {
            QueryRecResponse queryRecResponse = new QueryRecResponse();
            queryRecResponse.setTitle(titles[i]);
            queryRecResponse.setQueryResponseList(mLists.get(i));
            mList.add(queryRecResponse);
        }

        QueryAdapter adapter = new QueryAdapter(_mActivity, mList);
        mRecycleView.setLayoutManager(new LinearLayoutManager(_mActivity));
        mRecycleView.addItemDecoration(new ListLineDecoration());
        mRecycleView.setAdapter(adapter);
        adapter.setOnAdapterClick(this);
    }

    @Override
    public boolean inVisibleLeftDrawable() {
        return false;
    }

    private void surePic() {
        titles = new String[]{"连接绑定", "安装采集", "搜索"};

        mList1 = new ArrayList<>();
        images = new int[]{R.drawable.connection, R.drawable.bind};
        texts = new String[]{"设备连接", "标签绑定"};
        addItems(mList1);

        mList2 = new ArrayList<>();
        images = new int[]{R.drawable.debug, R.drawable.ic_dwgl, R.drawable.caidian};
        texts = new String[]{"安装调试", "点位管理", "手动采点"};
        addItems(mList2);

        mList3 = new ArrayList<>();
        images = new int[]{R.drawable.search, R.drawable.carrier};
        texts = new String[]{"标签搜索", "载波测试"};
        addItems(mList3);

        mLists.add(mList1);
        mLists.add(mList2);
        mLists.add(mList3);
    }


    //为list添加元素
    private void addItems(List<QueryRecResponse.QueryResponse> list) {
        for (int i = 0; i < images.length; i++) {
            QueryRecResponse.QueryResponse queryResponse = new QueryRecResponse.QueryResponse();
            queryResponse.setText(texts[i]);
            queryResponse.setPic(images[i]);
            list.add(queryResponse);
        }
    }

    @Override
    protected void initToolbarHere() {
        initToolbar("管理");
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_manager;
    }

    @Override
    public void onAdapterClick(int position1, int position) {
        // 根据图片进行相应的跳转
        switch (mList.get(position).getQueryResponseList().get(position1).getPic()) {

            case R.drawable.connection: //设备连接
                if (ContextCompat.checkSelfPermission(_mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //进入到这里代表没有获取定位权限
                    if (ActivityCompat.shouldShowRequestPermissionRationale(_mActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        //已经禁止提示了
                        Toast.makeText(_mActivity, "您已禁止该权限，需要重新开启", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        ActivityCompat.requestPermissions(_mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 11);

                    }
                }

                startActivity(new Intent(_mActivity, ConnectAct.class));
                break;
            case R.drawable.debug: //安装调试
                if (ContextCompat.checkSelfPermission(_mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //进入到这里代表没有获取定位权限
                    if (ActivityCompat.shouldShowRequestPermissionRationale(_mActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        //已经禁止提示了
                        Toast.makeText(_mActivity, "您已禁止该权限，需要重新开启", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        ActivityCompat.requestPermissions(_mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 11);

                    }
                }

                startActivity(new Intent(_mActivity, DeviceDebugAct.class));
                break;

            case R.drawable.bind://标签绑定
                startActivity(new Intent(_mActivity, BindCarAct.class));
//                startActivity(new Intent(_mActivity, ManagerBindActivity.class));
//                start(ManagerBindFragment.getInstance());
                break;

            case R.drawable.search://搜索
                if (ContextCompat.checkSelfPermission(_mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //进入到这里代表没有获取权限
                    if (ActivityCompat.shouldShowRequestPermissionRationale(_mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        //已经禁止提示了
                        Toast.makeText(_mActivity, "您已禁止该权限，需要重新开启", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        ActivityCompat.requestPermissions(_mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 22);

                    }
                }

                startActivity(new Intent(_mActivity, SearchTagAct.class));


                break;

            case R.drawable.caidian: //手动采点
                if (ContextCompat.checkSelfPermission(_mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //进入到这里代表没有获取定位权限
                    if (ActivityCompat.shouldShowRequestPermissionRationale(_mActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        //已经禁止提示了
                        Toast.makeText(_mActivity, "您已禁止该权限，需要重新开启", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        ActivityCompat.requestPermissions(_mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 11);

                    }
                }
                startActivity(new Intent(_mActivity, ShowMapAct.class).putExtra("flag", Constant.SDCD));
                break;

            case R.drawable.ic_dwgl:  //点位管理
                if (ContextCompat.checkSelfPermission(_mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //进入到这里代表没有获取定位权限
                    if (ActivityCompat.shouldShowRequestPermissionRationale(_mActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        //已经禁止提示了
                        Toast.makeText(_mActivity, "您已禁止该权限，需要重新开启", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        ActivityCompat.requestPermissions(_mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 11);

                    }
                }
                startActivity(new Intent(_mActivity, PointManageListAct.class));
                break;


            case R.drawable.carrier://载波测试
                if (isConnected()) {//蓝牙已连接,给设备发送指令
                    if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC) { //蓝牙2.0
                        reader.sendCarrierTest();
                    } else if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_LE) { //蓝牙4.0
                        reader.bleSendCmd(Constant.BLE_SEND_CARRIER_TEST, BlueToothHelper.CARRIER_TESTING, true);
                    }
                } else {//蓝牙设备未连接
                    Toast.makeText(_mActivity, "未连接设备,无法发送指令", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }

    //判断蓝牙设备是否已经连接
    private boolean isConnected() {

        SharedPreferences sp = _mActivity.getSharedPreferences(Constant.SP_CONNECT_STATUS, Context.MODE_PRIVATE);
        boolean status = sp.getBoolean(Constant.CONNECT_STATUS, false);

        if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC && status && (reader.getSocket() == null || !reader.getSocket().isConnected())) { //如果当前标记已连接，但是socket连接实际已经断了
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(Constant.CONNECT_STATUS, false);
            editor.commit();
            status = false;
        }


        return status;
    }
}
