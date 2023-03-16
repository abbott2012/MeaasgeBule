package com.guoji.mobile.cocobee.fragment.me;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bql.utils.CheckUtils;
import com.bql.utils.EventManager;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.callback.DialogCallback;
import com.guoji.mobile.cocobee.common.AppConstants;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.fragment.base.BaseFragment;
import com.guoji.mobile.cocobee.model.User;
import com.guoji.mobile.cocobee.response.FamilyResponse;
import com.guoji.mobile.cocobee.response.HomeRecResponse;
import com.guoji.mobile.cocobee.utils.ImageUtil;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.utils.XToastUtils;
import com.guoji.mobile.cocobee.view.BubblePopupWindow;
import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/6/28.
 */

public class PersonDetailFragment extends BaseFragment {

    private static HomeRecResponse homeRecResponse;
    @BindView(R.id.iv_add_pic)
    ImageView mIvAddPic;
    @BindView(R.id.tv_name_sex)
    TextView mTvNameSex;
    @BindView(R.id.tv_user_type)
    TextView mTvUserType;
    @BindView(R.id.tv_birth)
    TextView mTvBirth;
    @BindView(R.id.tv_id_card)
    TextView mTvIdCard;
    @BindView(R.id.tv_label_num)
    TextView mTvLabelNum;
    @BindView(R.id.tv_has_buy_service)
    TextView mTvHasBuyService;
    @BindView(R.id.ll_has_buy_service)
    LinearLayout mLlHasBuyService;
    @BindView(R.id.tv_manager)
    TextView mTvManager;
    @BindView(R.id.iv_father)
    ImageView mIvFather;
    @BindView(R.id.iv_add_father)
    ImageView mIvAddFather;
    @BindView(R.id.rl_father)
    RelativeLayout mRlFather;
    @BindView(R.id.iv_mother)
    ImageView mIvMother;
    @BindView(R.id.iv_add_mother)
    ImageView mIvAddMother;
    @BindView(R.id.rl_mother)
    RelativeLayout mRlMother;
    @BindView(R.id.iv_grandpa)
    ImageView mIvGrandpa;
    @BindView(R.id.iv_add_grandpa)
    ImageView mIvAddGrandpa;
    @BindView(R.id.rl_grandpa)
    RelativeLayout mRlGrandpa;
    @BindView(R.id.iv_grandma)
    ImageView mIvGrandma;
    @BindView(R.id.iv_add_grandma)
    ImageView mIvAddGrandma;
    @BindView(R.id.rl_grandma)
    RelativeLayout mRlGrandma;
    @BindView(R.id.iv_grandfather)
    ImageView mIvGrandfather;
    @BindView(R.id.iv_add_grandfather)
    ImageView mIvAddGrandfather;
    @BindView(R.id.rl_grandfather)
    RelativeLayout mRlGrandfather;
    @BindView(R.id.iv_grandmother)
    ImageView mIvGrandmother;
    @BindView(R.id.iv_add_grandmother)
    ImageView mIvAddGrandmother;
    @BindView(R.id.rl_grandmother)
    RelativeLayout mRlGrandmother;
    @BindView(R.id.tv_father)
    TextView mTvFather;
    @BindView(R.id.tv_mother)
    TextView mTvMother;
    @BindView(R.id.tv_son)
    TextView mTvSon;
    @BindView(R.id.tv_daughter)
    TextView mTvDaughter;
    @BindView(R.id.tv_brother)
    TextView mTvBrother;
    @BindView(R.id.tv_sister)
    TextView mTvSister;
    @BindView(R.id.iv_show_info)
    ImageView mIvShowInfo;

    private User mUserLoginInfo;
    private List<FamilyResponse> mFamilyResponses = new ArrayList<>();
    private boolean flag;
    private String[] familyString = new String[]{"妈妈", "儿子", "女儿", "兄弟", "姐妹"};

    public static PersonDetailFragment getInstance(HomeRecResponse homeRecResponse) {
        PersonDetailFragment.homeRecResponse = homeRecResponse;
        PersonDetailFragment fragment = new PersonDetailFragment();
        return fragment;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        mUserLoginInfo = Utils.getUserLoginInfo();
        initData();
        initView();
    }

