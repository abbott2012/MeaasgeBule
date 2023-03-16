package com.guoji.mobile.cocobee.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.common.Constant;
import com.guoji.mobile.cocobee.common.JsonResult;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.callback.StringDialogCallback;
import com.guoji.mobile.cocobee.model.ConveniencePoint;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 便民服务页面
 * Created by _H_JY on 2017/1/16.
 */
public class ConvenienceAct extends BaseAct implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ImageButton back_ib;
    private Button upload_btn;
    private Button search_btn;
    private ListView mListView;
    private ConvenienceAdapter adapter;
    private List<ConveniencePoint> convenienceList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_convenience);

        EventBus.getDefault().register(this);

        initView();


    }


    private void initView() {
        back_ib = (ImageButton) findViewById(R.id.back_ib);
        mListView = (ListView) findViewById(R.id.listView);
        upload_btn = (Button) findViewById(R.id.upload_btn);
        search_btn = (Button) findViewById(R.id.search_btn);


        back_ib.setOnClickListener(this);
        search_btn.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
        mListView.addHeaderView(new ViewStub(this));
        mListView.addFooterView(new ViewStub(this));

        if (user.getApproleid() == Constant.NORMAL_USER) { //普通用户无法上传便民点
            upload_btn.setVisibility(View.GONE);
        } else {
            upload_btn.setVisibility(View.VISIBLE);
            upload_btn.setOnClickListener(this);
        }


        loadData();
    }

    @Subscribe
    public void onEventMainThread(String eventMsg) {
        if (Constant.NEED_REFRESH_CONVENIENCE_LIST.equals(eventMsg)) {
            loadData();
        }
    }


    private void loadData() {

        OkGo.post(Path.GET_CONVENIENCE_POINTS).tag(this).execute(new StringDialogCallback(ConvenienceAct.this, "正在获取数据...") {
            @Override
            public void onSuccess(String result, Call call, Response response) {
                if (!TextUtils.isEmpty(result)) {
                    Gson gson = new Gson();
                    JsonResult jr = gson.fromJson(result, new TypeToken<JsonResult>() {
                    }.getType());
                    if (jr != null && jr.isFlag() && jr.getStatusCode() == 200) {
                        result = jr.getResult();
                        if (!TextUtils.isEmpty(result)) {
                            List<ConveniencePoint> list = gson.fromJson(result, new TypeToken<List<ConveniencePoint>>() {
                            }.getType());
                            if (list != null && list.size() > 0) {
                                convenienceList = list;
                                adapter = new ConvenienceAdapter(ConvenienceAct.this, convenienceList);
                                mListView.setAdapter(adapter);
                                app.setConveniencePoints(convenienceList);
                            } else {
                                Toast.makeText(ConvenienceAct.this, "暂无相关数据", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ConvenienceAct.this, "暂无相关数据", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ConvenienceAct.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ConvenienceAct.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                Toast.makeText(ConvenienceAct.this, "获取数据失败", Toast.LENGTH_SHORT).show();
            }

        });


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_ib:
                finish();
                break;

            case R.id.upload_btn:
                startActivity(new Intent(ConvenienceAct.this, ShowMapAct.class).putExtra("flag", Constant.BMFW));
                break;

            case R.id.search_btn:
                startActivity(new Intent(ConvenienceAct.this, ConvenienceSearchAct.class));
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = new Intent(ConvenienceAct.this, PointMapManageAct.class);
        i.putExtra("flag", Constant.BMFW);
        i.putExtra("currentPosition", position - 1);
        startActivity(i);
    }


    private class ViewHolder {

        private TextView cNameTv;
        private TextView cCoordinatesTv;
        private TextView cCategoryTv;
        private TextView cAddressTv;
        private TextView hideTv;
        private ImageView typeIv;

        public ViewHolder(View view) {
            cNameTv = (TextView) view.findViewById(R.id.recogcode_tv);
            cCoordinatesTv = (TextView) view.findViewById(R.id.coordinates_tv);
            cCategoryTv = (TextView) view.findViewById(R.id.install_status_tv);
            cAddressTv = (TextView) view.findViewById(R.id.address_tv);
            hideTv = (TextView) view.findViewById(R.id.online_status_tv);
            typeIv = (ImageView) view.findViewById(R.id.type_iv);
        }

    }


    private class ConvenienceAdapter extends BaseAdapter {

        private Context context;
        private List<ConveniencePoint> convenienceActList = new ArrayList<>();
        private LayoutInflater inflater;

        public ConvenienceAdapter(Context context, List<ConveniencePoint> convenienceActList) {
            this.context = context;
            this.convenienceActList = convenienceActList;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return convenienceActList.size();
        }

        @Override
        public Object getItem(int position) {
            return convenienceActList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            ConveniencePoint point = convenienceActList.get(position);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.pos_info_lv_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.hideTv.setVisibility(View.GONE);
            viewHolder.cNameTv.setText(Html.fromHtml("便民点名称：<font color='black'>" + point.getName() + "</font>"));
            viewHolder.cCoordinatesTv.setText("经纬度： ( " + point.getLongitude() + " , "
                    + point.getLatitude() + " )");
            viewHolder.cCoordinatesTv.setVisibility(View.GONE);
            switch (point.getCategory()) {
                case "1":
                    viewHolder.cCategoryTv.setText("类别：加油站");
                    viewHolder.typeIv.setVisibility(View.VISIBLE);
                    viewHolder.typeIv.setImageResource(R.drawable.gas);
                    break;
                case "2":
                    viewHolder.cCategoryTv.setText("类别：充电站");
                    viewHolder.typeIv.setVisibility(View.VISIBLE);
                    viewHolder.typeIv.setImageResource(R.drawable.charge);
                    break;
                case "3":
                    viewHolder.cCategoryTv.setText("类别：维修站");
                    viewHolder.typeIv.setVisibility(View.VISIBLE);
                    viewHolder.typeIv.setImageResource(R.drawable.repair);
                    break;
                default:
                    viewHolder.typeIv.setVisibility(View.GONE);
                    viewHolder.cCategoryTv.setText("类别：未定义");
                    break;

            }

            viewHolder.cAddressTv.setText("地址：" + point.getAddress());

            return convertView;
        }
    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        OkGo.getInstance().cancelTag(this);
        super.onDestroy();
    }
}
