package com.huatuo.adapter;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatuo.R;
import com.huatuo.activity.find.FindDetail_ServiceListActivity;
import com.huatuo.activity.find.FindDetail_StoreListActivity;
import com.huatuo.activity.find.FindDetail_TechListActivity;
import com.huatuo.activity.find.FindDetail_TextImgListActivity;
import com.huatuo.bean.FindItemBean;
import com.huatuo.custom_widget.ImageViewCircle;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.ImageLoader_DisplayImageOptions;
import com.huatuo.util.UmengEventUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

public class Find_ListViewAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<FindItemBean> mArrayList;
	private Activity mContext;

	public Find_ListViewAdapter(Activity mContext) {
		this.mContext = mContext;
		inflater = LayoutInflater.from(mContext);
		mArrayList = new ArrayList<FindItemBean>();
	}

	public void add(FindItemBean item) {
		mArrayList.add(item);
		notifyDataSetChanged();
	}

	public void add(ArrayList<FindItemBean> arrayList) {

		if (!CommonUtil.emptyListToString3(arrayList)) {
			this.mArrayList.addAll(arrayList);
			// for (int i = 0; i < arrayList.size(); i++) {
			// FindItemBean item = arrayList.get(i);
			// this.mArrayList.add(item);
			// }
		}
		notifyDataSetChanged();
	}

	public void clear() {
		this.mArrayList.clear();
	}

	@Override
	public int getCount() {
		return mArrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return mArrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		if (convertView == null) {
			convertView = inflater
					.inflate(R.layout.activity_find_lv_item, null);
			holder = new ViewHolder();
			holder.iv_find_icon = (ImageView) convertView
					.findViewById(R.id.iv_find_icon);
			holder.tv_find_content = (TextView) convertView
					.findViewById(R.id.tv_find_content);
			holder.tv_find_publish = (TextView) convertView
					.findViewById(R.id.tv_find_publish);
			// 设置图片的比例
			CommonUtil.initScreen(mContext);
			LayoutParams layoutParams = holder.iv_find_icon.getLayoutParams();
			layoutParams.width = (CommonUtil.WIDTH_SCREEN - CommonUtil.dip2px(
					mContext, 0));
			layoutParams.height = layoutParams.width / 4 * 3;
			holder.iv_find_icon.setLayoutParams(layoutParams);

			holder.rl_find_icon_bg = (RelativeLayout) convertView
					.findViewById(R.id.rl_find_icon_bg);
			LayoutParams layoutParams2 = holder.rl_find_icon_bg
					.getLayoutParams();
			layoutParams2.width = (CommonUtil.WIDTH_SCREEN - CommonUtil.dip2px(
					mContext, 0));
			layoutParams2.height = layoutParams2.width / 4 * 3;
			holder.rl_find_icon_bg.setLayoutParams(layoutParams2);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// JSONObject jsonObject = arrayList.get(position);

		// publishDate 发布日期 ANS 64 M 发布日期
		// ID 发现ID ANS 16 M 发现ID
		// image 大图标题 ANS 64 M 展示图片
		// title 文字标题 ANS 256 M 文字标题
		// pageCount 总共页数 N 10 O 总页数
		// tupleCount 总记录数 N 10 O 总数量
		// String ID = jsonObject.optString("ID", "");
		// String image = jsonObject.optString("image", "");
		// String title = jsonObject.optString("title", "");
		// String publishDate = jsonObject.optString("publishDate", "");
		// String type = jsonObject.optString("type", "");

		FindItemBean findItemBean = mArrayList.get(position);

		ImageLoader.getInstance().displayImage(
				findItemBean.image,
				holder.iv_find_icon,
				ImageLoader_DisplayImageOptions.getInstance()
						.setDefaultImageBigImg());

		holder.tv_find_content.setText(findItemBean.title);
		holder.tv_find_publish.setText(findItemBean.publishDate + "发布");
		convertView.setOnClickListener(new MyOnClickListener(position));
		return convertView;
	}

	static class ViewHolder {
		ImageView iv_find_icon;
		TextView tv_find_content, tv_find_publish;
		RelativeLayout rl_find_icon_bg;
	}

	class MyOnClickListener implements OnClickListener {
		private int position;

		public MyOnClickListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			UmengEventUtil.discover_detail(mContext);

			FindItemBean findItemBean = mArrayList.get(position);
			String type_str = findItemBean.type;
			String ID = findItemBean.ID;
			int type = -1;
			if (!TextUtils.isEmpty(type_str)) {
				type = Integer.parseInt(type_str.trim());
			}
			// type:0 门店 1 服务 2 技师 3 图文
			CommonUtil.log("---------------------type:" + type);
			Intent intent = new Intent();
			switch (type) {
			case 0:
				intent.setClass(mContext, FindDetail_StoreListActivity.class);
				intent.putExtra("ID", ID);
				mContext.startActivity(intent);
				break;
			case 1:
				intent.setClass(mContext, FindDetail_ServiceListActivity.class);
				intent.putExtra("ID", ID);
				mContext.startActivity(intent);
				break;
			case 2:
				intent.setClass(mContext, FindDetail_TechListActivity.class);
				intent.putExtra("ID", ID);
				mContext.startActivity(intent);
				break;
			case 3:
				intent.setClass(mContext, FindDetail_TextImgListActivity.class);
				intent.putExtra("ID", ID);
				mContext.startActivity(intent);
				break;
			}
		}

	}
}