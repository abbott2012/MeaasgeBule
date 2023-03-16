package com.guoji.mobile.cocobee.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.callback.StringComCallback;
import com.guoji.mobile.cocobee.common.Constant;
import com.guoji.mobile.cocobee.common.JsonResult;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.callback.StringDialogCallback;
import com.guoji.mobile.cocobee.model.AlarmInfo;
import com.guoji.mobile.cocobee.utils.FileOperateUtils;
import com.guoji.mobile.cocobee.utils.ImageUtils;
import com.guoji.mobile.cocobee.utils.LogUtil;
import com.guoji.mobile.cocobee.view.PointSearchView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Decoder.BASE64Encoder;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 报警信息搜索页面
 * Created by _H_JY on 2016/12/17.
 */
public class AlarmInfoSearchAct extends BaseAct implements View.OnClickListener,AdapterView.OnItemClickListener{
    private Context context;
    private ImageButton back_ib;
    private PointSearchView<AlarmInfo> pointSearchView;
    private ListView mListView;
    private SearchAlarmInfoAdapter adapter;
    private List<AlarmInfo> alarmInfos = new ArrayList<>();
    private int realPos;
    private EditText first_item_et; //第一个输入框，身份证或车牌号
    private int flag = 0; //默认选中车辆报警
    private String aType; //报警类型
    private String logid;
    private String alarmAddr;
    private String alarmLat;
    private String alarmLng;
    private int type = 0;
    private ImageView pic_iv; //报警图片
    private String pic_str;
    private String cid;



