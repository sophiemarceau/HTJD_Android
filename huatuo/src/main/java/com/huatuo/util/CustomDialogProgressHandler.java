package com.huatuo.util;

import android.content.Context;

import com.huatuo.base.BaseActivity;
import com.huatuo.custom_widget.CustomProgressDialog;

public class CustomDialogProgressHandler {
	private Context mcontext;
	private CustomProgressDialog customDialog;
	private static CustomDialogProgressHandler instance;
	public static CustomDialogProgressHandler getInstance() {
		if (instance == null) {
			synchronized (CustomDialogProgressHandler.class) {
				if (instance == null) {
					instance = new CustomDialogProgressHandler();
				}
			}
		}

		return instance;
	}
	
	
	// 显示自定义加载对话框
		public CustomProgressDialog showCustomCircleProgressDialog(Context context ,String title, String msg) {
			mcontext = context;
			if (customDialog != null) {
				try {
					customDialog.cancel();
					customDialog = null;
				} catch (Exception e) {
				}
			}

			customDialog = CustomProgressDialog.createDialog(mcontext);
			// dialog.setIndeterminate(false);
			// dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			customDialog.setTitle(title);
			customDialog.setMessage(msg);

			try {
				customDialog.show();
			} catch (Exception e) {
			}
			customDialog.setCanceledOnTouchOutside(false);//设置dilog点击屏幕空白处不消失
			return customDialog;
		}

		// 显示自定义加载对话框
		public CustomProgressDialog showCustomCircleProgressDialog(Context context ,String title, String msg, boolean isCancelable) {
			mcontext = context;
			if (customDialog != null) {
				try {
					customDialog.cancel();
					customDialog = null;
				} catch (Exception e) {
				}
			}

			customDialog = CustomProgressDialog.createDialog(mcontext);
			// dialog.setIndeterminate(false);
			// dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			customDialog.setCancelable(isCancelable);// 是否可用用"返回键"取消
			customDialog.setTitle(title);
			customDialog.setMessage(msg);

			try {
				customDialog.show();
			} catch (Exception e) {
			}
			
			customDialog.setCanceledOnTouchOutside(false);//设置dilog点击屏幕空白处不消失
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

		public void setCustomDialog(Context context,String msg, boolean cancelable) {
			if( null == customDialog || ("").equals(customDialog)){
				   customDialog = CustomProgressDialog.createDialog(context);
				   customDialog.show();
				}
			customDialog.setImageView();
			customDialog.setMessage(msg);
			customDialog.setCancelable(true);// 可以用“返回键”取消
		}
		
}
