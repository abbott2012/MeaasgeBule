package com.guoji.mobile.cocobee.fragment.manager;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bql.baseadapter.recycleView.BH;
import com.bql.baseadapter.recycleView.QuickRcvAdapter;
import com.bql.utils.CheckUtils;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.adapter.SelectAddressAdapter;
import com.guoji.mobile.cocobee.adapter.SelectAddressOrgAdapter;
import com.guoji.mobile.cocobee.callback.DialogCallback;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.fragment.base.RefreshAndLoadFragment;
import com.guoji.mobile.cocobee.model.User;
import com.guoji.mobile.cocobee.response.SelectAddressResponse;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.utils.XToastUtils;
import com.guoji.mobile.cocobee.view.ListLineDecoration;
import com.guoji.mobile.cocobee.view.ListNoDecoration;
import com.lzy.okgo.OkGo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/8/21.
 */

public class AddressSelectFragment1 extends RefreshAndLoadFragment<SelectAddressResponse> {

    private SelectAddressAdapter mSelectAddressAdapter;
    private User mUserLoginInfo;
    private View mView;
    private SelectAddressResponse mSelectAddressResponse;

    public static AddressSelectFragment1 getInstance() {
        return new AddressSelectFragment1();
    }

    @Override
    public void onRcvItemClick(RecyclerView.ViewHolder holder, int position) {

        if (position == 0 && mList.size() == 1) {
            mList.clear();
            mSelectAddressResponse = null;
            mSelectAddressAdapter.notifyDataSetChanged();
            mView.setVisibility(View.VISIBLE);
            getOrg();
        }

        if (position < mList.size() - 1) {
            for (int i = mList.size() - 1; i > position; i--) {
                mList.remove(i);
            }
            mSelectAddressAdapter.notifyDataSetChanged();
            mSelectAddressResponse = null;
            setFootViewVisbel(mList.get(position));
        }
    }

    @Override
    public QuickRcvAdapter<SelectAddressResponse> getAdapter() {
        return mSelectAddressAdapter;
    }


    @Override
    public RecyclerView.ItemDecoration getItemDecoration() {
        return new ListNoDecoration();
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(_mActivity);
//        return new GridLayoutManager(_mActivity, 2);
    }

    @Override
    public void onRefresh() {
    }

    @Override
    public boolean canRefresh() {
        return false;
    }

    @Override
    public void loadDataList(final int curPage, final boolean isPullToRefresh) {
        mCurPage = curPage;
    }

    @Override
    public boolean hideLoadMoreView() {
        return true;
    }

    @Override
    public void onLoadMore() {
        loadDataList(mCurPage, true);
    }

    @Override
    protected void initToolbarHere() {
        initToolbarWithRightText("选择组织机构", "确定");
    }

    @Override
    public void btnRightTextClick() {
        super.btnRightTextClick();
        if (mSelectAddressResponse != null) {
            sureOrg();
        } else {
            XToastUtils.showShortToast("请选择组织机构");
        }
    }

    //确定上传组织机构
    private void sureOrg() {
        Map<String, String> params = new HashMap<>();
        params.put("orgid", mSelectAddressResponse.getOrgid());
        params.put("pid", mUserLoginInfo.getPid());
        OkGo.post(Path.SAVE_SELECT_ORG).tag(this).params(params).execute(new DialogCallback<Object>(_mActivity, "保存组织机构中...") {
            @Override
            public void onSuccess(Object o, Call call, Response response) {
                XToastUtils.showShortToast("组织机构保存成功");
                mUserLoginInfo.setOrgid(mSelectAddressResponse.getOrgid());
                mUserLoginInfo.setOrgname(mSelectAddressResponse.getOrgname());
                Utils.putUserLoginInfo(mUserLoginInfo);
                _mActivity.finish();
            }
        });
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        mSelectAddressAdapter = new SelectAddressAdapter(_mActivity, mList);
        mUserLoginInfo = Utils.getUserLoginInfo();
        initFootView();
    }

    private void initFootView() {
        mView = View.inflate(_mActivity, R.layout.select_address_foot, null);
        TextView tvSelectAddress = (TextView) mView.findViewById(R.id.tv_select_address);
        tvSelectAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOrg();
            }
        });
        mRcvLoadMore.addFooterView(mView);
    }

    private void getOrg() {
        Map<String, String> params = new HashMap<>();
        if (mList == null || mList.size() == 0) {
            params.put("orgid", "520100000000");
        } else {
            params.put("orgid", mList.get(mList.size() - 1).getOrgid());
        }
        OkGo.post(Path.GET_USER_ALL_ORG).tag(this).params(params).execute(new DialogCallback<List<SelectAddressResponse>>(_mActivity, "获取组织机构中...") {
            @Override
            public void onSuccess(List<SelectAddressResponse> selectAddressResponses, Call call, Response response) {
                if (selectAddressResponses != null) {
                    initOrgDialog(selectAddressResponses);
                } else {
                    XToastUtils.showShortToast("暂无相关数据");
                }
            }

        });
    }

    private void initOrgDialog(List<SelectAddressResponse> selectAddressResponses) {
        View view1 = View.inflate(_mActivity, R.layout.label_list, null);
        final AlertDialog alertDialog = Utils.showCornerDialog(_mActivity, view1, 270, 270);
        RecyclerView listView = (RecyclerView) alertDialog.findViewById(R.id.listView);
        TextView tvTitleCarLabel = (TextView) alertDialog.findViewById(R.id.tv_title_car_label);
        tvTitleCarLabel.setText("请选择组织机构");
        SelectAddressOrgAdapter adapter = new SelectAddressOrgAdapter(_mActivity, selectAddressResponses);
        listView.setAdapter(adapter);
        listView.addItemDecoration(new ListLineDecoration());
        listView.setLayoutManager(new LinearLayoutManager(_mActivity));
        adapter.setOnItemClickListener(new QuickRcvAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(BH viewHolder, int position) {
                alertDialog.dismiss();
                SelectAddressResponse selectAddressResponse = selectAddressResponses.get(position);
                mList.add(selectAddressResponse);
                mSelectAddressAdapter.notifyDataSetChanged();
                setFootViewVisbel(selectAddressResponse);
            }
        });
    }

    private void setFootViewVisbel(SelectAddressResponse selectAddressResponse) {
        if (CheckUtils.equalsString(selectAddressResponse.getIs_nextl(), "1")) {
            mView.setVisibility(View.VISIBLE);
        } else if (CheckUtils.equalsString(selectAddressResponse.getIs_nextl(), "0")) {
            mSelectAddressResponse = selectAddressResponse;
            mView.setVisibility(View.GONE);
        }
    }


}
