package com.guoji.mobile.cocobee.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.common.JsonResult;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.callback.StringDialogCallback;
import com.guoji.mobile.cocobee.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 监护对象信息页面
 * Created by _H_JY on 2017/3/3.
 */
public class WardInfoAct extends BaseAct implements View.OnClickListener {

    private ImageButton back_ib;
    private TextView title_tv;
    private Button upload_btn, search_btn;
    private ListView mListView;
    private GuardianAdapter adapter;
    List<User> guardians = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_info);

        initViewWithData();

    }


    private void initViewWithData() {
        back_ib = (ImageButton) findViewById(R.id.back_ib);
        title_tv = (TextView) findViewById(R.id.title_tv);
        upload_btn = (Button) findViewById(R.id.upload_btn);
        search_btn = (Button) findViewById(R.id.search_btn);
        mListView = (ListView) findViewById(R.id.listView);

        mListView.addHeaderView(new ViewStub(this));
        mListView.addFooterView(new ViewStub(this));

        search_btn.setVisibility(View.GONE);
        upload_btn.setVisibility(View.GONE);
        back_ib.setOnClickListener(this);

        title_tv.setText("监护对象信息");



        Map<String, String> params = new HashMap<String, String>();
        params.put("pid", user.getPid());

        OkGo.post(Path.GET_GUARDIANS_INFO_PATH).tag(this).params(params).execute(new StringDialogCallback(WardInfoAct.this, "正在获取数据...") {
            @Override
            public void onSuccess(String result, Call call, Response response) {
                if (!TextUtils.isEmpty(result)) {
                    Gson gson = new Gson();
                    JsonResult jsonResult = gson.fromJson(result, new TypeToken<JsonResult>() {
                    }.getType());
                    if (jsonResult != null && jsonResult.isFlag() && jsonResult.getStatusCode() == 200) {
                        String jsonUsers = jsonResult.getResult();
                        if (!TextUtils.isEmpty(jsonUsers)) {
                            guardians = gson.fromJson(jsonUsers, new TypeToken<List<User>>() {
                            }.getType());
                            if (guardians != null && guardians.size() > 0) {
                                adapter = new GuardianAdapter(WardInfoAct.this, guardians);
                                mListView.setAdapter(adapter);
                            } else {
                                Toast.makeText(WardInfoAct.this, "暂无相关数据", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(WardInfoAct.this, "暂无相关数据", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(WardInfoAct.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(WardInfoAct.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                Toast.makeText(WardInfoAct.this, "获取数据失败", Toast.LENGTH_SHORT).show();
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


    private class ViewHolder {

        private TextView g_name_tv;
        private TextView g_idcard_tv;
        private TextView g_gender_tv;
        private TextView g_phone_tv;
        private TextView g_address_tv;


        public ViewHolder(View convertView) {
            g_name_tv = (TextView) convertView.findViewById(R.id.recogcode_tv);
            g_idcard_tv = (TextView) convertView.findViewById(R.id.coordinates_tv);
            g_gender_tv = (TextView) convertView.findViewById(R.id.install_status_tv);
            g_phone_tv = (TextView) convertView.findViewById(R.id.online_status_tv);
            g_address_tv = (TextView) convertView.findViewById(R.id.address_tv);
            g_address_tv.setVisibility(View.GONE);
        }

    }


    private class GuardianAdapter extends BaseAdapter {

        private Context context;
        private List<User> users = new ArrayList<>();
        private LayoutInflater inflater;


        public GuardianAdapter(Context context, List<User> users) {
            this.context = context;
            this.users = users;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return users.size();
        }

        @Override
        public Object getItem(int position) {
            return users.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            User user = users.get(position);
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.pos_info_lv_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            String name = user.getPname();
            if (!TextUtils.isEmpty(name)) {
                viewHolder.g_name_tv.setVisibility(View.VISIBLE);
                viewHolder.g_name_tv.setText("姓名：" + name);
            } else {
                viewHolder.g_name_tv.setVisibility(View.GONE);
            }


            String idcard = user.getIdcard();
            if (!TextUtils.isEmpty(idcard)) {
                viewHolder.g_idcard_tv.setVisibility(View.VISIBLE);
                viewHolder.g_idcard_tv.setText("身份证号：" + idcard);
            } else {
                viewHolder.g_idcard_tv.setVisibility(View.GONE);
            }


            String gender = user.getSex();
            if (!TextUtils.isEmpty(gender)) {
                viewHolder.g_gender_tv.setVisibility(View.VISIBLE);
                if (TextUtils.equals("0", gender)) {
                    viewHolder.g_gender_tv.setText("性别：男");
                } else {
                    viewHolder.g_gender_tv.setText("性别：女");
                }

            } else {
                viewHolder.g_gender_tv.setVisibility(View.GONE);
            }


            String phone = user.getMobile();
            if (!TextUtils.isEmpty(phone)) {
                viewHolder.g_phone_tv.setVisibility(View.VISIBLE);
                viewHolder.g_phone_tv.setText("电话：" + phone);
            } else {
                viewHolder.g_phone_tv.setVisibility(View.GONE);
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
