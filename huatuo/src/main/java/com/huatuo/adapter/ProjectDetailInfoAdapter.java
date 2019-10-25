package com.huatuo.adapter;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huatuo.R;

public class ProjectDetailInfoAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<JSONObject> arrayList;
	private Context mContext;

	public ProjectDetailInfoAdapter(Context mContext) {
		this.mContext = mContext;
		inflater = LayoutInflater.from(mContext);
		arrayList = new ArrayList<JSONObject>();
	}

	public void add(JSONObject item) {
		arrayList.add(item);
		notifyDataSetChanged();
	}

	public void add(ArrayList<JSONObject> arrayList) {
		for (int i = 0; i < arrayList.size(); i++) {
			JSONObject item = arrayList.get(i);
			this.arrayList.add(item);
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.activity_project_detail_info_lv_item, null);
			holder = new ViewHolder();

			holder.tv_info_tag = (TextView) convertView.findViewById(R.id.tv_info_tag);
			holder.tv_info = (TextView) convertView.findViewById(R.id.tv_info);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv_info_tag.setText(arrayList.get(position).optString("tag", "")
				+ "：");
		holder.tv_info.setText(arrayList.get(position).optString("content", ""));

		return convertView;
	}

	static class ViewHolder {
		TextView tv_info_tag,tv_info;
	}
}
