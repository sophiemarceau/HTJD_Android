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
import android.widget.TextView;

import com.huatuo.R;
import com.huatuo.activity.technician.Image_Show_Aty;
import com.huatuo.activity.technician.TechnicianDetail;
import com.huatuo.bean.WorkerItemBean;
import com.huatuo.custom_widget.ImageViewCircle;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.FormatDistanceUtil;
import com.huatuo.util.ImageLoader_DisplayImageOptions;
import com.huatuo.util.UmengEventUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

public class Technician_ListViewAdapter_Bean extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<WorkerItemBean> arrayList;
	private Context mContext;
	private final int type_icon = 1;
	private final int type_appoint = 2;

	public Technician_ListViewAdapter_Bean(Context mContext) {
		this.mContext = mContext;
		inflater = LayoutInflater.from(mContext);
		arrayList = new ArrayList<WorkerItemBean>();
	}

	public void add(WorkerItemBean item) {
		arrayList.add(item);
		notifyDataSetChanged();
	}

	public void add(ArrayList<WorkerItemBean> arrayList) {
		if (!CommonUtil.emptyListToString3(arrayList)) {
			this.arrayList.addAll(arrayList);
//			for (int i = 0; i < arrayList.size(); i++) {
//				WorkerItemBean item = arrayList.get(i);
//				this.arrayList.add(item);
//			}
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

		CommonUtil.log("arrayList:" + arrayList.size());
		if (!CommonUtil.emptyListToString3(arrayList)) {
			WorkerItemBean workerItemBean = arrayList.get(position);


			ImageLoader.getInstance().displayImage(
					workerItemBean.icon,
					holder.iv_icon,
					ImageLoader_DisplayImageOptions.getInstance()
							.setDefaultImageTechImg());
			String name = workerItemBean.name;
			if(!TextUtils.isEmpty(name)){
				name = name.trim();
			}
			
			String gradeName = workerItemBean.gradeName;
			if(!TextUtils.isEmpty(gradeName)){
				gradeName = gradeName.trim();
			}
			
			
			holder.tv_technicianName.setText(name);
			String orderCount = workerItemBean.orderCount;
			if(TextUtils.isEmpty(orderCount)){
				orderCount = "0";
			}
			holder.tv_orderNumber.setText(orderCount + "单");
			
			holder.tv_technicianLevel.setText(gradeName);
			holder.tv_distance.setText(FormatDistanceUtil
					.formatDistance(workerItemBean.distance));
			holder.tv_pingfen.setText(workerItemBean.score + "分");
			holder.tv_technicianLevel.setText(gradeName + "技师");
			String isBusy = workerItemBean.isBusy;
			if (null != isBusy && isBusy.equals("1")) {
				holder.tv_isAppoint.setText("今日满钟");
				holder.tv_isAppoint
						.setBackgroundResource(R.drawable.icon_teachlist_full);
			} else if (null != isBusy && isBusy.equals("2")) {
				holder.tv_isAppoint.setText("今日可约");
				holder.tv_isAppoint
						.setBackgroundResource(R.drawable.icon_teachlist_normal);
			}
			holder.tv_tech_info.setText(workerItemBean.introduction);
			// 到店
			if (("0").equals(workerItemBean.isSelfOwned)) {
				holder.tv_tech_from.setText(workerItemBean.storeName);
				// 自营的
			} else if (("1").equals(workerItemBean.isSelfOwned)) {
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
			WorkerItemBean workerItemBean = arrayList.get(position);
			switch (type) {
			case type_icon://技师头像
				UmengEventUtil.door_photo(mContext);
				String icon = workerItemBean.icon;
				String name = workerItemBean.name;
				Intent intent = new Intent(mContext, Image_Show_Aty.class);
				intent.putExtra("url", icon);
				intent.putExtra("name", name);
				mContext.startActivity(intent);
				((Activity) mContext).overridePendingTransition(0, 0);
				break;
			case type_appoint://技师详情
				UmengEventUtil.door_tichniciandetail(mContext);
				String ID = workerItemBean.ID;
				Intent intent2 = new Intent(mContext, TechnicianDetail.class);
				intent2.putExtra("ID", ID);
				mContext.startActivity(intent2);
				break;
			}

		}

	}
}