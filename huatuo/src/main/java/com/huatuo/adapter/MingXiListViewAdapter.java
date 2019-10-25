package com.huatuo.adapter;

import java.util.ArrayList;

import org.json.JSONObject;

import com.huatuo.R;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.NumFormatUtil;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MingXiListViewAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<JSONObject> arrayList;
	private Context mContext;

	public MingXiListViewAdapter(Context mContext) {
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
		// final ViewHolder holder;

		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(
					R.layout.activity_accountdetail_list_listview_item, null);
			viewHolder.tradeType = (TextView) convertView
					.findViewById(R.id.tradeType);
			viewHolder.tv_riqi = (TextView) convertView
					.findViewById(R.id.tv_riqi);
			viewHolder.tv_biandongjine = (TextView) convertView
					.findViewById(R.id.tv_biandongjine);
			viewHolder.tv_orderNum = (TextView) convertView
					.findViewById(R.id.tv_orderNum);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (!CommonUtil.emptyListToString3(arrayList)) {
			JSONObject jsonObject = arrayList.get(position);
			viewHolder.tradeType.setText(jsonObject.optString("tradeDesc", ""));
			viewHolder.tv_riqi.setText(jsonObject.optString("transactionTime",
					""));

			String amount = jsonObject.optString("amount", "");
			viewHolder.tv_biandongjine.setText(NumFormatUtil
					.centFormatYuanToString(amount) + "");

			String tradeName = jsonObject.optString("tradeName", "");
			if ("".equals(tradeName) || tradeName.equals(null)) {
				viewHolder.tv_orderNum.setVisibility(View.GONE);
			} else {
				viewHolder.tv_orderNum.setVisibility(View.VISIBLE);
				viewHolder.tv_orderNum.setText(tradeName);
			}

		}
		return convertView;
	}

	class ViewHolder {
		TextView tradeType, tv_riqi, tv_biandongjine, tv_orderNum;
	}
}
