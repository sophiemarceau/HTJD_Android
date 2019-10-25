package com.huatuo.bean;

import com.umeng.socialize.media.UMImage;

public class ShareObj {
	private UMImage image;
	private String targetUrl;
	private String title;
	private String content;
	
	public ShareObj() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getTargetUrl() {
		return targetUrl;
	}
	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public ShareObj(UMImage image, String targetUrl, String title,
			String content) {
		super();
		this.image = image;
		this.targetUrl = targetUrl;
		this.title = title;
		this.content = content;
	}
	public UMImage getImage() {
		return image;
	}
	public void setImage(UMImage image) {
		this.image = image;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

}
