package com.huatuo.adapter;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huatuo.R;
import com.huatuo.bean.SecKillActivitydescBean;
import com.huatuo.util.CommonUtil;

public class SecKill_Session_detail_list_servInfo_list_Adapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<SecKillActivitydescBean> arrayList;
	private Activity mContext;

	public SecKill_Session_detail_list_servInfo_list_Adapter(Activity mContext) {
		this.mContext = mContext;
		inflater = LayoutInflater.from(mContext);
		arrayList = new ArrayList<SecKillActivitydescBean>();
	}

	public void add(SecKillActivitydescBean item) {
		arrayList.add(item);
		notifyDataSetChanged();
	}

	public void add(ArrayList<SecKillActivitydescBean> arrayList) {
		if (!CommonUtil.emptyListToString3(arrayList)) {
				this.arrayList = arrayList;
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
			convertView = inflater.inflate(R.layout.fragment_first_seckill_item_detail_info_item,
					null);
			holder = new ViewHolder();

			holder.tv_info_tip = (TextView) convertView
					.findViewById(R.id.tv_info_tip);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (!CommonUtil.emptyListToString3(arrayList)) {
			SecKillActivitydescBean secKillActivitydescBean = arrayList.get(position);
			String servInfo = secKillActivitydescBean.content;
			holder.tv_info_tip.setText(servInfo);

		}
		return convertView;
	}

	static class ViewHolder {
		TextView tv_info_tip;
	}

}