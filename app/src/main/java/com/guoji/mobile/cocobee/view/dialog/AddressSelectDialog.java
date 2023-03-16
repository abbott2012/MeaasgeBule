package com.guoji.mobile.cocobee.view.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bql.pulltorefreshandloadmore.loadmoreview.HeaderAndFooterRecyclerViewAdapter;
import com.bql.pulltorefreshandloadmore.loadmoreview.LoadMoreRecyclerView;
import com.bql.utils.CheckUtils;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.adapter.AddressSelectAdapter;
import com.guoji.mobile.cocobee.callback.DialogCallback;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.model.User;
import com.guoji.mobile.cocobee.response.GetUserOrg;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.utils.XToastUtils;
import com.guoji.mobile.cocobee.view.ListLineDecoration;
import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 选择地址
 * Created: 2016/11/29 19:54
 */
public class AddressSelectDialog extends DialogFragment {

    @BindView(R.id.recycle_view)
    LoadMoreRecyclerView mRecyclerView;
    @BindView(R.id.tv_confirm)
    TextView mTvConfirm;
    @BindView(R.id.tv_sheng)
    TextView mTvSheng;
    @BindView(R.id.view_sheng)
    View mViewSheng;
    @BindView(R.id.tv_shi)
    TextView mTvShi;
    @BindView(R.id.view_shi)
    View mViewShi;
    @BindView(R.id.tv_xian)
    TextView mTvXian;
    @BindView(R.id.view_xian)
    View mViewXian;
    @BindView(R.id.tv_qu)
    TextView mTvQu;
    @BindView(R.id.view_qu)
    View mViewQu;
    @BindView(R.id.tv_jie)
    TextView mTvJie;
    @BindView(R.id.view_jie)
    View mViewJie;

    private List<GetUserOrg> list = new ArrayList<>();
    private List<GetUserOrg> shengList;
    private List<GetUserOrg> shiList;
    private List<GetUserOrg> xianList;
    private List<GetUserOrg> quList;
    private List<GetUserOrg> jieList;
    private AddressSelectAdapter mAdapter;
    private String flag = "1";
    private String mOrgId;
    //定位
    private LocationClient locationClient;
    private String mCity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = Utils.getScreenWidth();
        lp.height = Utils.dp2px(316);
        lp.windowAnimations = R.style.DialogAnimation;
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_use_coupon, container);
        ButterKnife.bind(this, view);
