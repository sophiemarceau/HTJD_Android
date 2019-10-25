package com.huatuo.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.R.bool;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatuo.R;
import com.huatuo.util.CommonUtil;

/**
 * 父ListView适配器
 * 
 * @author zihao
 * 
 */
public class Store_quickPay_store_amount_Adapter extends BaseAdapter {

	public Context mContext;// 上下文对象
	private List<JSONObject> list;
	private int selectionPosition = -1;
	public static Map<Integer, Boolean> selected_map;

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            // 上下文对象
	 * @param groupArr
	 *            // item标题数组
	 */
	public Store_quickPay_store_amount_Adapter(Context context,
			List<JSONObject> arrayList) {
		this.mContext = context;
		this.list = arrayList;
		selected_map = new HashMap<Integer, Boolean>();
		if (!CommonUtil.emptyListToString3(arrayList)) {
			for (int i = 0; i < list.size(); i++) {
				selected_map.put(i, false);
			}
		}

	}

	public void add(List<JSONObject> arrayList) {

		for (int i = 0; i < arrayList.size(); i++) {
			JSONObject item = arrayList.get(i);
			this.list.add(item);

		}
		notifyDataSetChanged();
	}

	public void clearSelection() {
		for (int i = 0; i < selected_map.size(); i++) {
			selected_map.put(i, false);
		}
		notifyDataSetChanged();
	}

	public void clear() {
		this.list.clear();
	}

	public void setSelection(int position, boolean setChecked) {
		selectionPosition = position;
		selected_map.put(position, setChecked);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.store_quick_pay_item, null);
			holder = new ViewHolder();
			holder.tv_privilege = (TextView) convertView
					.findViewById(R.id.tv_privilege);
			holder.iv_privilege = (ImageView) convertView
					.findViewById(R.id.iv_privilege);
			holder.view_privilege_line = (View) convertView
					.findViewById(R.id.view_privilege_line);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// CommonUtil.log("tv_service_tab_type"+
		// list.get(position).optString("positionName", ""));
		JSONObject jsonObject = list.get(position);
		holder.tv_privilege.setText(jsonObject.optString("activityName"));
		CommonUtil.log("selectionPosition:" + selectionPosition);
		CommonUtil.log("position:" + position);
		// 分割线是否显示
		if (position == list.size() - 1) {
			holder.view_privilege_line.setVisibility(View.GONE);
		} else {
			holder.view_privilege_line.setVisibility(View.VISIBLE);
		}
		boolean isChecked = selected_map.get(position);
		if (selectionPosition == position) {
			if (isChecked) {
				holder.iv_privilege.setImageResource(R.drawable.icon_choice_p);
			} else {
				holder.iv_privilege.setImageResource(R.drawable.icon_choice_n);
			}
		} else {
			holder.iv_privilege.setImageResource(R.drawable.icon_choice_n);
			selected_map.put(position, false);
		}

		return convertView;
	}

	static class ViewHolder {
		private TextView tv_privilege;
		private ImageView iv_privilege;
		private View view_privilege_line;
	}

	class MyOnClickListener implements OnClickListener {
		private int position;

		public MyOnClickListener(int position) {
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onClick(View v) {

		}

	}

}
