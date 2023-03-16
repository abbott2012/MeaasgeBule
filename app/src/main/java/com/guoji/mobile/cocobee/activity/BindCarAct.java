package com.guoji.mobile.cocobee.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.aiseminar.ui.CameraActivity;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.callback.StringComCallback;
import com.guoji.mobile.cocobee.common.AppManager;
import com.guoji.mobile.cocobee.common.Constant;
import com.guoji.mobile.cocobee.common.ElectricVehicleApp;
import com.guoji.mobile.cocobee.common.JsonResult;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.callback.StringDialogCallback;
import com.guoji.mobile.cocobee.model.Car;
import com.guoji.mobile.cocobee.model.User;
import com.guoji.mobile.cocobee.utils.Devcode;
import com.guoji.mobile.cocobee.utils.FileOperateUtils;
import com.guoji.mobile.cocobee.utils.ImageUtils;
import com.guoji.mobile.cocobee.utils.SharedPreferencesHelper;
import com.guoji.mobile.cocobee.utils.Tools;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.view.SwitchMultiButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.lzy.okgo.OkGo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Decoder.BASE64Encoder;
import chinaSafe.idcard.android.AuthParameterMessage;
import chinaSafe.idcard.android.AuthService;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 车辆绑定页面，包括车辆绑定和人员绑定
 * Created by _H_JY on 2016/10/26.
 */
public class BindCarAct extends AppCompatActivity implements View.OnClickListener, OnDateSetListener {

    private Context context;
    private ImageButton back_ib;
    private Button upload_btn;
    private SwitchMultiButton switchMultiButton; //选择按钮：车辆绑定（左）、人员绑定（右）
    private LinearLayout car_info_ll, ptype_ll;
    private TextView uname_tip_tv, uphone_tip_tv, tag_person_title;
    private EditText serial_et, ptype_et;
    private EditText uname_et, ucardid_et, uphone_et, ubirthday_et, cdynamo_et;
    private EditText uresidence_et, upolicestation_et, gender_et, remark_et;
    private EditText uworkaddress_et, uurgencyname_et, uurgencyphone_et;
    private EditText cnum_et, cframe_et, cprice_et, ctype_et, cbuytime_et, guardian_idcard_et;
    private TextView ocr_idcard_tv, ocr_cnumber_tv, scan_barcode_tv, ocr_guardian_idcard_tv;
    private LinearLayout guardian_cardid_ll;
    private ElectricVehicleApp app;
    private User user;
    private ImageView ucardid_iv, ucardid_opposite_iv, plate_iv, vehicle_front_iv, vehicle_side_one_iv, vehicle_side_two_iv; //身份证正面／身份证反面／车牌／车辆正面照/车辆侧面照1/车辆侧面照2
    private String ucardid_pic_str, ucardid_opposite_str, plate_str, vehicle_front_str, vehicle_side_one_str, vehicle_side_two_str;
    private View mView;
    private PopupWindow mWindow, genderWindow;
    public RelativeLayout uploadPhoto, uploadAlbum, choose_male_rl, choose_female_rl;
    private LinearLayout vehicle_side_ll, center_pic_ll; //车辆侧面照所在布局
    private int choose_pic_type = 0; //选择图片类型：1身份证正面／2身份证反面／3车牌照片／4车辆正面照片／5车辆侧面照1／6车辆侧面照2
    private String lid, orgid;
    long tenYears = 10L * 365 * 1000 * 60 * 60 * 24L;
    private TimePickerDialog mDialogAll;
    private int timeDialogFlag = 0;
    private int bindFlag = 1;
    private final int TYPE_BIND_CAR = 1;
    private final int TYPE_BIND_PEOPLE = 2;
    private String serial, pid = "-1", guardian_idcard;
    private boolean tagNeedCheck = false;
    private boolean idcardNeedCheck = false;
    private int ocr_idcard_type = 0;


    private Handler handler = new Handler();
    private final int PS_CAMERA_REQ = 1111;
    private final int PS_STORAGE_REQ = 2222;
    private final int MSG_REPEAT_BIND = 300; //重复绑定身份证

    private boolean tagIsOk = false;
    private boolean idcardIsOk = false;
    private boolean isUploadData = false;
    private String ptype;


