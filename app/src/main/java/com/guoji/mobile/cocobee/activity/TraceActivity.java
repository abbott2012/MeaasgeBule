package com.guoji.mobile.cocobee.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
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
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.callback.StringDialogCallback;
import com.guoji.mobile.cocobee.common.AppManager;
import com.guoji.mobile.cocobee.common.Constant;
import com.guoji.mobile.cocobee.common.JsonResult;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.model.Location;
import com.guoji.mobile.cocobee.model.User;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.utils.XToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.lzy.okgo.OkGo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 轨迹页面：普通用户的车辆轨迹、人员轨迹
 * Created by _H_JY on 16/10/28.
 */
public class TraceActivity extends AppCompatActivity implements View.OnClickListener, OnDateSetListener, OnGetRoutePlanResultListener {


    private ImageView back_ib;
    private TextView title_tv;
    private LinearLayout username_ll;
    private TextView username_tip_tv;
    private EditText cno_et;
    private TextView start_time_tv;
    private TextView end_time_tv;
    private Button search_trace_btn, replay_btn, speed_btn;

    //轨迹回放
    private SeekBar progressBar, speed_sb;
    private ImageView playTraceIv;
    List<LatLng> latLngPolygons = new ArrayList<>();
    List<Location> locations = new ArrayList<>();
    List<LatLng> planLatLngPolygons = new ArrayList<>();
    List<Location> locationWithoutSames = new ArrayList<>();


    private TimePickerDialog.Builder timePickerBuilder;


    //路线记录标识
    private int routeIndex;
    private boolean routeFlag;
    private final int ROUTE = 0;
    private final int FIRST_ROUTE = 1;
    private Marker routeMarker;
    private int ROUTETIME = 300;

    private double latstart;
    private double lngstart;
    private double latend;
    private double lngend;

    private boolean loadFlag = true;
    private boolean isLoading = false;

    private RoutePlanSearch routePlanSearch; //路线规划搜索接口
    // private int times = 0;

    private boolean isOneTimePlanFinish = true;

    private LinearLayout control_ll;

    private TimePickerDialog mDialogAll;
    private int timeDialogFlag = 0;
    private SweetAlertDialog sDialog;
    private MapView mapView;
    private BaiduMap baiduMap;
    private User user;
    private Context context;
    private int searchObjectFlag;
    long tenYears = 10L * 365 * 1000 * 60 * 60 * 24L;
    private final static int PROGRESS = 1234;
    private final static int PLAN_ROUTE_FINISH = 1235;
    private long startMill;
    private long endMill;
    private String mLabelId;
    private boolean isMoving = false;

