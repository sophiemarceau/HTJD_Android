package com.huatuo.util;

import android.graphics.Bitmap;

import com.huatuo.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class ImageLoader_DisplayImageOptions {
	public static ImageLoader_DisplayImageOptions instance;

	public static ImageLoader_DisplayImageOptions getInstance() {
		if (instance == null) {
			synchronized (ImageLoader_DisplayImageOptions.class) {
				if (instance == null) {
					instance = new ImageLoader_DisplayImageOptions();
				}
			}
		}

		return instance;
	}
	
	/**
	 * @return
	 */
	public DisplayImageOptions setDefaultFisrtImg() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.img_start_activity)
				.showImageForEmptyUri(R.drawable.img_start_activity)
				.showImageOnFail(R.drawable.img_start_activity)
				.resetViewBeforeLoading(true)
				.cacheInMemory(false)//防止oom
				.cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();
		return options;
	}
	
	public DisplayImageOptions setDefaultBanner() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.icon_banner_default)
				.showImageForEmptyUri(R.drawable.icon_banner_default)
				.showImageOnFail(R.drawable.icon_banner_default)
				.resetViewBeforeLoading(true)
				.cacheInMemory(false)//防止oom
				.cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();
		return options;
	}
	/*options = new DisplayImageOptions.Builder()  
    .showStubImage(R.drawable.ic_stub)          // 设置图片下载期间显示的图片  
    .showImageForEmptyUri(R.drawable.ic_empty)  // 设置图片Uri为空或是错误的时候显示的图片  
    .showImageOnFail(R.drawable.ic_error)       // 设置图片加载或解码过程中发生错误显示的图片      
    .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中  
    .cacheOnDisc(true)                          // 设置下载的图片是否缓存在SD卡中  
    .displayer(new RoundedBitmapDisplayer(20))  // 设置成圆角图片  
    .build();  */
	public DisplayImageOptions setDefaultImageBigImg() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.icon_default_big)
				.showImageForEmptyUri(R.drawable.icon_default_big)
				.showImageOnFail(R.drawable.icon_default_big)
				.resetViewBeforeLoading(true)
				.cacheInMemory(false)//防止oom
				.cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();
		return options;
	}

	public DisplayImageOptions setDefaultImageSmallImg() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.icon_default_small)
		.showImageForEmptyUri(R.drawable.icon_default_small)
		.showImageOnFail(R.drawable.icon_default_small)
		.resetViewBeforeLoading(true)
		.cacheInMemory(false)//防止oom
		.cacheOnDisk(true)
		.imageScaleType(ImageScaleType.EXACTLY)
		.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
		.displayer(new RoundedBitmapDisplayer(6)).build();
		
		return options;
	}

	public DisplayImageOptions setDefaultImageBannerImg() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.icon_default_banner)
		.showImageForEmptyUri(R.drawable.icon_default_banner)
		.showImageOnFail(R.drawable.icon_default_banner)
		.resetViewBeforeLoading(true)
		.cacheInMemory(false)//防止oom
		.cacheOnDisk(true)
		.imageScaleType(ImageScaleType.EXACTLY)
		.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
		.displayer(new FadeInBitmapDisplayer(300)).build();
		return options;
	}
	
	public DisplayImageOptions setDefaultImageTechImg() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.icon_tech_default)
		.showImageForEmptyUri(R.drawable.icon_tech_default)
		.showImageOnFail(R.drawable.icon_tech_default)
		.resetViewBeforeLoading(true)
		.cacheInMemory(false)//防止oom
		.cacheOnDisk(true)
		.imageScaleType(ImageScaleType.EXACTLY)
		.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
		.displayer(new FadeInBitmapDisplayer(300)).build();
		return options;
	}

}
