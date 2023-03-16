package com.guoji.mobile.cocobee.activity.me.car;

import android.Manifest;
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
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bql.utils.CheckUtils;
import com.bql.utils.EventManager;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.base.BaseActivity;
import com.guoji.mobile.cocobee.activity.me.LabelBinding;
import com.guoji.mobile.cocobee.callback.StringComCallback;
import com.guoji.mobile.cocobee.common.AppConstants;
import com.guoji.mobile.cocobee.common.Constant;
import com.guoji.mobile.cocobee.common.JsonResult;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.model.Car;
import com.guoji.mobile.cocobee.utils.FileOperateUtils;
import com.guoji.mobile.cocobee.utils.ImageUtils;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.utils.XToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/4/20.
 */

public class UploadCarPicActivity1 extends BaseActivity {

    @BindView(R.id.ll_back_pro)
    ImageView mBackPro;
    @BindView(R.id.plate_iv)
    ImageView mPlateIv;//车辆正面
    @BindView(R.id.vehicle_front_iv)
    ImageView mVehicleFrontIv;//车辆反面
    @BindView(R.id.center_pic_ll)
    LinearLayout mCenterPicLl;
    @BindView(R.id.vehicle_side_one_iv)
    ImageView mVehicleSideOneIv;//车辆侧面1
    @BindView(R.id.vehicle_side_two_iv)
    ImageView mVehicleSideTwoIv;//车辆侧面2
    @BindView(R.id.vehicle_side_ll)
    LinearLayout mVehicleSideLl;
    @BindView(R.id.reg_next)
    TextView mRegNext;

    private int choose_pic_type = 0; //选择图片类型：1身份证正面／2身份证反面／3车辆正面／4车辆反面照片／5车辆侧面照1／6车辆侧面照2

