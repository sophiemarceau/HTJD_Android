package com.huatuo.activity.guide;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatuo.R;
import com.huatuo.activity.home.HomeActivity;
import com.huatuo.base.BaseActivity;
import com.huatuo.base.MyApplication;
import com.huatuo.citylist.Content;
import com.huatuo.citylist.GetCityListUtil;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.thread.GetCityList;
import com.huatuo.net.thread.GetFirstFigure_thread;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Constants;
import com.huatuo.util.CustomDialogProgressHandler;
import com.huatuo.util.JumpTargetActivityByScanOrFirstImg;
import com.huatuo.util.LocationUtil_GD;
import com.huatuo.util.Toast_Util;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

public class StartActivity extends BaseActivity implements OnClickListener {
	private Context context = this;
	public static boolean isFirstIn;
	private Intent intent;
	/**
	 * 定位刷新广播
	 */
	private IntentFilter dynamic_filter_search = null;
	/**
	 * 广播接收器
	 */
	private BroadcastReceiver dynamicReceiver;
	private GetCityList getCityList;
	private ArrayList<JSONObject> cityList;
	private String cityListStr = null;
	private Handler mHandler_getCityList, mHandler_firstFigure;
	private GetFirstFigure_thread getFirstFigure_thread;
	private String firstFigurePath = "";// 图片地址 ANS 256 O 欢迎页图片地址
	private String firstFigureUrl = "";// 跳转地址 ANS 256 O 欢迎页跳转地址
	// 默认城市
	private String df_city = "";
	private String df_addr = "";
	private String df_cityCode = "";
	private String df_openStatus = "";
	private String df_lng = "";
	private String df_lat = "";

	// 定位城市
	private String localtion_city = "";// 定位的城市
	private String localtion_lat = "";// 定位的城市
	private String localtion_lng = "";// 定位的城市
	private String localtion_address = "";// 定位的详细地址
	private String localtion_city_openStatus = "";// 定位的城市开通状态
	private String localtion_cityCode = "";// 定位的城市code
	private int timeCount = 0;// 计算定位时长
	private String TAG = "StartActivity";

	private JSONObject firstFigureObj;
	private boolean isOnResume = false;
	private Timer mTimer = null;
	private final int BASEMSGID = 99999;
	private int timeLong = 3;// 展示广告最大时长限制为3秒
	private Handler mHandler_timer;
	private NotificationTimeTask notificationTimeTask;

	private RelativeLayout rl_jump;
	// private ImageView iv_start;
	private ImageView iv_change;
	private TextView tv_time;

	private Animation showAnim;
	private Animation hideAnim;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		MyApplication.getInstance().addActivity(this);
		context = this;