    private static ProgressDialog progressDialog;
    private AuthService.authBinder authBinder;
    private int ReturnAuthority = -1;
    private String sn;
    private String devcode = Devcode.devcode;// 项目授权开发码
    public ServiceConnection authConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            authBinder = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            authBinder = (AuthService.authBinder) service;
            try {

                AuthParameterMessage apm = new AuthParameterMessage();
                //apm.datefile = "assets"; // PATH+"/IDCard/wtdate.lsc";//预留
                apm.devcode = devcode;// 预留
                apm.sn = sn;

                ReturnAuthority = authBinder.getIDCardAuth(apm);
                if (ReturnAuthority != 0) { //激活失败。清楚已存储的序列号

                    switch (ReturnAuthority) {
                        case -10007:
                            Toast.makeText(getApplicationContext(),
                                    "序列号错误",
                                    Toast.LENGTH_SHORT).show();
                            break;

                        case -10008:
                            Toast.makeText(getApplicationContext(),
                                    "序列号已被使用",
                                    Toast.LENGTH_SHORT).show();
                            break;

                        case -10012:
                            break;

                        default:
                            Toast.makeText(getApplicationContext(),
                                    "ReturnAuthority:" + ReturnAuthority,
                                    Toast.LENGTH_SHORT).show();
                            break;
                    }


                    activeOCR();

                } else {//激活成功

                    /*跳转到识别界面*/
                    /**
                     * 由于在相机界面释放相机等资源会耗费很多时间， 为了优化用户体验，需要在调用相机的那个界面的oncreate()方法中
                     * 调用AppManager.getAppManager().finishAllActivity();
                     * 如果调用和识别的显示界面是同一个界面只需调用一次即可， 如果是不同界面，需要在显示界面的oncreate()方法中
                     * 调用AppManager.getAppManager().finishAllActivity();即可，
                     * 否则会造成相机资源的内存溢出。
                     */

                    final Runnable mTasks = new Runnable() {
                        public void run() {
                            Intent intent = new Intent(BindCarAct.this, CameraIDCardAct.class);
                            intent.putExtra("nMainId", SharedPreferencesHelper.getInt(
                                    getApplicationContext(), "nMainId", 2));
                            intent.putExtra("devcode", devcode);
                            intent.putExtra("flag", 0);
                            if (ocr_idcard_type == 1) {
                                startActivityForResult(intent, 6611);
                            } else if (ocr_idcard_type == 2) {
                                startActivityForResult(intent, 6161);
                            }
                        }
                    };
                    progressDialog = ProgressDialog.show(BindCarAct.this, "",
                            "正在跳转到识别界面…", true, false);

                    new Thread() {
                        public void run() {
                            try {
                                sleep(1000);// 设置休眠时间
                                handler.post(mTasks);// 发送
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();

                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        getString(R.string.license_verification_failed),
                        Toast.LENGTH_LONG).show();

                activeOCR();

            } finally {
                if (authBinder != null) {
                    unbindService(authConn);
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_bindcar);
        context = this;

        AppManager.getAppManager().addActivity(this);

        app = (ElectricVehicleApp) this.getApplication();
        user = Utils.getUserLoginInfo();

        initView();

    }


    private void initView() {
        back_ib = (ImageButton) findViewById(R.id.back_ib);
        upload_btn = (Button) findViewById(R.id.upload_btn);
        serial_et = (EditText) findViewById(R.id.tag_id_et);
        uname_et = (EditText) findViewById(R.id.uname_et);
        ucardid_et = (EditText) findViewById(R.id.ucardid_et);
        uphone_et = (EditText) findViewById(R.id.uphone_et);
        ubirthday_et = (EditText) findViewById(R.id.ubirthday_et);
        gender_et = (EditText) findViewById(R.id.gender_et);
        uresidence_et = (EditText) findViewById(R.id.uresidence_et);
        upolicestation_et = (EditText) findViewById(R.id.upolicestation_et);
        uworkaddress_et = (EditText) findViewById(R.id.uworkaddress_et);
        uurgencyname_et = (EditText) findViewById(R.id.uurgencyname_et);
        uurgencyphone_et = (EditText) findViewById(R.id.uurgencyphone_et);
        cnum_et = (EditText) findViewById(R.id.cnumber_et);
        cframe_et = (EditText) findViewById(R.id.cframe_et);
        cprice_et = (EditText) findViewById(R.id.cprice_et);
        ctype_et = (EditText) findViewById(R.id.ctype_et);
        cbuytime_et = (EditText) findViewById(R.id.cbuytime_et);
        cdynamo_et = (EditText) findViewById(R.id.cdynamo_et);
        remark_et = (EditText) findViewById(R.id.remark_et);
        car_info_ll = (LinearLayout) findViewById(R.id.car_info_ll);
        uname_tip_tv = (TextView) findViewById(R.id.uname_tip_tv);
        uphone_tip_tv = (TextView) findViewById(R.id.uphone_tip_tv);
        vehicle_side_ll = (LinearLayout) findViewById(R.id.vehicle_side_ll);
        center_pic_ll = (LinearLayout) findViewById(R.id.center_pic_ll);
        switchMultiButton = (SwitchMultiButton) findViewById(R.id.switchMultiButton);
        ocr_idcard_tv = (TextView) findViewById(R.id.ocr_idcard_tv);
        ocr_cnumber_tv = (TextView) findViewById(R.id.ocr_cnumber_tv);
        tag_person_title = (TextView) findViewById(R.id.tag_person_title);
        ptype_ll = (LinearLayout) findViewById(R.id.ptype_ll);
        ptype_et = (EditText) findViewById(R.id.ptype_et);
        scan_barcode_tv = (TextView) findViewById(R.id.scan_barcode_tv);
        guardian_cardid_ll = (LinearLayout) findViewById(R.id.guardian_cardid_ll);
        guardian_idcard_et = (EditText) findViewById(R.id.guardian_cardid_et);
        ocr_guardian_idcard_tv = (TextView) findViewById(R.id.ocr_guardian_idcard_tv);


        assert switchMultiButton != null; //断言
        switchMultiButton.setUnselectedTextColor(R.color.cadetblue);
        switchMultiButton.setText(Arrays.asList("车辆绑定", "人员绑定"));
        switchMultiButton.setOnSwitchListener(new SwitchMultiButton.OnSwitchListener() {
            @Override
            public void onSwitch(int position, String tabText) {
                switch (position) {
                    case 0:
                        bindFlag = TYPE_BIND_CAR; //车辆绑定
                        break;
                    case 1:
                        bindFlag = TYPE_BIND_PEOPLE; //人员绑定
                        break;
                }

                clearText();
                changeView(bindFlag);
            }
        });


        mDialogAll = new TimePickerDialog.Builder()
                .setCallBack(this)
                .setCancelStringId("取消")
                .setSureStringId("确定")
                .setTitleStringId("请选择时间")
                .setYearText("年")
                .setMonthText("月")
                .setDayText("日")
                .setHourText("时")
                .setMinuteText("分")
                .setCyclic(false)
                .setMinMillseconds(System.currentTimeMillis() - 15 * tenYears) //当前时间减去一百五十年
                .setMaxMillseconds(System.currentTimeMillis() + 10 * tenYears)
                .setCurrentMillseconds(System.currentTimeMillis())
                .setThemeColor(getResources().getColor(R.color.timepicker_dialog_bg))
                .setType(Type.ALL)
                .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
                .setWheelItemTextSelectorColor(getResources().getColor(R.color.timepicker_toolbar_bg))
                .setWheelItemTextSize(12)
                .build();


        //6张照片
        ucardid_iv = (ImageView) findViewById(R.id.ucardid_iv); //身份证正面
        ucardid_opposite_iv = (ImageView) findViewById(R.id.ucardid_opposite_iv); //身份证反面
        plate_iv = (ImageView) findViewById(R.id.plate_iv); //车牌照片
        vehicle_front_iv = (ImageView) findViewById(R.id.vehicle_front_iv);
        vehicle_side_one_iv = (ImageView) findViewById(R.id.vehicle_side_one_iv);
        vehicle_side_two_iv = (ImageView) findViewById(R.id.vehicle_side_two_iv);


        back_ib.setOnClickListener(this);
        ubirthday_et.setOnClickListener(this);
        ptype_et.setOnClickListener(this);
        gender_et.setOnClickListener(this);
        cbuytime_et.setOnClickListener(this);
        upolicestation_et.setOnClickListener(this);
        scan_barcode_tv.setOnClickListener(this);
        ocr_idcard_tv.setOnClickListener(this);
        ocr_guardian_idcard_tv.setOnClickListener(this);
        ocr_cnumber_tv.setOnClickListener(this);

        upload_btn.setOnClickListener(this);

        ucardid_iv.setOnClickListener(this);
        ucardid_opposite_iv.setOnClickListener(this);
        plate_iv.setOnClickListener(this);
        vehicle_front_iv.setOnClickListener(this);
        vehicle_side_one_iv.setOnClickListener(this);
        vehicle_side_two_iv.setOnClickListener(this);


        serial_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    tagNeedCheck = true;
                    serial_et.setTextColor(Color.BLACK);
                    if (!tagIsOk) { //校验失败
                        serial_et.setText("");
                    }
                } else {
                    if (!isUploadData) {
                        checkTag();
                    }
                }
            }
        });


