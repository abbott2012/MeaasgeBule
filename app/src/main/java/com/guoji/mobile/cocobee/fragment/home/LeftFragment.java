package com.guoji.mobile.cocobee.fragment.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author wuwenjie
 * @date 2014/11/14
 * @description 侧边栏菜单
 */
public class LeftFragment extends Fragment {
    @BindView(R.id.tv_home)
    TextView mTvHome;
    @BindView(R.id.tvMySettings)
    TextView mTvMySettings;
    @BindView(R.id.profile_image)
    ImageView mProfileImage;
    Unbinder unbinder;

    String title = null;
    Fragment newContent = null;
    private int flag = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_menu, null);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    /**
     * 切换fragment
     *
     * @param fragment
     */
    private void switchFragment(Fragment fragment, String title) {
        if (getActivity() == null) {
            return;
        }
        if (getActivity() instanceof MainActivity) {
            MainActivity fca = (MainActivity) getActivity();
            fca.switchConent(fragment, title);
        }
    }

    @OnClick({R.id.tv_home, R.id.tvMySettings})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.tv_home: // 首页
                if (newContent != null && flag == 1) {
                    break;
                }
                newContent = HomeFragment.getInstance();
                flag = 1;
                title = "平安城市";
                break;

            case R.id.tvMySettings: // 设置
                if (newContent != null && flag == 2) {
                    break;
                }
                newContent = new MySettingsFragment();
                flag = 2;
                title = "设置";
                break;
            default:
                break;
        }
        if (newContent != null) {
            switchFragment(newContent, title);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
