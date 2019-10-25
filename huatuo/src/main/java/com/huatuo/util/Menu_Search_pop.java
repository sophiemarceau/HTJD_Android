package com.huatuo.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.huatuo.R;
import com.huatuo.interfaces.Select_SearchType_OnStore_Interface;

public class Menu_Search_pop {
	private Activity mContext;
	private Handler mHandler;
	private View showPupWindow = null; // 选择区域的view
	/** 一级菜单名称数组 **/
	private String[] GroupNameArray;
	/** 二级菜单名称数组 **/
	private String[] childNameArray;

	/*
	 * private TextView radio_distanceNearest = null; private TextView
	 * radio_appraiseHigh = null; private TextView radio_orderMore = null;
	 */
	private LinearLayout pop_area_empty = null;
	private ListView store_service_sortType_lv;

	private ListView childListView = null;
	private String mCurrentAreaName;
	private String mCurrentAreaCode;
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
	private View mView;
	private static Menu_Search_pop instance;
	private Select_SearchType_OnStore_Interface select_SearchType_OnStore_Interface;
	private int[] searchType_arr_ID ;
	private String[] searchType_arr = new String[2];
	private MyAdapter adapter;

	public static Menu_Search_pop getInstance() {
		if (instance == null) {
			synchronized (Menu_Search_pop.class) {
				if (instance == null) {
					instance = new Menu_Search_pop();
				}
			}
		}

		return instance;
	}

	/**
	 * 动态添加排序类型
	 */
	private void addSortType() {
		for (int i = 0; i < searchType_arr_ID.length; i++) {
			searchType_arr[i] = mContext.getResources().getString(
					searchType_arr_ID[i]);
		}
	}

	/**
	 * 展示区域选择的对话框
	 * 
	 * @param handleInterface
	 */
	public void showPupupWindow(Activity context, View view,int[] searchType_arr_ID,
			Select_SearchType_OnStore_Interface select_SearchType_OnStore_Interface) {
		mContext = context;
		mView = view;
		this.searchType_arr_ID = searchType_arr_ID;
		this.select_SearchType_OnStore_Interface = select_SearchType_OnStore_Interface;
		
		addSortType();
		getWindowsPIX();
		setPop();
		CommonUtil.log("pop:============================mPopupWindow:"
				+ mPopupWindow);
		if (mPopupWindow == null) {
			showPupWindow = LayoutInflater.from(mContext).inflate(
					R.layout.search_pop, null);
			initPopuWindow(showPupWindow);
			/*
			 * radio_distanceNearest = (TextView) showPupWindow
			 * .findViewById(R.id.radio_distanceNearest); radio_appraiseHigh =
			 * (TextView) showPupWindow .findViewById(R.id.radio_appraiseHigh);
			 * radio_orderMore = (TextView) showPupWindow
			 * .findViewById(R.id.radio_orderMore);
			 */
			store_service_sortType_lv = (ListView) showPupWindow
					.findViewById(R.id.store_service_sortType_lv);
			adapter = new MyAdapter();
			store_service_sortType_lv.setAdapter(adapter);
			store_service_sortType_lv
					.setOnItemClickListener(new MyItemOnClickListener());

			// pop_area_empty = (LinearLayout)
			// showPupWindow.findViewById(R.id.pop_area_empty);
			// pop_area_empty.setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// // TODO Auto-generated method stub
			// if (null != mPopupWindow) {
			// mPopupWindow.dismiss();
			// }
			// }
			// });
		}

		showPupWindow.setAnimation(alphaAnimation);
		showPupWindow.startAnimation(alphaAnimation);
		showPupWindow.setFocusable(true);
		mPopupWindow.showAsDropDown(mView, CommonUtil.dip2px(mContext, 6.5f),
				CommonUtil.dip2px(mContext, 0));
		// mPopupWindow.showAtLocation(mView,Gravity.TOP|Gravity.RIGHT,
		// CommonUtil.dip2px(mContext, 6.5f),CommonUtil.dip2px(mContext, 110));
		// mPopupWindow.showAsDropDown(mView);
		// 设置activity 背景颜色变灰
		// backgroundAlpha(0.7f);
		mPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stubs
				// backgroundAlpha(1f);
			}
		});

	}

	private void setPop() {
		int[] location = new int[2];
		mView.getLocationOnScreen(location);// 获取控件在屏幕中的位置,方便展示Popupwindow
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
		mPopupWindow = new PopupWindow(view, CommonUtil.dip2px(mContext, 100),
				LayoutParams.WRAP_CONTENT);
		// /* 设置背景显示 */
		// mPopupWindow.setBackgroundDrawable(mContext.getResources().getDrawable(
		// R.drawable.custom_pop_bg));
		/* 设置触摸外面时消失 */
		mPopupWindow.setOutsideTouchable(true);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(R.color.pop_menu);
		dw.setAlpha(0);
		mPopupWindow.setBackgroundDrawable(dw);

		// BitmapDrawable bt = new BitmapDrawable();
		// bt.setAlpha(0);
		// mPopupWindow.setBackgroundDrawable(bt );

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

		// WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
		WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
		lp.alpha = bgAlpha; // 0.0-1.0
		mContext.getWindow().setAttributes(lp);
	}

	class MyItemOnClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			
			if (null != select_SearchType_OnStore_Interface) {
				//0://门店1://项目
				select_SearchType_OnStore_Interface.recallSearchType(
						position);
			}

			if (null != mPopupWindow) {
				CommonUtil.log("mPopupWindow.dismiss();");
				mPopupWindow.dismiss();
			}
		}
	}

	class MyAdapter extends BaseAdapter {
		private int index = -1;

		private void setSelecttion(int position) {
			index = position;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return searchType_arr.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return searchType_arr[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			TextView sortTypeName = null;
			View line;
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.sort_type_lv_item2, null);
			sortTypeName = (TextView) convertView
					.findViewById(R.id.tv_sortTypeName);
			line = (View) convertView.findViewById(R.id.line);
			sortTypeName.setText(searchType_arr[position]);
			if (position == searchType_arr.length - 1) {
				line.setVisibility(View.GONE);
			} else {
				line.setVisibility(View.VISIBLE);
			}

			// if (index == position) {
			// sortTypeName.setBackgroundColor(mContext.getResources().getColor(R.color.pop_checked_bg));
			// } else {
			// sortTypeName.setBackgroundColor(mContext.getResources().getColor(R.color.white));
			// }
			return convertView;

		}
	}

}