        guardian_idcard_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    idcardNeedCheck = true;
                    guardian_idcard_et.setTextColor(Color.BLACK);
                    if (!idcardIsOk) { //校验失败
                        guardian_idcard_et.setText("");
                    }
                } else {
                    if (!isUploadData) {
                        checkGuardian();
                    }
                }
            }
        });



         /*长按取消已经选中的身份证照片*/
        ucardid_iv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!TextUtils.isEmpty(ucardid_pic_str)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setItems(new String[]{"删除照片"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ucardid_iv.setImageResource(R.drawable.post_add_pic);
                            ucardid_pic_str = null;
                        }
                    });
                    builder.create().show();
                }

                return false;
            }
        });


         /*长按取消已经选中的身份证反面照片*/
        ucardid_opposite_iv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!TextUtils.isEmpty(ucardid_opposite_str)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setItems(new String[]{"删除照片"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ucardid_opposite_iv.setImageResource(R.drawable.post_add_pic);
                            ucardid_opposite_str = null;
                        }
                    });
                    builder.create().show();
                }

                return false;
            }
        });




         /*长按取消已经选中的车牌照片*/
        plate_iv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!TextUtils.isEmpty(plate_str)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setItems(new String[]{"删除照片"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            plate_iv.setImageResource(R.drawable.post_add_pic);
                            plate_str = null;
                        }
                    });
                    builder.create().show();
                }

                return false;
            }
        });



         /*长按取消已经选中的车辆正面照*/
        vehicle_front_iv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!TextUtils.isEmpty(vehicle_front_str)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setItems(new String[]{"删除照片"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            vehicle_front_iv.setImageResource(R.drawable.post_add_pic);
                            vehicle_front_str = null;
                        }
                    });
                    builder.create().show();
                }

                return false;
            }
        });



        /*长按取消已经选中的车辆正面照1*/
        vehicle_side_one_iv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!TextUtils.isEmpty(vehicle_side_one_str)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setItems(new String[]{"删除照片"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            vehicle_side_one_iv.setImageResource(R.drawable.post_add_pic);
                            vehicle_side_one_str = null;
                        }
                    });
                    builder.create().show();
                }

                return false;
            }
        });





         /*长按取消已经选中的车辆正面照1*/
        vehicle_side_two_iv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!TextUtils.isEmpty(vehicle_side_two_str)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setItems(new String[]{"删除照片"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            vehicle_side_two_iv.setImageResource(R.drawable.post_add_pic);
                            vehicle_side_two_str = null;
                        }
                    });
                    builder.create().show();
                }

                return false;
            }
        });


    }


    private void changeView(int flag) {
        if (flag == 2) { //人员绑定
            ptype_ll.setVisibility(View.VISIBLE);
            guardian_cardid_ll.setVisibility(View.VISIBLE);
            tag_person_title.setText("标签和人员信息");
            uname_tip_tv.setText(Html.fromHtml("<font color=red>*</font>姓名："));
            uphone_tip_tv.setText(Html.fromHtml("<font color=red>*</font>手机号码："));
            car_info_ll.setVisibility(View.GONE);
            center_pic_ll.setVisibility(View.GONE);
            vehicle_side_ll.setVisibility(View.GONE);

        } else { //车辆绑定
            ptype_ll.setVisibility(View.GONE);
            guardian_cardid_ll.setVisibility(View.GONE);
            tag_person_title.setText("标签和车主信息");
            uname_tip_tv.setText(Html.fromHtml("<font color=red>*</font>车主姓名："));
            uphone_tip_tv.setText(Html.fromHtml("<font color=red>*</font>车主手机号码："));
            car_info_ll.setVisibility(View.VISIBLE);
            center_pic_ll.setVisibility(View.VISIBLE);
            vehicle_side_ll.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {

        switch (timeDialogFlag) {
            case 1:
                ubirthday_et.setText(getDateToString(millseconds, timeDialogFlag));
                break;
            case 2:
                cbuytime_et.setText(getDateToString(millseconds, timeDialogFlag));
                break;
        }
    }


    private void checkTag() {
        serial = serial_et.getText().toString().trim();
        if (TextUtils.isEmpty(serial)) {
            return;
        }


        Map<String, String> params = new HashMap<String, String>();
        params.put("lno", serial);
        params.put("orgids", user.getOrgids());

        OkGo.post(Path.CHECK_TAG_PATH).tag(this).params(params).execute(new StringComCallback() {
            @Override
            public void onSuccess(String result, Call call, Response response) {
                if (!TextUtils.isEmpty(result)) {
                    JsonResult jsonResult = new Gson().fromJson(result, new TypeToken<JsonResult>() {
                    }.getType());
                    if (jsonResult != null && jsonResult.isFlag() && jsonResult.getStatusCode() == 200) {
                        tagIsOk = true;
                        serial_et.setTextColor(Color.parseColor("#297921"));
                        if (isUploadData) {
                            isUploadData = false;
                            uploadData();
                        }
                    } else if (jsonResult != null && jsonResult.getStatusCode() == 400) { //不存在
                        serial_et.setTextColor(Color.RED);
                        serial_et.setText(serial);
                        serial_et.append("(不存在)");
                        tagIsOk = false;
                        tagNeedCheck = false;

                        if (isUploadData) {
                            isUploadData = false;
                        }
                    } else if (jsonResult != null && jsonResult.getStatusCode() == 300) { //已绑定
                        serial_et.setTextColor(Color.RED);
                        serial_et.setText(serial);
                        serial_et.append("(已被使用)");
                        tagIsOk = false;
                        tagNeedCheck = false;

                        if (isUploadData) {
                            isUploadData = false;
                        }
                    } else {
                        tagIsOk = false;
                        tagNeedCheck = false;
                        serial_et.setTextColor(Color.RED);
                        serial_et.setText(serial);
                        serial_et.append("(校验失败)");

                        if (isUploadData) {
                            isUploadData = false;
                        }
                    }
                } else {
                    tagIsOk = false;
                    tagNeedCheck = false;
                    serial_et.setTextColor(Color.RED);
                    serial_et.setText(serial);
                    serial_et.append("(校验失败)");

                    if (isUploadData) {
                        isUploadData = false;
                    }
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                tagIsOk = false;
                tagNeedCheck = false;
                serial_et.setTextColor(Color.RED);
                serial_et.setText(serial);
                serial_et.append("(校验失败)");

                if (isUploadData) {
                    isUploadData = false;
                }
            }
        });


    }


    public String getDateToString(long time, int flag) {

        Date d = new Date(time);
        SimpleDateFormat sf = null;
        if (flag == 1) {
            sf = new SimpleDateFormat("yyyy-MM-dd");
        } else { //flag为2（购买时间）或者其他
            sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        return sf.format(d);
    }


    private void activeOCR() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("权限激活");
        final EditText editText = new EditText(context);
        LinearLayout ll = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = Tools.dip2px(context, 20);
        ll.addView(editText, layoutParams);

        editText.setHint("请输入序列号");
        builder.setView(ll);

        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("激活", null);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show(); //先show，这种情况下如果用户未输入文件名，则对话框不会消失

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sn = editText.getText().toString().trim();
                if (TextUtils.isEmpty(sn)) {
                    Toast.makeText(context, "请输入序列号", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent authIntent = new Intent(BindCarAct.this, AuthService.class);
                bindService(authIntent, authConn, Service.BIND_AUTO_CREATE);
                alertDialog.dismiss();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_ib:
                cancleInputTip();
                break;

            case R.id.upload_btn:
                upload();
                break;

            case R.id.ucardid_iv: //身份证正面照
                guardian_idcard_et.clearFocus();
                serial_et.clearFocus();
                choose_pic_type = 1;
                showPopupwindow(v);
                break;

            case R.id.ucardid_opposite_iv:
                serial_et.clearFocus();
                guardian_idcard_et.clearFocus();
                choose_pic_type = 2;
                showPopupwindow(v);
                break;

            case R.id.plate_iv:
                serial_et.clearFocus();
                choose_pic_type = 3;
                showPopupwindow(v);
                break;

            case R.id.vehicle_front_iv:
                serial_et.clearFocus();
                choose_pic_type = 4;
                showPopupwindow(v);
                break;

            case R.id.vehicle_side_one_iv:
                serial_et.clearFocus();
                choose_pic_type = 5;
                showPopupwindow(v);
                break;

            case R.id.vehicle_side_two_iv:
                serial_et.clearFocus();
                choose_pic_type = 6;
                showPopupwindow(v);
                break;

            case R.id.ubirthday_et: //出生日期
                serial_et.clearFocus();
                guardian_idcard_et.clearFocus();
                if (!mDialogAll.isAdded()) { //判断是否已经添加，避免因重复添加导致异常
                    mDialogAll.show(getSupportFragmentManager(), "all");
                }
                timeDialogFlag = 1;
                break;

            case R.id.cbuytime_et: //购买时间
                serial_et.clearFocus();
                guardian_idcard_et.clearFocus();
                if (!mDialogAll.isAdded()) {
                    mDialogAll.show(getSupportFragmentManager(), "all");
                }
                timeDialogFlag = 2;
                break;

            case R.id.upolicestation_et: //选择组织机构
                serial_et.clearFocus();
                startActivityForResult(new Intent(context, SelectAct.class).putExtra("selectFlag", Constant.SELECT_POLICE_ACT), 1023);
                break;


            case R.id.ocr_idcard_tv: //ocr识别身份证：车主身份证
                if (!Utils.isFastClick()) {
                    return;
                }
                ocr_idcard_type = 1;
                serial_et.clearFocus();
                guardian_idcard_et.clearFocus();
                Intent authIntent = new Intent(BindCarAct.this, AuthService.class);
                bindService(authIntent, authConn, Service.BIND_AUTO_CREATE);

                //Toast.makeText(context, "正在为您跳转到识别界面...", Toast.LENGTH_SHORT).show();

                break;


            case R.id.ocr_guardian_idcard_tv: //ocr识别身份证:监护人身份证
                ocr_idcard_type = 2;
                serial_et.clearFocus();
                guardian_idcard_et.clearFocus();
                authIntent = new Intent(BindCarAct.this, AuthService.class);
                bindService(authIntent, authConn, Service.BIND_AUTO_CREATE);

                //Toast.makeText(context, "正在为您跳转到识别界面...", Toast.LENGTH_SHORT).show();

                break;


            case R.id.ocr_cnumber_tv: //车牌识别
                serial_et.clearFocus();
                guardian_idcard_et.clearFocus();


                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setItems(new String[]{"电动车车牌识别", "汽车车牌识别"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                final Runnable mTasks = new Runnable() {
                                    public void run() {
                                        startActivityForResult(new Intent(context, CameraCarNumAct.class), 6613);
                                    }
                                };
                                progressDialog = ProgressDialog.show(BindCarAct.this, "",
                                        "正在跳转到识别界面…", true, false);

                                new Thread() {
                                    public void run() {
                                        try {
                                            sleep(1000);// 设置休眠时间
                                            handler.post(mTasks);// 发送
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();
                                break;


                            case 1:
                                final Runnable mTasksTwo = new Runnable() {
                                    public void run() {
//                                        startActivityForResult(new Intent(BindCarAct.this, CameraActivity.class), 2323);
                                    }
                                };


                                progressDialog = ProgressDialog.show(BindCarAct.this, "",
                                        "正在跳转到识别界面…", true, false);
                                new Thread() {
                                    public void run() {
                                        try {
                                            sleep(1000);// 设置休眠时间
                                            handler.post(mTasksTwo);// 发送
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();
                                break;
                        }
                    }
                });

                builder.create().show();


                break;


            case R.id.gender_et: //选择性别
                serial_et.clearFocus();
                guardian_idcard_et.clearFocus();
                if (genderWindow == null) {
                    mView = LayoutInflater.from(context).inflate(
                            R.layout.ppw_modify_image, null);
                    mView.setFocusable(true); // 这个很重要
                    mView.setFocusableInTouchMode(true);

                    choose_male_rl = (RelativeLayout) mView.findViewById(R.id.rlyt_upload_photo);
                    choose_female_rl = (RelativeLayout) mView.findViewById(R.id.rlyt_upload_album);

                    TextView male_tv = (TextView) mView.findViewById(R.id.tv_01);
                    TextView female_tv = (TextView) mView.findViewById(R.id.tv_02);
                    male_tv.setText("男");
                    female_tv.setText("女");

                    genderWindow = new PopupWindow(mView, 700, ViewGroup.LayoutParams.WRAP_CONTENT); //高度设为自适应
                }

                genderWindow.setAnimationStyle(R.style.popwin_anim_style);
                genderWindow.setFocusable(true);
                genderWindow.setBackgroundDrawable(new BitmapDrawable());
                backgroundAlpha(0.5f);
                // 添加pop窗口关闭事件
                genderWindow.setOnDismissListener(new popupDismissListener());
                // 重写onKeyListener
                mView.setOnKeyListener(new View.OnKeyListener() {
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            genderWindow.dismiss();
                            backgroundAlpha(1f);
                            return true;
                        }
                        return false;
                    }
                });
                genderWindow.update();

                genderWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

                choose_male_rl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gender_et.setText("男");
                        genderWindow.dismiss();
                        backgroundAlpha(1.0f);
                    }
                });

                choose_female_rl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gender_et.setText("女");
                        genderWindow.dismiss();
                        backgroundAlpha(1.0f);
                    }
                });
                break;


            case R.id.ptype_et: //选择人员类型
                serial_et.clearFocus();
                guardian_idcard_et.clearFocus();
                startActivityForResult(new Intent(context, SelectAct.class).putExtra("selectFlag", Constant.SELECT_PTYPE_ACT), 1024);
                break;


            case R.id.scan_barcode_tv: //扫条形码
                /*Intent i = new Intent(context,HandlePolicyInfoAct.class);
                i.putExtra("from",2146);
                i.putExtra("tagid",serial_et.getText().toString().trim());
                i.putExtra("name",uname_et.getText().toString().trim());
                i.putExtra("phone",uphone_et.getText().toString().trim());
                i.putExtra("idcard",ucardid_et.getText().toString().trim());
                i.putExtra("grade",cnum_et.getText().toString().trim());
                i.putExtra("model",ctype_et.getText().toString().trim());
                i.putExtra("motor",cdynamo_et.getText().toString().trim());
                i.putExtra("price",cprice_et.getText().toString().trim());
                i.putExtra("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                startActivity(i);*/

                serial_et.clearFocus();
                guardian_idcard_et.clearFocus();
                startActivityForResult(new Intent(context, BarCodeScanAct.class), 1666);
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        closeProgressDialog();
    }

    public static void closeProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

    }


    private void clearText() {
        serial_et.setText("");
        uname_et.setText("");
        ucardid_et.setText("");
        uphone_et.setText("");
        gender_et.setText("");
        ubirthday_et.setText("");
        uresidence_et.setText("");
        upolicestation_et.setText("");
        uworkaddress_et.setText("");
        uurgencyname_et.setText("");
        uurgencyphone_et.setText("");
        cnum_et.setText("");
        cframe_et.setText("");
        cprice_et.setText("");
        cbuytime_et.setText("");
        ctype_et.setText("");
        cdynamo_et.setText("");
        remark_et.setText("");

        ucardid_iv.setImageResource(R.drawable.post_add_pic);
        ucardid_pic_str = null;

        ucardid_opposite_iv.setImageResource(R.drawable.post_add_pic);
        ucardid_opposite_str = null;

        plate_iv.setImageResource(R.drawable.post_add_pic);
        plate_str = null;

        vehicle_front_iv.setImageResource(R.drawable.post_add_pic);
        vehicle_front_str = null;


        vehicle_side_one_iv.setImageResource(R.drawable.post_add_pic);
        vehicle_side_one_str = null;

        vehicle_side_two_iv.setImageResource(R.drawable.post_add_pic);
        vehicle_side_two_str = null;

    }


    private void upload() {
        if (user == null) {
            isUploadData = false;
            serial_et.clearFocus();
            guardian_idcard_et.clearFocus();
            Toast.makeText(context, "请先登录，再上传数据", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(serial_et.getText().toString().trim())) { //标签为空
            isUploadData = false;
            serial_et.clearFocus();
            guardian_idcard_et.clearFocus();
            Toast.makeText(context, "绑定标签号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }


        if (TextUtils.isEmpty(uname_et.getText().toString().trim())) { //标签为空
            isUploadData = false;
            serial_et.clearFocus();
            guardian_idcard_et.clearFocus();
            Toast.makeText(context, bindFlag == TYPE_BIND_CAR ? "车主姓名不能为空" : "姓名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }


        if (TextUtils.isEmpty(ucardid_et.getText().toString().trim())) { //身份证号为空
            isUploadData = false;
            serial_et.clearFocus();
            guardian_idcard_et.clearFocus();
            Toast.makeText(context, "身份证号码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }


        if (TextUtils.isEmpty(uphone_et.getText().toString().trim())) { //手机为空
            isUploadData = false;
            serial_et.clearFocus();
            guardian_idcard_et.clearFocus();
            Toast.makeText(context, bindFlag == TYPE_BIND_CAR ? "车主手机号码不能为空" : "手机号码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }


        if (TextUtils.isEmpty(gender_et.getText().toString().trim())) { //性别为空
            isUploadData = false;
            serial_et.clearFocus();
            guardian_idcard_et.clearFocus();
            Toast.makeText(context, "性别不能为空", Toast.LENGTH_SHORT).show();
            return;
        }


        if (TextUtils.isEmpty(uresidence_et.getText().toString().trim())) { //现居地址为空
            isUploadData = false;
            serial_et.clearFocus();
            guardian_idcard_et.clearFocus();
            Toast.makeText(context, "现居地址不能为空", Toast.LENGTH_SHORT).show();
            return;
        }


        if (!Tools.isMobileNO(uphone_et.getText().toString().trim())) {
            isUploadData = false;
            serial_et.clearFocus();
            guardian_idcard_et.clearFocus();
            Toast.makeText(context, bindFlag == TYPE_BIND_CAR ? "车主手机号码格式不正确" : "手机号码格式不正确", Toast.LENGTH_SHORT).show();
            return;
        }


        if (TextUtils.isEmpty(upolicestation_et.getText().toString().trim())) { //组织机构为空
            isUploadData = false;
            serial_et.clearFocus();
            guardian_idcard_et.clearFocus();
            Toast.makeText(context, "组织机构不能为空", Toast.LENGTH_SHORT).show();
            return;
        }


        /*if (!Tools.personIdValidation(ucardid_et.getText().toString().trim())) {
            isUploadData = false;
            serial_et.clearFocus();
            guardian_idcard_et.clearFocus();
            Toast.makeText(context, "请填写正确的身份证号码", Toast.LENGTH_SHORT).show();
            return;
        }*/


        if (TextUtils.isEmpty(uurgencyname_et.getText().toString().trim())) {
            isUploadData = false;
            serial_et.clearFocus();
            guardian_idcard_et.clearFocus();
            Toast.makeText(context, "紧急联系人不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(uurgencyphone_et.getText().toString().trim())) {
            isUploadData = false;
            serial_et.clearFocus();
            guardian_idcard_et.clearFocus();
            Toast.makeText(context, "紧急联系人电话不能为空", Toast.LENGTH_SHORT).show();
            return;
        }


       /* if(bindFlag == TYPE_BIND_PEOPLE && TextUtils.isEmpty(guardian_idcard_et.getText().toString().trim())){
            isUploadData = false;
            serial_et.clearFocus();
            guardian_idcard_et.clearFocus();
            Toast.makeText(context, "监护人身份证号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }*/


        if (bindFlag == TYPE_BIND_CAR) {
            if (TextUtils.isEmpty(cnum_et.getText().toString().trim())) {
                isUploadData = false;
                serial_et.clearFocus();
                Toast.makeText(context, "车牌号不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(cbuytime_et.getText().toString().trim())) {
                isUploadData = false;
                serial_et.clearFocus();
                Toast.makeText(context, "购买时间不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
        } else if (bindFlag == TYPE_BIND_PEOPLE) {
            if (TextUtils.isEmpty(ptype_et.getText().toString().trim())) {
                isUploadData = false;
                serial_et.clearFocus();
                guardian_idcard_et.clearFocus();
                Toast.makeText(context, "人员类型不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
        }


        if (!TextUtils.isEmpty(uurgencyphone_et.getText().toString().trim()) && !Tools.isMobileNO(uurgencyphone_et.getText().toString().trim())) {
            isUploadData = false;
            serial_et.clearFocus();
            guardian_idcard_et.clearFocus();
            Toast.makeText(context, "紧急联系人电话格式不正确", Toast.LENGTH_SHORT).show();
            return;
        }


        if (TextUtils.isEmpty(ucardid_pic_str)) {
            Toast.makeText(context, "身份证正面照不能为空", Toast.LENGTH_SHORT).show();
            return;
        }


        if (TextUtils.isEmpty(ucardid_opposite_str)) {
            Toast.makeText(context, "身份证反面照不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (bindFlag == TYPE_BIND_CAR) {
            if (TextUtils.isEmpty(plate_str)) {
                Toast.makeText(context, "车辆正面照不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(vehicle_front_str)) {
                Toast.makeText(context, "车辆侧面照不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(vehicle_side_one_str)) {
                Toast.makeText(context, "车牌照不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(vehicle_side_two_str)) {
                Toast.makeText(context, "车辆保单照不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
        }


        isUploadData = true;
        serial_et.clearFocus();


        if (!TextUtils.isEmpty(guardian_idcard_et.getText().toString().trim())) {
            if (bindFlag == TYPE_BIND_PEOPLE) {
                guardian_idcard_et.clearFocus();

                if (!idcardIsOk) {
                    if (!idcardNeedCheck) {
                        isUploadData = false;
                        return;
                    }
                    checkGuardian();
                    return;
                }
            }

        }


        if (!tagIsOk) {

            if (!tagNeedCheck) {
                isUploadData = false;
                return;
            }

            checkTag();
            return;
        }

        isUploadData = false;
        uploadData();


    }


    private void uploadData() {


        Map<String, String> params = new HashMap<String, String>();

        User carUser = new User();

        carUser.setUsername(user.getUsername());
        carUser.setPassword(user.getPassword());
        carUser.setOrgid(orgid);
        if (bindFlag == TYPE_BIND_CAR) {
            carUser.setPtype("1");//车主
        } else if (bindFlag == TYPE_BIND_PEOPLE) {
            carUser.setPtype(ptype);
            carUser.setLabelid(serial_et.getText().toString().trim());
            carUser.setGuardian(pid);
        }

        carUser.setApproleid(user.getApproleid());
        carUser.setStatus(user.getStatus());
        carUser.setSex(gender_et.getText().toString().trim().equals("女") ? "1" : "0"); //性别
        carUser.setBirthday(ubirthday_et.getText().toString().trim()); //生日

        //输入
        StringBuilder input = new StringBuilder(ucardid_et.getText().toString().trim());
        //预先定义一个18位0
        StringBuilder zero = new StringBuilder("000000000000000000");
        String idCardString = zero.substring(0, zero.length() - input.length()) + input;
        carUser.setIdcard(idCardString); //身份证号

        carUser.setPname(uname_et.getText().toString().trim()); //车主姓名
        carUser.setAddress(uresidence_et.getText().toString().trim()); //现居地址
        carUser.setUnit(uworkaddress_et.getText().toString().trim());//工作单位
        carUser.setOrgname(upolicestation_et.getText().toString().trim()); //组织机构
        carUser.setMobile(uphone_et.getText().toString().trim()); //手机号码
        carUser.setUrgencyname(uurgencyname_et.getText().toString().trim()); //紧急联系人姓名
        carUser.setUrgencymobile(uurgencyphone_et.getText().toString().trim()); //紧急联系人号码


        String idCardImgBase64 = null, idCardOppositeImgBase64 = null, plateImgBase64 = null, vehicleFrontImgBase64 = null,
                vehicleSideOneImgBase64 = null, vehicleSideTwoImgBase64 = null;

        if (!TextUtils.isEmpty(ucardid_pic_str)) {//身份证照片
            try {
                InputStream is = new FileInputStream(ucardid_pic_str);
                byte[] picData = new byte[is.available()];
                is.read(picData);
                is.close();
                BASE64Encoder encoder = new BASE64Encoder();
                idCardImgBase64 = encoder.encode(picData);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!TextUtils.isEmpty(ucardid_opposite_str)) { //行驶证照片
            try {
                InputStream is = new FileInputStream(ucardid_opposite_str);
                byte[] picData = new byte[is.available()];
                is.read(picData);
                is.close();
                BASE64Encoder encoder = new BASE64Encoder();
                idCardOppositeImgBase64 = encoder.encode(picData);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if (!TextUtils.isEmpty(plate_str)) { //车辆照片
            try {
                InputStream is = new FileInputStream(plate_str);
                byte[] picData = new byte[is.available()];
                is.read(picData);
                is.close();
                BASE64Encoder encoder = new BASE64Encoder();
                plateImgBase64 = encoder.encode(picData);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!TextUtils.isEmpty(vehicle_front_str)) { //车牌照片
            try {
                InputStream is = new FileInputStream(vehicle_front_str);
                byte[] picData = new byte[is.available()];
                is.read(picData);
                is.close();
                BASE64Encoder encoder = new BASE64Encoder();
                vehicleFrontImgBase64 = encoder.encode(picData);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if (!TextUtils.isEmpty(vehicle_side_one_str)) { //车牌照片
            try {
                InputStream is = new FileInputStream(vehicle_side_one_str);
                byte[] picData = new byte[is.available()];
                is.read(picData);
                is.close();
                BASE64Encoder encoder = new BASE64Encoder();
                vehicleSideOneImgBase64 = encoder.encode(picData);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if (!TextUtils.isEmpty(vehicle_side_two_str)) { //车牌照片
            try {
                InputStream is = new FileInputStream(vehicle_side_two_str);
                byte[] picData = new byte[is.available()];
                is.read(picData);
                is.close();
                BASE64Encoder encoder = new BASE64Encoder();
                vehicleSideTwoImgBase64 = encoder.encode(picData);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        //6张图片
        if (bindFlag == TYPE_BIND_CAR) {
            carUser.setIdcardphotourl(idCardImgBase64 + "," + idCardOppositeImgBase64);
        } else if (bindFlag == TYPE_BIND_PEOPLE) {
            carUser.setIdcardphotourl(idCardImgBase64 + "," + idCardOppositeImgBase64);
        }


        params.put("jsonuser", new Gson().toJson(carUser));


        if (bindFlag == TYPE_BIND_CAR) { //车辆绑定需要增加车辆信息
            Car car = new Car();
            car.setLabelid(serial_et.getText().toString().trim()); //可用标签号对应主键
            car.setOrgid(orgid);
            car.setCno(cnum_et.getText().toString().trim()); //车牌号
            car.setCframe(cframe_et.getText().toString().trim()); //车架号
            car.setCbuyprice(TextUtils.isEmpty(cprice_et.getText().toString().trim()) ? "0" : cprice_et.getText().toString().trim()); //购买价格
            car.setCbuytype(ctype_et.getText().toString().trim()); //车品牌
            car.setCbuytime(cbuytime_et.getText().toString().trim()); //购买时间
            car.setCdevice(cdynamo_et.getText().toString().trim()); //发动机号
            car.setRemark(remark_et.getText().toString().trim()); //备注

            car.setCcarpicurl(plateImgBase64 + "," + vehicleFrontImgBase64 + "," + vehicleSideOneImgBase64 + "," + vehicleSideTwoImgBase64);

            params.put("jsoncar", new Gson().toJson(car));
        }


        OkGo.post(bindFlag == TYPE_BIND_CAR ? Path.UPLOAD_CAR_INFO_PATH : Path.UPLOAD_PEOPLE_INFO_PATH).tag(this).params(params).execute(new StringDialogCallback(BindCarAct.this, "正在上传...") {
            @Override
            public void onSuccess(String result, Call call, Response response) {
                if (!TextUtils.isEmpty(result)) {
                    JsonResult jr = new Gson().fromJson(result, new TypeToken<JsonResult>() {
                    }.getType());
                    if (jr != null && jr.isFlag() && jr.getStatusCode() == 200) {
                        Toast.makeText(context, "上传成功", Toast.LENGTH_SHORT).show();

                    } else if (jr != null && !jr.isFlag() && jr.getStatusCode() == MSG_REPEAT_BIND) {
                        Toast.makeText(context, "此身份证号码已绑定标签，无法重复绑定", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "上传失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "上传失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                Toast.makeText(context, "上传失败", Toast.LENGTH_SHORT).show();
            }

        });


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
                    if (ActivityCompat.shouldShowRequestPermissionRationale(BindCarAct.this, Manifest.permission.CAMERA)) {
                        //已经禁止提示了
                        Toast.makeText(context, "您已禁止该权限，需要重新开启", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        ActivityCompat.requestPermissions(BindCarAct.this, new String[]{Manifest.permission.CAMERA}, PS_CAMERA_REQ);

                    }

                } else {

                    Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd_hhmmss");
                    Uri uri = null;
                    switch (choose_pic_type) {
                        case 1:
                            ucardid_pic_str = sDateFormat.format(new java.util.Date());
                            ucardid_pic_str = Path.IMAGE_TEMP_FILE_PATH + ucardid_pic_str + ".jpg"; //先放入临时文件夹
                            // 加载路径
                            uri = Uri.fromFile(new File(ucardid_pic_str));
                            break;

                        case 2:
                            ucardid_opposite_str = sDateFormat.format(new java.util.Date());
                            ucardid_opposite_str = Path.IMAGE_TEMP_FILE_PATH + ucardid_opposite_str + ".jpg"; //先放入临时文件夹
                            // 加载路径
                            uri = Uri.fromFile(new File(ucardid_opposite_str));
                            break;

                        case 3:
                            plate_str = sDateFormat.format(new java.util.Date());
                            plate_str = Path.IMAGE_TEMP_FILE_PATH + plate_str + ".jpg"; //先放入临时文件夹
                            uri = Uri.fromFile(new File(plate_str));
                            break;

                        case 4:
                            vehicle_front_str = sDateFormat.format(new java.util.Date());
                            vehicle_front_str = Path.IMAGE_TEMP_FILE_PATH + vehicle_front_str + ".jpg"; //先放入临时文件夹
                            uri = Uri.fromFile(new File(vehicle_front_str));
                            break;

                        case 5:
                            vehicle_side_one_str = sDateFormat.format(new java.util.Date());
                            vehicle_side_one_str = Path.IMAGE_TEMP_FILE_PATH + vehicle_side_one_str + ".jpg";
                            uri = Uri.fromFile(new File(vehicle_side_one_str));
                            break;

                        case 6:
                            vehicle_side_two_str = sDateFormat.format(new java.util.Date());
                            vehicle_side_two_str = Path.IMAGE_TEMP_FILE_PATH + vehicle_side_two_str + ".jpg";
                            uri = Uri.fromFile(new File(vehicle_side_two_str));
                            break;

                        default:
                            break;
                    }


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
                    if (ActivityCompat.shouldShowRequestPermissionRationale(BindCarAct.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        //已经禁止提示了
                        Toast.makeText(context, "您已禁止该权限，需要重新开启", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        ActivityCompat.requestPermissions(BindCarAct.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PS_STORAGE_REQ);

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

                    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd_hhmmss");
                    Uri uri = null;
                    switch (choose_pic_type) {
                        case 1:
                            ucardid_pic_str = sDateFormat.format(new java.util.Date());
                            ucardid_pic_str = Path.IMAGE_TEMP_FILE_PATH + ucardid_pic_str + ".jpg"; //先放入临时文件夹
                            // 加载路径
                            uri = Uri.fromFile(new File(ucardid_pic_str));
                            break;

                        case 2:
                            ucardid_opposite_str = sDateFormat.format(new java.util.Date());
                            ucardid_opposite_str = Path.IMAGE_TEMP_FILE_PATH + ucardid_opposite_str + ".jpg"; //先放入临时文件夹
                            // 加载路径
                            uri = Uri.fromFile(new File(ucardid_opposite_str));
                            break;

                        case 3:
                            plate_str = sDateFormat.format(new java.util.Date());
                            plate_str = Path.IMAGE_TEMP_FILE_PATH + plate_str + ".jpg"; //先放入临时文件夹
                            uri = Uri.fromFile(new File(plate_str));
                            break;

                        case 4:
                            vehicle_front_str = sDateFormat.format(new java.util.Date());
                            vehicle_front_str = Path.IMAGE_TEMP_FILE_PATH + vehicle_front_str + ".jpg"; //先放入临时文件夹
                            uri = Uri.fromFile(new File(vehicle_front_str));
                            break;

                        case 5:
                            vehicle_side_one_str = sDateFormat.format(new java.util.Date());
                            vehicle_side_one_str = Path.IMAGE_TEMP_FILE_PATH + vehicle_side_one_str + ".jpg"; //先放入临时文件夹
                            uri = Uri.fromFile(new File(vehicle_side_one_str));
                            break;

                        case 6:
                            vehicle_side_two_str = sDateFormat.format(new java.util.Date());
                            vehicle_side_two_str = Path.IMAGE_TEMP_FILE_PATH + vehicle_side_two_str + ".jpg"; //先放入临时文件夹
                            uri = Uri.fromFile(new File(vehicle_side_two_str));
                            break;

                        default:
                            break;
                    }

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


    private void checkGuardian() {

        guardian_idcard = guardian_idcard_et.getText().toString().trim();
        if (TextUtils.isEmpty(guardian_idcard)) {
            return;
        }


        Map<String, String> params = new HashMap<String, String>();
        params.put("idcard", guardian_idcard);

        OkGo.post(Path.CHECK_GUARDIAN_EXIST_PATH).tag(this).params(params).execute(new StringComCallback() {
            @Override
            public void onSuccess(String result, Call call, Response response) {
                if (!TextUtils.isEmpty(result)) {
                    JsonResult jr = new Gson().fromJson(result, new TypeToken<JsonResult>() {
                    }.getType());
                    if (jr != null && jr.isFlag() && jr.getStatusCode() == 200) {
                        String str = jr.getResult();
                        if (!TextUtils.isEmpty(str)) {
                            pid = new Gson().fromJson(str, new TypeToken<String>() {
                            }.getType());
                            idcardIsOk = true;
                            guardian_idcard_et.setTextColor(Color.parseColor("#297921"));
                        } else {
                            guardian_idcard_et.setTextColor(Color.RED);
                            guardian_idcard_et.setText(guardian_idcard);
                            idcardIsOk = false;
                            idcardNeedCheck = false;

                            if (isUploadData) {
                                isUploadData = false;
                            }

                            Toast.makeText(context, "监护人身份证号不存在", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (jr.getStatusCode() == 400) { //不存在
                            guardian_idcard_et.setTextColor(Color.RED);
                            guardian_idcard_et.setText(guardian_idcard);
                            idcardIsOk = false;
                            idcardNeedCheck = false;

                            if (isUploadData) {
                                isUploadData = false;
                            }

                            Toast.makeText(context, "监护人身份证号不存在", Toast.LENGTH_SHORT).show();
                        } else {
                            idcardIsOk = false;
                            idcardNeedCheck = false;
                            guardian_idcard_et.setTextColor(Color.RED);
                            guardian_idcard_et.setText(guardian_idcard);

                            Toast.makeText(context, "监护人身份证号校验失败", Toast.LENGTH_SHORT).show();

                            if (isUploadData) {
                                isUploadData = false;
                            }
                        }

                    }
                } else {
                    idcardIsOk = false;
                    idcardNeedCheck = false;
                    guardian_idcard_et.setTextColor(Color.RED);
                    guardian_idcard_et.setText(guardian_idcard);

                    Toast.makeText(context, "监护人身份证号校验失败", Toast.LENGTH_SHORT).show();

                    if (isUploadData) {
                        isUploadData = false;
                    }
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                idcardIsOk = false;
                idcardNeedCheck = false;
                guardian_idcard_et.setTextColor(Color.RED);
                guardian_idcard_et.setText(guardian_idcard);

                Toast.makeText(context, "监护人身份证号校验失败", Toast.LENGTH_SHORT).show();

                if (isUploadData) {
                    isUploadData = false;
                }
            }

        });


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

        ///////////////////////////////////////////////////////////////////
        if (requestCode == 1021 && resultCode == 1022) { //选择标签号结果返回
            String lidStr = data.getStringExtra("lid");
            String tagId = data.getStringExtra("tagId");
            if (!TextUtils.isEmpty(tagId)) {
                serial_et.setText(tagId);
                lid = lidStr;
            }
            return;
        }


        if (requestCode == 1666 && resultCode == 1667) {
            String scanResult = data.getStringExtra("scanResult");
            serial_et.setText(scanResult);
            serial_et.setTextColor(Color.BLACK);
            checkTag();
            return;
        }


        if (requestCode == 1314 && resultCode == 4131) {
            SweetAlertDialog continueDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
            continueDialog.setTitleText("上传成功！是否继续上传？");
            continueDialog.showCancelButton(true).setCancelText("否");
            continueDialog.setConfirmText("是");
            continueDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    clearText();
                    sweetAlertDialog.dismiss();
                }
            });

            continueDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismiss();
                    finish();
                }
            });

            continueDialog.show();
            return;
        }


        if (requestCode == 1166 && resultCode == 6612) {
            String cardid = data.getStringExtra("cardid");
            guardian_idcard_et.setText(cardid);
            return;
        }


        if (requestCode == 6613 && resultCode == 6614) {
            String cnum = data.getStringExtra("cnum");
            cnum_et.setText(cnum);
            return;
        }


        if (requestCode == 2323 && resultCode == 2324) {
            cnum_et.setText(data.getStringExtra("plate"));
            return;
        }


        if (resultCode == 6612) {
            if (requestCode == 6611) {
                String cardid = data.getStringExtra("cardid");
                String name = data.getStringExtra("name");
                String gender = data.getStringExtra("gender");
                String birth = data.getStringExtra("birth");
                String address = data.getStringExtra("address");
                if (!TextUtils.isEmpty(cardid)) {
                    ucardid_et.setText(cardid);
                }

                if (!TextUtils.isEmpty(name)) {
                    uname_et.setText(name);
                }

                if (!TextUtils.isEmpty(gender)) {
                    gender_et.setText(gender);
                }

                if (!TextUtils.isEmpty(birth)) {
                    ubirthday_et.setText(birth);
                }

                if (!TextUtils.isEmpty(address)) {
                    uresidence_et.setText(address);
                }
            }

            if (requestCode == 6161) {
                String cardid = data.getStringExtra("cardid");
                guardian_idcard_et.setText(cardid);
            }


            return;

        }

        if (requestCode == 1023 && resultCode == 1024) {
            orgid = data.getStringExtra("orgid");
            String orgname = data.getStringExtra("orgname");
            if (!TextUtils.isEmpty(orgname)) {
                upolicestation_et.setText(orgname);
            }
            return;
        }

        if (requestCode == 1024 && resultCode == 1035) {
            ptype = data.getStringExtra("ptype");
            String pTypeString = "";
            switch (ptype) {
                case "2":
                    pTypeString = "患者";
                    break;

                case "3":
                    pTypeString = "老人";
                    break;

                case "4":
                    pTypeString = "学生";
                    break;

                case "5":
                    pTypeString = "易走失人员";
                    break;

                case "6":
                    pTypeString = "警务人员";
                    break;

                case "7":
                    pTypeString = "保安人员";
                    break;

                case "8":
                    pTypeString = "协警人员";
                    break;

            }

            ptype_et.setText(pTypeString);

            return;
        }


        if (requestCode == 2323 && resultCode == 2324) {
            cnum_et.setText(data.getStringExtra("plate"));
            return;
        }

        //////////////////////////////////////////////////////////////////

        // The Return of Activity
        if (resultCode != RESULT_OK) {// Return ERROR
            //showToast(R.string.photo_toast_null);
            return;
        }

        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            Toast.makeText(context, "SD卡不可用", Toast.LENGTH_SHORT).show();
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
                            picturePath = uri.getPath();
                        }

                        try {
                            // Get the result data
                            File picFile = new File(picturePath);
                            Bitmap bitmap1 = null;
                            Bitmap bitmap = null;
                            if (FileOperateUtils.getFileSize(picFile) > Constant.PIC_SIZE_LIMIT) { //如果大于限定大小，压缩位图
                                bitmap1 = ImageUtils.compressImageFromFile(picturePath);
                            } else {
                                bitmap1 = BitmapFactory.decodeFile(picturePath);
                            }

                            // Set the datetime as the name of new picture
                            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd_hhmmss");

                            switch (choose_pic_type) {
                                case 1:
                                    ucardid_pic_str = sDateFormat.format(new java.util.Date());
                                    ucardid_pic_str = Path.IMAGE_TEMP_FILE_PATH + ucardid_pic_str + ".jpg"; //先放到临时文件夹管理
                                    // Write the picture data to SD card.
                                    //将原始图片缩放成ImageView控件的高宽
                                    bitmap = ImageUtils.zoomBitmap(bitmap1, ucardid_iv.getWidth(), ucardid_iv.getHeight(), 1);


                                    ImageUtils.compressBmpToFile(bitmap, new File(ucardid_pic_str));

                                    ucardid_iv.setImageBitmap(bitmap);
                                    break;

                                case 2:
                                    ucardid_opposite_str = sDateFormat.format(new java.util.Date());
                                    ucardid_opposite_str = Path.IMAGE_TEMP_FILE_PATH + ucardid_opposite_str + ".jpg"; //先放到临时文件夹管理
                                    // Write the picture data to SD card.
                                    //将原始图片缩放成ImageView控件的高宽
                                    bitmap = ImageUtils.zoomBitmap(bitmap1, ucardid_opposite_iv.getWidth(), ucardid_opposite_iv.getHeight(), 1);

                                    ImageUtils.compressBmpToFile(bitmap, new File(ucardid_opposite_str));

                                    ucardid_opposite_iv.setImageBitmap(bitmap);
                                    break;

                                case 3:
                                    plate_str = sDateFormat.format(new java.util.Date());
                                    plate_str = Path.IMAGE_TEMP_FILE_PATH + plate_str + ".jpg"; //先放到临时文件夹管理
                                    // Write the picture data to SD card.
                                    //将原始图片缩放成ImageView控件的高宽
                                    bitmap = ImageUtils.zoomBitmap(bitmap1, plate_iv.getWidth(), plate_iv.getHeight(), 1);
                                    ImageUtils.compressBmpToFile(bitmap, new File(plate_str));

                                    plate_iv.setImageBitmap(bitmap);
                                    break;

                                case 4:
                                    vehicle_front_str = sDateFormat.format(new java.util.Date());
                                    vehicle_front_str = Path.IMAGE_TEMP_FILE_PATH + vehicle_front_str + ".jpg"; //先放到临时文件夹管理
                                    // Write the picture data to SD card.
                                    //将原始图片缩放成ImageView控件的高宽
                                    bitmap = ImageUtils.zoomBitmap(bitmap1, vehicle_front_iv.getWidth(), vehicle_front_iv.getHeight(), 1);
                                    ImageUtils.compressBmpToFile(bitmap, new File(vehicle_front_str));

                                    vehicle_front_iv.setImageBitmap(bitmap);
                                    break;

                                case 5:
                                    vehicle_side_one_str = sDateFormat.format(new java.util.Date());
                                    vehicle_side_one_str = Path.IMAGE_TEMP_FILE_PATH + vehicle_side_one_str + ".jpg"; //先放到临时文件夹管理
                                    // Write the picture data to SD card.
                                    //将原始图片缩放成ImageView控件的高宽
                                    bitmap = ImageUtils.zoomBitmap(bitmap1, vehicle_side_one_iv.getWidth(), vehicle_side_one_iv.getHeight(), 1);
                                    ImageUtils.compressBmpToFile(bitmap, new File(vehicle_side_one_str));

                                    vehicle_side_one_iv.setImageBitmap(bitmap);
                                    break;

                                case 6:
                                    vehicle_side_two_str = sDateFormat.format(new java.util.Date());
                                    vehicle_side_two_str = Path.IMAGE_TEMP_FILE_PATH + vehicle_side_two_str + ".jpg"; //先放到临时文件夹管理
                                    // Write the picture data to SD card.
                                    //将原始图片缩放成ImageView控件的高宽
                                    bitmap = ImageUtils.zoomBitmap(bitmap1, vehicle_side_two_iv.getWidth(), vehicle_side_two_iv.getHeight(), 1);
                                    ImageUtils.compressBmpToFile(bitmap, new File(vehicle_side_two_str));

                                    vehicle_side_two_iv.setImageBitmap(bitmap);
                                    break;

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case 2: //拍照
                    switch (choose_pic_type) {
                        case 1:
                            ucardid_pic_str = getPicUrl(ucardid_pic_str, ucardid_iv);
                            break;


                        case 2:
                            ucardid_opposite_str = getPicUrl(ucardid_opposite_str, ucardid_opposite_iv);
                            break;


                        case 3:
                            plate_str = getPicUrl(plate_str, plate_iv);

                            break;


                        case 4:
                            vehicle_front_str = getPicUrl(vehicle_front_str, vehicle_front_iv);

                            break;


                        case 5:
                            vehicle_side_one_str = getPicUrl(vehicle_side_one_str, vehicle_side_one_iv);
                            break;

                        case 6:
                            vehicle_side_two_str = getPicUrl(vehicle_side_two_str, vehicle_side_two_iv);

                            break;


                    }

                    break;

                default:
                    break;
            }
        }


    }

    private String getPicUrl(String picStr, ImageView imageView) {
        try {
            File fileName = new File(picStr);
            if (FileOperateUtils.getFileSize(fileName) > Constant.PIC_SIZE_LIMIT) {
                Bitmap newBitmap = ImageUtils.compressImageFromFile(picStr);//获取压缩后的bitmap
                if (newBitmap != null) {
                    if (fileName.exists()) {
                        fileName.delete(); //删掉原文件
                    }
                    picStr = new SimpleDateFormat("yyyyMMdd_hhmmss").format(new Date());
                    picStr = Path.IMAGE_TEMP_FILE_PATH + picStr + ".jpg";
                    //将原始图片缩放成ImageView控件的高宽
                    newBitmap = ImageUtils.zoomBitmap(newBitmap, imageView.getWidth(), imageView.getHeight(), 1);
                    //创建新文件
                    ImageUtils.compressBmpToFile(newBitmap, new File(picStr));
                }
                imageView.setImageBitmap(newBitmap);
            } else {
                FileInputStream is = new FileInputStream(picStr);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                imageView.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return picStr;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            cancleInputTip();
        }
        return false;
    }


    private void cancleInputTip() {
        if (!TextUtils.isEmpty(serial_et.getText().toString().trim()) ||
                !TextUtils.isEmpty(uname_et.getText().toString().trim()) ||
                !TextUtils.isEmpty(ucardid_et.getText().toString().trim()) ||
                !TextUtils.isEmpty(uphone_et.getText().toString().trim()) ||
                !TextUtils.isEmpty(gender_et.getText().toString().trim()) ||
                !TextUtils.isEmpty(ubirthday_et.getText().toString().trim()) ||
                !TextUtils.isEmpty(uresidence_et.getText().toString().trim()) ||
                !TextUtils.isEmpty(uurgencyname_et.getText().toString().trim()) ||
                !TextUtils.isEmpty(uurgencyphone_et.getText().toString().trim()) ||
                !TextUtils.isEmpty(uworkaddress_et.getText().toString().trim()) ||
                !TextUtils.isEmpty(cnum_et.getText().toString().trim()) ||
                !TextUtils.isEmpty(cframe_et.getText().toString().trim()) ||
                !TextUtils.isEmpty(cprice_et.getText().toString().trim()) ||
                !TextUtils.isEmpty(ctype_et.getText().toString().trim()) ||
                !TextUtils.isEmpty(cbuytime_et.getText().toString().trim()) ||
                !TextUtils.isEmpty(cdynamo_et.getText().toString().trim()) ||
                !TextUtils.isEmpty(ucardid_pic_str) ||
                !TextUtils.isEmpty(ucardid_opposite_str) ||
                !TextUtils.isEmpty(plate_str) ||
                !TextUtils.isEmpty(vehicle_front_str)
                ) {

            SweetAlertDialog warnDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
            warnDialog.setTitleText("退出此次编辑？");
            warnDialog.showCancelButton(true).setCancelText("取消");
            warnDialog.setConfirmText("确定");
            warnDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    finish();
                    sweetAlertDialog.dismissWithAnimation();
                }
            });

            warnDialog.show();
        } else {
            finish();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        //当前页面销毁时，如果临时图片文件夹中有图片，则清空
        File f = new File(Path.IMAGE_TEMP_FILE_PATH);
        File[] files = f.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                file.delete();
            }
        }


        OkGo.getInstance().cancelTag(this);

        AppManager.getAppManager().finishActivity(this);
    }
}
