package com.huatuo.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatuo.R;
import com.huatuo.activity.map.GuideToStoreUtil;
import com.huatuo.bean.ApkInfo;
import com.huatuo.util.CommonUtil;

/**
 * 父ListView适配器
 * 
 * @author zihao
 * 
 */
public class Apk_Adapter extends BaseAdapter {

	public Context mContext;// 上下文对象
//	private List<Tip> list;
	private List<ApkInfo> list = new ArrayList<ApkInfo>();
	private String city;
	/**
	 * 构造方法
	 * 
	 * @param context
	 *            // 上下文对象
	 * @param groupArr
	 *            // item标题数组
	 */
	public Apk_Adapter(Context context,List<ApkInfo> list) {
		this.mContext = context;
		this.list = list;
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
	
	public void add(List<ApkInfo> arrayList,String city) {
		this.city = city;
		clear();
		if(!CommonUtil.emptyListToString3(arrayList)){
			for (int i = 0; i < arrayList.size(); i++) {
				ApkInfo item = arrayList.get(i);
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.apk_list_item, null);
			holder = new ViewHolder();
			holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		ApkInfo ApkInfo = list.get(position);
		String apkLabel = ApkInfo.getApkLabel();
		if(GuideToStoreUtil.GAODE_PACKAGENAME.equals(ApkInfo.getApkPackageName())){
			apkLabel = apkLabel+"（推荐）";
		}
		holder.iv_icon.setImageDrawable(ApkInfo.getApkIcon());
		holder.tv_name.setText(apkLabel);	

		return convertView;
	}

	static class ViewHolder {
		ImageView iv_icon;
		TextView tv_name;
	}

	
}
