package com.huatuo.bean;

import java.util.ArrayList;

/**
 * 查询分类导航
 * @author wrz
 *
 */
public class NavListBean {
	public ArrayList<NavListItemBean>  navigationList = null;//分类导航列表	JsonArray		M	分类导航列表

	public NavListBean(){
		navigationList = new ArrayList<NavListItemBean>();
	}
}
