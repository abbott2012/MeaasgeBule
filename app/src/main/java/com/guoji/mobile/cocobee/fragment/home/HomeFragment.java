package com.guoji.mobile.cocobee.fragment.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.bql.baseadapter.recycleView.BH;
import com.bql.baseadapter.recycleView.QuickRcvAdapter;
import com.bql.utils.CheckUtils;
import com.bql.utils.EventManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.TraceActivity;
import com.guoji.mobile.cocobee.activity.me.AlermActivity;
import com.guoji.mobile.cocobee.activity.me.LabelBindingCarAnXinActivity;
import com.guoji.mobile.cocobee.activity.me.LabelBindingPersonAnXinActivity;
import com.guoji.mobile.cocobee.activity.me.LabelBindingTiYan;
import com.guoji.mobile.cocobee.activity.me.car.SelectServiceActivity;
import com.guoji.mobile.cocobee.adapter.HomeAdapter;
import com.guoji.mobile.cocobee.callback.DialogCallback;
import com.guoji.mobile.cocobee.callback.JsonCallback;
import com.guoji.mobile.cocobee.common.AppConstants;
import com.guoji.mobile.cocobee.common.Constant;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.model.User;
import com.guoji.mobile.cocobee.response.HomePositionResponse;
import com.guoji.mobile.cocobee.response.HomeRecResponse;
import com.guoji.mobile.cocobee.response.SelectServiceResponse;
import com.guoji.mobile.cocobee.utils.ImageUtil;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.utils.XToastUtils;
import com.lzy.okgo.OkGo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/6/20.
 * 首页fragment
 */

public class HomeFragment extends Fragment {
    //解决侧滑时卡顿现象
    @BindView(R.id.mapView)
    TextureMapView mMapView;
    @BindView(R.id.iv_car)
    ImageView mIvCar;
    @BindView(R.id.iv_person)
    ImageView mIvPerson;
    @BindView(R.id.iv_lock)
    ImageView mIvLock;
    @BindView(R.id.iv_position)
    ImageView mIvPosition;
    @BindView(R.id.iv_path)
    ImageView mIvPath;
    @BindView(R.id.recycle_view)
    RecyclerView mRecycleView;
    @BindView(R.id.ll_add_more)
    LinearLayout mLlAddMore;
    @BindView(R.id.ll_car_person)
    LinearLayout mLlCarPerson;
    @BindView(R.id.ll_first_car_person)
    LinearLayout mLlFirstCarPerson;
    @BindView(R.id.view_home)
    View mViewHome;
    @BindView(R.id.iv_alert)
    ImageView mIvAlert;
    @BindView(R.id.ll_refresh)
    LinearLayout mLlRefresh;
    private BaiduMap baiduMap;

    //头部
    List<HomeRecResponse> mList = new ArrayList<>();
    private int selectPosition = -1;
    private double latitude;
    private double longitude;
    private User mUserLoginInfo;
    private HomeAdapter mAdapter;
    private ImageView mIvLocationIcon;


    //定位
    private LocationClient locationClient;
    private String address;
    private boolean isFirst = true;
    public boolean isMoving = false;
    private Context mContext;
    private EditText mEtCarDesc;

