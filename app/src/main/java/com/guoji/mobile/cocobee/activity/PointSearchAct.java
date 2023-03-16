package com.guoji.mobile.cocobee.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.common.Constant;
import com.guoji.mobile.cocobee.model.Point;
import com.guoji.mobile.cocobee.view.PointSearchView;

import java.util.ArrayList;
import java.util.List;

/**
 * 点位搜索页面
 * Created by _H_JY on 2016/11/14.
 */
public class PointSearchAct extends BaseAct implements View.OnClickListener, AdapterView.OnItemClickListener {

    private Context context;
    private ImageButton back_ib;
    private PointSearchView<Point> pointSearchView;
    private ListView mListView;
    private SearchPointAdapter adapter;
    private List<Point> points = new ArrayList<>();
    private List<Point> copyPoints = new ArrayList<>();
    private int realPos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_point_search);
        context = this;

        points.addAll(app.getPoints());
        copyPoints = app.getPoints();


        initView();
    }

    private void initView() {

        back_ib = (ImageButton) findViewById(R.id.back_ib);
        mListView = (ListView) findViewById(R.id.listView);
        pointSearchView = (PointSearchView) findViewById(R.id.searchView);



        pointSearchView.setHintString("识别码、状态、地址关键词");



        back_ib.setOnClickListener(this);

        adapter = new SearchPointAdapter(this, points);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);

        mListView.addHeaderView(new ViewStub(this));
        mListView.addFooterView(new ViewStub(this));

        //设置数据源
        pointSearchView.setDatas(points);

        //设置适配器
        pointSearchView.setAdapter(adapter);

        pointSearchView.setSearchDataListener(new PointSearchView.SearchDatas<Point>() {
            @Override
            public List<Point> filterDatas(List<Point> datas, List<Point> filterdatas, String inputstr) {
                for (int i = 0; i < datas.size(); i++) {
                    Point point = datas.get(i);
                    String installStateStr = "";
                    String onlineStateStr = "";
                    if (point.getPoinstall() != null && point.getPoinstall().equals("1")) {
                        installStateStr = "已安装";
                    } else {
                        installStateStr = "未安装";
                    }
                    if (point.getIsconn() != null && point.getIsconn().equals("1")) {
                        onlineStateStr = "在线";
                    } else {
                        onlineStateStr = "不在线";
                    }

                    if (point.getIdentitycode().contains(inputstr) || point.getAddress().contains(inputstr) || installStateStr.contains(inputstr) || onlineStateStr.startsWith(inputstr)) {
                        filterdatas.add(datas.get(i));
                    }
                }
                return filterdatas;
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_ib:
                finish();
                break;

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position >= 1){
            final Point point = points.get(position-1);

            for (int i = 0; i < copyPoints.size(); i++) {
                if (copyPoints.get(i).getPoid().equals(point.getPoid())) {
                    realPos = i;
                }
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setItems(new String[]{"前往安装调试", "修改点位数据"}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    switch (i) {
                        case 0: //安装调试
                            startActivity(new Intent(context, DeviceDebugAct.class).putExtra("code", point.getIdentitycode()).putExtra("poid", point.getPoid()).putExtra("flag", Constant.DWGL));
                            break;

                        case 1: //修改位置信息：110识别码
                            updatePointData(realPos);
                            break;
                    }
                }
            });

            builder.create().show();
        }
    }

    private void updatePointData(int position) {

        Intent i = new Intent(context, PointMapManageAct.class);
      /*  Bundle bundle = new Bundle();
        bundle.putInt("currentPosition",position);
        bundle.putSerializable("points", (Serializable) infos);
        i.putExtras(bundle);*/
        //i.putExtra("points", (Serializable) infos);
        i.putExtra("flag",Constant.DWGL);
        i.putExtra("currentPosition", position);
        startActivity(i);

    }


    private class ViewHolder {

        private TextView recogcode_tv;
        private TextView address_tv;
        private TextView coordinates_tv;
        private TextView install_status_tv;
        private TextView online_status_tv;


        public ViewHolder(View view) {
            recogcode_tv = (TextView) view.findViewById(R.id.recogcode_tv);
            address_tv = (TextView) view.findViewById(R.id.address_tv);
            coordinates_tv = (TextView) view.findViewById(R.id.coordinates_tv);
            install_status_tv = (TextView) view.findViewById(R.id.install_status_tv);
            online_status_tv = (TextView) view.findViewById(R.id.online_status_tv);
        }


    }


    public class SearchPointAdapter extends BaseAdapter {

        private Context context;
        private List<Point> searchResPoints = new ArrayList<>();
        private LayoutInflater layoutInflater;

        public SearchPointAdapter(Context context, List<Point> searchResPoints) {
            this.context = context;
            this.searchResPoints = searchResPoints;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return searchResPoints.size();
        }

        @Override
        public Object getItem(int position) {
            return searchResPoints.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            Point point = searchResPoints.get(position);
            ViewHolder viewHolder;
            if (view == null) {
                view = layoutInflater.inflate(R.layout.pos_info_lv_item, null);
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }


            viewHolder.recogcode_tv.setText(Html.fromHtml("110识别码：<font color='blue'>" + point.getIdentitycode() + "</font>"));
            viewHolder.address_tv.setText("地址：" + point.getAddress());
            viewHolder.coordinates_tv.setText("经纬度： ( " + point.getLongitude() + " , "
                    + point.getLatitude() + " )");

            String pointInstallStatus = point.getPoinstall();
            if (pointInstallStatus != null && pointInstallStatus.equals("1")) {
                viewHolder.install_status_tv.setText(Html.fromHtml("安装状态：<font color='#136006'>已安装</font>"));
                viewHolder.online_status_tv.setVisibility(View.VISIBLE);
                String isOnlineStr = point.getIsconn();
                if (isOnlineStr != null && isOnlineStr.equals("1")) {
                    viewHolder.online_status_tv.setText(Html.fromHtml("在线状态：<font color='#136006'>在线</font>"));
                } else {
                    viewHolder.online_status_tv.setText(Html.fromHtml("在线状态：<font color='red'>不在线</font>"));
                }
            } else {
                viewHolder.install_status_tv.setText(Html.fromHtml("安装状态：<font color='red'>未安装</font>"));
                viewHolder.online_status_tv.setVisibility(View.GONE);
            }

            return view;
        }
    }

}
