package com.huatuo.util;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huatuo.R;

public class Custom_Toast_Collect {
	private Activity mContext;

	private static Custom_Toast_Collect instance;

	public static Custom_Toast_Collect getInstance() {
		if (instance == null) {
			synchronized (Custom_Toast_Collect.class) {
				if (instance == null) {
					instance = new Custom_Toast_Collect();
				}
			}
		}

		return instance;
	}

	public void showToast(int drawableId, String Message,
			boolean isLong) {
		
		LayoutInflater inflater = mContext.getLayoutInflater();
		View layout = inflater.inflate(R.layout.custom_toast_vercial,
				(ViewGroup) mContext.findViewById(R.id.rl_custom_toast));
		ImageView image = (ImageView) layout.findViewById(R.id.toast_icon);
		image.setImageResource(drawableId);
		TextView text = (TextView) layout.findViewById(R.id.toast_message);
		text.setText(Message);
		Toast toast = new Toast(mContext.getApplicationContext());
		toast.setGravity(Gravity.CENTER, 0, 0);
		if (isLong) {
			toast.setDuration(Toast.LENGTH_LONG);
		} else {
			toast.setDuration(Toast.LENGTH_SHORT);
		}
		toast.setView(layout);
		toast.show();
	}
	
	public void handleCollect(Activity activity,boolean isCollect,
			boolean isLong){
		mContext = activity;
		if(isCollect){
			showToast(R.drawable.icon_collect_tip, "已收藏成功", isLong);
		}else {
			showToast(R.drawable.icon_collect_tip, "取消收藏成功", isLong);
		}
		
	}
	
	public void handleCollectIcon(Activity activity,boolean isCollect,
			ImageView iv_collect){
		mContext = activity;
		if(null != iv_collect){
			if(isCollect){
				//已收藏
				iv_collect.setImageResource(R.drawable.icon_sd_favorites_che);
				
			}else {
				//未收藏
				iv_collect.setImageResource(R.drawable.icon_sd_favorites);
				
			}
		}
		
	}
	
	
	
}
