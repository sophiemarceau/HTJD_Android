package com.huatuo.base;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.huatuo.R;
import com.huatuo.base.BaseFragment;
import com.huatuo.custom_widget.CustomProgressDialog;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.thread.BaseNetPort;
import com.huatuo.util.DialogUtils;

public class BaseNetFragment extends BaseFragment {
	protected Context mContext;
	/** 是否正在加载 */
	protected boolean isLoading;
	protected CustomProgressDialog customDialog;
	protected Handler mHandler;
	protected JSONObject inJson;
	protected JSONObject outJson;
	protected BaseNetPort netport;

	public void netCall(JSONObject inJson, String ServerName) {

		mContext = getActivity();
		mHandler = new MyHandler();

		showCustomCircleProgressDialog(null, getString(R.string.common_toast_net_prompt_submit));
		netport = new BaseNetPort(mContext, mHandler);
		netport.setInJsong(inJson);
		netport.setServerName(ServerName);

		Thread thread = new Thread(netport);
		thread.start();

	};

	public void netCallBack(Message msg) {
		if (msg.what == MsgId.DOWN_DATA_S)
			outJson = netport.getOutJson();
		else
			outJson = null;
	};

	class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			isLoading = false;
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				netCallBack(msg);
				break;
			case MsgId.DOWN_DATA_F:
				closeCustomCircleProgressDialog();
				DialogUtils.showToastMsg(mContext, getString(R.string.common_toast_net_down_data_fail), Toast.LENGTH_SHORT);
				break;
			case MsgId.NET_NOT_CONNECT:
				setCustomDialog(getString(R.string.common_toast_net_not_connect), true);
				break;
			default:
				break;
			}
		}
	}

}