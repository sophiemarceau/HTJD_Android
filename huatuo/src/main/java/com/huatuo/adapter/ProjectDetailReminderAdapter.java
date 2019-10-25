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

public class ProjectDetailReminderAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<JSONObject> arrayList;
	private Context mContext;

	public ProjectDetailReminderAdapter(Context mContext) {
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
					R.layout.projectdetail_reminder_adapter, null);
			holder = new ViewHolder();

			holder.tv_tag = (TextView) convertView.findViewById(R.id.tv_tag);
			holder.tv_content = (TextView) convertView
					.findViewById(R.id.tv_content);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv_tag.setText(arrayList.get(position).optString("tag", "")
				+ "：");
		String content = arrayList.get(position).optString("content", "");
	     holder.tv_content.setText(content);

		return convertView;
	}

	static class ViewHolder {
		TextView tv_tag;
		TextView tv_content;
	}
}
