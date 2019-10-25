package com.huatuo.bean;

import java.util.ArrayList;

/**
 * 获取时间轴内容列表 ,包括秒杀活动，活动专题，广告表，推荐服务
 * 
 */
public class SecKillActivityListItemBean {
	public String ID = "";// 秒杀活动ID
	public String name = "";// 秒杀活动名称
	public String servID = "";// 服务ID
	public String servName = "";// 服务名称
	public String icon = "";// 服务Icon
	public String storeID = "";// 店铺ID ANS
	public String storeName = "";// 属店铺名称 ANS
	public String address = "";// 店铺地址 ANS
	public String isLevel = "";// 是否开启等级 N
	public int stock = 0;// 库存 N
	public String minPrice = "";// 最低价格 ANS 64 C
	public String marketPrice = "";// 市场价格 ANS 64 C
	public ArrayList<SecKillActivitydescBean> activitydesc = null;// 活动说明 JSONArray
	public ArrayList<SecKillActivityGradeBean> servLevelList = null;// 服务价格等级列表 JSONArray
	public SecKillActivityListItemBean() {
		activitydesc = new ArrayList<SecKillActivitydescBean>();
		servLevelList = new ArrayList<SecKillActivityGradeBean>();
	}
}
