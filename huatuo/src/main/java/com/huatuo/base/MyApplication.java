package com.huatuo.base;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import com.huatuo.R;
import com.huatuo.activity.db.DatabaseService;
import com.huatuo.activity.find.FindDetail_ServiceListActivity;
import com.huatuo.activity.find.FindDetail_StoreListActivity;
import com.huatuo.activity.find.FindDetail_TechListActivity;
import com.huatuo.activity.find.FindDetail_TextImgListActivity;
import com.huatuo.activity.login.LoginUtil;
import com.huatuo.activity.order.OrderDetailActivity;
import com.huatuo.activity.pay.PayUtil;
import com.huatuo.activity.project.ProjectDetailActivity;
import com.huatuo.activity.store.StoreDetail_Aty;
import com.huatuo.activity.technician.TechnicianDetail;
import com.huatuo.citylist.Content;
import com.huatuo.citylist.GetCityListUtil;
import com.huatuo.dictionary.MsgId;
import com.huatuo.interfaces.IHandler;
import com.huatuo.net.thread.GetCityList;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Constants;
import com.huatuo.util.CustomDialogProgressHandler;
import com.huatuo.util.DialogUtils;
import com.huatuo.util.LocationUtil_GD;
import com.huatuo.util.Toast_Util;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import com.umeng.socialize.PlatformConfig;

public class MyApplication extends Application {
	private static MyApplication instance;
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
	private Handler mHandler_getCityList;
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
	private int timeLong = 5;// 定位最大时长限制为5秒

	private JSONObject umenPushObj;
	private PushAgent mPushAgent;
	private static List<Activity> mList = new LinkedList();
	public static Context context;
	private static SharedPreferences userInfoConfig;
	// public static JSONObject userInfo;
	public static File saveImagesFilePath;
	private DatabaseService ds;

	public static Context getAppContext() {
		return MyApplication.context;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		CommonUtil
				.log("------------------------------------------Application-------------------------------");
		context = getApplicationContext();
		CommonUtil.saveBooleanOfSharedPreferences(this, "HAS_OPEN_APP", false);
		initCommoParameters();// 初始化公共参数
		initImageLoader();// 初始化图片加载参数
		initCityInfo();// 初始化获取城市信息
		initUMengParam();// 初始化友盟参数
		createDB();// 创建数据库
		umengPushhandler();// 友盟推送回调

	}

	public static MyApplication getInstance() {
		if (instance == null) {
			instance = new MyApplication();
		}
		return instance;
	}

	/**
	 * client 接入方 ANDROID sign 签名 deviceId 设备号 version 客户端版本
	 */
	private void initCommoParameters() {
		// app 接入方 ANS 32 M ios 、android、wechat(微信)、H5
		// deviceID 设备号 ANS 64 C
		// 客户端唯一设备号，用imei(长度小于13位放弃)或MAC，如果俩个都得不到，初次安装的时候生成UUID（系统提供的一个方法生成的唯一号）将这个uuid记录下来，每次会话都用这个，微信用户这里用openid
		// screenPixel 屏幕像素 ANS 16 M 屏幕像素(如:320x480),用长x宽方式表示
		// longitude 经度 ANS 32 C （gps 位置坐标）
		// latitude 纬度 ANS 32 C （gps 位置坐标）
		// appVersion 客户端版本 ANS 16 M 手机客户端版本
		// messageID 消息id ANS 32 C [唯一消息id串]//同步处理不需要，服务端会原样返回
		// channelCode 推广渠道编号 ANS 128 O 推广渠道编号，可空
		// sign 签名 ANS 32 C 敏感接口必填（暂无）
		// 接入方
		// 获取手机串号（设备号）
		CommonUtil.DEVICEID = CommonUtil.getIMEI(context);
		// channelCode 推广渠道编号
		CommonUtil.CHANNELCODE = CommonUtil.getChannelCode(context);
		// 客户端版本
		CommonUtil.APPVERSION = CommonUtil.getVersionName(context);
	}