    public static HomeFragment getInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);
        ButterKnife.bind(this, view);
        mContext = getContext();
        mUserLoginInfo = Utils.getUserLoginInfo();
        EventBus.getDefault().register(this);
        getCurrentLocation();
        initData();
        initView();
        initMap();
        initRecycleView();
        return view;
    }


    private void getCurrentLocation() {
        /*使用百度SDK获取经纬度*/
        locationClient = new LocationClient(mContext);
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

                longitude = location.getLongitude(); //获取经度
                latitude = location.getLatitude();

                address = location.getAddrStr();

                if (isFirst) {
                    initLocation(mUserLoginInfo.getPhotourl(), AppConstants.TYPE_PEPOLE);//显示定位图标,进来显示用户头像
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

    private void initLocation(String photourl, String car) {
        baiduMap.setMyLocationEnabled(true);
        // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）

        //如果是人的加载人的默认icon,车加载车的默认icon
        if (CheckUtils.equalsString(car, AppConstants.TYPE_CAR)) {
            loadPersonCarPhoto(photourl, R.drawable.location_bike_default);
        } else {
            loadPersonCarPhoto(photourl, R.drawable.location_person_default);
        }

        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(0)  //如果不需要精度圈，将radius设为0即可
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(0).latitude(latitude)
                .longitude(longitude).build();
        // 设置定位数据
        baiduMap.setMyLocationData(locData);

        LatLng ll = new LatLng(latitude, longitude);
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(ll, 17f);
        baiduMap.animateMapStatus(mapStatusUpdate);
        isMoving = false;

    }

    //加载定位图标
    private void loadPersonCarPhoto(String photourl, int location_person_default) {
        View view = View.inflate(mContext, R.layout.location_icon, null);
        mIvLocationIcon = (ImageView) view.findViewById(R.id.iv_location_icon);
        Glide.with(mContext)
                .load(ImageUtil.getUrl(Path.IMG_BASIC_PATH + photourl))
                .bitmapTransform(new CenterCrop(mContext), new CropCircleTransformation(mContext))
                .crossFade()
                .placeholder(location_person_default)
                .error(location_person_default)
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        mIvLocationIcon.setImageDrawable(resource);
                        addMarker(view);
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        mIvLocationIcon.setImageDrawable(errorDrawable);
                        addMarker(view);
                    }
                });
    }

    private void addMarker(View view) {
        //图片加载完成在操作地图
        if (view == null) {
            return;
        }
        BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromView(view);
        MyLocationConfiguration.LocationMode currentMode = MyLocationConfiguration.LocationMode.NORMAL;
        MyLocationConfiguration config = new MyLocationConfiguration(currentMode, true, mCurrentMarker);
        baiduMap.setMyLocationConfigeration(config);
    }

    //请求绑定的人或车
    private void initData() {
        mUserLoginInfo = Utils.getUserLoginInfo();
        Map<String, String> params = new HashMap<String, String>();
        params.put("pid", mUserLoginInfo.getPid());

        OkGo.post(Path.GET_CAR_AND_PERSON_INFO).params(params).execute(new JsonCallback<List<HomeRecResponse>>(mContext) {

            @Override
            public void onSuccess(List<HomeRecResponse> homeRecResponses, Call call, Response response) {
                mLlRefresh.setVisibility(View.GONE);
                if (homeRecResponses == null) {
                    return;
                }
                mList.clear();
                mList.addAll(homeRecResponses);
                mAdapter.notifyDataSetChanged();
                initView();
                initLockIcon();
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                mLlRefresh.setVisibility(View.VISIBLE);
                setCarPersonInVisbility();
            }
        });

    }

    private void initView() {
        if (mList != null && mList.size() > 0) {//有车或人
            mLlCarPerson.setVisibility(View.VISIBLE);
            mLlFirstCarPerson.setVisibility(View.GONE);
            mViewHome.setVisibility(View.GONE);
            mIvLock.setEnabled(true);
            mIvAlert.setEnabled(true);
            mIvPath.setEnabled(true);
            mIvPosition.setEnabled(true);
            initLocation(mUserLoginInfo.getPhotourl(), AppConstants.TYPE_PEPOLE);//显示定位图标,进来显示用户头像
        } else {
            mLlCarPerson.setVisibility(View.GONE);
            mLlFirstCarPerson.setVisibility(View.VISIBLE);
            mViewHome.setVisibility(View.VISIBLE);
            mIvLock.setEnabled(false);
            mIvAlert.setEnabled(false);
            mIvPath.setEnabled(false);
            mIvPosition.setEnabled(false);
        }
    }

    //初始化RecycleView
    private void initRecycleView() {

        mAdapter = new HomeAdapter(mContext, mList);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        mRecycleView.setLayoutManager(layoutManager);
        mRecycleView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new QuickRcvAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(BH viewHolder, int position) {
                setCarPersonVisbility();
                HomeRecResponse homeRecResponse1 = mList.get(position);
                if (CheckUtils.equalsString(homeRecResponse1.getFinish(), "no")) {//未完成
                    showUnFinishDialog(homeRecResponse1, "您有一个标签已完成付费,但未完成信息绑定!", false);
                } else {
                    selectPosition = position;//选中的条目,供后面使用
                    List<HomeRecResponse> list = new ArrayList<>();
                    for (int i = 0; i < mList.size(); i++) {
                        HomeRecResponse homeRecResponse = mList.get(i);
                        if (i == position) {
                            homeRecResponse.setFlag(position);
                        } else {
                            homeRecResponse.setFlag(-1);
                        }
                        list.add(homeRecResponse);
                    }
                    mList.clear();
                    mList.addAll(list);
                    mAdapter.notifyDataSetChanged();
                    getLabelIdPosition();
                }
                initLockIcon();
            }
        });
    }


    private void showUnFinishDialog(HomeRecResponse homeRecResponse, String dialogContent, boolean isAlert) {
        SweetAlertDialog continueDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.NORMAL_TYPE);
        continueDialog.setTitleText("温馨提示");
        continueDialog.setContentText(dialogContent);
        continueDialog.showCancelButton(true).setCancelText("再等等");
        continueDialog.setConfirmText(isAlert ? "去升级" : "去绑定");
        continueDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                if (isAlert) {
                    gotoUpdate(homeRecResponse);
                } else {
                    gotoAddInfo(homeRecResponse);
                }
                sweetAlertDialog.dismiss();
            }
        });

        continueDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismiss();
            }
        });

        continueDialog.show();
    }

    //去升级
    private void gotoUpdate(HomeRecResponse homeRecResponse) {
        Intent intent = null;

        if (CheckUtils.isEmpty(mUserLoginInfo.getIdcard())) {
//            intent = new Intent(mContext, CarInputBaseInfo.class);
        } else {
            intent = new Intent(mContext, LabelBindingCarAnXinActivity.class);
        }

        if (intent != null) {
            SelectServiceResponse selectServiceResponse = new SelectServiceResponse();
            selectServiceResponse.setCard_id(homeRecResponse.getCard_id());
            selectServiceResponse.setTarget_id(homeRecResponse.getTarget_id());
            intent.putExtra("selectServiceResponse", selectServiceResponse);
            intent.putExtra("orderId", homeRecResponse.getOrder_id());
            intent.putExtra("lno", homeRecResponse.getLno());
        }
        startActivity(intent);
    }

    //去完善资料
    private void gotoAddInfo(HomeRecResponse homeRecResponse1) {
        Intent intent = null;
        if (CheckUtils.equalsString(homeRecResponse1.getCard_id(), AppConstants.TYPE_CARD_TIYAN)) {//体验卡
            intent = new Intent(mContext, LabelBindingTiYan.class);
        } else if (CheckUtils.equalsString(homeRecResponse1.getCard_id(), AppConstants.TYPE_CARD_ANXIN) && CheckUtils.equalsString(homeRecResponse1.getTarget_id(), AppConstants.TYPE_CAR)) {//安心卡,车
            if (CheckUtils.isEmpty(mUserLoginInfo.getIdcard())) {
//                intent = new Intent(mContext, CarInputBaseInfo.class);
            } else {
                intent = new Intent(mContext, LabelBindingCarAnXinActivity.class);
            }
        } else if (CheckUtils.equalsString(homeRecResponse1.getCard_id(), AppConstants.TYPE_CARD_ANXIN) && CheckUtils.equalsString(homeRecResponse1.getTarget_id(), AppConstants.TYPE_PEPOLE)) {//安心卡,人
            intent = new Intent(mContext, LabelBindingPersonAnXinActivity.class);
        }

        if (intent != null) {
            SelectServiceResponse selectServiceResponse = new SelectServiceResponse();
            selectServiceResponse.setCard_id(homeRecResponse1.getCard_id());
            selectServiceResponse.setTarget_id(homeRecResponse1.getTarget_id());
            intent.putExtra("selectServiceResponse", selectServiceResponse);
            intent.putExtra("orderId", homeRecResponse1.getOrder_id());
        }
        startActivity(intent);
    }

    private void initMap() {
        // 隐藏百度的LOGO
        View child = mMapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }
        // 不显示地图缩放控件（按钮控制栏）
        mMapView.showZoomControls(false);

        baiduMap = mMapView.getMap(); //得到百度地图
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL); //设置地图为正常模式
        baiduMap.setMyLocationEnabled(true);//开启图层定位
        //设定中心点坐标
        LatLng cenpt = new LatLng(latitude, longitude);//科技园
