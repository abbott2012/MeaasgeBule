package com.guoji.mobile.cocobee.activity.me;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.base.BaseToolbarActivity;
import com.guoji.mobile.cocobee.adapter.AllCityAdapter;
import com.guoji.mobile.cocobee.model.OfflineMapCityBean;

import java.util.ArrayList;
import java.util.List;

public class DownLoadMapActivity extends BaseToolbarActivity {

    protected static final String TAG = "downLoadMapActivity";
    /**
     * 离线地图功能
     */
    private MKOfflineMap mOfflineMap;
    private ListView mListView;
    private ExpandableListView mAllListView;
    /**
     * 热门城市离线地图的数据
     */
    private List<OfflineMapCityBean> mDatas = new ArrayList<>();

    /**
     * 所有城市离线地图的数据
     */
    private List<OfflineMapCityBean> mAllDatas = new ArrayList<>();

    /**
     * 适配器
     */
    private MyOfflineCityBeanAdapter mAdapter;
    private LayoutInflater mInflater;
    private Context context;
    /**
     * 目前加入下载队列的城市
     */
    private List<Integer> mCityCodes = new ArrayList<>();

    private int lastPosition = -1;// 代表之前打开的条目的ID
    private ArrayList<MKOLSearchRecord> allCityList;
    private ArrayList<MKOLUpdateElement> allUpdateInfo;
    private AllCityAdapter allCityAdapter;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.offlinemap;
    }


    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
