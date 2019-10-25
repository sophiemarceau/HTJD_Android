package com.huatuo.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.huatuo.R;
import com.huatuo.activity.login.LoginActivity;
import com.huatuo.activity.personal.Collect_ListActivity;
import com.huatuo.activity.seach.Search_OnStoreAty;
import com.huatuo.activity.seckill.SecKill_SessionActivity;
import com.huatuo.activity.select_address.SelectAddress_SearchAreaActivity;
import com.huatuo.adapter.Ad_List_Adapter;
import com.huatuo.adapter.First_Ad_List_Adapter;
import com.huatuo.adapter.SecKill_Session_List_Adapter;
import com.huatuo.adapter.SecKill_Session_detail_list_Adapter;
import com.huatuo.adapter.Service_girview_Adapter;
import com.huatuo.adapter.Service_girview_Adapter_Bean;
import com.huatuo.adapter.Store_List_Adapter_Bean;
import com.huatuo.base.BaseFragment;
import com.huatuo.base.MyApplication;
import com.huatuo.bean.ADItemBean;
import com.huatuo.bean.NavContentBean;
import com.huatuo.bean.NavListBean;
import com.huatuo.bean.NavListItemBean;
import com.huatuo.bean.SecKillActivityListItemBean;
import com.huatuo.bean.SecKillContentBean;
import com.huatuo.bean.SecKillListBean;
import com.huatuo.bean.SecKillListItemBean;
import com.huatuo.bean.SecKillSpecialListItemBean;
import com.huatuo.bean.ServiceItemBean;
import com.huatuo.bean.StoreItemBean;
import com.huatuo.custom_widget.CustomGridView;
import com.huatuo.custom_widget.CustomListView;
import com.huatuo.dictionary.MsgId;
import com.huatuo.dictionary.RequestUrl;
import com.huatuo.interfaces.HandleSortType_Interface;
import com.huatuo.net.http.RequestData;
import com.huatuo.net.http.RequestRunnable;
import com.huatuo.net.thread.GetOrderCount;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Constants;
import com.huatuo.util.DialogUtils;
import com.huatuo.util.Menu_pop_store;
import com.huatuo.util.StringUtil;
import com.huatuo.util.Toast_Util;
import com.huatuo.util.UmengEventUtil;

public class FirstFragment extends BaseFragment implements OnClickListener {
	private View v;
	private Context mContext;
	private Intent intent = new Intent();
	// 服务项目横向滚动tab
	private LinearLayout horizontal_tab_container;

	/**
	 * 秒杀横向滚动时间tab
	 */
	private LinearLayout horizontal_secSkillTime_tab_container;
	private LinearLayout layout_secSkill_time;
	private PullToRefreshScrollView mPullRefreshScrollView_secKill;
	/**
	 * 秒杀专场
	 */
	private CustomListView mCustomListView_secKill_session;
	private SecKill_Session_List_Adapter secKill_Session_List_Adapter;
	private ArrayList<SecKillSpecialListItemBean> specialList_seckill;
	/**
	 * 秒杀活动
	 */
	private CustomListView mCustomListView_secKill_detail;
	private SecKill_Session_detail_list_Adapter secKill_Session_detail_list_Adapter;
	private ArrayList<SecKillActivityListItemBean> atyList_seckill;
	/**
	 * 秒杀广告
	 */
	private CustomListView mCustomListView_secKill_ad;
	private Ad_List_Adapter first_Ad_List_Adapter_seckill;
	private ArrayList<ADItemBean> adList_seckill;

	/**
	 * 秒杀服务
	 */
	private CustomGridView mCustomGridView_secKill_project;
	private Service_girview_Adapter_Bean service_girview_Adapter_seckill;
	private ArrayList<ServiceItemBean> serList_seckill;

	/**
	 * 倒计时
	 */
	private TextView tv_count_down;// 倒计时展示
	private Timer mTimer = null;
	private Handler mHandler_timer;
	private NotificationTimeTask notificationTimeTask;
	private long remainTimeOfStart;// 秒杀剩余开始时间
	private long remainTimeOfFinish;// 秒杀剩余结束时间
	private long hours;
	private long minutes;
	private long seconds;
	private boolean isSecKill;// 秒杀为 true
	private boolean isSecKillSession;// 是否是秒杀专场

	private PullToRefreshScrollView mPullRefreshScrollView;
	private ScrollView mScrollView;

	// 定理位置展示
	private TextView tv_location;
	private RelativeLayout rl_locaiton;
	// private LinearLayout ll_header;

	// 广告列表
	private CustomListView lv_ad;
	private Ad_List_Adapter ad_List_Adapter_servHeader;
	private Ad_List_Adapter ad_List_Adapter_storeHeader;

	private View headerView;
	private CustomListView lv_ad_header;
	// 获取店铺列表
	private PullToRefreshListView mRefreshListView;
	private ListView lv_store;
	private Store_List_Adapter_Bean first_store_ListViewAdapter;

	// 服务列表
	private CustomGridView gv_service;
	private Service_girview_Adapter_Bean first_service_Grid_Adapter;

	private View v_ad;

	public static TextView tv_first_city_select;
	public ImageView iv_sort, iv_search, iv_scan;
	private final int REQUEST_SELECT_ADDRESS = 100;

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
	private String tabType = "-1";
	private int contextType = -100;// contextType 0 门店，1服务，2技师

	private RequestRunnable getNavigationList;
	public Handler mHandler_navigationList;
	private ArrayList<NavListItemBean> navigationList;

