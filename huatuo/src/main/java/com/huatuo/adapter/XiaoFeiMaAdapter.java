package com.huatuo.adapter;

import java.util.ArrayList;

import org.json.JSONObject;

import com.huatuo.R;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.ImageLoader_DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class XiaoFeiMaAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<JSONObject> arrayList;
	private Context mContext;

	public XiaoFeiMaAdapter(Context mContext) {
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
		JSONObject jsonObject = arrayList.get(position);
		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.activity_xiaofeima_list_listview_item, null);
			holder = new ViewHolder();
			holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
			holder.iv_status = (ImageView) convertView
					.findViewById(R.id.iv_Status);
			holder.tv_storeName = (TextView) convertView
					.findViewById(R.id.tv_storeName);
			holder.tv_xiaofeima = (TextView) convertView
					.findViewById(R.id.tv_xiaofeima);
			holder.tv_xiangmuName = (TextView) convertView
					.findViewById(R.id.tv_xiangmuName);
			holder.tv_yuyueTime = (TextView) convertView
					.findViewById(R.id.tv_yuyueTime);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if ("2".equals(jsonObject.optString("state", ""))) {
			holder.iv_status.setBackgroundResource(R.drawable.img_code_notused);
		} else if ("3".equals(jsonObject.optString("state", ""))) {
			holder.iv_status.setBackgroundResource(R.drawable.img_code_used);
		}
		ImageLoader.getInstance().displayImage(
				jsonObject.optString("icon", ""),
				holder.iv_icon,
				ImageLoader_DisplayImageOptions.getInstance()
						.setDefaultImageSmallImg());//图片icon
		holder.tv_storeName.setText(jsonObject.optString("storeName", ""));
		holder.tv_xiaofeima.setText(jsonObject.optString("exchangeCode", ""));
		holder.tv_xiangmuName.setText("服务项目:"
				+ jsonObject.optString("servName", ""));
		holder.tv_yuyueTime.setText("预约时间:"
				+ jsonObject.optString("serviceTime", ""));

		return convertView;
	}

	static class ViewHolder {
		TextView tv_storeName, tv_xiaofeima, tv_xiangmuName, tv_yuyueTime;
		ImageView iv_icon, iv_status;
	}
}
