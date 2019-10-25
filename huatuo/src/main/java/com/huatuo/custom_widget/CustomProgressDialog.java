package com.huatuo.custom_widget;

import com.huatuo.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CustomProgressDialog extends Dialog {
	private Context context = null;

	private static CustomProgressDialog customProgressDialog = null;

	public CustomProgressDialog(Context context) {
		super(context);
		this.context = context;
	}

	public CustomProgressDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	public static CustomProgressDialog createDialog(Context context) {
		
		customProgressDialog = new CustomProgressDialog(context,
				R.style.CustomProgressDialog);
		customProgressDialog.setContentView(R.layout.customprogressdialog);
		// customProgressDialog.setCancelable(false);// 不可以用“返回键”取消
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		// ImageView imageView = (ImageView)
		// customProgressDialog.findViewById(R.id.iv_loading);
		//
		// // 加载动画
		// Animation hyperspaceJumpAnimation =
		// AnimationUtils.loadAnimation(context, R.anim.loading_animation);
		// // 使用ImageView显示动画
		// imageView.startAnimation(hyperspaceJumpAnimation);
		return customProgressDialog;
	}

	// public void onWindowFocusChanged(boolean hasFocus)
	// {
	//
	// if (customProgressDialog == null)
	// {
	// return;
	// }
	//
	//
	// ImageView imageView = (ImageView)
	// customProgressDialog.findViewById(R.id.loadingImageView);
	//
	// // 加载动画
	// Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(context,
	// R.anim.loading_animation);
	// // 使用ImageView显示动画
	// imageView.startAnimation(hyperspaceJumpAnimation);
	// // AnimationDrawable animationDrawable = (AnimationDrawable)
	// imageView.getBackground();
	// //
	// // animationDrawable.start();
	//
	// }
	public void setImageView() {
		RelativeLayout rl_load = (RelativeLayout) customProgressDialog
				.findViewById(R.id.rl_load);
		// ImageView iv_loading = (ImageView)
		// customProgressDialog.findViewById(R.id.iv_loading);
		// iv_loading.clearAnimation();
		rl_load.setVisibility(View.GONE);
		ImageView iv_net_fail = (ImageView) customProgressDialog
				.findViewById(R.id.iv_net_fail);
		iv_net_fail.setVisibility(View.VISIBLE);
	}

	/**
	 * 
	 * 
	 * 
	 * [Summary]
	 * 
	 * setTitile 标题
	 * 
	 * @param strTitle
	 * 
	 * @return
	 * 
	 * 
	 */

	public CustomProgressDialog setTitile(String strTitle) {

		return customProgressDialog;

	}

	/**
	 * 
	 * 
	 * 
	 * [Summary]
	 * 
	 * setMessage 提示内容
	 * 
	 * @param strMessage
	 * 
	 * @return
	 * 
	 * 
	 */

	public CustomProgressDialog setMessage(String strMessage) {

		TextView tvMsg = (TextView) customProgressDialog
				.findViewById(R.id.id_tv_loadingmsg);

		if (tvMsg != null) {

			tvMsg.setText(strMessage);

		}

		return customProgressDialog;

	}

}
