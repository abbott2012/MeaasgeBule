package com.guoji.mobile.cocobee.fragment;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bql.utils.EventManager;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.AssetsSearchTagActivity;
import com.guoji.mobile.cocobee.activity.ConnectAct;
import com.guoji.mobile.cocobee.activity.DeviceDebugAct;
import com.guoji.mobile.cocobee.activity.MainActivity1;
import com.guoji.mobile.cocobee.activity.SearchTagAct;
import com.guoji.mobile.cocobee.activity.SearchTagActivity;
import com.guoji.mobile.cocobee.activity.SearchTagTempActivity;
import com.guoji.mobile.cocobee.adapter.QueryAdapter;
import com.guoji.mobile.cocobee.common.AppConstants;
import com.guoji.mobile.cocobee.fragment.base.BaseFragment;
import com.guoji.mobile.cocobee.response.QueryRecResponse;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.utils.XToastUtils;
import com.guoji.mobile.cocobee.view.ListLineDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * 查询fragment
 * Created by Administrator on 2017/4/17.
 */

public class QueryFragment extends BaseFragment implements QueryAdapter.OnAdapterClick {

    @BindView(R.id.recycle_view)
    RecyclerView mRecycleView;

    //第一层RecycleView的list
    private List<QueryRecResponse> mList = new ArrayList();
    String[] titles;
    List<List<QueryRecResponse.QueryResponse>> mLists = new ArrayList<>();
    int[] images;

    String[] texts;
    //实时查询的list
    private List<QueryRecResponse.QueryResponse> mList1;
    private List<QueryRecResponse.QueryResponse> mList2;


    public static QueryFragment getInstance() {
        QueryFragment queryFragment = new QueryFragment();
        return queryFragment;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
    }

    @Override
    protected void initToolbarHere() {
//        initToolbarWithRightDrawable(getString(R.string.manager), R.drawable.top_language);
        initToolbar(getString(R.string.manager));
        initView();
    }

    private void initView() {
        surePic();
        for (int i = 0; i < titles.length; i++) {
            QueryRecResponse queryRecResponse = new QueryRecResponse();
            queryRecResponse.setTitle(titles[i]);
            queryRecResponse.setQueryResponseList(mLists.get(i));
            mList.add(queryRecResponse);
        }

        QueryAdapter adapter = new QueryAdapter(_mActivity, mList);
        mRecycleView.setLayoutManager(new LinearLayoutManager(_mActivity));
        mRecycleView.addItemDecoration(new ListLineDecoration());
        mRecycleView.setAdapter(adapter);
        adapter.setOnAdapterClick(this);
    }

    @Override
    public boolean inVisibleLeftDrawable() {
        return true;
    }

    private void surePic() {

//        titles = new String[]{getResources().getString(R.string.technical), getString(R.string.search)};

        titles = new String[]{getResources().getString(R.string.technical)};

        mList1 = new ArrayList<>();
        images = new int[]{R.drawable.connection, R.drawable.debug};
        texts = new String[]{getResources().getString(R.string.device_connection), getResources().getString(R.string.debug)};
        addItems(mList1);

     /*   mList2 = new ArrayList<>();
        images = new int[]{R.drawable.query_b,R.drawable.tag_reading, R.drawable.search, R.drawable.scan};
        texts = new String[]{getResources().getString(R.string.tag_reading),getResources().getString(R.string.tag_reading), getResources().getString(R.string.tag_search), getResources().getString(R.string.tag_discovery)};
        addItems(mList2);*/

        mLists.add(mList1);

//        mLists.add(mList2);

    }

    //为list添加元素
    private void addItems(List<QueryRecResponse.QueryResponse> list) {
        for (int i = 0; i < images.length; i++) {
            QueryRecResponse.QueryResponse queryResponse = new QueryRecResponse.QueryResponse();
            queryResponse.setText(texts[i]);
            queryResponse.setPic(images[i]);
            list.add(queryResponse);
        }
    }


