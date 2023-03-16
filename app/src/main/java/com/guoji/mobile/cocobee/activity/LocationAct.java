package com.guoji.mobile.cocobee.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.common.Constant;
import com.guoji.mobile.cocobee.common.JsonResult;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.callback.StringDialogCallback;
import com.guoji.mobile.cocobee.model.Collector;
import com.guoji.mobile.cocobee.utils.Tools;
import com.guoji.mobile.cocobee.utils.XToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 位置页面：人员位置、车辆位置
 * Created by _H_JY on 2016/10/26.
 */
public class LocationAct extends BaseAct implements View.OnClickListener {

    private Context context;
    private ImageButton back_ib;
    private MapView mapView;
    private RadioGroup typeRadioGroup;
    private RadioButton pos_rb, gps_rb;
    private TextView title_tv;
    private BaiduMap baiduMap;
    private double longitude;
    private double latitude;

    private boolean isFirstLoc = true; //是否初次定位
    private Button setting_btn;
    private PopupWindow popupWindow;
    private RelativeLayout suoche_rl, jiesuo_rl, zhaohui_rl;
    private LinearLayout input_username_ll;
    private EditText cno_et;
    private Button search_btn;
    private View view;
    private int searchObjectFlag;

    private String person_look_type;

    private int operate_flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_carlocation);
        context = this;

        searchObjectFlag = getIntent().getIntExtra("searchObject", -1);


        initView();


        if (user == null) {
            Toast.makeText(context, "未登录，无法查看车辆位置", Toast.LENGTH_SHORT).show();
            return;
        }


        if (user.getApproleid() == Constant.NORMAL_USER) { //普通用户
            if (searchObjectFlag == Constant.SEARCH_CAR_POSITION) {//车辆位置
                input_username_ll.setVisibility(View.VISIBLE);
                cno_et.setFocusable(false);
                cno_et.setOnClickListener(this);
                typeRadioGroup.setVisibility(View.GONE);
            } else {//人员位置
                typeRadioGroup.setVisibility(View.VISIBLE);
                input_username_ll.setVisibility(View.GONE);
                loadLocation();
            }
//            if ("1".equals(user.getPtype())) { //车主
//                input_username_ll.setVisibility(View.VISIBLE);
//                cno_et.setFocusable(false);
//                cno_et.setOnClickListener(this);
//                typeRadioGroup.setVisibility(View.GONE);
//
//            } else {//人员标签
//                typeRadioGroup.setVisibility(View.VISIBLE);
//                input_username_ll.setVisibility(View.GONE);
//                loadLocation();
//            }

        } else { //管理员

            if (searchObjectFlag == Constant.SEARCH_CAR_POSITION) {
                typeRadioGroup.setVisibility(View.GONE);
            } else {
                typeRadioGroup.setVisibility(View.VISIBLE);
            }

            input_username_ll.setVisibility(View.VISIBLE);
        }


        typeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.pos_look_rb:
                        person_look_type = "1";
                        break;

                    case R.id.gps_look_rb:
                        person_look_type = "2";
                        break;
                }


                if (user.getApproleid() == Constant.NORMAL_USER && searchObjectFlag == Constant.SEARCH_PEOPLE_POSITION) {
                    loadLocation();
                }

            }
        });


    }

    private void initView() {
        /*绑定控件*/
        back_ib = (ImageButton) findViewById(R.id.back_ib);
        mapView = (MapView) findViewById(R.id.mapView);
        input_username_ll = (LinearLayout) findViewById(R.id.username_ll);
        cno_et = (EditText) findViewById(R.id.username_et);
        search_btn = (Button) findViewById(R.id.search_btn);
        title_tv = (TextView) findViewById(R.id.title_tv);
        setting_btn = (Button) findViewById(R.id.setting_btn);
        typeRadioGroup = (RadioGroup) findViewById(R.id.type_rg);
        pos_rb = (RadioButton) findViewById(R.id.pos_look_rb);
        gps_rb = (RadioButton) findViewById(R.id.gps_look_rb);


        View child = mapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }


        pos_rb.setChecked(true);
        person_look_type = "1";

