package com.huatuo.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import org.json.JSONObject;

import com.huatuo.R;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.HandleServiceTimeUtil;
import com.huatuo.util.JsonParser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 子ListView适配器
 * 
 * @author zihao
 * 
 */
public class Lv_selectTime_DayAdapter extends BaseAdapter {

	private Context mContext;
	private String[] old_arrayList;
	private String[] new_arrayList;
	private int clickTemp = 0;

	/**
	 * 构造方法
	 * 
	 * @param context
	 */
	public Lv_selectTime_DayAdapter(Context context) {
		mContext = context;
		// this.arrayList = new ArrayList<JSONObject>();
	}

	// 标识选择的Items
	public void setSeclection(int position) {
		clickTemp = position;
		notifyDataSetChanged();
	}

	// public void add(ArrayList<JSONObject> arrayList) {
	// for (int i = 0; i < arrayList.size(); i++) {
	// JSONObject item = arrayList.get(i);
	// this.arrayList.add(item);
	// }
	// notifyDataSetChanged();
	// }

	public void add(String[] old_arrayList, String[] new_arrayList) {

		this.old_arrayList = old_arrayList;
		this.new_arrayList = new_arrayList;
		// for (int i = 0; i < arrayList.length(); i++) {
		// JSONObject item = arrayList.get(i);
		// this.arrayList.add(item);
		// }
		notifyDataSetChanged();
	}

	/**
	 * 获取item总数
	 */
	@Override
	public int getCount() {
		return CommonUtil.emptyArrayToString(old_arrayList) ? 0
				: old_arrayList.length;
	}

	/**
	 * 获取某一个Item的内容
	 */
	@Override
	public String getItem(int position) {
		return old_arrayList[position];
	}

	/**
	 * 获取当前item的ID
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.activity_select_appoint_time_lv_day_item, null);
			holder.tv_selectAppointTime_dayCN = (TextView) convertView
					.findViewById(R.id.tv_selectAppointTime_dayCN);
			holder.tv_selectAppointTime_day = (TextView) convertView
					.findViewById(R.id.tv_selectAppointTime_day);
			holder.rl_selectAppointTime_day  = (RelativeLayout) convertView
					.findViewById(R.id.rl_selectAppointTime_day);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		CommonUtil.log("old_arrayList:" + old_arrayList);
		String day = CommonUtil.getWeek2(old_arrayList[position]);
//		day = day.replace("星期", "周");
		CommonUtil.log("day:" + day);
		String day2 = old_arrayList[position];
		day2 = day2.substring(5, day2.length()).replace("-", "/");

		switch (position) {
		case 0:
		case 1:
			holder.tv_selectAppointTime_dayCN.setText(new_arrayList[position]);
			break;
		case 2:
		case 3:
			holder.tv_selectAppointTime_dayCN.setText(day);
			break;
		}
		holder.tv_selectAppointTime_day.setText(day2);
		
		if(clickTemp == position){
			holder.tv_selectAppointTime_day.setTextColor(mContext.getResources().getColor(R.color.white));
			holder.tv_selectAppointTime_dayCN.setTextColor(mContext.getResources().getColor(R.color.white));
			holder.rl_selectAppointTime_day.setBackground(mContext.getResources().getDrawable(R.drawable.custom_selected_day_bg_yellow));
		}else {
			holder.tv_selectAppointTime_day.setTextColor(mContext.getResources().getColor(R.color.c6));
			holder.tv_selectAppointTime_dayCN.setTextColor(mContext.getResources().getColor(R.color.c6));
			holder.rl_selectAppointTime_day.setBackground(mContext.getResources().getDrawable(R.drawable.custom_selected_day_bg_white));
		}
		

		return convertView;
	}

	static class ViewHolder {
		TextView tv_selectAppointTime_dayCN;
		TextView tv_selectAppointTime_day;
		RelativeLayout rl_selectAppointTime_day;
	}

}
