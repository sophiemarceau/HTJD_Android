package com.huatuo.net.http;

import android.content.Context;

/**
 * 网络数据获取后 的 回调 接口
 * 
 * @author ftc
 * @version 创建时间：2014-8-4 上午11:20:40
 */
public interface ActionCallbackInterFace {
	/**
	 * 网络数据获取后 的 回调 接口,实现接口完成 数据操作
	 * 
	 * @param actionrespons
	 */
	public void executeresult(ActionResponse actionrespons, Context context);
}