package com.guoji.mobile.cocobee.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bql.recyclerview.swipe.SwipeMenu;
import com.bql.recyclerview.swipe.SwipeMenuAdapter;
import com.bql.recyclerview.swipe.SwipeMenuCreator;
import com.bql.recyclerview.swipe.SwipeMenuItem;
import com.guoji.mobile.cocobee.activity.ShowJPushMsgAct;
import com.guoji.mobile.cocobee.adapter.MessageAdapter;
import com.guoji.mobile.cocobee.callback.DialogCallback;
import com.guoji.mobile.cocobee.callback.LoadingViewCallback;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.fragment.base.SwipeRefreshAndLoadFragment;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.model.User;
import com.guoji.mobile.cocobee.response.MsgResponse;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.utils.XToastUtils;
import com.guoji.mobile.cocobee.view.ListLineDecoration;
import com.lzy.okgo.OkGo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Response;


/**
 * Created by liu on 2016/10/26.
 * 消息
 */
public class MessageFragment extends SwipeRefreshAndLoadFragment<MsgResponse> {
    MessageAdapter mMessageAdapter;
    private User mUserLoginInfo;

    public static MessageFragment getInstance() {
        MessageFragment fragment = new MessageFragment();
        return fragment;
    }

    @Override
    public void onRcvItemClick(RecyclerView.ViewHolder holder, int position) {
        //打开自定义的Activity
        Intent i = new Intent(_mActivity, ShowJPushMsgAct.class);
        Bundle bundle = new Bundle();
        bundle.putString(JPushInterface.EXTRA_NOTIFICATION_TITLE, mList.get(position).getPost_type());
        bundle.putString(JPushInterface.EXTRA_ALERT, mList.get(position).getPost_content());
        i.putExtras(bundle);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_message;
    }

    @Override
    public SwipeMenuAdapter getAdapter() {
        return mMessageAdapter;
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
        return new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
                SwipeMenuItem delItem = new SwipeMenuItem(_mActivity).setBackgroundColor(getResources().getColor(R.color.color_f91c4c))
                        .setText("删除")
                        .setTextColor(Color.WHITE)
                        .setWidth(getResources().getDimensionPixelSize(R.dimen.size70));
                swipeRightMenu.addMenuItem(delItem);
            }
        };
    }

    @Override
    public void onLeftSwipeMenuClick(int adapterPosition, int menuPosition) {

    }

    @Override
    public void onRightSwipeMenuClick(int adapterPosition, int menuPosition) {
        showDelDialog(adapterPosition);
    }

    /**
     * 删除提示框
     *
     * @param adapterPosition
     */
    private void showDelDialog(final int adapterPosition) {
        SweetAlertDialog continueDialog = new SweetAlertDialog(_mActivity, SweetAlertDialog.WARNING_TYPE);
        continueDialog.setTitleText("确定删除该消息吗");
        continueDialog.showCancelButton(true).setCancelText("否");
        continueDialog.setConfirmText("是");
        continueDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismiss();
                deletMsg(adapterPosition);
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

    private void deletMsg(final int adapterPosition) {
        MsgResponse msgResponse = mList.get(adapterPosition);
        Map<String, String> params = new HashMap<String, String>();
        params.put("post_id", msgResponse.getPost_id());//用户主键pid
        OkGo.post(Path.DELETE_SYS_MSG).params(params).execute(new DialogCallback<Object>(_mActivity, "消息删除中...") {
            @Override
            public void onSuccess(Object o, Call call, Response response) {
                XToastUtils.showLongToast("消息删除成功");
                mList.remove(adapterPosition);
                mMessageAdapter.notifyItemRemoved(adapterPosition);
            }
        });
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

        OkGo.post(Path.GET_SYS_MSG).params(params).execute(new RefreshAndLoadCallback<List<MsgResponse>>(_mActivity, isPullToRefresh) {
            @Override
            public void errorLeftOrEmptyBtnClick(View v) {
                loadDataList(1, false);
            }

            @Override
            public void onResultSuccess(List<MsgResponse> msgResponses, @Nullable Response response, LoadingViewCallback callback) {
                handleRefreshAndLoadListData(curPage, callback, msgResponses);
            }

        });
    }


    @Override
    public void onLoadMore() {
        loadDataList(mCurPage, true);
    }

    @Override
    protected void initToolbarHere() {
        initToolbar("消息");
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        mMessageAdapter = new MessageAdapter(_mActivity, mList);
        mUserLoginInfo = Utils.getUserLoginInfo();
    }

}