//        getCurrentLocation();
        getServerOrg("", "1");
        initView("0");
        setRecyclerView();
        return view;
    }

    private void getCurrentLocation() {
        /*使用百度SDK获取经纬度*/
        locationClient = new LocationClient(getContext());
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
                mCity = location.getCity();
                System.out.println("#######" + mCity);
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


    private void initView(String level) {
        if (CheckUtils.equalsString(level, "0")) {
            mTvShi.setVisibility(View.GONE);
            mTvXian.setVisibility(View.GONE);
            mTvQu.setVisibility(View.GONE);
            mTvJie.setVisibility(View.GONE);
        } else if (CheckUtils.equalsString(level, "1")) {
            mTvShi.setVisibility(View.GONE);
            mTvXian.setVisibility(View.GONE);
            mTvQu.setVisibility(View.GONE);
            mTvJie.setVisibility(View.GONE);
        } else if (CheckUtils.equalsString(level, "2")) {
            mTvShi.setVisibility(View.VISIBLE);
            mTvXian.setVisibility(View.GONE);
            mTvQu.setVisibility(View.GONE);
            mTvJie.setVisibility(View.GONE);
        } else if (CheckUtils.equalsString(level, "3")) {
            mTvShi.setVisibility(View.VISIBLE);
            mTvXian.setVisibility(View.VISIBLE);
            mTvQu.setVisibility(View.GONE);
            mTvJie.setVisibility(View.GONE);
        } else if (CheckUtils.equalsString(level, "4")) {
            mTvShi.setVisibility(View.VISIBLE);
            mTvXian.setVisibility(View.VISIBLE);
            mTvQu.setVisibility(View.VISIBLE);
            mTvJie.setVisibility(View.GONE);
        } else if (CheckUtils.equalsString(level, "5")) {
            mTvShi.setVisibility(View.VISIBLE);
            mTvXian.setVisibility(View.VISIBLE);
            mTvQu.setVisibility(View.VISIBLE);
            mTvJie.setVisibility(View.VISIBLE);
        }
    }

    //弹出时自动获取第一级
    public void getServerOrg(String fatherorgid, String orglevel) {
        Map<String, String> params = new HashMap<String, String>();
        if (CheckUtils.equalsString(orglevel, "1")) {
            params.put("orglevel", orglevel);
        } else {
            params.put("fatherorgid", fatherorgid);//父组织架构orgid
            params.put("orglevel", orglevel);//组织机构等级（1：第一级 2：第二级 3：第三级 4：第四级）
        }
        OkGo.post(Path.GET_USER_ORG).tag(this).params(params).execute(new DialogCallback<List<GetUserOrg>>(getActivity(), "查询中...") {

            @Override
            public void onSuccess(List<GetUserOrg> getUserOrgs, Call call, Response response) {
                if (getUserOrgs != null && getUserOrgs.size() > 0) {
                    list.clear();
                    list.addAll(getUserOrgs);
                    mAdapter.notifyDataSetChanged();
                    mOrgId = null;
                    if (CheckUtils.equalsString(orglevel, "1")) {
                        shengList = getUserOrgs;
                        flag = "2";
                    } else if (CheckUtils.equalsString(orglevel, "2")) {
                        shiList = getUserOrgs;
                        flag = "3";
                    } else if (CheckUtils.equalsString(orglevel, "3")) {
                        xianList = getUserOrgs;
                        flag = "4";
                    } else if (CheckUtils.equalsString(orglevel, "4")) {
                        quList = getUserOrgs;
                        flag = "5";
                    } else if (CheckUtils.equalsString(orglevel, "5")) {
                        jieList = getUserOrgs;
                        flag = "6";
                    }
                    initView(orglevel);
                } else {
                    mOrgId = fatherorgid;
                }
            }

        });

    }

    private void setRecyclerView() {
        mAdapter = new AddressSelectAdapter(getActivity(), list);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new ListLineDecoration());
        mRecyclerView.setOnItemClickListener(new HeaderAndFooterRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, int position) {
                List<GetUserOrg> list1 = new ArrayList<GetUserOrg>();
                for (int i = 0; i < list.size(); i++) {
                    GetUserOrg getUserOrg = list.get(i);
                    if (i == position) {
                        getUserOrg.setSelectPosition(position + "");
                    } else {
                        getUserOrg.setSelectPosition(null);
                    }
                    list1.add(getUserOrg);
                }
                list.clear();
                list.addAll(list1);
                mAdapter.notifyDataSetChanged();
                GetUserOrg getUserOrg = list.get(position);
                setTitleView(getUserOrg.getOrgname());
                if (CheckUtils.equalsString(flag, "6")) {
                    mOrgId = getUserOrg.getOrgid();
                    return;
                }
                getServerOrg(getUserOrg.getOrgid(), flag);
            }
        });
    }

    //设置选中的标题
    private void setTitleView(String orgname) {
        if (CheckUtils.equalsString(flag, "2")) {
            mTvSheng.setText(orgname);
            mTvSheng.setTextColor(getResources().getColor(R.color.color_3270ed));
            mViewSheng.setVisibility(View.VISIBLE);
        } else if (CheckUtils.equalsString(flag, "3")) {
            mTvShi.setText(orgname);
            mViewSheng.setVisibility(View.GONE);
            mTvSheng.setTextColor(getResources().getColor(R.color.color_000000));
            mTvShi.setTextColor(getResources().getColor(R.color.color_3270ed));
            mViewShi.setVisibility(View.VISIBLE);
        } else if (CheckUtils.equalsString(flag, "4")) {
            mTvXian.setText(orgname);
            mViewSheng.setVisibility(View.GONE);
            mViewShi.setVisibility(View.GONE);
            mTvShi.setTextColor(getResources().getColor(R.color.color_000000));
            mTvXian.setTextColor(getResources().getColor(R.color.color_3270ed));
            mViewXian.setVisibility(View.VISIBLE);
        } else if (CheckUtils.equalsString(flag, "5")) {
            mTvQu.setText(orgname);
            mViewSheng.setVisibility(View.GONE);
            mViewShi.setVisibility(View.GONE);
            mViewXian.setVisibility(View.GONE);
            mTvXian.setTextColor(getResources().getColor(R.color.color_000000));
            mTvQu.setTextColor(getResources().getColor(R.color.color_3270ed));
            mViewQu.setVisibility(View.VISIBLE);
        } else if (CheckUtils.equalsString(flag, "6")) {
            mTvJie.setText(orgname);
            mViewSheng.setVisibility(View.GONE);
            mViewShi.setVisibility(View.GONE);
            mViewXian.setVisibility(View.GONE);
            mViewQu.setVisibility(View.GONE);
            mTvQu.setTextColor(getResources().getColor(R.color.color_000000));
            mTvJie.setTextColor(getResources().getColor(R.color.color_3270ed));
            mViewJie.setVisibility(View.VISIBLE);
        }
    }


    @OnClick({R.id.tv_confirm, R.id.tv_sheng, R.id.tv_shi, R.id.tv_xian, R.id.tv_qu})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_confirm://确定上传组织架构
                if (mOrgId != null) {
                    updateOrgid();
                } else {
                    XToastUtils.showShortToast("请选择地址");
                }
                break;
            case R.id.tv_sheng:
                String tvSheng = mTvSheng.getText().toString().trim();
                if (CheckUtils.equalsString(tvSheng, "请选择")) {
                    return;
                }
                flag = "2";
                list.clear();
                list.addAll(shengList);
                mAdapter.notifyDataSetChanged();
                setShengView();
                shiList = null;
                xianList = null;
                quList = null;
                jieList = null;
                mOrgId = null;
                break;
            case R.id.tv_shi:
                String tvShi = mTvShi.getText().toString().trim();
                if (CheckUtils.equalsString(tvShi, "请选择")) {
                    return;
                }
                flag = "3";
                list.clear();
                list.addAll(shiList);
                mAdapter.notifyDataSetChanged();
                setShiView();
                xianList = null;
                quList = null;
                jieList = null;
                mOrgId = null;
                break;
            case R.id.tv_xian:
                String tvXian = mTvXian.getText().toString().trim();
                if (CheckUtils.equalsString(tvXian, "请选择")) {
                    return;
                }
                flag = "4";
                list.clear();
                list.addAll(xianList);
                mAdapter.notifyDataSetChanged();
                setXianView();
                quList = null;
                jieList = null;
                mOrgId = null;
                break;
            case R.id.tv_qu:
                String tvQu = mTvQu.getText().toString().trim();
                if (CheckUtils.equalsString(tvQu, "请选择")) {
                    return;
                }
                flag = "5";
                list.clear();
                list.addAll(quList);
                mAdapter.notifyDataSetChanged();
                setQuView();
                jieList = null;
                mOrgId = null;
                break;
        }
    }

    //上传组织架构
    private void updateOrgid() {
        User userLoginInfo = Utils.getUserLoginInfo();
        Map<String, String> params = new HashMap<String, String>();
        params.put("pid", userLoginInfo.getPid());//父组织架构orgid
        params.put("orgid", mOrgId);//组织机构等级（1：第一级 2：第二级 3：第三级 4：第四级）
        OkGo.post(Path.UPDATE_USER_ORGID).tag(this).params(params).execute(new DialogCallback<List<GetUserOrg>>(getActivity(), "上传中...") {

            @Override
            public void onSuccess(List<GetUserOrg> getUserOrgs, Call call, Response response) {
                XToastUtils.showShortToast("上传成功");
                userLoginInfo.setOrgid(mOrgId);
                Utils.putUserLoginInfo(userLoginInfo);
                dismiss();
            }

        });
    }

    private void setShengView() {
        setShiView();
        mViewShi.setVisibility(View.GONE);
        mTvShi.setText("请选择");
        mTvShi.setTextColor(getResources().getColor(R.color.color_000000));
        mTvShi.setVisibility(View.GONE);
        mTvSheng.setTextColor(getResources().getColor(R.color.color_3270ed));
        mViewSheng.setVisibility(View.VISIBLE);
    }

    private void setShiView() {
        setXianView();
        mViewXian.setVisibility(View.GONE);
        mTvXian.setText("请选择");
        mTvXian.setTextColor(getResources().getColor(R.color.color_000000));
        mTvXian.setVisibility(View.GONE);
        mTvShi.setTextColor(getResources().getColor(R.color.color_3270ed));
        mViewShi.setVisibility(View.VISIBLE);
    }

    private void setXianView() {
        setQuView();
        mTvQu.setText("请选择");
        mTvQu.setTextColor(getResources().getColor(R.color.color_000000));
        mTvQu.setVisibility(View.GONE);
        mViewQu.setVisibility(View.GONE);
        mTvXian.setTextColor(getResources().getColor(R.color.color_3270ed));
        mViewXian.setVisibility(View.VISIBLE);
    }

    private void setQuView() {
        mTvJie.setText("请选择");
        mTvJie.setTextColor(getResources().getColor(R.color.color_000000));
        mTvJie.setVisibility(View.GONE);
        mViewJie.setVisibility(View.GONE);
        mTvQu.setTextColor(getResources().getColor(R.color.color_3270ed));
        mViewQu.setVisibility(View.VISIBLE);
    }

}
