package com.huatuo.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.huatuo.R;
import com.huatuo.activity.login.LoginActivity;
import com.huatuo.activity.personal.Collect_ListActivity;
import com.huatuo.activity.seach.Search_OnVisitAty;
import com.huatuo.activity.select_address.SelectAddress_SearchAreaActivity;
import com.huatuo.adapter.Ad_List_Adapter;
import com.huatuo.adapter.Service_girview_Adapter_Bean;
import com.huatuo.adapter.Technician_ListViewAdapter_Bean;
import com.huatuo.base.BaseFragment;
import com.huatuo.base.MyApplication;
import com.huatuo.bean.ADItemBean;
import com.huatuo.bean.NavContentBean;
import com.huatuo.bean.NavListBean;
import com.huatuo.bean.NavListItemBean;
import com.huatuo.bean.ServiceItemBean;
import com.huatuo.bean.WorkerItemBean;
import com.huatuo.custom_widget.CustomGridView;
import com.huatuo.custom_widget.CustomHorizontalScrollView;
import com.huatuo.custom_widget.CustomListView;
import com.huatuo.dictionary.MsgId;
import com.huatuo.dictionary.RequestUrl;
import com.huatuo.interfaces.HandleSortType_Interface;
import com.huatuo.net.http.RequestData;
import com.huatuo.net.http.RequestRunnable;
import com.huatuo.net.thread.GetNavigationContent;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Constants;
import com.huatuo.util.DialogUtils;
import com.huatuo.util.Menu_pop_visit;
import com.huatuo.util.Toast_Util;
import com.huatuo.util.UmengEventUtil;

public class FragmentVisit extends BaseFragment implements OnClickListener {
	private View v_ad;
	private View v;
	private Context mContext;
	private Intent intent = new Intent();
	// 服务项目横向滚动tab
	private LinearLayout horizontal_tab_container;

	private TextView tv_location;
	private RelativeLayout rl_locaiton;

	private View headerView;
	private PullToRefreshListView mPullToRefreshListView;
	private ListView lv_visit_tech;
	private CustomListView lv_ad_header;

	private PullToRefreshScrollView mPullRefreshScrollView;
	private ScrollView mScrollView;
	private CustomGridView gridv_visit;
	private CustomListView lv_visit_ad;
	private Service_girview_Adapter_Bean service_girview_Adapter;
	private Technician_ListViewAdapter_Bean technician_ListViewAdapter;
	private Ad_List_Adapter ad_List_Adapter_servHeader;
	private Ad_List_Adapter ad_List_Adapter_workerHeader;

	public static TextView tv_first_city_select;
	public ImageView iv_sort, iv_serch;
	private final int REQUEST_SELECT_ADDRESS = 100;
	public static String city = "城市";
	private ArrayList<JSONObject> cityList;
	private int a = 0, b = 0, c = 0;

	private String orderByKey;
	private int orderByType;
	private int pageNo = 1;// 第几页
	private String pageSize = Constants.PAGE_SIZE_LIST;
	private int pageCount;// 总共页数
	private int totalCount;
	private boolean isBottom = true;
	// 导航tab
	private String tabId = "";
	private String tabName = "";
	// type 标签类型 ANS 4 M 0:普通导航,1:附近,2: 收藏(默认展示标签)
	// contextType 标签内容类型 ANS 4 M 0 门店，1服务，2技师
	private String tabType = "";
	private int contextType = -100;

	private RequestRunnable getNavigationList;
	public Handler mHandler_navigationList;
	private ArrayList<NavListItemBean> navigationList;

	// 导航内容
	private RequestRunnable getNavigationContent;
	public Handler mHandler_navigationContent;

	private ArrayList<ADItemBean> adList;//
	private ArrayList<WorkerItemBean> techList;
	private ArrayList<ServiceItemBean> serviceList;

