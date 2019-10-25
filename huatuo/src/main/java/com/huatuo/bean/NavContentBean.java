package com.huatuo.bean;

import java.util.ArrayList;

public class NavContentBean {
	
	public int  pageCount = 0;//总共页数	N	10	O	记录数
	public int  tupleCount = 0;//总记录数	N	10	O	记录数
	public String  navType = null;//navType	导航类型	ANS	10	M	0 门店 1 项目 2 技师
	public ArrayList<ADItemBean>  adList = null;//服务介绍	ANS	64	M	服务介绍
	public ArrayList<StoreItemBean>  storeList = null;//门店列表	JSONArray	
	public ArrayList<ServiceItemBean>  serviceList = null;//项目列表	JSONArray
	public ArrayList<WorkerItemBean>  skillList = null;//workerList	技师列表	JSONArray

	public NavContentBean(){
		adList = new ArrayList<ADItemBean>();
		storeList = new ArrayList<StoreItemBean>();
		serviceList = new ArrayList<ServiceItemBean>();
		skillList = new ArrayList<WorkerItemBean>();
	}
}
