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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 子ListView适配器
 * 
 * @author zihao
 * 
 */
public class Lv_selectTime_ClockAdapter extends BaseAdapter {

	private Context mContext;
	private String[] arrayList;
	private int clickTemp = -1;
	private String currentDay;

	/**
	 * 构造方法
	 * 
	 * @param context
	 */
	public Lv_selectTime_ClockAdapter(Context context) {
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

	public void add(String[] arrayList,String currentDay) {

		this.currentDay = currentDay;
		this.arrayList = arrayList;
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
		return CommonUtil.emptyArrayToString(arrayList) ? 0
				: arrayList.length;
	}

	/**
	 * 获取某一个Item的内容
	 */
	@Override
	public String getItem(int position) {
		return arrayList[position];
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
					R.layout.activity_select_appoint_time_lv_clock_item, null);
			holder.rl_selectAppointTime_clock= (RelativeLayout) convertView
					.findViewById(R.id.rl_selectAppointTime_clock);
			holder.tv_selectAppointTime_clock = (TextView) convertView
					.findViewById(R.id.tv_selectAppointTime_clock);
			holder.iv_selectAppointTime_day_selected  = (ImageView) convertView
					.findViewById(R.id.iv_selectAppointTime_day_selected);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv_selectAppointTime_clock.setText(arrayList[position]);
//		isAvailable	能否服务	ANS	16	M	0表示不能服务，1表示可以服务
		String isAvailable = HandleServiceTimeUtil.map_day_canService.get(currentDay)[position];
		if(("1").equals(isAvailable)){
			if(clickTemp == position){
				holder.tv_selectAppointTime_clock.setTextColor(mContext.getResources().getColor(R.color.c4));
				holder.iv_selectAppointTime_day_selected.setVisibility(View.VISIBLE);
				holder.rl_selectAppointTime_clock.setBackground(mContext.getResources().getDrawable(R.drawable.custom_selected_clock_bg_yellow));
			}else {
				
				holder.tv_selectAppointTime_clock.setTextColor(mContext.getResources().getColor(R.color.c5));
				holder.iv_selectAppointTime_day_selected.setVisibility(View.INVISIBLE);
				holder.rl_selectAppointTime_clock.setBackground(mContext.getResources().getDrawable(R.drawable.custom_selected_clock_bg_white));
			}
		}else {
			holder.tv_selectAppointTime_clock.setTextColor(mContext.getResources().getColor(R.color.c7));
			holder.iv_selectAppointTime_day_selected.setVisibility(View.INVISIBLE);
			holder.rl_selectAppointTime_clock.setBackground(mContext.getResources().getDrawable(R.drawable.custom_selected_clock_bg_gray));
		}
		

		return convertView;
	}

	static class ViewHolder {
		RelativeLayout rl_selectAppointTime_clock;
		TextView tv_selectAppointTime_dayCN;
		TextView tv_selectAppointTime_clock;
		ImageView iv_selectAppointTime_day_selected;
	}

}
