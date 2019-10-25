package com.huatuo.bean;

public class StoreItemBean {
//	ID	店铺ID	ANS	64	M	店铺 ID
//	name	店铺名称	ANS	64	M	店铺名称
//	minPrice	店铺最低价格	N	12	M	店铺最低价格，单位分
//	orderCount	店铺订单数量	N	12	M	店铺订单数量
//	score	店铺分数	ANS	64	M	店铺分数，满分为5分
//	distance	距离	ANS	64	M	距离，单位公里
//	introduction	店铺简介	ANS	256	M	店铺简介
//	icon	店铺默认大图	ANS	256	M	店铺默认大图的URL地址
//	isReservable	是否可预约	ANS	64	M	类型 0：否（仅展示），1：是，

	public String  ID = "";//店铺ID	ANS	64	M	店铺 ID
	public String  name = "";//店铺名称	ANS	64	M	店铺名称
	public int  orderCount = 0;//店铺订单数量	N	12	M	店铺订单数量
	public String  icon = "";//店铺默认大图	ANS	256	M	店铺默认大图的URL地址
	public String  score = "";//服务评分	ANS	64	M	项目分数
	public String  distance = "";//距离	ANS	64	M	距离，单位公里
	public String  introduction = "";//店铺简介	ANS	256	M	店铺简介
	public String  isReservable = "";//是否可预约	ANS	64	M	类型 0：否（仅展示），1：是，
	public String  minPrice = "";//店铺最低价格	N	12	M	店铺最低价格，单位分
}
