package com.huatuo.bean;

public class SearchAddressObj {
	private String id = "0";
	private String cityCode = "";
	private String district = "";
	private String areaName = "";
	private String lat = "";
	private String lng = "";
	public SearchAddressObj() {
		super();
		// TODO Auto-generated constructor stub
	}
	public SearchAddressObj(String cityCode, String district, String areaName,
			String lat, String lng) {
		super();
		this.cityCode = cityCode;
		this.district = district;
		this.areaName = areaName;
		this.lat = lat;
		this.lng = lng;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCityCode() {
		return cityCode;
	}
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	
}
