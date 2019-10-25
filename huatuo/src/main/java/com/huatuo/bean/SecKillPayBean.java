package com.huatuo.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class SecKillPayBean implements Serializable{
	public String minPrice = "";
	public String serviceName = "";
	public String serviceIcon = "";
	public String activityID = "";
	public String priceID = "";
	
	public ArrayList<SecKillActivitydescBean> activitydesc = null;// 活动说明 JSONArray
	
	public SecKillPayBean() {
		activitydesc = new ArrayList<SecKillActivitydescBean>();
	}
}
