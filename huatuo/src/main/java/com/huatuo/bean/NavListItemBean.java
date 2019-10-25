package com.huatuo.bean;

/**
 * 查询分类导航item
 *
 */
public class NavListItemBean {
	public String  ID = "";//分类导航ID	ANS	64	M	分类导航ID
	public String  name = "";//分类导航名称	ANS	128	M	分类导航名称
	public String  type = "";//标签类型	ANS	4	M	0:普通导航,1:附近,2: 收藏,3：秒杀
	public String  contextType = "";//标签内容类型	ANS	4	M	0 门店，1服务，2技师
	public String  isDefault = "";//是否默认标签	ANS	4	M	非默认0  默认1
}
