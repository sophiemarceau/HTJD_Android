package com.huatuo.activity.technician;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huatuo.R;
import com.huatuo.activity.login.LoginActivity;
import com.huatuo.activity.map.GuideToStoreUtil;
import com.huatuo.activity.store.StoreDetail_Aty;
import com.huatuo.adapter.Service_girview_Adapter_tech;
import com.huatuo.base.BaseActivity;
import com.huatuo.base.MyApplication;
import com.huatuo.bean.ShareObj;
import com.huatuo.custom_widget.CustomGridView;
import com.huatuo.custom_widget.CustomShareBoard;
import com.huatuo.custom_widget.CustomShareBoard.OnShareListener;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.thread.AddCollect;
import com.huatuo.net.thread.CancelCollect;
import com.huatuo.net.thread.GetSkillWorkerInfo;
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
import com.huatuo.util.Toast_Util;
import com.huatuo.util.UmengEventUtil;
import com.huatuo.util.UmengShare;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

public class TechnicianDetail extends BaseActivity {
	public Context mContext;
	public static final int FRAGMENT_ONE = 0;
	public static final int FRAGMENT_TWO = 1;
	private RelativeLayout layout_store_xialakuang;
	// 自营
	private TextView tv_tech_from, tv_shangquan;
	private RelativeLayout layout_shangquan;
	private View line_shangquan;

	// 店铺
	private RelativeLayout line_store_adress;
	private RelativeLayout rl_line_pingfen1;
	private RelativeLayout layou_store_adress, layou_store_call;
	private RelativeLayout rl_loadData_error, rl_loadData_empty;
	private TextView tv_headTiele, tv_geren1, tv_technicianName,
			tv_orderNumber, tv_distance, tv_pingfen, tv_pinglunshu,
			tv_suoshudianpu, tv_fuwudizhi;
	private RatingBar ratingBar;
	private RelativeLayout rl_pingfen1;
	private TextView tv_geren2;
	private ImageView iv_icon, iv_share, iv_collect;
	private ImageView iv_xialakuang;
	private GetSkillWorkerInfo getSkillWorkerInfoStore;
	private AddCollect addCollect;// 收藏
	private CancelCollect cancelCollect;// 取消收藏
	private boolean isCollect = false;
	public Handler mHandler, handlerAddCollect, handlerCancelCollect;
	private String juli, orderCount, introduction, score, commentCount,
			storeName, storeAddress, storeTel, skillWorkerIcon, isFavorite,
			latitude, longitude;
	private int isExpandOfTechInfo = 0;// 简介是否展开 0：处于未展开；1：展开
	private String skillWorkerID = "";
	private String isSelfOwned = "";// 0 门店 1 华佗自营
	private JSONObject technicianJsonObject;
	private String skillWorkerName, gradeName, serviceArea;
	private Service_girview_Adapter_tech service_girview_Adapter;
	private CustomGridView gv_jishifuwu;
	private Intent intent = new Intent();

