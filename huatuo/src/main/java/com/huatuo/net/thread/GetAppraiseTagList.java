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

public class GetAppraiseTagList implements Runnable {
	private Handler mHandler;
	private Context mContext;
	private ArrayList<JSONObject> tagList;
	private JSONObject outBodyJsonObject = null;
	private HashMap<String, String> inJsonObject = new HashMap<String, String>();

	public GetAppraiseTagList(Context mContext, Handler mHandler,
			HashMap<String, String> inJsonObject) {

		this.mContext = mContext;
		this.mHandler = mHandler;
		this.inJsonObject = inJsonObject;
		this.tagList = new ArrayList<JSONObject>();
	}

	@Override
	public void run() {
		try {

			CommonUtil.logE("获取评价tag列表：inJsonObject：" + inJsonObject);

			HttpAgent httpAgent = new HttpAgent("tag/getTag", inJsonObject,
					mContext);
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
		JSONObject bodyJsonObject = actionRespons.getRsbody();
		outBodyJsonObject = bodyJsonObject;

	}

	public JSONObject getOutJsonObject() {
		return outBodyJsonObject;
	}

	/** 返回tag列表 */
	public ArrayList<JSONObject> getTagList() {
		// 评价tag列表
		if (null != outBodyJsonObject) {
			JSONArray tagListArray = outBodyJsonObject.optJSONArray("tagList");
			JSONObject json;
			if (CommonUtil.NoEmptyArray(tagListArray)) {
				for (int i = 0; i < tagListArray.length(); i++) {
					try {
						json = tagListArray.getJSONObject(i);
					} catch (JSONException e) {
						e.printStackTrace();
						continue;
					}
					tagList.add(json);
				}
			}
		}
		return tagList;
	}

}
