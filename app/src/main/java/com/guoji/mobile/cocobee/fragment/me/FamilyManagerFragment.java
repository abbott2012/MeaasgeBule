package com.guoji.mobile.cocobee.fragment.me;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bql.recyclerview.swipe.SwipeMenuAdapter;
import com.bql.recyclerview.swipe.SwipeMenuCreator;
import com.guoji.mobile.cocobee.adapter.FamilyAdapter;
import com.guoji.mobile.cocobee.callback.LoadingViewCallback;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.fragment.base.SwipeRefreshAndLoadFragment;
import com.guoji.mobile.cocobee.response.FamilyResponse;
import com.guoji.mobile.cocobee.response.HomeRecResponse;
import com.guoji.mobile.cocobee.view.ListLineDecoration;
import com.lzy.okgo.OkGo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Response;

/**
 * 管理家庭成员
 * Created by Administrator on 2017/6/29.
 */

public class FamilyManagerFragment extends SwipeRefreshAndLoadFragment<FamilyResponse> {
    private static HomeRecResponse homeRecResponse;
    FamilyAdapter mFamilyAdapter;

    public static FamilyManagerFragment getInstance(HomeRecResponse homeRecResponse) {
        FamilyManagerFragment.homeRecResponse = homeRecResponse;
        FamilyManagerFragment fragment = new FamilyManagerFragment();
        return fragment;
    }

    @Override
    public void onRcvItemClick(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public SwipeMenuAdapter getAdapter() {
        return mFamilyAdapter;
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
    public SwipeMenuCreator getSwipeMenuCreator() {
        return null;
    }

    @Override
    public void onLeftSwipeMenuClick(int adapterPosition, int menuPosition) {

    }

    @Override
    public void onRightSwipeMenuClick(int adapterPosition, int menuPosition) {
    }

    @Override
    public void onRefresh() {
        loadDataList(1, true);
    }

    @Override
    public void loadDataList(final int curPage, final boolean isPullToRefresh) {
        mCurPage = curPage;
        Map<String, String> params = new HashMap<String, String>();
        params.put("peid",homeRecResponse.getPeid());

        OkGo.post(Path.CHECK_ALL_FAMILY).params(params).execute(new RefreshAndLoadCallback<List<FamilyResponse>>(_mActivity, isPullToRefresh) {
            @Override
            public void errorLeftOrEmptyBtnClick(View v) {
                loadDataList(1, false);
            }

            @Override
            public void onResultSuccess(List<FamilyResponse> familyResponses, @Nullable Response response, LoadingViewCallback callback) {
                handleRefreshAndLoadListData(curPage, callback, familyResponses);
            }

        });
    }


    @Override
    public void onLoadMore() {
        loadDataList(mCurPage, true);
    }

    @Override
    protected void initToolbarHere() {
        initToolbar("成员管理");
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        mFamilyAdapter = new FamilyAdapter(_mActivity, mList);
    }
}