		initView();
		// 发送策略设定了用户产生的数据发送回友盟服务器的频率。
		MobclickAgent.updateOnlineConfig(context);
		// 您可以通过在程序入口处的 Activity 中调用如下代码来设置加密模式
		// 如果enable为true，SDK会对日志进行加密。加密模式可以有效防止网络攻击，提高数据安全性。
		AnalyticsConfig.enableEncrypt(true);
		// getDeviceInfo(this);
		// 取得相应的值,如果没有该值,说明还未写入,用true作为默认值
		initHandler();
		getLocation();// 开始定位
		isFirstIn = MyApplication.getIsFirstIn();//获取用户是否是第一次安装
		new StartTask().execute();
	}

	private void initView() {
		// iv_start = (ImageView) findViewById(R.id.iv_start);
		iv_change = (ImageView) findViewById(R.id.iv_change);
		tv_time = (TextView) findViewById(R.id.tv_time);
		rl_jump = (RelativeLayout) findViewById(R.id.rl_jump);

		iv_change.setOnClickListener(this);
		rl_jump.setOnClickListener(this);

		showAnim = AnimationUtils.loadAnimation(this, R.anim.start_view_show);
		hideAnim = AnimationUtils.loadAnimation(this, R.anim.start_view_hide);
	}

	private void initHandler() {
		mHandler_timer = new MyHandler_Timer();
		mHandler_firstFigure = new Handler_firstFigure();
		mHandler_getCityList = new Handler_cityList();
		// 定位广播接听
		broadcastReceiver();
		// 注册广播
		registeBoardCast();

	}

	public class StartTask extends AsyncTask<String, Void, Integer> {
		@Override
		protected Integer doInBackground(String... params) {
			try {
				Thread.sleep(1 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return 1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			// 如果第一次启动 不显示广告
			CommonUtil.logE("如果第一次启动 不显示广告:isFirstIn:"+isFirstIn);
			if (!isFirstIn) {
				getFirstFigure();// 获取首图
			} else {
				jumpToGuideOrFirstActivity();
			}
		}
	}

	private void getLocation() {
		// 定位
		LocationUtil_GD.getInstance().startLocation(getApplicationContext(),
				Constants.SENDBROACAST_LOCATION_STARTACTIVITY);
		// startLocationTimer();// 定位倒计时
		// 获取运营更换图片

	}

	private void judgeLocationIsSuccess() {
		if (!TextUtils.isEmpty(localtion_city)
				&& !TextUtils.isEmpty(localtion_address)
				&& !TextUtils.isEmpty(localtion_lat)
				&& !TextUtils.isEmpty(localtion_lng)) {
			CommonUtil.log("startActivity----定位倒计时结束：定位成功，localtion_city："
					+ localtion_city);
		} else {
			// 定位失败
			LocationUtil_GD.getInstance().stopLocation();// 停止定位
			// 获取默认的地址--给定值
			initDefaultCityInfo();
			CommonUtil.log("startActivity----定位倒计时结束：定位失败");
			Toast_Util.showToastOnlyOne(getApplicationContext(), "定位失败，默认城市为："
					+ df_city, Gravity.BOTTOM, true);
		}
	}

	/**
	 * 用户信息修改广播
	 */
	private void registeBoardCast() {
		dynamic_filter_search = new IntentFilter();
		dynamic_filter_search
				.addAction(Constants.REFRESH_LOCATION_STARTACTIVITY);
		registerReceiver(dynamicReceiver, dynamic_filter_search);
	}

	/**
	 * 广播接收
	 */
	private void broadcastReceiver() {
		dynamicReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent intent) {
				if (null != intent) {
					// 定位刷新
					if (intent.getAction().equals(
							Constants.REFRESH_LOCATION_STARTACTIVITY)) {
						Bundle bundle = intent.getExtras();
						if (null != bundle) {
							localtion_city = bundle.getString("city");
							localtion_lat = bundle.getString("lat");
							localtion_lng = bundle.getString("lng");
							localtion_address = bundle.getString("address");

							CommonUtil.log("StartActivity:_localtion_city"
									+ localtion_city);
							CommonUtil.log("StartActivity:_localtion_lat:"
									+ localtion_lat);
							CommonUtil.log("StartActivity:_localtion_lng:"
									+ localtion_lng);
							CommonUtil.log("StartActivity:localtion_address:"
									+ localtion_address);
							if (!TextUtils.isEmpty(localtion_city)
									&& !TextUtils.isEmpty(localtion_address)
									&& !TextUtils.isEmpty(localtion_lat)
									&& !TextUtils.isEmpty(localtion_lng)) {
								getCityList();// 获取城市列表

							} else {
								// 定位失败
								// 获取默认的地址--给定值
								initDefaultCityInfo();
								Toast_Util.showToastOnlyOne(context,
										"定位失败，默认城市为：" + df_city,
										Gravity.BOTTOM, false);

							}
						} else {
							// 定位失败
							// 获取默认的地址--给定值
							initDefaultCityInfo();
							Toast_Util.showToastOnlyOne(context, "定位失败，默认城市为："
									+ df_city, Gravity.BOTTOM, false);

						}
					}
				}
			}
		};
	}

	private void getCityList() {
		getCityList = new GetCityList(context, mHandler_getCityList);
		Thread thread = new Thread(getCityList);
		thread.start();
	}

	class Handler_cityList extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				JSONObject jsonObject = getCityList.getOutObj();
				if (null != jsonObject) {
					cityListStr = jsonObject.toString();
					CommonUtil.saveStringOfSharedPreferences(
							getApplicationContext(), "city_list", cityListStr);
				}
				cityList = getCityList.getCityList();
				boolean isOpen = getCityCodeByLocationCity();
				CommonUtil
						.logE("---------------startActivity-------------isOpen:"
								+ isOpen);
				unOpenThisCity(isOpen);
				break;
			case MsgId.DOWN_DATA_F:
				// DialogUtils.showToastMsg(context, context.getResources()
				// .getString(R.string.common_toast_net_down_data_fail),
				// Toast.LENGTH_SHORT);
				// 获取城市列表失败，给定城市未开通，走默认城市
				// 获取默认的地址--给定值
				initDefaultCityInfo();
				break;
			case MsgId.NET_NOT_CONNECT:
				// 获取城市列表失败，给定城市未开通，走默认城市
				// 获取默认的地址--给定值
				initDefaultCityInfo();
				CustomDialogProgressHandler.getInstance().setCustomDialog(
						context,
						getString(R.string.common_toast_net_not_connect), true);
				break;
			}
		}
	}

	/**
	 * 判断城市是否开通 ，若是 开通获取城市cityCode
	 */
	private boolean getCityCodeByLocationCity() {
		List<Content> cityList = addCityToList();
		// CommonUtil
		// .log("=========================================== 获取城市cityCodecityList:"
		// + cityList);
		if (!CommonUtil.emptyListToString3(cityList)) {
			for (int i = 0; i < cityList.size(); i++) {
				String city = cityList.get(i).getName();

				CommonUtil.log("city:" + city);
				CommonUtil.log("localtion_city:" + localtion_city);

				if (localtion_city.contains(city)) {

					String cityCode = cityList.get(i).getCode();
					String openStatus = cityList.get(i).getServingStatus();

					localtion_cityCode = cityCode;
					localtion_city_openStatus = openStatus;
					return true;
				}
			}
		}

		return false;

	}

	/**
	 * 封装城市信息
	 * 
	 * @return
	 */
	private List<Content> addCityToList() {
		// 初始化数据
		List<Content> list = GetCityListUtil.getInstance().addCityToList(
				cityListStr);

		return list;

	}

	/**
	 * 根据城市是否开通 初始化城市信息
	 * 
	 * @param flag
	 */
	private void unOpenThisCity(boolean flag) {
		// 存储城市是否开通
		CommonUtil.saveBooleanOfSharedPreferences(context, "CITY_ISOPEN", flag);
		// 未开通
		if (!flag) {
			// 获取默认的地址--给定值
			initDefaultCityInfo();
			Toast_Util.showToastOnlyOne(context, "您当前所在的城市：" + localtion_city
					+ "还未开通，默认城市为：北京", Gravity.CENTER, true);
		} else {

			initMyApplicationCityInfo(localtion_city, localtion_cityCode,
					localtion_city_openStatus, localtion_lat, localtion_lng,
					localtion_address);
		}
	}

	/**
	 * 给定默认城市
	 */
	private void initDefaultCityInfo() {
		df_city = CommonUtil.getStringOfSharedPreferences(
				getApplicationContext(), "DF_CITY", context.getResources()
						.getString(R.string.df_city));
		df_addr = CommonUtil.getStringOfSharedPreferences(
				getApplicationContext(), "DF_ADDRESS", context.getResources()
						.getString(R.string.df_address));
		df_cityCode = CommonUtil.getStringOfSharedPreferences(
				getApplicationContext(), "DF_CITYCODE", context.getResources()
						.getString(R.string.df_citycode));
		df_openStatus = CommonUtil.getStringOfSharedPreferences(
				getApplicationContext(), "DF_OPEN_STATUS", context
						.getResources().getString(R.string.df_open_status));
		df_lng = CommonUtil.getStringOfSharedPreferences(
				getApplicationContext(), "DF_LNG", context.getResources()
						.getString(R.string.df_lng));
		df_lat = CommonUtil.getStringOfSharedPreferences(
				getApplicationContext(), "DF_LAT", context.getResources()
						.getString(R.string.df_lat));

		CommonUtil.saveStringOfSharedPreferences(getApplicationContext(),
				"NOW_CITY", df_city);
		CommonUtil.saveStringOfSharedPreferences(getApplicationContext(),
				"NOW_ADDRESS", df_addr);
		CommonUtil.saveStringOfSharedPreferences(getApplicationContext(),
				"NOW_CITYCODE", df_cityCode);
		CommonUtil.saveStringOfSharedPreferences(getApplicationContext(),
				"NOW_OPEN_STATUS", df_openStatus);
		CommonUtil.saveStringOfSharedPreferences(getApplicationContext(),
				"NOW_LNG", df_lng);
		CommonUtil.saveStringOfSharedPreferences(getApplicationContext(),
				"NOW_LAT", df_lat);

	}

	/**
	 * 初始化cityinfo
	 */
	private void initMyApplicationCityInfo(String city, String cityCode,
			String openStatus, String lat, String lng, String address) {
		CommonUtil.saveStringOfSharedPreferences(getApplicationContext(),
				"NOW_CITY", city);
		CommonUtil.saveStringOfSharedPreferences(getApplicationContext(),
				"NOW_ADDRESS", address);
		CommonUtil.saveStringOfSharedPreferences(getApplicationContext(),
				"NOW_CITYCODE", cityCode);
		CommonUtil.saveStringOfSharedPreferences(getApplicationContext(),
				"NOW_OPEN_STATUS", openStatus);
		CommonUtil.saveStringOfSharedPreferences(getApplicationContext(),
				"NOW_LNG", lng);
		CommonUtil.saveStringOfSharedPreferences(getApplicationContext(),
				"NOW_LAT", lat);

	}

	/**
	 * 展示图片三秒
	 * 
	 * @param timeLong
	 */
	private void startTimer(int timeLong) {
		this.timeLong = timeLong;
		CommonUtil.log("---------展示图片倒计时------------倒计时开始--------------------");
		mTimer = new Timer();
		notificationTimeTask = new NotificationTimeTask();
		mTimer.schedule(notificationTimeTask, 0, 1000);
	}

	private class NotificationTimeTask extends TimerTask {
		int time = BASEMSGID + timeLong;

		@Override
		public void run() {
			Message message = new Message();
			message.what = time--;
			mHandler_timer.sendMessage(message);
		}
	}

	class MyHandler_Timer extends Handler {
		@Override
		public void handleMessage(Message msg) {
			CommonUtil.log("倒计时：msg.what：" + msg.what);
			if (msg.what > BASEMSGID) {
				tv_time.setText(msg.what - BASEMSGID + "s");
				return;
			}

			if (msg.what == BASEMSGID) {
				if (mTimer != null) {
					mTimer.cancel();
				}
				CommonUtil.saveBooleanOfSharedPreferences(StartActivity.this,
						"HAS_OPEN_APP", true);
				tv_time.setVisibility(View.GONE);
				tv_time.setAnimation(hideAnim);
				jumpToGuideOrFirstActivity();
			}
		}
	}

	/**
	 * 跳转首页或者引导页
	 */
	private void jumpToGuideOrFirstActivity() {
		judgeLocationIsSuccess();// 判断定位是否成功
		// 跳转页面
		if (isFirstIn) {
			intent = new Intent(context, GuideActivity.class);
		} else {
			intent = new Intent(context, HomeActivity.class);
			intent.putExtra("tabIndex", 0);
		}
		startActivity(intent);
		finish();
	}

	/**
	 * 获取更换图片
	 */
	private void getFirstFigure() {
		getFirstFigure_thread = new GetFirstFigure_thread(context,
				mHandler_firstFigure, null);
		Thread thread = new Thread(getFirstFigure_thread);
		thread.start();
	}

	class Handler_firstFigure extends Handler {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				firstFigureObj = getFirstFigure_thread.getOuObjecct();
				if (null != firstFigureObj) {
					// firstFigurePath 图片地址 ANS 256 O 欢迎页图片地址
					// firstFigureUrl 跳转地址 ANS 256 O 欢迎页跳转地址
					firstFigurePath = firstFigureObj.optString(
							"firstFigurePath", "");
					firstFigureUrl = firstFigureObj.optString("firstFigureUrl",
							"");
					handleFirstFigure();
				} else {
					showTimeLongByIsSuccess(false);
				}

				break;
			case MsgId.DOWN_DATA_F:
				// Toast_Util.showToastOnlyOne(context,context.getResources().getString(R.string.common_toast_net_connect_error),
				// Gravity.BOTTOM, false);
				showTimeLongByIsSuccess(false);
				closeCustomCircleProgressDialog();
				break;
			case MsgId.NET_NOT_CONNECT:
				showTimeLongByIsSuccess(false);
				setCustomDialog(
						context.getResources().getString(
								R.string.common_toast_net_not_connect), true);
				break;
			}

		}
	}

	/**
	 * 获取首图连接
	 */
	private void handleFirstFigure() {

		if (!TextUtils.isEmpty(firstFigurePath)) {
			ImageLoader.getInstance().displayImage(firstFigurePath, iv_change,
					null, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
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

							CommonUtil
									.logE("-----------------------------Image_Viewpagetr:获取图片失败类型 :message:"
											+ message);
							showTimeLongByIsSuccess(false);
							// Toast_Util.showToastOnlyOne(mContext, message,
							// Gravity.CENTER, false);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							showTimeLongByIsSuccess(true);
						}
					});

		} else {
			showTimeLongByIsSuccess(false);
		}
	}

	/**
	 * 是否显示首图浮层
	 * 
	 * @param flag
	 */
	private void isShowFirstFigure(boolean flag) {
		if (flag) {
			// iv_start.setVisibility(View.GONE);
			iv_change.setVisibility(View.VISIBLE);
			rl_jump.setVisibility(View.VISIBLE);
			tv_time.setVisibility(View.VISIBLE);

			// iv_start.setAnimation(hideAnim);
			// iv_change.setAnimation(showAnim);
			// rl_jump.setAnimation(showAnim);
			tv_time.setAnimation(showAnim);
		} else {
			// iv_start.setVisibility(View.VISIBLE);
			iv_change.setVisibility(View.GONE);
			rl_jump.setVisibility(View.GONE);
			tv_time.setVisibility(View.GONE);

			// iv_start.setAnimation(showAnim);
			// iv_change.setAnimation(hideAnim);
			// rl_jump.setAnimation(hideAnim);
			tv_time.setAnimation(hideAnim);

		}
	}

	/**
	 * 获取首图是否成功
	 * 
	 * @param isSuccess
	 */
	private void showTimeLongByIsSuccess(boolean isSuccess) {
		if (isSuccess) {
			// 获取图片成功
			isShowFirstFigure(true);
			// 展示 图片3秒
			tv_time.setVisibility(View.VISIBLE);
			startTimer(3);
		} else {
			// 获取图片失败
			isShowFirstFigure(false);
			if (mTimer != null) {
				mTimer.cancel();
			}
			// 不展示展示图片
			jumpToGuideOrFirstActivity();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_change:// 广告页面
			if (mTimer != null) {
				mTimer.cancel();
			}
			// 图片连接为空点击直接打开app
			if (TextUtils.isEmpty(firstFigureUrl)) {
				jumpToGuideOrFirstActivity();
				break;
			}

			boolean isJump = JumpTargetActivityByScanOrFirstImg.getInstance()
					.jump(this, firstFigureUrl);
			// 没有获取到相应参数--直接跳转
			if (!isJump) {
				CommonUtil.saveBooleanOfSharedPreferences(StartActivity.this,
						"HAS_OPEN_APP", true);
				jumpToGuideOrFirstActivity();
			} else {
				// 获取到相应参数-跳转到相应界面并关闭当前页面
				CommonUtil.saveBooleanOfSharedPreferences(StartActivity.this,
						"HAS_OPEN_APP", false);
				finish();
			}
			break;
		case R.id.rl_jump:// 跳过
			if (mTimer != null) {
				mTimer.cancel();
			}
			CommonUtil.saveBooleanOfSharedPreferences(StartActivity.this,
					"HAS_OPEN_APP", true);
			jumpToGuideOrFirstActivity();
			break;
		default:
			break;
		}

	}

	@Override
	public void onBackPressed() {
		// MyApplication.getInstance().exit();
		MyApplication.getInstance().exit();
	}

}