	// 时间轴
	private RequestRunnable getSecKillTimeList;
	public Handler mHandler_secKillTimeList;
	private ArrayList<SecKillListItemBean> secKillTimelist;// 时间轴
	private String secKillTimeID = "";// 时间轴ID
	private String startTime = "";// 秒杀开始时间
	private String endTime = "";// 秒杀结束时间
	// 时间轴 对应内容
	private RequestRunnable getSecKillContentList;
	private Handler mHandler_secKillContentList;
	private SecKillContentBean secKillContentBean;

	// 导航内容
	private RequestRunnable getNavigationContent;
	public Handler mHandler_navigationContent;
	private JSONObject navigationContent = null;
	private ArrayList<ADItemBean> adList;//
	private ArrayList<StoreItemBean> storeList;
	private ArrayList<ServiceItemBean> serviceList;

	// 筛选条件
	private String orderBy = "0";// 0按距离，1 按评价数量，2 按订单数量，3 按价格
	private HandleSortType_Interface handleSortType_Interface;
	private RequestRunnable getOrderCount;
	private Handler mGetOrderCountHandler;
	private String unpaidCount;
	private static int enter = 0;

	private boolean isShowLoading = false;// 是否显示加载框---用于控制获取导航条与导航内容时=-----加载loading显示

	private String now_city = "";
	private String now_address = "";
	private String now_citycode = "";
	private String now_open_status = "";
	private String now_lng = "";
	private String now_lat = "";
	private String current_address = "";// 当前页面地址

	private RelativeLayout rl_loadData_error, rl_loadData_empty;
	private boolean loadDataContent = true;
	private boolean mHiden;

