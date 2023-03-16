package com.guoji.mobile.cocobee.fragment.manager;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bql.baseadapter.recycleView.BH;
import com.bql.baseadapter.recycleView.QuickRcvAdapter;
import com.bql.pulltorefreshandloadmore.loadmoreview.LoadMoreRecyclerView;
import com.bql.utils.CheckUtils;
import com.bql.utils.EventManager;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.adapter.SelectAddressAdapter;
import com.guoji.mobile.cocobee.adapter.SelectAddressOrgAdapter;
import com.guoji.mobile.cocobee.callback.DialogCallback;
import com.guoji.mobile.cocobee.common.AppConstants;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.fragment.base.BaseFragment;
import com.guoji.mobile.cocobee.model.User;
import com.guoji.mobile.cocobee.response.SelectAddressResponse;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.utils.XToastUtils;
import com.guoji.mobile.cocobee.view.ListLineDecoration;
import com.guoji.mobile.cocobee.view.ListNoDecoration;
import com.lzy.okgo.OkGo;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/8/21.
 */

public class AddressSelectFragment extends BaseFragment {

    @BindView(R.id.recycle_view)
    LoadMoreRecyclerView mRecycleView;
    private SelectAddressAdapter mSelectAddressAdapter;
    private User mUserLoginInfo;
    private View mView;
    private SelectAddressResponse mSelectAddressResponse;
    private ArrayList<SelectAddressResponse> mList = new ArrayList();


    public static AddressSelectFragment getInstance() {
        return new AddressSelectFragment();
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        mUserLoginInfo = Utils.getUserLoginInfo();
        initRecycleView();
        initFootView();
    }

    //初始化recycleView
    private void initRecycleView() {
        mSelectAddressAdapter = new SelectAddressAdapter(_mActivity, mList);
        mRecycleView.setLayoutManager(new LinearLayoutManager(_mActivity));
        mRecycleView.addItemDecoration(new ListNoDecoration());
        mRecycleView.setAdapter(mSelectAddressAdapter);
        mSelectAddressAdapter.setOnItemClickListener(new QuickRcvAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(BH viewHolder, int position) {
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
        });
    }

    @Override
    protected void initToolbarHere() {
        initToolbarWithRightText("选择组织机构", "确定");
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_address_select;
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
                EventBus.getDefault().post(new EventManager(AppConstants.ORG_SET_SUCCESS));
                _mActivity.finish();
            }
        });
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

        mRecycleView.addFooterView(mView);
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