    //获取家庭组成员
    private void initData() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("peid", homeRecResponse.getPeid());

        OkGo.post(Path.CHECK_ALL_FAMILY).params(params).execute(new DialogCallback<List<FamilyResponse>>(_mActivity, "获取家庭成员中...") {

            @Override
            public void onSuccess(List<FamilyResponse> familyResponses, Call call, Response response) {
                List<FamilyResponse> list = new ArrayList<>();
                if (familyResponses == null || familyResponses.size() == 0) {
                    return;
                }
                for (int i = 0; i < familyResponses.size(); i++) {
                    FamilyResponse familyResponse = familyResponses.get(i);
                    if (CheckUtils.equalsString(familyResponse.getT_people_id(), mUserLoginInfo.getPid())) {//是用户自己,添加到第一个
                        list.add(0, familyResponse);
                        if (CheckUtils.equalsString(familyResponse.getManage_control(), "1")) {//用户自己是管理员
                            flag = true;
                        }
                    } else {
                        list.add(familyResponse);
                    }
                }
                mFamilyResponses = list;
                setFamilyMenber();
            }
        });
    }

    //设置家庭成员
    private void setFamilyMenber() {
        if (mFamilyResponses == null || mFamilyResponses.size() == 0) {
            return;
        }

        switch (mFamilyResponses.size()) {
            case 1:
                if (flag) {//用户自己是管理员
                    mRlMother.setVisibility(View.VISIBLE);
                    mRlGrandpa.setVisibility(View.VISIBLE);
                    mRlGrandma.setVisibility(View.VISIBLE);
                    mRlGrandfather.setVisibility(View.VISIBLE);
                    mRlGrandmother.setVisibility(View.VISIBLE);
                } else {
                    mRlMother.setVisibility(View.GONE);
                    mRlGrandpa.setVisibility(View.GONE);
                    mRlGrandma.setVisibility(View.GONE);
                    mRlGrandfather.setVisibility(View.GONE);
                    mRlGrandmother.setVisibility(View.GONE);
                }
                setMenberView(mFamilyResponses.get(0), mIvFather, mIvAddFather, mTvFather);
                setNoMenber(mIvMother, mIvAddMother, mTvMother, R.drawable.mother_default, familyString[0]);
                setNoMenber(mIvGrandpa, mIvAddGrandpa, mTvSon, R.drawable.son_default, familyString[1]);
                setNoMenber(mIvGrandma, mIvAddGrandma, mTvDaughter, R.drawable.daughter_default, familyString[2]);
                setNoMenber(mIvGrandfather, mIvAddGrandfather, mTvBrother, R.drawable.grandpa_default, familyString[3]);
                setNoMenber(mIvGrandmother, mIvAddGrandmother, mTvSister, R.drawable.grandma_default, familyString[4]);
                break;
            case 2:
                mRlMother.setVisibility(View.VISIBLE);
                if (flag) {//用户自己是管理员
                    mRlGrandpa.setVisibility(View.VISIBLE);
                    mRlGrandma.setVisibility(View.VISIBLE);
                    mRlGrandfather.setVisibility(View.VISIBLE);
                    mRlGrandmother.setVisibility(View.VISIBLE);
                } else {
                    mRlGrandpa.setVisibility(View.GONE);
                    mRlGrandma.setVisibility(View.GONE);
                    mRlGrandfather.setVisibility(View.GONE);
                    mRlGrandmother.setVisibility(View.GONE);
                }
                setMenberView(mFamilyResponses.get(0), mIvFather, mIvAddFather, mTvFather);
                setMenberView(mFamilyResponses.get(1), mIvMother, mIvAddMother, mTvMother);
                setNoMenber(mIvGrandpa, mIvAddGrandpa, mTvSon, R.drawable.son_default, familyString[1]);
                setNoMenber(mIvGrandma, mIvAddGrandma, mTvDaughter, R.drawable.daughter_default, familyString[2]);
                setNoMenber(mIvGrandfather, mIvAddGrandfather, mTvBrother, R.drawable.grandpa_default, familyString[3]);
                setNoMenber(mIvGrandmother, mIvAddGrandmother, mTvSister, R.drawable.grandma_default, familyString[4]);
                break;
            case 3:
                mRlMother.setVisibility(View.VISIBLE);
                mRlGrandpa.setVisibility(View.VISIBLE);
                if (flag) {//用户自己是管理员
                    mRlGrandma.setVisibility(View.VISIBLE);
                    mRlGrandfather.setVisibility(View.VISIBLE);
                    mRlGrandmother.setVisibility(View.VISIBLE);
                } else {
                    mRlGrandma.setVisibility(View.GONE);
                    mRlGrandfather.setVisibility(View.GONE);
                    mRlGrandmother.setVisibility(View.GONE);
                }
                setMenberView(mFamilyResponses.get(0), mIvFather, mIvAddFather, mTvFather);
                setMenberView(mFamilyResponses.get(1), mIvMother, mIvAddMother, mTvMother);
                setMenberView(mFamilyResponses.get(2), mIvGrandpa, mIvAddGrandpa, mTvSon);
                setNoMenber(mIvGrandma, mIvAddGrandma, mTvDaughter, R.drawable.daughter_default, familyString[2]);
                setNoMenber(mIvGrandfather, mIvAddGrandfather, mTvBrother, R.drawable.grandpa_default, familyString[3]);
                setNoMenber(mIvGrandmother, mIvAddGrandmother, mTvSister, R.drawable.grandma_default, familyString[4]);
                break;
            case 4:
                mRlMother.setVisibility(View.VISIBLE);
                mRlGrandpa.setVisibility(View.VISIBLE);
                mRlGrandma.setVisibility(View.VISIBLE);
                if (flag) {//用户自己是管理员
                    mRlGrandfather.setVisibility(View.VISIBLE);
                    mRlGrandmother.setVisibility(View.VISIBLE);
                } else {
                    mRlGrandfather.setVisibility(View.GONE);
                    mRlGrandmother.setVisibility(View.GONE);
                }
                setMenberView(mFamilyResponses.get(0), mIvFather, mIvAddFather, mTvFather);
                setMenberView(mFamilyResponses.get(1), mIvMother, mIvAddMother, mTvMother);
                setMenberView(mFamilyResponses.get(2), mIvGrandpa, mIvAddGrandpa, mTvSon);
                setMenberView(mFamilyResponses.get(3), mIvGrandma, mIvAddGrandma, mTvDaughter);
                setNoMenber(mIvGrandfather, mIvAddGrandfather, mTvBrother, R.drawable.grandpa_default, familyString[3]);
                setNoMenber(mIvGrandmother, mIvAddGrandmother, mTvSister, R.drawable.grandma_default, familyString[4]);
                break;
            case 5:
                mRlMother.setVisibility(View.VISIBLE);
                mRlGrandpa.setVisibility(View.VISIBLE);
                mRlGrandma.setVisibility(View.VISIBLE);
                mRlGrandfather.setVisibility(View.VISIBLE);
                if (flag) {//用户自己是管理员
                    mRlGrandmother.setVisibility(View.VISIBLE);
                } else {
                    mRlGrandmother.setVisibility(View.GONE);
                }
                setMenberView(mFamilyResponses.get(0), mIvFather, mIvAddFather, mTvFather);
                setMenberView(mFamilyResponses.get(1), mIvMother, mIvAddMother, mTvMother);
                setMenberView(mFamilyResponses.get(2), mIvGrandpa, mIvAddGrandpa, mTvSon);
                setMenberView(mFamilyResponses.get(3), mIvGrandma, mIvAddGrandma, mTvDaughter);
                setMenberView(mFamilyResponses.get(4), mIvGrandfather, mIvAddGrandfather, mTvBrother);
                setNoMenber(mIvGrandmother, mIvAddGrandmother, mTvSister, R.drawable.grandma_default, familyString[4]);
                break;
            case 6:
                mRlMother.setVisibility(View.VISIBLE);
                mRlGrandpa.setVisibility(View.VISIBLE);
                mRlGrandma.setVisibility(View.VISIBLE);
                mRlGrandfather.setVisibility(View.VISIBLE);
                mRlGrandmother.setVisibility(View.VISIBLE);
                setMenberView(mFamilyResponses.get(0), mIvFather, mIvAddFather, mTvFather);
                setMenberView(mFamilyResponses.get(1), mIvMother, mIvAddMother, mTvMother);
                setMenberView(mFamilyResponses.get(2), mIvGrandpa, mIvAddGrandpa, mTvSon);
                setMenberView(mFamilyResponses.get(3), mIvGrandma, mIvAddGrandma, mTvDaughter);
                setMenberView(mFamilyResponses.get(4), mIvGrandfather, mIvAddGrandfather, mTvBrother);
                setMenberView(mFamilyResponses.get(5), mIvGrandmother, mIvAddGrandmother, mTvSister);
                break;
        }
    }

    private void setMenberView(FamilyResponse familyResponse, ImageView ivFather, ImageView ivAddFather, TextView tvFather) {
        ImageUtil.loadPersonSmallAvatar(_mActivity, Path.IMG_BASIC_PATH + familyResponse.getPhotourl(), ivFather);
        if (CheckUtils.equalsString(familyResponse.getT_people_id(), mUserLoginInfo.getPid())) {//是用户自己
            tvFather.setText("我");
        } else {
            String remark_relation = familyResponse.getRemark_relation();
            if (CheckUtils.isEmpty(remark_relation) && CheckUtils.equalsString(familyResponse.getManage_control(), "1")) {
                tvFather.setText("管理员");
            } else {
                tvFather.setText(remark_relation);
            }
        }
        if (CheckUtils.equalsString(familyResponse.getManage_control(), "1")) {//是管理员
            ivAddFather.setImageResource(R.drawable.in_manage);
        } else if (CheckUtils.equalsString(familyResponse.getIsNotRegister(), "0")) {//不是管理员,邀请状态为邀请中
            ivAddFather.setImageResource(R.drawable.wait_manager);
        } else {
            ivAddFather.setVisibility(View.GONE);
        }

    }

    //设置没有成员的显示
    private void setNoMenber(ImageView ivFather, ImageView ivAddFather, TextView tvFather, int mother_default, String father) {
        ivFather.setImageResource(mother_default);
        ivAddFather.setImageResource(R.drawable.add_manager);
        ivAddFather.setVisibility(View.VISIBLE);
        tvFather.setText(father);
    }


    private void initView() {
        String photourl = homeRecResponse.getPhotourl();
        String[] photos = photourl.split(",");
        if (photos != null && photos.length > 0) {
            ImageUtil.loadPersonDetailPic(_mActivity, Path.IMG_BASIC_PATH + photos[0], mIvAddPic);
        } else {
            ImageUtil.loadPersonDetailPic(_mActivity, Path.IMG_BASIC_PATH + photourl, mIvAddPic);
        }

        mTvUserType.setText(Utils.getUserType(homeRecResponse.getPtype()));
        mTvBirth.setText(homeRecResponse.getBirthday());
        mTvIdCard.setText(homeRecResponse.getIdcard());
        mTvLabelNum.setText(homeRecResponse.getLno());
        if (CheckUtils.equalsString(homeRecResponse.getOnline(), AppConstants.TYPE_CARD_ONLINE)) {//线上
            if (CheckUtils.equalsString(homeRecResponse.getCard_id(), AppConstants.TYPE_CARD_ANXIN)) {//安心卡
                mTvHasBuyService.setText("安心卡");
                setPname();
            } else if (CheckUtils.equalsString(homeRecResponse.getCard_id(), AppConstants.TYPE_CARD_TIYAN)) {
                mTvHasBuyService.setText("体验卡");
                mTvNameSex.setText(homeRecResponse.getPname());
            } else if (CheckUtils.equalsString(homeRecResponse.getCard_id(), AppConstants.TYPE_CARD_DINZHI)) {
                mTvHasBuyService.setText("定制卡");
                setPname();
            } else {
                mTvHasBuyService.setText("未购买服务");
                setPname();
            }

        } else {//线下
            if (CheckUtils.equalsString(homeRecResponse.getCard_id(), AppConstants.TYPE_CARD_DINZHI)) {
                mTvHasBuyService.setText("定制卡");
                setPname();
            } else {
                mTvHasBuyService.setText("线下推广卡");
                mTvNameSex.setText(homeRecResponse.getPname());
            }
        }
        if (CheckUtils.equalsString(homeRecResponse.getManage_control(), "1")) {//具备管理权限
            mTvManager.setVisibility(View.VISIBLE);
        } else {//不具备管理权限
            mTvManager.setVisibility(View.GONE);
        }
    }

    private void setPname() {
        if (CheckUtils.equalsString(homeRecResponse.getSex(), "0")) {
            mTvNameSex.setText(homeRecResponse.getPname() + " | 男");
        } else if (CheckUtils.equalsString(homeRecResponse.getSex(), "1")) {
            mTvNameSex.setText(homeRecResponse.getPname() + " | 女");
        } else {
            mTvNameSex.setText(homeRecResponse.getPname());
        }
    }

    @Override
    protected void initToolbarHere() {
        initToolbarWithLeftText("人员详情", "我的");
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_person_detail;
    }


    @OnClick({R.id.ll_has_buy_service, R.id.tv_manager, R.id.rl_mother, R.id.rl_grandpa, R.id.rl_grandma, R.id.rl_grandfather, R.id.rl_grandmother, R.id.iv_show_info})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_has_buy_service://已购买服务
                if (!CheckUtils.isEmpty(homeRecResponse.getOrder_id())) {//购买了保险(存在orderId)
                    start(ServiceDetailFragment.getInstance(homeRecResponse));
                } else {
                    XToastUtils.showShortToast("该卡不具备该功能");
                }

                break;
            case R.id.tv_manager://管理
                start(FamilyManagerFragment.getInstance(homeRecResponse));
                break;
            case R.id.rl_mother:
                if (mFamilyResponses.size() > 1) {
                    return;
                }
                start(AddFamilyFragment.getInstance(homeRecResponse, familyString[0]));
                break;
            case R.id.rl_grandpa:
                if (mFamilyResponses.size() > 2) {
                    return;
                }
                start(AddFamilyFragment.getInstance(homeRecResponse, familyString[1]));
                break;
            case R.id.rl_grandma:
                if (mFamilyResponses.size() > 3) {
                    return;
                }
                start(AddFamilyFragment.getInstance(homeRecResponse, familyString[2]));
                break;
            case R.id.rl_grandfather:
                if (mFamilyResponses.size() > 4) {
                    return;
                }
                start(AddFamilyFragment.getInstance(homeRecResponse, familyString[3]));
                break;
            case R.id.rl_grandmother:
                if (mFamilyResponses.size() > 5) {
                    return;
                }
                start(AddFamilyFragment.getInstance(homeRecResponse, familyString[4]));
                break;
            case R.id.iv_show_info:
                BubblePopupWindow leftTopWindow = new BubblePopupWindow(_mActivity);
                View bubbleView = LayoutInflater.from(_mActivity).inflate(R.layout.layout_popup_view, null);
                TextView tvContent = (TextView) bubbleView.findViewById(R.id.tvContent);
                tvContent.setText("免费服务：邀请家人一起查看老人/小孩的轨迹日常。被邀请人下载注册APP后即与该标签绑定，无需重新购买，快邀请家人一起体验吧！");
                leftTopWindow.setBubbleView(bubbleView); // 设置气泡内容
                leftTopWindow.setParam(Utils.getScreenWidth() / 2, ViewGroup.LayoutParams.WRAP_CONTENT);
                leftTopWindow.show(view, Gravity.BOTTOM, 0); // 显示弹窗
                break;
        }
    }

    @Override
    public boolean registerEventBus() {
        return true;
    }

    @Override
    protected void onHandleEvent(EventManager eventManager) {
        super.onHandleEvent(eventManager);
        switch (eventManager.getEventCode()) {
            case AppConstants.YAO_QING_FAMILY_SUCCESS://邀请家人成功
            case AppConstants.WAIT_YAO_QING_FAMILY://邀请家人中
            case AppConstants.CHANG_ROLE_SUCCESS://修改关系成功
            case AppConstants.DELETE_ROLE_SUCCESS://删除监护人成功
                initData();
                break;
        }
    }
}
