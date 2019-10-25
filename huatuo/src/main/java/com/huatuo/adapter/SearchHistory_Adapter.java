package com.huatuo.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huatuo.R;
import com.huatuo.util.CommonUtil;

/**
 * 父ListView适配器
 * 
 * @author zihao
 * 
 */
public class SearchHistory_Adapter extends BaseAdapter {

	public Context mContext;// 上下文对象
//	private List<Tip> list;
	private List<String> list;
	/**
	 * 构造方法
	 * 
	 * @param context
	 *            // 上下文对象
	 * @param groupArr
	 *            // item标题数组
	 */
	public SearchHistory_Adapter(Context context) {
		this.mContext = context;
		this.list = new ArrayList<String>();
	}

	public void add(List<String> arrayList) {
		if(!CommonUtil.emptyListToString3(arrayList)){
			for (int i = 0; i < arrayList.size(); i++) {
				String item = arrayList.get(i);
				this.list.add(item);
	
			}
		}
		notifyDataSetChanged();
	}
	
	public void clear() {
		this.list.clear();
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_search_history_lv_item, null);
			holder = new ViewHolder();
			holder.tv_search_keyWords = (TextView) convertView.findViewById(R.id.tv_search_keyWords);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.tv_search_keyWords.setText(list.get(position));


		return convertView;
	}

	static class ViewHolder {
		TextView tv_search_keyWords;
	}

	
}
