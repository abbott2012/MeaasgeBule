package com.guoji.mobile.cocobee.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.callback.StringComCallback;
import com.guoji.mobile.cocobee.common.Constant;
import com.guoji.mobile.cocobee.common.JsonResult;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.model.Equipment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 设备信息查看页面（安装调试上报设备信息到后台成功后，跳转到这个页面显示设备的位置信息）
 * Created by _H_JY on 2017/2/27.
 */
public class EquipPosLookAct extends BaseAct implements View.OnClickListener {

    private Context context;
    private ImageButton back_ib;
    private EditText device_id_et;
    private TextView title_tv;
    private Button right_btn;
    private MapView mapView;
    private BaiduMap baiduMap;
    private String collectorId; //设备ID
    private Timer timer; //计时器
    private int flag;
    /*软键盘管理器*/
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        context = this;

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        Intent i = getIntent();
        flag = i.getIntExtra("flag", -1);
        if (flag == Constant.AFTER_UPLOAD_DEVICE_INFO_SUCCESS) {
            collectorId = getIntent().getStringExtra("collectorId");
        }


        initView();

    }


    private void initView() {
        back_ib = (ImageButton) findViewById(R.id.back_ib);
        title_tv = (TextView) findViewById(R.id.title_tv);
        right_btn = (Button) findViewById(R.id.sure_btn);
        mapView = (MapView) findViewById(R.id.mapView);
        device_id_et = (EditText) findViewById(R.id.device_id_et);

        back_ib.setOnClickListener(this);

        //隐藏logo
        View child = mapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)){
            child.setVisibility(View.INVISIBLE);
        }



        if (flag == Constant.AFTER_UPLOAD_DEVICE_INFO_SUCCESS) {
            title_tv.setText("采集器位置");
            device_id_et.setVisibility(View.GONE);
            right_btn.setVisibility(View.GONE);
        }


        if (flag == Constant.AFTER_CHOOSE_LOOK_DEVICE_INFO) {
            right_btn.setVisibility(View.VISIBLE);
            title_tv.setVisibility(View.GONE);
            device_id_et.setVisibility(View.VISIBLE);
            right_btn.setBackgroundResource(R.drawable.search_white);
            right_btn.setOnClickListener(this);
        }


        baiduMap = mapView.getMap(); //得到百度地图
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL); //设置地图为正常模式


        if (flag == Constant.AFTER_CHOOSE_LOOK_DEVICE_INFO) {
            //设定中心点坐标
            LatLng cenpt = new LatLng(23.755673407168032, 114.7049539242698);
            //定义地图状态
            MapStatus mMapStatus = new MapStatus.Builder()
                    .target(cenpt)
                    .zoom(15)
                    .build();
            //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化

            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
            //改变地图状态
            baiduMap.setMapStatus(mMapStatusUpdate);
        }


        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //LatLng latLng = marker.getPosition();
                Bundle bundle = marker.getExtraInfo();
                String connStatus = bundle.getString("isConn");
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                if(TextUtils.equals(connStatus, "1")){ //在线
                    builder.setTitle(Html.fromHtml("采集器信息:(<font color='#136006'>在线</font>)"));
                }else {
                    builder.setTitle(Html.fromHtml("采集器信息:(<font color='red'>离线</font>)"));
                }



                View view = LayoutInflater.from(context).inflate(R.layout.dialog_collector_layout, null);
                TextView deviceIdTv = (TextView) view.findViewById(R.id.device_id_tv);
                deviceIdTv.setText("设备ID：" + bundle.getString("deviceId"));


                TextView deviceIpTv = (TextView) view.findViewById(R.id.device_ip_tv);
                deviceIpTv.setText("IP地址：" + bundle.getString("deviceIp"));

                TextView devicePortTv = (TextView) view.findViewById(R.id.device_port_tv);
                devicePortTv.setText("端口号：" + bundle.getString("devicePort"));

                TextView addressTv = (TextView) view.findViewById(R.id.address_tv);
                addressTv.setText("所在地：" + bundle.getString("deviceAddress"));

                TextView createTimeTv = (TextView) view.findViewById(R.id.create_time_tv);
                createTimeTv.setText("创建时间：" + bundle.getString("createtime"));

                TextView heartTimeTv = (TextView) view.findViewById(R.id.heart_time_tv);
                heartTimeTv.setText("心跳时间：" + bundle.getString("hearttime"));


                builder.setView(view);

                builder.setPositiveButton("关闭", null);
                builder.create().show();

                return false;
            }
        });
    }





    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            lookDeviceInfo();
        }
    }


    private void lookDeviceInfo() {
        Map<String, String> params = new HashMap<>();
        params.put("eqno", collectorId);
        params.put("orgids", user.getOrgids());
        OkGo.post(Path.EQU_WHETHER_EXIST_PATH).tag(this).params(params).execute(new StringComCallback(){
            @Override
            public void onSuccess(String result, Call call, Response response) {
                if (!TextUtils.isEmpty(result)) {
                    JsonResult jr = new Gson().fromJson(result, new TypeToken<JsonResult>() {
                    }.getType());
                    if (jr != null && jr.isFlag() && jr.getStatusCode() == 200) {
                        String jsonStr = jr.getResult();
                        if (!TextUtils.isEmpty(jsonStr)) {
                            Equipment equipment = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create().fromJson(jsonStr, new TypeToken<Equipment>() {
                            }.getType());
                            if (equipment != null) {
                                if(baiduMap != null){
                                    baiduMap.clear(); //加载之前先清空
                                }


                                double lat = Double.parseDouble(equipment.getEqlat());
                                double lng = Double.parseDouble(equipment.getEqlng());

                                //设定中心点坐标
                                LatLng cenpt = new LatLng(lat, lng);
                                //定义地图状态
                                MapStatus mMapStatus = new MapStatus.Builder()
                                        .target(cenpt)
                                        .zoom(15)
                                        .build();
                                //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化


                                MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                                //改变地图状态
                                if(baiduMap != null){
                                    baiduMap.setMapStatus(mMapStatusUpdate);
                                }


                                BitmapDescriptor currentMarker;
                                if (TextUtils.equals(equipment.getEqisconn(), "1")) { //在线
                                    currentMarker = BitmapDescriptorFactory.fromResource(R.drawable.p1);
                                } else {
                                    currentMarker = BitmapDescriptorFactory.fromResource(R.drawable.p2);
                                }

                                LatLng geoPoint = new LatLng(lat, lng);
                                Bundle bundle = new Bundle();
                                bundle.putString("deviceId", equipment.getEqno()); //设备id
                                bundle.putString("deviceIp", equipment.getEqip()); //ip地址
                                bundle.putString("devicePort", equipment.getEqport()); //端口
                                bundle.putString("deviceAddress", equipment.getEqaddr()); //地址
                                bundle.putString("createtime", equipment.getCreatetime()); //创建时间
                                bundle.putString("hearttime", equipment.getHearttime()); //心跳时间
                                bundle.putString("isConn",equipment.getEqisconn());

                                OverlayOptions option = new MarkerOptions().extraInfo(bundle).position(geoPoint).icon(currentMarker).zIndex(8).draggable(true);

                                if(baiduMap != null){
                                    baiduMap.addOverlay(option);
                                }
                            } else {
                                Toast.makeText(context, "采集器不存在！", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "采集器不存在！", Toast.LENGTH_SHORT).show();
                        }
                    } else if (jr != null && jr.getStatusCode() == 400) { //设备不存在
                        Toast.makeText(context, "采集器不存在！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "系统维护，请稍后操作！", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "系统维护，请稍后操作！", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                Toast.makeText(context, "系统维护，请稍后操作！", Toast.LENGTH_SHORT).show();
            }

        });

    }


    /*开启计时器*/
    public void startTimer() {
        timer = new Timer();
        timer.schedule(new MyTimerTask(), 0, 6000); //每6秒刷新一次UI
    }


    /*停止计时器*/
    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_ib:
                finish();
                break;


            case R.id.sure_btn:
                collectorId = device_id_et.getText().toString().trim();
                if (TextUtils.isEmpty(collectorId)) {
                    Toast.makeText(context, "请输入采集器号", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (imm != null && imm.isActive()) {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);//隐藏软键盘
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        lookDeviceInfo();
                    }
                }).start();


                break;


        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (mapView != null) {
            mapView.onResume();
        }

        if (flag != Constant.AFTER_CHOOSE_LOOK_DEVICE_INFO) {
            startTimer();
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        stopTimer();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    protected void onDestroy() {


        if (mapView != null) {
            mapView.onDestroy();
            mapView = null;
        }

        OkGo.getInstance().cancelTag(this);

        super.onDestroy();
    }
}
