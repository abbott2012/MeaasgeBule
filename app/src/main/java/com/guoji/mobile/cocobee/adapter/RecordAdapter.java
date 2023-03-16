package com.guoji.mobile.cocobee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.model.TagRecord;

import java.util.ArrayList;
import java.util.List;


/**
 * @author _H_JY
 * 2016-5-26下午1:50:02
 */

public class RecordAdapter extends BaseAdapter{

	private LayoutInflater inflater;
	private List<TagRecord> tagRecords = new ArrayList<>();
	
	
	
	
	public RecordAdapter(Context context,
						 List<TagRecord> transponders) {
		super();
		this.inflater = inflater.from(context);
		this.tagRecords = transponders;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return tagRecords.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return tagRecords.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		TagRecord tagRecord = tagRecords.get(position);
		if(convertView == null){
			convertView = inflater.inflate(R.layout.row_memory, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		viewHolder.lable_tv.setText(tagRecord.getLno()); //显示标签
		if(tagRecord.isLocalRecord()){
			viewHolder.date_tv.setText(tagRecord.getDate()); //显示日期
		}else {
			if("1".equals(tagRecord.getFlag())){
				viewHolder.date_tv.setText("车辆标签");
			}else if("2".equals(tagRecord.getFlag())){
				viewHolder.date_tv.setText("人员标签");
			}
		}

		return convertView;
	}
	
	
	public class ViewHolder{
		
	private TextView lable_tv;
	private TextView date_tv;

	public ViewHolder(View view){
		this.lable_tv = (TextView)view.findViewById(R.id.label_tv);
		this.date_tv = (TextView)view.findViewById(R.id.date_tv);
	}
		
	}

}
