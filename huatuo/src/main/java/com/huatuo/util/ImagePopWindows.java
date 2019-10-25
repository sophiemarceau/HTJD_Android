package com.huatuo.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatuo.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ImagePopWindows {
	private Activity mContext;
	private View showPupWindow = null; // 选择区域的view
	private int type;
	private ViewPager viewPager;
	private ImagePagerAdapter adapter;
	// 总页面的集合
	private List<ImageView> ivs;
	private int img_position;

	AlphaAnimation alphaAnimation;
	// TranslateAnimation animation;// 出现的动画效果
	// 屏幕的宽高
	private int screen_width = 0;
	private int screen_height = 0;
	// 屏幕宽度
	private WindowManager windowManager;
	private int sw;
	private int sh;
	public PopupWindow mPopupWindow = null;
	private TextView mtextView;
	private static ImagePopWindows instance;
	private ArrayList<JSONObject> list;

	public static ImagePopWindows getInstance() {
		if (instance == null) {
			synchronized (ImagePopWindows.class) {
				if (instance == null) {
					instance = new ImagePopWindows();
				}
			}
		}

		return instance;
	}

	/**
	 * 展示区域选择的对话框
	 * 
	 * @param handleInterface
	 */
	public void showPupupWindow(Activity context, int type, int img_position,
			JSONObject storeDetail) {
		mContext = context;
		this.type = type;
		this.img_position = img_position;
		CommonUtil.log("this.img_position:" + this.img_position);
		getWindowsPIX();
		setPop();
		CommonUtil.log("pop:============================mPopupWindow:"
				+ mPopupWindow);
		if (mPopupWindow == null) {
			showPupWindow = LayoutInflater.from(mContext).inflate(
					R.layout.image_viewpager_activity, null);
			initView(showPupWindow);
			initPopuWindow(showPupWindow);
		}

		handeDataByType(storeDetail);
		//
		showPupWindow.setAnimation(alphaAnimation);
		showPupWindow.startAnimation(alphaAnimation);

		showPupWindow.setFocusable(true);
		// mPopupWindow.showAsDropDown(location_line, 0, 0);
		mPopupWindow.showAtLocation(showPupWindow,

		Gravity.CENTER, 0, 0);

		// 设置activity 背景颜色变灰
		backgroundAlpha(1.0f);
		mPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stubs
				backgroundAlpha(1.0f);
			}
		});

	}

	private void setPop() {
		// int[] location = new int[2];
		// mtextView.getLocationOnScreen();// 获取控件在屏幕中的位置,方便展示Popupwindow
		// animation = new TranslateAnimation(0, 0, -700, location[1]);

		alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(300);
	}

	/**
	 * 获取屏幕横宽像素值
	 */
	private void getWindowsPIX() {
		DisplayMetrics dm = new DisplayMetrics();
		mContext.getWindowManager().getDefaultDisplay().getMetrics(dm); // 获取手机屏幕的大小
		screen_width = dm.widthPixels;
		screen_height = dm.heightPixels;

		//
		windowManager = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE); // 屏幕宽度
		sw = windowManager.getDefaultDisplay().getWidth();
		// 屏幕高度
		sh = windowManager.getDefaultDisplay().getHeight();

	}

	/**
	 * 初始化 PopupWindow
	 * 
	 * @param view
	 */
	public void initPopuWindow(View view) {
		/* 第一个参数弹出显示view 后两个是窗口大小 */
		mPopupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		/* 设置背景显示 */
		// mPopupWindow.setBackgroundDrawable(getResources().getDrawable(
		// R.drawable.custom_dialog_bg));
		/* 设置触摸外面时消失 */
		// mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindow.update();
		mPopupWindow.setTouchable(true);
		/* 设置点击menu以外其他地方以及返回键退出 */
		mPopupWindow.setFocusable(true);

		/**
		 * 1.解决再次点击MENU键无反应问题 2.sub_view是PopupWindow的子View
		 */
		view.setFocusableInTouchMode(true);
	}

	/**
	 * 设置添加屏幕的背景透明度
	 * 
	 * @param bgAlpha
	 */
	public void backgroundAlpha(float bgAlpha) {
		WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
		lp.alpha = bgAlpha; // 0.0-1.0
		mContext.getWindow().setAttributes(lp);
	}

	private void handeDataByType(JSONObject storeDetail) {
		CommonUtil.log("type:" + type);
		switch (type) {
		case 0:// storeEnvImgList 店铺环境图片
			list = JsonUtil.jsonArray2List(storeDetail
					.optJSONArray("storeEnvImgList"));

			break;
		case 1:// 店铺服务图片
			list = JsonUtil.jsonArray2List(storeDetail
					.optJSONArray("servImgList"));
			break;
		case 2:// 店铺技师图片
			list = JsonUtil.jsonArray2List(storeDetail
					.optJSONArray("skillmgList"));

			break;
		}

		if (!CommonUtil.emptyListToString3(list)) {

			ivs = new ArrayList<ImageView>();
			for (int i = 0; i < list.size(); i++) {
				ImageView iv = new ImageView(mContext);
				ivs.add(iv);
			}
			adapter.clear();
			adapter.add(list, type);
			viewPager.setCurrentItem(img_position);
		}

	}

	private void initView(View showPupWindow) {
//		viewPager = (ViewPager) showPupWindow
//				.findViewById(R.id.image_viewpager);
		adapter = new ImagePagerAdapter();
		viewPager.setAdapter(adapter);

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
			}

			@Override
			public void onPageScrolled(int position, float arg1, int arg2) {
				// goodvp_imageView1.setImageResource(liulan[position]);

			}

			@Override
			public void onPageScrollStateChanged(int position) {

			}
		});
	}

	private class ImagePagerAdapter extends PagerAdapter {
		private LayoutInflater inflater = mContext.getLayoutInflater();
		private ImageView item_imageView;
		private RelativeLayout goodvp_layout;
		private ArrayList<JSONObject> list;
		private int type;

		public ImagePagerAdapter() {
			super();
			this.list = new ArrayList<JSONObject>();
		}

		public void add(List<JSONObject> arrayList, int type) {

			this.type = type;
			for (int i = 0; i < arrayList.size(); i++) {
				JSONObject item = arrayList.get(i);
				this.list.add(item);

			}
			notifyDataSetChanged();
		}

		public void clear() {
			this.list.clear();
		}

		// 销毁页面
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			viewPager.removeView(ivs.get(position));
		}

		// 总页面数
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object instantiateItem(ViewGroup view, final int position) {
			View imageLayout = inflater.inflate(R.layout.image_viewpager_item,
					view, false);
			item_imageView = (ImageView) imageLayout
					.findViewById(R.id.viewpager_item);
			String imgUrl = null;
			switch (this.type) {
			case 0:
				imgUrl = list.get(position).optString("envImgUrl", "");
				break;
			case 1:
				imgUrl = list.get(position).optString("servImgUrl", "");
				break;
			case 2:
				imgUrl = list.get(position).optString("skillImgUrl", "");
				break;
			}

			if (!TextUtils.isEmpty(imgUrl)) {
				ImageLoader.getInstance().displayImage(imgUrl, item_imageView);
			}
			imageLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (null != mPopupWindow) {
						mPopupWindow.dismiss();
					}

				}
			});

			((ViewPager) view).addView(imageLayout, 0); // 将图片增加到ViewPager
			return imageLayout;

		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}
	}

}
