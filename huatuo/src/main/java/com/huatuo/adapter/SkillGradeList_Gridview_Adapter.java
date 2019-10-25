package com.huatuo.adapter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.huatuo.R;
import com.huatuo.util.CommonUtil;

/**
 * 父ListView适配器
 * 
 * @author zihao
 * 
 */
public class SkillGradeList_Gridview_Adapter extends BaseAdapter {

	public Context mContext;// 上下文对象
	private List<JSONObject> list;
	private int clickTemp = -1;

	// 标识选择的Items
	public void setSeclection(int position) {
		clickTemp = position;
		notifyDataSetChanged();
	}

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            // 上下文对象
	 * @param groupArr
	 *            // item标题数组
	 */
	public SkillGradeList_Gridview_Adapter(Context context) {
		this.mContext = context;
		this.list = new ArrayList<JSONObject>();
	}

	public void add(List<JSONObject> arrayList) {

		for (int i = 0; i < arrayList.size(); i++) {
			JSONObject item = arrayList.get(i);
			this.list.add(item);

		}
		notifyDataSetChanged();
	}

	public void clear() {
		this.list.clear();
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

		// 初始化布局控件
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.service_level, null);
			holder.tv_service_level = (TextView) convertView
					.findViewById(R.id.tv_service_level);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_service_level
				.setPadding(CommonUtil.dip2px(mContext, 10),
						CommonUtil.dip2px(mContext, 3),
						CommonUtil.dip2px(mContext, 10),
						CommonUtil.dip2px(mContext, 3));
		// workerGradeID
		// workerGradeName
		// gradePrice
		// marketPrice
		String skillGrade = list.get(position).optString("workerGradeName", "");
		String skillGradePrice = list.get(position).optString("gradePrice", "");
		String isDefault = list.get(position).optString("isDefault", "");
		// holder.tv_service_level.setText(skillGrade.replace("技师", ""));
		holder.tv_service_level.setText(skillGrade);

		CommonUtil.log("默认选中postion:clickTemp:" + clickTemp + "");

		if (clickTemp == position) {
			changeCheckTabStyle(true, holder.tv_service_level);
		} else {
			changeCheckTabStyle(false, holder.tv_service_level);
		}

		if (clickTemp == -1) {
			if (("1").equals(isDefault)) {
				changeCheckTabStyle(true, holder.tv_service_level);
			}
		}

		return convertView;
	}

	static class ViewHolder {
		private TextView tv_service_level;
	}

	private void changeCheckTabStyle(boolean isChecked, TextView rd) {
		// cx.setTextColor(getResources().getColor(R.color.home_bottom_navigation_text));
		// cx.setChecked(false);
		if (isChecked) {
			rd.setTextColor(mContext.getResources().getColor(R.color.white));
			rd.setBackgroundResource(R.drawable.dialog_custom_button_yellow_bg);
		} else {
			rd.setTextColor(mContext.getResources().getColor(
					R.color.home_bottom_navigation_text_s));
			rd.setBackgroundResource(R.drawable.dialog_custom_button_bg);
		}
	}

}
