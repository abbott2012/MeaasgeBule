package com.guoji.mobile.cocobee.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.model.OfflineMapCityBean;

import java.util.List;

/**
 * Created by Administrator on 2017/5/20.
 */

public class AllCityAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private List<OfflineMapCityBean> list;

    public AllCityAdapter(Context context, List<OfflineMapCityBean> list) {
        this.mContext = context;
        this.list = list;
    }

    //获取一级菜单条目的总数
    @Override
    public int getGroupCount() {
        return list == null ? 0 : list.size();
    }

    //获取二级菜单条目的总数
    @Override
    public int getChildrenCount(int groupPosition) {
        return list.get(groupPosition) == null ? 0 : list.get(groupPosition).getChildCities().size();
    }

    //获取一级菜单的一个条目
    @Override
    public Object getGroup(int groupPosition) {
        if (groupPosition == 0) {
            return null;
        } else {
            return list.get(groupPosition);
        }
    }

    //获取二级菜单的一个条目
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        int childrenCount = getChildrenCount(groupPosition);
        if (childrenCount == 0) {
            return null;
        } else {
            return list.get(groupPosition).getChildCities().get(childPosition);
        }
    }

    //获取一级菜单的id
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    //获取二级菜单的id
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    //获取一级菜单的视图
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolderGroup group = new ViewHolderGroup();
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.offlinemap_item, null);
            group.cityName1 = (TextView) convertView.findViewById(R.id.id_cityname);
            group.idProgress1 = (TextView) convertView.findViewById(R.id.id_progress);
            convertView.setTag(group);
        } else {
            group = (ViewHolderGroup) convertView.getTag();
        }
        OfflineMapCityBean offlineMapCityBean = list.get(groupPosition);
        group.cityName1.setText(offlineMapCityBean.getCityName());
        if (offlineMapCityBean.getChildCities() != null && offlineMapCityBean.getChildCities().size() != 0) {
            group.idProgress1.setVisibility(View.GONE);
        } else {
            group.idProgress1.setVisibility(View.VISIBLE);
            String progressState = setProgressState(offlineMapCityBean);
            group.idProgress1.setText(progressState);
        }
        return convertView;
    }

    private String setProgressState(OfflineMapCityBean progressBean) {
        int progress = progressBean.getProgress();
        String progressMsg = "";
        // 根据进度情况，设置显示
        if (progress == 0) {
            progressMsg = "未下载";
        } else if (progress == 100) {
            progressBean.setFlag(OfflineMapCityBean.Flag.NO_STATUS);
            progressMsg = "已下载";
        } else {
            progressMsg = progress + "%";
        }
        // 根据当前状态，设置显示
        switch (progressBean.getFlag()) {
            case PAUSE:
                progressMsg += "【等待下载】";
                break;
            case DOWNLOADING:
                progressMsg += "【正在下载】";
                break;
            default:
                break;
        }
        return progressMsg;
    }

    //获取二级菜单的视图
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolderChild child = new ViewHolderChild();
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.offlinemap_child_item, null);
            child.cityName2 = (TextView) convertView.findViewById(R.id.id_cityname);
            child.idProgress2 = (TextView) convertView.findViewById(R.id.id_progress);
            child.view = convertView.findViewById(R.id.view);
            convertView.setTag(child);
        } else {
            child = (ViewHolderChild) convertView.getTag();
        }
        OfflineMapCityBean offlineMapCityBean = list.get(groupPosition).getChildCities().get(childPosition);
        child.cityName2.setText("    " + offlineMapCityBean.getCityName());
        String progressState = setProgressState(offlineMapCityBean);
        child.idProgress2.setText(progressState);
        return convertView;
    }

    //决定子视图是否可以被点击
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class ViewHolderGroup {
        TextView cityName1;
        TextView idProgress1;
    }

    class ViewHolderChild {
        TextView cityName2;
        TextView idProgress2;
        View view;
    }
}