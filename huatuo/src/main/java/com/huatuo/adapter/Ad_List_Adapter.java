package com.huatuo.adapter;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatuo.R;
import com.huatuo.activity.ad.AD_ServiceListActivity;
import com.huatuo.activity.ad.AD_StoreListActivity;
import com.huatuo.activity.ad.AD_TechListActivity;
import com.huatuo.bean.ADItemBean;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.ImageLoader_DisplayImageOptions;
import com.huatuo.util.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

public class Ad_List_Adapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<ADItemBean> arrayList;
	private Activity mContext;

	public Ad_List_Adapter(Activity mContext) {
		this.mContext = mContext;
		inflater = LayoutInflater.from(mContext);
		arrayList = new ArrayList<ADItemBean>();
	}

	public void add(ADItemBean item) {
		arrayList.add(item);
		notifyDataSetChanged();
	}

	public void add(ArrayList<ADItemBean> arrayList) {
		if (!CommonUtil.emptyListToString3(arrayList)) {
			this.arrayList.addAll(arrayList);
//			for (int i = 0; i < arrayList.size(); i++) {
//				ADItemBean item = arrayList.get(i);
//				this.arrayList.add(item);
//			}
		}
		notifyDataSetChanged();
	}

	public void clear() {
		this.arrayList.clear();
		notifyDataSetChanged();
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
			convertView = inflater
					.inflate(R.layout.first_ad_list_adapter, null);
			holder = new ViewHolder();
			holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
			
			holder.ll_midle = (LinearLayout) convertView
					.findViewById(R.id.ll_midle);
			holder.tv_guanggaoyu = (TextView) convertView
					.findViewById(R.id.tv_guanggaoyu);
			holder.tv_guanggaomiaoshu = (TextView) convertView
					.findViewById(R.id.tv_guanggaomiaoshu);
			holder.view_ad_bootom = (View) convertView.findViewById(R.id.view_ad_bootom);
			// 设置图片的比例
			CommonUtil.initScreen(mContext);
			LayoutParams layoutParams = holder.iv_icon.getLayoutParams();
			layoutParams.width = (CommonUtil.WIDTH_SCREEN - CommonUtil.dip2px(
					mContext, 20));
			layoutParams.height = layoutParams.width / 2;
			holder.iv_icon.setLayoutParams(layoutParams);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (!CommonUtil.emptyListToString3(arrayList)) {
			ADItemBean adItemBean = arrayList.get(position);

			/*
			 * storeList 店铺列表 storeID 店铺ID stroeName 店铺名称 storeScore 店铺分数
			 * orderNum 店铺单数 storeAddress 店铺地址 workingTime_s 营业开始时间
			 * workingTime_e 营业结束时间 distance 距离 storeLogoImg 店铺图片
			 */
			String icon = adItemBean.icon;
			String slogan = adItemBean.slogan;
			String introduction = adItemBean.introduction;

			ImageLoader.getInstance().displayImage(
					icon,
					holder.iv_icon,
					ImageLoader_DisplayImageOptions.getInstance()
							.setDefaultBanner());
			holder.tv_guanggaoyu.setText(slogan);
			holder.tv_guanggaomiaoshu.setText(introduction);
			if(!TextUtils.isEmpty(introduction) && !TextUtils.isEmpty(StringUtil.replaceBlank(introduction))){
			  holder.tv_guanggaomiaoshu.setVisibility(View.VISIBLE);
			}else {
				holder.tv_guanggaomiaoshu.setVisibility(View.GONE);
			}
			convertView.setOnClickListener(new MyOnClickListener(position));
			
//			if(position == arrayList.size()-1){
//				holder.view_ad_bootom.setVisibility(View.GONE);
//			}else {
//				holder.view_ad_bootom.setVisibility(View.VISIBLE);
//			}
		}
		return convertView;
	}

	static class ViewHolder {
		ImageView iv_icon;
		TextView tv_guanggaoyu, tv_guanggaomiaoshu;
		View view_ad_bootom;
		LinearLayout ll_midle;
	}

	class MyOnClickListener implements OnClickListener {
		private int position;

		public MyOnClickListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			ADItemBean adItemBean = arrayList.get(position);
			CommonUtil.log("adItemBean.:" + adItemBean);
			if (null != adItemBean) {
				String ID = adItemBean.ID;
				String adName = adItemBean.name;
				CommonUtil.log("ID:" + ID);
				String itemType = adItemBean.itemType;
				CommonUtil.log("ID:" + ID);
				CommonUtil.log("adName:" + adName);
				CommonUtil.log("itemType:" + itemType);// 广告内容类型，0门店，1服务，2技师
				Intent intent = new Intent();
				if (!TextUtils.isEmpty(itemType)) {
					if (("0").equals(itemType)) {
						intent.setClass(mContext, AD_StoreListActivity.class);
						intent.putExtra("ID", ID);
						intent.putExtra("adName", adName);
						mContext.startActivity(intent);
					}
					if (("1").equals(itemType)) {
						intent.setClass(mContext, AD_ServiceListActivity.class);
						intent.putExtra("ID", ID);
						intent.putExtra("adName", adName);
						mContext.startActivity(intent);
					}
					if (("2").equals(itemType)) {
						intent.setClass(mContext, AD_TechListActivity.class);
						intent.putExtra("ID", ID);
						intent.putExtra("adName", adName);
						mContext.startActivity(intent);
					}
				}
			}
		}

	}
}
