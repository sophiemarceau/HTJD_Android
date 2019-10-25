package com.huatuo.adapter;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatuo.R;
import com.huatuo.activity.technician.Image_Show_Aty;
import com.huatuo.activity.technician.TechnicianDetail;
import com.huatuo.custom_widget.ImageViewCircle;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.FormatDistanceUtil;
import com.huatuo.util.ImageLoader_DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class Technician_ListViewAdapter_store extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<JSONObject> arrayList;
	private Context mContext;
	private final int type_icon = 1;
	private final int type_appoint = 2;

	public Technician_ListViewAdapter_store(Context mContext) {
		this.mContext = mContext;
		inflater = LayoutInflater.from(mContext);
		arrayList = new ArrayList<JSONObject>();
	}

	public void add(JSONObject item) {
		arrayList.add(item);
		notifyDataSetChanged();
	}

	public void add(ArrayList<JSONObject> arrayList) {
		if (!CommonUtil.emptyListToString3(arrayList)) {
			for (int i = 0; i < arrayList.size(); i++) {
				JSONObject item = arrayList.get(i);
				this.arrayList.add(item);
			}
		}
		notifyDataSetChanged();
	}

	public void clear() {
		this.arrayList.clear();
	}

	@Override
	public int getCount() {
		return arrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return arrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.technician_list_listview_item, null);
			holder = new ViewHolder();

			holder.iv_icon = (ImageViewCircle) convertView
					.findViewById(R.id.iv_icon);
			holder.tv_isAppoint = (TextView) convertView
					.findViewById(R.id.tv_isAppoint);
			holder.tv_technicianName = (TextView) convertView
					.findViewById(R.id.tv_technicianName);
			holder.tv_pingfen = (TextView) convertView
					.findViewById(R.id.tv_pingfen);
			holder.tv_orderNumber = (TextView) convertView
					.findViewById(R.id.tv_orderNumber);
			holder.tv_technicianLevel = (TextView) convertView
					.findViewById(R.id.tv_technicianLevel);
			holder.tv_distance = (TextView) convertView
					.findViewById(R.id.tv_distance);
			holder.tv_tech_info = (TextView) convertView
					.findViewById(R.id.tv_tech_info);
			holder.tv_tech_from = (TextView) convertView
					.findViewById(R.id.tv_tech_from);
			holder.tv_appoint = (TextView) convertView
					.findViewById(R.id.tv_appoint);
			holder.rl_tech_from = (RelativeLayout) convertView
					.findViewById(R.id.rl_tech_from);
			holder.rl_tech_from .setVisibility(View.GONE);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// ID 技师ID
		// name 技师姓名
		// icon 技师Icon
		// distance 距离
		// gradeName 技师等级
		// gradeID 技师等级ID
		// gender 性别
		// isBusy 是否今日满钟
		// serviceCount 成单次数
		// score 评分
		// introduction 技师详细介绍
		// storeID 店铺ID
		// storeName 店铺名称
		// isSelfOwned 技师归属
		// isCollect 是否收藏
		if (!CommonUtil.emptyListToString3(arrayList)) {
		JSONObject jsonObject = arrayList.get(position);

		String ID = jsonObject.optString("ID", "");
		String name = jsonObject.optString("name", "");
		String icon = jsonObject.optString("icon", "");
		String distance = jsonObject.optString("distance", "");
		String gradeName = jsonObject.optString("gradeName", "");
		String gradeID = jsonObject.optString("gradeID", "");
		String gender = jsonObject.optString("gender", "");
		String isBusy = jsonObject.optString("isBusy", "");
		String serviceCount = jsonObject.optString("orderCount", "");
		String score = jsonObject.optString("score", "");
		String introduction = jsonObject.optString("introduction", "");
		String storeID = jsonObject.optString("storeID", "");
		String storeName = jsonObject.optString("storeName", "");
		String isSelfOwned = jsonObject.optString("isSelfOwned", "");
		String isCollect = jsonObject.optString("isCollect", "");

		ImageLoader.getInstance().displayImage(
				icon,
				holder.iv_icon,
				ImageLoader_DisplayImageOptions.getInstance()
						.setDefaultImageTechImg());

		if(!TextUtils.isEmpty(name)){
			name = name.trim();
		}
		if(!TextUtils.isEmpty(gradeName)){
			gradeName = gradeName.trim();
		}
		
		holder.tv_technicianName.setText(name);
		holder.tv_orderNumber.setText(serviceCount + "单");
		holder.tv_distance.setText(FormatDistanceUtil.formatDistance(distance));
		holder.tv_pingfen.setText(score + "分");
		holder.tv_technicianLevel.setText(gradeName + "技师");

		if (null != isBusy && isBusy.equals("1")) {
			holder.tv_isAppoint.setText("今日满钟");
			holder.tv_isAppoint
					.setBackgroundResource(R.drawable.icon_teachlist_full);
		} else if (null != isBusy && isBusy.equals("2")) {
			holder.tv_isAppoint.setText("今日可约");
			holder.tv_isAppoint
					.setBackgroundResource(R.drawable.icon_teachlist_normal);
		}
		holder.tv_tech_info.setText(introduction);
		// 到店
		if (("0").equals(isSelfOwned)) {
			holder.tv_tech_from.setText(storeName);
			// 自营的
		} else if (("1").equals(isSelfOwned)) {
			holder.tv_tech_from.setText("华佗驾到自营");
		}
		convertView.setOnClickListener(new AppointTechOnClickListener(
				position, type_appoint));
		holder.iv_icon.setOnClickListener(new AppointTechOnClickListener(
				position, type_icon));
		}
		return convertView;
	}

	static class ViewHolder {
		ImageViewCircle iv_icon;
		TextView tv_isAppoint, tv_technicianName, tv_pingfen, tv_orderNumber,
				tv_technicianLevel, tv_distance, tv_tech_info, tv_tech_from,
				tv_appoint;
		RelativeLayout rl_tech_from;
	}

	class AppointTechOnClickListener implements OnClickListener {
		private int position;
		private int type;

		public AppointTechOnClickListener(int position, int type) {
			this.position = position;
			this.type = type;
		}

		@Override
		public void onClick(View v) {
			JSONObject jsonObject = arrayList.get(position);
			switch (type) {
			case type_icon:
				String icon = jsonObject.optString("icon", "");
				String name = jsonObject.optString("name", "");
				Intent intent = new Intent(mContext, Image_Show_Aty.class);
				intent.putExtra("url", icon);
				intent.putExtra("name", name);
				mContext.startActivity(intent);
				((Activity) mContext).overridePendingTransition(0, 0);
				break;
			case type_appoint:
				String ID = jsonObject.optString("ID", "");
				Intent intent2 = new Intent(mContext, TechnicianDetail.class);
				intent2.putExtra("ID", ID);
				mContext.startActivity(intent2);
				break;
			}

		}

	}
}