    @Override
    public void btnRightImageClick() {
        View view = View.inflate(_mActivity, R.layout.luanguage_select, null);
        AlertDialog alertDialog = Utils.showCornerDialog(_mActivity, view, 270, 270);
        TextView chinese = (TextView) alertDialog.findViewById(R.id.tv_chinese);
        TextView english = (TextView) alertDialog.findViewById(R.id.tv_english);
        chinese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean userLuanguage = Utils.getUserLuanguage();
                if (userLuanguage) {
                    alertDialog.dismiss();
                    return;
                }
                Utils.setLanguageConfig(_mActivity, true);
                _mActivity.finish();
                Intent intent = new Intent(_mActivity, MainActivity1.class);
                startActivity(intent);
            }
        });
        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean userLuanguage = Utils.getUserLuanguage();
                if (!userLuanguage) {
                    alertDialog.dismiss();
                    return;
                }
                Utils.setLanguageConfig(_mActivity, false);
                _mActivity.finish();
                Intent intent = new Intent(_mActivity, MainActivity1.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_query;
    }


    @Override
    public void onAdapterClick(int position1, int position) {
        // 根据图片进行相应的跳转
        switch (mList.get(position).getQueryResponseList().get(position1).getPic()) {
            case R.drawable.connection:
                //设备连接
                if (ContextCompat.checkSelfPermission(_mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //进入到这里代表没有获取定位权限
                    if (ActivityCompat.shouldShowRequestPermissionRationale(_mActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        //已经禁止提示了
                        new AlertDialog.Builder(_mActivity)
                                .setMessage(getString(R.string.need_permision))
                                .setPositiveButton(getString(R.string.settting), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        intent.setData(Uri.parse("package:" + _mActivity.getPackageName()));
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton(_mActivity.getString(R.string.cancel), null)
                                .create()
                                .show();
                        return;
                    } else {
                        ActivityCompat.requestPermissions(_mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 11);

                    }
                }
                startActivity(new Intent(_mActivity, ConnectAct.class));
                break;
            case R.drawable.debug:
                //安装调试
                startActivity(new Intent(_mActivity, DeviceDebugAct.class));
                break;
            case R.drawable.search://资产查找
                if (ContextCompat.checkSelfPermission(_mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //进入到这里代表没有获取权限
                    if (ActivityCompat.shouldShowRequestPermissionRationale(_mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        //已经禁止提示了
                        XToastUtils.showLongToast("您已禁止定位权限，需要重新开启");
                        return;
                    } else {
                        ActivityCompat.requestPermissions(_mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 22);

                    }
                }
                //                startActivity(new Intent(_mActivity, SearchTagAct.class));
                //                startActivity(new Intent(_mActivity, ReadExcelActivity.class));
                startActivity(new Intent(_mActivity, SearchTagActivity.class));

                break;
            case R.drawable.scan: //资产盘点
                if (ContextCompat.checkSelfPermission(_mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //进入到这里代表没有获取权限
                    if (ActivityCompat.shouldShowRequestPermissionRationale(_mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        //已经禁止提示了
                        XToastUtils.showLongToast("您已禁止定位权限，需要重新开启");
                        return;
                    } else {
                        ActivityCompat.requestPermissions(_mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 22);

                    }
                }
                startActivity(new Intent(_mActivity, AssetsSearchTagActivity.class));
                break;
            case R.drawable.tag_reading://标签搜索
                if (ContextCompat.checkSelfPermission(_mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //进入到这里代表没有获取权限
                    if (ActivityCompat.shouldShowRequestPermissionRationale(_mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        //已经禁止提示了
                        XToastUtils.showShortToast("您已禁止存储权限，需要重新开启");
                        return;
                    } else {
                        ActivityCompat.requestPermissions(_mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 22);

                    }
                }

                startActivity(new Intent(_mActivity, SearchTagAct.class));
                break;
            case R.drawable.query_b: //标签搜索
                if (ContextCompat.checkSelfPermission(_mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //进入到这里代表没有获取权限
                    if (ActivityCompat.shouldShowRequestPermissionRationale(_mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        //已经禁止提示了
                        XToastUtils.showShortToast("您已禁止存储权限，需要重新开启");
                        return;
                    } else {
                        ActivityCompat.requestPermissions(_mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 22);

                    }
                }

                startActivity(new Intent(_mActivity, SearchTagTempActivity.class));
                break;
            default:
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
            case AppConstants.ORG_SET_SUCCESS://设置组织机构成功
                mList.clear();
                mLists.clear();
                initView();
                break;
        }
    }

}
