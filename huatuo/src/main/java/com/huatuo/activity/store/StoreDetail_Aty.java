package com.huatuo.activity.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huatuo.R;
import com.huatuo.activity.login.LoginActivity;
import com.huatuo.activity.map.GuideToStoreUtil;
import com.huatuo.activity.pay.QuickyPay_Activity;
import com.huatuo.activity.technician.AppraiseList_Avtivity;
import com.huatuo.adapter.Image_ViewPagerAdapter;
import com.huatuo.adapter.Service_girview_Adapter_store;
import com.huatuo.adapter.Technician_ListViewAdapter_store;
import com.huatuo.base.BaseActivity;
import com.huatuo.base.MyApplication;
import com.huatuo.custom_widget.CirclePageIndicator;
import com.huatuo.custom_widget.CustomGridView;
import com.huatuo.custom_widget.CustomHorizontalScrollView;
import com.huatuo.custom_widget.CustomListView;
import com.huatuo.custom_widget.MyScrollView;
import com.huatuo.custom_widget.MyScrollView.OnScrollListener;
import com.huatuo.custom_widget.PageIndicator;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.thread.AddCollect;
import com.huatuo.net.thread.CancelCollect;
import com.huatuo.net.thread.GetSearchTechList;
import com.huatuo.net.thread.GetStoreDetail;
import com.huatuo.net.thread.UserGetCoupon;
import com.huatuo.util.CallUtil;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Constants;
import com.huatuo.util.Custom_Toast_Collect;
import com.huatuo.util.DialogUtils;
import com.huatuo.util.DisplayUtil;
import com.huatuo.util.FormatDistanceUtil;
import com.huatuo.util.JsonUtil;
import com.huatuo.util.JumpTargetActivityUtil;
import com.huatuo.util.NumFormatUtil;
import com.huatuo.util.StringUtil;
import com.huatuo.util.Toast_Util;
import com.huatuo.util.UmengEventUtil;
import com.huatuo.util.UmengShare;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.utils.Log;

