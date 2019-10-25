package com.huatuo.util;

import com.huatuo.activity.home.HomeActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class JumpTargetActivityUtil {
	private static JumpTargetActivityUtil instance;
	public static JumpTargetActivityUtil getInstance() {
		if (instance == null) {
			synchronized (JumpTargetActivityUtil.class) {
				if (instance == null) {
					instance = new JumpTargetActivityUtil();
				}
			}
		}

		return instance;
	}
	public void jumpToMyOrderList(Activity activity,String orderStatus){
		Intent intent = new Intent();
		intent.setClass(activity, HomeActivity.class);//订单列表
		Bundle bundle = new Bundle();
		bundle.putInt("tabIndex", 3 );
		bundle.putString(Constants.TAB_ORDER_STATUS, orderStatus);
		intent.putExtras(bundle);
		activity.startActivity(intent);
		activity.finish();
	}
	
	/**
	 * 跳转到home指定tab
	 * @param context
	 * @param tab 
	 */
	public void jumpToHomeActivity(Activity activity,int tab){
		//记录一下是 app处于打开状态
		CommonUtil.saveBooleanOfSharedPreferences(activity, "HAS_OPEN_APP",true);
		Intent intent = new Intent();
		intent.setClass(activity, HomeActivity.class);//首页面
		Bundle bundle = new Bundle();
		bundle.putInt("tabIndex", tab );
		intent.putExtras(bundle);
		activity.startActivity(intent);
		activity.finish();
	}
}
