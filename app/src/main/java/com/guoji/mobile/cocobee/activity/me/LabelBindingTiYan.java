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
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bql.utils.CheckUtils;
import com.bql.utils.EventManager;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.BarCodeScanAct;
import com.guoji.mobile.cocobee.activity.MainActivity1;
import com.guoji.mobile.cocobee.activity.base.BaseToolbarActivity;
import com.guoji.mobile.cocobee.callback.StringComCallback;
import com.guoji.mobile.cocobee.callback.StringDialogCallback;
import com.guoji.mobile.cocobee.common.AppConstants;
import com.guoji.mobile.cocobee.common.Constant;
import com.guoji.mobile.cocobee.common.JsonResult;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.model.User;
import com.guoji.mobile.cocobee.response.SelectServiceResponse;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 标签绑定
 * Created by Administrator on 2017/4/21.
 */

public class LabelBindingTiYan extends BaseToolbarActivity {

    @BindView(R.id.et_label_bind)
    EditText mEtLabelBind;
    @BindView(R.id.iv_scan)
    ImageView mIvScan;
    @BindView(R.id.et_name)
    EditText mEtName;
    @BindView(R.id.iv_add_photo)
    ImageView mIvAddPhoto;
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

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_label_binding_ti_yan;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        initToolbar("绑定标签");
        initIntent();
        //获取intent传递过来的数据
        mUser = Utils.getUserLoginInfo();
    }

    private void initIntent() {
        Intent intent = getIntent();
        mSelectServiceResponse = (SelectServiceResponse) intent.getSerializableExtra("selectServiceResponse");
        mOrderId = intent.getStringExtra("orderId");
    }

    //车主绑定标签
    private void binderCarLabel(final String labelBind) {
        String name = mEtName.getText().toString().trim();
        Map<String, String> params = new HashMap<String, String>();
        String path = "";
        if (!CheckUtils.isEmpty(plate_str)) {
            String plateImgBase64 = Utils.base64Pic(plate_str);
            params.put("pictrue_code", plateImgBase64);
        }
        params.put("pid", mUser.getPid());
        params.put("orgid", mUser.getOrgid());
        params.put("card_id", mSelectServiceResponse.getCard_id());
        params.put("labelid", labelBind);
        params.put("remarkname", name);
        if (CheckUtils.equalsString(mSelectServiceResponse.getTarget_id(), "38")) {//车
            params.put("order_id", mOrderId);
            params.put("createuser", mUser.getPname());
            path = Path.CAR_LABEL_BIND;
        } else {//人
            params.put("order_id", mOrderId);
            params.put("createuser", mUser.getPname());
            path = Path.PERSON_UPDATE_USE_INFO;
        }


        OkGo.post(path).tag(this).params(params).execute(new StringDialogCallback(this, "标签绑定中...") {
            @Override
            public void onSuccess(String result, Call call, Response response) {
                if (!TextUtils.isEmpty(result)) {
                    JsonResult jsonResult = new Gson().fromJson(result, new TypeToken<JsonResult>() {
                    }.getType());
                    if (jsonResult != null && jsonResult.isFlag() && jsonResult.getStatusCode() == 200) {//标签绑定成功
                        EventBus.getDefault().post(new EventManager(AppConstants.LABEL_BIND_SUCCESS));
                        XToastUtils.showShortToast("标签绑定成功");
                        Utils.startActivity(LabelBindingTiYan.this, MainActivity1.class);
                        finish();
                    } else if (jsonResult != null) { //标签绑定失败
                        XToastUtils.showShortToast(jsonResult.getMessage());
                    } else {
                        XToastUtils.showShortToast("绑定失败");
                    }
                } else {
                    XToastUtils.showShortToast("绑定失败");
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                XToastUtils.showShortToast("绑定标签出错");
            }
        });
    }

    private void checkTag(final String scanResult) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("lno", scanResult);

        OkGo.post(Path.CHECK_TAG_PATH).tag(this).params(params).execute(new StringComCallback() {
            @Override
            public void onSuccess(String result, Call call, Response response) {
                if (!TextUtils.isEmpty(result)) {
                    JsonResult jsonResult = new Gson().fromJson(result, new TypeToken<JsonResult>() {
                    }.getType());
                    if (jsonResult != null && jsonResult.isFlag() && jsonResult.getStatusCode() == 200) {//标签已经存在且未被绑定
                        binderCarLabel(scanResult);
                    } else if (jsonResult != null) { //不存在,标签不存在
                        XToastUtils.showShortToast(jsonResult.getMessage());
                    } else {
                        XToastUtils.showShortToast("暂时未查到相关标签信息");
                    }
                } else {
                    XToastUtils.showShortToast("暂时未查到相关标签信息");
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                XToastUtils.showShortToast("请求标签信息出错");
            }
        });
    }


    @OnClick({R.id.iv_scan, R.id.iv_add_photo, R.id.tv_bind_biaoqian})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_scan:
                startActivityForResult(new Intent(this, BarCodeScanAct.class), 1666);
                break;
            case R.id.iv_add_photo:
                showPopupwindow(view);
                break;
            case R.id.tv_bind_biaoqian:
                String labelBind = mEtLabelBind.getText().toString().trim();
                String name = mEtName.getText().toString().trim();
                if (CheckUtils.isEmpty(labelBind)) {
                    XToastUtils.showShortToast("请扫描标签或填写标签号");
                } else if (CheckUtils.isEmpty(name)) {
                    XToastUtils.showShortToast("请输入对象名称");
                } else {
                    checkTag(labelBind);
                }
                break;
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
                if (ContextCompat.checkSelfPermission(LabelBindingTiYan.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //进入到这里代表没有获取权限
                    if (ActivityCompat.shouldShowRequestPermissionRationale(LabelBindingTiYan.this, Manifest.permission.CAMERA)) {
                        //已经禁止提示了
                        Toast.makeText(LabelBindingTiYan.this, "您已禁止该权限，需要重新开启", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        ActivityCompat.requestPermissions(LabelBindingTiYan.this, new String[]{Manifest.permission.CAMERA}, PS_CAMERA_REQ);
                    }

                } else {

                    Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd_hhmmss");
                    Uri uri = null;

                    plate_str = sDateFormat.format(new java.util.Date());
                    plate_str = Path.IMAGE_TEMP_FILE_PATH + plate_str + ".jpg"; //先放入临时文件夹
                    uri = Uri.fromFile(new File(plate_str));

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
                if (ContextCompat.checkSelfPermission(LabelBindingTiYan.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //进入到这里代表没有获取权限
                    if (ActivityCompat.shouldShowRequestPermissionRationale(LabelBindingTiYan.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        //已经禁止提示了
                        Toast.makeText(LabelBindingTiYan.this, "您已禁止该权限，需要重新开启", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        ActivityCompat.requestPermissions(LabelBindingTiYan.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PS_STORAGE_REQ);

                    }

                } else {

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

                    plate_str = sDateFormat.format(new java.util.Date());
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

                            //将原始图片缩放成ImageView控件的高宽
                            Bitmap bitmap = ImageUtils.zoomBitmap(bitmap1, mIvAddPhoto.getWidth(), mIvAddPhoto.getHeight(), 1);

                            // Set the datetime as the name of new picture
                            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd_hhmmss");

                            plate_str = sDateFormat.format(new java.util.Date());
                            plate_str = Path.IMAGE_TEMP_FILE_PATH + plate_str + ".jpg"; //先放到临时文件夹管理
                            // Write the picture data to SD card.
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
