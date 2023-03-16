package com.guoji.mobile.cocobee.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
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
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.common.Constant;
import com.guoji.mobile.cocobee.common.JsonResult;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.callback.StringDialogCallback;
import com.guoji.mobile.cocobee.model.ConveniencePoint;
import com.guoji.mobile.cocobee.model.Point;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;


import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;


/**
 * 点位地图页面
 * @author _H_JY
 *         2016-5-26下午2:53:04
 */


public class PointMapManageAct extends BaseAct implements View.OnClickListener {

    private Context context;
    private ImageButton back_ib;
    private TextView title_tv;
    private EditText category_et;
    private MapView mapView;
    private BaiduMap baiduMap;
    private double longitude;
    private double latitude;
    private String cName, cCategory;
    private boolean isFirstLoc = true; //是否初次定位
    private Button sure_btn;
    private String address;
    private String recogCode;
    private List<Point> points = new ArrayList<>();
    private List<ConveniencePoint> conveniencePoints = new ArrayList<>();
    //private Thread mThread;
    private boolean loadFlag = true;

    private int flag = -1;
    private int category;

    private int curPos;
    private String oldCode, oldLat, oldLng, oldAddr, orgid, oldCName, oldCategory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        context = this;

        Intent intent = getIntent();
        flag = intent.getIntExtra("flag", -1);
        curPos = intent.getIntExtra("currentPosition", -1);

        if (flag == Constant.DWGL) { //点位管理
            points = app.getPoints();
            Point point = points.get(curPos);
            oldCode = point.getIdentitycode();
            oldLat = point.getLatitude();
            oldLng = point.getLongitude();
            oldAddr = point.getAddress();
        } else {//便民服务
//            conveniencePoints = app.getConveniencePoints();
            conveniencePoints = (List<ConveniencePoint>) intent.getSerializableExtra("list");
            ConveniencePoint point = conveniencePoints.get(curPos);
            oldCName = point.getName();
            oldLat = point.getLatitude();
            oldLng = point.getLongitude();
            oldAddr = point.getAddress();
            oldCategory = point.getCategory();

        }


