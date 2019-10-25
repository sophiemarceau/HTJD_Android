package com.huatuo.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.huatuo.R;
import com.huatuo.activity.home.HomeActivity;
import com.huatuo.custom_widget.CustomDialog;

public class CallUtil {
	public static void showCallDialog(final Context mContext, final String phoneNumber) {
//		Log.e("CallUtil", "PhoneType =" + HomeActivity.PhoneType);
		if (HomeActivity.PhoneType == TelephonyManager.PHONE_TYPE_NONE) {
//			Toast.makeText(mContext, "抱歉，您的设备无法拨打电话。", Toast.LENGTH_SHORT).show();
		} else {
			if (null != phoneNumber && !"".equals(phoneNumber)) {
				CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
				builder.setTitle("提示");
				builder.setMessage("您确定要拨打电话：" + phoneNumber);
				builder.setPositiveButton(mContext.getResources().getString(R.string.common_confirm), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 设置你的操作事项
						Intent intent = new Intent();
						intent.setAction(Intent.ACTION_DIAL); // android.intent.action.DIAL
						intent.setData(Uri.parse("tel:" + phoneNumber));
						mContext.startActivity(intent);
					}
				});

				builder.setNegativeButton(mContext.getResources().getString(R.string.common_cancel), new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

				builder.create().show();
			} else {
				Toast.makeText(mContext, "抱歉，该店铺未提供电话。", Toast.LENGTH_SHORT).show();
			}
		}
	}

}