    //定位
    private LocationClient locationClient;
    private double mLongitude;
    private double mLatitude;
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_trace);

        AppManager.getAppManager().addActivity(this);
        user = Utils.getUserLoginInfo();
        context = this;
        initIntent();

        timePickerBuilder = new TimePickerDialog.Builder()
                .setCallBack(this)
                .setCancelStringId("取消")
                .setSureStringId("确定")
                .setTitleStringId("请选择时间")
                .setYearText("年")
                .setMonthText("月")
                .setDayText("日")
                .setHourText("时")
                .setMinuteText("分")
                .setCyclic(false)
                .setMinMillseconds(System.currentTimeMillis() - 3 * tenYears)
                .setMaxMillseconds(System.currentTimeMillis())
                .setThemeColor(getResources().getColor(R.color.timepicker_dialog_bg))
                .setType(Type.ALL)
                .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
                .setWheelItemTextSelectorColor(getResources().getColor(R.color.timepicker_toolbar_bg))
                .setWheelItemTextSize(12);

        mDialogAll = timePickerBuilder.build();
        initView();
        //第一次进来默认30分钟
        initFirst();
        initMap();
        getCurrentLocation();
        initListener();

    }

    private void initFirst() {
        long millis = System.currentTimeMillis();
        endMill = millis;
        startMill = millis - 30 * 60 * 1000;
        start_time_tv.setText(getDateToString(startMill));
        end_time_tv.setText(getDateToString(endMill));
        loadTrace();
    }

    private void initIntent() {
        Intent intent = getIntent();
        searchObjectFlag = intent.getIntExtra("searchObject", 0);
        mLabelId = intent.getStringExtra("labelId");
    }

    private void getCurrentLocation() {
        /*使用百度SDK获取经纬度*/
        locationClient = new LocationClient(context);
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        option.setOpenGps(true);        //是否打开GPS
        option.setCoorType("bd09ll");       //设置返回值的坐标类型。
        option.setPriority(LocationClientOption.NetWorkFirst);  //设置定位优先级
        option.setProdName("Cocobee"); //设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
        option.setScanSpan(30000);  //设置定时定位的时间间隔为20秒。单位毫秒
        locationClient.setLocOption(option);

        // 注册位置监听器
        locationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                if (location == null) {
                    return;
                }

                mLongitude = location.getLongitude(); //获取经度
                mLatitude = location.getLatitude();
                if (isFirst) {
                    initMap();
                    isFirst = false;
                }
            }
        });
        locationClient.start();
                    /*
                     *当所设的整数值大于等于1000（ms）时，定位SDK内部使用定时定位模式。
                     *调用requestLocation( )后，每隔设定的时间，定位SDK就会进行一次定位。
                     *如果定位SDK根据定位依据发现位置没有发生变化，就不会发起网络请求，
                     *返回上一次定位的结果；如果发现位置改变，就进行网络请求进行定位，得到新的定位结果。
                     *定时定位时，调用一次requestLocation，会定时监听到定位结果。
                     */
        locationClient.requestLocation();
    }


    private void initListener() {
        baiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
                isMoving = true;
            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                isMoving = false;
            }
        });
    }

    private void initView() {
        back_ib = (ImageView) findViewById(R.id.back_ib);
        start_time_tv = (TextView) findViewById(R.id.starttime_tv);
        end_time_tv = (TextView) findViewById(R.id.endtime_tv);
        search_trace_btn = (Button) findViewById(R.id.search_trace_btn);
        mapView = (MapView) findViewById(R.id.mapView);
        username_ll = (LinearLayout) findViewById(R.id.username_ll);
        cno_et = (EditText) findViewById(R.id.username_et);
        title_tv = (TextView) findViewById(R.id.title_tv);
        username_tip_tv = (TextView) findViewById(R.id.username_tip_tv);
        control_ll = (LinearLayout) findViewById(R.id.control_ll);
        replay_btn = (Button) findViewById(R.id.replay_btn);
        speed_sb = (SeekBar) findViewById(R.id.speed_sb);

        playTraceIv = (ImageView) findViewById(R.id.location_ivPlay);
        progressBar = (SeekBar) findViewById(R.id.progressBar1);
        speed_btn = (Button) findViewById(R.id.speed_btn);

        progressBar.setEnabled(false);
        initBaiduMap();


        speed_sb.setOnSeekBarChangeListener(onSeekBarChangeListener);


        playTraceIv.setOnClickListener(this);
        replay_btn.setOnClickListener(this);
        speed_btn.setOnClickListener(this);

        //首页进去隐藏身份证,车牌号
        username_ll.setVisibility(View.GONE);

        if (searchObjectFlag == Constant.SEARCH_CAR_TRACE) { //查车辆轨迹
            title_tv.setText("车辆轨迹");
        } else {
            title_tv.setText("人员轨迹");
        }


        routePlanSearch = RoutePlanSearch.newInstance(); //初始化路线规划检索对象
        routePlanSearch.setOnGetRoutePlanResultListener(this); //设置监听，接收规划结果


        back_ib.setOnClickListener(this);
        search_trace_btn.setOnClickListener(this);
        start_time_tv.setOnClickListener(this);
        end_time_tv.setOnClickListener(this);

    }

    private void initBaiduMap() {
    /*隐藏百度地图logo*/
        View child = mapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }
        baiduMap = mapView.getMap(); //得到百度地图
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL); //设置地图为正常模式
    }

    private void initMap() {

        //设定中心点坐标
        LatLng cenpt = new LatLng(mLatitude, mLongitude);
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(cenpt)
                .zoom(17)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化

        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        baiduMap.setMapStatus(mMapStatusUpdate);
    }


    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            stopTimer();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            ROUTETIME = 500 - seekBar.getProgress();
        }
    };


    /**
     * 移动到指定位置 并缩放
     *
     * @param latlng
     */
    private void moveToLocation(LatLng latlng, boolean flag) {
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latlng);// 设置新的中心点

        baiduMap.animateMapStatus(u);
        if (flag && baiduMap.getMapStatus().zoom < 12.0f) {
            // 加个延时播放的效果,就可以有先平移 ，再缩放的效果
            mTimer.start();
        }


    }


    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }


    private CountDownTimer mTimer = new CountDownTimer(2000, 2000) {

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            if (baiduMap != null) {
                MapStatusUpdate u1 = MapStatusUpdateFactory.zoomTo(12.0f);
                baiduMap.animateMapStatus(u1);
            }
        }
    };


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_ib:
                if (!isMoving) {
                    finish();
                }
                break;

            case R.id.starttime_tv: //开始时间
                if (endMill != 0) {
                    timePickerBuilder.setMinMillseconds(endMill - 3 * 24 * 60 * 60 * 1000);
                    timePickerBuilder.setMaxMillseconds(endMill);
                } else {
                    timePickerBuilder.setCurrentMillseconds(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
                    timePickerBuilder.setMaxMillseconds(System.currentTimeMillis());
                }

                if (!mDialogAll.isAdded()) { //判断是否已经添加，避免因重复添加导致异常
                    mDialogAll.show(getSupportFragmentManager(), "all");
                }

                timeDialogFlag = 1;
                break;

            case R.id.endtime_tv:
                if (startMill != 0) {
                    timePickerBuilder.setMinMillseconds(startMill);
                    if (System.currentTimeMillis() < (startMill + 3 * 24 * 60 * 60 * 1000)) {
                        timePickerBuilder.setMaxMillseconds(System.currentTimeMillis());
                    } else {
                        timePickerBuilder.setMaxMillseconds(startMill + 3 * 24 * 60 * 60 * 1000);
                    }
                } else {
                    timePickerBuilder.setCurrentMillseconds(System.currentTimeMillis());
                    timePickerBuilder.setMaxMillseconds(System.currentTimeMillis());
                }


                if (!mDialogAll.isAdded()) {
                    mDialogAll.show(getSupportFragmentManager(), "all");
                }


                timeDialogFlag = 2;
                break;


            case R.id.search_trace_btn:
                loadTrace();
                break;

            case R.id.username_et: //普通用户车主，点击弹出车牌列表供选择
                startActivityForResult(new Intent(context, SelectAct.class).putExtra("selectFlag", Constant.SELECT_CNO_ACT), 1033);
                break;


            case R.id.location_ivPlay:
                if (latLngPolygons == null || latLngPolygons.size() <= 0) {
                    return;
                }


                routeFlag = !routeFlag;
                playTraceIv.setImageResource(routeFlag ? R.drawable.pause1
                        : R.drawable.play1);

                if (routeFlag) {
                    if (routeIndex == 0) {
                        baiduMap.clear();
                        routeMarker = null;

                        //先把设备点标出来
                        //开启线程加载设备点
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 当不需要定位图层时关闭定位图层
                                //List<OverlayOptions> options = new ArrayList<>();
                                int i = 0;
                                while (i < locationWithoutSames.size() && loadFlag) {

                                    Location point = locationWithoutSames.get(i);
                                    Double lat = null, lng = null;
                                    if (!TextUtils.isEmpty(point.getLat())) {
                                        lat = Double.valueOf(point.getLat());
                                    }

                                    if (!TextUtils.isEmpty(point.getLng())) {
                                        lng = Double.valueOf(point.getLng());
                                    }

                                    BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                                            .fromResource(R.drawable.online);

                                    // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
                                    LatLng geoPoint = new LatLng(lat, lng);

                                    OverlayOptions option = new MarkerOptions().position(geoPoint).icon(mCurrentMarker).zIndex(8).draggable(true);
                                    //options.add(option);
                                    i++;
                                    baiduMap.addOverlay(option);
                                }

                            }
                        }).start();
                    }
                    handler.sendEmptyMessageDelayed(ROUTE, 0);
                } else {
                    handler.removeMessages(0);
                }
                break;


            case R.id.replay_btn:
                if (latLngPolygons == null || latLngPolygons.size() <= 0) {
                    return;
                }
                handler.removeMessages(0);
                loadFlag = false;
                routeIndex = 0;
                routeFlag = true;
                baiduMap.clear();

                if (routeMarker != null) {
                    routeMarker.remove();
                    routeMarker = null;
                }
                playTraceIv.setImageResource(R.drawable.pause1);


                //先把设备点标出来
                //开启线程加载设备点
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        loadFlag = true;
                        // 当不需要定位图层时关闭定位图层
                        //List<OverlayOptions> options = new ArrayList<>();
                        int i = 0;
                        while (i < locationWithoutSames.size() && loadFlag) {

                            Location point = locationWithoutSames.get(i);
                            Double lat = null, lng = null;
                            if (!TextUtils.isEmpty(point.getLat())) {
                                lat = Double.valueOf(point.getLat());
                            }

                            if (!TextUtils.isEmpty(point.getLng())) {
                                lng = Double.valueOf(point.getLng());
                            }

                            BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                                    .fromResource(R.drawable.online);

                            // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
                            LatLng geoPoint = new LatLng(lat, lng);

                            OverlayOptions option = new MarkerOptions().position(geoPoint).icon(mCurrentMarker).zIndex(8).draggable(true);
                            //options.add(option);
                            i++;
                            baiduMap.addOverlay(option);
                        }

                    }
                }).start();

                handler.sendEmptyMessageDelayed(ROUTE, 0);
                break;


            case R.id.speed_btn:
                speed_sb.setVisibility(speed_sb.getVisibility() == View.GONE ? View.VISIBLE
                        : View.GONE);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1033 && resultCode == 1034) {
            String cno = data.getStringExtra("cno");
            cno_et.setText(cno);
        }
    }


    private Handler handler = new Handler() {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (sDialog != null && sDialog.isShowing()) {
                sDialog.dismiss();
            }

            switch (msg.what) {
                case ROUTE:
                    handler.sendEmptyMessage(PROGRESS);
                    if (routeIndex == planLatLngPolygons.size() - 1) {
                        routeFlag = false;
                        playTraceIv.setImageResource(R.drawable.play1);
                        Toast.makeText(TraceActivity.this, "播放完毕", Toast.LENGTH_LONG)
                                .show();
                        routeIndex = 0;
                        if (routeMarker != null) {
                            routeMarker.remove();
                            routeMarker = null;
                        }

                        //drawMyRoute(planLatLngPolygons);

                        addFinalPointIcon();

                        return;
                    }
                    if (routeIndex != 0) {
                        if (routeIndex == 1) { //起点
                            /**
                             * 创建自定义overlay
                             */
                            // 起点位置
                            LatLng geoPoint = new LatLng(latstart, lngstart);
                            // 构建Marker图标
                            BitmapDescriptor bitmap = BitmapDescriptorFactory
                                    .fromResource(R.drawable.ic_start);
                            // 构建MarkerOption，用于在地图上添加Marker
                            OverlayOptions option = new MarkerOptions().position(geoPoint)
                                    .icon(bitmap).zIndex(8).draggable(true);

                            List<OverlayOptions> overlay = new ArrayList<OverlayOptions>();
                            overlay.add(option);
                            baiduMap.addOverlays(overlay);
                        } else {
                            OverlayOptions polyLine = new PolylineOptions()
                                    .width(6)
                                    .color(0xFF1694FF)
                                    .points(planLatLngPolygons.subList(routeIndex - 1,
                                            routeIndex + 1));
                            ///

                            baiduMap.addOverlay(polyLine);


                        }

                    }
                    // 页面跟随移动,注掉这行就是在原图上绘制
                    if (routeIndex < planLatLngPolygons.size()) {
                        moveToLocation(planLatLngPolygons.get(routeIndex), false);


                        if (routeMarker == null) { //Marker在地图上移动
                            BitmapDescriptor currMarkerIcon = null;
                            if (searchObjectFlag == Constant.SEARCH_PEOPLE_TRACE) {//查人
                                currMarkerIcon = BitmapDescriptorFactory.fromResource(R.drawable.man);
                            } else { //查车
                                currMarkerIcon = BitmapDescriptorFactory.fromResource(R.drawable.bike);
                            }

                            OverlayOptions cur = new MarkerOptions()
                                    .position(planLatLngPolygons.get(routeIndex++)).icon(currMarkerIcon)
                                    .perspective(false).anchor(0.5f, 0.5f).zIndex(10);
                            routeMarker = (Marker) baiduMap.addOverlay(cur);
                        } else {
                            routeMarker.setPosition(planLatLngPolygons.get(routeIndex++)); //不断地去改变marker的位置
                        }
                    }
                    handler.sendEmptyMessageDelayed(ROUTE, ROUTETIME);
                    break;

                case PROGRESS:
                    if (routeIndex == 0) {// 因为播放完毕时routeIndex被赋值成了0，不写进度条会直接跳到0的位置
                        progressBar.setProgress(100);
                    } else {
                        progressBar.setProgress((routeIndex + 1) * 100 / planLatLngPolygons.size());
                    }
                    break;


                case PLAN_ROUTE_FINISH:
                    isLoading = false;
                    control_ll.setVisibility(View.VISIBLE);
                    routeFlag = false;
                    routeIndex = 0;
                    if (routeMarker != null) {
                        routeMarker.remove();
                        routeMarker = null;
                    }
                    Toast.makeText(context, "轨迹加载完毕", Toast.LENGTH_SHORT).show();
                    if (planLatLngPolygons.size() >= 2) {
                        drawMyRoute(planLatLngPolygons);
                    } else {
                        Toast.makeText(context, "规划后坐标数少于2，无法加载路径", Toast.LENGTH_SHORT).show();
                    }

                    break;


                case FIRST_ROUTE:

                    if (routeIndex == planLatLngPolygons.size() - 1) {
                        isOneTimePlanFinish = true;
                        return;
                    }

                    OverlayOptions polyLine = new PolylineOptions()
                            .width(6)
                            .color(0xFF1694FF)
                            .points(planLatLngPolygons.subList(routeIndex, routeIndex + 2));

                    ///
                    baiduMap.addOverlay(polyLine);

                    // 页面跟随移动,注掉这行就是在原图上绘制
                    moveToLocation(planLatLngPolygons.get(routeIndex), false);

                    if (routeMarker == null) { //Marker在地图上移动
                        BitmapDescriptor currMarkerIcon = null;
                        if (searchObjectFlag == Constant.SEARCH_PEOPLE_TRACE) {//查人
                            currMarkerIcon = BitmapDescriptorFactory.fromResource(R.drawable.man);
                        } else { //查车
                            currMarkerIcon = BitmapDescriptorFactory.fromResource(R.drawable.bike);
                        }

                        OverlayOptions cur = new MarkerOptions()
                                .position(planLatLngPolygons.get(routeIndex++)).icon(currMarkerIcon)
                                .perspective(false).anchor(0.5f, 0.5f).zIndex(10);
                        routeMarker = (Marker) baiduMap.addOverlay(cur);
                    } else {
                        routeMarker.setPosition(planLatLngPolygons.get(routeIndex++)); //不断地去改变marker的位置
                    }


                    handler.sendEmptyMessageDelayed(FIRST_ROUTE, 20);

                    break;


            }
        }
    };


    private void addFinalPointIcon() {
        if (planLatLngPolygons != null && planLatLngPolygons.size() > 0) {
            // 终点位置
            LatLng geoPoint1 = new LatLng(planLatLngPolygons.get(planLatLngPolygons.size() - 1).latitude, planLatLngPolygons.get(planLatLngPolygons.size() - 1).longitude);
            // 构建Marker图标
            BitmapDescriptor bitmap1 = BitmapDescriptorFactory
                    .fromResource(R.drawable.ic_end);
            // 构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option1 = new MarkerOptions().position(geoPoint1)
                    .icon(bitmap1).zIndex(8).draggable(true);
            baiduMap.addOverlay(option1);
        }
    }


    private void loadMapTrace(List<Location> locations) {


        if (locations != null && locations.size() < 2) {
            Toast.makeText(TraceActivity.this, "当前坐标点数少于2，无法加载轨迹", Toast.LENGTH_SHORT).show();
            return;
        }

        //过滤掉空的经纬度
        List<Location> userfulLocations = new ArrayList<>();

        if (locations != null && locations.size() > 0) {
            for (int i = 0; i < locations.size(); i++) {
                Location location = locations.get(i);
                if (!TextUtils.isEmpty(location.getLat()) && !TextUtils.isEmpty(location.getLng())) {
                    userfulLocations.add(location);
                }
            }
        }


        if (userfulLocations != null && userfulLocations.size() < 2) {
            Toast.makeText(TraceActivity.this, "当前坐标点数少于2，无法加载轨迹", Toast.LENGTH_SHORT).show();
            return;
        }


        try {
            latstart = Double.parseDouble(userfulLocations.get(0).getLat());
            lngstart = Double.parseDouble(userfulLocations.get(0).getLng());
            latend = Double.parseDouble(userfulLocations.get(userfulLocations.size() - 1).getLat());
            lngend = Double.parseDouble(userfulLocations.get(userfulLocations.size() - 1).getLng());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


        /**
         * 创建自定义overlay
         */
        // 起点位置
        LatLng geoPoint = new LatLng(latstart, lngstart);
        // 构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.ic_start);
        // 构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions().position(geoPoint)
                .icon(bitmap).zIndex(8).draggable(true);

        // 终点位置
        LatLng geoPoint1 = new LatLng(latend, lngend);
        // 构建Marker图标
        BitmapDescriptor bitmap1 = BitmapDescriptorFactory
                .fromResource(R.drawable.ic_end);
        // 构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option1 = new MarkerOptions().position(geoPoint1)
                .icon(bitmap1).zIndex(8).draggable(true);
        // 在地图上添加Marker，并显示

        List<OverlayOptions> overlay = new ArrayList<OverlayOptions>();
        overlay.add(option);
        overlay.add(option1);
        baiduMap.addOverlays(overlay);


        locationWithoutSames.clear();
        for (Location l : userfulLocations) {
            if (!locationWithoutSames.contains(l)) {
                locationWithoutSames.add(l);
            }
        }


        //开启线程加载设备点
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 当不需要定位图层时关闭定位图层
                //List<OverlayOptions> options = new ArrayList<>();
                int i = 0;
                while (i < locationWithoutSames.size() && loadFlag) {

                    Location point = locationWithoutSames.get(i);

                    if (!TextUtils.isEmpty(point.getLat()) && !TextUtils.isEmpty(point.getLng())) {
                        double lat = 0;
                        double lng = 0;
                        try {
                            lat = Double.parseDouble(point.getLat());
                            lng = Double.parseDouble(point.getLng());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }

                        BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                                .fromResource(R.drawable.online);

                        // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
                        LatLng geoPoint = new LatLng(lat, lng);

                        OverlayOptions option = new MarkerOptions().position(geoPoint).icon(mCurrentMarker).zIndex(8).draggable(true);
                        //options.add(option);
                        i++;
                        baiduMap.addOverlay(option);
                    }


                }

            }
        }).start();


        latLngPolygons.clear();
        double lat, lng;
        for (int k = 0; k < userfulLocations.size(); k++) {
            try {
                lat = Double.parseDouble(userfulLocations.get(k).getLat());
                lng = Double.parseDouble(userfulLocations.get(k).getLng());
                LatLng pt1 = new LatLng(lat, lng);
                latLngPolygons.add(pt1);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }


        // 百度最多支持10000个点连线
        if (latLngPolygons.size() > 10000) {
            latLngPolygons = latLngPolygons.subList(0, 10000);
        }

        loadFlag = true;

        if (latLngPolygons != null && latLngPolygons.size() > 1) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    isLoading = true;
                    for (int i = 0; i < latLngPolygons.size() && loadFlag; i++) {

                        if (i != latLngPolygons.size() - 1) { //到达最后一个点时无需再做路径规划
                            PlanNode stNode = PlanNode.withLocation(latLngPolygons.get(i));
                            PlanNode enNode = PlanNode.withLocation(latLngPolygons.get(i + 1));
                            isOneTimePlanFinish = false;
                            routePlanSearch.walkingSearch(new WalkingRoutePlanOption().from(stNode).to(enNode)); //开始步行规划检索


                            while (!isOneTimePlanFinish) { //一次规划还没处理完，就不开始下次规划
                                System.out.println("##############");
                            }


                        }
                    }
                    handler.sendEmptyMessage(PLAN_ROUTE_FINISH);
                }
            }).start();


        }

    }


    /**
     * 根据数据绘制轨迹
     *
     * @param points2
     */
    protected void drawMyRoute(List<LatLng> points2) {
        if (points2 != null && points2.size() >= 2) {
            OverlayOptions options = new PolylineOptions().color(0xFF1694FF)
                    .width(6).points(points2);
            baiduMap.addOverlay(options);

            moveToLocation(points2.get(points2.size() / 2), true);
        }
    }

    private void loadTrace() {

        if (user == null) {
            Toast.makeText(context, "请先登录，再查询轨迹", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(start_time_tv.getText().toString().trim())) {
            Toast.makeText(context, "请选择开始时间", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(end_time_tv.getText().toString().trim())) {
            Toast.makeText(context, "请选择结束时间", Toast.LENGTH_SHORT).show();
            return;
        }


        if (isLoading) {
            XToastUtils.showShortToast("当前正在加载轨迹，请稍后再查询");
            return;
        }

        if (routeMarker != null) {
            routeMarker.remove();
            routeMarker = null;
        }

        handler.removeMessages(1);
        loadFlag = false;


        //一些初始化、复位、清除工作
        handler.removeMessages(0);
        baiduMap.clear();
        routeFlag = false;
        routeIndex = 0;
        speed_sb.setProgress(0);
        ROUTETIME = 500;
        playTraceIv.setImageResource(R.drawable.play1);
        control_ll.setVisibility(View.GONE);
        speed_sb.setVisibility(View.GONE);
        planLatLngPolygons.clear();
        locationWithoutSames.clear();
        latLngPolygons.clear();


        search_trace_btn.setEnabled(false);

        Map<String, String> params = new HashMap<String, String>();
        params.put("labelid", mLabelId);
        String path = Path.CAR_PERSON_TRACE;


        params.put("starttime", start_time_tv.getText().toString().trim());
        params.put("endtime", end_time_tv.getText().toString().trim());


        OkGo.post(path).tag(this).params(params).execute(new StringDialogCallback(TraceActivity.this, "正在获取轨迹....") {
            @Override
            public void onSuccess(String result, Call call, Response response) {

                if (!TextUtils.isEmpty(result)) {
                    Gson gson = new Gson();
                    JsonResult jr = gson.fromJson(result, new TypeToken<JsonResult>() {
                    }.getType());
                    if (jr != null && jr.isFlag() && jr.getStatusCode() == 200) {
                        String jsonStr = jr.getResult();
                        if (!TextUtils.isEmpty(jsonStr)) {
                            List<Location> mLocations = gson.fromJson(jsonStr, new TypeToken<List<Location>>() {
                            }.getType());
                            if (mLocations != null && mLocations.size() > 0) {
                                locations = mLocations;

                                Toast.makeText(context, "查询轨迹数据成功", Toast.LENGTH_SHORT).show();

                                loadMapTrace(locations);

                                search_trace_btn.setEnabled(true);
                            } else {
                                baiduMap.clear();
                                locations.clear();
                                control_ll.setVisibility(View.GONE);
                                speed_sb.setVisibility(View.GONE);
                                search_trace_btn.setEnabled(true);
                                Toast.makeText(context, "查找不到轨迹信息", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            baiduMap.clear();
                            locations.clear();
                            control_ll.setVisibility(View.GONE);
                            speed_sb.setVisibility(View.GONE);
                            search_trace_btn.setEnabled(true);
                            Toast.makeText(context, "查找不到轨迹信息", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        baiduMap.clear();
                        locations.clear();
                        control_ll.setVisibility(View.GONE);
                        speed_sb.setVisibility(View.GONE);
                        search_trace_btn.setEnabled(true);
                        Toast.makeText(context, "获取轨迹信息失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    baiduMap.clear();
                    locations.clear();
                    control_ll.setVisibility(View.GONE);
                    speed_sb.setVisibility(View.GONE);
                    search_trace_btn.setEnabled(true);
                    Toast.makeText(context, "获取轨迹信息失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);

                baiduMap.clear();
                locations.clear();
                control_ll.setVisibility(View.GONE);
                speed_sb.setVisibility(View.GONE);
                search_trace_btn.setEnabled(true);
                Toast.makeText(context, "获取轨迹信息失败", Toast.LENGTH_SHORT).show();
            }

        });


    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        switch (timeDialogFlag) {
            case 1:
                start_time_tv.setText(getDateToString(millseconds));
                startMill = millseconds;
                break;
            case 2:
                end_time_tv.setText(getDateToString(millseconds));
                endMill = millseconds;
                break;
        }
    }


    public String getDateToString(long time) {

        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sf.format(d);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        loadFlag = false;  //停止正在执行的线程
        handler.removeMessages(0);
        handler.removeMessages(1);
        stopTimer();

        if (routePlanSearch != null) { //销毁路径规划检索
            routePlanSearch.destroy();
            routePlanSearch = null;
        }


        if (baiduMap != null) { //关闭定位图层
            baiduMap.setMyLocationEnabled(false);
        }

        if (mapView != null) {
            mapView.onDestroy();
            mapView = null;
        }

        OkGo.getInstance().cancelTag(this);
        locationClient.stop();
        AppManager.getAppManager().finishActivity(this);
    }


    //开始步行规划检索后，在此回调
    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
        if (walkingRouteResult == null || walkingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
            isOneTimePlanFinish = true;
            return;  //未找到结果
        }

        if (walkingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            isOneTimePlanFinish = true;
            return;
        }

        if (walkingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
            WalkingRouteLine routeLine = walkingRouteResult.getRouteLines().get(0); //如果有多种规划方案，直接获取第一种

            List<WalkingRouteLine.WalkingStep> walkingSteps = routeLine.getAllStep(); //获取所有路段信息
            for (int i = 0; i < walkingSteps.size(); i++) { //根据每个路段获取经纬度信息
                List<LatLng> latLngs = walkingSteps.get(i).getWayPoints();
                if (latLngs != null && latLngs.size() > 0) {
                    planLatLngPolygons.addAll(latLngs);
                }
            }


            handler.sendEmptyMessageDelayed(FIRST_ROUTE, 0);

        }

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {

    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }
}
