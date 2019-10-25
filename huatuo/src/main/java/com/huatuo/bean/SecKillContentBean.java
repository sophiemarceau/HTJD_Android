package com.huatuo.bean;

import java.util.ArrayList;

/**
 * 获取时间轴内容列表 ,包括秒杀活动，活动专题，广告表，推荐服务
 * 
 */
public class SecKillContentBean {
	public String currTime = "";// 当前时间 ANS 32 M 结束时间(yyyy-MM-dd HH:mm:ss)
	public ArrayList<SecKillSpecialListItemBean> specialList = null;// specialList
																	// 专题列表JSONArray
	public ArrayList<SecKillActivityListItemBean> activityList = null;// activityList
																		// 活动列表
	public ArrayList<ServiceItemBean> servList = null;// servList 服务列表 JSONArray
	public ArrayList<ADItemBean> adList = null;// adList 广告列表 JSONArray O

	public SecKillContentBean() {
		specialList = new ArrayList<SecKillSpecialListItemBean>();
		activityList = new ArrayList<SecKillActivityListItemBean>();
		servList = new ArrayList<ServiceItemBean>();
		adList = new ArrayList<ADItemBean>();
	}
}
