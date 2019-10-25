package com.huatuo.bean;

public class WorkerItemBean {

	public String  ID = "";//技师ID	ANS	64	M	技师ID
	public String  name = "";//技师姓名	ANS	64	M	技师姓名
	public String  icon = "";//技师Icon	ANS	256	M	技师头像
	public String  score = "";//服务评分	ANS	64	M	项目分数
	public String  distance = "";//距离	ANS	64	M	距离，单位公里
	public String  introduction = "";//技师详细介绍	ANS	64	M	技师详细介绍
	public String  gradeID = "";//技师等级ID	ANS	64	C	技师等级ID
	public String  gradeName = "";//技师等级	ANS	64	M	技师等级
	public String  gender = "";//性别	ANS	2	M	技师性别 男或者女
	public String  isBusy = "";//是否今日满钟	ANS	2	M	1 满钟  2 没有满钟
	public String  orderCount = "";//成单次数	N	64	M	成单次数
	public String  storeID = "";//店铺ID	ANS	64	C	店铺 ID
	public String  storeName = "";//店铺名称	ANS	64	C	店铺名称
	public String  isSelfOwned = "";//技师归属	ANS	4	M	0 门店 1 华佗自营
	
}
