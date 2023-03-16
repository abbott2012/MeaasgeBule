package com.guoji.mobile.cocobee.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.bql.baseadapter.recycleView.BH;
import com.bql.baseadapter.recycleView.QuickRcvAdapter;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.adapter.SelectTypeAdapter;
import com.guoji.mobile.cocobee.callback.DialogCallback;
import com.guoji.mobile.cocobee.common.Constant;
import com.guoji.mobile.cocobee.common.JsonResult;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.callback.StringDialogCallback;
import com.guoji.mobile.cocobee.model.TypeResponse;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.view.ListLineDecoration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;


import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 百度地图:手动采点/设备安装调试选择定位
 *
 * @author _H_JY
 *         2016-5-26下午2:53:04
 */
public class ShowMapAct extends BaseAct implements View.OnClickListener {

    private Context context;
    private ImageButton back_ib;
    private TextView title_tv;
    private EditText cCategoryET;
    private MapView mapView;
    private BaiduMap baiduMap;
    private double longitude;
    private double latitude;
    private float radius, direction;
    private boolean isFirstLoc = true; //是否初次定位
    private Button sure_btn;
    private String address, orgid;
    private LocationClient locationClient;
    private int intentFlag;
    private int category = 1;
    private TypeResponse mTypeResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        context = this;

        initView();

