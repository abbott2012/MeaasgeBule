package com.guoji.mobile.cocobee.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.bql.baseadapter.recycleView.QuickRcvAdapter;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.ConvenienceSearchAct;
import com.guoji.mobile.cocobee.activity.PointMapManageAct;
import com.guoji.mobile.cocobee.activity.ShowMapAct;
import com.guoji.mobile.cocobee.adapter.ConvenienceAdapter;
import com.guoji.mobile.cocobee.callback.LoadingViewCallback;
import com.guoji.mobile.cocobee.common.Constant;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.fragment.base.RefreshAndLoadFragment;
import com.guoji.mobile.cocobee.model.ConveniencePoint;
import com.guoji.mobile.cocobee.model.User;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.view.ListLineDecoration;
import com.lzy.okgo.OkGo;

import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/6/14.
 */

public class ConvenienceFragment extends RefreshAndLoadFragment<ConveniencePoint> {
    @BindView(R.id.back_ib)
    ImageButton back_ib;
    @BindView(R.id.search_btn)
    Button search_btn;
    @BindView(R.id.upload_btn)
    Button upload_btn;
    private ConvenienceAdapter mConvenienceAdapter;

    private List<ConveniencePoint> convenienceList = new ArrayList<>();
    private User user;

    public static ConvenienceFragment getInstance() {
        ConvenienceFragment fragment = new ConvenienceFragment();
        return fragment;
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onRcvItemClick(RecyclerView.ViewHolder holder, int position) {
        Intent i = new Intent(_mActivity, PointMapManageAct.class);
        i.putExtra("flag", Constant.BMFW);
        i.putExtra("currentPosition", position);
        i.putExtra("list", (Serializable) convenienceList);
        startActivity(i);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_convenience;
    }

    @Override
    public QuickRcvAdapter<ConveniencePoint> getAdapter() {
        return mConvenienceAdapter;
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
    public void loadDataList(int curPage, boolean isPullToRefresh) {
        mCurPage = curPage;
        OkGo.post(Path.GET_CONVENIENCE_POINTS).tag(this).execute(new RefreshAndLoadCallback<List<ConveniencePoint>>(isPullToRefresh) {

            @Override
            public void errorLeftOrEmptyBtnClick(View v) {
                loadDataList(1, false);
            }

            @Override
            public void onResultSuccess(List<ConveniencePoint> conveniencePoints, @Nullable Response response, LoadingViewCallback callback) {
                convenienceList = conveniencePoints;
                if (conveniencePoints != null) {
                    handleRefreshAndLoadListData(mCurPage, callback, conveniencePoints);
                }
            }
        });
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        mConvenienceAdapter = new ConvenienceAdapter(_mActivity, mList);
        user = Utils.getUserLoginInfo();
        initView();
    }

    private void initView() {

//        mListView.addHeaderView(new ViewStub(this));
//        mListView.addFooterView(new ViewStub(this));

        if (user.getApproleid() == Constant.NORMAL_USER) { //普通用户无法上传便民点
            upload_btn.setVisibility(View.GONE);
        } else {
            upload_btn.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void initToolbarHere() {

    }

    @Override
    public boolean registerEventBus() {
        return true;
    }


    @Subscribe
    public void onEventMainThread(String eventMsg) {
        if (Constant.NEED_REFRESH_CONVENIENCE_LIST.equals(eventMsg)) {
            loadDataList(1, true);
        }
    }

    @OnClick({R.id.back_ib, R.id.search_btn, R.id.upload_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_ib:
                _mActivity.finish();
                break;

            case R.id.upload_btn:
                startActivity(new Intent(_mActivity, ShowMapAct.class).putExtra("flag", Constant.BMFW));
                break;

            case R.id.search_btn:
                startActivity(new Intent(_mActivity, ConvenienceSearchAct.class));
                break;
        }
    }
}
