package com.guoji.mobile.cocobee.view;


import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.guoji.mobile.cocobee.R;


public class SettingWindow {

	private Context mContext;
	private BaseAdapter mAdapter;
	private String text = "";
	private String title = "";
	public SettingWindow(){};

	public SettingWindow(Context context ,BaseAdapter adapter,String text,String title){
		this.mContext = context;
		this.mAdapter = adapter;
		this.text = text;
		this.title = title;
	}
	

	
	public View settingInit(){
		LinearLayout lanuagePopLayout = new LinearLayout(mContext);
		lanuagePopLayout.setOrientation(LinearLayout.VERTICAL);
		lanuagePopLayout.setBackgroundColor(Color.BLACK);
//		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		lanuagePopLayout.setLayoutParams(lp);
		
		ListView lv_setting = new ListView(mContext);
		lv_setting.setAdapter(mAdapter);
		lv_setting.setCacheColorHint(0);
		lv_setting.setDivider(mContext.getResources().getDrawable(R.color.qquse));
		lv_setting.setDividerHeight((int)mContext.getResources().getDimension(R.dimen.public_1_dp));
//		lv_setting.setPadding((int)mContext.getResources().getDimension(R.dimen.public_10_dp), 
//				(int)mContext.getResources().getDimension(R.dimen.public_10_dp), 
//				(int)mContext.getResources().getDimension(R.dimen.public_46_dp), 
//				(int)mContext.getResources().getDimension(R.dimen.public_10_dp));
		
		TextView tv = new TextView(mContext);
		tv.setText(this.text);
		tv.setTextSize(mContext.getResources().getDimension(R.dimen.public_9_sp));
		LayoutParams tlp = new LayoutParams(LayoutParams.WRAP_CONTENT, 0);
		tlp.setMargins((int)mContext.getResources().getDimension(R.dimen.public_8_dp), 0, 
				(int)mContext.getResources().getDimension(R.dimen.public_8_dp), 0);
		tv.setLayoutParams(tlp);
		lanuagePopLayout.addView(tv);
		
//		TextView tv_title = new TextView(mContext);
//		tv_title.setText(this.title);
//		tv_title.setTextSize(16);
//		LayoutParams tlp_title = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
////		tlp_title.setMargins(10, 0, 10, 0);
//		tv_title.setPadding(10, 2, 10, 4);
//		tv_title.setLayoutParams(tlp_title);
//		tv_title.setBackgroundColor(Color.BLACK);
//		tv_title.getBackground().setAlpha(10);
//		lanuagePopLayout.addView(tv_title);
		
		lanuagePopLayout.addView(lv_setting);
//		lanuagePopLayout.setBackgroundColor(Color.BLACK);
//		lanuagePopLayout.getBackground().setAlpha(180);
		return lanuagePopLayout;
	}
}
