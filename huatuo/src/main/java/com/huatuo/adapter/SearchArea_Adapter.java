package com.huatuo.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.services.help.Tip;
import com.huatuo.R;
import com.huatuo.util.CommonUtil;

/**
 * 父ListView适配器
 * 
 * @author zihao
 * 
 */
public class SearchArea_Adapter extends BaseAdapter {

	public Context mContext;// 上下文对象
//	private List<Tip> list;
	private List<Tip> list;
	private String city;
	/**
	 * 构造方法
	 * 
	 * @param context
	 *            // 上下文对象
	 * @param groupArr
	 *            // item标题数组
	 */
	public SearchArea_Adapter(Context context) {
		this.mContext = context;
		this.list = new ArrayList<Tip>();
	}

	public void add(List<Tip> arrayList) {
		clear();
		if(!CommonUtil.emptyListToString3(arrayList)){
			for (int i = 0; i < arrayList.size(); i++) {
				Tip item = arrayList.get(i);
				this.list.add(item);
	
			}
		}
		notifyDataSetChanged();
	}
//	public void add(List<Tip> arrayList,String city) {
//		this.city = city;
//		clear();
//		if(!CommonUtil.emptyListToString3(arrayList)){
//			for (int i = 0; i < arrayList.size(); i++) {
//				Tip item = arrayList.get(i);
//				this.list.add(item);
//	
//			}
//		}
//		notifyDataSetChanged();
//	}
	
	public void add(List<Tip> arrayList,String city) {
		this.city = city;
		clear();
		if(!CommonUtil.emptyListToString3(arrayList)){
			for (int i = 0; i < arrayList.size(); i++) {
				Tip item = arrayList.get(i);
				this.list.add(item);
	
			}
		}
		notifyDataSetChanged();
	}
	public void clear() {
		this.list.clear();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_address_search_area_lv_item, null);
			holder = new ViewHolder();
			holder.tv_search_address = (TextView) convertView.findViewById(R.id.tv_search_address);
			holder.tv_search_area = (TextView) convertView.findViewById(R.id.tv_search_area);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		Tip tip = list.get(position);
		String district = tip.getDistrict();
		String address = tip.getName();
		holder.tv_search_address.setText(address);
		holder.tv_search_area.setText(district);	

//		LatLonPoint latLngPoint = tip.getPoint();
//		double lat = latLngPoint.getLatitude();
//		double lng = latLngPoint.getLongitude();
		String target = city+"市";
		CommonUtil.log("target:"+target);
		int start = district.indexOf(target);
		int end = district.length();
		CommonUtil.log("start:"+start);
		CommonUtil.log("end:"+end);
//		holder.tv_search_area.setText(district.subSequence(start+target.length(), end));	

		return convertView;
	}

	static class ViewHolder {
		TextView tv_search_address;
		TextView tv_search_area;
	}

	
}