	/**
	 * 屏幕唤醒广播
	 */
	private IntentFilter dynamic_filter = null;
	/**
	 * 广播接收器
	 */
	private BroadcastReceiver dynamicReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		v = inflater.inflate(R.layout.first_fragment3, container, false);
		mContext = getActivity();
		findViewById();
		initHandler();
		// 广播接收
		broadcastReceiver();
		// 注册广播
		registeBoardCast();
		// 刷新选中的排序方式
		refreshCheckedOrderType();
		// 初始化城市和地理位置信息展示
		initCityInfo();
		// 获取导航列表内容
		getNavigationList(true);
		return v;
	}

	private void initHandler() {
		mHandler_navigationList = new HandlerNavigationList();// 导航标签列表
		mHandler_navigationContent = new HandlerNavigationContent();// 导航标签列表对应的内容
		mGetOrderCountHandler = new GetOrderCountHandler();// 待处理订单数量
		mHandler_secKillTimeList = new Handler_secKillTimeList();// 秒杀时间轴
		mHandler_secKillContentList = new Handler_secKillContentList();// 秒杀时间对应的秒杀内容
		mHandler_timer = new MyHandler_Timer();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		CommonUtil.log("-------------------onHiddenChanged--------------------"
				+ hidden);
		mHiden = hidden;
		if (!hidden) {
			// 展示定理位置信息
			showSelectedCity();
			// 判断是否切换位置
			if (current_address != now_address) {
				getNavigationList(true);
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
				getNavigationList(true);
			}
		}
	}

	/**
	 * 注册广播
	 */
	private void registeBoardCast() {
		// 屏幕唤醒
		dynamic_filter = new IntentFilter();
		// 屏幕亮屏广播
		dynamic_filter.addAction(Intent.ACTION_SCREEN_ON);
		// 屏幕解锁广播
		dynamic_filter.addAction(Intent.ACTION_USER_PRESENT);
		getActivity().registerReceiver(dynamicReceiver, dynamic_filter);

	}

	/**
	 * 广播接收
	 */
	private void broadcastReceiver() {
		dynamicReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent intent) {
				String action = intent.getAction();
				if (action.equals(Intent.ACTION_SCREEN_OFF)) {
					CommonUtil.logE("屏幕亮屏广播...");
					isShowLoading = true;
					getSecKillContent();
				} else if (action.equals(Intent.ACTION_USER_PRESENT)) {
					CommonUtil.logE("屏幕解锁广播...");
					isShowLoading = true;
					// 只处理秒杀列表数据秒杀数据
					if (tabType.equals("3")) {
						getSecKillContent();
					}
				}
			}
		};
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
		// 展示城市和位置信息
		refreshCurentAddress();
		tv_first_city_select.setText(city);
		tv_location.setText(now_address);
	}

	/**
	 * 获取定位信息
	 */
	private void initCityInfo() {
		showSelectedCity();
	}

	private void findViewById() {
		tv_first_city_select = (TextView) v
				.findViewById(R.id.tv_first_city_select);
		v_ad = (View) v.findViewById(R.id.v_ad);
		iv_search = (ImageView) v.findViewById(R.id.iv_search);
		iv_sort = (ImageView) v.findViewById(R.id.iv_sort);
		iv_scan = (ImageView) v.findViewById(R.id.iv_scan);

		// 秒杀部分
		layout_secSkill_time = (LinearLayout) v
				.findViewById(R.id.layout_secSkill_time);
		horizontal_secSkillTime_tab_container = (LinearLayout) v
				.findViewById(R.id.horizontal_tab_secSkill_time_container);
		// 倒计时
		tv_count_down = (TextView) v.findViewById(R.id.tv_count_down);

		mPullRefreshScrollView_secKill = (PullToRefreshScrollView) v
				.findViewById(R.id.mPullRefreshScrollView_secKill);
		mCustomListView_secKill_session = (CustomListView) v
				.findViewById(R.id.clv_secKill_session);
		mCustomListView_secKill_detail = (CustomListView) v
				.findViewById(R.id.clv_secKill_detail);
		mCustomListView_secKill_ad = (CustomListView) v
				.findViewById(R.id.clv_secKill_ad);
		mCustomGridView_secKill_project = (CustomGridView) v
				.findViewById(R.id.cgv_service);

		secKill_Session_List_Adapter = new SecKill_Session_List_Adapter(
				getActivity());
		mCustomListView_secKill_session
				.setAdapter(secKill_Session_List_Adapter);

		secKill_Session_detail_list_Adapter = new SecKill_Session_detail_list_Adapter(
				getActivity());
		mCustomListView_secKill_detail
				.setAdapter(secKill_Session_detail_list_Adapter);

		first_Ad_List_Adapter_seckill = new Ad_List_Adapter(getActivity());
		mCustomListView_secKill_ad.setAdapter(first_Ad_List_Adapter_seckill);

		service_girview_Adapter_seckill = new Service_girview_Adapter_Bean(
				getActivity());
		mCustomGridView_secKill_project
				.setAdapter(service_girview_Adapter_seckill);

		// 其他标签
		lv_ad = (CustomListView) v.findViewById(R.id.lv_ad);
		gv_service = (CustomGridView) v.findViewById(R.id.gv_service);
		horizontal_tab_container = (LinearLayout) v
				.findViewById(R.id.horizontal_tab_service_container);
		mPullRefreshScrollView = (PullToRefreshScrollView) v
				.findViewById(R.id.pull_refresh_scrollview);

		tv_location = (TextView) v.findViewById(R.id.tv_locationAddress);
		rl_locaiton = (RelativeLayout) v.findViewById(R.id.rl_locaiton);

		ad_List_Adapter_servHeader = new Ad_List_Adapter(getActivity());
		lv_ad.setAdapter(ad_List_Adapter_servHeader);

		mRefreshListView = (PullToRefreshListView) v
				.findViewById(R.id.lv_store);
		lv_store = mRefreshListView.getRefreshableView();

		headerView = LayoutInflater.from(getActivity()).inflate(
				R.layout.fragment_adlist, null);
		lv_ad_header = (CustomListView) headerView
				.findViewById(R.id.lv_ad_header);

		ad_List_Adapter_storeHeader = new Ad_List_Adapter(getActivity());
		lv_ad_header.setAdapter(ad_List_Adapter_storeHeader);
		lv_store.addHeaderView(headerView);

		first_store_ListViewAdapter = new Store_List_Adapter_Bean(getActivity());
		lv_store.setAdapter(first_store_ListViewAdapter);

		first_service_Grid_Adapter = new Service_girview_Adapter_Bean(
				getActivity());
		gv_service.setAdapter(first_service_Grid_Adapter);

		// 加载数据失败页面
		rl_loadData_error = (RelativeLayout) v
				.findViewById(R.id.rl_loadData_error);
		rl_loadData_error.setOnClickListener(this);
		// 加载数据前的白界面
		rl_loadData_empty = (RelativeLayout) v
				.findViewById(R.id.rl_loadData_empty);
		setListener();
	}

	private void setListener() {
		tv_first_city_select.setOnClickListener(this);
		iv_sort.setOnClickListener(this);
		iv_search.setOnClickListener(this);
		iv_scan.setOnClickListener(this);
		// storeListOnItemClickListener();
		rl_locaiton.setOnClickListener(this);

		mPullRefreshScrollView
				.setOnRefreshListener(new OnRefreshListener2<ScrollView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						// TODO Auto-generated method stub
						refreshList();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						// TODO Auto-generated method stub
						CommonUtil
								.log("--------loadMore-----------------mPullRefreshScrollView------------------------");
						loadMore();
					}
				});

		mRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// TODO Auto-generated method stub
						refreshList();
					}
				});

		mRefreshListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						// TODO Auto-generated method stub
						CommonUtil
								.log("--------loadMore-----------------mRefreshListView------------------------");
						loadMore();
					}
				});
		mPullRefreshScrollView_secKill.setMode(Mode.PULL_DOWN_TO_REFRESH);
		mPullRefreshScrollView_secKill
				.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						// TODO Auto-generated method stub
						refreshSecKillList();
					}

				});
	}

	/**
	 * 刷新秒杀列表
	 */
	public void refreshSecKillList() {
		getSecKillContent();
	}

	/**
	 * 刷新非秒杀列表
	 */
	public void refreshList() {
		pageNo = 1;
		if (CommonUtil.emptyListToString3(navigationList)) {
			getNavigationList(true);
		} else {
			getNavigationContent();
		}
	}

	/**
	 * 加载更多非秒杀列表
	 */
	public void loadMore() {
		if (pageNo < pageCount) {
			++pageNo;
			getNavigationContent();
		} else {
			// contextType 导航类型 0 门店 1 项目 2 技师
			switch (contextType) {
			case 0:// 门店
					mRefreshListView.onRefreshComplete();
				break;
			case 1:// 项目
				mPullRefreshScrollView.onRefreshComplete();
				break;
			default:
				break;
			}
//			if (pageNo > 1) {
//				Toast_Util.showToastOnlyOne(mContext, mContext.getResources()
//						.getString(R.string.load_no_more_data));
//			}
		}
	}

	/**
	 * 处理标签tab
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
			if (mRefreshListView.isRefreshing()) {
				mRefreshListView.onRefreshComplete();
			}
			// 选中标签ID
			int checkedId = v.getId();
			CommonUtil.log("checkedId:" + checkedId);
			changeTab(checkedId);
			closeTimer();

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
				CommonUtil.log("选中的部位Name:" + checkedName);
				changeTabStyle(tv, true);
				getSelectedTabInfo(checkedId);
				// tv.setFocusable(true);
				// tv.setFocusableInTouchMode(true);
				// tv.requestFocus();
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("tagName", checkedName);
				UmengEventUtil.store_horizontal_tab(mContext, map);
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
				// String checkedName = tv.getText().toString();
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
	 * wrz 改变tab样式
	 * 
	 * @param textView
	 * @param flag
	 */
	private void changeTabStyle(TextView textView, boolean isChecked) {
		if (isChecked) {
			textView.setTextColor(mContext.getResources().getColor(R.color.c1));
			textView.setTextSize(17);
			// scrollTargetTag(textView);
		} else {
			textView.setTextColor(mContext.getResources().getColor(
					R.color.fragment_vist_tab_color));
			textView.setTextSize(14.6f);
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
				CommonUtil.logE("点击的导航标签：0 门店，1服务，2技师contextType：" + tabType);
			}
		}
		// 处理标签对应的操作
		handleTabType();
	}

	/**
	 * 展示不同列表
	 */
	private void showListByType(int type) {
		mRefreshListView.setVisibility(View.GONE);
		mPullRefreshScrollView.setVisibility(View.GONE);
		layout_secSkill_time.setVisibility(View.GONE);
		// 0 门店，1服务，2技师 ，3秒杀
		switch (type) {
		case 1:// 服务列表
			pageSize = Constants.PAGE_SIZE_GRID;
			mPullRefreshScrollView.setVisibility(View.VISIBLE);
			break;
		case 0:// 门店列表
			pageSize = Constants.PAGE_SIZE_LIST;
			mRefreshListView.setVisibility(View.VISIBLE);
			break;
		case 3:// 秒杀列表
			layout_secSkill_time.setVisibility(View.VISIBLE);
			break;
		}
	}

	/**
	 * 处理点击顶部标签下一步动作
	 */
	private void handleTabType() {
		// 0:普通导航,1:附近,2: 收藏(默认展示标签)3:秒杀
		CommonUtil.log("---------------------tabType:" + tabType);
		// 收藏
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
			// 秒杀标签
		} else if (("3").equals(tabType)) {
			// 展示标签对应的列表
			showListByType(3);
			// 获取时间轴
			getSecKillTimeList();
		} else {
			// 展示标签对应的列表
			showListByType(contextType);
			// 获取选中数钱的内容
			pageNo = 1;
			v_ad.setFocusable(true);
			v_ad.setFocusableInTouchMode(true);
			v_ad.requestFocus();
			getNavigationContent();
		}
	}

	/**
	 * 处理秒杀时间tab
	 */
	private void handleHorizontalSecSkillTimeTab() {
		CommonUtil.log("----------------secKillTimelist:" + secKillTimelist);
		horizontal_secSkillTime_tab_container.removeAllViews();
		View convertView = null;
		if (!CommonUtil.emptyListToString3(secKillTimelist)) {
			for (int i = 0; i < secKillTimelist.size(); i++) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.fragment_first_seckill_time_item, null);
				// 给item布局添加ID,此ID为int类型任意值
				convertView.setId(i);
				// convertView.setPadding(CommonUtil.dip2px(mContext, 5),
				// CommonUtil.dip2px(mContext, 0),
				// CommonUtil.dip2px(mContext, 0),
				// CommonUtil.dip2px(mContext, 0));
				LayoutParams layoutParams = new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				if (i != (secKillTimelist.size() - 1)) {
					layoutParams.setMargins(0, 0,
							CommonUtil.dip2px(mContext, 35), 0);
				} else {
					layoutParams.setMargins(0, 0,
							CommonUtil.dip2px(mContext, 0), 0);
				}
				convertView.setLayoutParams(layoutParams);
				TextView tv_secKillTime = (TextView) convertView
						.findViewById(R.id.tv_secskillTime);

				SecKillListItemBean secKillListItemBean = secKillTimelist
						.get(i);
				if (null != secKillListItemBean) {
					String startTime = secKillListItemBean.startTime;
					CommonUtil.logE("秒杀时间tabName:" + startTime);
					tv_secKillTime.setText(startTime);
					convertView
							.setOnClickListener(new SecSkillTimeTabOnClickListener());
					horizontal_secSkillTime_tab_container.addView(convertView);
				}
			}
			// 默认选择第一个
			changeSecSkillTimeTab(0);
		}
	}

	/**
	 * 时间轴时间选择点击事件
	 */
	class SecSkillTimeTabOnClickListener implements OnClickListener {

		public SecSkillTimeTabOnClickListener() {

		}

		@Override
		public void onClick(View v) {
			// 判断刷新动画是否消失
			if (mPullRefreshScrollView_secKill.isRefreshing()) {
				mPullRefreshScrollView_secKill.onRefreshComplete();
			}
			// 关闭倒计时（重新选择时间轴）
			closeTimer();

			// 选中的秒杀时间标签
			int checkedId = v.getId();
			CommonUtil.log("checkedId:" + checkedId);
			changeSecSkillTimeTab(checkedId);

		}
	}

	/**
	 * 选中tab的操作
	 * 
	 * @param checkedId
	 */
	private void changeSecSkillTimeTab(int checkedId) {
		TextView tv_secskillTime;
		View view_secskillTime_line;
		int tv_id;
		for (int i = 0; i < horizontal_secSkillTime_tab_container
				.getChildCount(); i++) {
			tv_id = horizontal_secSkillTime_tab_container.getChildAt(i).getId();
			View convertView = horizontal_secSkillTime_tab_container
					.getChildAt(i);
			tv_secskillTime = (TextView) convertView
					.findViewById(R.id.tv_secskillTime);
			view_secskillTime_line = convertView
					.findViewById(R.id.view_secskillTime_line);
			// 设置选中的时间标签
			if (checkedId == tv_id) {
				String checkedName = tv_secskillTime.getText().toString();
				CommonUtil.log("选中的秒杀时间:" + checkedName);
				// 改变选中的时间标签的样式
				changeSecSkillTimeTabStyle(tv_secskillTime,
						view_secskillTime_line, true);
				// 根据选中的秒杀时间标签获取 对应内容
				getSelectedSecSkillTimeTabInfo(checkedId);
			} else {
				// 改变未选中的时间标签的样式
				changeSecSkillTimeTabStyle(tv_secskillTime,
						view_secskillTime_line, false);
			}
		}
	}

	/**
	 * 改变秒杀时间标签的样式
	 * 
	 * @param textView
	 * @param flag
	 */
	private void changeSecSkillTimeTabStyle(TextView textView, View view,
			boolean isChecked) {
		if (isChecked) {
			textView.setTextColor(mContext.getResources().getColor(
					R.color.white));
			textView.setTextSize(15);
			view.setVisibility(View.VISIBLE);
		} else {
			textView.setTextColor(mContext.getResources().getColor(
					R.color.unchecked_secSkillTime_color));
			textView.setTextSize(14f);
			view.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 获取选中秒杀tag信息
	 * 
	 * @param index
	 */
	private void getSelectedSecSkillTimeTabInfo(int index) {
		startTime = "";// 清空
		endTime = "";// 清空
		for (int i = 0; i < secKillTimelist.size(); i++) {
			if (index == i) {
				SecKillListItemBean secKillListItemBean = secKillTimelist
						.get(index);
				// ID 时间轴ID ANS 64 M 时间轴ID
				// name 时间轴名称 ANS 64 M 时间轴名称
				// startTime 开始时间 ANS 32 M 开始时间
				// endTime 结束时间 ANS 32 M 结束时间
				// currTime 当前时间 ANS 32 M 结束时间(yyyy-MM-dd HH:mm:ss)
				secKillTimeID = secKillListItemBean.ID;
				// 获取秒杀列表内容
				startTime = secKillListItemBean.startTime;
				endTime = secKillListItemBean.endTime;
				clearSecKillList();// 清空秒杀列表
				getSecKillContent();// 获取秒杀列表

			}
		}

	}

	/**
	 * 获取导航栏
	 */
	public void getNavigationList(boolean isLoading) {
		if (isLoading) {
			showCustomCircleProgressDialog(null, mContext.getResources()
					.getString(R.string.common_toast_net_prompt_submit));
		}
		isShowLoading = false;
		HashMap<String, String> inJsonMap = new HashMap<String, String>();
		inJsonMap.put("cityCode", now_citycode);
		inJsonMap.put("type", "0");
		getNavigationList = RequestData.getInstance().request(getActivity(),
				mHandler_navigationList, RequestUrl.GET_NAVIGATIONLIST,
				inJsonMap);
	}

	class HandlerNavigationList extends Handler {
		@Override
		public void handleMessage(Message msg) {
			rl_loadData_empty.setVisibility(View.GONE);
			rl_loadData_error.setVisibility(View.GONE);
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				// 导航列表
				JSONObject jsonObj = getNavigationList.getOutJson();
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
				loadDataContent = false;
				// 加载数据失败
				rl_loadData_error.setVisibility(View.VISIBLE);
				break;
			case MsgId.NET_NOT_CONNECT:
				loadDataContent = false;
				// 加载数据失败
				rl_loadData_error.setVisibility(View.VISIBLE);
				closeCustomCircleProgressDialog();
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
				// 导航内容
				// 导航内容
				navigationContent = getNavigationContent.getOutJson();
				CommonUtil.log("navigationContent:" + navigationContent);
				if (CommonUtil.NotEmptyJSONObject(navigationContent)) {
					handleNavigation();
				} else {
					// 如果是刷新或者切换tag都是从新从第一页数据获取，如果获取数据失败就清空所有数据
					if (pageNo == 1) {
						clearData();
					}
				}
				break;
			case MsgId.DOWN_DATA_F:
				loadDataContent = true;
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
				loadDataContent = true;
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
			default:
				break;
			}

			CommonUtil.logE("enter------------------>" + enter);
			if (MyApplication.getLoginFlag() && enter == 0) {
				getOrderCount();
				enter = 1;
			} else {
				closeCustomCircleProgressDialog();
			}
			// contextType 导航类型 0 门店 1 项目 2 技师
			switch (contextType) {
			case 0:// 门店
				mRefreshListView.onRefreshComplete();
				break;
			case 1:// 项目
				mPullRefreshScrollView.onRefreshComplete();
				break;
			default:
				break;
			}

		}
	}

	/**
	 * 处理返回的数据
	 */
	private void handleNavigation() {

		// 项目的广告列表 直接加载到 项目列表顶部的广告列表
		NavContentBean navContentBean = JSON.parseObject(
				navigationContent.toString(), NavContentBean.class);
		pageCount = navContentBean.pageCount;
		CommonUtil.log("上门导航内容列表总共页数：pageCount：" + String.valueOf(pageCount));
		// 项目的广告列表 直接加载到 项目列表顶部的广告列表
		adList = navContentBean.adList;// 获取广告列表
		ad_List_Adapter_servHeader.clear();
		ad_List_Adapter_servHeader.add(adList);

		// 店铺的广告列表
		ad_List_Adapter_storeHeader.clear();
		ad_List_Adapter_storeHeader.add(adList);

		if (CommonUtil.emptyListToString3(adList)) {
			lv_store.removeHeaderView(headerView);
		} else {
			lv_store.removeHeaderView(headerView);
			lv_store.addHeaderView(headerView);
		}

		// 店铺列表
		storeList = navContentBean.storeList;

		CommonUtil.log("到店----获取storeList--------pageNo:" + pageNo);
		if (pageNo == 1) {
			first_store_ListViewAdapter.clear();
		}
		first_store_ListViewAdapter.add(storeList);

		serviceList = navContentBean.serviceList;

		CommonUtil.log("到店----获取serviceList--------pageNo:" + pageNo);
		if (pageNo == 1) {
			first_service_Grid_Adapter.clear();
		}
		first_service_Grid_Adapter.add(serviceList, "");
	}

	/**
	 * 获取时间轴
	 */
	public void getSecKillTimeList() {

		if (isShowLoading) {
			showCustomCircleProgressDialog(null,
					getString(R.string.common_toast_net_prompt_submit));
		}
		isShowLoading = false;
		// ID 分类导航ID ANS 64 M 分类导航ID
		// pageOffset 获取数据条数
		HashMap<String, String> inJsonMap = new HashMap<String, String>();
		inJsonMap.put("ID", tabId);
		getSecKillTimeList = RequestData.getInstance().request(getActivity(),
				mHandler_secKillTimeList, RequestUrl.GET_TIMEZONELIST,
				inJsonMap);
	}

	class Handler_secKillTimeList extends Handler {
		@Override
		public void handleMessage(Message msg) {
			rl_loadData_empty.setVisibility(View.GONE);
			rl_loadData_error.setVisibility(View.GONE);
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();

				JSONObject jsonObject = getSecKillTimeList.getOutJson();
				if (CommonUtil.NotEmptyJSONObject(jsonObject)) {
					SecKillListBean secKillListBean = JSON.parseObject(
							jsonObject.toString(), SecKillListBean.class);
					secKillTimelist = secKillListBean.timeZoneList;
					handleHorizontalSecSkillTimeTab();
				}
				break;
			case MsgId.DOWN_DATA_F:
				loadDataContent = true;
				rl_loadData_error.setVisibility(View.VISIBLE);
				closeCustomCircleProgressDialog();
				DialogUtils.showToastMsg(mContext, mContext.getResources()
						.getString(R.string.common_toast_net_down_data_fail),
						Toast.LENGTH_SHORT);
				mPullRefreshScrollView_secKill.onRefreshComplete();
				break;
			case MsgId.NET_NOT_CONNECT:
				closeCustomCircleProgressDialog();
				rl_loadData_error.setVisibility(View.VISIBLE);

				// setCustomDialog(
				// mContext.getResources().getString(
				// R.string.common_toast_net_not_connect), true);
				mPullRefreshScrollView_secKill.onRefreshComplete();
				break;
			default:
				break;
			}

		}
	}

	/**
	 * 获取秒杀活动内容
	 */
	public void getSecKillContent() {

		if (isShowLoading) {
			showCustomCircleProgressDialog(null,
					getString(R.string.common_toast_net_prompt_submit));
		}
		isShowLoading = true;
		// ID 分类导航ID ANS 64 M 分类导航ID
		HashMap<String, String> inJsonMap = new HashMap<String, String>();
		inJsonMap.put("ID", secKillTimeID);
		getSecKillContentList = RequestData.getInstance().request(
				getActivity(), mHandler_secKillContentList,
				RequestUrl.GET_TIMEZONECONTEXT, inJsonMap);
	}

	class Handler_secKillContentList extends Handler {
		@Override
		public void handleMessage(Message msg) {
			rl_loadData_empty.setVisibility(View.GONE);
			rl_loadData_error.setVisibility(View.GONE);
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				// 秒杀内容
				JSONObject secKillcontent = getSecKillContentList.getOutJson();
				if (CommonUtil.NotEmptyJSONObject(secKillcontent)) {

					secKillContentBean = JSON
							.parseObject(secKillcontent.toString(),
									SecKillContentBean.class);
					handleSecKillContent(secKillContentBean);
				}
				break;
			case MsgId.DOWN_DATA_F:
				loadDataContent = true;
				closeCustomCircleProgressDialog();
				rl_loadData_error.setVisibility(View.VISIBLE);
				// DialogUtils.showToastMsg(mContext, mContext.getResources()
				// .getString(R.string.common_toast_net_down_data_fail),
				// Toast.LENGTH_SHORT);
				break;
			case MsgId.NET_NOT_CONNECT:
				closeCustomCircleProgressDialog();
				rl_loadData_error.setVisibility(View.VISIBLE);
				// setCustomDialog(
				// mContext.getResources().getString(
				// R.string.common_toast_net_not_connect), true);
				break;
			default:
				break;
			}

			mPullRefreshScrollView_secKill.onRefreshComplete();
		}
	}

	private void handleSecKillContent(SecKillContentBean secKillContentBean) {
		String currTime = secKillContentBean.currTime;

		// // 测试数据
		// String startTime = "12:00:10";
		// String currTime = "";
		// if (!isStart) {
		// currTime = "2015-12-12 12:00:00";
		// } else {
		// currTime = "2015-12-12 12:00:10";
		// }
		currTime = StringUtil.replaceBlank(currTime);
		currTime = currTime.substring(10, currTime.length());
		// String endTime = "12:30:00";
		countTime(currTime, startTime, endTime);
	}

	/**
	 * 处理秒杀列表数据
	 */
	private void handleSecKillList() {

		// 秒杀专题
		specialList_seckill = secKillContentBean.specialList;
		CommonUtil.logE("specialList_seckill:" + specialList_seckill);
		CommonUtil.logE("isSecKillSession:" + isSecKillSession);
		secKill_Session_List_Adapter.clear();
		secKill_Session_List_Adapter.add(specialList_seckill, isSecKill,
				isSecKillSession);
		// 秒杀活动
		atyList_seckill = secKillContentBean.activityList;
		CommonUtil.logE("atyList_seckill:" + atyList_seckill);
		secKill_Session_detail_list_Adapter.clear();
		secKill_Session_detail_list_Adapter.add(atyList_seckill, isSecKill);
		// 秒杀广告
		adList_seckill = secKillContentBean.adList;
		CommonUtil.logE("adList_seckill:" + adList_seckill);
		first_Ad_List_Adapter_seckill.clear();
		first_Ad_List_Adapter_seckill.add(adList_seckill);
		// 秒杀项目
		serList_seckill = secKillContentBean.servList;
		CommonUtil.logE("serList_seckill:" + serList_seckill);
		service_girview_Adapter_seckill.clear();
		service_girview_Adapter_seckill.add(serList_seckill, "");
	}

	/**
	 * 处理秒杀列表数据
	 */
	private void clearSecKillList() {
		// 秒杀专题
		secKill_Session_List_Adapter.clear();
		secKill_Session_List_Adapter.notifyDataSetChanged();
		// 秒杀活动
		secKill_Session_detail_list_Adapter.clear();
		secKill_Session_detail_list_Adapter.notifyDataSetChanged();
		// 秒杀广告
		first_Ad_List_Adapter_seckill.clear();
		first_Ad_List_Adapter_seckill.notifyDataSetChanged();
		// 秒杀项目
		service_girview_Adapter_seckill.clear();
		service_girview_Adapter_seckill.notifyDataSetChanged();
	}

	/**
	 * 获取未处理订单数量
	 */
	private void getOrderCount() {
		// showCustomCircleProgressDialog(null,
		// mContext.getResources().getString(R.string.common_toast_net_prompt_submit));
		HashMap<String, String> inJsonMap = new HashMap<String, String>();
		inJsonMap.put("userID", MyApplication.getUserID());
		getOrderCount = RequestData.getInstance().request(mContext,
				mGetOrderCountHandler, RequestUrl.GET_ORDERCOUNT, inJsonMap);
	}

	class GetOrderCountHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				unpaidCount = getOrderCount.getOutJson().optString(
						"unpaidCount", "");
				// Log.e("enter", "unpaidCount------------------>" +
				// unpaidCount);
				int unpaid = 0;
				if (!unpaidCount.isEmpty()) {
					unpaid = Integer.parseInt(unpaidCount);
				}
				// Log.e("enter", "unpaid------------------>" + unpaid);
				if (unpaid > 0) {
					Intent intent = new Intent();
					intent.setAction(Constants.REFRESH_UNPAIDCOUNT);
					intent.putExtra("unpaidCount", unpaidCount);
					mContext.sendBroadcast(intent);
				}
				break;
			case MsgId.DOWN_DATA_F:
				closeCustomCircleProgressDialog();
				// DialogUtils.showToastMsg(mContext,
				// mContext.getResources().getString(R.string.common_toast_net_down_data_fail),
				// Toast.LENGTH_SHORT);
				break;
			case MsgId.NET_NOT_CONNECT:
				closeCustomCircleProgressDialog();
				// setCustomDialog(mContext.getResources().getString(R.string.common_toast_net_not_connect),
				// true);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 刷新选中的排序方式
	 */
	private void refreshCheckedOrderType() {
		// 0按距离，1 按评价数量，2 按订单数量，3 按价格
		handleSortType_Interface = new HandleSortType_Interface() {

			@Override
			public void callBack(int mSortType, String mSortTypeName) {
				// 0按距离，1 按评价数量，2 按订单数量，3 按价格
				switch (mSortType) {
				case 0:// 距离
					orderBy = "0";
					UmengEventUtil.store_screen_near(mContext);
					break;

				case 1:// 1 按评价数量
					orderBy = "1";
					UmengEventUtil.store_screen_evaluate(mContext);
					break;
				case 2:// 按价格
					orderBy = "3";
					UmengEventUtil.store_screen_price(mContext);
					break;
				// case 2:// 按订单数量
				// orderBy = "2";
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
		case 0:// 门店
			ad_List_Adapter_storeHeader.clear();
			first_store_ListViewAdapter.clear();
			first_store_ListViewAdapter.notifyDataSetChanged();
			break;
		case 1:// 项目
			ad_List_Adapter_servHeader.clear();
			first_service_Grid_Adapter.notifyDataSetChanged();

			first_service_Grid_Adapter.clear();
			first_service_Grid_Adapter.notifyDataSetChanged();
			break;

		default:
			break;
		}

	}

	/**
	 * 计算剩余时间
	 * 
	 * @param startTime
	 * @param endTime
	 */
	private void countTime(String currTime, String startTime, String endTime) {
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		// SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date current_date;
		Date start_date;
		Date end_date;
		try {
			current_date = df.parse(currTime);
			start_date = df.parse(startTime);
			end_date = df.parse(endTime);
			remainTimeOfStart = (start_date.getTime() - current_date.getTime()) / 1000;
			CommonUtil.logE("剩余开始时间:" + remainTimeOfStart);
			// 剩余开始时间无
			if (remainTimeOfStart <= 0) {
				isSecKillSession = true;
				remainTimeOfFinish = (end_date.getTime() - current_date
						.getTime()) / 1000;
				CommonUtil.logE("剩余结束时间:" + remainTimeOfFinish);
				// 剩余时间大于0
				if (remainTimeOfFinish > 0) {
					startTimer(remainTimeOfFinish, 2);
					isSecKill = true;
				} else {
					// 通知不能秒杀
					closeTimer();
					tv_count_down.setText("本场已结束");
					isSecKill = false;
				}

			} else {// 开始 计时距离秒杀开始时间
				remainTimeOfFinish = (end_date.getTime() - start_date.getTime()) / 1000;
				isSecKill = false;// 不能秒杀
				isSecKillSession = false;
				startTimer(remainTimeOfStart, 1);
			}

			handleSecKillList();

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			CommonUtil.logE("ParseException" + e);
		}

	}

	/**
	 * @param time
	 * @param type
	 *            倒计时类型：1：剩余开始时间 2：剩余结束时间
	 */
	private void startTimer(long time, int type) {
		closeTimer();
		CommonUtil.log("--------------------倒计时开始--------------------");
		mTimer = new Timer();
		notificationTimeTask = new NotificationTimeTask(time, type);
		mTimer.schedule(notificationTimeTask, 0, 1000);
	}

	private class NotificationTimeTask extends TimerTask {
		private long time;
		private int type;// 是倒计时类型：1：剩余开始时间 2：剩余结束时间

		public NotificationTimeTask(long time, int type) {
			this.time = time;
			this.type = type;
		}

		@Override
		public void run() {
			Message message = new Message();
			message.what = (int) time--;
			message.obj = type;
			mHandler_timer.sendMessage(message);
		}
	}

	class MyHandler_Timer extends Handler {
		@Override
		public void handleMessage(Message msg) {
			int type = (Integer) msg.obj;
			long time = msg.what;
			CommonUtil.log("倒计时：type：" + type);
			CommonUtil.log("倒计时：time：" + time);
			// 秒杀结束剩余时间
			if (type == 2) {
				if (time > 0) {
					countHMS(time);
					showRemainTime(type);
					return;
				}
				if (time == 0) {
					if (mTimer != null) {
						mTimer.cancel();
						mTimer = null;
						// 通知秒杀结束
						isSecKill = false;
						tv_count_down.setText("本场已结束");
						handleSecKillList();
					}
				}
			} else if (type == 1) {// 秒杀开始剩余时间
				CommonUtil.logE("秒杀开始剩余时间:" + time);
				if (time > 0) {
					countHMS(time);
					showRemainTime(type);
					return;
				}
				if (time == 0) {
					if (mTimer != null) {
						mTimer.cancel();
						mTimer = null;
						Toast_Util.showToast(mContext, "通知距离秒杀开始");
						// 通知距离秒杀开始
						// 秒杀开始-调取秒杀列表
						getSecKillContent();
						// handleSecKillList();
						// startTimer(remainTimeOfFinish, 2);
					}
				}
			}
		}

	}

	/*
	 * @param time 计算时分秒
	 */
	private void countHMS(long time) {
		CommonUtil.logE("计算时分秒time:" + time);
		if (time > 0) {
			long days = time / (60 * 60 * 24);
			hours = (time - days * (60 * 60 * 24)) / (60 * 60);
			minutes = (time - days * (60 * 60 * 24) - hours * (60 * 60)) / (60);
			seconds = (time - days * (60 * 60 * 24) - hours * (60 * 60) - minutes * (60));
			CommonUtil.logE("计算时分秒:" + hours + "小时" + minutes + "分" + seconds
					+ "秒");
		}
	}

	/**
	 * @param type倒计时类型
	 *            ：1：剩余开始时间 2：剩余结束时间
	 */
	private void showRemainTime(int type) {
		switch (type) {
		case 1:// 距离本场开抢
			tv_count_down.setText("距离本场开抢" + hours + "小时" + minutes + "分"
					+ seconds + "秒");
			break;
		case 2:// 距离本场结束
			tv_count_down.setText("距离本场结束" + hours + "小时" + minutes + "分"
					+ seconds + "秒");
			break;
		default:
			break;
		}
	}

	/**
	 * 关闭倒计时
	 */
	private void closeTimer() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		if (intent == null) {
			intent = new Intent();
		}
		switch (v.getId()) {
		case R.id.tv_first_city_select:// 选择城市
			UmengEventUtil.store_city(mContext);
			intent.setClass(mContext, SelectAddress_SearchAreaActivity.class);
			intent.putExtra(
					"cityList",
					CommonUtil.getStringOfSharedPreferences(
							mContext.getApplicationContext(), "city_list", ""));
			startActivityForResult(intent, REQUEST_SELECT_ADDRESS);
			break;
		case R.id.iv_sort:// 排序
			UmengEventUtil.store_screen(mContext);
			Menu_pop_store.getInstance().showPupupWindow(getActivity(),
					iv_sort, handleSortType_Interface, 1);
			break;
		case R.id.iv_search:// 搜索
			UmengEventUtil.store_search(mContext);
			intent.setClass(mContext, Search_OnStoreAty.class);
			startActivity(intent);
			break;
		case R.id.iv_scan:// 二维码扫描
			UmengEventUtil.store_qr(mContext);
			// intent.setClass(getActivity(), CaptureActivity.class);
			// 测试
			intent.setClass(getActivity(), SecKill_SessionActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_locaiton:// 定位
			UmengEventUtil.store_position(mContext);
			intent.setClass(mContext, SelectAddress_SearchAreaActivity.class);
			intent.putExtra(
					"cityList",
					CommonUtil.getStringOfSharedPreferences(
							mContext.getApplicationContext(), "city_list", ""));
			startActivityForResult(intent, REQUEST_SELECT_ADDRESS);
			break;
		case R.id.rl_loadData_error:// 重新加载数据
			// 秒杀数据
			if (tabType.equals("3")) {
				if (CommonUtil.emptyListToString3(secKillTimelist)) {
					getSecKillTimeList();
				} else {
					getSecKillContent();
				}
			} else {
				pageNo = 1;
				if (loadDataContent) {
					isShowLoading = true;
					getNavigationContent();

				} else {
					getNavigationList(true);
				}
			}
			break;
		}

	}

	@Override
	public void onDestroy() {
		closeTimer();
		if (null != dynamicReceiver) {
			getActivity().unregisterReceiver(dynamicReceiver);
		}
		super.onDestroy();
	}
}
