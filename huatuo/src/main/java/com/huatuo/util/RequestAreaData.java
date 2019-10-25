package com.huatuo.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.huatuo.base.MyApplication;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.thread.GetAreaJson;

public class RequestAreaData {
	private static RequestAreaData instance;
	private GetAreaJson getAreaJson;
	private MyHandler mHandler;
	private Context mContext;
	public synchronized static RequestAreaData getInstance() {
		if(instance == null){
			instance = new RequestAreaData();
		}
		return instance;
	}
	
	/* 从网络获取数据 */
	public void initJsonData(Context context) {
		mContext = context;
		mHandler = new MyHandler();
		getAreaJson = new GetAreaJson(mContext, mHandler);
		Thread t = new Thread(getAreaJson);
		t.start();
	}
	
	class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// isLoading = false;
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				// closeCustomCircleProgressDialog();
				MyApplication.outObj = getAreaJson.getOutJson();
				HandleAddress.initData();
//				Log.e("AddressListActivity", "outObj------------>" + outObj);
				break;
			case MsgId.DOWN_DATA_F:
				// closeCustomCircleProgressDialog();
//				DialogUtils.showToastMsg(mContext, mContext.getResources().getString(R.string.common_toast_net_down_data_fail), Toast.LENGTH_SHORT);
				break;
			case MsgId.NET_NOT_CONNECT:
//				CustomDialogProgressHandler.getInstance().setCustomDialog(mContext,mContext.getResources().getString(R.string.common_toast_net_not_connect), true);
				break;
			default:
				break;
			}
		}
	}
}
