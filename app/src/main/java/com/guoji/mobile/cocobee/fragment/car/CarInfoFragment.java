package com.guoji.mobile.cocobee.fragment.car;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bql.baseadapter.recycleView.QuickRcvAdapter;
import com.bql.utils.EventManager;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.me.car.CarBindActivity;
import com.guoji.mobile.cocobee.activity.me.car.CarDetailInfoActivity;
import com.guoji.mobile.cocobee.adapter.CarInfoAdapter;
import com.guoji.mobile.cocobee.callback.LoadingViewCallback;
import com.guoji.mobile.cocobee.common.AppConstants;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.fragment.base.RefreshAndLoadFragment;
import com.guoji.mobile.cocobee.model.User;
import com.guoji.mobile.cocobee.response.CarInfoResponse;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.view.ListLineDecoration;
import com.lzy.okgo.OkGo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Response;


/**
 * Created by liu on 2016/10/26.
 * 消息
 */
public class CarInfoFragment extends RefreshAndLoadFragment<CarInfoResponse> {

    @BindView(R.id.ll_left_container)
    ImageView mLlLeftContainer;
    @BindView(R.id.iv_add_car)
    ImageView mIvAddCar;
    private CarInfoAdapter mCarInfoAdapter;
    private User mUserLoginInfo;

    public static CarInfoFragment getInstance() {
        CarInfoFragment fragment = new CarInfoFragment();
        return fragment;
    }

    @Override
    public void onRcvItemClick(RecyclerView.ViewHolder holder, int position) {
        CarInfoResponse carInfoResponse = mList.get(position);
        Intent intent = new Intent(_mActivity, CarDetailInfoActivity.class);
        intent.putExtra("carInfo", carInfoResponse);
        startActivity(intent);
    }

    @Override
    public QuickRcvAdapter<CarInfoResponse> getAdapter() {
        return mCarInfoAdapter;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_car_info;
    }


    @Override
    public RecyclerView.ItemDecoration getItemDecoration() {
        return new ListLineDecoration();
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(_mActivity);
    }

    @Override
    public void onRefresh() {
        loadDataList(1, true);
    }

    @Override
    public void loadDataList(final int curPage, final boolean isPullToRefresh) {
        mCurPage = curPage;
        //车辆信息查询
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", mUserLoginInfo.getUsername());
        params.put("orgids", mUserLoginInfo.getOrgids());

        OkGo.post(Path.CAR_INFO_QUEREY).tag(this).params(params).execute(new RefreshAndLoadCallback<List<CarInfoResponse>>(isPullToRefresh) {

            @Override
            public void errorLeftOrEmptyBtnClick(View v) {
                loadDataList(1, false);
            }

            @Override
            public void onResultSuccess(List<CarInfoResponse> carInfoResponses, @Nullable Response response, LoadingViewCallback callback) {
                if (carInfoResponses != null) {
                    handleRefreshAndLoadListData(curPage, callback, carInfoResponses);
                }
            }

        });
    }


    @Override
    public void onLoadMore() {
        loadDataList(mCurPage, true);
    }

    @Override
    protected void initToolbarHere() {
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        mCarInfoAdapter = new CarInfoAdapter(_mActivity, mList);
        mUserLoginInfo = Utils.getUserLoginInfo();
    }


    @OnClick({R.id.ll_left_container, R.id.iv_add_car})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_left_container:
                _mActivity.finish();
                break;
            case R.id.iv_add_car:
                startActivity(new Intent(_mActivity, CarBindActivity.class));
                break;

        }
    }

    @Override
    public boolean registerEventBus() {
        return true;
    }

    @Override
    protected void onHandleEvent(EventManager eventManager) {
        super.onHandleEvent(eventManager);
        switch (eventManager.getEventCode()) {
            case AppConstants.LABEL_BIND_SUCCESS://标签绑定成功
            case AppConstants.CAR_INFO_UPDATE_SUCCESS://车辆信息录入成功
            case AppConstants.BUY_POLICY_SUCCESS://购买保险成功
            case AppConstants.PAY_FINISH://支付完成
                loadDataList(1, true);
                break;
        }
    }
}