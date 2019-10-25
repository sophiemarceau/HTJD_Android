package com.huatuo.bean;

public class ServiceItemBean {
//	ID	服务ID	ANS	64	M	服务ID
//	name	服务名称	ANS	64	M	服务名称
//	introduction	服务介绍	ANS	64	M	服务介绍
//	icon	服务大图	ANS	64	M	服务默认大图
//	score	服务评分	ANS	64	M	项目分数
//	storeID	店铺ID	ANS	64	C	店铺 ID
//	storeName	店铺名称	ANS	64	C	店铺名称
//	isSelfOwned	服务归属	ANS	4	M	0 门店 1 华佗自营
//	minPrice	最低价格	ANS	64	M	最低价格
//	isLevel	是否开启等级	N	4	M	是否开启等级(0 未启用， 1启用)
//	marketPrice	市场价格	ANS	64	C	最低价格的市场价格
	public String  ID = "";//服务ID	ANS	64	M	服务ID
	public String  name = "";//服务名称	ANS	64	M	服务名称
	public String  introduction = "";//服务介绍	ANS	64	M	服务介绍
	public String  icon = "";//服务大图	ANS	64	M	服务默认大图
	public String  score = "";//服务评分	ANS	64	M	项目分数
	public String  storeID = "";//店铺ID	ANS	64	C	店铺 ID
	public String  storeName = "";//店铺名称	ANS	64	C	店铺名称
	public String  isSelfOwned = "";//服务归属	ANS	4	M	0 门店 1 华佗自营
	public String  minPrice = "";//最低价格	ANS	64	M	最低价格
	public int  isLevel = -1;//是否开启等级	N	4	M	是否开启等级(0 未启用， 1启用)
	public String  marketPrice = "";//市场价格	ANS	64	C	最低价格的市场价格
}
