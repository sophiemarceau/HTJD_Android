package com.huatuo.custom_widget;

import java.util.Timer;
import java.util.TimerTask;

import com.huatuo.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

public class CustomToast {
	private static final int MESSAGE_TIMEOUT = 2;
	private boolean mShowTime;
	private boolean mIsShow;
	private WindowManager mWdm;
	private WindowManager.LayoutParams mParams;
	private View mToastView;
	private Timer mTimer;
	private WorkerHandler mHandler;
	private boolean Isshow = false;
	private static Activity mActivity;

//	private CustomToast(Activity context, String text, boolean showTime) {
//		mHandler = new WorkerHandler();
//		mShowTime = showTime;// 记录Toast的显示长短类型
//		mIsShow = false;// 记录当前Toast的内容是否已经在显示
//		mWdm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//		// 通过Toast实例获取当前android系统的默认Toast的View布局
//		mToastView = Toast.makeText(context, text, Toast.LENGTH_SHORT)
//				.getView();
//		mTimer = new Timer();
//		// 设置布局参数
//		setParams();
//	}

	/**
	 * @param context
	 * @param text
	 * @param showTime
	 *            true :长时间 false:短时间
	 * @return
	 */
//	public static CustomToast makeText(Activity context, String text,
//			boolean showTime) {
//		mActivity = context;
//		CustomToast customToast = new CustomToast(mActivity, text, showTime);
//		return customToast;
//	}

	private void setParams() {
		mParams = new WindowManager.LayoutParams();
		mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.format = PixelFormat.TRANSLUCENT;
		mParams.windowAnimations = R.style.anim_view;// 设置进入退出动画效果
		mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
		mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
		mParams.gravity = Gravity.CENTER_HORIZONTAL;
		mParams.y = 250;
	}

	public void show() {
		if(!Isshow){
			Isshow = true;
			mWdm.addView(mToastView, mParams);
			mHandler.sendEmptyMessageDelayed(MESSAGE_TIMEOUT, (long) (3 * 1000));
		}
	}

	public void cancel() {
		if(mActivity!=null && !mActivity.isFinishing()){
		  mWdm.removeView(mToastView);
		}
	}

	private class WorkerHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_TIMEOUT:
				Isshow = false;
				cancel();
				break;
			}
		}
	}
}