    private PopupWindow mWindow;
    private View mView;
    public RelativeLayout uploadPhoto, uploadAlbum;
    private final int PS_CAMERA_REQ = 1111;
    private final int PS_STORAGE_REQ = 2222;
    private String plate_str, vehicle_front_str, vehicle_side_one_str, vehicle_side_two_str;
    private Car mCar;
    private static final int RESULT_SUCESS = 33333;
    private static final int RESULT_ERROR = 4444;
    private static final int RESULT_START = 5555;
    private SweetAlertDialog dialog;


    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_upload_car_pic;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        Intent intent = getIntent();
        mCar = (Car) intent.getSerializableExtra("car");
        initLongListener();
        initDialog();
    }

    private void initDialog() {
        dialog = new SweetAlertDialog(UploadCarPicActivity1.this, SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitleText("车辆信息绑定中...");
        dialog.getProgressHelper().setBarColor(Color.parseColor("#4169e1"));
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
    }

    private void initLongListener() {

         /*长按取消已经选中的车牌照片*/
        mPlateIv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!TextUtils.isEmpty(plate_str)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UploadCarPicActivity1.this);
                    builder.setItems(new String[]{"删除照片"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mPlateIv.setBackgroundResource(R.drawable.bike_front);
                            plate_str = null;
                        }
                    });
                    builder.create().show();
                }

                return false;
            }
        });

         /*长按取消已经选中的车辆正面照*/
        mVehicleFrontIv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!TextUtils.isEmpty(vehicle_front_str)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UploadCarPicActivity1.this);
                    builder.setItems(new String[]{"删除照片"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mVehicleFrontIv.setBackgroundResource(R.drawable.bike_back);
                            vehicle_front_str = null;
                        }
                    });
                    builder.create().show();
                }

                return false;
            }
        });
        /*长按取消已经选中的车辆正面照1*/
        mVehicleSideOneIv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!TextUtils.isEmpty(vehicle_side_one_str)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UploadCarPicActivity1.this);
                    builder.setItems(new String[]{"删除照片"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mVehicleSideOneIv.setBackgroundResource(R.drawable.bike_sidef);
                            vehicle_side_one_str = null;
                        }
                    });
                    builder.create().show();
                }

                return false;
            }
        });

         /*长按取消已经选中的车辆正面照1*/
        mVehicleSideTwoIv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!TextUtils.isEmpty(vehicle_side_two_str)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UploadCarPicActivity1.this);
                    builder.setItems(new String[]{"删除照片"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
//                            mVehicleSideTwoIv.setBackgroundResource(R.drawable.bike_sides);
                            mVehicleSideTwoIv.setImageResource(R.drawable.bike_sides);
                            vehicle_side_two_str = null;
                        }
                    });
                    builder.create().show();
                }

                return false;
            }
        });
    }


    @OnClick({R.id.ll_back_pro, R.id.plate_iv, R.id.vehicle_front_iv, R.id.vehicle_side_one_iv, R.id.vehicle_side_two_iv, R.id.reg_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_back_pro:
                finish();
                break;
            case R.id.plate_iv://车辆正面
                choose_pic_type = 3;
                showPopupwindow(view);
                break;
            case R.id.vehicle_front_iv://车辆反面
                choose_pic_type = 4;
                showPopupwindow(view);
                break;
            case R.id.vehicle_side_one_iv://车两侧面1
                choose_pic_type = 5;
                showPopupwindow(view);
                break;
            case R.id.vehicle_side_two_iv://车辆侧面2
                choose_pic_type = 6;
                showPopupwindow(view);
                break;
            case R.id.reg_next:
                //防止一秒内点击多次
                if (!Utils.isFastClick()) {
                    return;
                }
                if (CheckUtils.isEmpty(plate_str)) {
                    XToastUtils.showShortToast("请上传车辆正面照");
                } else if (CheckUtils.isEmpty(vehicle_front_str)) {
                    XToastUtils.showShortToast("请上传车辆背面照");
                } else if (CheckUtils.isEmpty(vehicle_side_one_str)) {
                    XToastUtils.showShortToast("请上传车辆侧面照1");
                } else if (CheckUtils.isEmpty(vehicle_side_two_str)) {
                    XToastUtils.showShortToast("请上传车辆侧面照2");
                } else {
                    newthreadPost();
                }
                break;
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RESULT_SUCESS:
                    //网络请求结束后关闭对话框
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    String result = (String) msg.obj;
                    if (!TextUtils.isEmpty(result)) {
                        JsonResult jr = new Gson().fromJson(result, new TypeToken<JsonResult>() {
                        }.getType());
                        if (jr != null && jr.isFlag() && jr.getStatusCode() == 200) {
                            EventBus.getDefault().post(new EventManager(AppConstants.CAR_INFO_UPDATE_SUCCESS));
                            SweetAlertDialog continueDialog = new SweetAlertDialog(UploadCarPicActivity1.this, SweetAlertDialog.WARNING_TYPE);
                            continueDialog.setTitleText("录入成功!是否领取标签");
                            continueDialog.showCancelButton(true).setCancelText("否");
                            continueDialog.setConfirmText("是");
                            continueDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                    Intent intent = new Intent(UploadCarPicActivity1.this, LabelBinding.class);
                                    Car car = new Car();
                                    car.setCno(mCar.getCno());
                                    car.setCbuytype(mCar.getCbuytype());
                                    intent.putExtra("car_info", car);
                                    startActivity(intent);
                                    finish();
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
                        } else if (jr != null && jr.getStatusCode() == 300) {
                            XToastUtils.showShortToast(jr.getMessage());
                        }
                    } else {
                        XToastUtils.showShortToast("上传失败");
                    }
                    break;
                case RESULT_ERROR:
                    //网络请求结束后关闭对话框
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    XToastUtils.showShortToast("网络异常");
                    break;
                case RESULT_START://请求开始
                    //网络请求前显示对话框
                    if (dialog != null && !dialog.isShowing()) {
                        dialog.show();
                    }
                    break;
            }
        }
    };

    //开启新线程,防止按钮卡顿
    private void newthreadPost() {
        new Thread() {
            public void run() {
                Looper.prepare();
                carPutIn();
            }
        }.start();
    }

    //车辆信息录入
    private void carPutIn() {

        String plateImgBase64 = Utils.base64Pic(plate_str);
        String vehicleFrontImgBase64 = Utils.base64Pic(vehicle_front_str);
        String vehicleSideOneImgBase64 = Utils.base64Pic(vehicle_side_one_str);
        String vehicleSideTwoImgBase64 = Utils.base64Pic(vehicle_side_two_str);

        Map<String, String> params = new HashMap<String, String>();
        mCar.setOrgid(user.getOrgid());
        mCar.setPid(user.getPid());
        mCar.setCcarpicurl(plateImgBase64 + "," + vehicleFrontImgBase64 + "," + vehicleSideOneImgBase64 + "," + vehicleSideTwoImgBase64);
        params.put("jsoncar", new Gson().toJson(mCar));
        params.put("idcard", user.getIdcard());

        //请求开始,显示进度
        Message msg = handler.obtainMessage();
        msg.what = RESULT_START;
        handler.sendMessage(msg);

        //上传车辆信息
        OkGo.post(Path.CAR_INFO_PUT_IN).tag(this).params(params).execute(new StringComCallback(){
            @Override
            public void onSuccess(String result, final Call call, Response response) {
                Message msg = handler.obtainMessage();
                msg.obj = result;
                msg.what = RESULT_SUCESS;
                handler.sendMessage(msg);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                Message msg = handler.obtainMessage();
                msg.what = RESULT_ERROR;
                handler.sendMessage(msg);
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
                if (ContextCompat.checkSelfPermission(UploadCarPicActivity1.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //进入到这里代表没有获取权限
                    if (ActivityCompat.shouldShowRequestPermissionRationale(UploadCarPicActivity1.this, Manifest.permission.CAMERA)) {
                        //已经禁止提示了
                        Toast.makeText(UploadCarPicActivity1.this, "您已禁止该权限，需要重新开启", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        ActivityCompat.requestPermissions(UploadCarPicActivity1.this, new String[]{Manifest.permission.CAMERA}, PS_CAMERA_REQ);

                    }

                } else {

                    Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd_hhmmss");
                    Uri uri = null;
                    switch (choose_pic_type) {

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
                if (ContextCompat.checkSelfPermission(UploadCarPicActivity1.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //进入到这里代表没有获取权限
                    if (ActivityCompat.shouldShowRequestPermissionRationale(UploadCarPicActivity1.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        //已经禁止提示了
                        Toast.makeText(UploadCarPicActivity1.this, "您已禁止该权限，需要重新开启", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        ActivityCompat.requestPermissions(UploadCarPicActivity1.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PS_STORAGE_REQ);

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
                    Uri uri = null;
                    switch (choose_pic_type) {
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
            Toast.makeText(UploadCarPicActivity1.this, "SD卡不可用", Toast.LENGTH_SHORT).show();
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

                            //将原始图片缩放成ImageView控件的高宽
//                            Bitmap bitmap = ImageUtils.zoomBitmap(bitmap1, mVehicleFrontIv.getWidth(), mVehicleFrontIv.getHeight(), 1);

                            // Set the datetime as the name of new picture
                            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd_hhmmss");

                            switch (choose_pic_type) {

                                case 3:
                                    plate_str = sDateFormat.format(new java.util.Date());
                                    plate_str = Path.IMAGE_TEMP_FILE_PATH + plate_str + ".jpg"; //先放到临时文件夹管理
                                    // Write the picture data to SD card.
                                    ImageUtils.compressBmpToFile(bitmap1, new File(plate_str));
                                    mPlateIv.setImageBitmap(bitmap1);
                                    break;

                                case 4:
                                    vehicle_front_str = sDateFormat.format(new java.util.Date());
                                    vehicle_front_str = Path.IMAGE_TEMP_FILE_PATH + vehicle_front_str + ".jpg"; //先放到临时文件夹管理
                                    // Write the picture data to SD card.
                                    ImageUtils.compressBmpToFile(bitmap1, new File(vehicle_front_str));
                                    mVehicleFrontIv.setImageBitmap(bitmap1);
                                    break;

                                case 5:
                                    vehicle_side_one_str = sDateFormat.format(new java.util.Date());
                                    vehicle_side_one_str = Path.IMAGE_TEMP_FILE_PATH + vehicle_side_one_str + ".jpg"; //先放到临时文件夹管理
                                    // Write the picture data to SD card.
                                    ImageUtils.compressBmpToFile(bitmap1, new File(vehicle_side_one_str));

                                    mVehicleSideOneIv.setImageBitmap(bitmap1);
                                    break;

                                case 6:
                                    vehicle_side_two_str = sDateFormat.format(new java.util.Date());
                                    vehicle_side_two_str = Path.IMAGE_TEMP_FILE_PATH + vehicle_side_two_str + ".jpg"; //先放到临时文件夹管理
                                    // Write the picture data to SD card.
                                    ImageUtils.compressBmpToFile(bitmap1, new File(vehicle_side_two_str));

                                    mVehicleSideTwoIv.setImageBitmap(bitmap1);
                                    break;

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case 2: //拍照
                    switch (choose_pic_type) {
                        case 3:
                            try {
                                FileInputStream is = new FileInputStream(plate_str);
                                Bitmap bitmap = BitmapFactory.decodeStream(is);

                                File fileName = new File(plate_str);
                                if (FileOperateUtils.getFileSize(fileName) > Constant.PIC_SIZE_LIMIT) {
                                    Bitmap newBitmap = ImageUtils.compressImageFromFile(plate_str);//获取压缩后的bitmap
                                    if (newBitmap != null) {
                                        if (fileName.exists()) {
                                            fileName.delete(); //删掉原文件
                                        }
                                        plate_str = new SimpleDateFormat("yyyyMMdd_hhmmss").format(new java.util.Date());
                                        plate_str = Path.IMAGE_TEMP_FILE_PATH + plate_str + ".jpg";
                                        //创建新文件
                                        ImageUtils.compressBmpToFile(newBitmap, new File(plate_str));
                                        bitmap = newBitmap;
                                    }

                                }
                                //将原始图片缩放成ImageView控件的高宽
//                                Bitmap bitmap1 = ImageUtils.zoomBitmap(bitmap, mPlateIv.getWidth(), mPlateIv.getHeight(), 1);
                                mPlateIv.setImageBitmap(bitmap);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;


                        case 4:
                            try {
                                FileInputStream is = new FileInputStream(vehicle_front_str);
                                Bitmap bitmap = BitmapFactory.decodeStream(is);

                                File fileName = new File(vehicle_front_str);
                                if (FileOperateUtils.getFileSize(fileName) > Constant.PIC_SIZE_LIMIT) {
                                    Bitmap newBitmap = ImageUtils.compressImageFromFile(vehicle_front_str);//获取压缩后的bitmap
                                    if (newBitmap != null) {
                                        if (fileName.exists()) {
                                            fileName.delete(); //删掉原文件
                                        }
                                        vehicle_front_str = new SimpleDateFormat("yyyyMMdd_hhmmss").format(new java.util.Date());
                                        vehicle_front_str = Path.IMAGE_TEMP_FILE_PATH + vehicle_front_str + ".jpg";
                                        //创建新文件
                                        ImageUtils.compressBmpToFile(newBitmap, new File(vehicle_front_str));
                                        bitmap = newBitmap;
                                    }

                                }
                                //将原始图片缩放成ImageView控件的高宽
//                                Bitmap bitmap1 = ImageUtils.zoomBitmap(bitmap, mVehicleFrontIv.getWidth(), mVehicleFrontIv.getHeight(), 1);
                                mVehicleFrontIv.setImageBitmap(bitmap);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;


                        case 5:
                            try {
                                FileInputStream is = new FileInputStream(vehicle_side_one_str);
                                Bitmap bitmap = BitmapFactory.decodeStream(is);

                                File fileName = new File(vehicle_side_one_str);
                                if (FileOperateUtils.getFileSize(fileName) > Constant.PIC_SIZE_LIMIT) {
                                    Bitmap newBitmap = ImageUtils.compressImageFromFile(vehicle_side_one_str);//获取压缩后的bitmap
                                    if (newBitmap != null) {
                                        if (fileName.exists()) {
                                            fileName.delete(); //删掉原文件
                                        }
                                        vehicle_side_one_str = new SimpleDateFormat("yyyyMMdd_hhmmss").format(new java.util.Date());
                                        vehicle_side_one_str = Path.IMAGE_TEMP_FILE_PATH + vehicle_side_one_str + ".jpg";
                                        //创建新文件
                                        ImageUtils.compressBmpToFile(newBitmap, new File(vehicle_side_one_str));
                                        bitmap = newBitmap;
                                    }

                                }
                                //将原始图片缩放成ImageView控件的高宽
//                                Bitmap bitmap1 = ImageUtils.zoomBitmap(bitmap, mVehicleSideOneIv.getWidth(), mVehicleSideOneIv.getHeight(), 1);
                                mVehicleSideOneIv.setImageBitmap(bitmap);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;

                        case 6:
                            try {
                                FileInputStream is = new FileInputStream(vehicle_side_two_str);
                                Bitmap bitmap = BitmapFactory.decodeStream(is);

                                File fileName = new File(vehicle_side_two_str);
                                if (FileOperateUtils.getFileSize(fileName) > Constant.PIC_SIZE_LIMIT) {
                                    Bitmap newBitmap = ImageUtils.compressImageFromFile(vehicle_side_two_str);//获取压缩后的bitmap
                                    if (newBitmap != null) {
                                        if (fileName.exists()) {
                                            fileName.delete(); //删掉原文件
                                        }
                                        vehicle_side_two_str = new SimpleDateFormat("yyyyMMdd_hhmmss").format(new java.util.Date());
                                        vehicle_side_two_str = Path.IMAGE_TEMP_FILE_PATH + vehicle_side_two_str + ".jpg";
                                        //创建新文件
                                        ImageUtils.compressBmpToFile(newBitmap, new File(vehicle_side_two_str));
                                        bitmap = newBitmap;
                                    }

                                }
                                //将原始图片缩放成ImageView控件的高宽
//                                Bitmap bitmap1 = ImageUtils.zoomBitmap(bitmap, mVehicleSideTwoIv.getWidth(), mVehicleSideTwoIv.getHeight(), 1);
                                mVehicleSideTwoIv.setImageBitmap(bitmap);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;


                    }

                    break;

                default:
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
