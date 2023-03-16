package com.guoji.mobile.cocobee.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.model.Policy;
import com.guoji.mobile.cocobee.utils.NumberToCN;
import com.guoji.mobile.cocobee.view.PointSearchView;
import com.guoji.mobile.cocobee.zxing.encode.EncodingHandler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 保单搜索页面
 * Created by _H_JY on 2017/1/19.
 */
public class PolicySearchAct extends BaseAct implements View.OnClickListener,AdapterView.OnItemClickListener {

    private ImageButton back_ib;
    private PointSearchView<Policy> policySearchView;
    private ListView mListView;
    private SearchPolicyInfoAdapter adapter;
    private List<Policy> policyInfos = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_point_search);

        policyInfos.addAll(app.getPolicyInfos());

        initView();
    }

    private void initView() {

        back_ib = (ImageButton) findViewById(R.id.back_ib);
        mListView = (ListView) findViewById(R.id.listView);
        policySearchView = (PointSearchView) findViewById(R.id.searchView);

        mListView.setDividerHeight(0);
        mListView.setBackgroundColor(getResources().getColor(R.color.cadetblue));
        mListView.setOnItemClickListener(this);

        policySearchView.setHintString("姓名、身份证号、保单号等关键词");

        back_ib.setOnClickListener(this);

        adapter = new SearchPolicyInfoAdapter(this, policyInfos);
        mListView.setAdapter(adapter);

        //设置数据源
        policySearchView.setDatas((ArrayList) policyInfos);

        //设置适配器
        policySearchView.setAdapter(adapter);

        policySearchView.setSearchDataListener(new PointSearchView.SearchDatas<Policy>() {
            @Override
            public List<Policy> filterDatas(List<Policy> datas, List<Policy> filterdatas, String inputstr) {
                for (int i = 0; i < datas.size(); i++) {
                    Policy policy = datas.get(i);

                    if (policy.getPname().contains(inputstr) || policy.getIdcard().contains(inputstr) || policy.getMobile().contains(inputstr) || policy.getOrder_id().contains(inputstr)) {
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
        startActivity(new Intent(PolicySearchAct.this,PicLookAct.class));
    }


    private class ViewHolder {

        private TextView policy_id_tv;
        private TextView tag_id_tv;
        private TextView name_tv;
        private TextView phone_tv;
        private TextView idcard_tv;
        private TextView cno_tv;
        private TextView model_tv;
        private TextView date_tv;
        private TextView price_lev_tv;
        private TextView capitcal_damage_tv;
        private TextView damage_tv;
        private TextView capitcal_service_price_tv;
        private TextView service_price_tv;
        private ImageView policy_id_barcode_iv;
        private TextView service_term_tv;

        public ViewHolder(View view) {
            policy_id_tv = (TextView) view.findViewById(R.id.policy_id_tv);
            tag_id_tv = (TextView) view.findViewById(R.id.tag_id_tv);
            name_tv = (TextView) view.findViewById(R.id.name_tv);
            phone_tv = (TextView) view.findViewById(R.id.phone_tv);
            idcard_tv = (TextView) view.findViewById(R.id.idcard_tv);
            cno_tv = (TextView) view.findViewById(R.id.cno_tv);
            model_tv = (TextView) view.findViewById(R.id.model_tv);
            date_tv = (TextView) view.findViewById(R.id.date_tv);
            price_lev_tv = (TextView) view.findViewById(R.id.price_lev_tv);
            capitcal_damage_tv = (TextView) view.findViewById(R.id.capitcal_damage_tv);
            damage_tv = (TextView) view.findViewById(R.id.damage_tv);
            capitcal_service_price_tv = (TextView) view.findViewById(R.id.capitcal_service_price_tv);
            service_price_tv = (TextView) view.findViewById(R.id.service_price_tv);
            policy_id_barcode_iv = (ImageView)view.findViewById(R.id.policy_id_barcode_iv);
            service_term_tv = (TextView)view.findViewById(R.id.service_term_tv);
        }
    }



    private class SearchPolicyInfoAdapter extends BaseAdapter {

        private Context context;
        private List<Policy> policyInfos = new ArrayList<>();
        private LayoutInflater inflater;

        public SearchPolicyInfoAdapter(Context context, List<Policy> policyInfos) {
            this.context = context;
            this.policyInfos = policyInfos;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return policyInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return policyInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            Policy policy = policyInfos.get(position);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.policy_lv_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            //保单号
            String policyId = policy.getOrder_id();
            if (!TextUtils.isEmpty(policyId)) {
                viewHolder.policy_id_barcode_iv.setVisibility(View.VISIBLE);
                Bitmap bitmap = null;
                try {
                    bitmap = EncodingHandler.createBarCode(policyId, 430, 120);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                viewHolder.policy_id_barcode_iv.setImageBitmap(bitmap);
                viewHolder.policy_id_tv.setText(policyId);
            } else {
                viewHolder.policy_id_barcode_iv.setVisibility(View.INVISIBLE);
                viewHolder.policy_id_tv.setText("");
            }

            //电子标签号
            String tagId = policy.getLno();
            if (!TextUtils.isEmpty(tagId)) {
                viewHolder.tag_id_tv.setText(tagId);
            } else {
                viewHolder.tag_id_tv.setText("");
            }


            //车主姓名
            String name = policy.getPname();
            if (!TextUtils.isEmpty(name)) {
                viewHolder.name_tv.setText(name);
            } else {
                viewHolder.name_tv.setText("");
            }


            //车主手机号
            String phone = policy.getMobile();
            if (!TextUtils.isEmpty(phone)) {
                viewHolder.phone_tv.setText(phone);
            } else {
                viewHolder.phone_tv.setText("");
            }
            //车主身份证号
            String idcard = policy.getIdcard();
            if (!TextUtils.isEmpty(idcard)) {
                viewHolder.idcard_tv.setText(idcard);
            } else {
                viewHolder.idcard_tv.setText("");
            }
            //车辆牌号
            String cno = policy.getCno();
            if (!TextUtils.isEmpty(cno)) {
                viewHolder.cno_tv.setText(cno);
            } else {
                viewHolder.cno_tv.setText("");
            }
            //车辆型号
            String model = policy.getCbuytype();
            if(!TextUtils.isEmpty(model)){
                viewHolder.model_tv.setText(model);
            }else {
                viewHolder.model_tv.setText("");
            }


            //车辆录入平台日期
            String date = policy.getCreatetime();
            if (!TextUtils.isEmpty(date)) {
                viewHolder.date_tv.setText(date);
            } else {
                viewHolder.date_tv.setText("");
            }
            //车价档次
            String priceLev = policy.getGrade();
            if (!TextUtils.isEmpty(priceLev)) {
                viewHolder.price_lev_tv.setText(priceLev + "档");
            } else {
                viewHolder.price_lev_tv.setText("");

            }

            //车辆丢失赔偿金
            String damagePrice = policy.getMax_price();
            if (!TextUtils.isEmpty(damagePrice)) {

                String capitalDamagePrice = NumberToCN.number2CNMontrayUnit(new BigDecimal(Double.parseDouble(damagePrice)));
                viewHolder.capitcal_damage_tv.setText(capitalDamagePrice);
                viewHolder.damage_tv.setText(damagePrice);

            } else {
                viewHolder.capitcal_damage_tv.setText("");
                viewHolder.damage_tv.setText("");
            }


            //防盗标签服务费
            String servicePrice = policy.getService_price();
            if (!TextUtils.isEmpty(servicePrice)) {

                String capitalServicePrice = NumberToCN.number2CNMontrayUnit(new BigDecimal(Double.parseDouble(servicePrice)));
                viewHolder.capitcal_service_price_tv.setText(capitalServicePrice);
                viewHolder.service_price_tv.setText(servicePrice);

            } else {
                viewHolder.capitcal_service_price_tv.setText("");
                viewHolder.service_price_tv.setText("");
            }

            String startTime = policy.getPayTime();
            String endTime = policy.getMaturity_date();
            if(!TextUtils.isEmpty(startTime) && !TextUtils.isEmpty(endTime)){
                viewHolder.service_term_tv.setText(startTime+"至"+endTime);
            }else {
                viewHolder.service_term_tv.setText("");
            }


//            String orderStartTime = policy.getPayTime();
//            if(!TextUtils.isEmpty(orderStartTime)){
//                viewHolder.order_starttime_tv.setText(orderStartTime);
//            }else {
//                viewHolder.order_starttime_tv.setText("");
//            }

            return convertView;
        }
    }



}
