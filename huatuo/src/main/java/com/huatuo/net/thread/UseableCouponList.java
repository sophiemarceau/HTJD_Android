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
import com.huatuo.util.CommonUtil;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

/** 获取兑换券列表 */
public class UseableCouponList implements Runnable {
	private Handler mHandler;
	private Context mContext;
	private ArrayList<JSONObject> CouponList;
	private JSONObject outJsonObject;
	private HashMap<String, String> injsonMap;
	private String outMsgJson = null;
	private int outCode;

	public UseableCouponList(Context mContext, Handler mHandler,
			HashMap<String, String> injosnMap) {

		this.mContext = mContext;
		this.mHandler = mHandler;
		this.injsonMap = injosnMap;
		CouponList = new ArrayList<JSONObject>();

	}

	@Override
	public void run() {
		try {

			CommonUtil.logE("获取可用优惠券列表 上传参数：injosnMap---------------->"
					+ injsonMap);

			HttpAgent httpAgent = new HttpAgent("market/userCouponList", injsonMap,
					mContext);
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
		outJsonObject = bodyJsonObject;
		outMsgJson = actionRespons.getMsg();
		outCode = actionRespons.getCode();
		
		JSONArray couponListArray = bodyJsonObject.optJSONArray("couponList");
		JSONObject json;
		if (couponListArray == null || couponListArray.length() == 0) {
			return;
		} else {
			for (int i = 0; i < couponListArray.length(); i++) {
				try {
					json = couponListArray.getJSONObject(i);
				} catch (JSONException e) {
					e.printStackTrace();
					continue;
				}
				CouponList.add(json);
			}
		}
	}

	/** 返回优惠券列表列表 */
	public ArrayList<JSONObject> getCouponList() {
		return CouponList;
	}

	/** 返回优惠券对象 */
	public JSONObject getCouponObjecct() {
		return outJsonObject;
	}

	public String getOutMsg() {
		return outMsgJson;
	}

	public int getOutCode() {
		return outCode;
	}

}