        initView();

    }


    private void initView() {

           /*绑定控件*/
        back_ib = (ImageButton) findViewById(R.id.back_ib);
        mapView = (MapView) findViewById(R.id.mapView);
        sure_btn = (Button) findViewById(R.id.sure_btn);
        title_tv = (TextView) findViewById(R.id.title_tv);


        //隐藏百度地图logo
        View child = mapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)){
            child.setVisibility(View.INVISIBLE);
        }

        if (user.getApproleid() == Constant.NORMAL_USER && flag == Constant.BMFW) {//普通用户无法修改便民点
            sure_btn.setVisibility(View.GONE);
        }

        title_tv.setText("点位地图");

        sure_btn.setOnClickListener(this);


        initBDView();
    }


    private void initBDView() {

        baiduMap = mapView.getMap(); //得到百度地图
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL); //设置地图为正常模式

        if (!(user.getApproleid() == Constant.NORMAL_USER && flag == Constant.BMFW)) {
            baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    latitude = latLng.latitude;
                    longitude = latLng.longitude;
                    if (flag == Constant.DWGL) {
                        final Point point = points.get(curPos);

                        point.setLatitude(String.valueOf(latitude));
                        point.setLongitude(String.valueOf(longitude));
                    } else if (flag == Constant.BMFW) {
                        ConveniencePoint point = conveniencePoints.get(curPos);
                        point.setLatitude(String.valueOf(latitude));
                        point.setLongitude(String.valueOf(longitude));
                    }


                    //先清除图层
                    //baiduMap.clear();
                    MyLocationData locData = new MyLocationData.Builder()
                            .accuracy(0)  //如果不需要精度圈，将radius设为0即可
                                    // 此处设置开发者获取到的方向信息，顺时针0-360
                            .direction(0).latitude(latitude)
                            .longitude(longitude).build();
                    // 设置定位数据
                    baiduMap.setMyLocationData(locData);


                    //实例化一个地理编码查询对象
                    GeoCoder geoCoder = GeoCoder.newInstance();
                    //设置反地理编码位置坐标
                    ReverseGeoCodeOption op = new ReverseGeoCodeOption();
                    op.location(latLng);
                    //发起反地理编码请求(经纬度->地址信息)
                    geoCoder.reverseGeoCode(op);
                    geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
                        @Override
                        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

                        }

                        @Override
                        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                            address = reverseGeoCodeResult.getAddress();
                            if (flag == Constant.DWGL) {
                                points.get(curPos).setAddress(address);
                            } else if (flag == Constant.BMFW) {
                                conveniencePoints.get(curPos).setAddress(address);
                            }

                        }
                    });

                }

                @Override
                public boolean onMapPoiClick(MapPoi mapPoi) {
                    return false;
                }
            });
        }


        if (points != null && points.size() > 0 && flag == Constant.DWGL) {

            baiduMap.clear(); //加载之前先清空
            baiduMap.setMyLocationEnabled(true);
            //优先加载显示当前点
            Point curPoint = points.get(curPos);
            if (!TextUtils.isEmpty(curPoint.getLatitude()) && !TextUtils.isEmpty(curPoint.getLongitude())) {
                try {
                    latitude = Double.parseDouble(curPoint.getLatitude());
                    longitude = Double.parseDouble(curPoint.getLongitude());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            address = curPoint.getAddress();
            recogCode = curPoint.getIdentitycode();

            //找到当前的位置点，需要重新定位
            if (isFirstLoc) { //初次定位执行以下操作
                isFirstLoc = false;
                LatLng ll = new LatLng(latitude, longitude);
                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(ll, 16f); //原来是20
                baiduMap.animateMapStatus(mapStatusUpdate);
            }

            BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.location_marker);
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(0)  //如果不需要精度圈，将radius设为0即可
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(0).latitude(latitude)
                    .longitude(longitude).build();
            // 设置定位数据
            baiduMap.setMyLocationData(locData);
            MyLocationConfiguration.LocationMode currentMode = MyLocationConfiguration.LocationMode.NORMAL;
            MyLocationConfiguration config = new MyLocationConfiguration(currentMode, true, mCurrentMarker);
            baiduMap.setMyLocationConfigeration(config);


            //开启线程加载剩余的点
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 当不需要定位图层时关闭定位图层
                    //List<OverlayOptions> options = new ArrayList<>();
                    int i = 0;
                    while (i < points.size() && loadFlag) {

                        Point point = points.get(i);
                        if (!TextUtils.isEmpty(point.getLatitude()) && !TextUtils.isEmpty(point.getLongitude())) {
                            double lat = 0;
                            double lng = 0;
                            try {
                                lat = Double.parseDouble(point.getLatitude());
                                lng = Double.parseDouble(point.getLongitude());
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }

                            String code = point.getIdentitycode();
                            String addr = point.getAddress();


                            BitmapDescriptor mCurrentMarker = null;
                            String pointStateStr = point.getPoinstall();
                            if (pointStateStr != null && pointStateStr.equals("0")) { //未安装，用旗子表示
                                mCurrentMarker = BitmapDescriptorFactory
                                        .fromResource(R.drawable.uninstall);
                            } else if (pointStateStr != null && pointStateStr.equals("1")) {//已安装

                                String connStateStr = point.getIsconn();

                                if (connStateStr != null && connStateStr.equals("1")) { //在线,用红点表示
                                    mCurrentMarker = BitmapDescriptorFactory
                                            .fromResource(R.drawable.online);
                                } else { //未在线，用灰点表示
                                    mCurrentMarker = BitmapDescriptorFactory
                                            .fromResource(R.drawable.offline);
                                }

                            }


                            if (i == curPos) {
                                i++;
                                continue;
                            }


                            // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
                            LatLng geoPoint = new LatLng(lat, lng);
                            Bundle bundle = new Bundle();
                            bundle.putString("pointCode", code);
                            bundle.putString("pointAddr", addr);

                            OverlayOptions option = new MarkerOptions().extraInfo(bundle).position(geoPoint).icon(mCurrentMarker).zIndex(8).draggable(true);
                            //options.add(option);
                            i++;
                            baiduMap.addOverlay(option);
                        }


                    }
                    //baiduMap.addOverlays(options);

                }
            }).start();


            baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    LatLng latLng = marker.getPosition();
                    Bundle bundle = marker.getExtraInfo();

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("点位信息");

                    View view = LayoutInflater.from(context).inflate(R.layout.cd_post_dialog_view, null);
                    final EditText recogCodeET = (EditText) view.findViewById(R.id.recogcode_et);
                    recogCodeET.setFocusable(false);
                    recogCodeET.setText(bundle.getString("pointCode"));
                    //recogCodeET.setFocusableInTouchMode(false);
                    final EditText coordinates_et = (EditText) view.findViewById(R.id.coordinates_et);
                    coordinates_et.setFocusable(false);
                    coordinates_et.setText(" ( " + latLng.longitude + " , "
                            + latLng.latitude + " )");
                    final EditText address_et = (EditText) view.findViewById(R.id.address_et);
                    address_et.setFocusable(false);
                    address_et.setText(bundle.getString("pointAddr"));

                    builder.setView(view);

                    builder.setPositiveButton("关闭", null);
                    builder.create().show();

                    return false;
                }
            });


        } else if (conveniencePoints != null && conveniencePoints.size() > 0 && flag == Constant.BMFW) {
            baiduMap.clear(); //加载之前先清空

            baiduMap.setMyLocationEnabled(true);

            //优先加载显示当前点
            ConveniencePoint curPoint = conveniencePoints.get(curPos);
            if (!TextUtils.isEmpty(curPoint.getLatitude()) && !TextUtils.isEmpty(curPoint.getLongitude())) {
                try {
                    latitude = Double.parseDouble(curPoint.getLatitude());
                    longitude = Double.parseDouble(curPoint.getLongitude());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            address = curPoint.getAddress();
            cName = curPoint.getName();
            cCategory = curPoint.getCategory();


            //找到当前的位置点，需要重新定位
            if (isFirstLoc) { //初次定位执行以下操作
                isFirstLoc = false;
                LatLng ll = new LatLng(latitude, longitude);
                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(ll, 16f); //原来是20
                baiduMap.animateMapStatus(mapStatusUpdate);
            }


            BitmapDescriptor mCurrentMarker;

            if ("1".equals(cCategory)) {
                mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.gas);
            } else if ("2".equals(cCategory)) {
                mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.charge);
            } else {
                mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.repair);
            }


            if (Constant.NORMAL_USER != user.getApproleid()) {
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(0)  //如果不需要精度圈，将radius设为0即可
                                // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(0).latitude(latitude)
                        .longitude(longitude).build();
                // 设置定位数据
                baiduMap.setMyLocationData(locData);
                MyLocationConfiguration.LocationMode currentMode = MyLocationConfiguration.LocationMode.NORMAL;
                MyLocationConfiguration config = new MyLocationConfiguration(currentMode, true, mCurrentMarker);
                baiduMap.setMyLocationConfigeration(config);

            } else {
                // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
                LatLng geoPoint = new LatLng(latitude, longitude);
                Bundle bundle = new Bundle();
                bundle.putString("cName", cName);
                bundle.putString("cCategory", cCategory);
                bundle.putString("pointAddr", address);
                OverlayOptions option = new MarkerOptions().extraInfo(bundle).position(geoPoint).icon(mCurrentMarker).zIndex(8).draggable(true);
                baiduMap.addOverlay(option);
            }


            //开启线程加载剩余的点
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 当不需要定位图层时关闭定位图层
                    //List<OverlayOptions> options = new ArrayList<>();
                    int i = 0;
                    while (i < conveniencePoints.size() && loadFlag) {

                        ConveniencePoint point = conveniencePoints.get(i);

                        if (!TextUtils.isEmpty(point.getLatitude()) && !TextUtils.isEmpty(point.getLongitude())) {
                            double lat = 0;
                            double lng = 0;
                            try {
                                lat = Double.parseDouble(point.getLatitude());
                                lng = Double.parseDouble(point.getLongitude());
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }


                            String cName = point.getName();
                            String addr = point.getAddress();
                            String cCategory = point.getCategory();


                            BitmapDescriptor mCurrentMarker = null;
                            if (("1").equals(cCategory)) { //加油站
                                mCurrentMarker = BitmapDescriptorFactory
                                        .fromResource(R.drawable.gas);
                            } else if (("2").equals(cCategory)) {//充电站

                                mCurrentMarker = BitmapDescriptorFactory
                                        .fromResource(R.drawable.charge);
                            } else { //维修站
                                mCurrentMarker = BitmapDescriptorFactory
                                        .fromResource(R.drawable.repair);
                            }


                            if (i == curPos) {
                                i++;
                                continue;
                            }


                            // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
                            LatLng geoPoint = new LatLng(lat, lng);
                            Bundle bundle = new Bundle();
                            bundle.putString("cName", cName);
                            bundle.putString("cCategory", cCategory);
                            bundle.putString("pointAddr", addr);

                            OverlayOptions option = new MarkerOptions().extraInfo(bundle).position(geoPoint).icon(mCurrentMarker).zIndex(8).draggable(true);
                            //options.add(option);
                            i++;
                            baiduMap.addOverlay(option);
                        }


                    }
                    //baiduMap.addOverlays(options);

                }
            }).start();


            baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    LatLng latLng = marker.getPosition();
                    Bundle bundle = marker.getExtraInfo();

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("便民点信息");

                    View view = LayoutInflater.from(context).inflate(R.layout.cd_post_dialog_view, null);
                    TextView cNameTv = (TextView) view.findViewById(R.id.recogcode_tv);
                    cNameTv.setText("名称：");
                    final EditText cNameET = (EditText) view.findViewById(R.id.recogcode_et);
                    cNameET.setFocusable(false);
                    cNameET.setText(bundle.getString("cName"));


                    TextView cCategoryTv = (TextView) view.findViewById(R.id.police_station_tv);
                    cCategoryTv.setVisibility(View.VISIBLE);
                    cCategoryTv.setText("类型：");

                    EditText cCategoryET = (EditText) view.findViewById(R.id.police_station_et);
                    cCategoryET.setFocusable(false);
                    cCategoryET.setVisibility(View.VISIBLE);

                    String cCategoryStr = bundle.getString("cCategory");
                    switch (cCategoryStr) {
                        case "1":
                            cCategoryET.setText("加油站");
                            break;
                        case "2":
                            cCategoryET.setText("充电站");
                            break;
                        case "3":
                            cCategoryET.setText("维修站");
                            break;
                    }

                    //recogCodeET.setFocusableInTouchMode(false);
                    final EditText coordinates_et = (EditText) view.findViewById(R.id.coordinates_et);
                    coordinates_et.setFocusable(false);
                    coordinates_et.setText(" ( " + latLng.longitude + " , "
                            + latLng.latitude + " )");

                    final EditText address_et = (EditText) view.findViewById(R.id.address_et);
                    address_et.setFocusable(false);
                    address_et.setText(bundle.getString("pointAddr"));

                    builder.setView(view);

                    builder.setPositiveButton("关闭", null);
                    builder.create().show();

                    return false;
                }
            });


        }


        back_ib.setOnClickListener(this);


        // 在地图上添加Marker，并显示
        /*LatLng point = new LatLng(latitude, longitude);
        OverlayOptions option = new MarkerOptions().position(point)
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.app_logo)).zIndex(8).draggable(true);
        baiduMap.addOverlay(option);
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(context,"Hello",Toast.LENGTH_SHORT).show();
                return true;
            }
        });*/


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_ib:
                finish();
                break;

            case R.id.sure_btn:
                if (flag == Constant.DWGL) {
                    uploadPosition();
                } else if (flag == Constant.BMFW) {
                    uploadConveniencePoint();
                }

            default:
                break;
        }
    }


    private void uploadConveniencePoint() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("修改当前点位数据");

        View view = LayoutInflater.from(context).inflate(R.layout.cd_post_dialog_view, null);
        final EditText cNameET = (EditText) view.findViewById(R.id.recogcode_et);
        TextView cNameTv = (TextView) view.findViewById(R.id.recogcode_tv);
        cNameTv.setText("名称：");
        cNameET.setHint("请输入便民点名称");

        final EditText coordinates_et = (EditText) view.findViewById(R.id.coordinates_et);
        final EditText address_et = (EditText) view.findViewById(R.id.address_et);

        TextView category_tv = (TextView) view.findViewById(R.id.police_station_tv);
        category_tv.setText("类型：");
        category_tv.setVisibility(View.VISIBLE);
        category_et = (EditText) view.findViewById(R.id.police_station_et);
        category_et.setHint("请选择类型");
        category_et.setVisibility(View.VISIBLE);
        cNameET.setText(cName);

        switch (cCategory) {
            case "1":
                category_et.setText("加油站");
                break;
            case "2":
                category_et.setText("充电站");
                break;
            case "3":
                category_et.setText("维修站");
                break;
        }

        coordinates_et.setText(" ( " + longitude + " , "
                + latitude + " )");
        address_et.setText(address);
        builder.setView(view);


        category_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setItems(new String[]{"加油站", "充电站", "维修站"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                category_et.setText("加油站");
                                break;
                            case 1:
                                category_et.setText("充电站");
                                break;
                            case 2:
                                category_et.setText("维修站");
                                break;
                        }
                        dialog.dismiss();
                    }

                });

                builder1.create().show();
            }
        });


        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("修改数据", null);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();


        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String cNameStr = cNameET.getText().toString().trim();
                final String coordinateStr = coordinates_et.getText().toString().trim();
                final String addressStr = address_et.getText().toString().trim();
                final String cCategoryStr = category_et.getText().toString().trim();


                if (TextUtils.isEmpty(cNameStr)) {
                    Toast.makeText(context, "便民点名称不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(cCategoryStr)) {
                    Toast.makeText(context, "类别不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (TextUtils.isEmpty(coordinateStr)) {
                    Toast.makeText(context, "经纬度不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(addressStr)) {
                    Toast.makeText(context, "地址不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }


                alertDialog.dismiss();


                if ("加油站".equals(cCategoryStr)) {
                    category = 1;
                } else if ("充电站".equals(cCategoryStr)) {
                    category = 2;
                } else if ("维修站".equals(cCategoryStr)) {
                    category = 3;
                }


                Map<String, String> map = new HashMap<String, String>();
                map.put("id", String.valueOf(conveniencePoints.get(curPos).getSid()));
                map.put("lat", String.valueOf(latitude));
                map.put("lng", String.valueOf(longitude));
                map.put("name", cNameStr);
                map.put("address", addressStr);
                map.put("category", String.valueOf(category));
                map.put("orgids", user.getOrgids());
                //校验通过，开始上传数据
                OkGo.post(Path.UPDATE_CONVENIENCE_POINT).tag(this).params(map).execute(new StringDialogCallback(PointMapManageAct.this, "正在修改...") {
                    @Override
                    public void onSuccess(String result, Call call, Response response) {
                        if (!TextUtils.isEmpty(result)) {
                            JsonResult jr = new Gson().fromJson(result, new TypeToken<JsonResult>() {
                            }.getType());
                            if (jr != null && jr.isFlag() && jr.getStatusCode() == 200) {
                                if (flag == Constant.DWGL) {
                                    EventBus.getDefault().post(Constant.NEED_REFRESH_POINTLIST); //修改点位数据成功，需要更新页面展示
                                } else if (flag == Constant.BMFW) {
                                    EventBus.getDefault().post(Constant.NEED_REFRESH_CONVENIENCE_LIST);
                                }

                                Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "修改失败", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "修改失败", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toast.makeText(context, "修改失败", Toast.LENGTH_SHORT).show();
                    }

                });


            }
        });
    }


    private void uploadPosition() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("修改当前点位数据");

        View view = LayoutInflater.from(context).inflate(R.layout.cd_post_dialog_view, null);
        final EditText recogCodeET = (EditText) view.findViewById(R.id.recogcode_et);
        final EditText coordinates_et = (EditText) view.findViewById(R.id.coordinates_et);
        final EditText address_et = (EditText) view.findViewById(R.id.address_et);
        TextView police_station_tv = (TextView) view.findViewById(R.id.police_station_tv);
        police_station_tv.setVisibility(View.VISIBLE);
        category_et = (EditText) view.findViewById(R.id.police_station_et);
        category_et.setVisibility(View.VISIBLE);
        recogCodeET.setText(recogCode);
        coordinates_et.setText(" ( " + longitude + " , "
                + latitude + " )");
        address_et.setText(address);
        builder.setView(view);


        category_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(context, SelectAct.class).putExtra("selectFlag", Constant.SELECT_POLICE_ACT), 1023);
            }
        });


        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("修改数据", null);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();


        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String recogCodeStr = recogCodeET.getText().toString().trim();
                final String coordinateStr = coordinates_et.getText().toString().trim();
                final String addressStr = address_et.getText().toString().trim();

                if (points != null && oldCode != null && oldLng != null && oldLat != null && oldAddr != null) {
                    if (oldCode.equals(recogCodeStr) && oldLng.equals(String.valueOf(longitude)) && oldLat.equals(String.valueOf(latitude)) && oldAddr.equals(addressStr) && TextUtils.isEmpty(category_et.getText().toString().trim())) {
                        Toast.makeText(context, "当前点位数据未发生变化，无需修改", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }


                if (TextUtils.isEmpty(category_et.getText().toString().trim())) {
                    Toast.makeText(context, "组织结构不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (TextUtils.isEmpty(coordinateStr)) {
                    Toast.makeText(context, "经纬度不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(addressStr)) {
                    Toast.makeText(context, "地址不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }


                alertDialog.dismiss();


                //校验通过，开始上传数据

                Map<String, String> map = new HashMap<String, String>();
                map.put("id", points.get(curPos).getPoid());
                map.put("lat", String.valueOf(latitude));
                map.put("lng", String.valueOf(longitude));
                map.put("code", recogCodeStr);
                map.put("addr", addressStr);
                map.put("orgid", orgid);
                map.put("orgids", user.getOrgids());
                OkGo.post(Path.CHANGE_SINGLEPOINT_DATA).tag(this).params(map).execute(new StringDialogCallback(PointMapManageAct.this, "正在修改...") {
                    @Override
                    public void onSuccess(String result, Call call, Response response) {
                        if (!TextUtils.isEmpty(result)) {
                            JsonResult jr = new Gson().fromJson(result, new TypeToken<JsonResult>() {
                            }.getType());
                            if (jr != null && jr.isFlag() && jr.getStatusCode() == 200) {
                                if (flag == Constant.DWGL) {
                                    EventBus.getDefault().post(Constant.NEED_REFRESH_POINTLIST); //修改点位数据成功，需要更新页面展示
                                } else if (flag == Constant.BMFW) {
                                    EventBus.getDefault().post(Constant.NEED_REFRESH_CONVENIENCE_LIST);
                                }

                                Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "修改失败", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "修改失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toast.makeText(context, "修改失败", Toast.LENGTH_SHORT).show();
                    }

                });


            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1023 && resultCode == 1024) {
            orgid = data.getStringExtra("orgid");
            String orgname = data.getStringExtra("orgname");
            if (!TextUtils.isEmpty(orgname)) {
                category_et.setText(orgname);
            }
            return;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }


    @Override
    protected void onDestroy() {


        //当前Activity销毁时停止线程
        loadFlag = false;

        if (baiduMap != null) { //关闭定位图层
            baiduMap.setMyLocationEnabled(false);
        }

        if (mapView != null) {
            mapView.onDestroy();
            mapView = null;
        }

        OkGo.getInstance().cancelTag(this);

        super.onDestroy();
    }


    /************************************************测试百度地图轨迹显示***************************************************************/
    //从2.0开始，百度地图SDK不支持直接继承Overlay


    /*****************************************************************************************************************************/


}
