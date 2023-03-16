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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.pay.BuyPoliceActivity;
import com.guoji.mobile.cocobee.callback.StringDialogCallback;
import com.guoji.mobile.cocobee.common.JsonResult;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.model.Policy;
import com.guoji.mobile.cocobee.utils.NumberToCN;
import com.guoji.mobile.cocobee.zxing.encode.EncodingHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 保单信息页面
 * Created by _H_JY on 2017/1/19.
 */
public class PolicyInfoActivity extends BaseAct implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ImageView back_ib;
    private TextView title_tv;
    private Button search_btn;
    private Button right_btn;
    private ListView mListView;
    private Button buy_policy_btn;
    private PolicyAdapter adapter;
    private List<Policy> policyList = new ArrayList<>();
    private String mLno;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_info);
        initIntent();

        initView();

        loadData();
    }

    private void initIntent() {
        Intent intent = getIntent();
        mLno = intent.getStringExtra("lno");
    }


    private void initView() {
        back_ib = (ImageView) findViewById(R.id.back_ib);
        title_tv = (TextView) findViewById(R.id.title_tv);
        mListView = (ListView) findViewById(R.id.listView);
        search_btn = (Button) findViewById(R.id.search_btn);
        right_btn = (Button) findViewById(R.id.upload_btn);
        buy_policy_btn = (Button) findViewById(R.id.buy_policy_btn);
        buy_policy_btn.setVisibility(View.GONE);


        mListView.setDividerHeight(0);
        mListView.setOnItemClickListener(this);


        title_tv.setText("保单信息");
        back_ib.setOnClickListener(this);
        search_btn.setOnClickListener(this);
        right_btn.setVisibility(View.GONE);
        buy_policy_btn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_ib:
                finish();
                break;

            case R.id.search_btn:
                startActivity(new Intent(PolicyInfoActivity.this, PolicySearchAct.class));
                break;

            case R.id.buy_policy_btn:
                startActivity(new Intent(PolicyInfoActivity.this, BuyPoliceActivity.class).putExtra("from", 2145));
                break;


        }
    }

    private void loadData() {

        if (user == null) {
            Toast.makeText(PolicyInfoActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("lno", mLno);

        OkGo.post(Path.GET_POLICY_INFO).tag(this).params(params).execute(new StringDialogCallback(PolicyInfoActivity.this, "正在获取保单信息...") {
            @Override
            public void onSuccess(String result, Call call, Response response) {
                if (!TextUtils.isEmpty(result)) {
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                    JsonResult jr = gson.fromJson(result, new TypeToken<JsonResult>() {
                    }.getType());
                    if (jr != null  && jr.getStatusCode() == 200) {
                        result = jr.getResult();
                        if (!TextUtils.isEmpty(result)) {
                            List<Policy> policies = gson.fromJson(result, new TypeToken<List<Policy>>() {
                            }.getType());
                            if (policies != null && policies.size() > 0) {
                                policyList = policies;
                                adapter = new PolicyAdapter(PolicyInfoActivity.this, policyList);
                                mListView.setAdapter(adapter);
                                search_btn.setVisibility(View.GONE);
                                app.setPolicyInfos(policyList);
                                mListView.setBackgroundColor(getResources().getColor(R.color.cadetblue));
                                Toast.makeText(PolicyInfoActivity.this, "点击即可查看背面信息", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(PolicyInfoActivity.this, "暂无相关数据", Toast.LENGTH_SHORT).show();
                                mListView.setBackgroundColor(getResources().getColor(R.color.gainsboro));
                            }
                        } else {
                            Toast.makeText(PolicyInfoActivity.this, "暂无相关数据", Toast.LENGTH_SHORT).show();
                            mListView.setBackgroundColor(getResources().getColor(R.color.gainsboro));
                        }
                    } else {

                            Toast.makeText(PolicyInfoActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                            mListView.setBackgroundColor(getResources().getColor(R.color.gainsboro));

                    }
                } else {
                    Toast.makeText(PolicyInfoActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                    mListView.setBackgroundColor(getResources().getColor(R.color.gainsboro));
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                Toast.makeText(PolicyInfoActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                mListView.setBackgroundColor(getResources().getColor(R.color.gainsboro));
            }

        });


    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(PolicyInfoActivity.this, PicLookAct.class));
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
            policy_id_barcode_iv = (ImageView) view.findViewById(R.id.policy_id_barcode_iv);
            service_term_tv = (TextView) view.findViewById(R.id.service_term_tv);

        }
    }


    private class PolicyAdapter extends BaseAdapter {

        private Context context;
        private List<Policy> policyInfos = new ArrayList<>();
        private LayoutInflater inflater;

        public PolicyAdapter(Context context, List<Policy> policyInfos) {
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
                convertView = inflater.inflate(R.layout.policy_lv_item1, null);
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
            if (!TextUtils.isEmpty(model)) {
                viewHolder.model_tv.setText(model);
            } else {
                viewHolder.model_tv.setText("");
            }


            //车辆录入平台日期
            String date = policy.getCreatetime();
            if (!TextUtils.isEmpty(date)) {
                viewHolder.date_tv.setText(date);
            } else {
                viewHolder.date_tv.setText("");
            }
            //保险档位
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
            if (!TextUtils.isEmpty(startTime) && !TextUtils.isEmpty(endTime)) {
                viewHolder.service_term_tv.setText(startTime + "至" + endTime);
            } else {
                viewHolder.service_term_tv.setText("");
            }


            return convertView;
        }
    }


    @Override
    protected void onDestroy() {
        OkGo.getInstance().cancelTag(this);
        super.onDestroy();

    }
}
