package com.huatuo.activity.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huatuo.R;
import com.huatuo.activity.appoint.Apponitment_OnStore_FromProjectActivity;
import com.huatuo.activity.appoint.Apponitment_OnStore_FromTechActivity;
import com.huatuo.activity.appoint.Apponitment_OnVisit_FromProjectActivity;
import com.huatuo.activity.appoint.Apponitment_OnVisit_FromTechActivity;
import com.huatuo.activity.home.HomeActivity;
import com.huatuo.activity.login.LoginActivity;
import com.huatuo.activity.map.GuideToStoreUtil;
import com.huatuo.activity.store.StoreDetail_Aty;
import com.huatuo.activity.technician.AppraiseList_Avtivity;
import com.huatuo.activity.technician.TechnicianDetail;
import com.huatuo.adapter.Image_ViewPagerAdapter;
import com.huatuo.adapter.ProjectDetailInfoAdapter;
import com.huatuo.adapter.ProjectDetailReminderAdapter;
import com.huatuo.base.BaseActivity;
import com.huatuo.base.MyApplication;
import com.huatuo.custom_widget.CirclePageIndicator;
import com.huatuo.custom_widget.CustomDialog;
import com.huatuo.custom_widget.CustomListView;
import com.huatuo.custom_widget.ObservableScrollView;
import com.huatuo.custom_widget.PageIndicator;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.thread.AddCollect;
import com.huatuo.net.thread.CancelCollect;
import com.huatuo.net.thread.GetServiceDetail;
import com.huatuo.util.CallUtil;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Constants;
import com.huatuo.util.Custom_Toast_Collect;
import com.huatuo.util.DialogUtils;
import com.huatuo.util.FormatDistanceUtil;
import com.huatuo.util.ImageLoader_DisplayImageOptions;
import com.huatuo.util.JsonUtil;
import com.huatuo.util.JumpTargetActivityUtil;
import com.huatuo.util.NumFormatUtil;
import com.huatuo.util.UmengEventUtil;
import com.huatuo.util.UmengShare;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ProjectDetailActivity extends BaseActivity implements OnClickListener {
	private Context mContext;
	private String msg;
	private Intent intent = new Intent();
	private Bundle bundle;
	private RelativeLayout rl_top, rl_pager, rl_store_info, rl_review, rl_technician;
	private ImageView iv_top;
	private TextView tv_name;
	private ImageView iv_store_address1, iv_next_tech, iv_userReview, iv_tech_icon, iv_next;
	private View view_tech;
	private RatingBar ratingBar1;
	private TextView tv_projectName, tv_projectPrice, tv_projectPrice2, tv_special, tv_projectPrice_old, tv_timeLong,
			tv_userReview, tv_projectReviewNum, tv_techName, tv_starNum, tv_orderNum, tv_techReviewNum, tv_service_from;
	private Button bt_appoint_commit;

	private TextView tv_position, tv_indications;
	private RelativeLayout line_pingfen1;
	private RelativeLayout line_store_adress;
	private RelativeLayout layou_store_adress, layou_store_call;
	private TextView tv_suoshudianpu, tv_fuwudizhi, tv_distance;
	private ObservableScrollView scrollView1;
	private LinearLayout ll_back, ll_share, ll_collect;
	private ImageView iv_share, iv_collect;
	private RelativeLayout rl_loadData_error, rl_loadData_empty;
	private RelativeLayout rl_commit;
	private CustomListView lv_service_info;
	private ProjectDetailInfoAdapter projectDetailInfoAdapter;
	private ArrayList<JSONObject> introList;
	private CustomListView lv_serviceNotes;
	private ProjectDetailReminderAdapter projectDetailReminderAdapter;
	private ViewPager viewPager;
	private PageIndicator mIndicator;
	private Image_ViewPagerAdapter myViewPagerAdapter;
	private Runnable viewpagerRunnable;
	private static Handler handler;

	private GetServiceDetail getServiceDetail;
	private Handler sysServiceDetail_handler;

	private String servID, servBelong, serviceName, sitType, serviceTimeLong, servNro, servImg, evalNum, storeID,
			storeName, storeAddress, distance, storeTel, minServPrice, maxServPrice, applyPosition, applySymptoms,
			isFavorite, isSelfOwned, isLevel, isGoHome, latitude, longitude;

	private String techName, techOrderCount, techGradeID, techGradeName, techIntroduction, techCommentCount, techScore;
	private String techIcon;
	private String techIsFavorite;
	private String state, storeState, workerState;// 3:生效，9：已下架
	private ArrayList<JSONObject> reminder, imgList;
	private List<JSONObject> list_reminder;
	private JSONObject serviceDetailObj;
	private JSONObject techObj;

	// 透明渐变值 0是透明
	private float alpha = 0;
	private float a;

	// 收藏与取消
	private AddCollect addCollect;// 收藏
	private CancelCollect cancelCollect;// 取消收藏
	private boolean isCollect = false;
	public Handler handlerAddCollect, handlerCancelCollect;

	private String workerID = "";
	// private int servType;
	private int appointServType = -100;
	private boolean push = false;// 是否是推送

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = this;
		MyApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_project_detail);
		initHandle();
		sysServiceDetail_handler = new SysServiceDetail_handler();
		initView();
		initViewPager();
		getServID();

	}

	/***
	 * 将activity 的创建模式设置为singletask,
	 * 使用这个方法可以再其他activity想这个activity发送Intent时，这个Intent能够及时更新
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		CommonUtil.logE("--------------------------------------------onNewIntent-----------------------------");
		super.onNewIntent(intent);
		setIntent(intent); // 这一句必须的，否则Intent无法获得最新的数据
		getServID();
	}

	private void initHandle() {
		handlerAddCollect = new HandlerAddCollect();
		handlerCancelCollect = new HandlerCancelCollect();
		handler = new Handler();
	}

	private void getServID() {
		Bundle bundle = getIntent().getExtras();
		if (null != bundle) {
			servID = bundle.getString("ID");
			workerID = bundle.getString("workerID");
			push = bundle.getBoolean("push");
			// 获取项目id
			CommonUtil.log("servID:" + servID);
			CommonUtil.log("workerID:" + workerID);
			getServiceDetail(servID, workerID);
			CommonUtil.log("push:" + push);

		}

	}

	private void initView() {

		iv_top = (ImageView) findViewById(R.id.iv_top);
		tv_name = (TextView) findViewById(R.id.tv_name);
		scrollView1 = (ObservableScrollView) findViewById(R.id.scrollView1);

		// titlebar
		ll_back = (LinearLayout) findViewById(R.id.ll_back1);
		ll_share = (LinearLayout) findViewById(R.id.ll_share);
		ll_collect = (LinearLayout) findViewById(R.id.ll_collect);
		iv_share = (ImageView) findViewById(R.id.iv_share);// 分享
		iv_collect = (ImageView) findViewById(R.id.iv_collect);// 收藏

		rl_top = (RelativeLayout) findViewById(R.id.rl_top);
		// 服务信息
		tv_projectName = (TextView) findViewById(R.id.tv_projectName);
		tv_projectPrice = (TextView) findViewById(R.id.tv_projectPrice);
		tv_projectPrice2 = (TextView) findViewById(R.id.tv_projectPrice2);
		tv_special = (TextView) findViewById(R.id.tv_special);
		tv_projectPrice_old = (TextView) findViewById(R.id.tv_projectPrice_old);
		tv_timeLong = (TextView) findViewById(R.id.tv_timeLong);
		rl_pager = (RelativeLayout) findViewById(R.id.rl_pager);

		iv_top.setFocusable(true);
		iv_top.setFocusableInTouchMode(true);
		iv_top.requestFocus();

		// 用户评论
		rl_review = (RelativeLayout) findViewById(R.id.rl_review);
		tv_userReview = (TextView) findViewById(R.id.tv_userReview);
		tv_projectReviewNum = (TextView) findViewById(R.id.tv_projectReviewNum);

		// 技师信息

		rl_technician = (RelativeLayout) findViewById(R.id.rl_technician);
		ratingBar1 = (RatingBar) findViewById(R.id.ratingBar1);
		tv_techName = (TextView) findViewById(R.id.tv_techName);
		tv_starNum = (TextView) findViewById(R.id.tv_starNum);
		tv_techReviewNum = (TextView) findViewById(R.id.tv_techReviewNum);
		tv_orderNum = (TextView) findViewById(R.id.tv_orderNum);
		iv_next_tech = (ImageView) findViewById(R.id.iv_next_tech);
		iv_userReview = (ImageView) findViewById(R.id.iv_userReview);
		iv_tech_icon = (ImageView) findViewById(R.id.iv_tech_icon);
		iv_next = (ImageView) findViewById(R.id.iv_next);
		view_tech = (View) findViewById(R.id.view_tech);
		// 服务
		tv_position = (TextView) findViewById(R.id.tv_position);
		tv_indications = (TextView) findViewById(R.id.tv_indications);
		bt_appoint_commit = (Button) findViewById(R.id.bt_appoint_commit);
		bt_appoint_commit.setText("预约");

		// 服务介绍
		lv_service_info = (CustomListView) findViewById(R.id.lv_project_info);
		projectDetailInfoAdapter = new ProjectDetailInfoAdapter(mContext);
		lv_service_info.setAdapter(projectDetailInfoAdapter);

		lv_service_info.setAdapter(projectDetailInfoAdapter);
		// 服务须知
		lv_serviceNotes = (CustomListView) findViewById(R.id.lv_serviceNotes);
		projectDetailReminderAdapter = new ProjectDetailReminderAdapter(mContext);
		lv_serviceNotes.setAdapter(projectDetailReminderAdapter);
		tv_service_from = (TextView) findViewById(R.id.tv_service_from);

		// 店铺
		rl_store_info = (RelativeLayout) findViewById(R.id.rl_store_info);
		layou_store_call = (RelativeLayout) findViewById(R.id.layou_store_call);
		line_store_adress = (RelativeLayout) findViewById(R.id.line_store_adress);
		layou_store_adress = (RelativeLayout) findViewById(R.id.layou_store_adress);
		line_pingfen1 = (RelativeLayout) findViewById(R.id.line_pingfen1);
		tv_fuwudizhi = (TextView) findViewById(R.id.tv_fuwudizhi);
		tv_distance = (TextView) findViewById(R.id.tv_store_distance);
		tv_suoshudianpu = (TextView) findViewById(R.id.tv_suoshudianpu);

		setListener();

		// 加载数据失败页面
		rl_loadData_error = (RelativeLayout) findViewById(R.id.rl_loadData_error);
		rl_loadData_error.setOnClickListener(this);
		// 加载数据前的白界面
		rl_loadData_empty = (RelativeLayout) findViewById(R.id.rl_loadData_empty);
		rl_commit = (RelativeLayout) findViewById(R.id.rl_commit);
	}

	private void setListener() {
		// LinearLayout ll_back = (LinearLayout) findViewById(R.id.ll_back1);
		ll_back.setOnClickListener(this);
		rl_review.setOnClickListener(this);
		rl_technician.setOnClickListener(this);
		bt_appoint_commit.setOnClickListener(this);

		iv_collect.setOnClickListener(this);
		iv_share.setOnClickListener(this);

		layou_store_call.setOnClickListener(this);
		layou_store_adress.setOnClickListener(this);
		scrollView1.setScrollViewListener(new com.huatuo.custom_widget.ScrollViewListener() {

			@SuppressLint("NewApi")
			@Override
			public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
				rl_pager.setY(oldy / 2);
				if (0 != y && 0 != oldy) {
					a = (float) 2 * y / (float) viewPager.getHeight();
					iv_top.setAlpha(a);
					tv_name.setAlpha(a);
				}
				ll_back.setBackgroundColor(Color.argb(0, 0, 0, 0));
				ll_share.setBackgroundColor(Color.argb(0, 0, 0, 0));
				ll_collect.setBackgroundColor(Color.argb(0, 0, 0, 0));
			}
		});
		scrollView1.setOnBorderListener(new com.huatuo.custom_widget.OnBorderListener() {

			@SuppressLint("NewApi")
			@Override
			public void onTop() {
				// TODO Auto-generated method stub
				rl_pager.setY(0);
				ll_back.setBackgroundResource(R.drawable.icon_bg);
				ll_share.setBackgroundResource(R.drawable.icon_bg);
				ll_collect.setBackgroundResource(R.drawable.icon_bg);
			}

			@Override
			public void onBottom() {
				// TODO Auto-generated method stub

			}
		});

	}

	/**
	 * 显示 隐藏相应view
	 */
	private void showOrHideView() {

		tv_service_from.setVisibility(View.GONE);
		// 店铺
		layou_store_call.setVisibility(View.GONE);
		layou_store_adress.setVisibility(View.GONE);
		line_store_adress.setVisibility(View.GONE);
		line_pingfen1.setVisibility(View.GONE);

		// 技师
		rl_technician.setVisibility(View.GONE);
		view_tech.setVisibility(View.GONE);
		if (("0").equals(isSelfOwned)) {
			tv_service_from.setVisibility(View.GONE);
			layou_store_call.setVisibility(View.VISIBLE);
			layou_store_adress.setVisibility(View.VISIBLE);
			line_store_adress.setVisibility(View.VISIBLE);
			line_pingfen1.setVisibility(View.VISIBLE);

		} else if (("1").equals(isSelfOwned)) {
			tv_service_from.setVisibility(View.VISIBLE);
			tv_service_from.setText("【自营】华佗驾到");
			tv_service_from.setVisibility(View.VISIBLE);
		}

		// 是否从技师过来
		if (!TextUtils.isEmpty(workerID)) {
			rl_technician.setVisibility(View.VISIBLE);
			view_tech.setVisibility(View.VISIBLE);
		} else {
			rl_technician.setVisibility(View.GONE);
			view_tech.setVisibility(View.GONE);
		}
	}

	/**
	 * 获取项目详情
	 * 
	 * @param workerID
	 * @param servID
	 */
	private void getServiceDetail(String servID, String workerID) {
		showCustomCircleProgressDialog(null, getString(R.string.common_toast_net_prompt_submit));
		getServiceDetail = new GetServiceDetail(mContext, sysServiceDetail_handler, servID, workerID);
		Thread thread = new Thread(getServiceDetail);
		thread.start();
	}

	/**
	 * 处理项目详情
	 * 
	 * @author Android开发工程师
	 * 
	 */
	class SysServiceDetail_handler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			rl_loadData_empty.setVisibility(View.GONE);
			rl_loadData_error.setVisibility(View.GONE);
			rl_commit.setVisibility(View.VISIBLE);
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				serviceDetailObj = getServiceDetail.getOutJson();
				if (null != serviceDetailObj) {
					handleData(serviceDetailObj);
				}

				break;
			case MsgId.DOWN_DATA_F:
				closeCustomCircleProgressDialog();
				rl_loadData_error.setVisibility(View.VISIBLE);
				rl_commit.setVisibility(View.GONE);
				break;
			case MsgId.NET_NOT_CONNECT:
				rl_loadData_error.setVisibility(View.VISIBLE);
				rl_commit.setVisibility(View.GONE);
				closeCustomCircleProgressDialog();
				break;
			default:
				break;
			}
		}
	}

	private void handleData(JSONObject jsonObject) {
		servID = jsonObject.optString("ID", "");// 服务ID
		serviceName = jsonObject.optString("name", "");// 服务名称
		sitType = jsonObject.optString("sitType", "");// 服务名0：坐，1：卧
		serviceTimeLong = jsonObject.optString("duration", "");// 服务时长
		servNro = jsonObject.optString("introduction", "");// 服务介绍
		evalNum = jsonObject.optString("commentCount", "");// 评价数量
		isSelfOwned = jsonObject.optString("isSelfOwned", "");// 0 门店 1 华佗自营
		isLevel = jsonObject.optString("isLevel", "");// isLevel 是否开启等级是否开启等级(0
														// 未启用， 1启用)
		isGoHome = jsonObject.optString("isGoHome", "");// 0 到店 1 上门
		storeID = jsonObject.optString("storeID", "");// 店铺ID
		storeName = jsonObject.optString("storeName", "");// 属店铺名称
		storeAddress = jsonObject.optString("storeAddress", "");// 店铺地址
		distance = jsonObject.optString("distance", "");// 距离
		storeTel = jsonObject.optString("storeTel", "");// 店铺电话
		applyPosition = jsonObject.optString("forBodyPart", "");// 针对部位
		applySymptoms = jsonObject.optString("forSymptom", "");// 适用症状
		isFavorite = jsonObject.optString("isFavorite", "");// 是否收藏
		latitude = jsonObject.optString("latitude", "");// 是否收藏
		longitude = jsonObject.optString("longitude", "");// 是否收藏

		// 获取技师信息
		// workerID 技师ID
		// workerName 技师姓名
		// orderCount 技师服务单数
		// workerGradeID 技师等级
		// workerGradeName 技师等级名称
		// commentCount 评论数
		// score 技师分数
		// workerIcon 技师logo
		techName = jsonObject.optString("workerName", "");
		techOrderCount = jsonObject.optString("orderCount", "");
		techGradeID = jsonObject.optString("workerGradeID", "");
		techGradeName = jsonObject.optString("workerGradeName", "");
		techCommentCount = jsonObject.optString("workerCommentCount", "");// 技师评价数量
		techScore = jsonObject.optString("score", "");
		techIcon = jsonObject.optString("workerIcon", "");
		// 店铺信息
		tv_suoshudianpu.setText(storeName);
		tv_fuwudizhi.setText(storeAddress);
		tv_distance.setText(FormatDistanceUtil.formatDistance(distance));

		// 服务介绍
		try {
			introList = JsonUtil.jsonArray2List(jsonObject.getJSONArray("introduction"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (!CommonUtil.emptyListToString3(introList)) {
			projectDetailInfoAdapter.clear();
			projectDetailInfoAdapter.add(introList);
		}

		// 服务须知
		try {
			reminder = JsonUtil.jsonArray2List(jsonObject.getJSONArray("reminder"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (!CommonUtil.emptyListToString3(reminder)) {
			projectDetailReminderAdapter.clear();
			projectDetailReminderAdapter.add(reminder);
		}

		// 服务图片
		try {
			imgList = JsonUtil.jsonArray2List(jsonObject.getJSONArray("imageList"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 服务信息
		// 0：坐，1：卧
		if (!TextUtils.isEmpty(sitType)) {
			if (("1").equals(sitType.trim())) {
				tv_projectName.setText(serviceName + "（卧）");
			} else {
				tv_projectName.setText(serviceName + "（坐）");
			}

		} else {
			tv_name.setText(serviceName);
		}
		tv_name.setText(serviceName);
		tv_timeLong.setText(serviceTimeLong + "分钟");
		if (!CommonUtil.emptyListToString3(imgList)) {
			myViewPagerAdapter.clear();
			myViewPagerAdapter.add(imgList);
		}

		minServPrice = jsonObject.optString("minPrice", "");// 服务最低价格
		maxServPrice = jsonObject.optString("maxPrice", "");// 服务最高价格
		// 到店---处理服务类型---处理市场价格是否显示
		// if (("0").equals(isSelfOwned)) {
		tv_special.setVisibility(View.VISIBLE);
		tv_projectPrice_old.setVisibility(View.VISIBLE);//

		// 自营的 --不显示市场价格--取最小值
		// } else if (("1").equals(isSelfOwned)) {
		// tv_special.setVisibility(View.GONE);
		// tv_projectPrice_old.setVisibility(View.GONE);
		// }
		tv_projectPrice_old.setText("￥" + NumFormatUtil.centFormatYuanToString(maxServPrice));
		tv_projectPrice_old.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
		tv_projectPrice.setText(NumFormatUtil.centFormatYuanToString(minServPrice));

		// 未启用等级---处理是否显示 多少元 起
		if (("0").equals(isLevel)) {
			tv_projectPrice2.setVisibility(View.GONE);// 不显示多少元 起
		} else {
			if (TextUtils.isEmpty(workerID)) {
				tv_projectPrice2.setVisibility(View.VISIBLE);// 显示多少元 起
			} else {
				tv_projectPrice2.setVisibility(View.GONE);// 不显示多少元 起
			}
		}

		// 技师信息

		// 技师评分
		// 判断评分是否为0
		float score_fl = NumFormatUtil.stringToFloat(techScore);
		if (score_fl == 0f) {
			tv_starNum.setText("暂无评分");
			ratingBar1.setVisibility(View.GONE);
		} else {
			ratingBar1.setRating(score_fl);
			tv_starNum.setText(techScore + "分");
			ratingBar1.setVisibility(View.VISIBLE);
		}
		CommonUtil.log("技师评分：techScore:" + techScore);
		tv_techName.setText(techName);
		tv_orderNum.setText(techOrderCount);
		tv_techReviewNum.setText(techCommentCount);// 技师评论数
		ImageLoader.getInstance().displayImage(techIcon, iv_tech_icon,
				ImageLoader_DisplayImageOptions.getInstance().setDefaultImageSmallImg());// 技师图片icon
		// 项目评论数
		if (!TextUtils.isEmpty(evalNum) && !("0").equals(evalNum)) {
			tv_projectReviewNum.setText(evalNum + "评");
			tv_projectReviewNum.setTextColor(R.color.c5);
		} else {
			tv_projectReviewNum.setText("暂无评论");
			tv_projectReviewNum.setTextColor(R.color.c6);
		}

		if (!TextUtils.isEmpty(applyPosition)) {
			tv_position.setText(applyPosition);
		}

		if (!TextUtils.isEmpty(applySymptoms)) {
			tv_indications.setText(applySymptoms);
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

		showOrHideView();

		// state storeState workerState 3:生效，9：已下架
		storeState = jsonObject.optString("storeState", "-1");
		state = jsonObject.optString("state", "-1");
		workerState = jsonObject.optString("workerState", "-1");

	}

	/**
	 * 初始化ViewPager(广告位)
	 */
	private void initViewPager() {
		viewPager = (ViewPager) findViewById(R.id.first_viewPager);
		CommonUtil.initScreen(this);
		// 设置图片的为正方形
		LayoutParams layoutParams = viewPager.getLayoutParams();
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

		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		}

		public void onPageSelected(final int position) {
			// setCurrentDot(position);
			// DialogUtils.showToastMsg(mContext, position+"选中",
			// Toast.LENGTH_SHORT);
		}
	}

	/**
	 * 添加事件监听
	 */
	private void initListener() {
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			boolean isScrolled = false;

			@Override
			public void onPageScrollStateChanged(int status) {
				switch (status) {
				case 1:// 手势滑动
					isScrolled = false;
					break;
				case 2:// 界面切换
					isScrolled = true;
					break;
				case 0:// 滑动结束
						// 当前为最后一张，此时从右向左滑，则切换到第一张
					if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1 && !isScrolled) {
						viewPager.setCurrentItem(0);
					}
					// 当前为第一张，此时从左向右滑，则切换到最后一张
					else if (viewPager.getCurrentItem() == 0 && !isScrolled) {
						viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 1);
					}
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageSelected(int index) {

			}
		});
	}

	private static final int TIME = 2000;

	protected void initRunnable() {
		viewpagerRunnable = new Runnable() {

			@Override
			public void run() {
				int nowIndex = viewPager.getCurrentItem();
				int count = viewPager.getAdapter().getCount();
				// 如果下一张的索引大于最后一张，则切换到第一张
				if (nowIndex + 1 >= count) {
					viewPager.setCurrentItem(0);
				} else {
					viewPager.setCurrentItem(nowIndex + 1);
				}
				handler.postDelayed(viewpagerRunnable, TIME);
			}
		};
		handler.postDelayed(viewpagerRunnable, TIME);
	}

	/**
	 * 判断门店 技师 项目状态获取对应提示语
	 */
	private void appoint() {
		CommonUtil.log("项目详情：isSelfOwned：" + isSelfOwned);
		CommonUtil.log("项目详情：storeState：" + storeState);
		CommonUtil.log("项目详情：state：" + state);
		CommonUtil.log("项目详情：workerState：" + workerState);
		// 店铺的项目
		if (("0").equals(isSelfOwned)) {
			if (storeState.equals("9")) {//门店下线
				// 门店下线 --去往到店列表
				msg = getResources().getString(R.string.store_offLine);
				showDialog();
			} else if (state.equals("9")) {//项目下线
				msg = getResources().getString(R.string.service_offLine);
				showDialog();
			} else if (workerState.equals("9")) {// 技师下线
				msg = getResources().getString(R.string.tech_store_offLine);
				showDialog();
			} else {
				judgeState();
			}
			UmengEventUtil.store_storedetail_projectdetail_placeorder(mContext);
			// 自营的项目
		} else if (("1").equals(isSelfOwned)) {
			if (state.equals("9")) {// 项目下线
				msg = getResources().getString(R.string.service_offLine);
				showDialog();
			} else if (workerState.equals("9")) {// 技师下线
				msg = getResources().getString(R.string.tech_offLine);
				showDialog();
			} else {// 正常或者没有技师
				judgeState();
			}
			
			UmengEventUtil.door_tichniciandetail_projectdetail_placeorder(mContext);
		}

	}

	private void showDialog() {
		CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
		builder.setTitle("提示");
		builder.setMessage(msg);
		builder.setPositiveButton(getResources().getString(R.string.common_confirm),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						judgeState();
					}
				});

		builder.setNegativeButton(getResources().getString(R.string.common_cancel),
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();

	}

	/**
	 * 判断门店 技师 项目状态
	 */
	private void judgeState() {
		/**
		 * ==============================店铺的项目=======================
		 */
		if (("0").equals(isSelfOwned)) {
			if (storeState.equals("9")) {//门店下线
				// 门店下线 --去往到店列表
				CommonUtil.log("-----订单-------------门店下线 --去往到店列表-----------------");
				appointJumpToTargetView(Constants.APPOINT_VIEW_FIST_FRAGMENT);
			} else if (state.equals("9")) {//项目下线
				CommonUtil.log("-----订单-------------门店在线-项目下线- 不管技师状态 ---去往门店详情页面-----------------");
				// 门店在线-项目下线-技师在线 ---去往门店详情页面
				appointJumpToTargetView(Constants.APPOINT_VIEW_STORE_DEATAIL);

			} else if (!workerState.equals("3")) {////技师下线或者不带技师
				CommonUtil.log("-----订单-------------门店在线-项目在线-技师下线 （或无技师）---去往不带技师的项目预约界面-----------------");
				// 门店在线-项目在线-技师下线 ---去往不带技师的项目预约界面
				// 到店---处理服务类型
				if (("0").equals(isGoHome)) {
					appointJumpToTargetView(Constants.APPOINT_VIEW_STORE_NO_TECH);
				} else {

					appointJumpToTargetView(Constants.APPOINT_VIEW_VISIT_NO_TECH);
				}

			} else {
				CommonUtil.log("-----订单-------------门店在线-项目在线-技师在线 ---去往带技师的项目预约界面-----------------");

				// 到店---处理服务类型
				if (("0").equals(isGoHome)) {
					appointJumpToTargetView(Constants.APPOINT_VIEW_STORE_WITH_TECH);
				} else {

					appointJumpToTargetView(Constants.APPOINT_VIEW_VISIT_WITH_TECH);
				}
			}

			/**
			 * ==============================自营的项目=======================
			 */
		} else if (("1").equals(isSelfOwned)) {
			if (state.equals("9")) {//项目下线
				CommonUtil.log("-----订单-------------项目下线-技师在线 ---去往上门页面----------------");
				// 项目下线-技师在线 ---去往上门页面
				appointJumpToTargetView(Constants.APPOINT_VIEW_VISIT_FRAGMENT);

			} else if (!workerState.equals("3")) {//技师下线 (或无技师)
				CommonUtil.log("-----订单-------------项目在线-技师下线 (或无技师)---去往不带技师的项目预约界面----------------");
				// 项目在线-技师下线 ---去往不带技师的项目预约界面
				// 自营的
				appointJumpToTargetView(Constants.APPOINT_VIEW_VISIT_NO_TECH);
			} else {
				CommonUtil.log("-----订单-------------项目在线-技师在线 ---去往带技师的项目预约界面----------------");
				// 项目在线-技师下线 ---去往带技师的项目预约界面
				// 自营的
				appointJumpToTargetView(Constants.APPOINT_VIEW_VISIT_WITH_TECH);

			}
		}

	}

	/**
	 * 预约上门的服务类型2上门，3 自营的上门
	 */
	private void getAppointVisitServType() {
		// 预约的服务类型：1 到店(不用判断)， 2上门，3 自营的上门
		if (("0").equals(isSelfOwned)) {
			// 到店---处理服务类型
			appointServType = Constants.SERVTYPE_STORE_VISIT;
			// 自营的
		} else if (("1").equals(isSelfOwned)) {
			appointServType = Constants.SERVTYPE_VISIT;
		}
	}

	private void appointJumpToTargetView(int targetView) {
		switch (targetView) {
		case Constants.APPOINT_VIEW_VISIT_WITH_TECH:// 预约上门带技师
			if (null == serviceDetailObj) {
				break;
			}
			getAppointVisitServType();// 预约上门的服务类型2上门，3 自营的上门
			intent.setClass(mContext, Apponitment_OnVisit_FromTechActivity.class);
			bundle = new Bundle();
			bundle.putString(Constants.APPOINT_SERVICE, serviceDetailObj.toString());
			bundle.putInt("servType", appointServType);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		case Constants.APPOINT_VIEW_VISIT_NO_TECH:// 预约上门不带技师
			if (null == serviceDetailObj) {
				break;
			}
			getAppointVisitServType();// 预约上门的服务类型2上门，3 自营的上门
			intent.setClass(mContext, Apponitment_OnVisit_FromProjectActivity.class);
			bundle = new Bundle();
			bundle.putString(Constants.APPOINT_SERVICE, serviceDetailObj.toString());
			bundle.putInt("servType", appointServType);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		case Constants.APPOINT_VIEW_STORE_WITH_TECH:// 预约到店 带技师
			if (null == serviceDetailObj) {
				break;
			}
			intent.setClass(mContext, Apponitment_OnStore_FromTechActivity.class);
			bundle = new Bundle();
			bundle.putString(Constants.APPOINT_SERVICE, serviceDetailObj.toString());
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		case Constants.APPOINT_VIEW_STORE_NO_TECH:// 预约到店 不带技师
			if (null == serviceDetailObj) {
				break;
			}
			intent.setClass(mContext, Apponitment_OnStore_FromProjectActivity.class);
			bundle = new Bundle();
			bundle.putString(Constants.APPOINT_SERVICE, serviceDetailObj.toString());
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		case Constants.APPOINT_VIEW_STORE_DEATAIL:// 去往店铺详情
			intent.setClass(mContext, StoreDetail_Aty.class);
			bundle = new Bundle();
			bundle.putString("ID", storeID);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		case Constants.APPOINT_VIEW_FIST_FRAGMENT:// 去往到店列表
			intent.setClass(mContext, HomeActivity.class);
			bundle = new Bundle();
			bundle.putInt("tabIndex", 0);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		case Constants.APPOINT_VIEW_VISIT_FRAGMENT:// 去往上门列表
			intent.setClass(mContext, HomeActivity.class);
			bundle = new Bundle();
			bundle.putInt("tabIndex", 1);
			intent.putExtras(bundle);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	/**
	 * 收藏
	 * 
	 * @param skillWorkerID
	 */
	private void addCollect() {
		showCustomCircleProgressDialog(null, getString(R.string.common_toast_net_prompt_submit));

		// userID 用户ID
		// itemType 收藏内容类型
		// itemID 内容id
		HashMap<String, String> inJsonMap = new HashMap<String, String>();
		inJsonMap.put("userID", MyApplication.getUserID());
		inJsonMap.put("itemType", "1");
		inJsonMap.put("itemID", servID);
		addCollect = new AddCollect(mContext, handlerAddCollect, inJsonMap);
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
				DialogUtils.showToastMsg(mContext, getString(R.string.common_toast_net_down_data_fail),
						Toast.LENGTH_SHORT);
				break;
			case MsgId.NET_NOT_CONNECT:
				setCustomDialog(getString(R.string.common_toast_net_not_connect), true);
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
		showCustomCircleProgressDialog(null, getString(R.string.common_toast_net_prompt_submit));
		//
		// userID 用户ID ANS 64 M 用户ID
		// itemID 项目Id ANS 11 M 项目Id
		// itemType 类型 ANS 11 M 0 门店 1 服务 2 技师 3 发现
		HashMap<String, String> inJsonMap = new HashMap<String, String>();
		inJsonMap.put("userID", MyApplication.getUserID());
		inJsonMap.put("itemID", servID);
		inJsonMap.put("itemType", "1");// itemType 类型 ANS 11 M 0 门店 1 服务 2 技师 3
										// 发现

		cancelCollect = new CancelCollect(mContext, handlerCancelCollect, inJsonMap);
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
				DialogUtils.showToastMsg(mContext, getString(R.string.common_toast_net_down_data_fail),
						Toast.LENGTH_SHORT);
				break;
			case MsgId.NET_NOT_CONNECT:
				setCustomDialog(getString(R.string.common_toast_net_not_connect), true);
				break;
			default:
				break;
			}
		}
	}

	private void collectHandle() {
		if (!isCollect) {
			// 收藏
			addCollect();
		} else {
			// 取消收藏
			cancelCollect();
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
		Custom_Toast_Collect.getInstance().handleCollectIcon(this, type, iv_collect);
	}

	/**
	 * 打电话
	 */
	private void call() {
		CallUtil.showCallDialog(mContext, storeTel);
	}

	/**
	 * 获取服务介绍
	 * 
	 * @return
	 */
	private String getServiceInfo() {
		StringBuffer sb = new StringBuffer();
		if (!CommonUtil.emptyListToString3(introList)) {
			for (int i = 0; i < introList.size(); i++) {
				String tag = introList.get(i).optString("tag", "");
				String content = introList.get(i).optString("content", "");

				if (!TextUtils.isEmpty(tag)) {
					sb.append(tag + "：" + content);
					if (i != introList.size() - 1) {
						sb.append("\n");
					}
				}
			}

		}
		return sb.toString();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layou_store_call:// 店铺电话
			UmengEventUtil.store_storedetail_projectdetail_storedetail(mContext);
			call();
			break;
		case R.id.layou_store_adress:// 导航
		 UmengEventUtil.store_storedetail_projectdetail_adress(mContext);
			GuideToStoreUtil.getInstance().jumpToGuideActivity(this, latitude, longitude, storeAddress, storeName);
			break;
		case R.id.bt_appoint_commit:// 预约
			if (!MyApplication.getLoginFlag()) {
				// showCustomCircleProgressDialog("请登录", "你尚未登录,请登录!");
				DialogUtils.showToastMsg(mContext, "你尚未登录,请登录!", Toast.LENGTH_SHORT);
				intent.setClass(mContext, LoginActivity.class);
				startActivity(intent);
				return;
			}
			
			appoint();
			break;
		case R.id.rl_review:// 查看评价列表
			if (TextUtils.isEmpty(evalNum) || ("0").equals(evalNum)) {
				break;
			}
			
			if (("0").equals(isSelfOwned)) {
				//门店的项目的查看评价列表
				UmengEventUtil.store_storedetail_projectdetail_comment(mContext);
			}else {
				UmengEventUtil.door_tichniciandetail_projectdetail_comment(mContext);
			}
			
			
			intent.setClass(mContext, AppraiseList_Avtivity.class);
			intent.putExtra("ID", servID);
			intent.putExtra("type", Constants.APPRAISE_TYPE_PROJECT);
			startActivity(intent);
			break;
		case R.id.rl_technician:// 查看技师详情
			UmengEventUtil.door_tichniciandetail_projectdetail_tichniciandetail(mContext);
			intent.setClass(mContext, TechnicianDetail.class);
			intent.putExtra("ID", workerID);
			startActivity(intent);
			break;
		case R.id.iv_collect:// 收藏/取消收藏
			if (!MyApplication.getLoginFlag()) {
				// showCustomCircleProgressDialog("请登录", "你尚未登录,请登录!");
				DialogUtils.showToastMsg(mContext, "你尚未登录,请登录!", Toast.LENGTH_SHORT);
				intent.setClass(mContext, LoginActivity.class);
				startActivity(intent);
				return;
			}
			if (("0").equals(isSelfOwned)) {
				//门店的项目的查看评价列表
				UmengEventUtil.store_storedetail_projectdetail_collect(mContext);
			}else {
				UmengEventUtil.door_tichniciandetail_projectdetail_collect(mContext);
			}
			
			collectHandle();
			break;
		case R.id.iv_share:// 分享
			if (("0").equals(isSelfOwned)) {
				//门店的项目的查看评价列表
				UmengEventUtil.store_storedetail_projectdetail_share(mContext);
			}else {
				UmengEventUtil.door_tichniciandetail_projectdetail_share(mContext);
			}
			String imageUrl = "";
			if (!CommonUtil.emptyListToString3(imgList)) {
				imageUrl = imgList.get(0).optString("url", "");
			}
			String info = getServiceInfo();// 获取服务介绍
			UmengShare.getInstance().initShareParams(ProjectDetailActivity.this, imageUrl, serviceName, info, servID,
					Constants.SHARE_PROJECT);
			
			break;
		case R.id.rl_loadData_error:// 重新加载数据
			getServiceDetail(servID, workerID);
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
		boolean has_open_app = CommonUtil.getBooleanOfSharedPreferences(this, "HAS_OPEN_APP", false);
		CommonUtil.logE("是否已经打开app：has_open_app：" + has_open_app);
		// 点击通知后或者首图--app未打开状态 -返回时回到主页面home
		if (push && !has_open_app) {
			CommonUtil.saveBooleanOfSharedPreferences(this, "HAS_OPEN_APP", true);
			JumpTargetActivityUtil.getInstance().jumpToHomeActivity(this, 0);
		} else {
			finish();
		}
	}

	@Override
	public void onBackPressed() {
		handleOnBackPress();
	}

}
