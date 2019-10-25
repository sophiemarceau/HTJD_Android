package com.huatuo.bean;

import java.util.ArrayList;

/**
 * 发现列表
 * @author wrz
 *
 */
public class FindBean {
	public int  pageCount = 0;//总共页数
	public int  tupleCount = 0;//总记录数
	public ArrayList<FindItemBean>  discoverList = null;//发现列表
	public FindBean() {
		discoverList = new ArrayList<FindItemBean>();
	}

}
