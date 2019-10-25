package com.huatuo.citylist;

public class Content {
//	code	城市代码
//	name	城市名称
//	servingArea	服务区域
//	servingStatus	开通状态
//	isNew	是否显示new字样
//	abbreviation 	简称
//	initialCharacter	首字母
	private String initialCharacter;
	private String name;
	private String code;
	private String servingStatus;
	public Content(String initialCharacter, String name, String code,
			String servingStatus) {
		super();
		this.initialCharacter = initialCharacter;
		this.name = name;
		this.code = code;
		this.servingStatus = servingStatus;
	}
	public String getInitialCharacter() {
		return initialCharacter;
	}
	public void setInitialCharacter(String initialCharacter) {
		this.initialCharacter = initialCharacter;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getServingStatus() {
		return servingStatus;
	}
	public void setServingStatus(String servingStatus) {
		this.servingStatus = servingStatus;
	}
	
	
	
	
	
}