    private View mView;
    private PopupWindow mWindow;
    public  RelativeLayout uploadPhoto, uploadAlbum;
    private final int PS_CAMERA_REQ = 1111;
    private final int PS_STORAGE_REQ = 2222;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_point_search);
        context = this;

        alarmInfos.addAll(app.getAlarmInfos());

        initView();
    }

    private void initView() {

        back_ib = (ImageButton) findViewById(R.id.back_ib);
        mListView = (ListView) findViewById(R.id.listView);
        pointSearchView = (PointSearchView) findViewById(R.id.searchView);

        mListView.addHeaderView(new ViewStub(this));
        mListView.addFooterView(new ViewStub(this));

        mListView.setOnItemClickListener(this);

        pointSearchView.setHintString("姓名、身份证号、电话号码关键词");

        back_ib.setOnClickListener(this);

        adapter = new SearchAlarmInfoAdapter(this, alarmInfos);
        mListView.setAdapter(adapter);

        //设置数据源
        pointSearchView.setDatas((ArrayList)alarmInfos);

        //设置适配器
        pointSearchView.setAdapter(adapter);

        pointSearchView.setSearchDataListener(new PointSearchView.SearchDatas<AlarmInfo>() {
            @Override
            public List<AlarmInfo> filterDatas(List<AlarmInfo> datas, List<AlarmInfo> filterdatas, String inputstr) {
                for (int i = 0; i < datas.size(); i++) {
                    AlarmInfo alarmInfo = datas.get(i);

                    if (alarmInfo.getCarOwnerName().contains(inputstr) || alarmInfo.getCarOwnerPhone().contains(inputstr) || alarmInfo.getCarOwnerId().contains(inputstr)) {
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


            case R.id.pic_iv:
                showPopupwindow(v);
                break;

        }
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(position >= 1){
            final AlarmInfo alarmInfo = alarmInfos.get(position-1);

            if(TextUtils.equals(alarmInfo.getAlarmStatus(),"2")){ //异常信息才需要忽略和报案
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setItems(new String[]{"忽略","报案"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        switch (which){
                            case 0: //忽略
                                Map<String,String> params = new HashMap<String, String>();
                                params.put("logid",alarmInfo.getLogid());
                                params.put("ishandle","3");
                                OkGo.post(Path.HANDLE_ALARM_INFO_PATH).tag(this).params(params).execute(new StringDialogCallback(AlarmInfoSearchAct.this,"忽略中...") {
                                    @Override
                                    public void onSuccess(String result, Call call, Response response) {
                                        if(!TextUtils.isEmpty(result)){
                                            JsonResult jsonResult = new Gson().fromJson(result,new TypeToken<JsonResult>(){}.getType());
                                            if(jsonResult != null && jsonResult.isFlag() && jsonResult.getStatusCode() == 200){//忽略成功
                                                alarmInfos.remove(alarmInfo);
                                                adapter.notifyDataSetChanged();
                                                Toast.makeText(context, "已忽略", Toast.LENGTH_SHORT).show();
                                            }else {
                                                Toast.makeText(context,"操作失败",Toast.LENGTH_SHORT).show();
                                            }
                                        }else {
                                            Toast.makeText(context,"操作失败",Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onError(Call call, Response response, Exception e) {
                                        super.onError(call, response, e);
                                        Toast.makeText(context,"操作失败",Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;

                            case 1: //报案
                                logid = alarmInfo.getLogid();
                                cid = alarmInfo.getCid();
                                type = 10; //表示报案
                                aType = alarmInfo.getAtype();
                                String aType = alarmInfo.getAtype();
                                if(TextUtils.equals("1",aType)){ //车辆报警
                                    showUploadDialog(alarmInfo.getCno());
                                }else if(TextUtils.equals("2",aType)){ //人员报警
                                    showUploadDialog(alarmInfo.getCarOwnerId());
                                }else {
                                    showUploadDialog("");
                                }
                                break;
                        }


                    }
                });

                builder.create().show();
            }

        }

    }





    private void showUploadDialog(String text) { //参数存放车牌或身份证

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View contentView = LayoutInflater.from(context).inflate(R.layout.act_upload_alarminfo, null);
        final EditText describe_et = (EditText) contentView.findViewById(R.id.describe_et);
        final TextView first_item_tip_tv = (TextView) contentView.findViewById(R.id.first_item_tip_tv);
        LinearLayout chooseCarLl = (LinearLayout) contentView.findViewById(R.id.ll);
        first_item_et = (EditText) contentView.findViewById(R.id.car_et);


        first_item_et.setText(text);

        RadioGroup radioGroup = (RadioGroup) contentView.findViewById(R.id.type_rg);
        RadioButton car_rb = (RadioButton)contentView.findViewById(R.id.car_rb);
        RadioButton people_rb = (RadioButton)contentView.findViewById(R.id.people_rb);

        if(type == 10){ //报案
            if(TextUtils.equals(aType,"1")){ //车辆报警
                flag = 0;
                car_rb.setVisibility(View.VISIBLE);
                car_rb.setChecked(true);
                first_item_tip_tv.setText("车牌号：");
                first_item_et.setHint("请输入车牌号");
                people_rb.setVisibility(View.GONE);
            }else if(TextUtils.equals(aType,"2")){ //人员报警
                flag=1;
                first_item_tip_tv.setText("身份证号：");
                first_item_et.setHint("请输入身份证号");
                car_rb.setVisibility(View.GONE);
                people_rb.setVisibility(View.VISIBLE);
                people_rb.setChecked(true);
            }else {
                car_rb.setVisibility(View.VISIBLE);
                people_rb.setVisibility(View.VISIBLE);
            }
        }else {
            car_rb.setVisibility(View.VISIBLE);
            people_rb.setVisibility(View.VISIBLE);
        }





        pic_iv = (ImageView) contentView.findViewById(R.id.pic_iv);
        pic_iv.setOnClickListener(this);
                  /*长按取消已经选中的照片*/
        pic_iv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!TextUtils.isEmpty(pic_str)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setItems(new String[]{"删除照片"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            pic_iv.setImageResource(R.drawable.post_add_pic);
                            pic_str = null;
                        }
                    });
                    builder.create().show();
                }

                return false;
            }
        });

        builder.setTitle("报警信息上传");
        builder.setView(contentView);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("报警", null);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();


        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (true/*TextUtils.equals(user.getPtype(), "1")*/ || (user.getApproleid() == Constant.ADMINISTRATOR)) {//车主或管理员
                    if (TextUtils.isEmpty(first_item_et.getText().toString().trim())) {
                        Toast.makeText(context, flag == 1 ? "身份证号不能为空" : "车牌号不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                final String describeStr = describe_et.getText().toString().trim();
                if (TextUtils.isEmpty(describeStr)) {
                    Toast.makeText(context, "描述不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (TextUtils.isEmpty(pic_str)) {
                    Toast.makeText(context, "图片不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                alertDialog.dismiss();


                Map<String, String> params = new HashMap<String, String>();

                if (user.getApproleid() == Constant.ADMINISTRATOR) { //管理员

                    if (TextUtils.equals(aType,"1")) { //车辆报警
                        params.put("cno", first_item_et.getText().toString().trim());
                    } else { //人员报警
                        params.put("idcard", first_item_et.getText().toString().trim());
                    }

                } else { //普通人员
                    params.put("pid", user.getPid());
                    if (true /*TextUtils.equals(user.getPtype(), "1")*/) { //车主
                        params.put("cid", cid);
                    } else { //标签人员
                        params.put("labelid", user.getLabelid());
                    }

                    params.put("apeople", user.getUsername());
                    params.put("amobile", user.getMobile());
                    params.put("atype","1" /*user.getPtype()*/);
                    params.put("orgid", user.getOrgid());

                }


                    params.put("aaddress", alarmAddr);
                    params.put("alarmlng", alarmLng);
                    params.put("alarmlat", alarmLat);




                params.put("adesc", describeStr);
                params.put("orgids", user.getOrgids());

                String picBase64Str = "";
                if (!TextUtils.isEmpty(pic_str)) {//身份证照片
                    try {
                        InputStream is = new FileInputStream(pic_str);
                        byte[] picData = new byte[is.available()];
                        is.read(picData);
                        is.close();
                        BASE64Encoder encoder = new BASE64Encoder();
                        picBase64Str = encoder.encode(picData);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                params.put("photo", picBase64Str);

                String path = "";
                if (user.getApproleid() == Constant.ADMINISTRATOR && flag == 0) { //管理员车辆报警
                    path = Path.ADMINISTRATOR_UPLOAD_CAR_ALARM_INFO_PATH;
                } else if (user.getApproleid() == Constant.ADMINISTRATOR && flag == 1) { //管理员人员报警
                    path = Path.ADMINISTRATOR_UPLOAD_PERSON_ALARM_INFO_PATH;
                } else {
                    path = Path.NORMAL_USER_UPLOAD_ALARM_INFO_PATH;
                }


                OkGo.post(path).tag(this).params(params).execute(new StringDialogCallback(AlarmInfoSearchAct.this, "正在报警...") {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (!TextUtils.isEmpty(s)) {
                            JsonResult jr = new Gson().fromJson(s, new TypeToken<JsonResult>() {
                            }.getType());
                            if (jr != null && jr.isFlag() && jr.getStatusCode() == 200) {
                                Toast.makeText(context, "报警成功，我们会尽快为您核实处理", Toast.LENGTH_SHORT).show();

                                if(type == 10){ //报案，需要修改后台状态
                                    Map<String,String> params = new HashMap<String, String>();
                                    params.put("logid",logid);
                                    params.put("ishandle","4");
                                    OkGo.post(Path.HANDLE_ALARM_INFO_PATH).tag(this).params(params).execute(new StringComCallback() {
                                        @Override
                                        public void onSuccess(String result, Call call, Response response) {

                                        }
                                    });
                                }

                            } else {

                                if (user.getApproleid() == Constant.ADMINISTRATOR && jr.getStatusCode() == 300) {
                                    if (flag == 0) { //车辆
                                        Toast.makeText(context, "车辆不存在", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "用户不存在", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(context, "报警失败", Toast.LENGTH_SHORT).show();
                                }

                            }
                        } else {
                            Toast.makeText(context, "报警失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toast.makeText(context, "报警失败", Toast.LENGTH_SHORT).show();
                    }

                });


            }
        });

    }




    private class ViewHolder{

        private TextView host_name_tv;
        private TextView host_id_tv;
        private TextView host_phone_tv;
        private TextView device_id_tv;
        private TextView alarm_address_tv;
        private TextView alarm_time_tv;
        private TextView alarm_status_tv;
        private TextView cno_tv;


        public ViewHolder(View view){
            host_name_tv = (TextView) view.findViewById(R.id.name_tv);
            host_id_tv = (TextView)view.findViewById(R.id.idcard_tv);
            host_phone_tv = (TextView)view.findViewById(R.id.phone_tv);
            device_id_tv = (TextView)view.findViewById(R.id.policy_order_tv);
            alarm_address_tv = (TextView)view.findViewById(R.id.policy_price_tv);
            alarm_time_tv = (TextView)view.findViewById(R.id.start_time_tv);
            alarm_status_tv = (TextView)view.findViewById(R.id.alarm_status_tv);
            cno_tv = (TextView) view.findViewById(R.id.cno_tv);
        }

    }


    public class SearchAlarmInfoAdapter extends BaseAdapter {

        private Context context;
        private List<AlarmInfo> searchResAlarmInfos = new ArrayList<>();
        private LayoutInflater layoutInflater;

        public SearchAlarmInfoAdapter(Context context, List<AlarmInfo> searchResAlarmInfos) {
            this.context = context;
            this.searchResAlarmInfos = searchResAlarmInfos;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return searchResAlarmInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return searchResAlarmInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            AlarmInfo alarmInfo = searchResAlarmInfos.get(position);
            ViewHolder viewHolder;
            if(view == null){
                view  = layoutInflater.inflate(R.layout.alarminfo_lv_item,null);
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) view.getTag();
            }


            String typeTip="";
            if(TextUtils.equals(alarmInfo.getAtype(), "1")){ //车辆报警信息，显示车牌
                viewHolder.cno_tv.setVisibility(View.VISIBLE);
                viewHolder.cno_tv.setText("车牌号：" + alarmInfo.getCno());
                typeTip = "车辆";
            }else if(TextUtils.equals(alarmInfo.getAtype(),"2")){
                viewHolder.cno_tv.setVisibility(View.GONE);
                typeTip = "人员";
            }else {
                viewHolder.cno_tv.setVisibility(View.GONE);
            }



            viewHolder.host_name_tv.setText("姓名："+alarmInfo.getCarOwnerName());
            viewHolder.host_id_tv.setText("身份证号："+alarmInfo.getCarOwnerId());
            viewHolder.host_phone_tv.setText("电话号码："+alarmInfo.getCarOwnerPhone());
            viewHolder.device_id_tv.setText("采集器序列号："+alarmInfo.getDeviceNum());
            viewHolder.alarm_address_tv.setText("报警地点："+alarmInfo.getAlarmAddress());
            viewHolder.alarm_time_tv.setText("报警时间："+alarmInfo.getAlarmTime());

            if ("1".equals(alarmInfo.getAlarmStatus())) {
                viewHolder.alarm_status_tv.setTextColor(Color.parseColor("#fa8072"));
                viewHolder.alarm_status_tv.setText(typeTip+"报警信息");
            } else if ("2".equals(alarmInfo.getAlarmStatus())) {
                viewHolder.alarm_status_tv.setTextColor(Color.parseColor("#dc143c"));
                viewHolder.alarm_status_tv.setText(typeTip+"异常信息");
            }


            return view;
        }
    }




    /**
     * @param parent
     * @description popupwindow实现
     */
    @SuppressWarnings("deprecation")
    private void showPopupwindow(View parent) {

        if (mWindow == null) {
            mView = LayoutInflater.from(this).inflate(
                    R.layout.ppw_modify_image, null);
            mView.setFocusable(true); // 这个很重要
            mView.setFocusableInTouchMode(true);
            uploadPhoto = (RelativeLayout) mView   //拍照上传
                    .findViewById(R.id.rlyt_upload_photo);
            uploadAlbum = (RelativeLayout) mView //相册上传
                    .findViewById(R.id.rlyt_upload_album);
            mWindow = new PopupWindow(mView, 700, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        mWindow.setAnimationStyle(R.style.popwin_anim_style);
        mWindow.setFocusable(true);
        mWindow.setBackgroundDrawable(new BitmapDrawable());
        backgroundAlpha(0.5f);

        // 添加pop窗口关闭事件
        mWindow.setOnDismissListener(new popupDismissListener());
        // 重写onKeyListener
        mView.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mWindow.dismiss();
                    backgroundAlpha(1f);
                    return true;
                }
                return false;
            }
        });
        mWindow.update();
        mWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
        uploadPhoto.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) { //拍照
                /*使用6.0的SDK（23）在6.0的手机上，对于危险权限需要在代码中动态申请*/
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //进入到这里代表没有获取权限
                    if (ActivityCompat.shouldShowRequestPermissionRationale(AlarmInfoSearchAct.this, Manifest.permission.CAMERA)) {
                        //已经禁止提示了
                        Toast.makeText(context, "您已禁止该权限，需要重新开启", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        ActivityCompat.requestPermissions(AlarmInfoSearchAct.this, new String[]{Manifest.permission.CAMERA}, PS_CAMERA_REQ);

                    }

                } else {

                    Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    SimpleDateFormat sDateFormat = new SimpleDateFormat(
                            "yyyyMMdd_hhmmss");
                    Uri uri = null;

                    pic_str = sDateFormat.format(new java.util.Date());
                    pic_str = Path.IMAGE_TEMP_FILE_PATH + pic_str + ".jpg"; //先放入临时文件夹
                    // 加载路径
                    uri = Uri.fromFile(new File(pic_str));

                    // 指定存储路径，这样就可以保存原图了
                    intent2.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(intent2, 2);
                    mWindow.dismiss();
                    backgroundAlpha(1f);
                }


            }
        });
        uploadAlbum.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) { //从手机系统图库获取

                  /*使用6.0的SDK（23）在6.0的手机上，对于危险权限需要在代码中动态申请*/
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //进入到这里代表没有获取权限
                    if (ActivityCompat.shouldShowRequestPermissionRationale(AlarmInfoSearchAct.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        //已经禁止提示了
                        Toast.makeText(context, "您已禁止该权限，需要重新开启", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        ActivityCompat.requestPermissions(AlarmInfoSearchAct.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PS_STORAGE_REQ);

                    }

                } else {
                   /* Intent intent1 = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);*/
                /*intent1.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image*//*");*/

                    Intent intent = new Intent();
                    intent.setType("image/*");//可选择图片视频
                    //修改为以下两句代码
                    intent.setAction(Intent.ACTION_PICK);
                    intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//使用以上这种模式，并添加以上两句
                    startActivityForResult(intent, 1);

                    mWindow.dismiss();
                    backgroundAlpha(1f);
                }


            }
        });
    }

    /*使用6.0的SDK（23）在6.0的手机上，对于危险权限需要在代码中动态申请*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PS_CAMERA_REQ:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //用户同意授权

                    Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    SimpleDateFormat sDateFormat = new SimpleDateFormat(
                            "yyyyMMdd_hhmmss");

                    pic_str = sDateFormat.format(new java.util.Date());
                    pic_str = Path.IMAGE_TEMP_FILE_PATH + pic_str + ".jpg"; //先放入临时文件夹
                    // 加载路径
                    Uri uri = Uri.fromFile(new File(pic_str));

                    // 指定存储路径，这样就可以保存原图了
                    intent2.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(intent2, 2);
                    mWindow.dismiss();
                    backgroundAlpha(1f);
                } else {
                    //用户拒绝授权
                }
                break;
            case PS_STORAGE_REQ:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //用户同意授权
                 /*   Intent intent1 = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);*/
                    Intent intent = new Intent();
                    intent.setType("image/*");//可选择图片视频
                    //修改为以下两句代码
                    intent.setAction(Intent.ACTION_PICK);
                    intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//使用以上这种模式，并添加以上两句
                    startActivityForResult(intent, 1);

                    mWindow.dismiss();
                    backgroundAlpha(1f);
                } else {
                    //用户拒绝授权
                }
                break;
        }
    }


    /**
     * @param bgAlpha
     * @description 设置添加屏幕的背景透明度
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        this.getWindow().setAttributes(lp);
    }

    /**
     * @author Guan
     * @version 1.0
     * @description 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
     * @date 2015-6-8 下午10:08:16
     */
    public class popupDismissListener implements PopupWindow.OnDismissListener {
        public void onDismiss() {
            backgroundAlpha(1f);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            LogUtil.I("SD card is not avaiable/writeable right now.");
            return;
        }

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                // 图库
                case 1:
                    if (data != null) {
                        //这里加个判断，两种系统版本获取图片路径
                        String picturePath;
                        Uri uri = data.getData();
                        if (!TextUtils.isEmpty(uri.getAuthority())) {

                            String[] filePathColumn = {MediaStore.Images.Media.DATA};

                            Cursor cursor = getContentResolver().query(uri,
                                    filePathColumn, null, null, null);

                            if (null == cursor) {
                                Toast.makeText(this, "图片没找到", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            cursor.moveToFirst();

                            picturePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
                            cursor.close();
                        } else {
                            picturePath = data.getData().getPath();
                        }

                        try {
                            // Get the result data
                            File picFile = new File(picturePath);
                            Bitmap bitmap = null;
                            if (FileOperateUtils.getFileSize(picFile) > Constant.PIC_SIZE_LIMIT) { //如果大于限定大小，压缩位图
                                bitmap = ImageUtils.compressImageFromFile(picturePath);
                            } else {
                                bitmap = BitmapFactory.decodeFile(picturePath);
                            }

                            // Set the datetime as the name of new picture
                            SimpleDateFormat sDateFormat = new SimpleDateFormat(
                                    "yyyyMMdd_hhmmss");

                            pic_str = sDateFormat.format(new java.util.Date());
                            pic_str = Path.IMAGE_TEMP_FILE_PATH + pic_str + ".jpg"; //先放到临时文件夹管理
                            // Write the picture data to SD card.
                            ImageUtils.compressBmpToFile(bitmap, new File(pic_str));

                            pic_iv.setImageBitmap(bitmap);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case 2: //拍照

                    try {
                        FileInputStream is = new FileInputStream(pic_str);
                        Bitmap bitmap = BitmapFactory.decodeStream(is);

                        File fileName = new File(pic_str);
                        if (FileOperateUtils.getFileSize(fileName) > Constant.PIC_SIZE_LIMIT) {
                            Bitmap newBitmap = ImageUtils.compressImageFromFile(pic_str);//获取压缩后的bitmap
                            if (newBitmap != null) {
                                if (fileName.exists()) {
                                    fileName.delete(); //删掉原文件
                                }
                                pic_str = new SimpleDateFormat(
                                        "yyyyMMdd_hhmmss").format(new java.util.Date());
                                pic_str = Path.IMAGE_TEMP_FILE_PATH + pic_str + ".jpg";
                                //创建新文件
                                ImageUtils.compressBmpToFile(newBitmap, new File(pic_str));
                                bitmap = newBitmap;
                            }

                        }

                        pic_iv.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;

                default:
                    break;
            }
        }
    }



    @Override
    protected void onDestroy() {


        //当前页面销毁时，如果临时图片文件夹中有图片，则清空
        File f = new File(Path.IMAGE_TEMP_FILE_PATH);
        File[] files = f.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                file.delete();
            }
        }

        OkGo.getInstance().cancelTag(this);

        super.onDestroy();
    }
}
