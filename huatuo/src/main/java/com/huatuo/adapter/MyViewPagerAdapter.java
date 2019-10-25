package com.huatuo.adapter;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.huatuo.R;
import com.huatuo.util.CommonUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class MyViewPagerAdapter extends PagerAdapter {
	private Context mContext;
	private LayoutInflater inflater;
	private ArrayList<JSONObject> viewList;

	public MyViewPagerAdapter(Context mContext) {
		// this.viewList = viewList;
		this.mContext = mContext;
		inflater = LayoutInflater.from(mContext);
		viewList = new ArrayList<JSONObject>();
	}

	public void add(String item) {
		JSONObject itemJson = new JSONObject();
		try {
			itemJson.put("imageUrl", item);
			itemJson.put("imageUrl_p", item);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		viewList.add(itemJson);
		notifyDataSetChanged();
	}

	public void add(ArrayList<JSONObject> arrayList) {
		for (int i = 0; i < arrayList.size(); i++) {
			JSONObject item = arrayList.get(i);
			this.viewList.add(item);
		}
		notifyDataSetChanged();
	}

	public void clear() {
		this.viewList.clear();
	}

	@Override
	public int getCount() {
		return viewList.size();
	}

	// 这里是关键, 如果这里返回POSITION_NONE，那这一个object对应的位置的view将被重新 创建
	@Override
	public int getItemPosition(Object object) {
		// TODO Auto-generated method stub
		// Object tag = ((View)object).getTag();
		// for (int i = 0; i < viewList.size(); i++)
		// {
		// if (tag.equals(viewList.get(i)))
		// {
		// return i;
		// }
		// }
		return POSITION_NONE;
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		// destroyItem记录的views的索引是大于0的，但是重新加载的views的count为1的时候，这是removeView的时候会出现数组越界错误
		((ViewPager) container).removeView((View) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		// TODO Auto-generated method stub
		View imageLayout = inflater.inflate(
				R.layout.baby_show_image_details_pager_item, null, false);
		assert imageLayout != null;
		CommonUtil.log("instantiateItem-----------------position:" + position);
			final JSONObject jsonObject = viewList.get(position
					);
			ImageView imageView = (ImageView) imageLayout
					.findViewById(R.id.image);
			final ProgressBar spinner = (ProgressBar) imageLayout
					.findViewById(R.id.loading);
			ImageLoader.getInstance().displayImage(
					jsonObject.optString("topImage", ""), imageView,
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							spinner.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							spinner.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							spinner.setVisibility(View.GONE);
						}
					});
			imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int linkType = jsonObject.optInt("linkType", 0);
					// LogUtil.e("deesha", "linkType=" + linkType);
					String linkUrl = jsonObject.optString("linkUrl", "");
					// switch (linkType) {
					// case 0:
					// break;
					// case 1:
					// Intent intent = new Intent();
					// intent.putExtra("linkUrl", linkUrl);
					// intent.setClass(mContext, TechnicianList.class);
					// mContext.startActivity(intent);
					// break;
					// case 2:
					// intent = new Intent();
					// intent.putExtra("linkUrl", linkUrl);
					// intent.setClass(mContext, ServiceList.class);
					// mContext.startActivity(intent);
					// break;
					// case 3:
					// intent = new Intent();
					// intent.putExtra("linkUrl", linkUrl);
					// intent.setClass(mContext, TechnicianDetail.class);
					// mContext.startActivity(intent);
					// break;
					// case 4:
					// intent = new Intent();
					// intent.putExtra("linkUrl", linkUrl);
					// intent.setClass(mContext, ServiceDetail.class);
					// mContext.startActivity(intent);
					// break;
					// case 6:
					// intent = new Intent();
					// intent.putExtra("linkUrl", "http://www.baidu.com");
					// intent.setClass(mContext, ShowHtmlActivity.class);
					// mContext.startActivity(intent);
					// break;
					// default:
					// break;
					// }
				}
			});
			// view.addView(imageLayout, LayoutParams.MATCH_PARENT,
			// LayoutParams.MATCH_PARENT);
			container.addView(imageLayout);
		return imageLayout;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void finishUpdate(View arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public Parcelable saveState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
		// TODO Auto-generated method stub
	}
}