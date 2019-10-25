package com.huatuo.bean;

import java.util.ArrayList;

/**
 * 专项
 * 
 */
public class SecKilSpecialBean {
	public String icon = "";// 秒杀专项头部图片
	public String name = "";// 秒杀专项题目
	public ArrayList<SecKillActivitydescBean> featureDesc = null;// 服务介绍
	public ArrayList<SecKillActivityListItemBean> activityList = null;// 活动列表JSONArray
	
	public SecKilSpecialBean() {
		featureDesc = new ArrayList<SecKillActivitydescBean>();
		activityList = new ArrayList<SecKillActivityListItemBean>();
	}
}
