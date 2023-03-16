package com.guoji.mobile.cocobee.fragment;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.bql.baseadapter.recycleView.BH;
import com.bql.baseadapter.recycleView.QuickRcvAdapter;
import com.bql.utils.CheckUtils;
import com.bql.utils.EventManager;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.me.SettingActivity;
import com.guoji.mobile.cocobee.activity.me.UserInfoActivity;
import com.guoji.mobile.cocobee.adapter.MeAdapter;
import com.guoji.mobile.cocobee.callback.JsonCallback;
import com.guoji.mobile.cocobee.callback.StringComCallback;
import com.guoji.mobile.cocobee.common.AppConstants;
import com.guoji.mobile.cocobee.common.Constant;
import com.guoji.mobile.cocobee.common.JsonResult;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.fragment.base.BaseFragment;
import com.guoji.mobile.cocobee.fragment.me.AlarmInfoFragment;
import com.guoji.mobile.cocobee.fragment.me.CarDetailFragment;
import com.guoji.mobile.cocobee.fragment.me.IdeaBackFragment;
import com.guoji.mobile.cocobee.fragment.me.PersonDetailFragment;
import com.guoji.mobile.cocobee.model.User;
import com.guoji.mobile.cocobee.response.AdResponse;
import com.guoji.mobile.cocobee.response.HomeRecResponse;
import com.guoji.mobile.cocobee.service.MyService;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.utils.XToastUtils;
import com.guoji.mobile.cocobee.view.GlideImageLoader;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;


/**
 * Created by Administrator on 2017/4/17.
 */

public class MeFragment extends BaseFragment {

    @BindView(R.id.ll_user_info)
    LinearLayout mLlUserInfo;
    @BindView(R.id.ll_setting)
    LinearLayout mLlSetting;
    @BindView(R.id.ll_alrm_info)
    LinearLayout mLlAlrmInfo;
    @BindView(R.id.recycle_view)
    RecyclerView mRecycleView;
    @BindView(R.id.ll_recycle_view)
    LinearLayout mLlRecycleView;
    @BindView(R.id.banner)
    Banner mBanner;
    @BindView(R.id.ll_help_back)
    LinearLayout mLlHelpBack;

    private User mUserLoginInfo;
    private MeAdapter mAdapter;
    //头部
    List<HomeRecResponse> mList = new ArrayList<>();

    private List<Integer> imgs = new ArrayList<>();

    public static MeFragment getInstance() {
        MeFragment meFragment = new MeFragment();
        return meFragment;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        mUserLoginInfo = Utils.getUserLoginInfo();
        if (mUserLoginInfo == null) {
            XToastUtils.showShortToast("您还未登陆,请先登录");
            Utils.gotoLogin();
        } else {
            initView();
            getAdPic();
            initBanner();
            initData();
            initRecycleView();
        }

    }

    //请求绑定的人或车
    private void initData() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("pid", mUserLoginInfo.getPid());

