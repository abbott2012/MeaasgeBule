package com.guoji.mobile.cocobee.activity.me;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bql.baseadapter.recycleView.BH;
import com.bql.baseadapter.recycleView.QuickRcvAdapter;
import com.bql.utils.CheckUtils;
import com.bql.utils.EventManager;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.BarCodeScanAct;
import com.guoji.mobile.cocobee.activity.MainActivity1;
import com.guoji.mobile.cocobee.activity.base.BaseToolbarActivity;
import com.guoji.mobile.cocobee.adapter.PersonTypeAdapter;
import com.guoji.mobile.cocobee.callback.DialogCallback;
import com.guoji.mobile.cocobee.callback.JsonCallback;
import com.guoji.mobile.cocobee.common.AppConstants;
import com.guoji.mobile.cocobee.common.Constant;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.model.User;
import com.guoji.mobile.cocobee.response.PersonTypeResponse;
import com.guoji.mobile.cocobee.response.SelectServiceResponse;
import com.guoji.mobile.cocobee.utils.FileOperateUtils;
import com.guoji.mobile.cocobee.utils.ImageUtils;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.utils.XToastUtils;
import com.guoji.mobile.cocobee.view.ListLineDecoration;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.lzy.okgo.OkGo;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 标签绑定
 * Created by Administrator on 2017/4/21.
 */

public class LabelBindingPersonAnXinActivity extends BaseToolbarActivity implements OnDateSetListener {

    @BindView(R.id.et_label_bind)
    EditText mEtLabelBind;
    @BindView(R.id.iv_scan)
    ImageView mIvScan;
    @BindView(R.id.et_name)
    EditText mEtName;
    @BindView(R.id.check_box_man)
    CheckBox mCheckBoxMan;
    @BindView(R.id.check_box_woman)
    CheckBox mCheckBoxWoman;
    @BindView(R.id.ubirthday_et)
    EditText mUbirthdayEt;
    @BindView(R.id.iv_add_photo)
    ImageView mIvAddPhoto;
    @BindView(R.id.et_select_person_type)
    EditText mEtSelectPersonType;
    @BindView(R.id.et_idcard_num)
    EditText mEtIdcardNum;
    @BindView(R.id.tv_bind_biaoqian)
    TextView mTvBindBiaoqian;
    private User mUser;

    private PopupWindow mWindow;
    private View mView;
    public RelativeLayout uploadPhoto, uploadAlbum;
    private final int PS_CAMERA_REQ = 1111;
    private final int PS_STORAGE_REQ = 2222;
    private String plate_str;
    private SelectServiceResponse mSelectServiceResponse;
    private String mOrderId;


    private TimePickerDialog mDialogAll;
    long tenYears = 10L * 365 * 1000 * 60 * 60 * 24L;
    private PersonTypeResponse mPersonTypeResponse;
    private String mLno;
    private TimePickerDialog.Builder mBuilder;

    //初始化时间选择
    private void initTimePicker() {
        mDialogAll = mBuilder
                .setCallBack(this)
                .setCancelStringId("取消")
                .setSureStringId("确定")
                .setTitleStringId("请选择时间")
                .setYearText("年")
                .setMonthText("月")
                .setDayText("日")
                .setCyclic(false)
                .setMinMillseconds(System.currentTimeMillis() - 15 * tenYears) //当前时间减去一百五十年
                .setMaxMillseconds(System.currentTimeMillis())
                .setCurrentMillseconds(System.currentTimeMillis())
                .setThemeColor(getResources().getColor(R.color.timepicker_dialog_bg))
                .setType(Type.YEAR_MONTH_DAY)
                .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
                .setWheelItemTextSelectorColor(getResources().getColor(R.color.timepicker_toolbar_bg))
                .setWheelItemTextSize(12)
                .build();

    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_label_binding_person_an_xin;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        initToolbar("绑定标签");
        initIntent();
        mBuilder = new TimePickerDialog.Builder();
        mUser = Utils.getUserLoginInfo();
    }

    //获取intent传递过来的数据
    private void initIntent() {
        Intent intent = getIntent();
        mSelectServiceResponse = (SelectServiceResponse) intent.getSerializableExtra("selectServiceResponse");
        mOrderId = intent.getStringExtra("orderId");
        mLno = intent.getStringExtra("lno");//不为空说明为体验卡升级为安心卡
        initView();
    }

    private void initView() {
        if (mLno != null) {//标签号不为空
            mEtLabelBind.setText(mLno);
            mEtLabelBind.setFocusable(false);
        }
    }

