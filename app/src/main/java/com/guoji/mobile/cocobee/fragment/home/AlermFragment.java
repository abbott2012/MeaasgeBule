package com.guoji.mobile.cocobee.fragment.home;

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
import com.guoji.mobile.cocobee.callback.DialogCallback;
import com.guoji.mobile.cocobee.common.AppConstants;
import com.guoji.mobile.cocobee.common.Constant;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.fragment.base.BaseFragment;
import com.guoji.mobile.cocobee.model.User;
import com.guoji.mobile.cocobee.response.HomeRecResponse;
import com.guoji.mobile.cocobee.utils.FileOperateUtils;
import com.guoji.mobile.cocobee.utils.ImageUtils;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.utils.XToastUtils;
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
 * Created by Administrator on 2017/7/7.
 */

public class AlermFragment extends BaseFragment {
    private static HomeRecResponse homeRecResponse;
    private static String address;
    private static String latitude;
    private static String longitude;
    @BindView(R.id.describe_et)
    EditText mDescribeEt;
    @BindView(R.id.pic_iv)
    ImageView mPicIv;
    @BindView(R.id.tv_alerm)
    TextView mTvAlerm;


    //上传图片
    private PopupWindow mWindow;
    private View mView;
    public RelativeLayout uploadPhoto, uploadAlbum;
    private final int PS_CAMERA_REQ = 1111;
    private final int PS_STORAGE_REQ = 2222;
    private String plate_str;
    private User mUserLoginInfo;

    public static AlermFragment getInstance(HomeRecResponse homeRecResponse, String address, String latitude, String longitude) {
        AlermFragment.homeRecResponse = homeRecResponse;
        AlermFragment.address = address;
        AlermFragment.latitude = latitude;
        AlermFragment.longitude = longitude;
        AlermFragment fragment = new AlermFragment();
        return fragment;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        mUserLoginInfo = Utils.getUserLoginInfo();
        if (savedInstanceState != null) {
            address = savedInstanceState.getString("address");
            latitude = savedInstanceState.getString("latitude");
            longitude = savedInstanceState.getString("longitude");
            homeRecResponse = (HomeRecResponse) savedInstanceState.getSerializable("homeRecResponse");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("homeRecResponse", homeRecResponse);
        outState.putString("address", address);
        outState.putString("latitude", latitude);
        outState.putString("longitude", longitude);
    }

    @Override
    protected void initToolbarHere() {
        initToolbar("报警信息上传");
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.home_upload_alarminfo;
    }

    @OnClick({R.id.pic_iv, R.id.tv_alerm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.pic_iv:
                showPopupwindow(view);
                break;
            case R.id.tv_alerm:
                updateAlertInfo();
                break;
        }
    }


    //上传报警信息
    private void updateAlertInfo() {
        String describeEt = mDescribeEt.getText().toString().trim();
        if (CheckUtils.isEmpty(describeEt)) {
            XToastUtils.showShortToast("请输入丢车描述");
        } else if (CheckUtils.isEmpty(plate_str)) {
            XToastUtils.showShortToast("请上传照片");
        } else {

            String base64Pic = Utils.base64Pic(plate_str);
            Map<String, String> params = new HashMap<String, String>();
            params.put("pid", mUserLoginInfo.getPid());
            params.put("cid", homeRecResponse.getCid());
            params.put("labelid", homeRecResponse.getLabelid());
            params.put("apeople", mUserLoginInfo.getPname());
            params.put("amobile", mUserLoginInfo.getMobile());
            params.put("adesc", describeEt);
            params.put("atype", "1");
            params.put("orgid", mUserLoginInfo.getOrgid());
            params.put("aaddress", address);
            params.put("alarmlng", longitude);
            params.put("alarmlat", latitude);

            params.put("photo", base64Pic);

            OkGo.post(Path.NORMAL_USER_UPLOAD_ALARM_INFO_PATH).tag(this).params(params).execute(new DialogCallback<Object>(getContext(), "报警信息上传中...") {
                @Override
                public void onSuccess(Object o, Call call, Response response) {
                    XToastUtils.showShortToast("报警信息上传成功,我们会尽快为您处理!");
                    EventBus.getDefault().post(new EventManager(AppConstants.ALERM_SUCCESS));
                    _mActivity.finish();
                }
            });
        }

    }


    /**
     * @param parent
     * @description popupwindow实现
     */
    @SuppressWarnings("deprecation")
    private void showPopupwindow(View parent) {

        if (mWindow == null) {
            mView = LayoutInflater.from(getContext()).inflate(R.layout.ppw_modify_image, null);
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
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //进入到这里代表没有获取权限
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
                        //已经禁止提示了
                        Toast.makeText(getContext(), "您已禁止该权限，需要重新开启", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, PS_CAMERA_REQ);
                    }

                } else {

                    Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd_hhmmss");
                    Uri uri = null;

                    plate_str = sDateFormat.format(new Date());
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
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //进入到这里代表没有获取权限
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        //已经禁止提示了
                        Toast.makeText(getContext(), "您已禁止该权限，需要重新开启", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PS_STORAGE_REQ);

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            XToastUtils.showShortToast("SD卡不可用");
            return;
        }
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                // 图库
                case 1:
                    if (data != null) {
                        //这里加个判断，两种系统版本获取图片路径
                        String picturePath;
                        Uri uri = data.getData();
                        if (!TextUtils.isEmpty(uri.getAuthority())) {

                            String[] filePathColumn = {MediaStore.Images.Media.DATA};

                            Cursor cursor = getActivity().getContentResolver().query(uri,
                                    filePathColumn, null, null, null);

                            if (null == cursor) {
                                XToastUtils.showShortToast("图片没找到");
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
                            Bitmap bitmap1;
                            if (FileOperateUtils.getFileSize(picFile) > Constant.PIC_SIZE_LIMIT) { //如果大于限定大小，压缩位图
                                bitmap1 = ImageUtils.compressImageFromFile(picturePath);
                            } else {
                                bitmap1 = BitmapFactory.decodeFile(picturePath);
                            }

                            // Set the datetime as the name of new picture
                            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd_hhmmss");

                            plate_str = sDateFormat.format(new Date());
                            plate_str = Path.IMAGE_TEMP_FILE_PATH + plate_str + ".jpg"; //先放到临时文件夹管理
                            // Write the picture data to SD card.
                            ImageUtils.compressBmpToFile(bitmap1, new File(plate_str));
                            mPicIv.setImageBitmap(bitmap1);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case 2: //拍照


                    try {
                        FileInputStream is = new FileInputStream(plate_str);
                        Bitmap  bitmap = BitmapFactory.decodeStream(is);

                        File fileName = new File(plate_str);
                        if (FileOperateUtils.getFileSize(fileName) > Constant.PIC_SIZE_LIMIT) {
                            Bitmap newBitmap = ImageUtils.compressImageFromFile(plate_str);//获取压缩后的bitmap
                            if (newBitmap != null) {
                                if (fileName.exists()) {
                                    fileName.delete(); //删掉原文件
                                }
                                plate_str = new SimpleDateFormat("yyyyMMdd_hhmmss").format(new Date());
                                plate_str = Path.IMAGE_TEMP_FILE_PATH + plate_str + ".jpg";
                                //创建新文件
                                ImageUtils.compressBmpToFile(newBitmap, new File(plate_str));
                                bitmap = newBitmap;
                            }

                        }
                        mPicIv.setImageBitmap(bitmap);

                        is.close();
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
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        getActivity().getWindow().setAttributes(lp);
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
