package com.huatuo.adapter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.huatuo.R;
import com.huatuo.util.CommonUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 父ListView适配器
 * 
 * @author zihao
 * 
 */
public class Store_Image_Gridview_Adapter extends BaseAdapter {

	public Activity mContext;// 上下文对象
	private List<JSONObject> list;
	private int mType;
	private Intent intent = null;

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            // 上下文对象
	 * @param groupArr
	 *            // item标题数组
	 */
	public Store_Image_Gridview_Adapter(Activity context) {
		this.mContext = context;
		this.list = new ArrayList<JSONObject>();
	}

	public void add(List<JSONObject> arrayList, int type) {
		mType = type;
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
					R.layout.aty_store_show_image_gv_item, null);
			holder.iv_store_gridview_item = (ImageView) convertView
					.findViewById(R.id.iv_store_gridview_item);
			
			//设置图片的为正方形
			CommonUtil.initScreen(mContext);
			LayoutParams layoutParams = holder.iv_store_gridview_item.getLayoutParams();
			layoutParams.width = CommonUtil.WIDTH_SCREEN/2-CommonUtil.dip2px(mContext, 19);
			layoutParams.height = layoutParams.width;
			holder.iv_store_gridview_item.setLayoutParams(layoutParams);
			
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		switch (mType) {
		case 0:
			String envImgUrl = list.get(position).optString("envImgUrl", "");
			CommonUtil.log("envImgUrl:" + envImgUrl);
			if (!TextUtils.isEmpty(envImgUrl)) {
				ImageLoader.getInstance().displayImage(envImgUrl,
						holder.iv_store_gridview_item);
			}

//			holder.iv_store_gridview_item
//					.setOnClickListener(new OnClickListener() {
//
//						@Override
//						public void onClick(View v) {
//							intent = new Intent(mContext,
//									S203_Store_showImage_ViewPager_Aty.class);
//							intent.putExtra("storeInfo", list.get(position)
//									.toString());
//							intent.putExtra("type", 0);
//							mContext.startActivity(intent);
//
//						}
//					});
			break;
		case 1:
			String servImgUrl = list.get(position).optString("servImgUrl", "");
			CommonUtil.log("servImgUrl:" + servImgUrl);
			if (!TextUtils.isEmpty(servImgUrl)) {
				ImageLoader.getInstance().displayImage(servImgUrl,
						holder.iv_store_gridview_item);
			}

//			holder.iv_store_gridview_item
//					.setOnClickListener(new OnClickListener() {
//
//						@Override
//						public void onClick(View v) {
//							intent = new Intent(mContext,
//									S203_Store_showImage_ViewPager_Aty.class);
//							intent.putExtra("storeInfo", list.get(position)
//									.toString());
//							intent.putExtra("type", 1);
//							mContext.startActivity(intent);
//
//						}
//					});
			break;
		case 2:
			String skillImgUrl = list.get(position)
					.optString("skillImgUrl", "");
			CommonUtil.log("skillImgUrl:" + skillImgUrl);
			if (!TextUtils.isEmpty(skillImgUrl)) {
				ImageLoader.getInstance().displayImage(skillImgUrl,
						holder.iv_store_gridview_item);
			}

			
			break;

		}

		return convertView;
	}

	static class ViewHolder {
		private ImageView iv_store_gridview_item;
	}

}
