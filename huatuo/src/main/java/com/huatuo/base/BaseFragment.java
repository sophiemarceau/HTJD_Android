package com.huatuo.base;

import com.huatuo.custom_widget.CustomProgressDialog;

import android.os.Handler;
import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {
	private CustomProgressDialog customDialog;

	public Handler mHandler;

	// 离开Fragment时一定要关闭对话框窗口
	public void onPause() {
		super.onPause();
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

		customDialog = CustomProgressDialog.createDialog(getActivity());
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

		customDialog = CustomProgressDialog.createDialog(getActivity());
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
		if( null != customDialog){
			customDialog.setImageView();
			customDialog.setMessage(msg);
			customDialog.setCancelable(true);// 可以用“返回键”取消
		}
	}
}