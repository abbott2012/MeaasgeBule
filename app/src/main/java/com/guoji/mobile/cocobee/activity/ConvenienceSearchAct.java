package com.guoji.mobile.cocobee.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.common.Constant;
import com.guoji.mobile.cocobee.model.ConveniencePoint;
import com.guoji.mobile.cocobee.view.PointSearchView;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索便民服务页面
 * Created by _H_JY on 2017/1/19.
 */
public class ConvenienceSearchAct extends BaseAct implements View.OnClickListener ,AdapterView.OnItemClickListener{
    private Context context;
    private ImageButton back_ib;
    private PointSearchView<ConveniencePoint> cSearchView;
    private ListView mListView;
    private SearchCInfoAdapter adapter;
    private EditText search_et;
    private List<ConveniencePoint> cInfos = new ArrayList<>();
    private List<ConveniencePoint> copyCInfos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_point_search);
        context = this;

        cInfos.addAll(app.getConveniencePoints());
        copyCInfos = app.getConveniencePoints();

        initView();
    }

    private void initView() {

        back_ib = (ImageButton) findViewById(R.id.back_ib);
        mListView = (ListView) findViewById(R.id.listView);
        cSearchView = (PointSearchView) findViewById(R.id.searchView);

        cSearchView.setHintString("名称、类别、地址关键词");

        back_ib.setOnClickListener(this);

        adapter = new SearchCInfoAdapter(this, cInfos);
        mListView.setAdapter(adapter);
        mListView.addHeaderView(new ViewStub(this));
        mListView.addFooterView(new ViewStub(this));

        mListView.setOnItemClickListener(this);

        //设置数据源
        cSearchView.setDatas((ArrayList) cInfos);

        //设置适配器
        cSearchView.setAdapter(adapter);

        cSearchView.setSearchDataListener(new PointSearchView.SearchDatas<ConveniencePoint>() {
            @Override
            public List<ConveniencePoint> filterDatas(List<ConveniencePoint> datas, List<ConveniencePoint> filterdatas, String inputstr) {
                for (int i = 0; i < datas.size(); i++) {
                    ConveniencePoint conveniencePoint = datas.get(i);
                    String category = conveniencePoint.getCategory();
                    switch (category){
                        case "1":
                            category = "加油站";
                            break;
                        case "2":
                            category = "充电站";
                            break;
                        case "3":
                            category = "维修站";
                            break;

                    }
                    if (conveniencePoint.getName().contains(inputstr) || category.contains(inputstr) || conveniencePoint.getAddress().contains(inputstr)) {
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
            ConveniencePoint point = cInfos.get(position-1);
            int realPos=0;
            for (int i = 0; i < copyCInfos.size(); i++) {
                if (copyCInfos.get(i).getSid()==(point.getSid())) {
                    realPos = i;
                }
            }
            Intent i = new Intent(ConvenienceSearchAct.this, PointMapManageAct.class);
            i.putExtra("flag", Constant.BMFW);
            i.putExtra("currentPosition", realPos);
            startActivity(i);
        }

    }


    private class ViewHolder{

        private TextView cNameTv;
        private TextView cCoordinatesTv;
        private TextView cCategoryTv;
        private TextView cAddressTv;
        private TextView hideTv;
        private ImageView typeIv;

        public ViewHolder(View view){
            cNameTv = (TextView) view.findViewById(R.id.recogcode_tv);
            cCoordinatesTv = (TextView) view.findViewById(R.id.coordinates_tv);
            cCategoryTv = (TextView) view.findViewById(R.id.install_status_tv);
            cAddressTv = (TextView) view.findViewById(R.id.address_tv);
            hideTv = (TextView) view.findViewById(R.id.online_status_tv);
            typeIv = (ImageView)view.findViewById(R.id.type_iv);
        }

    }





    private class SearchCInfoAdapter extends BaseAdapter{

        private Context context;
        private List<ConveniencePoint> convenienceActList = new ArrayList<>();
        private LayoutInflater inflater;

        public SearchCInfoAdapter(Context context, List<ConveniencePoint> convenienceActList) {
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
            if(convertView == null){
                convertView = inflater.inflate(R.layout.pos_info_lv_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.hideTv.setVisibility(View.GONE);
            viewHolder.cNameTv.setText(Html.fromHtml("便民点名称：<font color='black'>" + point.getName() + "</font>"));
            viewHolder.cCoordinatesTv.setText("经纬度： ( " + point.getLongitude() + " , "
                    + point.getLatitude() + " )");
            switch (point.getCategory()){
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

            viewHolder.cAddressTv.setText("地址："+point.getAddress());

            return convertView;
        }
    }



}