    //人绑定标签
    private void binderCarLabel(final String labelBind) {
        String unameEt = mEtName.getText().toString().trim();//姓名
        String ubirthdayEt = mUbirthdayEt.getText().toString().trim();//生日
        String sex = "2";//性别
        //调用完善个人信息接口
        if (mCheckBoxMan.isChecked()) {//0男 1女
            sex = "0";
        } else if (mCheckBoxWoman.isChecked()) {
            sex = "1";
        }
        String ucardidEt = mEtIdcardNum.getText().toString().trim();//身份证号码
        Map<String, String> params = new HashMap<String, String>();
        String plateImgBase64 = Utils.base64Pic(plate_str);
        String path;
        params.put("pid", mUser.getPid());
        params.put("ptype", mPersonTypeResponse.getDid());
        params.put("sex", sex);
        params.put("idcard", ucardidEt);
        params.put("birthday", ubirthdayEt);
        params.put("photourl", plateImgBase64);
        params.put("pname", unameEt);
        if (CheckUtils.equalsString(mSelectServiceResponse.getCard_id(), AppConstants.TYPE_CARD_DINZHI)) {//定制卡
            path = Path.BIND_LABEL_DINZHI_PERSON;
            params.put("orgid", mUser.getOrgid());
            params.put("lno", labelBind);

        } else if (mLno != null) {//体验卡升级为安心卡
            params.put("order_id", mOrderId);
            params.put("labelid", labelBind);
            params.put("target_id", "39");
            params.put("updateby", mUser.getPname());
            path = Path.CAR_TIYAN_UPDATE_ANXIN;
        } else {
            params.put("order_id", mOrderId);
            params.put("labelid", labelBind);
            params.put("orgid", mUser.getOrgid());
            params.put("pid", mUser.getPid());
            params.put("createuser", mUser.getPname());
            params.put("card_id", mSelectServiceResponse.getCard_id());
            path = Path.PERSON_UPDATE_USE_INFO;
        }

        OkGo.post(path).tag(this).params(params).execute(new DialogCallback<Object>(this, mLno == null ? "标签绑定中..." : "升级中...") {

            @Override
            public void onSuccess(Object o, Call call, Response response) {
                EventBus.getDefault().post(new EventManager(AppConstants.LABEL_BIND_SUCCESS));
                XToastUtils.showShortToast(mLno == null ? "标签绑定成功" : "升级成功");
                Utils.startActivity(LabelBindingPersonAnXinActivity.this, MainActivity1.class);
                finish();
            }
        });
    }

    private void checkTag(final String scanResult) {
        Map<String, String> params = new HashMap<String, String>();
        String path;
        params.put("lno", scanResult);
        if (CheckUtils.equalsString(mSelectServiceResponse.getCard_id(), AppConstants.TYPE_CARD_DINZHI)) {
            params.put("orgid", mUser.getOrgid());
            path = Path.BIND_LABEL_DINZHI;
        } else {
            path = Path.CHECK_TAG_PATH;
        }

        OkGo.post(path).tag(this).params(params).execute(new JsonCallback<Object>(LabelBindingPersonAnXinActivity.this) {

            @Override
            public void onSuccess(Object o, Call call, Response response) {
                binderCarLabel(scanResult);
            }
        });

    }

