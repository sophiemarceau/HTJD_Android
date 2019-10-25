package com.huatuo.adapter;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatuo.R;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.NumFormatUtil;

public class ExchangeListViewAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<JSONObject> arrayList;
	private Context mContext;

	public ExchangeListViewAdapter(Context mContext) {
		this.mContext = mContext;
		inflater = LayoutInflater.from(mContext);
		arrayList = new ArrayList<JSONObject>();
	}

	public void add(JSONObject item) {
		arrayList.add(item);
		notifyDataSetChanged();
	}

	public void add(ArrayList<JSONObject> arrayList) {
		if(!CommonUtil.emptyListToString3(arrayList)){
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
			convertView = inflater.inflate(R.layout.activity_exchange_list_adapter, null);
			holder = new ViewHolder();
			holder.iv_status = (ImageView) convertView.findViewById(R.id.iv_status);
			holder.tv_couponPrice = (TextView) convertView.findViewById(R.id.tv_couponPrice);
			holder.tv_couponName = (TextView) convertView.findViewById(R.id.tv_couponName);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.tv_useType = (TextView) convertView.findViewById(R.id.tv_useType);
			holder.ll_coupon = (LinearLayout) convertView.findViewById(R.id.ll_coupon);
			holder.rl_couponPrice = (RelativeLayout) convertView.findViewById(R.id.rl_couponPrice);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		JSONObject jsonObject = arrayList.get(position);
		holder.tv_couponPrice.setText(NumFormatUtil.centFormatYuanTodouble(jsonObject.optString("price", ""))+"");
		holder.tv_couponName.setText(jsonObject.optString("name", ""));
		holder.tv_time.setText(jsonObject.optString("startTime", "")+" ~ " + jsonObject.optString("endTime", ""));
		holder.tv_useType.setText(jsonObject.optString("introduction", ""));
		String status = jsonObject.optString("status", "0");
		if ("0".equals(status)) {//未使用
//			holder.iv_status.setImageResource(R.drawable.icon_moneyju_3x);
			holder.iv_status.setVisibility(View.GONE);
			holder.rl_couponPrice.setBackgroundResource(R.drawable.img_youhui);
		} else if ("1".equals(status)) {//1 已使用
//			holder.iv_status.setImageResource(R.drawable.icon_yishiyong_3x);
			holder.iv_status.setVisibility(View.GONE);
			holder.rl_couponPrice.setBackgroundResource(R.drawable.img_youhuiused);
		} else if ("2".equals(status)) {//2 已过期
			holder.iv_status.setBackgroundResource(R.drawable.bg_user_coupongraypos);
			holder.rl_couponPrice.setBackgroundResource(R.drawable.img_youhuiused);
		}

		return convertView;
	}

	static class ViewHolder {
		ImageView iv_mark, iv_status;
		TextView tv_couponPrice, tv_time, tv_useType,tv_couponName;
		LinearLayout ll_coupon;
		RelativeLayout rl_couponPrice;
	}
}
