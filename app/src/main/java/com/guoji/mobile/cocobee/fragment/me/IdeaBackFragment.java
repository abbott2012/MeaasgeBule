package com.guoji.mobile.cocobee.fragment.me;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyz.actionsheet.ActionSheet;
import com.bql.baseadapter.recycleView.BH;
import com.bql.baseadapter.recycleView.QuickRcvAdapter;
import com.bql.utils.CheckUtils;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.adapter.IdeaBackAdapter;
import com.guoji.mobile.cocobee.callback.DialogCallback;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.fragment.base.BaseFragment;
import com.guoji.mobile.cocobee.model.User;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.utils.XToastUtils;
import com.lzy.okgo.OkGo;

import java.util.ArrayList;
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
 * Created by Administrator on 2017/8/7.
 */

public class IdeaBackFragment extends BaseFragment {

    @BindView(R.id.tv_type_exc)
    TextView mTvTypeExc;
    @BindView(R.id.tv_type_tiyan)
    TextView mTvTypeTiyan;
    @BindView(R.id.tv_type_sug)
    TextView mTvTypeSug;
    @BindView(R.id.tv_type_other)
    TextView mTvTypeOther;
    @BindView(R.id.et_desc)
    EditText mEtDesc;
    @BindView(R.id.tv_text_num)
    TextView mTvTextNum;
    @BindView(R.id.tv_sure)
    TextView mTvSure;
    @BindView(R.id.recycle_view)
    RecyclerView mRecycleView;
    @BindView(R.id.iv_add_pic)
    ImageView mIvAddPic;
    private int type = 1;//type 1 功能异常, 2 体验问题, 3 功能建议, 4 其他

    private final int REQUEST_CODE_CAMERA = 1000;
    private final int REQUEST_CODE_GALLERY = 1001;
    private IdeaBackAdapter mAdapter;
    private ArrayList<PhotoInfo> mPhotoList = new ArrayList<>();
    private User mUserLoginInfo;

    public static IdeaBackFragment getInstance() {
        IdeaBackFragment fragment = new IdeaBackFragment();
        return fragment;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        mUserLoginInfo = Utils.getUserLoginInfo();
        initListener();
        initRecycleView();
    }


    //初始化RecycleView
    private void initRecycleView() {
        mAdapter = new IdeaBackAdapter(getContext(), mPhotoList);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        mRecycleView.setLayoutManager(layoutManager);
        mRecycleView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new QuickRcvAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(BH viewHolder, int position) {

            }
        });
    }

    private void initListener() {
        mEtDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String desc = mEtDesc.getText().toString().trim();
                mTvTextNum.setText(desc.length() + "/500");
                if (CheckUtils.isEmpty(desc)){
                    mTvSure.setEnabled(false);
                }else {
                    mTvSure.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected void initToolbarHere() {
        initToolbarWithRightText("意见反馈","帮助");
    }

    @Override
    public void btnRightTextClick() {
        start(HelpFragment.getInstance());
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_idea_back;
    }


    @OnClick({R.id.tv_type_exc, R.id.tv_type_tiyan, R.id.tv_type_sug, R.id.tv_type_other, R.id.tv_sure, R.id.iv_add_pic})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_type_exc://功能异常
                type = 1;
                setBlueType(mTvTypeExc);
                setcommenType(mTvTypeTiyan);
                setcommenType(mTvTypeSug);
                setcommenType(mTvTypeOther);
                break;
            case R.id.tv_type_tiyan://体验问题
                type = 2;
                setBlueType(mTvTypeTiyan);
                setcommenType(mTvTypeExc);
                setcommenType(mTvTypeSug);
                setcommenType(mTvTypeOther);
                break;
            case R.id.tv_type_sug://功能建议
                type = 3;
                setBlueType(mTvTypeSug);
                setcommenType(mTvTypeExc);
                setcommenType(mTvTypeTiyan);
                setcommenType(mTvTypeOther);
                break;
            case R.id.tv_type_other://其他
                type = 4;
                setBlueType(mTvTypeOther);
                setcommenType(mTvTypeExc);
                setcommenType(mTvTypeTiyan);
                setcommenType(mTvTypeSug);
                break;
            case R.id.iv_add_pic://添加照片
                showOpen();
                break;
            case R.id.tv_sure://提交
                if (!Utils.isFastClick()) {
                    return;
                }
                uploadSug();
                break;
        }
    }

    private void uploadSug() {
        String desc = mEtDesc.getText().toString().trim();
        if (CheckUtils.isEmpty(desc)) {
            XToastUtils.showShortToast("请填写问题描述!");
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("type", type + "");
        params.put("content", desc);
        params.put("pid", mUserLoginInfo.getPid());
        if (mPhotoList.size() > 0) {
            String url = "";
            for (int i = 0; i < mPhotoList.size(); i++) {
                if (i == mPhotoList.size() - 1) {
                    url += Utils.base64Pic(mPhotoList.get(i).getPhotoPath());
                } else {
                    url += Utils.base64Pic(mPhotoList.get(i).getPhotoPath()) + ",";
                }
            }
            params.put("imgurl", url);
        }

        OkGo.post(Path.USER_IDEA_BACK).tag(this).params(params).execute(new DialogCallback<Object>(_mActivity, "反馈意见上传中...") {

            @Override
            public void onSuccess(Object o, Call call, Response response) {
                XToastUtils.showShortToast("感谢您的意见反馈,我们会认真对待每一份留言!");
            }

        });
    }

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
                                GalleryFinal.openGalleryMuti(REQUEST_CODE_GALLERY, 3 - mPhotoList.size(), mOnHanlderResultCallback);
                                break;
                            case 1:
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
                mPhotoList.addAll(resultList);
                mAdapter.notifyDataSetChanged();
                if (mPhotoList.size() > 2) {
                    mIvAddPic.setVisibility(View.GONE);
                } else {
                    mIvAddPic.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            XToastUtils.showShortToast(errorMsg);
        }
    };


    private void setcommenType(TextView tvTypeTiyan) {
        tvTypeTiyan.setTextColor(getResources().getColor(R.color.color_000000));
        tvTypeTiyan.setBackgroundResource(R.drawable.corner_rec_gray_btn);
    }

    private void setBlueType(TextView tvTypeExc) {
        tvTypeExc.setTextColor(getResources().getColor(R.color.white));
        tvTypeExc.setBackgroundResource(R.drawable.corner_rec_blue_btn);
    }


}
