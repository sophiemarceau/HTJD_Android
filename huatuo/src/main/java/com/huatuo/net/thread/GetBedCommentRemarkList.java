package com.huatuo.net.thread;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.huatuo.dictionary.MsgId;
import com.huatuo.net.http.ActionResponse;
import com.huatuo.net.http.HttpAgent;
import com.huatuo.util.CommonUtil;

public class GetBedCommentRemarkList implements Runnable {
	private Handler mHandler;
	private Context mContext;
	private String type;
	private ArrayList<JSONObject> BedCommentRemarkList;
	private String outMsgJson = null;
	private int outCode;

	public GetBedCommentRemarkList(Context mContext, Handler mHandler,String type) {

		this.mContext = mContext;
		this.mHandler = mHandler;
		this.type = type;
		BedCommentRemarkList = new ArrayList<JSONObject>();
	}

	@Override
	public void run() {
		try {
			HashMap<String, String> inJsonObject = new HashMap<String, String>();
			inJsonObject.put("type", type);
			CommonUtil.log("请求评价标签上传参数：inJsonObject："+inJsonObject);
			HttpAgent httpAgent = new HttpAgent("tag/getTag", inJsonObject, mContext);
			ActionResponse response = httpAgent.sendRequest(null);
			int code = response.getCode();
			initRsultData(response);
			if (code == 0) {
				// initRsultData(response);
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
		JSONObject bodyJsonObject = actionRespons.getRsbody();
		outMsgJson = actionRespons.getMsg();
		outCode = actionRespons.getCode();

		// 订单列表
		JSONArray BedCommentRemarkListArray = bodyJsonObject.optJSONArray("tagList");
		JSONObject json;
		if (BedCommentRemarkListArray == null || BedCommentRemarkListArray.length() == 0) {
			return;
		} else {
			for (int i = 0; i < BedCommentRemarkListArray.length(); i++) {
				try {
					json = BedCommentRemarkListArray.getJSONObject(i);
				} catch (JSONException e) {
					e.printStackTrace();
					continue;
				}
				BedCommentRemarkList.add(json);
			}
		}
	}

	/** 返回订单列表 */
	public ArrayList<JSONObject> getBedCommentRemarkList() {
		return BedCommentRemarkList;
	}

	public String getOutMsg() {
		return outMsgJson;
	}

	public int getOutCode() {
		return outCode;
	}
}
