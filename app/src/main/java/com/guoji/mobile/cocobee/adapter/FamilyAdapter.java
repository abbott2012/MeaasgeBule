package com.guoji.mobile.cocobee.adapter;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bql.recyclerview.swipe.SwipeMenuAdapter;
import com.bql.utils.CheckUtils;
import com.bql.utils.EventManager;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.callback.DialogCallback;
import com.guoji.mobile.cocobee.common.AppConstants;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.response.FamilyResponse;
import com.guoji.mobile.cocobee.utils.ImageUtil;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.utils.XToastUtils;
import com.lzy.okgo.OkGo;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by liu on 2016/10/29.
 * 管理家庭成员adapter
 */
public class FamilyAdapter extends SwipeMenuAdapter<FamilyAdapter.FamilyAdapterViewHolder> {
    private List<FamilyResponse> mList;
    private Context mContext;


    public FamilyAdapter(Context context, List<FamilyResponse> list) {
        this.mList = list;
        this.mContext = context;
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_family, parent, false);
    }

    @Override
    public FamilyAdapterViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        return new FamilyAdapterViewHolder(realContentView);
    }

    @Override
    public void onBindViewHolder(FamilyAdapterViewHolder holder, int position) {
        holder.setData(mContext, mList, position, this);

    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    static class FamilyAdapterViewHolder extends RecyclerView.ViewHolder {
        private ImageView mRolePic;
        private ImageView mChangRole;
        private ImageView mDeleteRole;
        private EditText mRelation;
        private List<FamilyResponse> mList;
        private Context mContext;
        private AlertDialog mAlertDialog;
        private FamilyAdapter mFamilyAdapter;

        public FamilyAdapterViewHolder(View itemView) {
            super(itemView);
            mRolePic = (ImageView) itemView.findViewById(R.id.iv_ralation_pic);
            mChangRole = (ImageView) itemView.findViewById(R.id.iv_chang_role);
            mDeleteRole = (ImageView) itemView.findViewById(R.id.iv_delete_role);
            mRelation = (EditText) itemView.findViewById(R.id.et_relation);
        }

        public void setData(Context context, List<FamilyResponse> list, int position, FamilyAdapter familyAdapter) {
            mContext = context;
            mList = list;
            mFamilyAdapter = familyAdapter;
            FamilyResponse familyResponse = list.get(position);
            if (CheckUtils.equalsString(familyResponse.getManage_control(), "1")) {
                mDeleteRole.setVisibility(View.GONE);
                mChangRole.setVisibility(View.GONE);
                mRelation.setText("管理员");
            } else {
                mDeleteRole.setVisibility(View.VISIBLE);
                mRelation.setText(familyResponse.getRemark_relation());
            }
            ImageUtil.loadPersonSmallAvatar(mContext,Path.IMG_BASIC_PATH + familyResponse.getPhotourl(), mRolePic);
            mDeleteRole.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDeleteDialog(familyResponse, position);
                }
            });
            mChangRole.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changRole(familyResponse);//修改关系
                }
            });
        }

        private void showDeleteDialog(FamilyResponse familyResponse, int position) {
            SweetAlertDialog continueDialog = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
            continueDialog.setTitleText("温馨提示");
            continueDialog.setContentText("确定要删除该家庭成员吗?");
            continueDialog.showCancelButton(true).setCancelText("取消");
            continueDialog.setConfirmText("确定");
            continueDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismiss();
                    deleteRole(familyResponse, position);//删除关系
                }
            });

            continueDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismiss();
                }
            });

            continueDialog.show();
        }


        //修改关系
        private void changRole(FamilyResponse familyResponse) {
            View view = View.inflate(mContext, R.layout.chang_role, null);
            mAlertDialog = Utils.showCornerDialog(mContext, view, 270, 150);
            TextView mSuerChang = (TextView) mAlertDialog.findViewById(R.id.tv_sure_chang);
            EditText mEtChang = (EditText) mAlertDialog.findViewById(R.id.et_chang);
            mSuerChang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sureChang(mEtChang, familyResponse);
                }
            });
        }

        private void sureChang(EditText mEtChang, FamilyResponse familyResponse) {
            String sureRole = mEtChang.getText().toString().trim();
            if (CheckUtils.isEmpty(sureRole)) {
                XToastUtils.showShortToast("请输入关系");
            } else {
                Map<String, String> map = new HashMap<>();
                map.put("id", familyResponse.getId());
                map.put("remark_relation", sureRole);

                OkGo.post(Path.CHANG_FAMILY_PERSON).tag(this).params(map).execute(new DialogCallback<Object>(mContext, "修改中...") {

                    @Override
                    public void onSuccess(Object o, Call call, Response response) {
                        EventBus.getDefault().post(new EventManager(AppConstants.CHANG_ROLE_SUCCESS));
                        XToastUtils.showShortToast("修改成功");
                        mAlertDialog.dismiss();
                        mRelation.setText(sureRole);
                    }
                });
            }
        }

        //删除关系
        private void deleteRole(FamilyResponse familyResponse, int position) {
            Map<String, String> map = new HashMap<>();
            map.put("id", familyResponse.getId());

            OkGo.post(Path.DELETE_FAMILY_PERSON).tag(this).params(map).execute(new DialogCallback<Object>(mContext, "删除中...") {

                @Override
                public void onSuccess(Object o, Call call, Response response) {
                    EventBus.getDefault().post(new EventManager(AppConstants.DELETE_ROLE_SUCCESS));
                    XToastUtils.showShortToast("删除成功");
                    mList.remove(position);
                    mFamilyAdapter.notifyItemRemoved(position);
                }
            });
        }
    }
}