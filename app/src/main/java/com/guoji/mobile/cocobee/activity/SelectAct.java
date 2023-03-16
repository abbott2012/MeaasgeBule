package com.guoji.mobile.cocobee.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.callback.StringComCallback;
import com.guoji.mobile.cocobee.common.Constant;
import com.guoji.mobile.cocobee.common.JsonResult;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.model.Car;
import com.guoji.mobile.cocobee.model.CarScallResponse;
import com.guoji.mobile.cocobee.model.PoliceStation;
import com.guoji.mobile.cocobee.model.UsableTag;
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
 * 对话框选择页面，选择组织结构、人员类型、车牌、用户等
 * Created by _H_JY on 16/11/4.
 */
public class SelectAct extends BaseAct implements AdapterView.OnItemClickListener {

    private Context context;
    private ProgressBar progressBar;
    private ListView mListView;
    private TextView result_tip_tv;
    private ArrayAdapter<UsableTag> adapter;
    private List<UsableTag> usableTags = new ArrayList<>();
    private List<PoliceStation> policeStations = new ArrayList<>();
    private List<Car> cars = new ArrayList<>();
    private List<String> pTypeList = new ArrayList<>();
    private List<CarScallResponse> policyList = new ArrayList<>();
    private int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_tagid);

        context = this;

        flag = getIntent().getIntExtra("selectFlag", 0);

        initView();
    }


    private void initView() {
        mListView = (ListView) findViewById(R.id.usable_tags_lv);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        result_tip_tv = (TextView) findViewById(R.id.result_tip_tv);

        mListView.setOnItemClickListener(this);


        loadData(flag);
    }


    private void loadData(final int flag) {
        result_tip_tv.setText("正在加载数据...");
        progressBar.setVisibility(View.VISIBLE);

        Map<String, String> map = new HashMap<>();
        if (flag != Constant.SELECT_CAR_STALL_ACT) { //获取档次时无需传入orgids
            map.put("orgids", user.getOrgids());
        }
        String path = "";
        switch (flag) {
            case Constant.SELECT_TAG_ACT:
                path = Path.GET_USABLE_TAGS;
                break;

            case Constant.SELECT_POLICE_ACT:
                path = Path.GET_POLICE_STATIONS;
                map.put("orgid", user.getOrgid());
                break;

            case Constant.SELECT_CNO_ACT:
                path = Path.GET_CNOS;
                if (user == null) {
                    Toast.makeText(context, "请先登录，再查看车牌号", Toast.LENGTH_SHORT).show();
                    return;
                }
                map.put("username", user.getUsername());
                break;


            case Constant.SELECT_CAR_STALL_ACT:
                path = Path.CAR_STALL_INFO_PATH;
                break;

            case Constant.SELECT_PTYPE_ACT:
                progressBar.setVisibility(View.GONE);

                pTypeList.add("患者");
                pTypeList.add("老人");
                pTypeList.add("学生");
                pTypeList.add("易走失人员");
                pTypeList.add("警务人员");
                pTypeList.add("保安人员");
                pTypeList.add("协警人员");

                adapter = new SelectAdapter(context, android.R.layout.simple_list_item_1, pTypeList);
                result_tip_tv.setText("请选择人员类型");
                mListView.setAdapter(adapter);
                return;
        }
        if (flag == Constant.SELECT_CAR_STALL_ACT) { //获取档次
            OkGo.post(path).tag(this).params("user_id", user.getPid()).execute(new StringComCallback(){
                @Override
                public void onSuccess(String result, Call call, Response response) {
                    progressBar.setVisibility(View.GONE);
                    if (!TextUtils.isEmpty(result)) {
                        Gson gson = new Gson();
                        final JsonResult jr = gson.fromJson(result, new TypeToken<JsonResult>() {
                        }.getType());
                        if (jr != null && jr.getStatusCode() == 200) {
                            String jsonString = jr.getResult();
                            if (!TextUtils.isEmpty(jsonString)) {
                                policyList = gson.fromJson(jsonString, new TypeToken<List<CarScallResponse>>() {
                                }.getType());
                                if (policyList != null && policyList.size() > 0) {
                                    adapter = new SelectAdapter(context, android.R.layout.simple_list_item_1, policyList);
                                    result_tip_tv.setText("请选择车价档次" + "(" + policyList.size() + ")");
                                    mListView.setAdapter(adapter);
                                }

                            } else {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "暂无相关数据", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }, 1000);
                            }
                        } else {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, jr.getMessage(), Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }, 1000);
                        }
                    } else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "加载数据失败", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }, 1000);
                    }
                }

                @Override
                public void onError(Call call, Response response, final Exception e) {
                    super.onError(call, response, e);
                    progressBar.setVisibility(View.GONE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }, 1000);
                }

            });

        } else {

            OkGo.post(path).tag(this).params(map).execute(new StringComCallback(){
                @Override
                public void onSuccess(String result, Call call, Response response) {
                    progressBar.setVisibility(View.GONE);
                    if (!TextUtils.isEmpty(result)) {
                        Gson gson = new Gson();
                        JsonResult jr = gson.fromJson(result, new TypeToken<JsonResult>() {
                        }.getType());
                        if (jr != null && jr.isFlag() && jr.getStatusCode() == 200) {
                            String jsonString = jr.getResult();
                            if (!TextUtils.isEmpty(jsonString)) {
                                if (flag == Constant.SELECT_TAG_ACT) {
                                    usableTags = gson.fromJson(jsonString, new TypeToken<List<UsableTag>>() {
                                    }.getType());
                                    if (usableTags != null && usableTags.size() > 0) {

                                        adapter = new SelectAdapter(context, android.R.layout.simple_list_item_1, usableTags);
                                        result_tip_tv.setText("请选择标签号" + "(" + usableTags.size() + ")");


                                        mListView.setAdapter(adapter);
                                    } else {
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(context, "暂无相关数据", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        }, 1000);
                                    }
                                }

                                if (flag == Constant.SELECT_POLICE_ACT) {
                                    policeStations = gson.fromJson(jsonString, new TypeToken<List<PoliceStation>>() {
                                    }.getType());
                                    if (policeStations != null && policeStations.size() > 0) {
                                        adapter = new SelectAdapter(context, android.R.layout.simple_list_item_1, policeStations);
                                        result_tip_tv.setText("请选择组织机构" + "(" + policeStations.size() + ")");
                                        mListView.setAdapter(adapter);
                                    } else {
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(context, "暂无相关数据", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        }, 1000);
                                    }
                                }


                                if (flag == Constant.SELECT_CNO_ACT) {
                                    cars = gson.fromJson(jsonString, new TypeToken<List<Car>>() {
                                    }.getType());
                                    if (cars != null && cars.size() > 0) {
                                        adapter = new SelectAdapter(context, android.R.layout.simple_list_item_1, cars);
                                        result_tip_tv.setText("请选择车牌号" + "(" + cars.size() + ")");
                                        mListView.setAdapter(adapter);

                                    } else {
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(context, "暂无相关数据", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        }, 1000);
                                    }
                                }


                           /* if(flag == Constant.SELECT_CAR_STALL_ACT){
                                policyList = gson.fromJson(jsonString,new TypeToken<List<Policy>>(){}.getType());
                                if(policyList != null && policyList.size()>0){
                                    adapter = new SelectAdapter(context,android.R.layout.simple_list_item_1,policyList);
                                    result_tip_tv.setText("请选择车价档次" + "(" + policyList.size() + ")");
                                    mListView.setAdapter(adapter);
                                }
                            }*/


                            } else {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "暂无相关数据", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }, 1000);
                            }
                        } else {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "加载数据失败", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }, 1000);
                        }
                    } else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "加载数据失败", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }, 1000);
                    }
                }

                @Override
                public void onError(Call call, Response response, Exception e) {
                    super.onError(call, response, e);
                    progressBar.setVisibility(View.GONE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "加载数据失败", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }, 1000);
                }

            });
        }

    }


    public class SelectAdapter<T> extends ArrayAdapter {
        private Context context;
        private int resource;

        public SelectAdapter(Context context, int resource, List<T> objects) {
            super(context, resource, objects);
            this.context = context;
            this.resource = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //UsableTag usableTag = (UsableTag)getItem(position);
            ViewHolder viewHolder;
            if (convertView == null) { //使用系统布局资源
                convertView = LayoutInflater.from(context).inflate(resource, null);
                viewHolder = new ViewHolder();
                viewHolder.text = (TextView) convertView.findViewById(android.R.id.text1);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (flag == Constant.SELECT_TAG_ACT) {
                viewHolder.text.setText(((UsableTag) getItem(position)).getLno());
            } else if (flag == Constant.SELECT_POLICE_ACT) {
                viewHolder.text.setText(((PoliceStation) getItem(position)).getOrgname());
            } else if (flag == Constant.SELECT_CNO_ACT) {
                Car car = (Car) getItem(position);
                if ("1".equals(car.getIslocked())) {
                    viewHolder.text.setText(car.getCno() + "     未锁车");
                } else if ("0".equals(car.getIslocked())) {
                    viewHolder.text.setText(car.getCno() + "     已锁车");
                } else {
                    viewHolder.text.setText(car.getCno());
                }

            } else if (flag == Constant.SELECT_PTYPE_ACT) {
                viewHolder.text.setText(getItem(position).toString());
            } else if (flag == Constant.SELECT_CAR_STALL_ACT) {
                viewHolder.text.setText(((CarScallResponse) getItem(position)).getDname() + ((CarScallResponse) getItem(position)).getGrade());
            }


            return convertView;
        }
    }


    private class ViewHolder {
        private TextView text;
    }


    //实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (flag == Constant.SELECT_TAG_ACT) { //选择标签
            String lid = usableTags.get(position).getLid();
            String tagId = usableTags.get(position).getLno();
            Intent i = new Intent();
            i.putExtra("lid", lid);
            i.putExtra("tagId", tagId);

            setResult(1022, i);
        } else if (flag == Constant.SELECT_POLICE_ACT) { //选择组织机构
            String orgid = policeStations.get(position).getOrgid();
            String orgname = policeStations.get(position).getOrgname();
            Intent i = new Intent();
            i.putExtra("orgid", orgid);
            i.putExtra("orgname", orgname);

            setResult(1024, i);
        } else if (flag == Constant.SELECT_CNO_ACT) {  //选择车牌
            String cno = cars.get(position).getCno();
            String cid = cars.get(position).getCid();
            Intent i = new Intent();
            i.putExtra("cno", cno);
            i.putExtra("cid", cid);
            setResult(1034, i);
        } else if (flag == Constant.SELECT_PTYPE_ACT) { //选择人员类型
            Intent i = new Intent();
            switch (pTypeList.get(position)) {
                case "患者":
                    i.putExtra("ptype", "2");
                    break;

                case "老人":
                    i.putExtra("ptype", "3");
                    break;

                case "学生":
                    i.putExtra("ptype", "4");
                    break;

                case "易走失人员":
                    i.putExtra("ptype", "5");
                    break;

                case "警务人员":
                    i.putExtra("ptype", "6");
                    break;

                case "保安人员":
                    i.putExtra("ptype", "7");
                    break;

                case "协警人员":
                    i.putExtra("ptype", "8");
                    break;
            }

            setResult(1035, i);
        } else if (flag == Constant.SELECT_CAR_STALL_ACT) { //返回车价档次选择结果
            String maxPrice = policyList.get(position).getMax_price();
            String servicePrice = policyList.get(position).getService_price();
            String grade = policyList.get(position).getGrade();
            String safeConfigId = policyList.get(position).getId();

            Intent intent = new Intent();
            intent.putExtra("safeClass", grade);
            intent.putExtra("safeLosePrice", maxPrice);
            intent.putExtra("safeServicePrice", servicePrice);
            intent.putExtra("safeConfigId", safeConfigId);
            setResult(1036, intent);
        }

        finish();
    }


    @Override
    protected void onDestroy() {
        OkGo.getInstance().cancelTag(this);
        super.onDestroy();
    }
}
