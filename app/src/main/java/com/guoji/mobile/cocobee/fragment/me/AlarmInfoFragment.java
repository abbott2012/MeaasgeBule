package com.guoji.mobile.cocobee.fragment.me;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bql.recyclerview.swipe.SwipeMenuAdapter;
import com.bql.recyclerview.swipe.SwipeMenuCreator;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.adapter.AlarmInfoAdapter;
import com.guoji.mobile.cocobee.callback.LoadingViewCallback;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.fragment.base.SwipeRefreshAndLoadFragment;
import com.guoji.mobile.cocobee.model.User;
import com.guoji.mobile.cocobee.response.AlarmInfoResponse;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.view.ListLineDecoration;
import com.lzy.okgo.OkGo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Response;


/**
 * Created by liu on 2016/10/26.
 * 消息
 */
public class AlarmInfoFragment extends SwipeRefreshAndLoadFragment<AlarmInfoResponse> {
    AlarmInfoAdapter mAlarmInfoAdapter;
    private User mUserLoginInfo;

    public static AlarmInfoFragment getInstance() {
        AlarmInfoFragment fragment = new AlarmInfoFragment();
        return fragment;
    }

    @Override
    public void onRcvItemClick(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_message;
    }

    @Override
    public SwipeMenuAdapter getAdapter() {
        return mAlarmInfoAdapter;
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
        params.put("pid", mUserLoginInfo.getPid());//用户主键pid

        OkGo.post(Path.GET_USER_ALARM_INFO).params(params).execute(new RefreshAndLoadCallback<List<AlarmInfoResponse>>(_mActivity, isPullToRefresh) {
            @Override
            public void errorLeftOrEmptyBtnClick(View v) {
                loadDataList(1, false);
            }

            @Override
            public void onResultSuccess(List<AlarmInfoResponse> alarmInfoResponses, @Nullable Response response, LoadingViewCallback callback) {
                if (alarmInfoResponses != null) {
                    handleRefreshAndLoadListData(curPage, callback, alarmInfoResponses);
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
        initToolbar("报警记录");
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        mAlarmInfoAdapter = new AlarmInfoAdapter(_mActivity, mList);
        mUserLoginInfo = Utils.getUserLoginInfo();
    }

}