public class StoreDetail_Aty extends BaseActivity implements OnClickListener,
		OnScrollListener {
	private Context mContext;
	private UMShareAPI mShareAPI = null;
	private ViewPager viewPager;
	private PageIndicator mIndicator;
	private Image_ViewPagerAdapter myViewPagerAdapter;
	private Runnable viewpagerRunnable;
	private static Handler handler, handler_viewpager;
	private RelativeLayout rl_viewPager;
	private RelativeLayout rl_View_store_feature, rl_store_feature;// 门店特色
	// 顶部tab
	private RelativeLayout rl_call, rl_address, rl_pinglun, rl_quick_pay,
			rl_serviceProject, rl_View1, rl_View2, rl_View3, rl_gray_bar;
	private TextView tv_quick_pay;
	private CustomHorizontalScrollView horizontalListView_compoun;
	private LinearLayout horizontal_compoun_container;
	// private ImageView iv_store_icon, iv_store_icon_canSee;
	private TextView tv_StoreName, tv_price, tv_price1, tv_price2,
			tv_orderNumber, tv_orderNumber1, tv_orderNumber2,
			tv_store_pinglunshu, tv_store_pingfen, tv_store_tel,
			tv_store_distance, tv_store_workTime, tv_store_address, tv_project,
			tv_technician;
	private View v_project, v_technician, v_quick_pay;

	private RelativeLayout layout_store_info, rl_store_compuon;
	private ImageView imageView_next_appraise, iv_collect;

	private CustomGridView gv_store_service;
	private CustomListView lv_store_tech;
	// private Store_service_Adapter service_adapter;
	private Technician_ListViewAdapter_store technician_ListViewAdapter;
	private Service_girview_Adapter_store service_Grid_Adapter;

	private List<JSONObject> groupList;
	private GetStoreDetail getStoreDetail;
	private GetSearchTechList getSearchTechList;
	private Handler storeDetail_handler, tech_handeler;

	private AddCollect addCollect;
	private Handler addCollect_handler;

	private UserGetCoupon userGetCoupon;
	private Handler addCoupon_Handler;

	private CancelCollect cancelCollect;
	private Handler cancelCollect_handler;

	private Boolean isCollect = false;

	private String ID = "";
	private String storeName = "";
	private String minPrice, storeScore, orderCount, isFavorite, storeAddress,
			openTime, distance, introduction, longitude, latitude, isFlashPay,
			isReservable, store_feature;
	private String sex = "";
	private String orderType = "";// 1评价高，2接单多，不填默认按距离
	private String tel;
	private ArrayList<JSONObject> serviceList = new ArrayList<JSONObject>();// 服务列表
	private ArrayList<JSONObject> techList = new ArrayList<JSONObject>();// 图片列表
	private ArrayList<JSONObject> activityList = new ArrayList<JSONObject>();// 活动列表
	private ArrayList<JSONObject> imageList = new ArrayList<JSONObject>();// 图片列表
	private ArrayList<JSONObject> position_list;
	private JSONObject tech_obj;
	private JSONObject storeDetailObj;
	private String commentCount;// 评价数量
	private int pageStart = 1;// 第几页
	private String pageOffset = Constants.PAGE_SIZE_LIST;// 每页默认10条数据
	private int pageCount;// 总页数
	private int tupleCount;// 总纪录数
	private boolean isShowLoading = true;
	private boolean isLoadStoreInfo = true;

	// 透明渐变值 0是透明
	private float alpha = 0;
	float a;
	private MyScrollView scrollView1;
	private RelativeLayout rl_top;
	private ImageView iv_top;
	private TextView tv_name;
	private LinearLayout ll_back, ll_share;
	private LinearLayout filter_layout;
	private RelativeLayout rl_collect;
	private RelativeLayout rl_loadData_error, rl_loadData_empty;

	private LinearLayout search01, search02, ll_bar, ll_top;
	private int searchLayoutTop;

	private boolean push = false;// 是否是推送

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = this;
		// 友盟分享
		mShareAPI = UMShareAPI.get(this);
		MyApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_store_detail3);
		// scrollView1 = new ObservableScrollView(this);
		initView();
		handler = new Handler();
		handler_viewpager = new Handler_ViewPager();
		storeDetail_handler = new GetStoreDetail_Handler();
		tech_handeler = new getTechList_Handler();
		addCollect_handler = new HandlerAddCollect();
		cancelCollect_handler = new HandlerCancelCollect();
		addCoupon_Handler = new AddCoupon_Handler();
		initViewPager();
		getID();
		setListener();

	}

	/***
	 * 将activity 的创建模式设置为singletask,
	 * 使用这个方法可以再其他activity想这个activity发送Intent时，这个Intent能够及时更新
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		CommonUtil
				.logE("--------------------------------------------onNewIntent-----------------------------");
		super.onNewIntent(intent);
		setIntent(intent); // 这一句必须的，否则Intent无法获得最新的数据
		getID();
	}

	private void getID() {
		Bundle bundle = getIntent().getExtras();
		if (null != bundle) {
			ID = bundle.getString("ID");
			push = bundle.getBoolean("push");
			if (TextUtils.isEmpty(ID)) {
				// Toast_Util.showToast(mContext, "店铺ID缺少");
			} else {
				getSearchTechList(true);

			}
		}

	}

	private void initView() {
		// 列表
		gv_store_service = (CustomGridView) findViewById(R.id.gv_store_service);
		lv_store_tech = (CustomListView) findViewById(R.id.lv_store_tech);
		scrollView1 = (MyScrollView) findViewById(R.id.scrollView1);
		imageView_next_appraise = (ImageView) findViewById(R.id.imageView_next_appraise);
		iv_collect = (ImageView) findViewById(R.id.iv_collect);
		horizontalListView_compoun = (CustomHorizontalScrollView) findViewById(R.id.lv_store_compuon);
		horizontal_compoun_container = (LinearLayout) findViewById(R.id.horizontal_compounn_container);
		filter_layout = (LinearLayout) findViewById(R.id.filter_layout);
		rl_top = (RelativeLayout) findViewById(R.id.rl_top);
		rl_viewPager = (RelativeLayout) findViewById(R.id.rl_viewPager);
		rl_quick_pay = (RelativeLayout) findViewById(R.id.rl_quick_pay);
		rl_store_compuon = (RelativeLayout) findViewById(R.id.rl_store_compuon);
		rl_serviceProject = (RelativeLayout) findViewById(R.id.rl_serviceProject);
		rl_call = (RelativeLayout) findViewById(R.id.rl_call);
		rl_address = (RelativeLayout) findViewById(R.id.rl_address);
		rl_pinglun = (RelativeLayout) findViewById(R.id.rl_pinglun);
		// 门店特色
		rl_View_store_feature = (RelativeLayout) findViewById(R.id.rl_View_store_feature);
		rl_store_feature = (RelativeLayout) findViewById(R.id.rl_store_feature);
		rl_View1 = (RelativeLayout) findViewById(R.id.rl_View1);
		rl_View2 = (RelativeLayout) findViewById(R.id.rl_View2);
		rl_View3 = (RelativeLayout) findViewById(R.id.rl_View3);
		rl_gray_bar = (RelativeLayout) findViewById(R.id.rl_gray_bar);
		layout_store_info = (RelativeLayout) findViewById(R.id.layout_store_info);
		tv_StoreName = (TextView) findViewById(R.id.tv_StoreName);
		tv_price = (TextView) findViewById(R.id.tv_price);
		tv_price1 = (TextView) findViewById(R.id.tv_price1);
		tv_price2 = (TextView) findViewById(R.id.tv_price2);
		tv_orderNumber = (TextView) findViewById(R.id.tv_orderNumber);
		tv_orderNumber1 = (TextView) findViewById(R.id.tv_orderNumber1);
		tv_orderNumber2 = (TextView) findViewById(R.id.tv_orderNumber2);
		tv_store_pinglunshu = (TextView) findViewById(R.id.tv_store_pinglunshu);
		tv_store_pingfen = (TextView) findViewById(R.id.tv_store_pingfen);
		tv_quick_pay = (TextView) findViewById(R.id.tv_quick_pay);
		tv_store_tel = (TextView) findViewById(R.id.tv_store_tel);
		tv_store_workTime = (TextView) findViewById(R.id.tv_store_workTime);
		tv_store_address = (TextView) findViewById(R.id.tv_store_address);
		tv_store_distance = (TextView) findViewById(R.id.tv_store_distance);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_project = (TextView) filter_layout.findViewById(R.id.tv_project);
		tv_technician = (TextView) filter_layout
				.findViewById(R.id.tv_technician);
		v_project = (View) filter_layout.findViewById(R.id.v_project);
		v_technician = (View) filter_layout.findViewById(R.id.v_technician);
		v_quick_pay = (View) findViewById(R.id.v_quick_pay);
		search01 = (LinearLayout) findViewById(R.id.search01);
		search02 = (LinearLayout) findViewById(R.id.search02);
		ll_bar = (LinearLayout) findViewById(R.id.ll_bar);
		ll_top = (LinearLayout) findViewById(R.id.ll_top);
		// top_filter_layout1 = (LinearLayout)
		// findViewById(R.id.top_filter_layout1);
		// top_filter_layout = (RelativeLayout)
		// findViewById(R.id.top_filter_layout);
		ll_back = (LinearLayout) findViewById(R.id.ll_back1);
		ll_share = (LinearLayout) findViewById(R.id.ll_share);
		rl_collect = (RelativeLayout) findViewById(R.id.rl_collect);
		iv_top = (ImageView) findViewById(R.id.iv_top);
		tv_name = (TextView) findViewById(R.id.tv_name);
		iv_top.setFocusable(true);
		iv_top.setFocusableInTouchMode(true);
		iv_top.requestFocus();

		service_Grid_Adapter = new Service_girview_Adapter_store(
				StoreDetail_Aty.this);
		technician_ListViewAdapter = new Technician_ListViewAdapter_store(
				StoreDetail_Aty.this);

		gv_store_service.setAdapter(service_Grid_Adapter);
		lv_store_tech.setAdapter(technician_ListViewAdapter);

		changeTab(1);

		scrollView1
				.setScrollViewListener(new com.huatuo.custom_widget.MyScrollViewListener() {

					@SuppressLint("NewApi")
					@Override
					public void onScrollChanged(MyScrollView scrollView, int x,
							int y, int oldx, int oldy) {
						rl_viewPager.setY(oldy / 2);
						if (0 != y && 0 != oldy) {
							a = (float) 2 * y
									/ (float) rl_viewPager.getHeight();
							iv_top.setAlpha(a);
							tv_name.setAlpha(a);
						}
						ll_back.setBackgroundColor(Color.argb(0, 0, 0, 0));
						ll_share.setBackgroundColor(Color.argb(0, 0, 0, 0));
						rl_collect.setBackgroundColor(Color.argb(0, 0, 0, 0));

					}
				});

		scrollView1
				.setOnBorderListener(new com.huatuo.custom_widget.OnBorderListener() {

					@SuppressLint("NewApi")
					@Override
					public void onTop() {
						// TODO Auto-generated method stub
						rl_viewPager.setY(0);
						ll_back.setBackgroundResource(R.drawable.icon_bg);
						ll_share.setBackgroundResource(R.drawable.icon_bg);
						rl_collect.setBackgroundResource(R.drawable.icon_bg);
					}

					@SuppressLint("NewApi")
					@Override
					public void onBottom() {
						// TODO Auto-generated method stub
						loadMore();
					}
				});

		// 加载数据失败页面
		rl_loadData_error = (RelativeLayout) findViewById(R.id.rl_loadData_error);
		rl_loadData_error.setOnClickListener(this);
		// 加载数据前的白界面
		rl_loadData_empty = (RelativeLayout) findViewById(R.id.rl_loadData_empty);

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			searchLayoutTop = ll_top.getBottom()
					- DisplayUtil.dip2px(mContext, 50);// 获取searchLayout的顶部位置
			CommonUtil.logE("searchLayoutTop-------------->" + searchLayoutTop);
		}
	}

	@Override
	public void onScroll(int scrollY) {
		// TODO Auto-generated method stub
		if (scrollY >= searchLayoutTop) {
			if (ll_bar.getParent() != search01) {
				search02.removeView(ll_bar);
				search01.addView(ll_bar);
			}
		} else {
			if (ll_bar.getParent() != search02) {
				search01.removeView(ll_bar);
				search02.addView(ll_bar);
			}
		}
	}

	public void loadMore() {
		CommonUtil.log("pageStart:" + pageStart + "\n" + "pageCount:"
				+ pageCount);
		if (lv_store_tech.getVisibility() == View.VISIBLE) {
			if (pageStart < pageCount) {
				if (!isShowLoading) {
					++pageStart;
					getSearchTechList(false);
				}
			} else {
				if (pageStart > 1) {
					// Toast_Util.showToastOnlyOne(mContext,
					// mContext.getResources().getString(R.string.load_no_more_data));
				}
			}
		}
	}

	private void setListener() {
		rl_pinglun.setOnClickListener(this);
		rl_call.setOnClickListener(this);
		rl_address.setOnClickListener(this);
		rl_pinglun.setOnClickListener(this);
		tv_quick_pay.setOnClickListener(this);
		layout_store_info.setOnClickListener(this);
		rl_collect.setOnClickListener(this);
		ll_share.setOnClickListener(this);
		tv_project.setOnClickListener(this);
		tv_technician.setOnClickListener(this);
		rl_store_feature.setOnClickListener(this);
		scrollView1.setOnScrollListener(this);
		ll_back.setOnClickListener(this);
	}

	/**
	 * 获取店铺技師列表详情
	 */
	private void getSearchTechList(boolean isFirst) {
		showCustomCircleProgressDialog(null,
				getString(R.string.common_toast_net_prompt_submit));
		isShowLoading = true;
		if (isFirst) {
			isLoadStoreInfo = true;
			pageStart = 1;
		} else {
			isLoadStoreInfo = false;
		}
		HashMap<String, String> inJsonObject = new HashMap<String, String>();

		String citycode = CommonUtil.getStringOfSharedPreferences(
				mContext.getApplicationContext(), "NOW_CITYCODE", "");
		String lng = CommonUtil.getStringOfSharedPreferences(
				mContext.getApplicationContext(), "NOW_LNG", "");
		String lat = CommonUtil.getStringOfSharedPreferences(
				mContext.getApplicationContext(), "NOW_LAT", "");
		inJsonObject.put("longitude", lng);
		inJsonObject.put("latitude", lat);
		inJsonObject.put("cityCode", citycode);

		inJsonObject.put("storeID", ID);
		inJsonObject.put("pageStart", pageStart + "");
		inJsonObject.put("pageOffset", pageOffset);
		getSearchTechList = new GetSearchTechList(mContext, tech_handeler,
				inJsonObject);
		Thread thread = new Thread(getSearchTechList);
		thread.start();
	}

	/**
	 * 处理店铺详情
	 * 
	 * @author Android开发工程师
	 * 
	 */
	class getTechList_Handler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			isShowLoading = false;
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				tech_obj = getSearchTechList.getOutJsonObject();
				techList = getSearchTechList.getTechList();
				// Log.e("techList", "techList------------>" + techList);
				// 获取返回数据的页数
				if (null != tech_obj) {
					String totalPages = tech_obj.optString("pageCount", "0");
					String totalCounts = tech_obj.optString("totalCounts", "0");
					CommonUtil.log("店铺详情技师总共页数：totalPages：" + totalPages);
					CommonUtil.log("店铺详情总共页数：totalCounts：" + totalCounts);
					if (!TextUtils.isEmpty(totalPages)) {
						pageCount = Integer.parseInt(totalPages);// 总页数;
					}
					if (!TextUtils.isEmpty(totalCounts)) {
						tupleCount = Integer.parseInt(totalCounts);// 总纪录数;
					}

					if (pageStart == 1) {
						technician_ListViewAdapter.clear();
						technician_ListViewAdapter.add(techList);
					} else {
						technician_ListViewAdapter.add(techList);
					}

					if (techList.size() == 0) {
						filter_layout.setVisibility(View.GONE);
						lv_store_tech.setVisibility(View.GONE);
					}

				}
				if (isLoadStoreInfo) {
					getStoreDetail();
				}
				break;
			case MsgId.DOWN_DATA_F:
				if (pageStart > 1) {
					pageStart--;

				}

				if (pageStart == 1) {
					rl_loadData_error.setVisibility(View.VISIBLE);
				} else {
					rl_loadData_error.setVisibility(View.GONE);
				}
				closeCustomCircleProgressDialog();
				break;
			case MsgId.NET_NOT_CONNECT:
				if (pageStart > 1) {
					pageStart--;
				}

				if (pageStart == 1) {
					rl_loadData_error.setVisibility(View.VISIBLE);
				} else {
					rl_loadData_error.setVisibility(View.GONE);
				}
				closeCustomCircleProgressDialog();
				// setCustomDialog(getString(R.string.common_toast_net_not_connect),
				// true);
				break;
			default:
				break;
			}
		}
	}

	/* 添加兑换券 */
	private void addCoupon(String activityID) {
		showCustomCircleProgressDialog(null,
				getString(R.string.common_toast_net_prompt_submit));
		userGetCoupon = new UserGetCoupon(mContext, addCoupon_Handler,
				activityID);
		Thread thread = new Thread(userGetCoupon);
		thread.start();
	}

	class AddCoupon_Handler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			String OutMsg;
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				Toast_Util.showToastOfLONG(mContext,
						"您已成功领取优惠券，可在“我的华佗-优惠兑换”中查看并使用。");
				getStoreDetail();
				break;
			case MsgId.DOWN_DATA_F:
				closeCustomCircleProgressDialog();
				OutMsg = userGetCoupon.getOutMsg();
				Toast_Util.showToast(mContext, OutMsg);
				break;
			case MsgId.NET_NOT_CONNECT:
				setCustomDialog(
						getString(R.string.common_toast_net_not_connect), true);
				break;
			}
		}
	}

	/**
	 * 收藏
	 * 
	 * @param skillWorkerID
	 */
	private void addCollect() {
		showCustomCircleProgressDialog(null,
				getString(R.string.common_toast_net_prompt_submit));

		// userID 用户ID
		// itemType 收藏内容类型
		// itemID 内容id
		HashMap<String, String> inJsonMap = new HashMap<String, String>();
		inJsonMap.put("userID", MyApplication.getUserID());
		inJsonMap.put("itemType", "0");
		inJsonMap.put("itemID", ID);
		addCollect = new AddCollect(mContext, addCollect_handler, inJsonMap);
		Thread thread = new Thread(addCollect);
		thread.start();
	}

	class HandlerAddCollect extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				changeCollectType(true);
				break;
			case MsgId.DOWN_DATA_F:
				closeCustomCircleProgressDialog();
				DialogUtils.showToastMsg(mContext,
						getString(R.string.common_toast_net_down_data_fail),
						Toast.LENGTH_SHORT);
				break;
			case MsgId.NET_NOT_CONNECT:
				setCustomDialog(
						getString(R.string.common_toast_net_not_connect), true);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 收藏
	 * 
	 * @param skillWorkerID
	 */
	private void cancelCollect() {
		showCustomCircleProgressDialog(null,
				getString(R.string.common_toast_net_prompt_submit));
		//
		// userID 用户ID ANS 64 M 用户ID
		// itemID 项目Id ANS 11 M 项目Id
		// itemType 类型 ANS 11 M 0 门店 1 服务 2 技师 3 发现
		HashMap<String, String> inJsonMap = new HashMap<String, String>();
		inJsonMap.put("userID", MyApplication.getUserID());
		inJsonMap.put("itemID", ID);
		inJsonMap.put("itemType", "0");// itemType 类型 ANS 11 M 0 门店 1 服务 2 技师 3
										// 发现

		cancelCollect = new CancelCollect(mContext, cancelCollect_handler,
				inJsonMap);
		Thread thread = new Thread(cancelCollect);
		thread.start();
	}

	class HandlerCancelCollect extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				changeCollectType(false);
				break;
			case MsgId.DOWN_DATA_F:
				closeCustomCircleProgressDialog();
				DialogUtils.showToastMsg(mContext,
						getString(R.string.common_toast_net_down_data_fail),
						Toast.LENGTH_SHORT);
				break;
			case MsgId.NET_NOT_CONNECT:
				setCustomDialog(
						getString(R.string.common_toast_net_not_connect), true);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 改变收藏参数和提示语 状态
	 * 
	 * @param type
	 */
	private void changeCollectType(boolean type) {
		isCollect = type;
		changeCollectIcon(type);
		Custom_Toast_Collect.getInstance().handleCollect(this, type, false);
	}

	/**
	 * 改变收藏按钮样式
	 * 
	 * @param type
	 */
	private void changeCollectIcon(boolean type) {
		Custom_Toast_Collect.getInstance().handleCollectIcon(this, type,
				iv_collect);
	}

	/**
	 * 获取店铺详情
	 */
	private void getStoreDetail() {
		showCustomCircleProgressDialog(null,
				getString(R.string.common_toast_net_prompt_submit));
		getStoreDetail = new GetStoreDetail(mContext, storeDetail_handler, ID);
		Thread thread = new Thread(getStoreDetail);
		thread.start();
	}

	private void handleData(JSONObject jsonObject) {
		ID = jsonObject.optString("ID", ""); // 店铺ID String
		storeName = jsonObject.optString("name", ""); // 店铺名称
		minPrice = jsonObject.optString("minPrice", ""); // 店铺名称
		storeScore = jsonObject.optString("score", ""); // 店铺分数
		orderCount = jsonObject.optString("orderCount", ""); // 订单数
		isFavorite = jsonObject.optString("isFavorite", ""); // 是否收藏
		storeAddress = jsonObject.optString("address", ""); // 店铺地址
		openTime = jsonObject.optString("openTime", ""); // 营业开始时间
		distance = jsonObject.optString("distance", ""); // 距离
		introduction = jsonObject.optString("introduction", "");// 店铺详细介绍
		tel = jsonObject.optString("tel", ""); // 服务电话
		commentCount = jsonObject.optString("commentCount", ""); // 店铺评价数量
		longitude = jsonObject.optString("longitude", "");// 经度
		latitude = jsonObject.optString("latitude", "");// 纬度
		isFlashPay = jsonObject.optString("isFlashPay", "");// 类型0：否，1：是
		isReservable = jsonObject.optString("isReservable", "");// 是否展示[0:仅展示,1:可约]

		store_feature = jsonObject.optString("storeFeature", "");// 门店特色

		// 门店特色是否展示
		if (!TextUtils.isEmpty(store_feature)
				&& !TextUtils.isEmpty(StringUtil.replaceBlank(store_feature))) {
			CommonUtil
					.log("store_feature:不为空:" + store_feature.trim().length());
			rl_store_feature.setVisibility(View.VISIBLE);
			rl_View_store_feature.setVisibility(View.VISIBLE);
		} else {
			CommonUtil.log("store_feature:为空");
			rl_store_feature.setVisibility(View.GONE);
			rl_View_store_feature.setVisibility(View.GONE);
		}

		serviceList = getStoreDetail.getServiceList();
		activityList = getStoreDetail.getActivityList();
		imageList = getStoreDetail.getImgList();

		if (techList.size() == 0 && serviceList.size() == 0) {
			filter_layout.setVisibility(View.GONE);
			lv_store_tech.setVisibility(View.GONE);
			rl_serviceProject.setVisibility(View.GONE);
			gv_store_service.setVisibility(View.GONE);
		}

		if (techList.size() == 0 && serviceList.size() != 0) {
			filter_layout.setVisibility(View.GONE);
			lv_store_tech.setVisibility(View.GONE);
			rl_serviceProject.setVisibility(View.VISIBLE);
			gv_store_service.setVisibility(View.VISIBLE);
		}

		service_Grid_Adapter.add(serviceList);
		service_Grid_Adapter.notifyDataSetChanged();

		tv_StoreName.setText(storeName);
		tv_price.setText(NumFormatUtil.centFormatYuanToString(minPrice) + "");
		tv_name.setText(storeName);
		tv_orderNumber.setText(orderCount);
		tv_store_pingfen.setText(storeScore);
		tv_store_workTime.setText("营业：" + openTime);
		tv_store_tel.setText(tel);
		tv_store_address.setText(storeAddress);
		tv_store_distance.setText(FormatDistanceUtil.formatDistance(distance));

		if ("0".equals(isReservable)) {
			tv_price.setVisibility(View.GONE);
			tv_price1.setVisibility(View.GONE);
			tv_price2.setVisibility(View.GONE);
			tv_orderNumber.setVisibility(View.GONE);
			tv_orderNumber1.setVisibility(View.GONE);
			tv_orderNumber2.setVisibility(View.GONE);
			rl_quick_pay.setVisibility(View.GONE);
			v_quick_pay.setVisibility(View.GONE);
			rl_store_compuon.setVisibility(View.GONE);
			rl_pinglun.setVisibility(View.GONE);
			rl_View3.setVisibility(View.GONE);
			rl_serviceProject.setVisibility(View.GONE);
			filter_layout.setVisibility(View.GONE);
			tv_store_pingfen.setVisibility(View.GONE);
			lv_store_tech.setVisibility(View.GONE);
			gv_store_service.setVisibility(View.GONE);
		} else {

			if (filter_layout.getVisibility() == View.GONE) {
				filter_layout.setVisibility(View.GONE);
			} else {
				filter_layout.setVisibility(View.VISIBLE);
			}

			if (lv_store_tech.getVisibility() == View.GONE) {
				lv_store_tech.setVisibility(View.GONE);
			} else {
				lv_store_tech.setVisibility(View.VISIBLE);
			}

			if (rl_serviceProject.getVisibility() == View.GONE) {
				rl_serviceProject.setVisibility(View.GONE);
			} else {
				rl_serviceProject.setVisibility(View.VISIBLE);
			}

			if (gv_store_service.getVisibility() == View.GONE) {
				gv_store_service.setVisibility(View.GONE);
			} else {
				gv_store_service.setVisibility(View.VISIBLE);
			}

			tv_price.setVisibility(View.VISIBLE);
			tv_price1.setVisibility(View.VISIBLE);
			tv_price2.setVisibility(View.VISIBLE);
			// tv_orderNumber.setVisibility(View.VISIBLE);
			// tv_orderNumber1.setVisibility(View.VISIBLE);
			// tv_orderNumber2.setVisibility(View.VISIBLE);
			rl_quick_pay.setVisibility(View.VISIBLE);
			v_quick_pay.setVisibility(View.VISIBLE);
			rl_store_compuon.setVisibility(View.VISIBLE);
			rl_pinglun.setVisibility(View.VISIBLE);
			rl_View3.setVisibility(View.VISIBLE);
			tv_store_pingfen.setVisibility(View.VISIBLE);
		}

		if (activityList.size() == 0) {
			rl_store_compuon.setVisibility(View.GONE);
		}

		if ("0".equals(isFlashPay)) {
			rl_quick_pay.setVisibility(View.GONE);
			v_quick_pay.setVisibility(View.GONE);
		} else {
			rl_quick_pay.setVisibility(View.VISIBLE);
			v_quick_pay.setVisibility(View.VISIBLE);
		}

		if (!TextUtils.isEmpty(commentCount) && !("0").equals(commentCount)) {
			tv_store_pinglunshu.setText(commentCount + "评");
			tv_store_pinglunshu.setTextColor(R.color.c5);
		} else {
			tv_store_pinglunshu.setText("暂无评论");
			tv_store_pinglunshu.setTextColor(R.color.c6);
		}

		// 收藏类型 0：否，1：是，
		CommonUtil.log("isFavorite:" + isFavorite);
		if (!TextUtils.isEmpty(isFavorite)) {
			if (("0").equals(isFavorite.trim())) {
				isCollect = false;
			} else {
				isCollect = true;
			}
		}

		changeCollectIcon(isCollect);
		CommonUtil.log("service_list_all:" + serviceList);
		service_Grid_Adapter.clear();
		service_Grid_Adapter.add(serviceList);
		CommonUtil.log("position_list:" + position_list);

		ArrayList<JSONObject> imageList = new ArrayList<JSONObject>();
		JsonUtil.jsonArray2List(jsonObject.optJSONArray("imageList"), imageList);
		myViewPagerAdapter.clear();
		myViewPagerAdapter.add(imageList);
		handleHorizontalListView_compoun(horizontal_compoun_container);
	}

	private String getDefaultImage() {
		JSONObject jsonObject = null;
		String url = "";
		for (int i = 0; i < imageList.size(); i++) {
			jsonObject = imageList.get(i);
			if (null != jsonObject) {
				// isDefault 是否默认大图 N 1 M 0 否 1是

				String isDefault = jsonObject.optString("isDefault");
				if (("1").equals(isDefault)) {
					url = jsonObject.optString("url", "");
				}
			}
		}

		CommonUtil.log("url:" + url);
		return url;

	}

	/**
	 * 处理compoun
	 */
	private void handleHorizontalListView_compoun(
			LinearLayout horizontal_compoun_container) {
		horizontal_compoun_container.removeAllViews();
		// if (CommonUtil.emptyListToString2(position_list) != null) {
		View convertView;
		for (int i = 0; i < activityList.size(); i++) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.activity_storedetail_exchange_list_adapter, null);
			// 给item布局添加ID,此ID为int类型任意值
			convertView.setId(i);
			// ImageView iv_couponImgUrl = (ImageView)
			// convertView.findViewById(R.id.iv_couponImgUrl);
			TextView textView1 = (TextView) convertView
					.findViewById(R.id.textView1);
			TextView tv_couponName = (TextView) convertView
					.findViewById(R.id.tv_couponName);
			ImageView iv_used = (ImageView) convertView
					.findViewById(R.id.iv_used);
			LinearLayout ll_root = (LinearLayout) convertView
					.findViewById(R.id.ll_root1);
			String name = activityList.get(i).optString("name", "");
			String ID = activityList.get(i).optString("ID", "");
			String isReceiveable = activityList.get(i).optString(
					"isReceiveable", "");
			if ("1".equals(isReceiveable)) {
				iv_used.setVisibility(View.GONE);
				ll_root.setBackgroundResource(R.drawable.bg_coupon_get);
				textView1.setTextColor(mContext.getResources().getColor(
						R.color.store_cunpon));
				tv_couponName.setTextColor(mContext.getResources().getColor(
						R.color.store_cunpon));
			} else if ("0".equals(isReceiveable)) {
				ll_root.setBackgroundResource(R.drawable.bg_coupon_getgary);
				iv_used.setVisibility(View.VISIBLE);
				textView1.setTextColor(mContext.getResources().getColor(
						R.color.store_cunpon_gray));
				tv_couponName.setTextColor(mContext.getResources().getColor(
						R.color.store_cunpon_gray));
				convertView.setClickable(false);
			}
			tv_couponName.setText(name);
			convertView.setPadding(CommonUtil.dip2px(mContext, 3),
					CommonUtil.dip2px(mContext, 6),
					CommonUtil.dip2px(mContext, 8),
					CommonUtil.dip2px(mContext, 3));
			LayoutParams layoutParams = new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			convertView.setLayoutParams(layoutParams);
			convertView.setOnClickListener(new CompounOnClickListener());
			horizontal_compoun_container.addView(convertView);

		}
	}

	class CompounOnClickListener implements OnClickListener {

		public CompounOnClickListener() {
		}

		@Override
		public void onClick(View v) {
			UmengEventUtil.store_storedetail_coupon(mContext);
			int checkedId = v.getId();
			CommonUtil.log("checkedId:" + checkedId);
			String activityID = activityList.get(checkedId).optString("ID", "");
			if (MyApplication.getLoginFlag()) {
				addCoupon(activityID);
			} else {
				Toast_Util.showToast(mContext, "抱歉，您尚未登录");
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
			textView.setBackgroundResource(R.drawable.shopdetail_tab_bg_sel);
			textView.setTextColor(getResources().getColor(R.color.white));
		} else {
			textView.setBackgroundResource(R.drawable.shopdetail_tab_bg);
			textView.setTextColor(getResources().getColor(
					R.color.nearShop_select_color));
		}

	}

	/**
	 * 处理店铺详情
	 * 
	 * @author Android开发工程师
	 * 
	 */
	class GetStoreDetail_Handler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			rl_loadData_empty.setVisibility(View.GONE);
			rl_loadData_error.setVisibility(View.GONE);
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				storeDetailObj = getStoreDetail.getOutJsonObject();
				if (null != storeDetailObj) {
					handleData(storeDetailObj);
				}
				break;
			case MsgId.DOWN_DATA_F:
				// 加载数据失败
				rl_loadData_error.setVisibility(View.VISIBLE);
				closeCustomCircleProgressDialog();
				break;
			case MsgId.NET_NOT_CONNECT:
				// 加载数据失败
				rl_loadData_error.setVisibility(View.VISIBLE);
				closeCustomCircleProgressDialog();
				// setCustomDialog(getString(R.string.common_toast_net_not_connect),
				// true);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 打电话
	 */
	private void call() {
		CallUtil.showCallDialog(mContext, tel);
	}

	private void initTab() {
		gv_store_service.setVisibility(View.GONE);
		lv_store_tech.setVisibility(View.GONE);
		tv_project.setTextColor(StoreDetail_Aty.this.getResources().getColor(
				R.color.c5));
		tv_technician.setTextColor(StoreDetail_Aty.this.getResources()
				.getColor(R.color.c5));
		v_project.setVisibility(View.INVISIBLE);
		v_technician.setVisibility(View.INVISIBLE);
	}

	private void changeTab(int tag) {
		initTab();
		switch (tag) {
		case 1:
			gv_store_service.setVisibility(View.VISIBLE);
			tv_project.setTextColor(StoreDetail_Aty.this.getResources()
					.getColor(R.color.c3));
			v_project.setVisibility(View.VISIBLE);
			break;
		case 2:
			lv_store_tech.setVisibility(View.VISIBLE);
			tv_technician.setTextColor(StoreDetail_Aty.this.getResources()
					.getColor(R.color.c3));
			v_technician.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}

	/**
	 * 初始化ViewPager(广告位)
	 */
	private void initViewPager() {
		viewPager = (ViewPager) findViewById(R.id.first_viewPager);

		CommonUtil.initScreen(this);
		android.view.ViewGroup.LayoutParams layoutParams = viewPager
				.getLayoutParams();
		layoutParams.width = CommonUtil.WIDTH_SCREEN;
		layoutParams.height = layoutParams.width / 4 * 3;
		viewPager.setLayoutParams(layoutParams);

		myViewPagerAdapter = new Image_ViewPagerAdapter(this);
		viewPager.setAdapter(myViewPagerAdapter);
		viewPager.setCurrentItem(0);
		mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(viewPager);
		mIndicator.setOnPageChangeListener(new MyOnPageChangeListener());

		// initRunnable();
		// initAdvertViewPagerAdapter();
		// viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	/**
	 * ViewPager监听
	 */
	class MyOnPageChangeListener implements OnPageChangeListener {
		public void onPageScrollStateChanged(int arg0) {
		}

		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
		}

		public void onPageSelected(final int position) {
			// setCurrentDot(position);
			// DialogUtils.showToastMsg(mContext, position+"选中",
			// Toast.LENGTH_SHORT);
		}
	}

	/**
	 * 处理Bannner
	 * 
	 * @author Android开发工程师
	 * 
	 */
	class Handler_ViewPager extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				// closeCustomCircleProgressDialog();

				// getProjectSortList();

				break;
			case MsgId.DOWN_DATA_F:
				closeCustomCircleProgressDialog();

				DialogUtils.showToastMsg(mContext, mContext.getResources()
						.getString(R.string.common_toast_net_down_data_fail),
						Toast.LENGTH_SHORT);
				break;
			case MsgId.NET_NOT_CONNECT:
				setCustomDialog(
						mContext.getResources().getString(
								R.string.common_toast_net_not_connect), true);
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {

		Intent intent = null;

		switch (v.getId()) {
		case R.id.rl_pingfen1:// 去评价界面
			if (TextUtils.isEmpty(commentCount) || ("0").equals(commentCount)) {
				break;
			}

			intent = new Intent();
			intent.setClass(mContext, AppraiseList_Avtivity.class);
			intent.putExtra("ID", ID);
			intent.putExtra("type", Constants.APPRAISE_TYPE_STORE);
			startActivity(intent);
			break;

		case R.id.rl_call:// 打电话
			UmengEventUtil.store_storedetail_phone(mContext);
			call();
			break;
		case R.id.rl_address:
			UmengEventUtil.store_storedetail_adress(mContext);
			GuideToStoreUtil.getInstance().jumpToGuideActivity(this, latitude,
					longitude, storeAddress, storeName);
			break;
		case R.id.rl_pinglun:
			UmengEventUtil.store_storedetail_comment(mContext);
			if (TextUtils.isEmpty(commentCount) || ("0").equals(commentCount)) {
				break;
			}
			intent = new Intent(mContext, AppraiseList_Avtivity.class);
			intent.putExtra("ID", ID);
			intent.putExtra("type", Constants.APPRAISE_TYPE_STORE);
			startActivity(intent);
			break;
		case R.id.rl_store_feature:// 门店特色
			intent = new Intent(mContext, Store_FeatureActivity.class);
			intent.putExtra("ID", "10000020");
			intent.putExtra("store_feature", store_feature);
			startActivity(intent);
			break;

		case R.id.tv_quick_pay:// 快闪付

			if (!MyApplication.getLoginFlag()) {
				DialogUtils.showToastMsg(mContext, "你尚未登录,请登录!",
						Toast.LENGTH_SHORT);
				intent = new Intent();
				intent.setClass(mContext, LoginActivity.class);
				startActivity(intent);
				return;
			}
			
			UmengEventUtil.store_storedetail_pay(mContext);
			intent = new Intent();
			intent.setClass(mContext, QuickyPay_Activity.class);
			intent.putExtra("ID", ID);
			intent.putExtra("storeName", storeName);
			startActivity(intent);
			break;
		case R.id.rl_collect:
			UmengEventUtil.store_storedetail_collect(mContext);
			// Toast.makeText(mContext, "click collect , isCollect=" +
			// isCollect, 1).show();
			if (MyApplication.getLoginFlag()) {
				if (isCollect) {
					cancelCollect();
				} else {
					addCollect();
				}
			} else {
				Toast_Util.showToast(mContext, "抱歉，您尚未登录");
			}
			break;
		case R.id.ll_share:// 分享
			UmengEventUtil.store_storedetail_share(mContext);
			share();
			break;
		case R.id.tv_project:
			changeTab(1);
			break;
		case R.id.tv_technician:
			changeTab(2);
			break;

		case R.id.rl_loadData_error:// 重新加载数据
			getSearchTechList(true);
			break;
		case R.id.ll_back1:// 处理返回键
			handleOnBackPress();
			break;
		}
	}

	/**
	 * 处理返回键
	 */
	private void handleOnBackPress() {
		boolean has_open_app = CommonUtil.getBooleanOfSharedPreferences(this,
				"HAS_OPEN_APP", false);
		CommonUtil.logE("是否已经打开app：has_open_app：" + has_open_app);
		// 点击通知后或者首图--app未打开状态 -返回时回到主页面home
		if (push && !has_open_app) {
			CommonUtil.saveBooleanOfSharedPreferences(this, "HAS_OPEN_APP",
					true);
			JumpTargetActivityUtil.getInstance().jumpToHomeActivity(this, 0);
		} else {
			finish();
			// super.onBackPressed();
		}
	}

	@Override
	public void onBackPressed() {
		handleOnBackPress();
	}

	private void share() {
		String imageUrl = "";
		if (!CommonUtil.emptyListToString3(imageList)) {
			imageUrl = imageList.get(0).optString("url", "");
		}
		
		UmengShare.getInstance().initShareParams(StoreDetail_Aty.this, imageUrl,
				storeName, introduction, ID, Constants.SHARE_STORE);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		 UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
	}
}