//        //状态栏与应用同色
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }
        initToolbar("离线地图");
        context = this;
        mInflater = LayoutInflater.from(this);
        /**
         * 初始化离线地图
         */
        initOfflineMap();
        /**
         * 初始化ListView数据
         */
        initData();
        /**
         * 初始化ListView
         */
        initListView();
    }

    private void initListView() {
        mListView = (ListView) findViewById(R.id.id_offline_map_lv);
        mAdapter = new MyOfflineCityBeanAdapter(mDatas);
        mListView.setAdapter(mAdapter);
        onClickListView(mListView, mDatas);

        mAllListView = (ExpandableListView) findViewById(R.id.id_offline_map_all);
        initExpandableListView();
    }

    private void initExpandableListView() {

        //设置ExpandableListView的适配器
        allCityAdapter = new AllCityAdapter(this, mAllDatas);
        mAllListView.setAdapter(allCityAdapter);
        mAllListView.setVerticalScrollBarEnabled(false);//去掉右侧的滚动条

        //设置 属性 GroupIndicator 去掉默认向下的箭头
        mAllListView.setGroupIndicator(null);
        //讲ExpandableListView的条目间距设为0
        mAllListView.setDividerHeight(0);
        //设置一级条目的点击事件
        mAllListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (mAllDatas.get(groupPosition).getChildCities() != null && mAllDatas.get(groupPosition).getChildCities().size() != 0) {

                    // 全部被合上, 点击一个就打开一个
                    if (lastPosition == -1) {
                        // 展开一个一级菜单
                        mAllListView.expandGroup(groupPosition);
                        lastPosition = groupPosition;

                    } else {
                        // 开了一个, 如果点自己,把自己合上
                        if (lastPosition == groupPosition) {
                            mAllListView.collapseGroup(groupPosition);
                            lastPosition = -1;
                        } else {
                            // 开了一个, 点别人, 把自己合上,打开别人
                            mAllListView.collapseGroup(lastPosition);
                            mAllListView.expandGroup(groupPosition);
                            lastPosition = groupPosition;
                        }
                    }
                } else {
                    int cityId = mAllDatas.get(groupPosition).getCityCode();
                    if (mCityCodes.contains(cityId)) {
                        removeTaskFromQueue(cityId);
                    } else {
                        addToDownloadQueue(cityId);
                    }
                }
                return true;
            }
        });

        mAllListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                List<OfflineMapCityBean> childCities = mAllDatas.get(groupPosition).getChildCities();
                int cityId = childCities.get(childPosition).getCityCode();
                if (mCityCodes.contains(cityId)) {
                    removeTaskFromQueue(cityId);
                } else {
                    addToDownloadQueue(cityId);
                }
                return true;
            }
        });
    }

    private void onClickListView(final ListView listView, final List<OfflineMapCityBean> list) {
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                int cityId = list.get(position).getCityCode();
                if (mCityCodes.contains(cityId)) {
                    removeTaskFromQueue(cityId);
                } else {
                    addToDownloadQueue(cityId);
                }

            }
        });
    }

    /**
     * 将任务移除下载队列
     *
     * @param cityId
     */
    public void removeTaskFromQueue(int cityId) {
        mCityCodes.remove((Integer) cityId);
        mOfflineMap.pause(cityId);
        twoListSetFlag(mDatas, cityId, OfflineMapCityBean.Flag.NO_STATUS);
        twoListSetFlag(mAllDatas, cityId, OfflineMapCityBean.Flag.NO_STATUS);
        allCityAdapter.notifyDataSetChanged();
        mAdapter.notifyDataSetChanged();

    }

    //两个集合里面都需要设置才会同步
    private void twoListSetFlag(List<OfflineMapCityBean> datas, int cityId, OfflineMapCityBean.Flag flag) {
        for (OfflineMapCityBean offlineCityBean : datas) {
            if (offlineCityBean.getChildCities() != null && offlineCityBean.getChildCities().size() != 0) {
                for (OfflineMapCityBean bean : offlineCityBean.getChildCities()) {
                    if (bean.getCityCode() == cityId) {
                        bean.setFlag(flag);
                    }
                }
            } else if (offlineCityBean.getCityCode() == cityId) {
                offlineCityBean.setFlag(flag);
            }

        }
    }

    /**
     * 将下载任务添加至下载队列
     *
     * @param cityId
     */
    public void addToDownloadQueue(int cityId) {
        mCityCodes.add(cityId);
        mOfflineMap.start(cityId);
        twoListSetFlag(mDatas, cityId, OfflineMapCityBean.Flag.PAUSE);
        twoListSetFlag(mAllDatas, cityId, OfflineMapCityBean.Flag.PAUSE);
        allCityAdapter.notifyDataSetChanged();
        mAdapter.notifyDataSetChanged();
    }

    private void initData() {

        // 获得所有热门城市
        ArrayList<MKOLSearchRecord> offlineCityList = mOfflineMap.getHotCityList();
        // 手动添加了西安
        MKOLSearchRecord xian = new MKOLSearchRecord();
        xian.cityID = 233;
        xian.cityName = "西安市";
        offlineCityList.add(xian);
        // 获得所有已经下载的城市列表
        allUpdateInfo = mOfflineMap.getAllUpdateInfo();
        // 设置所有数据的状态
        for (MKOLSearchRecord record : offlineCityList) {
            OfflineMapCityBean cityBean = new OfflineMapCityBean();
            cityBean.setCityName(record.cityName);
            cityBean.setCityCode(record.cityID);
            //没有任何下载记录，返回null,为啥不返回空列表~~
            if (allUpdateInfo != null) {
                for (MKOLUpdateElement ele : allUpdateInfo) {
                    if (ele.cityID == record.cityID) {
                        cityBean.setProgress(ele.ratio);
                    }
                }

            }
            mDatas.add(cityBean);
        }

        //获得所有城市
        allCityList = mOfflineMap.getOfflineCityList();
        for (MKOLSearchRecord record : allCityList) {
            ArrayList<MKOLSearchRecord> childCities = record.childCities;
            List<OfflineMapCityBean> list1 = new ArrayList<>();
            OfflineMapCityBean offlineMapCityBean = new OfflineMapCityBean();
            offlineMapCityBean.setCityName(record.cityName);
            offlineMapCityBean.setCityCode(record.cityID);
            //没有任何下载记录，返回null,为啥不返回空列表~~
            if (allUpdateInfo != null) {
                for (MKOLUpdateElement ele : allUpdateInfo) {
                    if (ele.cityID == record.cityID) {
                        offlineMapCityBean.setProgress(ele.ratio);
                    }
                }

            }
            if (childCities != null && childCities.size() != 0) {
                for (int i = 0; i < childCities.size(); i++) {
                    MKOLSearchRecord record1 = childCities.get(i);
                    OfflineMapCityBean offlineMapCityBean1 = new OfflineMapCityBean();
                    offlineMapCityBean1.setCityName(record1.cityName);
                    offlineMapCityBean1.setCityCode(record1.cityID);
                    //没有任何下载记录，返回null,为啥不返回空列表~~
                    if (allUpdateInfo != null) {
                        for (MKOLUpdateElement ele : allUpdateInfo) {
                            if (ele.cityID == record1.cityID) {
                                offlineMapCityBean1.setProgress(ele.ratio);
                            }
                        }

                    }
                    list1.add(offlineMapCityBean1);
                }
            }
            offlineMapCityBean.setChildCities(list1);
            mAllDatas.add(offlineMapCityBean);
        }
    }

    /**
     * 初始化离线地图
     */

    private void initOfflineMap() {
        mOfflineMap = new MKOfflineMap();
        // 设置监听
        mOfflineMap.init(new MKOfflineMapListener() {
            @Override
            public void onGetOfflineMapState(int type, int state) {
                switch (type) {
                    case MKOfflineMap.TYPE_DOWNLOAD_UPDATE:
                        // 离线地图下载更新事件类型
                        MKOLUpdateElement update = mOfflineMap.getUpdateInfo(state);
                        Log.e(TAG, update.cityName + " ," + update.ratio);
                        for (OfflineMapCityBean bean : mDatas) {
                            if (bean.getCityCode() == state) {
                                bean.setProgress(update.ratio);
                                bean.setFlag(OfflineMapCityBean.Flag.DOWNLOADING);
                            }
                        }
                        for (OfflineMapCityBean bean : mAllDatas) {
                            if (bean.getChildCities() != null && bean.getChildCities().size() != 0) {
                                for (OfflineMapCityBean childBean : bean.getChildCities()) {
                                    if (childBean.getCityCode() == state) {
                                        childBean.setProgress(update.ratio);
                                        childBean.setFlag(OfflineMapCityBean.Flag.DOWNLOADING);
                                    }
                                }
                            } else {
                                if (bean.getCityCode() == state) {
                                    bean.setProgress(update.ratio);
                                    bean.setFlag(OfflineMapCityBean.Flag.DOWNLOADING);
                                }
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                        allCityAdapter.notifyDataSetChanged();
                        Log.e(TAG, "TYPE_DOWNLOAD_UPDATE");
                        break;
                    case MKOfflineMap.TYPE_NEW_OFFLINE:
                        // 有新离线地图安装
                        Log.e(TAG, "TYPE_NEW_OFFLINE");
                        break;
                    case MKOfflineMap.TYPE_VER_UPDATE:
                        // 版本更新提示
                        break;
                }

            }
        });

    }

    @Override
    protected void onDestroy() {
        mOfflineMap.destroy();
        super.onDestroy();
    }

    /**
     * 热门城市地图列表的Adapter
     *
     * @author zhy
     */
    class MyOfflineCityBeanAdapter extends BaseAdapter {

        private List<OfflineMapCityBean> dates = new ArrayList<>();

        public MyOfflineCityBeanAdapter(List<OfflineMapCityBean> list) {
            dates = list;
        }

        @Override
        public boolean isEnabled(int position) {
            if (dates.get(position).getProgress() == 100) {
                return false;
            }
            return super.isEnabled(position);
        }

        @Override
        public int getCount() {
            return dates.size();
        }

        @Override
        public OfflineMapCityBean getItem(int position) {
            return dates.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            OfflineMapCityBean bean = dates.get(position);
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.offlinemap_item, parent, false);
                holder.cityName = (TextView) convertView.findViewById(R.id.id_cityname);
                holder.progress = (TextView) convertView.findViewById(R.id.id_progress);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.cityName.setText(bean.getCityName());
            int progress = bean.getProgress();
            String progressMsg = "";
            // 根据进度情况，设置显示
            if (progress == 0) {
                progressMsg = "未下载";
            } else if (progress == 100) {
                bean.setFlag(OfflineMapCityBean.Flag.NO_STATUS);
                progressMsg = "已下载";
            } else {
                progressMsg = progress + "%";
            }
            // 根据当前状态，设置显示
            switch (bean.getFlag()) {
                case PAUSE:
                    progressMsg += "【等待下载】";
                    break;
                case DOWNLOADING:
                    progressMsg += "【正在下载】";
                    break;
                default:
                    break;
            }
            holder.progress.setText(progressMsg);
            return convertView;
        }

        private class ViewHolder {
            TextView cityName;
            TextView progress;

        }
    }
}
