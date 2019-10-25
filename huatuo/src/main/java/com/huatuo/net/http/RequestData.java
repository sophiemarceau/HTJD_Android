package com.huatuo.net.http;

import java.util.HashMap;

import android.content.Context;
import android.os.Handler;

/**
 * 数据请求工具
 * @author wrz
 *
 */
public class RequestData {
	private static RequestData instance;

	public static RequestData getInstance() {
		if (instance == null) {
			synchronized (RequestData.class) {
				if (instance == null) {
					instance = new RequestData();
				}
			}
		}
		return instance;
	}

	/**
	 * @param mContext
	 * @param handler 
	 * @param url  RequestUrl
	 * @param map  上传参数
	 * @return
	 */
	public RequestRunnable request(Context mContext, Handler handler,
			String url, HashMap<String, String> map) {
		RequestRunnable requestRunnable = new RequestRunnable(mContext,
				handler, url, map);
		Thread thread = new Thread(requestRunnable);
		thread.start();
		return requestRunnable;
	}
}
