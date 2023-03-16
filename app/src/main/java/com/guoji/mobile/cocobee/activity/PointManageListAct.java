package com.guoji.mobile.cocobee.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.common.Constant;
import com.guoji.mobile.cocobee.common.JsonResult;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.callback.StringDialogCallback;
import com.guoji.mobile.cocobee.model.Point;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 点位管理页面
 * Created by _H_JY on 16/11/9.
 */
public class PointManageListAct extends BaseAct implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ImageButton back_ib;
    private Button search_point_btn;
    private ListView mListView;
    private View headerView;
    private TextView size_tv;  //展示标签记录数
    private List<Point> infos = new ArrayList<>();
    private PosInfoAdapter adapter;
    private Context context;
    private int refresh_flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_posmanage);
        EventBus.getDefault().register(this);
        context = this;
        initView();
    }


    private void initView() {

        back_ib = (ImageButton) findViewById(R.id.back_ib);
        mListView = (ListView) findViewById(R.id.listView);
        search_point_btn = (Button) findViewById(R.id.search_point_btn);

        headerView = LayoutInflater.from(context).inflate(R.layout.lv_header, null);
        mListView.addHeaderView(headerView);
        size_tv = (TextView) headerView.findViewById(R.id.record_num_tv);

        mListView.setOnItemClickListener(this);

        search_point_btn.setOnClickListener(this);
        back_ib.setOnClickListener(this);


        loadData();
    }




    @Subscribe
    public void onEventMainThread(String eventMsg) {
        if (eventMsg != null && eventMsg.equals(Constant.NEED_REFRESH_POINTLIST)) {
            refresh_flag = 2;
            loadData();
        }
    }

    private void loadData() {

        String title = "";
        if (refresh_flag == 2) {
            title = "正在更新点位数据...";
        } else {
            title = "正在加载点位数据...";
        }


        Map<String, String> params = new HashMap<String, String>();
        params.put("orgid", user.getOrgid());
        params.put("orgids", user.getOrgids());

        OkGo.post(Path.POS_INFO_PATH).tag(this).params(params).execute(new StringDialogCallback(PointManageListAct.this, title) {
            @Override
            public void onSuccess(String result, Call call, Response response) {
                if (!TextUtils.isEmpty(result)) {
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                    JsonResult jr = gson.fromJson(result, new TypeToken<JsonResult>() {
                    }.getType());
                    if (jr != null && jr.isFlag() && jr.getStatusCode() == 200) {
                        String jsonStr = jr.getResult();
                        if (!TextUtils.isEmpty(jsonStr)) {
                            infos = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create().fromJson(jsonStr, new TypeToken<List<Point>>() {
                            }.getType());
                            if (infos != null && infos.size() > 0) {
                                adapter = new PosInfoAdapter(context, infos);
                                mListView.setAdapter(adapter);
                                if (refresh_flag == 2) {
                                    //Toast.makeText(context, "更新点位数据列表成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    //Toast.makeText(context, "获取点位数据列表成功", Toast.LENGTH_SHORT).show();
                                }

                                size_tv.setText("点位总数: " + infos.size());

                                search_point_btn.setVisibility(View.VISIBLE);
                                app.setPoints(infos);

                            } else {
                                search_point_btn.setVisibility(View.GONE);
                                app.setPoints(null);
                                Toast.makeText(context, "暂无相关数据", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            search_point_btn.setVisibility(View.GONE);
                            app.setPoints(null);
                            if (refresh_flag == 2) {
                                Toast.makeText(context, "更新点位数据列表失败", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "获取点位数据列表失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        search_point_btn.setVisibility(View.GONE);
                        app.setPoints(null);
                        if (refresh_flag == 2) {
                            Toast.makeText(context, "更新点位数据列表失败", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "获取点位数据列表失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    search_point_btn.setVisibility(View.GONE);
                    app.setPoints(null);
                    if (refresh_flag == 2) {
                        Toast.makeText(context, "更新点位数据列表失败", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "获取点位数据列表失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                search_point_btn.setVisibility(View.GONE);
                app.setPoints(null);
                if (refresh_flag == 2) {
                    Toast.makeText(context, "更新点位数据列表失败", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "获取点位数据列表失败", Toast.LENGTH_SHORT).show();
                }
            }

        });



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.back_ib:
                finish();
                break;

            case R.id.search_point_btn:
                startActivity(new Intent(context, PointSearchAct.class));
                break;


        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
        if (position >= 1) {
            final Point point = infos.get(position - 1);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setItems(new String[]{"前往安装调试", "修改点位数据"}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    switch (i) {
                        case 0: //安装调试
                            startActivity(new Intent(context, DeviceDebugAct.class).putExtra("code", point.getIdentitycode()).putExtra("poid", point.getPoid()).putExtra("flag", Constant.DWGL));
                            break;

                        case 1: //修改位置信息：110识别码
                            updatePointData(position - 1);
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
        i.putExtra("flag", Constant.DWGL);
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


    public class PosInfoAdapter extends BaseAdapter {

        private Context context;
        private List<Point> points = new ArrayList<>();
        private LayoutInflater layoutInflater;

        public PosInfoAdapter(Context context, List<Point> alarmInfos) {
            this.context = context;
            this.points = alarmInfos;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return points.size();
        }

        @Override
        public Object getItem(int position) {
            return points.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            Point point = points.get(position);
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


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        OkGo.getInstance().cancelTag(this);
        super.onDestroy();
    }
}
