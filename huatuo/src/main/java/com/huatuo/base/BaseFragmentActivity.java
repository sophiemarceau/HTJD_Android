package com.huatuo.base;

import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.huatuo.R;
import com.huatuo.custom_widget.CustomProgressDialog;
import com.huatuo.dictionary.MsgId;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.DialogUtils;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseFragmentActivity extends FragmentActivity {
	protected Context mContext;
	/** 是否正在加载 */
	protected boolean isLoading;
	protected CustomProgressDialog customDialog;
	protected Handler mHandler;
	protected JSONObject inJson;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mContext = this;
		mHandler = new MyHandler();
		inJson = new JSONObject();
		super.onCreate(savedInstanceState);
		initWindowPixel();
	}
	/*
	 * client 接入方 ANDROID sign 签名 deviceId 设备号 version 客户端版本
	 */
	private void initWindowPixel() {
		//客户端版本
		if(TextUtils.isEmpty(CommonUtil.SCREENPIXEL)){
			CommonUtil.initScreen(this);
			CommonUtil.SCREENPIXEL = CommonUtil.WIDTH_SCREEN+"x"+ CommonUtil.HEIGHT_SCREEN;
		}
		
	}
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}


	// 离开Fragment时一定要关闭对话框窗口
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		if (customDialog != null) {
			try {
				customDialog.cancel();
				customDialog = null;
			} catch (Exception e) {
			}
		}
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

		customDialog = CustomProgressDialog.createDialog(mContext);
		// dialog.setIndeterminate(false);
		// dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		customDialog.setTitle(title);
		customDialog.setMessage(msg);

		try {
			customDialog.show();
		} catch (Exception e) {
		}
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

		customDialog = CustomProgressDialog.createDialog(mContext);
		// dialog.setIndeterminate(false);
		// dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		customDialog.setCancelable(isCancelable);// 是否可用用"返回键"取消
		customDialog.setTitle(title);
		customDialog.setMessage(msg);

		try {
			customDialog.show();
		} catch (Exception e) {
		}
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
		customDialog.setImageView();
		customDialog.setMessage(msg);
		customDialog.setCancelable(true);// 可以用“返回键”取消
	}

	public abstract void netCall(JSONObject inJson);

	public abstract void netCallBack(Message msg);

	class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			isLoading = false;
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				netCallBack(msg);
				break;
			case MsgId.DOWN_DATA_F:
				closeCustomCircleProgressDialog();
				DialogUtils.showToastMsg(mContext, getString(R.string.common_toast_net_down_data_fail), Toast.LENGTH_SHORT);
				break;
			case MsgId.NET_NOT_CONNECT:
				setCustomDialog(getString(R.string.common_toast_net_not_connect), true);
				break;
			default:
				break;
			}
		}
	}

}