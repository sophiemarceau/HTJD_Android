package com.huatuo.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.huatuo.R;
import com.huatuo.custom_widget.CustomProgressDialog;
import com.huatuo.util.CommonUtil;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends Activity {
	private CustomProgressDialog customDialog;
	private static BaseActivity instance;
	// 返回键处理方式 0 表示正常处理。 1 表示返回首页。2 .表示提示后退出
	private int iRetun_action = 0;

	public static BaseActivity getInstance() {
		if (instance == null) {
			synchronized (BaseActivity.class) {
				if (instance == null) {
					instance = new BaseActivity();
				}
			}
		}

		return instance;
	}

	protected void bindListener() {
		LinearLayout ll_back = (LinearLayout) findViewById(R.id.ll_back1);
		ll_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// CommonUtil.hideKeyboard(BaseActivity.this);
				onBackPressed();
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		initWindowPixel();
	}

	/**
	 * client 接入方 ANDROID sign 签名 deviceId 设备号 version 客户端版本
	 */
	private void initWindowPixel() {
		// 客户端版本
		if (TextUtils.isEmpty(CommonUtil.SCREENPIXEL)) {
			CommonUtil.initScreen(this);
			CommonUtil.SCREENPIXEL = CommonUtil.WIDTH_SCREEN + "x" + CommonUtil.HEIGHT_SCREEN;
		}

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
//		keepScreenOn(BaseActivity.this, true);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
//		keepScreenOn(BaseActivity.this, false);
	}

	// 显示自定义加载对话框
	public CustomProgressDialog showCustomCircleProgressDialog(String title, String msg) {
		if (customDialog != null) {
			try {
				customDialog.cancel();
				customDialog = null;
			} catch (Exception e) {
			}
		}

		customDialog = CustomProgressDialog.createDialog(this);
		// dialog.setIndeterminate(false);
		// dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		customDialog.setTitle(title);
		customDialog.setMessage(msg);

		try {
			customDialog.show();
		} catch (Exception e) {
		}
		customDialog.setCanceledOnTouchOutside(false);// 设置dilog点击屏幕空白处不消失
		return customDialog;
	}

	// 显示自定义加载对话框
	public CustomProgressDialog showCustomCircleProgressDialog(String title, String msg, boolean isCancelable) {
		if (customDialog != null) {
			try {
				customDialog.cancel();
				customDialog = null;
			} catch (Exception e) {
			}
		}

		customDialog = CustomProgressDialog.createDialog(this);
		// dialog.setIndeterminate(false);
		// dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		customDialog.setCancelable(isCancelable);// 是否可用用"返回键"取消
		customDialog.setTitle(title);
		customDialog.setMessage(msg);

		try {
			customDialog.show();
		} catch (Exception e) {
		}

		customDialog.setCanceledOnTouchOutside(false);// 设置dilog点击屏幕空白处不消失
		return customDialog;
	}

	// 关闭自定义加载对话框
	public void closeCustomCircleProgressDialog() {
		if (customDialog != null) {
			try {
				customDialog.cancel();
				customDialog = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setCustomDialog(String msg, boolean cancelable) {
		CommonUtil.log("customDialog:" + customDialog);
		if (null == customDialog || ("").equals(customDialog)) {
			customDialog = CustomProgressDialog.createDialog(this);
			customDialog.show();
		}
		customDialog.setImageView();
		customDialog.setMessage(msg);
		customDialog.setCancelable(true);// 可以用“返回键”取消
	}

}