//        if ((user.getApproleid() == Constant.NORMAL_USER && "1".equals(user.getPtype())) || (user.getApproleid() == Constant.ADMINISTRATOR && searchObjectFlag == Constant.SEARCH_CAR_POSITION)) { //普通用户车主或管理员
        if (searchObjectFlag == Constant.SEARCH_CAR_POSITION) { //普通用户车主或管理员
            title_tv.setText("电动车实时位置");
            setting_btn.setVisibility(View.VISIBLE);
        } else {
            title_tv.setText("人员实时位置");
            setting_btn.setVisibility(View.GONE);
        }


        if (user.getApproleid() == Constant.NORMAL_USER) { //普通用户
            if (searchObjectFlag == Constant.SEARCH_PEOPLE_POSITION) { //标签人员
                input_username_ll.setVisibility(View.GONE);
            } else {//车辆，弹出列表选择车牌号
                input_username_ll.setVisibility(View.VISIBLE);
                cno_et.setHint("点击选择车牌号");
                cno_et.setFocusable(false);
                cno_et.setOnClickListener(this);
            }

        } else { //管理员
            input_username_ll.setVisibility(View.VISIBLE);
            if (searchObjectFlag == Constant.SEARCH_CAR_POSITION) { //车辆位置查询根据车牌号来查
                cno_et.setHint("请输入车牌号");
            } else {
                cno_et.setHint("请输入用户名");
            }
        }


        search_btn.setOnClickListener(this);


        cno_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    input_username_ll.setBackgroundResource(R.drawable.ic_search_ll_white);
                } else {
                    input_username_ll.setBackgroundResource(R.drawable.ic_search_ll_blue);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        baiduMap = mapView.getMap(); //得到百度地图
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL); //设置地图为正常模式


        baiduMap.setMyLocationEnabled(true);//开启图层定位


        //设定中心点坐标
//        LatLng cenpt = new LatLng(23.755673407168032, 114.7049539242698);//河源
        //LatLng cenpt = new LatLng(28.883385,105.446511);
//        LatLng cenpt = new LatLng(29.806651,121.606983);//百度地图默认点
        LatLng cenpt = new LatLng(22.555331, 113.951446);//科技园