        initData();

    }


    private void initView() {

           /*绑定控件*/
        back_ib = (ImageButton) findViewById(R.id.back_ib);
        mapView = (MapView) findViewById(R.id.mapView);
        sure_btn = (Button) findViewById(R.id.sure_btn);
        title_tv = (TextView) findViewById(R.id.title_tv);


        View child = mapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }
    }

    private void initData() {
        Intent intent = getIntent();
        intentFlag = intent.getIntExtra("flag", -1);
        if (intentFlag == Constant.AZTS) {

//            longitude = intent.getDoubleExtra(Constant.LONGITUDE, 0.0); //获取不到时默认是0.0
//            latitude = intent.getDoubleExtra(Constant.LATITUDE, 0.0);
//            radius = intent.getFloatExtra(Constant.RADIUS, 0.0f);
//            direction = intent.getFloatExtra(Constant.DIRECTION, 0.0f);
//            address = intent.getStringExtra("addr");
//            initBDView();
            getLaLon();


        } else if (intentFlag == Constant.SDCD || intentFlag == Constant.BMFW) {

            if (intentFlag == Constant.SDCD) {
                title_tv.setText("手动采点");
            } else {
                title_tv.setText("便民点上传");
            }
            getLaLon();


        }


    }

    private void getLaLon() {
    /*使用百度SDK获取经纬度*/
        locationClient = new LocationClient(this);
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        option.setOpenGps(true);        //是否打开GPS
        option.setCoorType("bd09ll");       //设置返回值的坐标类型。
        option.setPriority(LocationClientOption.NetWorkFirst);  //设置定位优先级
        option.setProdName("Cocobee"); //设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
        option.setScanSpan(600000);    //设置定时定位的时间间隔为10分钟。单位毫秒
        locationClient.setLocOption(option);

        // 注册位置监听器
        locationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                if (location == null) {
                    return;
                }
                longitude = location.getLongitude(); //获取经度
                latitude = location.getLatitude();
                radius = location.getRadius(); //获取精度圈半径
                direction = location.getDirection(); //获取方向信息

                address = location.getAddrStr();

                initBDView();


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


    private void initBDView() {
        if (mapView == null) {
            return;
        }
        sure_btn.setOnClickListener(this);

        baiduMap = mapView.getMap(); //得到百度地图
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL); //设置地图为正常模式


        baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                latitude = latLng.latitude;
                longitude = latLng.longitude;


                //先清除图层
                baiduMap.clear();
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(radius)  //如果不需要精度圈，将radius设为0即可
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(direction).latitude(latitude)
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
                    }
                });

            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });


        baiduMap.setMyLocationEnabled(true);//开启图层定位
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(radius)  //如果不需要精度圈，将radius设为0即可
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(direction).latitude(latitude)
                .longitude(longitude).build();
        // 设置定位数据
        baiduMap.setMyLocationData(locData);

        if (isFirstLoc) { //初次定位执行以下操作
            isFirstLoc = false;
            LatLng ll = new LatLng(latitude, longitude);
            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(ll, 20f);
            baiduMap.animateMapStatus(mapStatusUpdate);
        }

        // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
        BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                .fromResource(R.drawable.location_marker);
        MyLocationConfiguration.LocationMode currentMode = MyLocationConfiguration.LocationMode.NORMAL;
        MyLocationConfiguration config = new MyLocationConfiguration(currentMode, true, mCurrentMarker);
        baiduMap.setMyLocationConfigeration(config);

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
                if (intentFlag == Constant.AZTS) {
//                    Intent i = new Intent();
//                    i.putExtra("lat", latitude);
//                    i.putExtra("lng", longitude);
//                    i.putExtra("address", address);
//                    setResult(2001, i);
                    finish();
                } else if (intentFlag == Constant.SDCD) {
                    uploadPosition();
                } else if (intentFlag == Constant.BMFW) {
                    uploadConveniencePoint();
                }

            default:
                break;
        }
    }


    private void uploadConveniencePoint() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("数据完善与上传");

        View view = LayoutInflater.from(context).inflate(R.layout.cd_post_dialog_view, null);
        TextView cNameTv = (TextView) view.findViewById(R.id.recogcode_tv);
        cNameTv.setText("名称：");
        final EditText cNameET = (EditText) view.findViewById(R.id.recogcode_et);
        cNameET.setHint("请输入便民点名称");
        final EditText coordinates_et = (EditText) view.findViewById(R.id.coordinates_et);
        final EditText address_et = (EditText) view.findViewById(R.id.address_et);
        final TextView cCategoryTv = (TextView) view.findViewById(R.id.police_station_tv);
        cCategoryTv.setText("类型：");
        cCategoryTv.setVisibility(View.VISIBLE);
        cCategoryET = (EditText) view.findViewById(R.id.police_station_et);
        cCategoryET.setHint("请选择类型");
        cCategoryET.setVisibility(View.VISIBLE);
        coordinates_et.setText(" ( " + longitude + " , "
                + latitude + " )");
        address_et.setText(address);
        builder.setView(view);


        cCategoryET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getType();

                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setItems(new String[]{"加油站", "充电站", "维修站"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                cCategoryET.setText("加油站");
                                category = 1;
                                break;
                            case 1:
                                cCategoryET.setText("充电站");
                                category = 2;
                                break;
                            case 2:
                                cCategoryET.setText("维修站");
                                category = 3;
                                break;
                        }
                        dialog.dismiss();
                    }

                });

                builder1.create().show();
            }
        });

        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("上传数据", null);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();


        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String cNameStr = cNameET.getText().toString().trim();
                final String coordinateStr = coordinates_et.getText().toString().trim();
                final String addressStr = address_et.getText().toString().trim();
                final String cCategoryStr = cCategoryET.getText().toString().trim();


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


                //校验通过，开始上传数据

                Map<String, String> map = new HashMap<String, String>();
                map.put("name", cNameStr);
                map.put("category", mTypeResponse.getDid() /*String.valueOf(category)*/);
                map.put("lat", String.valueOf(latitude));
                map.put("lng", String.valueOf(longitude));
                map.put("address", addressStr);
                map.put("orgids", user.getOrgids());
                map.put("pid", user.getPid());

                OkGo.post(Path.UPLOAD_CONVENIENCE_POINT).tag(this).params(map).execute(new StringDialogCallback(ShowMapAct.this, "正在上传...") {
                    @Override
                    public void onSuccess(String result, Call call, Response response) {
                        if (!TextUtils.isEmpty(result)) {
                            JsonResult jr = new Gson().fromJson(result, new TypeToken<JsonResult>() {
                            }.getType());
                            if (jr != null && jr.isFlag() && jr.getStatusCode() == 200) {
                                if (intentFlag == Constant.BMFW) { //便民服务列表需要更新数据
                                    EventBus.getDefault().post(Constant.NEED_REFRESH_CONVENIENCE_LIST);
                                }

                                Toast.makeText(context, "上传数据成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "上传数据失败", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "上传数据失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toast.makeText(context, "上传数据失败", Toast.LENGTH_SHORT).show();
                    }

                });


            }
        });


    }

    //获取类型
    private void getType() {
        OkGo.post(Path.GET_TYPE).tag(this).execute(new DialogCallback<List<TypeResponse>>(context, "获取类型中...") {
            @Override
            public void onSuccess(List<TypeResponse> typeResponses, Call call, Response response) {
                initTypeDialog(typeResponses);
            }
        });
    }

    private void initTypeDialog(List<TypeResponse> typeResponses) {
        View view1 = View.inflate(context, R.layout.label_list, null);
        final AlertDialog alertDialog = Utils.showCornerDialog(context, view1, 270, 270);
        RecyclerView listView = (RecyclerView) alertDialog.findViewById(R.id.listView);
        TextView tvTitleCarLabel = (TextView) alertDialog.findViewById(R.id.tv_title_car_label);
        tvTitleCarLabel.setText("请选择类型");
        SelectTypeAdapter adapter = new SelectTypeAdapter(context, typeResponses);
        listView.setAdapter(adapter);
        listView.addItemDecoration(new ListLineDecoration());
        listView.setLayoutManager(new LinearLayoutManager(context));
        adapter.setOnItemClickListener(new QuickRcvAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(BH viewHolder, int position) {
                alertDialog.dismiss();
                mTypeResponse = typeResponses.get(position);
                cCategoryET.setText(mTypeResponse.getDname());
            }
        });
    }


    private void uploadPosition() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("数据完善与上传");

        View view = LayoutInflater.from(context).inflate(R.layout.cd_post_dialog_view, null);
        final EditText recogCodeET = (EditText) view.findViewById(R.id.recogcode_et);
        final EditText coordinates_et = (EditText) view.findViewById(R.id.coordinates_et);
        final EditText address_et = (EditText) view.findViewById(R.id.address_et);
        TextView police_station_tv = (TextView) view.findViewById(R.id.police_station_tv);
        police_station_tv.setVisibility(View.VISIBLE);
        cCategoryET = (EditText) view.findViewById(R.id.police_station_et);
        cCategoryET.setVisibility(View.VISIBLE);
        coordinates_et.setText(" ( " + longitude + " , "
                + latitude + " )");
        address_et.setText(address);
        builder.setView(view);


        cCategoryET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(context, SelectAct.class).putExtra("selectFlag", Constant.SELECT_POLICE_ACT), 1023);
            }
        });


        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("上传数据", null);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();


        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String recogCodeStr = recogCodeET.getText().toString().trim();
                final String coordinateStr = coordinates_et.getText().toString().trim();
                final String addressStr = address_et.getText().toString().trim();
                String policeStr = cCategoryET.getText().toString().trim();
                /*if(TextUtils.isEmpty(recogCodeStr)){
                    Toast.makeText(context,"请输入110快速识别码",Toast.LENGTH_SHORT).show();
                    return;
                }*/

               /* if(recogCodeStr.length() < 16){
                    Toast.makeText(context,"长度16个字符",Toast.LENGTH_SHORT).show();
                    return;
                }*/

                if (TextUtils.isEmpty(policeStr)) {
                    Toast.makeText(context, "组织机构不能为空", Toast.LENGTH_SHORT).show();
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
                map.put("lat", String.valueOf(latitude));
                map.put("lng", String.valueOf(longitude));
                map.put("code", recogCodeStr);
                map.put("addr", addressStr);
                map.put("orgid", orgid);
                map.put("orgids", user.getOrgids());

                OkGo.post(Path.UPLOAD_CD_INFO_PATH).tag(this).params(map).execute(new StringDialogCallback(ShowMapAct.this, "正在上传...") {
                    @Override
                    public void onSuccess(String result, Call call, Response response) {
                        if (!TextUtils.isEmpty(result)) {
                            JsonResult jr = new Gson().fromJson(result, new TypeToken<JsonResult>() {
                            }.getType());
                            if (jr != null && jr.isFlag() && jr.getStatusCode() == 200) {
                                if (intentFlag == Constant.BMFW) { //便民服务列表需要更新数据
                                    EventBus.getDefault().post(Constant.NEED_REFRESH_CONVENIENCE_LIST);
                                }

                                Toast.makeText(context, "上传数据成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "上传数据失败", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "上传数据失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toast.makeText(context, "上传数据失败", Toast.LENGTH_SHORT).show();
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
                cCategoryET.setText(orgname);
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
