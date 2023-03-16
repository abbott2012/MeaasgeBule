package com.guoji.mobile.cocobee.fragment.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.LoginActivity;
import com.guoji.mobile.cocobee.common.AppManager;
import com.guoji.mobile.cocobee.model.User;
import com.guoji.mobile.cocobee.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * @author wuwenjie
 */
public class MySettingsFragment extends Fragment {


    @BindView(R.id.out_login)
    TextView mOutLogin;
    Unbinder unbinder;
    private User mUserLoginInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_settings, null);
        unbinder = ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        mUserLoginInfo = Utils.getUserLoginInfo();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.out_login)
    public void onViewClicked() {
        if (mUserLoginInfo != null) {
            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE);
            sweetAlertDialog.setCustomImage(R.drawable.exit_tip);
            sweetAlertDialog.setTitleText("退出当前用户？");
            sweetAlertDialog.setConfirmText("确定");
            sweetAlertDialog.showCancelButton(true);
            sweetAlertDialog.setCancelText("取消");
            sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismiss();
                    mUserLoginInfo = null;
                    Utils.putUserLoginInfo(mUserLoginInfo);

                    AppManager.getAppManager().releaseBluetoothResource(getContext()); //释放蓝牙资源
                    startActivity(new Intent(getContext(), LoginActivity.class));
                    AppManager.getAppManager().finishAllActivity();

                }
            });

            sweetAlertDialog.show();
        } else {
            startActivity(new Intent(getContext(), LoginActivity.class));
        }
    }
}
