package com.huatuo.bean;

import android.graphics.drawable.Drawable;

public class ApkInfo {
	private Drawable apkIcon ;
	private String apkPackageName ="";
	private String apkLabel="";
	public Drawable getApkIcon() {
		return apkIcon;
	}
	public void setApkIcon(Drawable apkIcon) {
		this.apkIcon = apkIcon;
	}
	public String getApkPackageName() {
		return apkPackageName;
	}
	public void setApkPackageName(String apkPackageName) {
		this.apkPackageName = apkPackageName;
	}
	public String getApkLabel() {
		return apkLabel;
	}
	public void setApkLabel(String apkLabel) {
		this.apkLabel = apkLabel;
	}

}
