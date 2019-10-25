package com.huatuo.net.thread;

import java.util.HashMap;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.huatuo.base.MyApplication;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.http.ActionResponse;
import com.huatuo.net.http.HttpAgent;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.LogUtil;

public class GetVersionInfo implements Runnable {
	private Handler mHandler;
	private Context mContext;
	private JSONObject outJson;
	private int outCode;
	private String outMsg = null;

	public GetVersionInfo(Context mContext, Handler mHandler) {
		this.mContext = mContext;
		this.mHandler = mHandler;
	}

	@Override
	public void run() {
		try {
			HashMap<String, String> inJsonObject = new HashMap<String, String>();
			inJsonObject.put("ct", "android");
			inJsonObject.put("type", "user");
			inJsonObject.put("version", CommonUtil.APPVERSION);
			CommonUtil.logE("版本更新提交参数------------>" +  inJsonObject);
			HttpAgent httpAgent = new HttpAgent("cms/getVersionInfo", inJsonObject, mContext);
			ActionResponse response = httpAgent.sendRequest(null);
			int code = response.getCode();
			initRsultData(response);
			if (code == 0) {
				mHandler.sendEmptyMessage(MsgId.DOWN_DATA_S);
			} else if (code == MsgId.NET_NOT_CONNECT) {
				mHandler.sendEmptyMessage(MsgId.NET_NOT_CONNECT);
			} else {
				mHandler.sendEmptyMessage(MsgId.DOWN_DATA_F);
			}
		} catch (Exception e) {
			// LogUtil.e(Tag, "LoginInvokeItem run" + e.getMessage());
			e.printStackTrace();
		}
	}

	/** 初始化返回结果数据 */
	private void initRsultData(ActionResponse actionRespons) {
		outJson = actionRespons.getRsbody();
		outCode = actionRespons.getCode();
		outMsg = actionRespons.getMsg();
	}

	/** 返回 */
	public JSONObject getOutJson() {
		return outJson;
	}

	public String getOutMsg() {
		return outMsg;
	}

	public int getOutCode() {
		return outCode;
	}
}
