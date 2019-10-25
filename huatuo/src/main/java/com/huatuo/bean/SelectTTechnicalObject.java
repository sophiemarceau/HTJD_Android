package com.huatuo.bean;

import java.io.Serializable;

public class SelectTTechnicalObject implements Serializable{
	/*servID	服务项目ID
	city	城市
	isService	1表示推荐  2 表示服务过的。空表示全部
	address	用户填的地址
	sex	用户的性别 1表示男，2表示女
	dateTime	预约时间，格式为yyyy-MM-dd HH:mm
	 beltCount	钟数
	longitude	经度
	latitude	纬度
	userID	用户ID
	addressID	用户的地址ID*/
	public String servID;
	public String city;
	public String isService;
	public String address;
	public String sex;
	public String dateTime;
	public String  beltCount;
	public String longitude;
	public String latitude;
	public String addressID;

}
