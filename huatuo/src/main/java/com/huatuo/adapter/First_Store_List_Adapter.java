package com.huatuo.adapter;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatuo.R;
import com.huatuo.activity.store.StoreDetail_Aty;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.FormatDistanceUtil;
import com.huatuo.util.ImageLoader_DisplayImageOptions;
import com.huatuo.util.NumFormatUtil;
import com.huatuo.util.UmengEventUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

public class First_Store_List_Adapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<JSONObject> arrayList;
	private Activity mContext;

	public First_Store_List_Adapter(Activity mContext) {
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
			convertView = inflater.inflate(R.layout.first_store_list_adapter,
					null);
			holder = new ViewHolder();

			holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
			holder.tv_storeName = (TextView) convertView
					.findViewById(R.id.tv_storeName);
			holder.tv_detail = (TextView) convertView
					.findViewById(R.id.tv_detail);
			holder.tv_distance = (TextView) convertView
					.findViewById(R.id.tv_distance);
			holder.tv_score = (TextView) convertView
					.findViewById(R.id.tv_score);
			holder.tv_price = (TextView) convertView
					.findViewById(R.id.tv_price);
			holder.tv_price1 = (TextView) convertView
					.findViewById(R.id.tv_price1);
			holder.tv_price2 = (TextView) convertView
					.findViewById(R.id.tv_price2);

			CommonUtil.initScreen(mContext);
			LayoutParams layoutParams = holder.iv_icon.getLayoutParams();
			layoutParams.width = (CommonUtil.WIDTH_SCREEN - CommonUtil.dip2px(
					mContext, 20));
			layoutParams.height = layoutParams.width / 4 * 3;
			holder.iv_icon.setLayoutParams(layoutParams);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (!CommonUtil.emptyListToString3(arrayList)) {
			JSONObject jsonObject = arrayList.get(position);

			/*
			 * storeList 店铺列表 ID 店铺ID name 店铺名称 score 店铺分数 orderCount 店铺单数
			 * isCollect 店铺地址 introduction 营业开始时间 minPrice 营业结束时间 distance 距离
			 * icon 店铺图片
			 */
			String ID = jsonObject.optString("ID", "");
			String name = jsonObject.optString("name", "");
			String score = jsonObject.optString("score", "");
			String isCollect = jsonObject.optString("isCollect", "");
			String introduction = jsonObject.optString("introduction", "");
			String minPrice = NumFormatUtil.centFormatYuanToString(jsonObject
					.optString("minPrice", "")) + "";
			String distance = jsonObject.optString("distance", "");
			String icon = jsonObject.optString("icon", "");
			String orderCount = jsonObject.optString("orderCount", "");
			String isReservable = jsonObject.optString("isReservable", "");

			ImageLoader.getInstance().displayImage(
					icon,
					holder.iv_icon,
					ImageLoader_DisplayImageOptions.getInstance()
							.setDefaultImageBigImg());
			holder.tv_storeName.setText(name);

			holder.tv_detail.setText(introduction);

			SpannableStringBuilder spanBuilder = new SpannableStringBuilder(score);
			spanBuilder.setSpan(new TextAppearanceSpan(mContext, R.style.store_score_style1), 0, 1, 0);
			spanBuilder.setSpan(new TextAppearanceSpan(mContext, R.style.store_score_style2), 2, 3, 0);
			holder.tv_score.setText(spanBuilder);

			holder.tv_price.setText(minPrice);
			holder.tv_distance.setText(FormatDistanceUtil
					.formatDistance(distance));

			if ("0".equals(isReservable)) {
				holder.tv_price.setVisibility(View.GONE);
				holder.tv_price1.setVisibility(View.GONE);
				holder.tv_price2.setVisibility(View.GONE);
			} else {
				holder.tv_price.setVisibility(View.VISIBLE);
				holder.tv_price1.setVisibility(View.VISIBLE);
				holder.tv_price2.setVisibility(View.VISIBLE);
			}

			convertView.setOnClickListener(new MyOnClickListener(position));
		}
		return convertView;
	}

	static class ViewHolder {
		ImageView iv_icon;
		TextView tv_storeName, tv_detail, tv_distance, tv_score, tv_price,
				tv_price1, tv_price2;
	}

	class MyOnClickListener implements OnClickListener {
		private int position;

		public MyOnClickListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			UmengEventUtil.store_storedetail(mContext);
			// TODO Auto-generated method stub
			JSONObject jsonObject = arrayList.get(position);
			String ID = "";
			if (null != jsonObject) {
				ID = jsonObject.optString("ID", "");
			}
			Intent intent = new Intent();
			intent.setClass(mContext, StoreDetail_Aty.class);
			intent.putExtra("ID", ID);
			mContext.startActivity(intent);
		}

	}
}
