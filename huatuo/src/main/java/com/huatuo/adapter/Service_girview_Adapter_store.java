package com.huatuo.adapter;

import java.util.ArrayList;

import org.json.JSONObject;

import com.huatuo.R;
import com.huatuo.activity.project.ProjectDetailActivity;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Constants;
import com.huatuo.util.ImageLoader_DisplayImageOptions;
import com.huatuo.util.NumFormatUtil;
import com.huatuo.util.UmengEventUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 子ListView适配器
 * 
 * @author zihao
 * 
 */
public class Service_girview_Adapter_store extends BaseAdapter {

	Activity mContext;
	private ArrayList<JSONObject> arrayList;
	private String workerID = "";

	/**
	 * 构造方法
	 * 
	 * @param context
	 */
	public Service_girview_Adapter_store(Activity context) {
		mContext = context;
		arrayList = new ArrayList<JSONObject>();
	}

	public void clear() {
		this.arrayList.clear();
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

	public void add(ArrayList<JSONObject> arrayList, String workerID) {
		this.workerID = workerID;
		if (!CommonUtil.emptyListToString3(arrayList)) {
			for (int i = 0; i < arrayList.size(); i++) {
				JSONObject item = arrayList.get(i);
				this.arrayList.add(item);
			}
		}
		notifyDataSetChanged();
	}

	/**
	 * 获取item总数
	 */
	@Override
	public int getCount() {
		// if (CommonUtil.emptyListToString2(arrayList) == null) {
		// return 0;
		// }
		return arrayList.size();
	}

	/**
	 * 获取某一个Item的内容
	 */
	@Override
	public Object getItem(int position) {
		return arrayList.get(position);
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.fragment_visit_gv_item, null);
			holder.img_griview_item_icon = (ImageView) convertView.findViewById(R.id.iv_serviceImg);
			holder.tv_fuwuName = (TextView) convertView.findViewById(R.id.tv_serviceName);
			holder.tv_from_store = (TextView) convertView.findViewById(R.id.tv_shop);
			holder.tv_jiage = (TextView) convertView.findViewById(R.id.tv_price);
			holder.tv_price2 = (TextView) convertView.findViewById(R.id.tv_price2);
			holder.tv_Oldjiage = (TextView) convertView.findViewById(R.id.tv_Oldjiage);
			holder.tv_Oldjiage.setVisibility(View.VISIBLE);
			holder.tv_pingfen = (TextView) convertView.findViewById(R.id.tv_servicePingfen);
			// 设置图片的为正方形
			CommonUtil.initScreen(mContext);
			LayoutParams layoutParams = holder.img_griview_item_icon.getLayoutParams();
			layoutParams.width = (CommonUtil.WIDTH_SCREEN - CommonUtil.dip2px(mContext, 15)) / 2;
			layoutParams.height = layoutParams.width / 4 * 3;
			holder.img_griview_item_icon.setLayoutParams(layoutParams);
			holder.tv_from_store.setVisibility(View.GONE);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();

		}
		// ID 服务ID ANS 64 M 服务ID
		// name 服务名称 ANS 64 M 服务名称
		// introduction 服务介绍 ANS 64 M 服务介绍
		// icon 服务大图 ANS 64 M 服务默认大图
		// score 服务评分 ANS 64 M 项目分数
		// storeID 店铺ID ANS 64 C 店铺 ID
		// storeName 店铺名称 ANS 64 C 店铺名称
		// isSelfOwned 服务归属 ANS 4 M 0 门店 1 华佗自营
		// minPrice 最低价格 ANS 64 M 最低价格
		// distance 距离 ANS 64 M 距离，单位公里
		// isLevel 是否开启等级 N 4 M 是否开启等级(0 未启用， 1启用)
		if (!CommonUtil.emptyListToString3(arrayList)) {
			JSONObject jsonObject = arrayList.get(position);
			String ID = jsonObject.optString("ID", "");
			String name = jsonObject.optString("name", "");
			String introduction = jsonObject.optString("introduction", "");
			String icon = jsonObject.optString("icon", "");
			String score = jsonObject.optString("score", "");
			String storeID = jsonObject.optString("storeID", "");
			String storeName = jsonObject.optString("storeName", "");
			String isSelfOwned = jsonObject.optString("isSelfOwned", "");
			String minPrice = jsonObject.optString("minPrice", "");
			
			String isCollect = jsonObject.optString("isCollect", "");
			String isLevel = jsonObject.optString("isLevel", "");
			// 店铺最低价格，单位分
			if (!TextUtils.isEmpty(minPrice)) {
				minPrice = NumFormatUtil.centFormatYuanToString(minPrice);
			}

			// ImageLoader.getInstance().displayImage(icon,
			// holder.img_griview_item_icon);
			ImageLoader.getInstance().displayImage(icon, holder.img_griview_item_icon, ImageLoader_DisplayImageOptions.getInstance().setDefaultImageSmallImg());
			holder.tv_fuwuName.setText(name);

			// 是否开启等级(0 未启用， 1启用)
			if (("0").equals(isLevel)) {
				holder.tv_price2.setVisibility(View.GONE);
			} else {
				holder.tv_price2.setVisibility(View.VISIBLE);
			}

			holder.tv_jiage.setText(minPrice);
			// 到店---处理服务类型
			if (("0").equals(isSelfOwned)) {
				holder.tv_from_store.setText(storeName);
				// 自营的
			} else if (("1").equals(isSelfOwned)) {
				holder.tv_from_store.setText("华佗自营");
			}
			
			String marketPrice = jsonObject.optString("marketPrice", "");
			holder.tv_Oldjiage.setText( NumFormatUtil.centFormatYuanToString(marketPrice));// 服务项目原价价格
			holder.tv_Oldjiage.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

			holder.tv_pingfen.setText(score + "分");

			convertView.setOnClickListener(new MyOnItemOnClickListener(position));
		}
		return convertView;
	}

	static class ViewHolder {
		ImageView img_griview_item_icon;
		TextView tv_from_store, tv_fuwuName, tv_jiage, tv_price2, tv_pingfen, tv_Oldjiage;
	}

	class MyOnItemOnClickListener implements OnClickListener {
		private int position;

		public MyOnItemOnClickListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			UmengEventUtil.store_storedetail_projectdetail(mContext);
			JSONObject jsonObject = arrayList.get(position);
			CommonUtil.log("jsonObject:" + jsonObject);
			if (null != jsonObject) {
				String ID = jsonObject.optString("ID", "");
				CommonUtil.log("ID:" + ID);
				CommonUtil.log("workerID:" + workerID);
				Intent intent = new Intent();
				intent.setClass(mContext, ProjectDetailActivity.class);
				intent.putExtra("ID", ID);
				intent.putExtra("workerID", workerID);
				mContext.startActivity(intent);
			}

		}

	}

}