//        LatLng cenpt = new LatLng(22.555331, 113.951446);//科技园
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(cenpt)
                .zoom(17)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化

        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        baiduMap.setMapStatus(mMapStatusUpdate);
        initListener();
    }

    //监听百度地图滑动动画是否完成,只有完成后才能允许跳转,否则地图卡死
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

    @OnClick({R.id.iv_car, R.id.iv_person, R.id.iv_lock, R.id.iv_position, R.id.iv_path, R.id.ll_add_more, R.id.iv_alert, R.id.view_home, R.id.ll_refresh})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_car://守护车辆
                Intent intent = new Intent(mContext, SelectServiceActivity.class);
                intent.putExtra("target_id", AppConstants.TYPE_CAR);
                startActivity(intent);
                break;
            case R.id.iv_person://守护家人
                Intent i = new Intent(mContext, SelectServiceActivity.class);
                i.putExtra("target_id", AppConstants.TYPE_PEPOLE);
                startActivity(i);
                break;
            case R.id.iv_lock://锁车
                if (selectPosition == -1) {
                    XToastUtils.showShortToast("请选择要锁定的对象");
                    return;
                }
                HomeRecResponse recResponse = mList.get(selectPosition);
                String islocked = recResponse.getIslocked();
                if (CheckUtils.equalsString(recResponse.getTarget_id(), AppConstants.TYPE_CAR) && CheckUtils.equalsString(recResponse.getFinish(), "yes")) {//选中的是车
                    if (CheckUtils.equalsString(islocked, "0")) {//目前是锁定状态
                        lockCar(recResponse, "1");//解锁
                    } else {//解锁状态
                        lockCar(recResponse, "0");//锁车
                    }
                } else {
                    XToastUtils.showShortToast("当前对象不具备锁定功能");
                }
                break;
            case R.id.iv_position://位置
                getLabelIdPosition();
                break;
            case R.id.iv_path://轨迹
                startToTrace();
                break;
            case R.id.ll_add_more://添加更多
                HomeRecResponse homeRecResponse1 = null;
                if (mList != null && mList.size() > 0) {
                    for (int j = 0; j < mList.size(); j++) {
                        HomeRecResponse homeRecResponse = mList.get(j);
                        if (CheckUtils.equalsString(homeRecResponse.getFinish(), "no")) {//未完成
                            homeRecResponse1 = homeRecResponse;
                            break;
                        }
                    }
                }
                if (homeRecResponse1 != null) {//存在未完成订单
                    showUnFinishDialog(homeRecResponse1, "您有一个未完成信息绑定的标签,请先绑定后再添加更多!", false);
                } else {//不存在未完成订单
                    setCarPersonVisble();
                }
                break;
            case R.id.iv_alert://报警
                if (selectPosition == -1) {
                    XToastUtils.showShortToast("请选择报警对象");
                } else {
                    HomeRecResponse homeRecResponse = mList.get(selectPosition);
                    if (CheckUtils.equalsString(homeRecResponse.getCard_id(), AppConstants.TYPE_CARD_TIYAN)) {//体验卡
                        showUnFinishDialog(homeRecResponse, "您需要升级为安心卡才能使用报警功能,是否去升级?", true);
                    } else {//安心卡
                        gotoAlert(homeRecResponse);
                    }
                }
                break;
            case R.id.view_home:
                if (mList == null || mList.size() == 0) {
                    return;
                }
                setCarPersonVisbility();
                break;
            case R.id.ll_refresh://断网刷新
                mLlRefresh.setVisibility(View.GONE);
                initData();
                break;
        }
    }

    private void gotoAlerm(HomeRecResponse homeRecResponse) {
        Intent intent = new Intent(getActivity(), AlermActivity.class);
        intent.putExtra("homeRecResponse", homeRecResponse);
        intent.putExtra("address", address);
        intent.putExtra("longitude", String.valueOf(longitude));
        intent.putExtra("latitude", String.valueOf(latitude));
        startActivity(intent);
    }

    private void setCarPersonVisble() {
        //点击添加更多,车和人显示
        if (mLlFirstCarPerson.getVisibility() == View.VISIBLE) {
            mLlFirstCarPerson.setVisibility(View.GONE);
            mViewHome.setVisibility(View.GONE);
            mIvLock.setEnabled(true);
            mIvAlert.setEnabled(true);
            mIvPath.setEnabled(true);
            mIvPosition.setEnabled(true);
        } else {
            mLlFirstCarPerson.setVisibility(View.VISIBLE);
            mViewHome.setVisibility(View.VISIBLE);
            mIvLock.setEnabled(false);
            mIvAlert.setEnabled(false);
            mIvPath.setEnabled(false);
            mIvPosition.setEnabled(false);
        }
    }

    //点击条目,取消车和人的添加
    private void setCarPersonVisbility() {
        if (mLlFirstCarPerson.getVisibility() == View.VISIBLE) {
            mLlFirstCarPerson.setVisibility(View.GONE);
            mViewHome.setVisibility(View.GONE);
            mIvLock.setEnabled(true);
            mIvAlert.setEnabled(true);
            mIvPath.setEnabled(true);
            mIvPosition.setEnabled(true);
        }
    }

    //刚进来时没有请求到数据显示
    private void setCarPersonInVisbility() {
        mLlFirstCarPerson.setVisibility(View.GONE);
        mViewHome.setVisibility(View.GONE);
        mIvLock.setEnabled(false);
        mIvAlert.setEnabled(false);
        mIvPath.setEnabled(false);
        mIvPosition.setEnabled(false);
    }

    //报警
    private void gotoAlert(HomeRecResponse homeRecResponse) {
        View view = View.inflate(getContext(), R.layout.home_upload_alarm_info, null);
        AlertDialog alertDialog = Utils.showCornersDialog(getContext(), view);
        TextView tvName = (TextView) alertDialog.findViewById(R.id.tv_name);
        TextView tvPhoneNum = (TextView) alertDialog.findViewById(R.id.tv_phone_num);
        TextView tvIdCardNum = (TextView) alertDialog.findViewById(R.id.tv_id_card_num);
        TextView tvCarType = (TextView) alertDialog.findViewById(R.id.tv_car_type);
        TextView tvCarNum = (TextView) alertDialog.findViewById(R.id.tv_car_num);
        TextView tvCancel = (TextView) alertDialog.findViewById(R.id.tv_cancel);
        TextView tvSureAlerm = (TextView) alertDialog.findViewById(R.id.tv_sure_alerm);
        ImageView ivCarPic = (ImageView) alertDialog.findViewById(R.id.iv_car_pic);
        mEtCarDesc = (EditText) alertDialog.findViewById(R.id.et_car_desc);
        String phoneFirstAndEnd = Utils.getPhoneFirstAndEnd(mUserLoginInfo.getMobile());
        String idCardFirstAndEnd = Utils.getIdCardFirstAndEnd(homeRecResponse.getIdcard());
        tvName.setText("车主姓名: " + homeRecResponse.getPname());
        tvPhoneNum.setText("手机号: " + phoneFirstAndEnd);
        tvIdCardNum.setText("身份证号: " + idCardFirstAndEnd);
        tvCarType.setText("车辆型号: " + homeRecResponse.getCbuytype());
        tvCarNum.setText("车牌号: " + homeRecResponse.getCno());
        ImageUtil.loadAlermPic(mContext, Path.IMG_BASIC_PATH + getFirstPic(homeRecResponse.getCcarpicurl()), ivCarPic);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        tvSureAlerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAlertInfo(homeRecResponse, alertDialog);
            }
        });

    }


    //上传报警信息
    private void updateAlertInfo(HomeRecResponse homeRecResponse, AlertDialog alertDialog) {
        Map<String, String> params = new HashMap<>();
        String describeEt = mEtCarDesc.getText().toString().trim();
        if (CheckUtils.isEmpty(describeEt)) {
            params.put("adesc", "我的车被盗了,请求支援!");
        } else {
            params.put("adesc", describeEt);
        }
        params.put("pid", mUserLoginInfo.getPid());
        params.put("cid", homeRecResponse.getCid());
        params.put("labelid", homeRecResponse.getLabelid());
        params.put("apeople", mUserLoginInfo.getPname());
        params.put("amobile", mUserLoginInfo.getMobile());
        params.put("atype", "1");
        params.put("orgid", mUserLoginInfo.getOrgid());
        params.put("aaddress", address);
        params.put("alarmlng", String.valueOf(longitude));
        params.put("alarmlat", String.valueOf(latitude));

        params.put("photo", "");

        OkGo.post(Path.NORMAL_USER_UPLOAD_ALARM_INFO_PATH).tag(this).params(params).execute(new DialogCallback<Object>(getContext(), "报警信息上传中...") {
            @Override
            public void onSuccess(Object o, Call call, Response response) {
                XToastUtils.showShortToast("报警信息上传成功,我们会尽快为您处理!");
                alertDialog.dismiss();
            }
        });

    }

    //设置锁车和报警图标
    private void initLockIcon() {
        if (mList == null || mList.size() == 0) {
            return;
        }
        if (selectPosition == -1) {
            return;
        }
        HomeRecResponse homeRecResponse = mList.get(selectPosition);
        if (CheckUtils.equalsString(homeRecResponse.getTarget_id(), AppConstants.TYPE_CAR)) {//只有车才显示报警和锁车图标
            mIvLock.setVisibility(View.VISIBLE);
            mIvAlert.setVisibility(View.VISIBLE);
            if (CheckUtils.equalsString(homeRecResponse.getIslocked(), "0")) {//锁定状态
                mIvLock.setImageResource(R.drawable.button_lock_c);
            } else {
                mIvLock.setImageResource(R.drawable.btn_lock_selector);
            }
        } else {
            mIvLock.setVisibility(View.INVISIBLE);
            mIvAlert.setVisibility(View.INVISIBLE);
        }
    }


    //锁车
    private void lockCar(HomeRecResponse recResponse, String statu) {
        Map<String, String> map = new HashMap<>();
        map.put("labelid", recResponse.getLabelid());
        map.put("statu", statu); //锁车传0， 解锁传1

        OkGo.post(Path.LOCK_UNLOCK_CAR).tag(this).params(map).execute(new DialogCallback<Object>(getActivity(), CheckUtils.equalsString(statu, "0") ? "正在锁车..." : "正在解锁...") {

            @Override
            public void onSuccess(Object o, Call call, Response response) {
                if (CheckUtils.equalsString(statu, "0")) {
                    XToastUtils.showShortToast("锁车成功");
                    mIvLock.setImageResource(R.drawable.button_lock_c);
                } else {
                    XToastUtils.showShortToast("解锁成功");
                    mIvLock.setImageResource(R.drawable.btn_lock_selector);
                }
                List<HomeRecResponse> list = new ArrayList<>();
                for (int i = 0; i < mList.size(); i++) {
                    HomeRecResponse homeRecResponse = mList.get(i);
                    if (i == selectPosition) {
                        homeRecResponse.setIslocked(CheckUtils.equalsString(statu, "0") ? "0" : "1");
                    }
                    list.add(homeRecResponse);
                }
                mList.clear();
                mList.addAll(list);
            }
        });
    }

    //进入轨迹
    private void startToTrace() {
        if (selectPosition == -1) {
            XToastUtils.showShortToast("请选择要查询的对象");
            return;
        }
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //进入到这里代表没有获取定位权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                //已经禁止提示了
                XToastUtils.showShortToast("您已禁止该权限，需要重新开启");
                return;
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 11);

            }
        }
        HomeRecResponse homeRecResponse = mList.get(selectPosition);
        Intent intent = new Intent(mContext, TraceActivity.class);
        intent.putExtra("labelId", homeRecResponse.getLabelid());
        if (CheckUtils.equalsString(homeRecResponse.getTarget_id(), AppConstants.TYPE_CAR)) {//车
            intent.putExtra("searchObject", Constant.SEARCH_CAR_TRACE);
        } else {
            intent.putExtra("searchObject", Constant.SEARCH_PEOPLE_TRACE);
        }
        if (!isMoving) {
            startActivity(intent);
        }
    }

    //查询标签位置
    private void getLabelIdPosition() {
        if (selectPosition == -1) {
            XToastUtils.showShortToast("请选择要查询的对象");
            return;
        }
        HomeRecResponse homeRecResponse = mList.get(selectPosition);
        Map<String, String> params = new HashMap<String, String>();
        params.put("labelid", homeRecResponse.getLabelid());

        OkGo.post(Path.GET_LABEL_POSITION).params(params).execute(new DialogCallback<HomePositionResponse>(mContext, "位置获取中...") {

            @Override
            public void onSuccess(HomePositionResponse homePositionResponse, Call call, Response response) {
                if (homePositionResponse != null && !CheckUtils.isEmpty(homePositionResponse.getEqlat()) && !CheckUtils.isEmpty(homePositionResponse.getEqlng())) {
                    latitude = Double.parseDouble(homePositionResponse.getEqlat());
                    longitude = Double.parseDouble(homePositionResponse.getEqlng());
                    if (CheckUtils.equalsString(homeRecResponse.getTarget_id(), AppConstants.TYPE_CAR)) {//车
                        String firstPic = getFirstPic(homeRecResponse.getCcarpicurl());
                        initLocation(firstPic, AppConstants.TYPE_CAR);
                    } else {
                        String firstPic = getFirstPic(homeRecResponse.getPhotourl());
                        initLocation(firstPic, AppConstants.TYPE_PEPOLE);
                    }
                } else {
                    XToastUtils.showShortToast("暂无位置信息");
                }
            }
        });
    }

    private String getFirstPic(String photourl) {
        if (!CheckUtils.isEmpty(photourl)) {
            String[] carPic = photourl.split(",");
            if (carPic != null && carPic.length > 0) {
                return carPic[0];
            }
        }
        return null;
    }

    @Override
    public void onStart() {
        super.onStart();
        //开启定位
        baiduMap.setMyLocationEnabled(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        //关闭定位
        baiduMap.setMyLocationEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        if (baiduMap != null) { //关闭定位图层
            baiduMap.setMyLocationEnabled(false);
        }

        if (mMapView != null) {
            mMapView.onDestroy();
            mMapView = null;
        }
        EventBus.getDefault().unregister(this);
        OkGo.getInstance().cancelTag(this);
        locationClient.stop();
        super.onDestroy();
    }

    @Subscribe
    public void onEventMainThread(EventManager eventManager) {
        if (null != eventManager) {
            switch (eventManager.getEventCode()) {
                case AppConstants.PAY_FINISH://支付完成
//                case AppConstants.LABEL_BIND_SUCCESS://体验卡,人安心卡绑定标签成功
//                case AppConstants.CAR_INFO_UPDATE_SUCCESS://车安心卡绑定标签成功
                case AppConstants.YAO_QING_FAMILY_SUCCESS://邀请家人成功
                case AppConstants.WAIT_YAO_QING_FAMILY://邀请家人中
                case AppConstants.INFO_PUT_SUCCESS://个人信息录入成功
                case AppConstants.ALERM_SUCCESS://用户报警成功
                    initData();
                    break;
                case AppConstants.OUT_LOGIN_SUCCESS://退出登录成功
                    getActivity().finish();
                    break;

            }
        }
    }

}
