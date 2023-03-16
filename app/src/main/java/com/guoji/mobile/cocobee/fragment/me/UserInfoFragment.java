package com.guoji.mobile.cocobee.fragment.me;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyz.actionsheet.ActionSheet;
import com.bql.utils.CheckUtils;
import com.bql.utils.EventManager;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.callback.StringDialogCallback;
import com.guoji.mobile.cocobee.common.AppConstants;
import com.guoji.mobile.cocobee.common.JsonResult;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.fragment.base.BaseFragment;
import com.guoji.mobile.cocobee.model.User;
import com.guoji.mobile.cocobee.utils.ImageUtil;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.utils.XToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/4/27.
 */

public class UserInfoFragment extends BaseFragment {

    @BindView(R.id.iv_user_pic)
    ImageView mIvUserPic;
    @BindView(R.id.ll_user_pic)
    LinearLayout mLlUserPic;
    @BindView(R.id.tv_user_name)
    TextView mTvUserName;
    @BindView(R.id.ll_user_name)
    LinearLayout mLlUserName;
    @BindView(R.id.tv_sex)
    TextView mTvSex;
    @BindView(R.id.ll_sex)
    LinearLayout mLlSex;
    @BindView(R.id.tv_user_birth)
    TextView mTvUserBirth;
    @BindView(R.id.ll_user_birth)
    LinearLayout mLlUserBirth;
    @BindView(R.id.tv_user_phone)
    TextView mTvUserPhone;
    @BindView(R.id.ll_user_phone)
    LinearLayout mLlUserPhone;
    @BindView(R.id.tv_user_id)
    TextView mTvUserId;
    @BindView(R.id.ll_user_id)
    LinearLayout mLlUserId;
    @BindView(R.id.tv_user_addr)
    TextView mTvUserAddr;
    @BindView(R.id.ll_user_addr)
    LinearLayout mLlUserAddr;
    private User mUserLoginInfo;

    private String plate_str;

    //拍照相册类型
    private final int REQUEST_CODE_CAMERA = 1000;
    private final int REQUEST_CODE_GALLERY = 1001;

    public static UserInfoFragment getInstance() {
        UserInfoFragment fragment = new UserInfoFragment();
        return fragment;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        mUserLoginInfo = Utils.getUserLoginInfo();

        if (mUserLoginInfo != null) {
            initUserInfo();
        } else {
            Utils.gotoLogin();
        }
    }

    private void initUserInfo() {

        mTvUserName.setText(mUserLoginInfo.getPname());
        if (CheckUtils.equalsString(mUserLoginInfo.getSex(), "0")) {
            mTvSex.setText("男");
        } else if (CheckUtils.equalsString(mUserLoginInfo.getSex(), "1")) {
            mTvSex.setText("女");
        }
        ImageUtil.loadSmallAvatar(_mActivity, Path.IMG_BASIC_PATH + mUserLoginInfo.getPhotourl(), mIvUserPic);
        System.out.println("#########" + Path.IMG_BASIC_PATH + mUserLoginInfo.getPhotourl());
        String phoneFirstAndEnd = Utils.getPhoneFirstAndEnd(mUserLoginInfo.getMobile());
        String idCardFirstAndEnd = Utils.getIdCardFirstAndEnd(mUserLoginInfo.getIdcard());
        mTvUserAddr.setText(mUserLoginInfo.getRegiaddr());
        mTvUserBirth.setText(mUserLoginInfo.getBirthday());
        mTvUserId.setText(idCardFirstAndEnd);
        mTvUserPhone.setText(phoneFirstAndEnd);
    }

    @Override
    protected void initToolbarHere() {
        initToolbar("我的信息");
    }


    @Override
    protected int getContentViewLayoutID() {
        return R.layout.user_info_fragment;
    }


    @OnClick({R.id.ll_user_pic})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_user_pic:
                if (!Utils.isFastClick()) {
                    return;
                }
                showOpen();
                break;
        }
    }

    //弹相册拍照框
    private void showOpen() {
        ActionSheet.createBuilder(_mActivity, getFragmentManager())
                .setCancelButtonTitle("取消(Cancel)")
                .setOtherButtonTitles("打开相册(Open Gallery)", "拍照(Camera)")
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        switch (index) {
                            case 0:
                                GalleryFinal.openGallerySingle(REQUEST_CODE_GALLERY, mOnHanlderResultCallback);
                                break;
                            case 1://拍照
                                GalleryFinal.openCamera(REQUEST_CODE_CAMERA, mOnHanlderResultCallback);
                                break;
                        }
                    }
                }).show();

    }

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                plate_str = resultList.get(0).getPhotoPath();
                uploadAvar();
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            XToastUtils.showShortToast(errorMsg);
        }
    };

    //上传头像
    private void uploadAvar() {
        String picUrl = Utils.getPicUrl(plate_str);
        String platestrBase64 = Utils.base64Pic(picUrl);

        Map<String, String> params = new HashMap<>();
        params.put("pid", mUserLoginInfo.getPid());
        params.put("userphoto", platestrBase64);

        OkGo.post(Path.USER_AVAR_UPLOAD).tag(this).params(params).execute(new StringDialogCallback(_mActivity, "头像上传中...") {
            @Override
            public void onSuccess(String result, final Call call, Response response) {
                if (!TextUtils.isEmpty(result)) {
                    JsonResult jr = new Gson().fromJson(result, new TypeToken<JsonResult>() {
                    }.getType());
                    if (jr != null && jr.getStatusCode() == 200) {
                        mUserLoginInfo.setPhotourl(jr.getUrl());
                        Utils.putUserLoginInfo(mUserLoginInfo);
                        ImageUtil.loadPersonAvatar(_mActivity, plate_str, mIvUserPic);
                        EventBus.getDefault().post(new EventManager(AppConstants.AVAR_PIC_UPLOAD_SUCCESS));
                    } else {
                        XToastUtils.showShortToast("上传失败");
                    }
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                XToastUtils.showShortToast("网络异常");
            }

        });

    }

}
