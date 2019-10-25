package com.huatuo.activity.home;

import org.json.JSONObject;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.huatuo.R;
import com.huatuo.activity.find.FindListFragment;
import com.huatuo.activity.guide.UpdateManager;
import com.huatuo.activity.order.FragmentMyOrder;
import com.huatuo.activity.personal.FragmentPersonalCenter;
import com.huatuo.base.BaseFragmentActivity;
import com.huatuo.base.MyApplication;
import com.huatuo.custom_widget.CustomDialog;
import com.huatuo.dictionary.MsgId;
import com.huatuo.fragment.FirstFragment;
import com.huatuo.fragment.FragmentVisit;
import com.huatuo.net.thread.GetVersionInfo;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Constants;
import com.huatuo.util.CustomDialogProgressHandler;
import com.huatuo.util.DialogUtils;
import com.huatuo.util.UmengEventUtil;
import com.huatuo.util.UmengPushUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

/**
 * 项目的主Activity，所有的Fragment都嵌入在这里。
 * 
 * @author ftc
 */
public class HomeActivity extends FragmentActivity implements OnClickListener {
	String TAG = "HomeActivity";
	private Context mContext;
	/** 首页 Fragment */
	private FirstFragment firstFragment;
	// 上门
	private FragmentVisit fragmentVisit;
	private FindListFragment findListFragment;
	/** 我的订单 Fragment */
	private FragmentMyOrder fragmentMyOrder;
	/** 个人中心 Fragment */
	private FragmentPersonalCenter fragmentPersonalCenter;
	private TextView tv_home_first, tv_home_visit, tv_home_found,
			tv_home_order, tv_wode, tv_order_num;
	/** 用于对Fragment进行管理 */
	private FragmentManager fragmentManager;
	/** 用来标识请求 登录 */
	private static final int REQUEST_LOGIN_ACTIVITY = 123;
	private int tabIndex = 0;
	private int receive_index = 0;
	public static int PhoneType;

	private String localtion_district = "";// 定位的区
	private String localtion_city = "";// 定位的城市
	private String localtion_address = "";// 定位的详细地址
	private Intent intent;
	/**
	 * 定位刷新广播
	 */
	private IntentFilter dynamic_filter = null;
	/**
	 * 广播接收器
	 */
	private BroadcastReceiver dynamicReceiver;

	private IntentFilter dynamic_filter_refresh_unpaidCount = null;

	// 版本更新
	private GetVersionInfo getVersionInfo;
	private Handler mHandler;

	// private final String EXITAPP = "eixtapp";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_home);
		mContext = this;
		initWindowPixel();
		fragmentManager = getSupportFragmentManager();
		// 初始化布局元素
		initWidget();
		CommonUtil.logE("DeviceInfo:" + getDeviceInfo(mContext));
		mHandler = new MyHandler();// 版本更新
		CommonUtil.saveBooleanOfSharedPreferences(mContext, "HAS_OPEN_APP",
				true);
		PushAgent mPushAgent = PushAgent.getInstance(this);
		mPushAgent.enable();

