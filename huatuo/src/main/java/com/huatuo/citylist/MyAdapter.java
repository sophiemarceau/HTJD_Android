package com.huatuo.citylist;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.huatuo.R;
import com.huatuo.util.CommonUtil;

public class MyAdapter extends BaseAdapter implements SectionIndexer{

	private List<Content> list = null;
	private Context mContext;
	private SectionIndexer mIndexer;
	
	public MyAdapter(Context mContext) {
		this.mContext = mContext;
		this.list = new ArrayList<Content>();
	}
	
	public void addList(List<Content> list){
		if(!CommonUtil.emptyListToString3(list)){
			this.list = list;
		}
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.city_list_item, null);
			viewHolder.tvTitle = (TextView) view.findViewById(R.id.title);
			viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		final Content mContent = list.get(position);
		if (position == 0) {
			viewHolder.tvLetter.setVisibility(View.VISIBLE);
			viewHolder.tvLetter.setText(mContent.getInitialCharacter());
		} else {
			String lastCatalog = list.get(position - 1).getInitialCharacter();
			if (mContent.getInitialCharacter().equals(lastCatalog)) {
				viewHolder.tvLetter.setVisibility(View.GONE);
			} else {
				viewHolder.tvLetter.setVisibility(View.VISIBLE);
				viewHolder.tvLetter.setText(mContent.getInitialCharacter());
			}
		}
		viewHolder.tvTitle.setText(this.list.get(position).getName());
		return view;

	}
	


	final static class ViewHolder {
		TextView tvTitle;
		TextView tvLetter;
	}


	public Object[] getSections() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getSectionForPosition(int position) {
		
		return 0;
	}

	public int getPositionForSection(int section) {
		Content mContent;
		String l;
		if (section == '!') {
			return 0;
		} else {
			for (int i = 0; i < getCount(); i++) {
				mContent = (Content) list.get(i);
				l = mContent.getInitialCharacter();
				char firstChar = l.toUpperCase().charAt(0);
				if (firstChar == section) {
					return i + 1;
				}

			}
		}
		mContent = null;
		l = null;
		return -1;
	}
}