	private void initImageLoader() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.icon_default_small)
				.showImageForEmptyUri(R.drawable.icon_default_small)
				.showImageOnFail(R.drawable.icon_default_small)
				.resetViewBeforeLoading(true)
				.cacheInMemory(false)
				// 防止oom
				.cacheOnDisk(true).imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();
		// 获取本地缓存的目录，该目录在SDCard的根目录下
		// File cacheDir =
		// StorageUtils.getCacheDirectory(context);//.diskCache(new
		// UnlimitedDiscCache(cacheDir))// 设定缓存的SDcard目录，UnlimitDiscCache速度最快（加）
		// LogUtil.e("deesha", cacheDir.getPath());
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context)
				.threadPoolSize(3)
				// 设置线程数量（加）
				.memoryCache(new WeakMemoryCache())
				// 设定内存缓存为弱缓存（加）
				// .memoryCacheExtraOptions(480, 800)
				// //设定内存图片缓存大小限制，不设置默认为屏幕的宽高（加）
				.imageDownloader(new BaseImageDownloader(context, 10000, 60000))
				// 设定网络连接超时 timeout: 10s 读取网络连接超时read timeout: 60s（加）
				.threadPriority(Thread.NORM_PRIORITY - 2)
				// 设定线程等级比普通低一点
				.denyCacheImageMultipleSizesInMemory()
				// 设定只保存同一尺寸的图片在内存
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				// 设定缓存到SDCard目录的文件命名
				.diskCacheSize(50 * 1024 * 1024)
				// 50 Mb
				.diskCacheExtraOptions(480, 800, null)
				// .diskCacheExtraOptions(480, 320, null)//防止oom
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.defaultDisplayImageOptions(options) // default
				.build();
		ImageLoader.getInstance().init(config);
	}

	/**
	 * 友盟推送通知接受
	 */
	private void umengPushhandler() {

		/**
		 * 该Handler是在BroadcastReceiver中被调用，故
		 * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
		 * */

		UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
			@Override
			public void launchApp(Context context, UMessage msg) {
				JSONObject object = msg.getRaw();
				if (!object.isNull("extra")) {
					try {
						umenPushObj = object.getJSONObject("extra");
						CommonUtil.log("友盟推送umenPushObj:" + umenPushObj);
						// 用于判断点击通知是否需要再次定位---退出app清空
						// ---不退出app（防止点击通知后再次定位，会把用户选择的地址冲刷掉）
						boolean has_open_app = CommonUtil
								.getBooleanOfSharedPreferences(
										getApplicationContext(),
										"HAS_OPEN_APP", false);
						CommonUtil
								.logE("getLocation--定位前============是否打开过app：has_open_app："
										+ has_open_app);
						// app未打开--要定位
						if (!has_open_app) {
							getLocation();
						} else {
							// app打开--无需定位
							handleJump();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		};
		mPushAgent.setNotificationClickHandler(notificationClickHandler);
	}

	// 各个平台的配置，建议放在全局Application或者程序入口
	{
		PlatformConfig.setWeixin(PayUtil.WX_APP_ID, PayUtil.WX_APP_SECRET);

	}

	/**
	 * 友盟推送
	 */
	private void initUMengParam() {
		CommonUtil.log("-------Application-------initUMengParam");
		mPushAgent = PushAgent.getInstance(this);
		mPushAgent.setDebugMode(false);
		// 当应用在后台运行超过30秒（默认）再回到前端，将被认为是两个独立的session(启动)，例如用户回到home，或进入其他程序，经过一段时间后再返回之前的应用。
		// 可通过接口：MobclickAgent.setSessionContinueMillis(long interval)
		// 来自定义这个间隔（参数单位为毫秒）。
		MobclickAgent.setSessionContinueMillis(Constants.INTERVAL);
	}

	/**
	 * 开启线程创建数据库
	 */
	private void createDB() {
		ds = new DatabaseService(this);
		CommonUtil.log("-------Application-------启动时创建数据库");
		new Thread(new Runnable() {

			@Override
			public void run() {
				ds.create_Search_StorePro_Table();
				ds.create_Search_VisitPro_Table();
				ds.create_Search_Tech_Table();
				ds.create_Search_Store_Table();
				ds.create_search_area_selectCity_Table();
			}
		}).start();
	}

	/**
	 * 得到当前的日期 并按此yyyy-MM-dd格式返回
	 * 
	 * @return
	 */
	public static String getCurrentDate() {
		SimpleDateFormat sdf = new java.text.SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		String str = sdf.format(new java.util.Date());
		return str;
	}

	/**
	 * 用来存放用户设置信息的SharedPreferences文件
	 */
	private static void initUserInfoConfig() {
		if (userInfoConfig == null) {
			userInfoConfig = context.getSharedPreferences("userInfoConfig",
					Context.MODE_PRIVATE);
		}
	}

	/**
	 * 获取用户是否登录
	 * 
	 */
	public static boolean getLoginFlag() {
		initUserInfoConfig();
		return userInfoConfig.getBoolean("LoginFlag", false);
	}

	/**
	 * 保存用户是否登录结果
	 */
	public static void setLoginFlag(Boolean isLoginFlag) {
		initUserInfoConfig();
		SharedPreferences.Editor editor = userInfoConfig.edit();
		editor.putBoolean("LoginFlag", isLoginFlag);
		editor.commit();
	}

	/**
	 * 保存用户ID
	 */
	public static void setUserID(String userID) {
		initUserInfoConfig();
		SharedPreferences.Editor editor = userInfoConfig.edit();
		editor.putString("userID", userID);
		editor.commit();
	}

	/**
	 * 获取用户ID
	 * 
	 */
	public static String getUserID() {
		initUserInfoConfig();
		return userInfoConfig.getString("userID", "");
	}

	/**
	 * 保存用户Mobile
	 */
	public static void setUserMobile(String mobile) {
		initUserInfoConfig();
		SharedPreferences.Editor editor = userInfoConfig.edit();
		editor.putString("mobile", mobile);
		editor.commit();
	}

	/**
	 * 获取用户Mobile
	 * 
	 */
	public static String getUserMobile() {
		initUserInfoConfig();
		return userInfoConfig.getString("mobile", "");
	}

	/**
	 * 保存 UseServiceNumber
	 */
	public static void setUserJSON(JSONObject obj) {
		initUserInfoConfig();
		SharedPreferences.Editor editor = userInfoConfig.edit();
		editor.putString("userJson", obj.toString());
		editor.commit();
	}

	/**
	 * 获取用户 UseServiceNumber
	 * 
	 */
	public static JSONObject getUserJSON() {
		initUserInfoConfig();
		String str = userInfoConfig.getString("userJson", "{}");
		JSONObject out = null;
		try {
			out = new JSONObject(str);
		} catch (JSONException e) {
			out = new JSONObject();
		}

		return out;

	}

	/**
	 * 获取是否第一次启动应用，默认用户是（true）第一次启动
	 * 
	 */
	public static boolean getIsFirstIn() {
		initUserInfoConfig();
		return userInfoConfig.getBoolean("isFirstIn", true);
	}

	/**
	 * 第一次启动应用后将状态改为false
	 */
	public static void setIsFirstIn() {
		initUserInfoConfig();
		SharedPreferences.Editor editor = userInfoConfig.edit();
		editor.putBoolean("isFirstIn", false);
		editor.commit();
	}

	public static JSONObject outObj;

	/**
	 * 初始化定位相关信息
	 */
	private void initCityInfo() {
		mHandler_getCityList = new Handler_cityList();
		// 定位广播接听
		broadcastReceiver();
		// 注册广播
		registeBoardCast();
	}

	/**
	 * 定位
	 */
	private void getLocation() {
		// 定位
		LocationUtil_GD.getInstance().startLocation(getApplicationContext(),
				Constants.SENDBROACAST_LOCATION_APPLICATION);
		startTimer();// 倒计时五秒

	}

	private void startTimer() {
		CommonUtil
				.log("--------Application-------------倒计时开始--------------------");
		// mTimer = new Timer();
		// notificationTimeTask = new NotificationTimeTask();
		// mTimer.schedule(notificationTimeTask, 0, 1000);
		new StartTask().execute();
	}

	public class StartTask extends AsyncTask<String, Void, Integer> {
		@Override
		protected Integer doInBackground(String... params) {
			try {
				Thread.sleep(timeLong * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return 1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if (!TextUtils.isEmpty(localtion_city)
					&& !TextUtils.isEmpty(localtion_address)
					&& !TextUtils.isEmpty(localtion_lat)
					&& !TextUtils.isEmpty(localtion_lng)) {
				CommonUtil.log("application----倒计时结束：定位成功，localtion_city："
						+ localtion_city);
			} else {
				// 定位失败
				// 获取默认的地址--给定值
				initDefaultCityInfo();
				CommonUtil.log("application----倒计时结束：定位失败");
				Toast_Util.showToastOnlyOne(getApplicationContext(),
						"定位失败，默认城市为：" + df_city, Gravity.BOTTOM, true);
				LocationUtil_GD.getInstance().stopLocation();
				handleJump();//
			}
		}
	}

	/**
	 * 用户信息修改广播
	 */
	private void registeBoardCast() {
		dynamic_filter_search = new IntentFilter();
		dynamic_filter_search.addAction(Constants.REFRESH_LOCATION_APPLICATION);
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
							Constants.REFRESH_LOCATION_APPLICATION)) {
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
								handleJump();

							}
						} else {
							// 定位失败
							// 获取默认的地址--给定值
							initDefaultCityInfo();
							Toast_Util.showToastOnlyOne(context, "定位失败，默认城市为："
									+ df_city, Gravity.BOTTOM, false);
							handleJump();

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
				DialogUtils.showToastMsg(context, context.getResources()
						.getString(R.string.common_toast_net_down_data_fail),
						Toast.LENGTH_SHORT);
				// 获取城市列表失败，给定城市未开通，走默认城市
				// 获取默认的地址--给定值
				initDefaultCityInfo();
				break;
			case MsgId.NET_NOT_CONNECT:
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
	 * 获取城市cityCode
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

	private List<Content> addCityToList() {
		// 初始化数据
		List<Content> list = GetCityListUtil.getInstance().addCityToList(
				cityListStr);

		return list;

	}

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
		handleJump();

	}

	/**
	 * 处理推送后续行为
	 */
	private void handleJump() {

		// 仅打开app：
		// Key: app Value: app
		// 打开门店页:
		// Key: store Value: 门店id 如：00600afc-093b-4ef5-bac3-5323e4273260
		// 打开服务页
		// Key: service Value: 服务id 如：000f57f5-84d8-4952-80cd-6f4a1ce68610
		// 打开技师页
		// Key: skillWorker Value: 技师id 如：00084ac3-ed2e-4314-bd6e-79e89f511375
		// 打开发现页
		// Key:find Value: 发现id 如：10000010
		CommonUtil.log("友盟推送umenPushObj:" + umenPushObj);
		if (null == umenPushObj) {
			return;
		}

		umengJump();

		// 跳转到程序主入口
		// Intent intent = new Intent(this, ExitAcitivty.class);
		// intent.putExtra("UMENG_PUSH", umenPushObj.toString());
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// startActivity(intent);

	}

	private void umengJump() {
		Intent intent;
		// if (null == umenPushObj) {
		// return;
		// }
		try {
			String open_type = umenPushObj.getString(Constants.PUSH_OPEN_TYPE);
			CommonUtil.log("友盟推送open_type:" + open_type);
			if (Constants.PUSH_OPEN_STORE.equals(open_type)) {// 打开门店页:

				String storeID;
				storeID = umenPushObj.getString(Constants.PUSH_OPEN_STORE);

				CommonUtil.log("友盟推送storeID:" + storeID);
				intent = new Intent(this, StoreDetail_Aty.class);
				intent.putExtra("ID", storeID);
				intent.putExtra("push", true);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);

			} else if (Constants.PUSH_OPEN_SERVICE.equals(open_type)) {// 打开服务页
				String serviceID = umenPushObj
						.getString(Constants.PUSH_OPEN_SERVICE);
				CommonUtil.log("友盟推送serviceID:" + serviceID);
				intent = new Intent(this, ProjectDetailActivity.class);
				intent.putExtra("ID", serviceID);
				intent.putExtra("push", true);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			} else if (Constants.PUSH_OPEN_SKILLWORKER.equals(open_type)) {// 打开技师页
				String techID = umenPushObj
						.getString(Constants.PUSH_OPEN_SKILLWORKER);
				CommonUtil.log("友盟推送techID:" + techID);
				intent = new Intent(this, TechnicianDetail.class);
				intent.putExtra("ID", techID);
				intent.putExtra("push", true);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			} else if (Constants.PUSH_OPEN_FIND.equals(open_type)) {// 打开发现页
				String find_type = umenPushObj
						.getString(Constants.PUSH_OPEN_FIND_TYPE);
				CommonUtil.log("友盟推送find_type:" + find_type);
				String ID = umenPushObj.getString(Constants.PUSH_OPEN_FIND);
				int type = -1;
				if (!TextUtils.isEmpty(find_type)) {
					type = Integer.parseInt(find_type.trim());
				}
				// type:0 门店 1 服务 2 技师 3 图文
				CommonUtil.log("---------------------type:" + type);
				switch (type) {
				case 0:
					intent = new Intent(context,
							FindDetail_StoreListActivity.class);
					intent.putExtra("ID", ID);
					intent.putExtra("push", true);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
					break;
				case 1:
					intent = new Intent(context,
							FindDetail_ServiceListActivity.class);
					intent.putExtra("ID", ID);
					intent.putExtra("push", true);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
					break;
				case 2:
					intent = new Intent(context,
							FindDetail_TechListActivity.class);
					intent.putExtra("ID", ID);
					intent.putExtra("push", true);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
					break;
				case 3:
					intent = new Intent(context,
							FindDetail_TextImgListActivity.class);
					intent.putExtra("ID", ID);
					intent.putExtra("push", true);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
					break;
				}
			} else if (Constants.PUSH_OPEN_ORDER.equals(open_type)) {// 打开订单页
				final String orderID = umenPushObj
						.getString(Constants.PUSH_OPEN_ORDER);
				CommonUtil.log("友盟推送orderID:" + orderID);
				// 判断是否登录
				LoginUtil.IsLogin(this, new IHandler() {

					@Override
					public void doHandler() {
						Intent intent = new Intent(getApplicationContext(),
								OrderDetailActivity.class);
						intent.putExtra("orderID", orderID);
						intent.putExtra("push", true);
						intent.putExtra("orderFrom", Constants.NOPAY);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);// TODO Auto-generated method stub
					}
				});

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	// add Activity
	public void addActivity(Activity activity) {
		mList.add(activity);
	}

	public void exit() {
		try {
			for (Activity activity : mList) {
				if (activity != null)
					activity.finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);

			// 防止退不出程序 进程为啥死掉
			ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
			am.killBackgroundProcesses(getPackageName()); // API Level至少为8才能使用
		}
	}

	public void onLowMemory() {
		super.onLowMemory();
		System.gc();
	}

}