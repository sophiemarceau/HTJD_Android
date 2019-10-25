package com.huatuo.adapter;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.huatuo.R;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.ImageLoader_DisplayImageOptions;
import com.huatuo.util.Toast_Util;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class Image_ViewPagerAdapter extends PagerAdapter {
	private Activity mContext;
	private LayoutInflater inflater;
	private ArrayList<JSONObject> viewList;

	public Image_ViewPagerAdapter(Activity mContext) {
		// this.viewList = viewList;
		this.mContext = mContext;
		inflater = LayoutInflater.from(mContext);
		viewList = new ArrayList<JSONObject>();
	}

	// public void add(String item) {
	// JSONObject itemJson = new JSONObject();
	// try {
	// itemJson.put("promotionsUrl", item);
	// itemJson.put("promotionsImage", item);
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// viewList.add(itemJson);
	// notifyDataSetChanged();
	// }

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

		View imageLayout = inflater.inflate(R.layout.baby_show_image_details_pager_item, null, false);
		assert imageLayout != null;
		try {
			final JSONObject jsonObject = viewList.get(position);

			ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
			CommonUtil.initScreen((Activity) mContext);
			// 设置图片的为正方形
			LayoutParams layoutParams_img = imageView.getLayoutParams();
			layoutParams_img.width = CommonUtil.WIDTH_SCREEN;
			layoutParams_img.height = layoutParams_img.width / 4 * 3;
			imageView.setLayoutParams(layoutParams_img);

			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);
			String imageUrl = jsonObject.optString("url", "");
			// CommonUtil.log("=====服务详情：imageUrl" + imageUrl);
			ImageLoader.getInstance().displayImage(imageUrl, imageView, ImageLoader_DisplayImageOptions.getInstance().setDefaultImageBigImg(),
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							spinner.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
							String message = null;
							switch (failReason.getType()) { // 获取图片失败类型
							case IO_ERROR: // 文件I/O错误
								message = "Input/Output error";
								break;
							case DECODING_ERROR: // 解码错误
								message = "图片解码错误";
								break;
							case NETWORK_DENIED: // 网络延迟
								message = "网络延迟";
								break;
							case OUT_OF_MEMORY: // 内存不足
								message = "手机内存不足 ";
								break;
							case UNKNOWN: // 原因不明
								message = "原因不明";
								break;
							}

							CommonUtil.logE("-----------------------------Image_Viewpagetr:获取图片失败类型 :message:" + message);
//							Toast_Util.showToastOnlyOne(mContext, message, Gravity.CENTER, false);
							spinner.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
							spinner.setVisibility(View.GONE);
						}
					});

			container.addView(imageLayout);
		} catch (Exception e) {
			// handler something
		}
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