//        LatLng cenpt = new LatLng(28.883161,105.446784);//泸州
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(cenpt)
                .zoom(15)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化


        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        baiduMap.setMapStatus(mMapStatusUpdate);


        back_ib.setOnClickListener(this);


        setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(v);
            }
        });

    }


    private void loadLocation() {

        Map<String, String> params = new HashMap<String, String>();
        params.put("orgids", user.getOrgids());
        if (user.getApproleid() == Constant.NORMAL_USER) { //普通用户 1.车主   2.标签人员
            if (searchObjectFlag == Constant.SEARCH_PEOPLE_POSITION) {//非车主
                params.put("idcard", user.getUsername());
                params.put("ptype", person_look_type);
            } else { //车辆
                params.put("cno", cno_et.getText().toString().trim());
                params.put("usertype", "2");//1代表管理员,2代表车主
            }

        } else { //管理员
            if (searchObjectFlag == Constant.SEARCH_CAR_POSITION) { //查询车辆位置
                params.put("cno", cno_et.getText().toString().trim());
                params.put("usertype", "1");
            } else if (searchObjectFlag == Constant.SEARCH_PEOPLE_POSITION) { //查询人员位置
                params.put("idcard", cno_et.getText().toString().trim());
                params.put("ptype", person_look_type);
            }

        }


        String path = "";
        if (/*(user.getApproleid() == Constant.NORMAL_USER && !TextUtils.equals("1", user.getPtype())) || (user.getApproleid() == Constant.ADMINISTRATOR && */searchObjectFlag == Constant.SEARCH_PEOPLE_POSITION) {
            path = Path.PEOPLE_LOCATION_PATH;
        } else {
            path = Path.CAR_LOCATION_PATH;
        }

        OkGo.post(path).tag(this).params(params).execute(new StringDialogCallback(LocationAct.this, "位置加载中...") {
            @Override
            public void onSuccess(String result, Call call, Response response) {
                if (!TextUtils.isEmpty(result)) {

                    Gson gson = new Gson();

                    JsonResult jsonResult = gson.fromJson(result, new TypeToken<JsonResult>() {
                    }.getType());

                    if (jsonResult != null && jsonResult.isFlag() && jsonResult.getStatusCode() == 200) {
                        String jsonStr = jsonResult.getResult();
                        if (!TextUtils.isEmpty(jsonStr)) {
                            Collector collector = gson.fromJson(jsonStr, new TypeToken<Collector>() {
                            }.getType());

                            if (collector != null) {
                                String latitudeStr = collector.getLatitude();
                                String longitudeStr = collector.getLongitude();

                                if (TextUtils.isEmpty(latitudeStr) || TextUtils.isEmpty(longitudeStr)) {
                                    XToastUtils.showShortToast("暂无相关数据");
                                    return;
                                }


                                //gps坐标，需要纠偏成百度坐标
                                if ((user.getApproleid() == Constant.NORMAL_USER && searchObjectFlag == Constant.SEARCH_PEOPLE_POSITION && TextUtils.equals("2", person_look_type))
                                        || (user.getApproleid() == Constant.ADMINISTRATOR && searchObjectFlag == Constant.SEARCH_PEOPLE_POSITION && TextUtils.equals("2", person_look_type))) {
                                    double srcLat = 0;
                                    double srcLng = 0;
                                    try {
                                        srcLat = Double.parseDouble(latitudeStr);
                                        srcLng = Double.parseDouble(longitudeStr);
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                    }
                                    LatLng latLng = Tools.gpsToBaidu(new LatLng(srcLat, srcLng));

                                    latitude = latLng.latitude;
                                    longitude = latLng.longitude;

                                } else { //其他情况，无需纠偏
                                    try {
                                        latitude = Double.parseDouble(latitudeStr);
                                        longitude = Double.parseDouble(longitudeStr);
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                    }
                                }


                                MyLocationData locData = new MyLocationData.Builder()
                                        .accuracy(0)  //如果不需要精度圈，将radius设为0即可
                                        // 此处设置开发者获取到的方向信息，顺时针0-360
                                        .direction(0).latitude(latitude)
                                        .longitude(longitude).build();
                                // 设置定位数据
                                baiduMap.setMyLocationData(locData);


                                //if (isFirstLoc) { //初次定位执行以下操作
                                //  isFirstLoc = false;
                                LatLng ll = new LatLng(latitude, longitude);
                                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(ll, 20f);
                                baiduMap.animateMapStatus(mapStatusUpdate);
                                // }


                                // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
                                BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                                        .fromResource(R.drawable.location_marker);
                                MyLocationConfiguration.LocationMode currentMode = MyLocationConfiguration.LocationMode.NORMAL;
                                MyLocationConfiguration config = new MyLocationConfiguration(currentMode, true, mCurrentMarker);
                                baiduMap.setMyLocationConfigeration(config);
                                // 当不需要定位图层时关闭定位图层
                                baiduMap.setMyLocationEnabled(true);
                            } else {
                               XToastUtils.showShortToast("暂无相关数据");
                            }
                        } else {
                           XToastUtils.showShortToast("暂无相关数据");
                        }
                    } else if (jsonResult != null){
                       XToastUtils.showShortToast(jsonResult.getMessage());
                    }else {
                        XToastUtils.showShortToast("暂无相关数据");
                    }
                } else {
                    Toast.makeText(context, "获取位置信息失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                Toast.makeText(context, "获取位置信息失败", Toast.LENGTH_SHORT).show();
            }

        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1033 && resultCode == 1034) {
            String cno = data.getStringExtra("cno");
            cno_et.setText(cno);
        }
    }

    private void showPopupWindow(View parent) {
        if (popupWindow == null) {
            view = LayoutInflater.from(this).inflate(
                    R.layout.option_layout, null);
            view.setFocusable(true); // 这个很重要
            view.setFocusableInTouchMode(true);

            suoche_rl = (RelativeLayout) view.findViewById(R.id.option2_rl);
            suoche_rl.setVisibility(View.VISIBLE);


            jiesuo_rl = (RelativeLayout) view.findViewById(R.id.option3_rl);
            jiesuo_rl.setVisibility(View.VISIBLE); //不用了，隐藏掉

            view.findViewById(R.id.line3).setVisibility(View.GONE); //隐藏最后一条分割线

            zhaohui_rl = (RelativeLayout) view.findViewById(R.id.option4_rl);
            zhaohui_rl.setVisibility(View.GONE); //不用了，隐藏掉

            popupWindow = new PopupWindow(view, 450, ViewGroup.LayoutParams.WRAP_CONTENT);
        }


        suoche_rl.setOnClickListener(this);
        jiesuo_rl.setOnClickListener(this);
        zhaohui_rl.setOnClickListener(this);

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
        //popupWindow.showAtLocation(parent, Gravity.RELATIVE_LAYOUT_DIRECTION, 0, 0);
        popupWindow.showAsDropDown(parent, 0, 23);

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_ib:
                finish();
                break;

            case R.id.option2_rl: //锁车
                operate_flag = 1;
                carStateOperate("正在锁车...", Path.LOCK_CAR_PATH);
                popupWindow.dismiss();
                break;

            case R.id.option3_rl: //解锁
                operate_flag = 2;
                carStateOperate("正在解锁...", Path.UNLOCK_CAR_PATH);
                popupWindow.dismiss();
                break;


            case R.id.search_btn:
                searchFromUser();
                break;

            case R.id.username_et:
                startActivityForResult(new Intent(context, SelectAct.class).putExtra("selectFlag", Constant.SELECT_CNO_ACT), 1033);
                break;

            default:
                break;
        }
    }


    private void searchFromUser() {
        if (user == null) {
            Toast.makeText(context, "请先登录，再查询位置", Toast.LENGTH_SHORT).show();
            return;
        }

        if (user.getApproleid() == Constant.ADMINISTRATOR || searchObjectFlag == Constant.SEARCH_CAR_POSITION) { //管理员
            if (TextUtils.isEmpty(cno_et.getText().toString().trim())) {
                Toast.makeText(context, cno_et.getHint().toString(), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        loadLocation();

    }


    private void carStateOperate(final String title, final String path) {

//        if ((user.getApproleid() == Constant.ADMINISTRATOR && searchObjectFlag == Constant.SEARCH_CAR_POSITION) || (user.getApproleid() == Constant.NORMAL_USER && "1".equals(user.getPtype()))) {
        if (searchObjectFlag == Constant.SEARCH_CAR_POSITION) {
            if (TextUtils.isEmpty(cno_et.getText().toString().trim())) {
                Toast.makeText(context, "请输入车牌号", Toast.LENGTH_SHORT).show();
                return;
            }

        }

        Map<String, String> map = new HashMap<>();
        map.put("cno", cno_et.getText().toString().trim());
        map.put("orgids", user.getOrgids());

        OkGo.post(path).tag(this).params(map).execute(new StringDialogCallback(LocationAct.this, title) {
            @Override
            public void onSuccess(String result, Call call, Response response) {
                if (!TextUtils.isEmpty(result)) {
                    JsonResult jsonResult = new Gson().fromJson(result, new TypeToken<JsonResult>() {
                    }.getType());
                    if (jsonResult != null && jsonResult.isFlag() && jsonResult.getStatusCode() == 200) {
                        if (operate_flag == 1) {
                            Toast.makeText(context, "锁车成功", Toast.LENGTH_SHORT).show();
                        } else if (operate_flag == 2) {
                            Toast.makeText(context, "解锁成功", Toast.LENGTH_SHORT).show();
                        } else if (operate_flag == 3) {
                            Toast.makeText(context, "丢失上报成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "找回上报成功", Toast.LENGTH_SHORT).show();
                        }
                    } else if (jsonResult != null && jsonResult.getStatusCode() == 300) { //找不到指定车辆
                        Toast.makeText(context, "找不到指定车辆", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "操作失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                Toast.makeText(context, "操作失败", Toast.LENGTH_SHORT).show();
            }

        });


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
}
