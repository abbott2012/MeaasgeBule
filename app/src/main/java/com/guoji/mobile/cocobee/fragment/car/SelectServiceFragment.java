package com.guoji.mobile.cocobee.fragment.car;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bql.baseadapter.recycleView.QuickRcvAdapter;
import com.bql.utils.CheckUtils;
import com.bql.utils.EventManager;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.me.CarInputBaseInfo;
import com.guoji.mobile.cocobee.activity.me.LabelBindingCarAnXinActivity;
import com.guoji.mobile.cocobee.activity.me.LabelBindingPersonAnXinActivity;
import com.guoji.mobile.cocobee.adapter.SelectServiceAdapter;
import com.guoji.mobile.cocobee.callback.DialogCallback;
import com.guoji.mobile.cocobee.callback.LoadingViewCallback;
import com.guoji.mobile.cocobee.common.AppConstants;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.fragment.base.RefreshAndLoadFragment;
import com.guoji.mobile.cocobee.model.User;
import com.guoji.mobile.cocobee.response.CardLabelResponse;
import com.guoji.mobile.cocobee.response.SelectServiceResponse;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.view.ListNoDecoration;
import com.lzy.okgo.OkGo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;


/**
 * Created by liu on 2016/10/26.
 * 选择服务
 */
public class SelectServiceFragment extends RefreshAndLoadFragment<SelectServiceResponse> {

    private static String target_id;
    private SelectServiceAdapter mSelectServiceAdapter;
    private User mUserLoginInfo;

    public static SelectServiceFragment getInstance(String target_id) {
        SelectServiceFragment.target_id = target_id;
        SelectServiceFragment fragment = new SelectServiceFragment();
        return fragment;
    }

    @Override
    public void onRcvItemClick(RecyclerView.ViewHolder holder, int position) {
        SelectServiceResponse selectServiceResponse = mList.get(position);
        Intent intent = null;
        if (CheckUtils.equalsString(selectServiceResponse.getCard_id(), AppConstants.TYPE_CARD_DINZHI) && CheckUtils.equalsString(selectServiceResponse.getTarget_id(), AppConstants.TYPE_CAR)) {//车的定制卡
            if (CheckUtils.isEmpty(mUserLoginInfo.getIdcard())) {
                intent = new Intent(_mActivity, CarInputBaseInfo.class);
            } else {
                intent = new Intent(_mActivity, LabelBindingCarAnXinActivity.class);
            }

        } else if (CheckUtils.equalsString(selectServiceResponse.getCard_id(), AppConstants.TYPE_CARD_DINZHI) && CheckUtils.equalsString(selectServiceResponse.getTarget_id(), AppConstants.TYPE_PEPOLE)) {//人的定制卡
            intent = new Intent(_mActivity, LabelBindingPersonAnXinActivity.class);
        } else {
            getCardPrice(selectServiceResponse);
            return;
        }
        if (intent != null) {
            intent.putExtra("selectServiceResponse", selectServiceResponse);
            startActivity(intent);
        }
    }

    private void getCardPrice(SelectServiceResponse selectServiceResponse) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("orgid", mUserLoginInfo.getOrgid());
        params.put("card_id", selectServiceResponse.getCard_id());
        params.put("target_id", selectServiceResponse.getTarget_id());

        OkGo.post(Path.GET_CAR_LABEL_PRICE).params(params).execute(new DialogCallback<List<CardLabelResponse>>(_mActivity, "获取卡档位中...") {

            @Override
            public void onSuccess(List<CardLabelResponse> cardLabelResponses, Call call, Response response) {
                if (CheckUtils.equalsString(selectServiceResponse.getCard_id(), AppConstants.TYPE_CARD_ANXIN) && CheckUtils.equalsString(selectServiceResponse.getTarget_id(), AppConstants.TYPE_CAR)) {//车的安心卡
                    start(BuyServiceAnxinFragment.getInstance(selectServiceResponse, cardLabelResponses.get(0)));
                } else {//体验卡,人的安心卡
                    start(BuyServiceFragment.getInstance(selectServiceResponse, cardLabelResponses.get(0)));
                }
            }
        });
    }

    @Override
    public QuickRcvAdapter<SelectServiceResponse> getAdapter() {
        return mSelectServiceAdapter;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_select_service;
    }

    @Override
    public RecyclerView.ItemDecoration getItemDecoration() {
        return new ListNoDecoration();
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
        Map<String, String> params = new HashMap<String, String>();
        params.put("orgid", mUserLoginInfo.getOrgid());
        params.put("target_id", target_id);//查车传38,查人传39

        OkGo.post(Path.GET_CARD_INFO).params(params).execute(new RefreshAndLoadCallback<List<SelectServiceResponse>>(isPullToRefresh) {

            @Override
            public void errorLeftOrEmptyBtnClick(View v) {
                loadDataList(1, false);
            }

            @Override
            public void onResultSuccess(List<SelectServiceResponse> selectServiceResponses, @Nullable Response response, LoadingViewCallback callback) {
                if (selectServiceResponses != null) {
                    handleRefreshAndLoadListData(curPage, callback, selectServiceResponses);
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
        initToolbar("选择服务");
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        mSelectServiceAdapter = new SelectServiceAdapter(_mActivity, mList);
        mUserLoginInfo = Utils.getUserLoginInfo();
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
            case AppConstants.PAY_FINISH://支付成功
                _mActivity.finish();
                break;
        }
    }
}