		// 获取deviceToken
		// 判断是否获取到devicetoken
		UmengPushUtil.getInstance().getDeviceToken(this);
		getExtras(getIntent());
		// 广播接收
		broadcastReceiver();
		// 注册广播
		registeBoardCast();

		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		PhoneType = tm.getPhoneType();
		getVersionInfo();

	}
	 private static WakeLock wakeLock;
	 public static void keepScreenOn(Context context, boolean on) {
	        if (on) {
	            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
	            wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "==KeepScreenOn==");
	            wakeLock.acquire();
	        } else {
	            if (wakeLock != null) {
	                wakeLock.release();
	                wakeLock = null;
	            }
	        }
	    }
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
//		keepScreenOn(HomeActivity.this, true);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	/**
	 * client 接入方 ANDROID sign 签名 deviceId 设备号 version 客户端版本
	 */
	private void initWindowPixel() {
		// 客户端版本
		if (TextUtils.isEmpty(CommonUtil.SCREENPIXEL)) {
			CommonUtil.initScreen(this);
			CommonUtil.SCREENPIXEL = CommonUtil.WIDTH_SCREEN + "x"
					+ CommonUtil.HEIGHT_SCREEN;
		}
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
		getExtras(getIntent());
	}

	private void getExtras(Intent intent) {
		Bundle bundle = null;
		if (intent != null) {
			bundle = intent.getExtras();
			if (null != bundle) {
				receive_index = bundle.getInt("tabIndex", -1);
				if (receive_index != -1) {
					tabIndex = receive_index;
				}
			}
			// 第一次启动时选中第0个tab
		}
		CommonUtil.logE("--------------------------------------------intent:"
				+ intent);
		CommonUtil.logE("--------------------------------------------bundle:"
				+ bundle);
		CommonUtil.logE("--------------------------------------------tabIndex:"
				+ tabIndex);
		setTabSelection(tabIndex);

	}

	/**
	 * 注册广播
	 */
	private void registeBoardCast() {
		// 切换tab
		dynamic_filter = new IntentFilter();
		dynamic_filter.addAction(Constants.REFRESH_CHANGETAB);

		// 推出程序
		dynamic_filter.addAction(Constants.EXIT_APP);
		registerReceiver(dynamicReceiver, dynamic_filter);

		// 刷新unpaidCount
		dynamic_filter_refresh_unpaidCount = new IntentFilter();
		dynamic_filter_refresh_unpaidCount
				.addAction(Constants.REFRESH_UNPAIDCOUNT);
		registerReceiver(dynamicReceiver, dynamic_filter_refresh_unpaidCount);
	}

	/**
	 * 广播接收
	 */
	private void broadcastReceiver() {
		dynamicReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent intent) {
				if (null != intent) {
					// 刷新选中tab
					if (intent.getAction().equals(Constants.REFRESH_CHANGETAB)) {
						getExtras(intent);
					} else if (intent.getAction().equals(
							Constants.REFRESH_UNPAIDCOUNT)) {
						String num = intent.getStringExtra("unpaidCount");
						if (!num.isEmpty()) {
							tv_order_num.setVisibility(View.VISIBLE);
							tv_order_num.setText(num);
						}
					} else if (intent.getAction().equals(Constants.EXIT_APP)) {
						CommonUtil
								.logE("---------------------退出程序-------------------------");
						finish();
					}
				}
			}
		};
	}

	/**
	 * 在这里获取到每个需要用到的控件的实例，并给它们设置好必要的点击事件。
	 */
	private void initWidget() {
		tv_home_first = (TextView) findViewById(R.id.tv_home_first);
		tv_home_visit = (TextView) findViewById(R.id.tv_home_visit);
		tv_home_found = (TextView) findViewById(R.id.tv_home_found);
		tv_home_order = (TextView) findViewById(R.id.tv_home_order);
		tv_order_num = (TextView) findViewById(R.id.tv_order_num);
		tv_wode = (TextView) findViewById(R.id.tv_home_wode);

		tv_home_first.setOnClickListener(this);
		tv_home_visit.setOnClickListener(this);
		tv_home_found.setOnClickListener(this);
		tv_home_order.setOnClickListener(this);
		tv_wode.setOnClickListener(this);

		tv_order_num.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_home_first:
			UmengEventUtil.tab_store(mContext);
			// 当点击了首页tab时，选中第1个tab
			setTabSelection(0);
			break;
		case R.id.tv_home_visit:
			UmengEventUtil.tab_door(mContext);
			// 当点击了首页tab时，选中第1个tab
			setTabSelection(1);
			break;
		case R.id.tv_home_found:
			UmengEventUtil.tab_discover(mContext);
			// if (!MyApplication.getLoginFlag()) {
			// Intent intent = new Intent();
			// intent.setClass(mContext, LoginActivity.class);
			// startActivity(intent);
			// return;
			// }

			// 当点击了我的订单tab时，选中第2个tab
			setTabSelection(2);
			break;
		case R.id.tv_home_order:
			UmengEventUtil.tab_order(mContext);
			setTabSelection(3);
			break;
		case R.id.tv_home_wode:
			UmengEventUtil.tab_my(mContext);
			// 当点击了个人中心tab时，选中第3个tab
			setTabSelection(4);
			break;
		// case R.id.ll_services:
		// // 当点击了服务中心tab时，选中第4个tab
		// setTabSelection(3);
		// break;
		default:
			break;
		}
	}

	/**
	 * 根据传入的index参数来设置选中的tab页。
	 * 
	 * @param index
	 *            每个tab页对应的下标。0表示宝贝秀，1表示育儿，2表示互帮，3表示活动，4表示我的。
	 */
	private void setTabSelection(int index) {
		FragmentTransaction transaction = null;
		switch (index) {
		case 0:// ------------到店
			tabIndex = 0;
			// 每次选中之前先清楚掉上次的选中状态
			clearSelection();
			// 开启一个Fragment事务
			transaction = fragmentManager.beginTransaction();
			// 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
			hideFragments(transaction);

			// // 当点击了首页tab时，改变控件的图片和文字颜色
			tv_home_first.setCompoundDrawablesWithIntrinsicBounds(null,
					getResources().getDrawable(R.drawable.tab_home_che), null,
					null);
			tv_home_first.setTextColor(getResources().getColor(R.color.c1));

			// 当点击了首页tab时，改变控件的图片和文字颜色
			if (firstFragment == null) {
				// 如果fragmentHome为空，则创建一个并添加到界面上
				firstFragment = new FirstFragment();
				transaction.add(R.id.content, firstFragment, "firstFragment");
			} else {
				hideFragments(transaction);
				transaction.show(firstFragment);
			}

			transaction.commitAllowingStateLoss();
			break;
		case 1:// -------------上门
			tabIndex = 1;
			// 每次选中之前先清楚掉上次的选中状态
			clearSelection();
			// 开启一个Fragment事务
			transaction = fragmentManager.beginTransaction();
			// 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
			hideFragments(transaction);

			// 当点击了我的订单tab时，改变控件的图片和文字颜色
			tv_home_visit.setCompoundDrawablesWithIntrinsicBounds(null,
					getResources().getDrawable(R.drawable.tab_door_che), null,
					null);
			tv_home_visit.setTextColor(mContext.getResources().getColor(
					R.color.c1));
			// tv_order.setTextColor(getResources().getColorStateList(R.color.home_bottom_navigation_text_s));
			if (fragmentVisit == null) {
				// 如果fragmentOrder为空，则创建一个并添加到界面上
				fragmentVisit = new FragmentVisit();
				transaction.add(R.id.content, fragmentVisit, "fragmentVisit");
			} else {
				hideFragments(transaction);
				transaction.show(fragmentVisit);

			}

			transaction.commitAllowingStateLoss();

			break;
		case 2:// ------------发现
			tabIndex = 2;
			// 每次选中之前先清楚掉上次的选中状态
			clearSelection();
			// 开启一个Fragment事务
			transaction = fragmentManager.beginTransaction();
			// 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
			hideFragments(transaction);

			// 当点击了我的订单tab时，改变控件的图片和文字颜色
			tv_home_found.setCompoundDrawablesWithIntrinsicBounds(null,
					getResources().getDrawable(R.drawable.tab_found_che), null,
					null);
			tv_home_found.setTextColor(mContext.getResources().getColor(
					R.color.c1));
			// tv_order.setTextColor(getResources().getColorStateList(R.color.home_bottom_navigation_text_s));
			if (findListFragment == null) {
				// 如果fragmentOrder为空，则创建一个并添加到界面上
				findListFragment = new FindListFragment();
				transaction.add(R.id.content, findListFragment,
						"findListFragment");
			} else {
				hideFragments(transaction);
				transaction.show(findListFragment);
			}
			transaction.commitAllowingStateLoss();
			break;

		case 3:// ------------订单
			tabIndex = 3;
			clearSelection();
			transaction = fragmentManager.beginTransaction();
			hideFragments(transaction);
			tv_home_order.setCompoundDrawablesWithIntrinsicBounds(null,
					getResources().getDrawable(R.drawable.tab_orders_che),
					null, null);
			tv_home_order.setTextColor(mContext.getResources().getColor(
					R.color.c1));
			if (fragmentMyOrder == null) {
				fragmentMyOrder = new FragmentMyOrder();
				transaction.add(R.id.content, fragmentMyOrder,
						"fragmentMyOrder");
			} else {
				hideFragments(transaction);

				transaction.show(fragmentMyOrder);

			}
			transaction.commitAllowingStateLoss();
			if (tv_order_num.getVisibility() == View.VISIBLE) {
				tv_order_num.setVisibility(View.GONE);
			}
			if (!MyApplication.getLoginFlag()) {
				DialogUtils.showToastMsg(mContext, "你尚未登录,请登录!",
						Toast.LENGTH_SHORT);
				setTabSelection(4);
			}
			break;
		case 4:// ------------我的
			tabIndex = 4;
			// 每次选中之前先清楚掉上次的选中状态
			clearSelection();
			// 开启一个Fragment事务
			transaction = fragmentManager.beginTransaction();
			// 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
			hideFragments(transaction);
			// 当点击了个人中心tab时，改变控件的图片和文字颜色
			tv_wode.setCompoundDrawablesWithIntrinsicBounds(null,
					getResources().getDrawable(R.drawable.tab_usercenter_che),
					null, null);
			tv_wode.setTextColor(mContext.getResources().getColor(R.color.c1));

			if (fragmentPersonalCenter == null) {

				// 如果fragmentPersonalCenter为空，则创建一个并添加到界面上
				fragmentPersonalCenter = new FragmentPersonalCenter();
				// LogUtil.e("deesha",
				// "helpFragment create="+helpFragment.toString());
				transaction.add(R.id.content, fragmentPersonalCenter,
						"fragmentPersonalCenter");
			} else {
				// 如果fragmentPersonalCenter不为空，则直接将它显示出来
				// LogUtil.e("deesha",
				// "helpFragment show="+helpFragment.toString());
				hideFragments(transaction);
				transaction.show(fragmentPersonalCenter);
			}
			transaction.commitAllowingStateLoss();
			break;
		}

	}

	/**
	 * 清除掉所有的选中状态。
	 */
	private void clearSelection() {
		tv_home_first.setCompoundDrawablesWithIntrinsicBounds(null,
				getResources().getDrawable(R.drawable.tab_home), null, null);
		tv_home_visit.setCompoundDrawablesWithIntrinsicBounds(null,
				getResources().getDrawable(R.drawable.tab_door), null, null);
		tv_home_found.setCompoundDrawablesWithIntrinsicBounds(null,
				getResources().getDrawable(R.drawable.tab_found), null, null);
		tv_home_order.setCompoundDrawablesWithIntrinsicBounds(null,
				getResources().getDrawable(R.drawable.tab_orders), null, null);
		tv_wode.setCompoundDrawablesWithIntrinsicBounds(null, getResources()
				.getDrawable(R.drawable.tab_usercenter), null, null);

		tv_home_first.setTextColor(getResources().getColorStateList(
				R.color.home_bottom_navigation_text));
		tv_home_visit.setTextColor(getResources().getColorStateList(
				R.color.home_bottom_navigation_text));
		tv_home_found.setTextColor(getResources().getColorStateList(
				R.color.home_bottom_navigation_text));
		tv_home_order.setTextColor(getResources().getColorStateList(
				R.color.home_bottom_navigation_text));
		tv_wode.setTextColor(getResources().getColorStateList(
				R.color.home_bottom_navigation_text));
		// iv_services.setImageResource(R.drawable.icon_home_nav_4);
		// tv_services.setTextColor(getResources().getColorStateList(R.color.home_bottom_navigation_text));

	}

	/**
	 * 将所有的Fragment都置为隐藏状态。
	 * 
	 * @param transaction
	 *            用于对Fragment执行操作的事务
	 */
	private void hideFragments(FragmentTransaction transaction) {

		CommonUtil
				.log("-------------------------firstFragment----------------------------"
						+ firstFragment);
		CommonUtil
				.log("-------------------------fragmentVisit----------------------------"
						+ fragmentVisit);
		CommonUtil
				.log("-------------------------fragmentMyOrder----------------------------"
						+ fragmentMyOrder);
		CommonUtil
				.log("-------------------------fragmentPersonalCenter----------------------------"
						+ fragmentPersonalCenter);
		CommonUtil
				.log("-------------------------fragmentPersonalCenter----------------------------"
						+ fragmentPersonalCenter);
		if (firstFragment != null) {
			transaction.hide(firstFragment);
		}

		if (fragmentVisit != null) {
			transaction.hide(fragmentVisit);
		}

		if (findListFragment != null) {
			transaction.hide(findListFragment);
		}

		if (fragmentMyOrder != null) {
			transaction.hide(fragmentMyOrder);
		}

		if (fragmentPersonalCenter != null) {
			transaction.hide(fragmentPersonalCenter);
		}

	}

	private void getVersionInfo() {
		// showCustomCircleProgressDialog(null,
		// getString(R.string.common_toast_net_prompt_submit));
		getVersionInfo = new GetVersionInfo(mContext, mHandler);
		Thread thread = new Thread(getVersionInfo);
		thread.start();
	}

	class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				JSONObject json = getVersionInfo.getOutJson();
				// status 当前版本状态 ANS 1 M 0 最新 1 可用 2 不可用
				// app 客户端类型 ANS 64 M ios 或者android
				// userType App类型 ANS 64 M common 表示普通用户
				// worker 表示技术工作者
				// appVersion 版本号 ANS 64 M 版本号，以区别系统版本号
				// downloadUrl 下载URL地址 ANS 64 M 下载URL地址
				// versionInfo 版本信息 ANS 64 M 版本信息
				// releaseDate 版本发布时间 ANS 64 M 版本发布时间
				// newApp 客户端类型 ANS 64 C ios 或者android(状态不是最新时有)
				// newUserType App类型 ANS 64
				// C common 表示普通用户
				// newAppVersion 版本号 ANS 64 C 版本号(状态不是最新时有)
				// newDownloadUrl 下载URL地址 ANS 64 C 下载URL地址(状态不是最新时有)
				// newVersionInfo 版本信息 ANS 64 C 版本信息(状态不是最新时有)
				// newReleaseDate 版本发布时间 ANS 64 C 版本发布时间(状态不是最新时有)
				CommonUtil.STATUS = json.optString("status", "");
				CommonUtil.NEW_VERSION = json.optString("new_version", "");
				CommonUtil.NEW_DOWNLOADURL = json.optString("new_downloadUrl",
						"");
				CommonUtil.NEW_DOWNLOADURL = json.optString("new_downloadUrl",
						"");
				UpdateManager manager = new UpdateManager(mContext);

				// 版本更新
				if (manager.isUpdate()) {
					manager.checkUpdate();
				} else {
					// 无更新版本
					CommonUtil.log("版本不更新:页面跳转");
					// 页面跳转
					// handleJump();
				}
				break;
			case MsgId.DOWN_DATA_F:
				String MSG = getVersionInfo.getOutMsg();
				int code = getVersionInfo.getOutCode();
				CommonUtil.log("获取version的错误code：" + code);
				// if (code < MsgId.HTTP_APPOINTMENT_ERRORCODE_MIN || code >
				// MsgId.HTTP_APPOINTMENT_ERRORCODE_MAX) {
				// Toast_Util.showToast(mContext, MSG);
				// } else {
				// DialogUtils.showToastMsg(mContext,
				// getString(R.string.common_toast_net_down_data_fail),
				// Toast.LENGTH_SHORT);
				// }
				CommonUtil.log("版本更新--数据下载失败:页面跳转");
				break;
			case MsgId.NET_NOT_CONNECT:
				CustomDialogProgressHandler.getInstance().setCustomDialog(
						mContext,
						getString(R.string.common_toast_net_not_connect), true);
				break;
			default:
				break;
			}
		}
	}

	private void showExitDialog() {
		CustomDialog.Builder builder = new CustomDialog.Builder(this);
		builder.setTitle("提示");
		builder.setMessage(getString(R.string.common_quit_application_message));
		builder.setPositiveButton(getString(R.string.common_confirm),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 设置你的操作事项
						// 如果开发者调用Process.kill或者System.exit之类的方法杀死进程，
						// 请务必在此之前调用MobclickAgent.onKillProcess(Context
						// context)方法，用来保存统计数据。
						MobclickAgent.onKillProcess(mContext);
						// 退出程序
						CommonUtil.saveBooleanOfSharedPreferences(
								HomeActivity.this, "HAS_OPEN_APP", false);// 用于判断点击通知是否需要再次定位
						MyApplication.getInstance().exit();
						// 设置启动模式 singleTask ，清空home以上的activity
						// Intent intent = new Intent();
						// intent.setClass(HomeActivity.this,
						// ExitAcitivty.class);
						// // intent.putExtra("tabIndex", -100);
						// intent.putExtra(Constants.EXITAPP,
						// Constants.EXITAPP);
						// startActivity(intent);
						// finish();

						// 发送通知退出程序--测试
						// Intent intent_exit = new Intent();
						// intent_exit.setAction(Constants.EXIT_APP);
						// sendBroadcast(intent_exit);
					}
				});
		builder.setPositiveButton(getString(R.string.common_confirm),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 设置你的操作事项
						// 如果开发者调用Process.kill或者System.exit之类的方法杀死进程，
						// 请务必在此之前调用MobclickAgent.onKillProcess(Context
						// context)方法，用来保存统计数据。
						MobclickAgent.onKillProcess(mContext);
						// 退出程序
						CommonUtil.saveBooleanOfSharedPreferences(
								HomeActivity.this, "HAS_OPEN_APP", false);// 用于判断点击通知是否需要再次定位
						MyApplication.getInstance().exit();
						// 设置启动模式 singleTask ，清空home以上的activity
						// Intent intent = new Intent();
						// intent.setClass(HomeActivity.this,
						// ExitAcitivty.class);
						// // intent.putExtra("tabIndex", -100);
						// intent.putExtra(Constants.EXITAPP,
						// Constants.EXITAPP);
						// startActivity(intent);
						// finish();

						// 发送通知退出程序--测试
						// Intent intent_exit = new Intent();
						// intent_exit.setAction(Constants.EXIT_APP);
						// sendBroadcast(intent_exit);
					}
				});

		builder.setNegativeButton(getString(R.string.common_cancel),
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();

	}

	@Override
	public void onBackPressed() {
		showExitDialog();
	}

	@Override
	protected void onDestroy() {
//		keepScreenOn(HomeActivity.this, false);
		// 有很多情况下系统会简单的杀死这个Activity的宿主进程而不调用它的onDestroy方法，所以在这个方法中不要做任何有关保留数据或者状态的相关操作。
		if (null != dynamicReceiver) {
			unregisterReceiver(dynamicReceiver);
		}
		// 退出程序 还原app是否打开为false
		CommonUtil.saveBooleanOfSharedPreferences(HomeActivity.this,
				"HAS_OPEN_APP", false);
		// 退出程序 还原app是否打开为false
		CommonUtil.saveBooleanOfSharedPreferences(HomeActivity.this,
				"HAS_OPEN_APP", false);
		super.onDestroy();
	}

	@SuppressLint("NewApi")
	public static boolean checkPermission(Context context, String permission) {
		boolean result = false;

		if (Build.VERSION.SDK_INT >= 23) {
			if (context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
				result = true;
			}
		} else {
			PackageManager pm = context.getPackageManager();

			if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
				result = true;
			}
		}

		return result;
	}

	public static String getDeviceInfo(Context context) {
		try {
			org.json.JSONObject json = new org.json.JSONObject();
			android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);

			String device_id = null;

			if (checkPermission(context, permission.READ_PHONE_STATE)) {
				device_id = tm.getDeviceId();
			}

			android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);

			String mac = wifi.getConnectionInfo().getMacAddress();

			json.put("mac", mac);

			if (TextUtils.isEmpty(device_id)) {
				device_id = mac;
			}

			if (TextUtils.isEmpty(device_id)) {
				device_id = android.provider.Settings.Secure.getString(
						context.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);
			}

			json.put("device_id", device_id);

			return json.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