    @OnClick({R.id.iv_scan, R.id.check_box_man, R.id.check_box_woman, R.id.ubirthday_et, R.id.iv_add_photo, R.id.et_select_person_type, R.id.tv_bind_biaoqian})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_scan:
                if (mLno != null) {//标签号不为空说明是体验卡升级为安心卡,标签号不能更改
                    return;
                }
                startActivityForResult(new Intent(this, BarCodeScanAct.class), 1666);
                break;
            case R.id.check_box_man://性别男
                mCheckBoxMan.setChecked(true);
                mCheckBoxWoman.setChecked(false);
                setCheckBox(mCheckBoxMan, mCheckBoxWoman);
                break;
            case R.id.check_box_woman://性别女
                mCheckBoxMan.setChecked(false);
                mCheckBoxWoman.setChecked(true);
                setCheckBox(mCheckBoxWoman, mCheckBoxMan);
                break;
            case R.id.ubirthday_et:
                initTimePicker();
                if (!mDialogAll.isAdded()) { //判断是否已经添加，避免因重复添加导致异常
                    mDialogAll.show(getSupportFragmentManager(), "all");
                }
                break;
            case R.id.iv_add_photo:
                showPopupwindow(view);
                break;
            case R.id.et_select_person_type://获取人员类型
                getPersonType();
                break;
            case R.id.tv_bind_biaoqian:
                if (!Utils.isFastClick()) {
                    return;
                }
                gotoBindLabel();
                break;
        }
    }

    //绑定标签
    private void gotoBindLabel() {
        String labelNum = mEtLabelBind.getText().toString().trim();
        String unameEt = mEtName.getText().toString().trim();//姓名
        String ubirthdayEt = mUbirthdayEt.getText().toString().trim();//生日
        String personType = mEtSelectPersonType.getText().toString().trim();//人员类型
        String ucardidEt = mEtIdcardNum.getText().toString().trim();//身份证号码
        String idCArdNum = "";
        try {
            idCArdNum = CheckUtils.IDCardValidate(ucardidEt);
        } catch (ParseException e) {
            e.printStackTrace();
            XToastUtils.showLongToast("身份证无效");
        }
        if (CheckUtils.isEmpty(labelNum)) {
            XToastUtils.showLongToast("请输入标签号");
        } else if (CheckUtils.isEmpty(unameEt)) {
            XToastUtils.showLongToast("请输入姓名");
        } else if (!mCheckBoxWoman.isChecked() && !mCheckBoxMan.isChecked()) {
            XToastUtils.showLongToast("请选择性别");
        } else if (CheckUtils.isEmpty(ubirthdayEt)) {
            XToastUtils.showLongToast("请选择出生日期");
        } else if (CheckUtils.isEmpty(plate_str)) {
            XToastUtils.showLongToast("请上传照片");
        } else if (CheckUtils.isEmpty(personType)) {
            XToastUtils.showLongToast("请选择人员类型");
        } else if (!CheckUtils.isEmpty(ucardidEt) && !CheckUtils.equalsString(idCArdNum, "")) {
            XToastUtils.showLongToast("身份证无效");
        } else {//下一步
            if (mLno != null) {
                binderCarLabel(labelNum);
            } else {
                checkTag(labelNum);
            }
        }
    }

    private void getPersonType() {
        OkGo.post(Path.GET_PERSON_TYPE).tag(this).execute(new DialogCallback<List<PersonTypeResponse>>(this, "获取人员类型中...") {
            @Override
            public void onSuccess(List<PersonTypeResponse> personTypeResponses, Call call, Response response) {
                initDialog(personTypeResponses);
            }
        });
    }

    //弹选择框
    private void initDialog(List<PersonTypeResponse> personTypeResponses) {
        View view1 = View.inflate(this, R.layout.label_list, null);
        final AlertDialog alertDialog = Utils.showCornerDialog(this, view1, 270, 270);
        RecyclerView listView = (RecyclerView) alertDialog.findViewById(R.id.listView);
        TextView tvTitleCarLabel = (TextView) alertDialog.findViewById(R.id.tv_title_car_label);
        tvTitleCarLabel.setText("请选择人员类型");
        PersonTypeAdapter adapter = new PersonTypeAdapter(this, personTypeResponses);
        listView.setAdapter(adapter);
        listView.addItemDecoration(new ListLineDecoration());
        listView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnItemClickListener(new QuickRcvAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(BH viewHolder, int position) {
                alertDialog.dismiss();
                mPersonTypeResponse = personTypeResponses.get(position);
                mEtSelectPersonType.setText(mPersonTypeResponse.getDname());
            }
        });
    }

    private void setCheckBox(CheckBox checkBoxMan, CheckBox checkBoxWoman) {
        checkBoxMan.setChecked(true);
        checkBoxMan.setButtonDrawable(R.drawable.select_point);
        checkBoxMan.setTextColor(getResources().getColor(R.color.color_3270ed));
        checkBoxWoman.setChecked(false);
        checkBoxWoman.setTextColor(getResources().getColor(R.color.color_cccccc));
        checkBoxWoman.setButtonDrawable(R.drawable.not_select_point);
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
            uploadPhoto = (RelativeLayout) mView.findViewById(R.id.rlyt_upload_photo);//拍照上传
            uploadAlbum = (RelativeLayout) mView.findViewById(R.id.rlyt_upload_album);//相册上传
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
                if (ContextCompat.checkSelfPermission(LabelBindingPersonAnXinActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //进入到这里代表没有获取权限
                    if (ActivityCompat.shouldShowRequestPermissionRationale(LabelBindingPersonAnXinActivity.this, Manifest.permission.CAMERA)) {
                        //已经禁止提示了
                        Toast.makeText(LabelBindingPersonAnXinActivity.this, "您已禁止该权限，需要重新开启", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        ActivityCompat.requestPermissions(LabelBindingPersonAnXinActivity.this, new String[]{Manifest.permission.CAMERA}, PS_CAMERA_REQ);
                    }

                } else {

                    Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd_hhmmss");

                    plate_str = sDateFormat.format(new Date());
                    plate_str = Path.IMAGE_TEMP_FILE_PATH + plate_str + ".jpg"; //先放入临时文件夹
                    Uri uri = Uri.fromFile(new File(plate_str));

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
                if (ContextCompat.checkSelfPermission(LabelBindingPersonAnXinActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //进入到这里代表没有获取权限
                    if (ActivityCompat.shouldShowRequestPermissionRationale(LabelBindingPersonAnXinActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        //已经禁止提示了
                        Toast.makeText(LabelBindingPersonAnXinActivity.this, "您已禁止该权限，需要重新开启", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        ActivityCompat.requestPermissions(LabelBindingPersonAnXinActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PS_STORAGE_REQ);

                    }

                } else {

                    Intent intent = new Intent();
                    intent.setType("image/*");//可选择图片视频
                    //修改为以下两句代码
                    intent.setAction(Intent.ACTION_PICK);
                    intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//使用以上这种模式，并添加以上两句
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

                    plate_str = sDateFormat.format(new Date());
                    plate_str = Path.IMAGE_TEMP_FILE_PATH + plate_str + ".jpg"; //先放入临时文件夹
                    Uri uri = Uri.fromFile(new File(plate_str));

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

                    Intent intent = new Intent();
                    intent.setType("image/*");//可选择图片视频
                    //修改为以下两句代码
                    intent.setAction(Intent.ACTION_PICK);
                    intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//使用以上这种模式，并添加以上两句
                    startActivityForResult(intent, 1);

                    mWindow.dismiss();
                    backgroundAlpha(1f);
                } else {
                    //用户拒绝授权
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            XToastUtils.showShortToast("SD卡不可用");
            return;
        }
        if (requestCode == 1666 && resultCode == 1667) {
            String scanResult = data.getStringExtra("scanResult");
            mEtLabelBind.setText(scanResult);
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
                            if (FileOperateUtils.getFileSize(picFile) > Constant.PIC_SIZE_LIMIT) { //如果大于限定大小，压缩位图
                                bitmap1 = ImageUtils.compressImageFromFile(picturePath);
                            } else {
                                bitmap1 = BitmapFactory.decodeFile(picturePath);
                            }

//                            //将原始图片缩放成ImageView控件的高宽
                            Bitmap bitmap = ImageUtils.zoomBitmap(bitmap1, mIvAddPhoto.getWidth(), mIvAddPhoto.getHeight(), 1);
//
                            // Set the datetime as the name of new picture
                            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd_hhmmss");

                            plate_str = sDateFormat.format(new Date());
                            plate_str = Path.IMAGE_TEMP_FILE_PATH + plate_str + ".jpg"; //先放到临时文件夹管理
//                            // Write the picture data to SD card.
                            ImageUtils.compressBmpToFile(bitmap, new File(plate_str));
                            mIvAddPhoto.setImageBitmap(bitmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case 2: //拍照

                    try {
                        File fileName = new File(plate_str);
                        if (FileOperateUtils.getFileSize(fileName) > Constant.PIC_SIZE_LIMIT) {
                            Bitmap newBitmap = ImageUtils.compressImageFromFile(plate_str);//获取压缩后的bitmap
                            if (newBitmap != null) {
                                if (fileName.exists()) {
                                    fileName.delete(); //删掉原文件
                                }
                                plate_str = new SimpleDateFormat("yyyyMMdd_hhmmss").format(new Date());
                                plate_str = Path.IMAGE_TEMP_FILE_PATH + plate_str + ".jpg";
                                //将原始图片缩放成ImageView控件的高宽
                                newBitmap = ImageUtils.zoomBitmap(newBitmap, mIvAddPhoto.getWidth(), mIvAddPhoto.getHeight(), 1);
                                //创建新文件
                                ImageUtils.compressBmpToFile(newBitmap, new File(plate_str));
                            }
                            mIvAddPhoto.setImageBitmap(newBitmap);
                        } else {
                            FileInputStream is = new FileInputStream(plate_str);
                            Bitmap bitmap = BitmapFactory.decodeStream(is);
                            mIvAddPhoto.setImageBitmap(bitmap);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
            }
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

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        mUbirthdayEt.setText(getDateToString(millseconds));
    }

    public String getDateToString(long time) {

        Date d = new Date(time);
        SimpleDateFormat sf = null;
//        （购买时间）或者其他
        sf = new SimpleDateFormat("yyyy-MM-dd");
        return sf.format(d);
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


}