	// 筛选条件
	private String orderBy = "0";// 0按距离，1 按评价数量，2 按订单数量，3 按价格
	// 定位服务
	public static ServiceConnection conn;
	private HandleSortType_Interface handleSortType_Interface;
	private boolean isShowLoading = false;// 是否显示加载框

	private String now_city = "";
	private String now_address = "";
	private String now_citycode = "";
	private String now_open_status = "";
	private String now_lng = "";
	private String now_lat = "";
	private String current_address = "";// 当前页面地址
	private RelativeLayout rl_loadData_error, rl_loadData_empty;
	private boolean loadDataContentError = true;// 用于加载导航栏失败
												// 为false；加载导航内容失败为：true

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		v = inflater.inflate(R.layout.fragment_visit2, container, false);
		mContext = getActivity();
		findViewById();
		initHandler();
		refreshCheckedOrderType();
		refreshCurentAddress();
		showSelectedCity();// 获取选中的城市信息
		getNavigationList();
		return v;
	}

	private void initHandler() {
		mHandler_navigationList = new HandlerNavigationList();
		mHandler_navigationContent = new HandlerNavigationContent();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		// 该fragment 显示时候 加刷新数据
		CommonUtil.log("-------------------onHiddenChanged--------------------"
				+ hidden);

		if (!hidden) {
			showSelectedCity();

			CommonUtil.log("上门：current_address:" + current_address);
			CommonUtil.log("上门：onHiddenChanged：now_city:" + now_city);
			CommonUtil.log("上门：now_citycode:" + now_citycode);
			CommonUtil.log("上门：onHiddenChanged：now_address:" + now_address);
			if (current_address != now_address) {
				getNavigationList();
				refreshCurentAddress();
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUEST_SELECT_ADDRESS) {
				refreshCurentAddress();
				showSelectedCity();
				pageNo = 1;
				getNavigationList();
			}
		}
	}

	/**
	 * 刷新当前地址
	 */
	private void refreshCurentAddress() {
		current_address = now_address;
	}

	private void showSelectedCity() {

		now_city = CommonUtil.getStringOfSharedPreferences(
				mContext.getApplicationContext(), "NOW_CITY", "");
		now_address = CommonUtil.getStringOfSharedPreferences(
				mContext.getApplicationContext(), "NOW_ADDRESS", "");
		now_citycode = CommonUtil.getStringOfSharedPreferences(
				mContext.getApplicationContext(), "NOW_CITYCODE", "");
		now_open_status = CommonUtil.getStringOfSharedPreferences(
				mContext.getApplicationContext(), "NOW_OPEN_STATUS", "");
		now_lng = CommonUtil.getStringOfSharedPreferences(
				mContext.getApplicationContext(), "NOW_LNG", "");
		now_lat = CommonUtil.getStringOfSharedPreferences(
				mContext.getApplicationContext(), "NOW_LAT", "");

		CommonUtil.log("---showSelectedCity------------now_city:" + now_city);
		CommonUtil.log("---showSelectedCity------------now_address:"
				+ now_address);
		CommonUtil.log("---showSelectedCity------------now_citycode:"
				+ now_citycode);
		CommonUtil.log("---showSelectedCity------------now_open_status:"
				+ now_open_status);
		CommonUtil.log("---showSelectedCity------------now_lng:" + now_lng);
		CommonUtil.log("---showSelectedCity------------now_lat:" + now_lat);

		String city = "城市";
		if (!TextUtils.isEmpty(now_city)) {
			String lastChar = String
					.valueOf(now_city.charAt(now_city.length() - 1));
			if (lastChar.equals("市")) {
				city = now_city.substring(0, now_city.length() - 1);
			} else {
				city = now_city;
			}
		}

		refreshCurentAddress();
		tv_first_city_select.setText(city);
		tv_location.setText(now_address);
	}

	/**
	 * 设置服务tab默认第一个选中
	 */
	private void setFirstTabChecked() {
		TextView tv = (TextView) horizontal_tab_container.getChildAt(0);
		changeTabStyle(tv, true);
	}

	private void findViewById() {
		v_ad = (View) v.findViewById(R.id.v_ad);
		iv_serch = (ImageView) v.findViewById(R.id.iv_search);
		tv_first_city_select = (TextView) v
				.findViewById(R.id.tv_first_city_select);
		iv_sort = (ImageView) v.findViewById(R.id.iv_sort);

		// 服务项目的tab
		horizontal_tab_container = (LinearLayout) v
				.findViewById(R.id.horizontal_tab_service_container);

		mPullRefreshScrollView = (PullToRefreshScrollView) v
				.findViewById(R.id.pull_refresh_scrollview);
		mScrollView = mPullRefreshScrollView.getRefreshableView();

		tv_location = (TextView) v.findViewById(R.id.tv_locationAddress);
		rl_locaiton = (RelativeLayout) v.findViewById(R.id.rl_locaiton);

		// 广告
		lv_visit_ad = (CustomListView) v.findViewById(R.id.lv_visit_ad);
		ad_List_Adapter_servHeader = new Ad_List_Adapter(getActivity());
		lv_visit_ad.setAdapter(ad_List_Adapter_servHeader);

		// 服务
		gridv_visit = (CustomGridView) v.findViewById(R.id.gridv_visit);
		service_girview_Adapter = new Service_girview_Adapter_Bean(
				getActivity());
		gridv_visit.setAdapter(service_girview_Adapter);

		// 技师
		mPullToRefreshListView = (PullToRefreshListView) v
				.findViewById(R.id.lv_visit_tech);
		lv_visit_tech = mPullToRefreshListView.getRefreshableView();

		headerView = LayoutInflater.from(getActivity()).inflate(
				R.layout.fragment_adlist, null);
		lv_ad_header = (CustomListView) headerView
				.findViewById(R.id.lv_ad_header);
		ad_List_Adapter_workerHeader = new Ad_List_Adapter(getActivity());
		lv_ad_header.setAdapter(ad_List_Adapter_workerHeader);
		lv_visit_tech.addHeaderView(headerView);

		technician_ListViewAdapter = new Technician_ListViewAdapter_Bean(
				getActivity());
		lv_visit_tech.setAdapter(technician_ListViewAdapter);
		setListener();

		// 加载数据失败页面
		rl_loadData_error = (RelativeLayout) v
				.findViewById(R.id.rl_loadData_error);
		rl_loadData_error.setOnClickListener(this);
		// 加载数据前的白界面
		rl_loadData_empty = (RelativeLayout) v
				.findViewById(R.id.rl_loadData_empty);

	}

	private void setListener() {
		tv_first_city_select.setOnClickListener(this);
		iv_sort.setOnClickListener(this);
		iv_serch.setOnClickListener(this);

		// 刷新
		setOnRefreshListener();
		// 选择地址
		rl_locaiton.setOnClickListener(this);
	}

	private void setOnRefreshListener() {

		mPullRefreshScrollView
				.setOnRefreshListener(new OnRefreshListener2<ScrollView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						refresh();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						loadMore();
					}
				});

		mPullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// TODO Auto-generated method stub
						refresh();
					}
				});

		mPullToRefreshListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						// TODO Auto-generated method stub
						loadMore();
					}
				});
	}

	private void refresh() {
		pageNo = 1;
		if (CommonUtil.emptyListToString3(navigationList)) {
			getNavigationList();
		} else {
			getNavigationContent();
		}

	}

	public void loadMore() {
		CommonUtil.log("pageNo:" + pageNo);
		CommonUtil.log("pageCount:" + pageCount);

		if (pageNo < pageCount) {
			++pageNo;
			getNavigationContent();
		} else {
			// contextType 导航类型 0 门店 1 项目 2 技师
			switch (contextType) {
			case 2:// 技师
				mPullToRefreshListView.onRefreshComplete();
				break;
			case 1:// 项目
				mPullRefreshScrollView.onRefreshComplete();
				break;
			default:
				break;
			}

//			if (pageNo > 1 && pageNo == pageCount) {
//				Toast_Util.showToastOnlyOne(mContext, mContext.getResources()
//						.getString(R.string.load_no_more_data));
//			}
		}
	}

	/**
	 * 处理tab
	 */
	private void handleHorizontalTab() {

		CommonUtil.log("----------------navigationList:" + navigationList);
		horizontal_tab_container.removeAllViews();

		if (!CommonUtil.emptyListToString3(navigationList)) {
			for (int i = 0; i < navigationList.size(); i++) {

				// navigationList 分类导航列表 JsonArray M 分类导航列表
				// ID 分类导航ID ANS 64 M 分类导航ID(默认展示标签)
				// name 分类导航名称 ANS 128 M 分类导航名称(默认展示标签)
				// type 标签类型 ANS 4 M 0:普通导航,1:附近,2: 收藏(默认展示标签)

				final TextView fuwuName = new TextView(mContext);
				fuwuName.setId(i);
				fuwuName.setPadding(CommonUtil.dip2px(mContext, 8),
						CommonUtil.dip2px(mContext, 0),
						CommonUtil.dip2px(mContext, 8),
						CommonUtil.dip2px(mContext, 0));
				LayoutParams layoutParams = new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				if (i == navigationList.size() - 1) {
					layoutParams.setMargins(0, 0,
							CommonUtil.dip2px(mContext, 10), 0);
				} else {
					layoutParams.setMargins(0, 0,
							CommonUtil.dip2px(mContext, 25), 0);
				}

				fuwuName.setLayoutParams(layoutParams);
				fuwuName.setGravity(Gravity.CENTER);
				fuwuName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.6f); // 14sp
				fuwuName.setTextColor(mContext.getResources().getColor(
						R.color.nearShop_select_color));

				NavListItemBean navListItemBean = navigationList.get(i);
				if (null != navListItemBean) {
					// tabId = jsonObject.optString("ID","");
					tabName = navListItemBean.name;
					// tabType = jsonObject.optString("type","");

					fuwuName.setText(tabName);

					fuwuName.setOnClickListener(new TabOnClickListener());
					horizontal_tab_container.addView(fuwuName);

				}
			}

			// 默认选择第一个
			// changeTab(navigationList.size() - 1);
			getDefaultTag();
		} else {
			clearData();
		}

	}

	class TabOnClickListener implements OnClickListener {

		public TabOnClickListener() {
		}

		@Override
		public void onClick(View v) {
			// 判断刷新动画是否消失
			if (mPullRefreshScrollView.isRefreshing()) {
				mPullRefreshScrollView.onRefreshComplete();
			}
			if (mPullToRefreshListView.isRefreshing()) {
				mPullToRefreshListView.onRefreshComplete();
			}

			int checkedId = v.getId();
			CommonUtil.log("checkedId:" + checkedId);
			changeTab(checkedId);
		}

	}

	/**
	 * 选中tab的操作
	 * 
	 * @param checkedId
	 */
	private void changeTab(int checkedId) {
		TextView tv;
		int tv_id;
		for (int i = 0; i < horizontal_tab_container.getChildCount(); i++) {
			tv_id = horizontal_tab_container.getChildAt(i).getId();
			tv = (TextView) horizontal_tab_container.getChildAt(i);
			if (checkedId == tv_id) {
				String checkedName = tv.getText().toString();
				changeTabStyle(tv, true);
				// 获取选中的tag标签
				getSelectedTabInfo(checkedId);

				HashMap<String, String> map = new HashMap<String, String>();
				map.put("tagName", checkedName);
				UmengEventUtil.door_horizontal_tab(mContext, map);

			} else {
				changeTabStyle(tv, false);
			}
		}
	}

	/**
	 * 获取默认tag
	 */
	private void getDefaultTag() {
		boolean isDefault_b = false;
		for (int i = 0; i < navigationList.size(); i++) {
			NavListItemBean navListItemBean = navigationList.get(i);
			String isDefault = navListItemBean.isDefault;// 非默认0 默认1
			if (!TextUtils.isEmpty(isDefault)) {
				if (("1").equals(isDefault.trim())) {
					String tabName = navListItemBean.name;
					CommonUtil.log("默认的tag:tabName" + tabName);
					changeTab(tabName);
					isDefault_b = true;
					break;
				} else {
					isDefault_b = false;
				}
			} else {
				isDefault_b = false;
			}
		}

		// 无默认值，默认选中第一个
		if (!isDefault_b) {
			CommonUtil
					.log("========================无默认的tag===================");
			changeTab(0);
		}

	}

	/**
	 * 选中tab的操作
	 * 
	 * @param checkedTagName
	 *            通过tag的name
	 */
	private void changeTab(String checkedTagName) {
		TextView tv;
		for (int i = 0; i < horizontal_tab_container.getChildCount(); i++) {
			tv = (TextView) horizontal_tab_container.getChildAt(i);
			if (checkedTagName.equals(tv.getText().toString().trim())) {
				CommonUtil.log("默认的tag:checkedTagName:" + checkedTagName
						+ "----tv.getText().toString().trim()："
						+ tv.getText().toString().trim());
				String checkedName = tv.getText().toString();
				// CommonUtil.log("选中的部位Name:" + checkedName);
				changeTabStyle(tv, true);
				getSelectedTabInfo(tv.getId());
				// horizontal_tab_container.getChildAt(i).setFocusable(true);
				// horizontal_tab_container.getChildAt(i).setFocusableInTouchMode(true);
				// horizontal_tab_container.getChildAt(i).requestFocus();

			} else {
				changeTabStyle(tv, false);
			}

		}
	}

	/**
	 * 获取选中tab信息
	 * 
	 * @param index
	 */
	private void getSelectedTabInfo(int index) {
		for (int i = 0; i < navigationList.size(); i++) {
			if (index == i) {
				NavListItemBean navListItemBean = navigationList.get(index);
				tabId = navListItemBean.ID;
				tabType = navListItemBean.type;
				String contextType_str = navListItemBean.contextType;
				if (!TextUtils.isEmpty(contextType_str)) {
					contextType = Integer.parseInt(contextType_str.trim());
				}
			}
		}
		// 展示标签对应的列表
		showListByType();
		// 处理标签对应的操作
		handleTabType();
	}

	/**
	 * 展示不同列表
	 */
	private void showListByType() {
		mPullToRefreshListView.setVisibility(View.GONE);
		mPullRefreshScrollView.setVisibility(View.GONE);
		// 0 门店，1服务，2技师
		// 显示列表 根据contextType 标签内容类型 ANS 4 M 0 门店，1服务，2技师
		CommonUtil.log("contextType:" + contextType);
		switch (contextType) {
		case 1:
			pageSize = Constants.PAGE_SIZE_GRID;
			mPullRefreshScrollView.setVisibility(View.VISIBLE);
			break;
		case 2:
			pageSize = Constants.PAGE_SIZE_LIST;
			mPullToRefreshListView.setVisibility(View.VISIBLE);
			break;
		}
	}

	private void handleTabType() {
		// 0:普通导航,1:附近,2: 收藏(默认展示标签)
		CommonUtil.log("---------------------tabType:" + tabType);
		if (("2").equals(tabType)) {
			if (!MyApplication.getLoginFlag()) {
				// showCustomCircleProgressDialog("请登录", "你尚未登录,请登录!");
				DialogUtils.showToastMsg(mContext, "你尚未登录,请登录!",
						Toast.LENGTH_SHORT);
				intent.setClass(mContext, LoginActivity.class);
				startActivity(intent);
				return;
			}

			intent = new Intent();
			intent.setClass(mContext, Collect_ListActivity.class);
			startActivity(intent);

		} else {
			// 获取选中数钱的内容
			pageNo = 1;
			v_ad.setFocusable(true);
			v_ad.setFocusableInTouchMode(true);
			v_ad.requestFocus();
			getNavigationContent();
		}
	}

	/**
	 * wrz 改变tab样式
	 * 
	 * @param textView
	 * @param flag
	 */
	private void changeTabStyle(TextView textView, boolean isChecked) {
		// shopdetail_tab_bg"
		// shopdetail_tab_bg_sel"
		if (isChecked) {
			// textView.setBackgroundResource(R.drawable.shopdetail_tab_bg_sel);
			textView.setTextColor(mContext.getResources().getColor(R.color.c3));
			textView.setTextSize(17);

			// scrollTargetTag(textView);
		} else {
			// textView.setBackgroundResource(R.drawable.shopdetail_tab_bg);
			textView.setTextColor(mContext.getResources().getColor(R.color.c6));
			textView.setTextSize(14.6f);
		}

	}

	// private void scrollTargetTag(final TextView textView) {
	// horizontalScrollView_tab.post(new Runnable() {
	// @Override
	// public void run() {
	// // To change body of implemented methods use File | Settings |
	// // File Templates.
	// // mRootScrollView.fullScroll(ScrollView.FOCUS_DOWN);
	// int[] location = new int[2];
	// textView.getLocationOnScreen(location);
	// // - horizontalScrollView_tab.getMeasuredWidth()
	// CommonUtil.log("location[1]:" + location[1]);
	// int offset = location[1];
	// if (offset < 0) {
	// offset = 0;
	// }
	// horizontalScrollView_tab.smoothScrollTo(offset, 0);
	// }
	// });
	// }

	/**
	 * 获取导航栏
	 */
	public void getNavigationList() {
		isShowLoading = false;
		showCustomCircleProgressDialog(
				null,
				mContext.getResources().getString(
						R.string.common_toast_net_prompt_submit));

		HashMap<String, String> inJsonMap = new HashMap<String, String>();
		inJsonMap.put("cityCode", now_citycode);
		inJsonMap.put("type", "1");
		getNavigationList = RequestData.getInstance().request(getActivity(),
				mHandler_navigationList, RequestUrl.GET_NAVIGATIONLIST,
				inJsonMap);
		// getNavigationList = new GetNavigationList(mContext,
		// mHandler_navigationList, inJsonMap);
		// Thread thread = new Thread(getNavigationList);
		// thread.start();
	}

	class HandlerNavigationList extends Handler {
		@Override
		public void handleMessage(Message msg) {
			rl_loadData_empty.setVisibility(View.GONE);
			rl_loadData_error.setVisibility(View.GONE);
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				// 导航列表
				JSONObject jsonObj = getNavigationList.getOutJson();
				if (!CommonUtil.NotEmptyJSONObject(jsonObj)) {
					// 加载数据失败
					rl_loadData_error.setVisibility(View.VISIBLE);
					return;
				}
				// 项目的广告列表 直接加载到 项目列表顶部的广告列表
				NavListBean navListBean = JSON.parseObject(jsonObj.toString(),
						NavListBean.class);
				navigationList = navListBean.navigationList;
				handleHorizontalTab();
				break;
			case MsgId.DOWN_DATA_F:
				closeCustomCircleProgressDialog();
				DialogUtils.showToastMsg(mContext, mContext.getResources()
						.getString(R.string.common_toast_net_down_data_fail),
						Toast.LENGTH_SHORT);
				loadDataContentError = false;
				// 加载数据失败
				rl_loadData_error.setVisibility(View.VISIBLE);
				break;
			case MsgId.NET_NOT_CONNECT:
				loadDataContentError = false;
				// 加载数据失败
				rl_loadData_error.setVisibility(View.VISIBLE);
				closeCustomCircleProgressDialog();
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 获取导航栏对应内容
	 */
	public void getNavigationContent() {

		if (isShowLoading) {
			showCustomCircleProgressDialog(null,
					getString(R.string.common_toast_net_prompt_submit));
		}
		isShowLoading = true;
		// userID 用户ID
		// longitude 用户经度
		// latitude 用户纬度
		// ID 分类导航ID
		// orderBy 排序字段
		// pageStart 当前页数
		// pageOffset 获取数据条数

		HashMap<String, String> inJsonMap = new HashMap<String, String>();
		inJsonMap.put("userID", MyApplication.getUserID());
		inJsonMap.put("longitude", now_lng);
		inJsonMap.put("latitude", now_lat);
		inJsonMap.put("ID", tabId);
		inJsonMap.put("orderBy", orderBy);
		inJsonMap.put("pageStart", pageNo + "");
		inJsonMap.put("pageOffset", pageSize + "");
		getNavigationContent = RequestData.getInstance().request(getActivity(),
				mHandler_navigationContent, RequestUrl.GET_NAVCONTENT,
				inJsonMap);
	}

	class HandlerNavigationContent extends Handler {
		@Override
		public void handleMessage(Message msg) {
			rl_loadData_empty.setVisibility(View.GONE);
			rl_loadData_error.setVisibility(View.GONE);
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				// 导航内容
				JSONObject navigationContent = getNavigationContent
						.getOutJson();
				CommonUtil.log("navigationContent:" + navigationContent);
				if (CommonUtil.NotEmptyJSONObject(navigationContent)) {
					handleNavigation(navigationContent);
				} else {
					// 如果是刷新或者切换tag都是从新从第一页数据获取，如果获取数据失败就清空所有数据
					if (pageNo == 1) {
						// 加载数据失败
						rl_loadData_error.setVisibility(View.VISIBLE);
						clearData();
					}
				}

				break;
			case MsgId.DOWN_DATA_F:
				loadDataContentError = true;
				// 加载数据失败
				if (pageNo > 1) {
					pageNo--;
				}
				if (pageNo == 1) {
					rl_loadData_error.setVisibility(View.VISIBLE);
				} else {
					rl_loadData_error.setVisibility(View.GONE);
				}
				closeCustomCircleProgressDialog();
				DialogUtils.showToastMsg(mContext, mContext.getResources()
						.getString(R.string.common_toast_net_down_data_fail),
						Toast.LENGTH_SHORT);
				break;
			case MsgId.NET_NOT_CONNECT:
				loadDataContentError = true;
				// 加载数据失败
				if (pageNo > 1) {
					pageNo--;
				}
				if (pageNo == 1) {
					rl_loadData_error.setVisibility(View.VISIBLE);
				} else {
					rl_loadData_error.setVisibility(View.GONE);
				}
				closeCustomCircleProgressDialog();
				break;
			}

			// contextType 导航类型 0 门店 1 项目 2 技师
			switch (contextType) {
			case 2:// 技师
				mPullToRefreshListView.onRefreshComplete();
				break;
			case 1:// 项目
				mPullRefreshScrollView.onRefreshComplete();
				break;
			}
		}
	}

	/**
	 * 处理返回的数据
	 * 
	 * @param navigationContent
	 */
	private void handleNavigation(JSONObject navigationContent) {

		NavContentBean navContentBean = JSON.parseObject(
				navigationContent.toString(), NavContentBean.class);
		pageCount = navContentBean.pageCount;
		CommonUtil.log("上门导航内容列表总共页数：pageCount：" + String.valueOf(pageCount));

		// 项目的广告列表 直接加载到 项目列表顶部的广告列表
		adList = navContentBean.adList;// 获取广告列表
		ad_List_Adapter_servHeader.clear();
		ad_List_Adapter_servHeader.add(adList);

		// 技师的广告列表 通过listview addHeader
		ad_List_Adapter_workerHeader.clear();
		ad_List_Adapter_workerHeader.add(adList);
		if (CommonUtil.emptyListToString3(adList)) {
			lv_visit_tech.removeHeaderView(headerView);
		} else {
			lv_visit_tech.removeHeaderView(headerView);
			lv_visit_tech.addHeaderView(headerView);
		}
		//
		// 技师列表
		techList = navContentBean.skillList;
		CommonUtil.logE("techList:" + techList);
		if (pageNo == 1) {
			technician_ListViewAdapter.clear();
		}
		technician_ListViewAdapter.add(techList);

		// 项目列表
		serviceList = navContentBean.serviceList;
		// serviceList = getNavigationContent.getServiceList();
		if (pageNo == 1) {
			service_girview_Adapter.clear();
		}
		service_girview_Adapter.add(serviceList, "");
	}

	/**
	 * 刷新选中的排序方式
	 */
	private void refreshCheckedOrderType() {
		// 0按距离，1 按评价数量，2 按订单数量，3 按价格
		handleSortType_Interface = new HandleSortType_Interface() {

			@Override
			public void callBack(int mSortType, String mSortTypeName) {
				switch (mSortType) {
				case 0:// 距离
					orderBy = "0";
					UmengEventUtil.door_screen_near(mContext);
					break;

				case 1:// 1 按评价数量
					orderBy = "1";
					UmengEventUtil.door_screen_evaluate(mContext);
					break;

				case 2:// 按订单数量
					orderBy = "2";
					UmengEventUtil.door_screen_order(mContext);
					break;

				// case 3:// 按价格
				// orderBy = "3";
				// break;

				}
				pageNo = 1;
				v_ad.setFocusable(true);
				v_ad.setFocusableInTouchMode(true);
				v_ad.requestFocus();
				getNavigationContent();
			}
		};
	}

	/**
	 * 清空数据
	 */
	private void clearData() {

		switch (contextType) {
		case 2:// 技师
			ad_List_Adapter_workerHeader.clear();

			technician_ListViewAdapter.clear();
			technician_ListViewAdapter.notifyDataSetChanged();
			break;
		case 1:// 项目
			ad_List_Adapter_servHeader.clear();
			ad_List_Adapter_servHeader.notifyDataSetChanged();

			service_girview_Adapter.clear();
			service_girview_Adapter.notifyDataSetChanged();
			break;
		default:
			break;
		}

		technician_ListViewAdapter.clear();
		technician_ListViewAdapter.notifyDataSetChanged();

	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		if (intent == null) {
			intent = new Intent();
		}
		switch (v.getId()) {
		case R.id.tv_first_city_select:// 选择城市
			UmengEventUtil.door_city(mContext);
			intent.setClass(mContext, SelectAddress_SearchAreaActivity.class);
			startActivityForResult(intent, REQUEST_SELECT_ADDRESS);
			break;
		case R.id.iv_sort:
			UmengEventUtil.door_screen(mContext);
			Menu_pop_visit.getInstance().showPupupWindow(getActivity(),
					iv_sort, handleSortType_Interface, 1);
			break;
		case R.id.iv_search:
			UmengEventUtil.door_search(mContext);
			intent.setClass(mContext, Search_OnVisitAty.class);
			startActivity(intent);
			break;
		case R.id.rl_locaiton:
			UmengEventUtil.door_position(mContext);
			intent.setClass(mContext, SelectAddress_SearchAreaActivity.class);
			intent.putExtra(
					"cityList",
					CommonUtil.getStringOfSharedPreferences(
							mContext.getApplicationContext(), "city_list", ""));
			startActivityForResult(intent, REQUEST_SELECT_ADDRESS);
			break;
		case R.id.rl_loadData_error:// 重新加载数据
			pageNo = 1;

			if (loadDataContentError && !TextUtils.isEmpty(tabId)) {
				isShowLoading = true;
				getNavigationContent();
			} else {
				getNavigationList();
			}
			break;
		}

	}
}
