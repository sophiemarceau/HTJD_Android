package com.huatuo.util;

import android.app.Dialog;
import android.content.Context;
import android.view.WindowManager;

/**
 * 自定义Dialog大小
 * @author Administrator
 *
 */
public class DialogSizeUtil {
	// 屏幕宽度
	static WindowManager windowManager;
	static int sw;
	static int sh;
	public static void setDialogSize(Context context, Dialog dialog){
		windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE); // 屏幕宽度
		sw = windowManager.getDefaultDisplay().getWidth();
		// 屏幕高度
		sh = windowManager.getDefaultDisplay().getHeight();
		
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.width = sw/7*6;// 定义宽度
		lp.height = sh/7*5;// 定义宽度
		dialog.getWindow().setAttributes(lp);

	}

	public static void setDialogSize2(Context context, Dialog dialog){
		windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE); // 屏幕宽度
		sw = windowManager.getDefaultDisplay().getWidth();
		// 屏幕高度
		sh = windowManager.getDefaultDisplay().getHeight();
		
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.width = sw/7*7;// 定义宽度
		dialog.getWindow().setAttributes(lp);

	}
	
	
	public static void setDialogSize3(Context context, Dialog dialog){
		windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE); // 屏幕宽度
		sw = windowManager.getDefaultDisplay().getWidth();
		// 屏幕高度
		sh = windowManager.getDefaultDisplay().getHeight();
		
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.width = (int) (sw*0.8);// 定义宽度
		dialog.getWindow().setAttributes(lp);

	}
	

}
