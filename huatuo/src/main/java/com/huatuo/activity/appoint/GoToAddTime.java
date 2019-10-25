package com.huatuo.activity.appoint;

import org.json.JSONException;
import org.json.JSONObject;

import com.huatuo.R;
import com.huatuo.activity.home.HomeActivity;
import com.huatuo.base.MyApplication;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.thread.GetAddTimeInfo;
import com.huatuo.net.thread.GetServiceDetail;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Constants;
import com.huatuo.util.CustomDialogProgressHandler;
import com.huatuo.util.ImageLoader_DisplayImageOptions;
import com.huatuo.util.JsonUtil;
import com.huatuo.util.NumFormatUtil;
import com.huatuo.util.Toast_Util;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

public class GoToAddTime {
	private Activity mActivity;
	private String orderID = "";
	private GetAddTimeInfo getAddTimeInfo;
	private Handler getAddTimeInfo_handler;

	private JSONObject serviceObj = null;
	private String techID = "";
	private static GoToAddTime instance;

	public static GoToAddTime getInstance() {
		if (instance == null) {
			synchronized (GoToAddTime.class) {
				if (instance == null) {
					instance = new GoToAddTime();
				}
			}
		}

		return instance;
	}

	public void goToAddTime(Activity activity, String orderID) {
		this.mActivity = activity;
		this.orderID = orderID;
		getAddTimeInfo();
	}

	/**
	 * 获取项目详情
	 * 
	 * @param workerID
	 * @param servID
	 */
	private void getAddTimeInfo() {
		CustomDialogProgressHandler.getInstance()
				.showCustomCircleProgressDialog(
						mActivity,
						null,
						mActivity.getResources().getString(
								R.string.common_toast_net_prompt_submit));
		getAddTimeInfo_handler = new AddTimeInfo_handler();
		getAddTimeInfo = new GetAddTimeInfo(mActivity, getAddTimeInfo_handler,
				orderID);
		Thread thread = new Thread(getAddTimeInfo);
		thread.start();
	}

	/**
	 * 处理项目详情
	 * 
	 * @author Android开发工程师
	 * 
	 */
	class AddTimeInfo_handler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				CustomDialogProgressHandler.getInstance()
						.closeCustomCircleProgressDialog();
				JSONObject addTimeInfo = getAddTimeInfo.getOutJson();
				if (null != addTimeInfo) {
					jumpToAddTime_Activity(addTimeInfo.toString());
				}

				break;
			case MsgId.DOWN_DATA_F:
				CustomDialogProgressHandler.getInstance()
						.closeCustomCircleProgressDialog();
				break;
			case MsgId.NET_NOT_CONNECT:
				CustomDialogProgressHandler.getInstance().setCustomDialog(
						mActivity,
						mActivity.getResources().getString(
								R.string.common_toast_net_not_connect), true);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 去加钟页面
	 * @param addtimeInfo
	 */
	private void jumpToAddTime_Activity(String addtimeInfo) {
		Intent intent = new Intent();
		intent.setClass(mActivity, AddTime_Activity.class);// 这是加钟服务页面
		intent.putExtra("ADDTIME_INFO", addtimeInfo);
		mActivity.startActivity(intent);
//		mActivity.finish();
	}

}
