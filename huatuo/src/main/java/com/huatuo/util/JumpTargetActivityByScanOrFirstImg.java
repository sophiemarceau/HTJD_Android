package com.huatuo.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.huatuo.activity.ad.AD_ServiceListActivity;
import com.huatuo.activity.ad.AD_StoreListActivity;
import com.huatuo.activity.ad.AD_TechListActivity;
import com.huatuo.activity.project.ProjectDetailActivity;
import com.huatuo.activity.store.StoreDetail_Aty;
import com.huatuo.activity.technician.TechnicianDetail;

public class JumpTargetActivityByScanOrFirstImg {
	private static JumpTargetActivityByScanOrFirstImg instance;
	public static JumpTargetActivityByScanOrFirstImg getInstance() {
		if (instance == null) {
			synchronized (JumpTargetActivityByScanOrFirstImg.class) {
				if (instance == null) {
					instance = new JumpTargetActivityByScanOrFirstImg();
				}
			}
		}

		return instance;
	}
	/**
	 * 跳转到home指定tab
	 * @param context
	 * @param tab 
	 */
	public boolean jump( Context context,String resultString){
		
		CommonUtil.logE("URL------------->" + resultString);
				String R = "";
				int k = resultString.length();
				for (int i = 0; i < resultString.length(); i++) {
					if (resultString.substring(i, i + 1).equals("#")) {
						R = resultString.substring(i + 1, k).trim();
					}
				}
				String type = "";
				String L = "";
				int l = R.length();
				for (int i = 0; i < R.length(); i++) {
					if (R.substring(i, i + 1).equals("?")) {
						type = R.substring(0, i).trim();
						L = R.substring(i + 1, l).trim();
					}
				}
				String id = "";
				l = L.length();
				for (int i = 0; i < L.length(); i++) {
					if (L.substring(i, i + 1).equals("=")) {
						id = L.substring(i + 1, l).trim();
					}
				}

				if ("shop-detail".equals(type)) {
					Intent intent = new Intent(context, StoreDetail_Aty.class);
					Bundle bundle = new Bundle();
					bundle.putString("ID", id);
					bundle.putBoolean("push", true);//跟app关闭状态的推送通知操作一样
					intent.putExtras(bundle);
					context.startActivity(intent);
					return true;
				} else if ("project-detail".equals(type)) {
					Intent intent = new Intent(context, ProjectDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("ID", id);
					bundle.putBoolean("push", true);
					intent.putExtras(bundle);
					context.startActivity(intent);
					return true;
				} else if ("pt-detail".equals(type)) {
					Intent intent = new Intent(context, TechnicianDetail.class);
					Bundle bundle = new Bundle();
					bundle.putString("ID", id);
					bundle.putBoolean("push", true);
					intent.putExtras(bundle);
					context.startActivity(intent);
					return true;
				} else if ("shop-topic".equals(type)) {
					Intent intent = new Intent(context, AD_StoreListActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("ID", id);
					bundle.putBoolean("push", true);
					intent.putExtras(bundle);
					context.startActivity(intent);
					return true;
				} else if ("project-topic".equals(type)) {
					Intent intent = new Intent(context, AD_ServiceListActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("ID", id);
					bundle.putBoolean("push", true);
					intent.putExtras(bundle);
					context.startActivity(intent);
					return true;
				} else if ("pt-topic".equals(type)) {
					Intent intent = new Intent(context, AD_TechListActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("ID", id);
					bundle.putBoolean("push", true);
					intent.putExtras(bundle);
					context.startActivity(intent);
					return true;
				} else {
					Toast_Util.showToastOfLONG(context, "图片连接异常");
					return false;
				}
	}
}
