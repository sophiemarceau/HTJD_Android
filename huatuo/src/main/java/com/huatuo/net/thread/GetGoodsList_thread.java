package com.huatuo.net.thread;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.huatuo.base.MyApplication;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.http.ActionResponse;
import com.huatuo.net.http.HttpAgent;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

/** 获取兑换券列表 */
public class GetGoodsList_thread implements Runnable {
	private Handler mHandler;
	private Context mContext;
	private ArrayList<JSONObject> goodsCardList;
	private HashMap<String, String> injsonMap;
	private String outMsgJson = null;
	private int outCode;

	public GetGoodsList_thread(Context mContext, Handler mHandler,
			HashMap<String, String> injsonMap) {

		this.mContext = mContext;
		this.mHandler = mHandler;
		this.injsonMap = injsonMap;
		goodsCardList = new ArrayList<JSONObject>();

	}

	@Override
	public void run() {
		try {
			// Log.e("获取充值卡列上传参数", "injsonMap---------------->"+injsonMap);

			HttpAgent httpAgent = new HttpAgent("goods/getGoodsList",
					injsonMap, mContext);
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

			e.printStackTrace();
		}
	}

	/** 初始化返回结果数据 */
	private void initRsultData(ActionResponse actionRespons) {
		JSONObject bodyJsonObject = actionRespons.getRsbody();
		outMsgJson = actionRespons.getMsg();
		outCode = actionRespons.getCode();
		// 充值卡
		if (null != bodyJsonObject) {
			JSONArray goodsCardListArray = bodyJsonObject
					.optJSONArray("goodsList");
			JSONObject json;
			if (goodsCardListArray == null || goodsCardListArray.length() == 0) {
				return;
			} else {
				for (int i = 0; i < goodsCardListArray.length(); i++) {
					try {
						json = goodsCardListArray.getJSONObject(i);
					} catch (JSONException e) {
						e.printStackTrace();
						continue;
					}
					goodsCardList.add(json);
				}
			}
		}
	}

	/** 返回充值卡列表 */
	public ArrayList<JSONObject> getGoodsCardList() {
		return goodsCardList;
	}

	public String getOutMsg() {
		return outMsgJson;
	}

	public int getOutCode() {
		return outCode;
	}
}