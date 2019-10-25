package com.huatuo.util;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
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
import com.huatuo.interfaces.HandleSortType_Interface;

public class Menu_pop_store {
	private Activity mContext;
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
	private AlphaAnimation animation;
	public PopupWindow mPopupWindow = null;
	private View mView;
	private static Menu_pop_store instance;
	private HandleSortType_Interface mhHandleSortType_Interface;
	private int[] sortType_arr_ID = new int[] { R.string.near_fromMine, R.string.appraise_high, R.string.price_low };
	private String[] sortType_arr = new String[3];
	private MyAdapter adapter;

	public static Menu_pop_store getInstance() {
		if (instance == null) {
			synchronized (Menu_pop_store.class) {
				if (instance == null) {
					instance = new Menu_pop_store();
				}
			}
		}

		return instance;
	}

	/**
	 * 动态添加排序类型
	 */
	private void addSortType() {
		for (int i = 0; i < sortType_arr_ID.length; i++) {
			sortType_arr[i] = mContext.getResources().getString(sortType_arr_ID[i]);
		}
	}

	/**
	 * 展示区域选择的对话框
	 * 
	 * @param handleInterface
	 */
	public void showPupupWindow(Activity context, View view, final HandleSortType_Interface handleSortType_Interface,
			int type) {
		mContext = context;
		mView = view;
		mhHandleSortType_Interface = handleSortType_Interface;
		addSortType();
		setPopAnimation();
		CommonUtil.log("pop:============================mPopupWindow:" + mPopupWindow);
		if (mPopupWindow == null) {
			showPupWindow = LayoutInflater.from(mContext).inflate(R.layout.store_pop, null);
			initPopuWindow(showPupWindow);
			/*
			 * radio_distanceNearest = (TextView) showPupWindow
			 * .findViewById(R.id.radio_distanceNearest); radio_appraiseHigh =
			 * (TextView) showPupWindow .findViewById(R.id.radio_appraiseHigh);
			 * radio_orderMore = (TextView) showPupWindow
			 * .findViewById(R.id.radio_orderMore);
			 */
			store_service_sortType_lv = (ListView) showPupWindow.findViewById(R.id.store_service_sortType_lv);
			adapter = new MyAdapter();
			store_service_sortType_lv.setAdapter(adapter);
			store_service_sortType_lv.setOnItemClickListener(new MyItemOnClickListener());

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
		showPupWindow.setAnimation(animation);
		showPupWindow.startAnimation(animation);
		showPupWindow.setFocusable(true);
		mPopupWindow.showAsDropDown(mView, CommonUtil.dip2px(mContext, 6.5f), CommonUtil.dip2px(mContext, 0));
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

		CommonUtil.log("默认选中的item:" + type);
		adapter.setSelecttion(type);// 设置默认选中的item

	}

	private void setPopAnimation() {
		int[] location = new int[2];
		mView.getLocationOnScreen(location);// 获取控件在屏幕中的位置,方便展示Popupwindow
		animation = new AlphaAnimation(0, 1);
		animation.setDuration(300);
	}

	/**
	 * 初始化 PopupWindow
	 * 
	 * @param view
	 */
	public void initPopuWindow(View view) {
		/* 第一个参数弹出显示view 后两个是窗口大小 */
		mPopupWindow = new PopupWindow(view, CommonUtil.dip2px(mContext, 110), LayoutParams.WRAP_CONTENT);
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
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			adapter.setSelecttion(position);

			if (null != mhHandleSortType_Interface) {
				mhHandleSortType_Interface.callBack(position, sortType_arr[position]);
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
			return sortType_arr.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return sortType_arr[position];
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.sort_type_lv_item2, null);
			sortTypeName = (TextView) convertView.findViewById(R.id.tv_sortTypeName);
			line = (View) convertView.findViewById(R.id.line);
			sortTypeName.setText(sortType_arr[position]);
			if (position == sortType_arr.length - 1) {
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
