package com.huatuo.bean;

import java.util.ArrayList;

/**
 * 秒杀时间轴
 *
 */
public class SecKillListBean {
	public ArrayList<SecKillListItemBean>  timeZoneList = null;//秒杀时间轴列表	JSONArray
	
	public SecKillListBean(){
		timeZoneList = new ArrayList<SecKillListItemBean>();
	}
}