	private boolean push = false;// 是否是推送
	private UMShareAPI mShareAPI = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_technician_detail3);
		mContext = this;
		// 友盟分享
		mShareAPI = UMShareAPI.get(this);
		initHandler();
		getExtras();
		initWidget();
		bindListener1();
		getSkillWorkerInfo(skillWorkerID);
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
		getExtras();
	}

	private void getExtras() {
		Bundle bundle = getIntent().getExtras();
		if (null != bundle) {
			skillWorkerID = bundle.getString("ID");
			push = bundle.getBoolean("push");
		}
	}

	private void initHandler() {
		mHandler = new MyHandler();
		handlerAddCollect = new HandlerAddCollect();
		handlerCancelCollect = new HandlerCancelCollect();
	}

	/**
	 * 初始化webview
	 */
	// private void initWebview() {
	// WebSettings webSettings1 = tv_geren2.getSettings();
	// webSettings1.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
	// webSettings1.setDefaultFontSize(14);
	// }

	/** 在这里获取到每个需要用到的控件的实例 */
	/**
	 * 
	 */
	private void initWidget() {
		bindListener();
		iv_share = (ImageView) findViewById(R.id.iv_share);// 分享
		iv_collect = (ImageView) findViewById(R.id.iv_collect);// 收藏

		// 自营
		tv_tech_from = (TextView) findViewById(R.id.tv_tech_from);
		layout_shangquan = (RelativeLayout) findViewById(R.id.layout_shangquan);// 技师的用户评价
		line_shangquan = (View) findViewById(R.id.line_shangquan);
		tv_shangquan = (TextView) findViewById(R.id.tv_shangquan);
		// 店铺
		layou_store_call = (RelativeLayout) findViewById(R.id.layou_store_call);
		line_store_adress = (RelativeLayout) findViewById(R.id.line_store_adress);
		layou_store_adress = (RelativeLayout) findViewById(R.id.layou_store_adress);
		rl_line_pingfen1 = (RelativeLayout) findViewById(R.id.rl_line_pingfen1);
		tv_fuwudizhi = (TextView) findViewById(R.id.tv_fuwudizhi);
		tv_distance = (TextView) findViewById(R.id.tv_store_distance);
		tv_suoshudianpu = (TextView) findViewById(R.id.tv_suoshudianpu);

		tv_headTiele = (TextView) findViewById(R.id.tv_headTiele);
		tv_geren1 = (TextView) findViewById(R.id.tv_geren1);
		tv_geren2 = (TextView) findViewById(R.id.tv_geren2);
		// initWebview();

		ratingBar = (RatingBar) findViewById(R.id.ratingBar1);
		tv_pingfen = (TextView) findViewById(R.id.tv_pingfen);
		tv_pinglunshu = (TextView) findViewById(R.id.tv_tech_pinglunshu);
		tv_technicianName = (TextView) findViewById(R.id.tv_technicianName);
		tv_orderNumber = (TextView) findViewById(R.id.tv_orderNumber);

		iv_icon = (ImageView) findViewById(R.id.iv_icon);

		iv_xialakuang = (ImageView) findViewById(R.id.iv_xialakuang);
		layout_store_xialakuang = (RelativeLayout) findViewById(R.id.layout_store_xialakuang);// 开关简介更多
		layou_store_call = (RelativeLayout) findViewById(R.id.layou_store_call);// 所属店铺--点击可以打电话
		rl_pingfen1 = (RelativeLayout) findViewById(R.id.rl_pingfen1);// 技师的用户评价
		// 简介
		tv_geren1.setVisibility(View.VISIBLE);
		tv_geren2.setVisibility(View.GONE);
		// 服务列表
		gv_jishifuwu = (CustomGridView) findViewById(R.id.gv_jishifuwu);
		// 初始化adapter
		service_girview_Adapter = new Service_girview_Adapter_tech(this);
		gv_jishifuwu.setAdapter(service_girview_Adapter);

		// 加载数据失败页面
		rl_loadData_error = (RelativeLayout) findViewById(R.id.rl_loadData_error);
		rl_loadData_error.setOnClickListener(new MyOnClickListener());
		// 加载数据前的白界面
		rl_loadData_empty = (RelativeLayout) findViewById(R.id.rl_loadData_empty);

	}

	private void showOrHideByTechType() {
		CommonUtil.log("=----isSelfOwned:" + isSelfOwned);
		int type = -1;// 0 门店 1 华佗自营
		if (!TextUtils.isEmpty(isSelfOwned)) {
			type = Integer.parseInt(isSelfOwned.trim());
		}

		// 自营
		tv_tech_from.setVisibility(View.GONE);
		layout_shangquan.setVisibility(View.GONE);
		line_shangquan.setVisibility(View.GONE);

		// 店铺
		layou_store_call.setVisibility(View.GONE);
		layou_store_adress.setVisibility(View.GONE);
		line_store_adress.setVisibility(View.GONE);
		rl_line_pingfen1.setVisibility(View.GONE);
		switch (type) {
		case 0:// 来源店铺
			layou_store_call.setVisibility(View.VISIBLE);
			layou_store_adress.setVisibility(View.VISIBLE);
			line_store_adress.setVisibility(View.VISIBLE);
			rl_line_pingfen1.setVisibility(View.VISIBLE);
			break;
		case 1:// 来源自营
			tv_tech_from.setVisibility(View.VISIBLE);
			layout_shangquan.setVisibility(View.VISIBLE);
			line_shangquan.setVisibility(View.VISIBLE);
			break;
		}
	}

	private void handleData() {

		// ID 技师ID ANS 64 M 技师ID
		// name 技师姓名 ANS 64 M 技师姓名
		// orderCount 技师服务单数 N 11 M 技师服务单数
		// gradeID 技师等级 ANS 64 M 技师等级ID
		// gradeName 技师等级名称 ANS 64 M 技师等级名称
		// introduction 技师介绍 ANS 64 M 技师介绍
		// commentCount 评论数 N 11 M 评论数
		// score 技师分数 ANS 11 M 技师分数，满分5分
		// storeID 技师所属店铺ID ANS 64 C 技师所属店铺ID
		// storeName 技师所属店铺名称 ANS 64 C 技师所属店铺名称
		// storeAddress 店铺地址 ANS 64 C 店铺地址
		// distance 距离 ANS 64 C 距离，单位米
		// storeTel 店铺电话 ANS 64 C 店铺电话
		// icon 技师logo ANS 64 M 技师logo
		// isFavorite 是否收藏 ANS 64 O 类型 0：否，1：是，
		// serviceList 技师服务列表 JsonArray M 技师服务列表
		// isSelfOwned 技师归属 ANS 4 M 0 门店 1 华佗自营

		skillWorkerIcon = technicianJsonObject.optString("icon", "");
		juli = technicianJsonObject.optString("distance", "");
		introduction = technicianJsonObject.optString("introduction", "");
		score = technicianJsonObject.optString("score", "0");
		commentCount = technicianJsonObject.optString("commentCount", "0");
		orderCount = technicianJsonObject.optString("orderCount", "0");
		skillWorkerName = technicianJsonObject.optString("name", "");
		gradeName = technicianJsonObject.optString("gradeName", "");
		serviceArea = technicianJsonObject.optString("serviceArea", "");
		storeName = technicianJsonObject.optString("storeName", "");
		storeAddress = technicianJsonObject.optString("storeAddress", "");
		storeTel = technicianJsonObject.optString("storeTel", "");
		isFavorite = technicianJsonObject.optString("isFavorite", "");
		isSelfOwned = technicianJsonObject.optString("isSelfOwned", "");
		latitude = technicianJsonObject.optString("latitude", "");// 是否收藏
		longitude = technicianJsonObject.optString("longitude", "");// 是否收藏
		if (!TextUtils.isEmpty(skillWorkerName)) {
			skillWorkerName = skillWorkerName.trim();
		}
		setDataToView();
		showOrHideByTechType();

	}

	private void setDataToView() {

		ImageLoader.getInstance().displayImage(
				skillWorkerIcon,
				iv_icon,
				ImageLoader_DisplayImageOptions.getInstance()
						.setDefaultImageTechImg());
		tv_headTiele.setText(skillWorkerName);
		tv_technicianName.setText(skillWorkerName);
		tv_shangquan.setText(serviceArea);
		tv_distance.setText(FormatDistanceUtil.formatDistance(juli));
		tv_orderNumber.setText("已成交" + orderCount + "单");
		tv_geren1.setText(introduction);
		// tv_geren2.setText(jianjie);
		// tv_geren2.loadDataWithBaseURL(null, introduction, "text/html",
		// "utf-8", null);
		tv_geren2.setText(introduction);

		// 判断评分是否为0
		float score_fl = NumFormatUtil.stringToFloat(score);
		if (score_fl == 0f) {
			tv_pingfen.setText("暂无评分");
			ratingBar.setVisibility(View.GONE);
		} else {
			tv_pingfen.setText(score + "分");
			ratingBar.setVisibility(View.VISIBLE);
		}
		CommonUtil.log("=======技师详情============score_fl:" + score_fl + "");
		ratingBar.setRating(score_fl);
		tv_suoshudianpu.setText(storeName);
		tv_fuwudizhi.setText(storeAddress);
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
		if (!TextUtils.isEmpty(commentCount) && !("0").equals(commentCount)) {
			tv_pinglunshu.setText("" + commentCount + "评");
			tv_pinglunshu.setTextColor(R.color.c5);
			// imageView_next_appraise.setVisibility(View.VISIBLE);
		} else {
			tv_pinglunshu.setText("暂无评论");
			tv_pinglunshu.setTextColor(R.color.c6);
			// imageView_next_appraise.setVisibility(View.INVISIBLE);
		}
		// 自营还是店铺
		if (("0").equals(isSelfOwned)) {
			tv_tech_from.setText(storeName);
		} else if (("1").equals(isSelfOwned)) {
			tv_tech_from.setText("【自营】华佗驾到");

		}

		// 服务列表
		JSONArray array = technicianJsonObject.optJSONArray("serviceList");
		service_girview_Adapter.add(JsonUtil.jsonArray2List(array),
				skillWorkerID);
	}

	private void bindListener1() {
		MyOnClickListener myOnClickListener = new MyOnClickListener();
		layout_store_xialakuang.setOnClickListener(myOnClickListener);
		rl_pingfen1.setOnClickListener(myOnClickListener);
		iv_share.setOnClickListener(myOnClickListener);
		iv_collect.setOnClickListener(myOnClickListener);
		layou_store_call.setOnClickListener(myOnClickListener);
		layou_store_adress.setOnClickListener(myOnClickListener);

		LinearLayout ll_back = (LinearLayout) findViewById(R.id.ll_back1);
		ll_back.setOnClickListener(myOnClickListener);
	}

	/**
	 * 打电话
	 */
	private void call() {
		CallUtil.showCallDialog(mContext, storeTel);
	}

	/**
	 * 获取技师详情
	 * 
	 * @param skillWorkerID
	 */
	private void getSkillWorkerInfo(String skillWorkerID) {
		showCustomCircleProgressDialog(null,
				getString(R.string.common_toast_net_prompt_submit));
		getSkillWorkerInfoStore = new GetSkillWorkerInfo(mContext, mHandler,
				skillWorkerID);
		Thread thread = new Thread(getSkillWorkerInfoStore);
		thread.start();
	}

	class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {

			rl_loadData_empty.setVisibility(View.GONE);
			rl_loadData_error.setVisibility(View.GONE);
			iv_collect.setClickable(true);
			iv_share.setClickable(true);

			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				technicianJsonObject = getSkillWorkerInfoStore.getOutJson();
				if (null != technicianJsonObject) {
					handleData();// 处理返回的数据
				}
				break;
			case MsgId.DOWN_DATA_F:
				closeCustomCircleProgressDialog();
				String MSG = getSkillWorkerInfoStore.getOutMsg();
				int code = getSkillWorkerInfoStore.getOutCode();
				CommonUtil.log("获取技师详情返回的错误code：" + code);

				if (code < MsgId.HTTP_APPOINTMENT_ERRORCODE_MIN
						|| code > MsgId.HTTP_APPOINTMENT_ERRORCODE_MAX) {
					Toast_Util.showToast(mContext, MSG);
				} else {
					DialogUtils.showToastMsg(
							mContext,
							mContext.getResources().getString(
									R.string.common_toast_net_down_data_fail),
							Toast.LENGTH_SHORT);
				}

				iv_collect.setClickable(false);
				iv_share.setClickable(false);
				rl_loadData_error.setVisibility(View.VISIBLE);
				break;
			case MsgId.NET_NOT_CONNECT:
				closeCustomCircleProgressDialog();
				iv_collect.setClickable(false);
				iv_share.setClickable(false);
				rl_loadData_error.setVisibility(View.VISIBLE);
				// setCustomDialog(
				// getString(R.string.common_toast_net_not_connect), true);

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
	private void addCollect() {
		showCustomCircleProgressDialog(null,
				getString(R.string.common_toast_net_prompt_submit));

		// userID 用户ID
		// itemType 收藏内容类型
		// itemID 内容id
		HashMap<String, String> inJsonMap = new HashMap<String, String>();
		inJsonMap.put("userID", MyApplication.getUserID());
		inJsonMap.put("itemType", "2");
		inJsonMap.put("itemID", skillWorkerID);
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
		inJsonMap.put("itemID", skillWorkerID);
		inJsonMap.put("itemType", "2");// itemType 类型 ANS 11 M 0 门店 1 服务 2 技师 3
										// 发现

		cancelCollect = new CancelCollect(mContext, handlerCancelCollect,
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
		Custom_Toast_Collect.getInstance().handleCollect(TechnicianDetail.this,
				type, false);
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

	private class MyOnClickListener implements OnClickListener {
		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			switch (view.getId()) {
			case R.id.rl_pingfen1:// 查看用户评论
				UmengEventUtil.door_tichniciandetail_comment(mContext);
				if (TextUtils.isEmpty(commentCount)
						|| ("0").equals(commentCount)) {
					break;
				}
				intent.setClass(mContext, AppraiseList_Avtivity.class);
				intent.putExtra("ID", skillWorkerID);
				intent.putExtra("type", Constants.APPRAISE_TYPE_TECH);
				startActivity(intent);

				break;
			case R.id.layout_store_xialakuang:// 开关简介更多
				UmengEventUtil.door_tichniciandetail_more(mContext);
				
				if (isExpandOfTechInfo == 0) {
					tv_geren1.setVisibility(View.GONE);
					tv_geren2.setVisibility(View.VISIBLE);
					iv_xialakuang.setBackgroundResource(R.drawable.icon_up);
					isExpandOfTechInfo = 1;
				} else if (isExpandOfTechInfo == 1) {
					tv_geren2.setVisibility(View.GONE);
					tv_geren1.setVisibility(View.VISIBLE);
					iv_xialakuang.setBackgroundResource(R.drawable.icon_down);
					isExpandOfTechInfo = 0;
				}
				
				break;
			case R.id.layou_store_call:// 打电话
				call();
				break;
			case R.id.layou_store_adress:// 导航

				GuideToStoreUtil.getInstance().jumpToGuideActivity(
						TechnicianDetail.this, latitude, longitude,
						storeAddress, storeName);
				break;
			case R.id.iv_share:// 分享
				UmengEventUtil.door_tichniciandetail_share(mContext);
				share();
				break;
			case R.id.iv_collect:// 收藏/取消收藏
				if (!MyApplication.getLoginFlag()) {
					// showCustomCircleProgressDialog("请登录", "你尚未登录,请登录!");
					DialogUtils.showToastMsg(mContext, "你尚未登录,请登录!",
							Toast.LENGTH_SHORT);
					intent.setClass(mContext, LoginActivity.class);
					startActivity(intent);
					return;
				}
				UmengEventUtil.door_tichniciandetail_collect(mContext);
				collectHandle();
				break;
			case R.id.rl_loadData_error:// 重新加载数据
				getSkillWorkerInfo(skillWorkerID);
				break;
			case R.id.ll_back1:// 处理返回键
				handleOnBackPress();
				break;
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	/**
	 * 处理返回键
	 */
	private void handleOnBackPress() {
		boolean has_open_app = CommonUtil.getBooleanOfSharedPreferences(this,
				"HAS_OPEN_APP", false);
		CommonUtil.logE("是否已经打开app：has_open_app：" + has_open_app);
		CommonUtil.logE("push：" + push);
		// 点击通知后或者首图--app未打开状态 -返回时回到主页面home
		if (push && !has_open_app) {
			CommonUtil.saveBooleanOfSharedPreferences(this, "HAS_OPEN_APP",
					true);
			JumpTargetActivityUtil.getInstance().jumpToHomeActivity(this, 0);
		} else {
			finish();
		}
	}

	@Override
	public void onBackPressed() {
		handleOnBackPress();
	}
	private void share() {
		if (!mShareAPI.isInstall(this, SHARE_MEDIA.WEIXIN)) {
			Toast_Util.showToastOnlyOne(mContext, "您还未安装微信客户端,请安装后再分享",
					Gravity.BOTTOM, false);
			return;
		}

		UmengShare.getInstance().initShareParams(this,
				skillWorkerIcon, skillWorkerName + "（" + gradeName + "）",
				introduction, skillWorkerID, Constants.SHARE_TECH);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
	}
}