        OkGo.post(Path.GET_CAR_AND_PERSON_INFO).tag(this).params(params).execute(new JsonCallback<List<HomeRecResponse>>(_mActivity) {

            @Override
            public void onSuccess(List<HomeRecResponse> homeRecResponses, Call call, Response response) {
                if (mLlRecycleView == null){
                    return;
                }else if ( homeRecResponses == null || homeRecResponses.size() == 0) {
                    if (mLlRecycleView.getVisibility() == View.VISIBLE) {
                        mLlRecycleView.setVisibility(View.GONE);
                    }
                    return;
                }

                mLlRecycleView.setVisibility(View.VISIBLE);
                List<HomeRecResponse> list = new ArrayList<>();
                if (homeRecResponses != null && homeRecResponses.size() > 0) {
                    for (int i = 0; i < homeRecResponses.size(); i++) {
                        HomeRecResponse homeRecResponse = homeRecResponses.get(i);
                        if (CheckUtils.equalsString(homeRecResponse.getFinish(), "yes")) {
                            list.add(homeRecResponse);
                        }
                    }
                }
                mList.clear();
                mList.addAll(list);
                mAdapter.notifyDataSetChanged();
                initView();
            }
        });

    }

    //初始化RecycleView
    private void initRecycleView() {
        mAdapter = new MeAdapter(getContext(), mList);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        mRecycleView.setLayoutManager(layoutManager);
        mRecycleView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new QuickRcvAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(BH viewHolder, int position) {
                HomeRecResponse homeRecResponse = mList.get(position);
                if (CheckUtils.equalsString(homeRecResponse.getTarget_id(), AppConstants.TYPE_PEPOLE)) {//人
                    start(PersonDetailFragment.getInstance(homeRecResponse));
                } else {//车
                    start(CarDetailFragment.getInstance(homeRecResponse));
                }
            }
        });
    }

    private void initView() {
        switch (mUserLoginInfo.getApproleid()) {
            case Constant.POLICE:
                Intent intent = new Intent(_mActivity, MyService.class);
                _mActivity.startService(intent);
                break;
        }
    }

    private void initBanner() {
        imgs.add(R.drawable.appbanner01);
        imgs.add(R.drawable.appbanner02);
        //设置banner样式
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器
        mBanner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        mBanner.setImages(imgs);
        //设置banner动画效果
        mBanner.setBannerAnimation(Transformer.Default);
        //设置标题集合（当banner样式有显示title时）
//        mBanner.setBannerTitles(titles);
        //设置自动轮播，默认为true
//        mBanner.isAutoPlay(true);
        //设置轮播时间
//        mBanner.setDelayTime(1500);
        //设置指示器位置（当banner模式中有指示器时）
        mBanner.setIndicatorGravity(BannerConfig.CENTER);
        //banner设置方法全部调用完毕时最后调用
        mBanner.start();
    }

    //从服务器获取广告图片
    public void getAdPic() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("orgid", mUserLoginInfo.getOrgid());

        OkGo.post(Path.GET_AD_INFO).tag(this).params(params).execute(new StringComCallback() {
            @Override
            public void onSuccess(String result, Call call, Response response) {
                if (!TextUtils.isEmpty(result)) {
                    JsonResult jsonResult = new Gson().fromJson(result, new TypeToken<JsonResult>() {
                    }.getType());
                    if (jsonResult != null && jsonResult.isFlag() && jsonResult.getStatusCode() == 200) {//成功
                        String jsonStr = jsonResult.getResult();
                        if (!CheckUtils.isEmpty(jsonStr)) {
                            List<AdResponse> list = new Gson().fromJson(jsonStr, new TypeToken<List<AdResponse>>() {
                            }.getType());
                            List<String> adList = new ArrayList<>();
                            if (list == null || list.size() == 0) {
                                return;
                            }
                            for (int i = 0; i < list.size(); i++) {
                                AdResponse adResponse = list.get(i);
                                if (!CheckUtils.isEmpty(adResponse.getAd_pic_url())) {
                                    adList.add(Path.IMG_BASIC_PATH + adResponse.getAd_pic_url());
                                }
                            }
                            if (adList.size() > 0) {
                                mBanner.update(adList);
                            }
                        }
                    }
                }

            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
            }
        });
    }

    @Override
    protected void initToolbarHere() {
        initToolbar("我的");
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_me;
    }


    @OnClick({R.id.ll_user_info, R.id.ll_setting, R.id.ll_alrm_info, R.id.ll_help_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.ll_user_info:
                if (mUserLoginInfo != null) {
                    startActivity(new Intent(_mActivity, UserInfoActivity.class));
                } else {
                    XToastUtils.showShortToast("请先登录");
                    Utils.gotoLogin();
                }
                break;

            case R.id.ll_setting://设置
                startActivity(new Intent(_mActivity, SettingActivity.class));
                break;
            case R.id.ll_alrm_info://报警信息
                start(AlarmInfoFragment.getInstance());
                break;
            case R.id.ll_help_back://意见反馈
                start(IdeaBackFragment.getInstance());
                break;
        }
    }


    private double latitude = 0.0;
    private double longitude = 0.0;
    private LocationManager locationManager;

    //民警用户30秒上传一次位置信息
    public void getLoc() {
        locationManager = (LocationManager) _mActivity.getSystemService(Context.LOCATION_SERVICE);
        //监听
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
        handler.postDelayed(runnable, 30000);//每30秒执行一次runnable.
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //gps已打开
            getLocation();
        } else {
            XToastUtils.showShortToast("请打开gps");
        }
    }

    private void getLocation() {
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
        updateLocation(longitude, latitude);
    }

    //上传位置信息到服务器
    private void updateLocation(double lat, double lon) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("pid", mUserLoginInfo.getPid());//民警用户主键pid
        params.put("plng", "" + lat);//民警当前位置经度
        params.put("plat", "" + lon);//民警当前位置纬度

        OkGo.post(Path.POLICE_NOW_LOCATION).tag(this).params(params).execute(new StringComCallback() {
            @Override
            public void onSuccess(String result, Call call, Response response) {

                if (!TextUtils.isEmpty(result)) {
                    JsonResult jsonResult = new Gson().fromJson(result, new TypeToken<JsonResult>() {
                    }.getType());
                    if (jsonResult != null && jsonResult.getStatusCode() == 200) {//上传成功

                    }
                }
            }
        });
    }

    LocationListener locationListener = new LocationListener() {
        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        // Provider被enable时触发此函数，比如GPS被打开
        @Override
        public void onProviderEnabled(String provider) {
            XToastUtils.showShortToast("Gps被打开");
            getLocation();
        }

        // Provider被disable时触发此函数，比如GPS被关闭
        @Override
        public void onProviderDisabled(String provider) {
            XToastUtils.showShortToast("Gps被关闭");
        }

        // 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                Log.e("Map", "Location changed : Lat: " + location.getLatitude() + " Lng: " + location.getLongitude());
                latitude = location.getLatitude(); // 纬度
                longitude = location.getLongitude(); // 经度
            }
        }
    };

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //要做的事情
            getLoc();
        }
    };


    @Override
    public boolean registerEventBus() {
        return true;
    }

    @Override
    protected void onHandleEvent(EventManager eventManager) {
        super.onHandleEvent(eventManager);
        switch (eventManager.getEventCode()) {
            case AppConstants.AVAR_PIC_UPLOAD_SUCCESS://头像上传成功
                mUserLoginInfo = Utils.getUserLoginInfo();
                initData();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
        handler.removeCallbacks(runnable);
    }
}
