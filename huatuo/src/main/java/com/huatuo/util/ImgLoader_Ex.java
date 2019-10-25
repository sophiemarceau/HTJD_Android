package com.huatuo.util;

import java.lang.ref.SoftReference;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;

public class ImgLoader_Ex {
	private PicManager_Ex manager;
	private static final LinkedList<String> CACHE_PIC_KEY = new LinkedList<String>(); // 此对象用来保持Bitmap的回收顺序,保证最后使用的图片被回收

	// 在UI线程里处理 MemoryErrorException
	public ImgLoader_Ex(Context mContext, Handler handler, int imageCompressSize, Map<String, SoftReference<Bitmap>> cache_pic_bitmap) {
		manager = new PicManager_Ex(mContext, handler, imageCompressSize, cache_pic_bitmap);
	}

	public ImgLoader_Ex(Context mContext, int imageCompressSize, Map<String, SoftReference<Bitmap>> cache_pic_bitmap) {
		manager = new PicManager_Ex(mContext, imageCompressSize, cache_pic_bitmap);
	}

	// public ImgLoader_Ex(Context mContext,Handler handler,int
	// imageCompressSize)
	// {
	// manager = new PicManager_Ex(mContext,handler,imageCompressSize);
	// }
	private Handler handler = new Handler();
	private ExecutorService threadPool = Executors.newFixedThreadPool(5);

	public Bitmap loadImgForListView(final String url, final ImgCallback callback) {
		// 先从缓存中读取图片资源
		Bitmap bitmap = null;
		try {
			bitmap = manager.getImgFromCache(url);
			// LogUtil.e("oom", "bitmap="+bitmap);
			// LogUtil.e("oom", "------------------------");
			if (bitmap == null) {
				// 如果图片已经存在于下载队列
				if (CACHE_PIC_KEY.contains(url)) {
					return null;
				} else {
					// 加入下载队列
					CACHE_PIC_KEY.add(url);
					// 开启线程从网络上下载
					threadPool.submit(new Runnable() {// submit方法确保下载是从线程池中的线程执行
								@Override
								public void run() {
									try {
										final Bitmap bitmapFromUrl = manager.getBitMapFromUrl(url);
										if (bitmapFromUrl != null) {
											// manager.writePic2SDCard(bitmapFromUrl,url);
											// //把解码输入流生成的bitmap对象回收
											// if(!bitmapFromUrl.isRecycled())
											// {
											// bitmapFromUrl.recycle();
											// }
											// //LogUtil.e("oom", "下载完成" + url);
											// final Bitmap sdCardBitmap =
											// manager.getImgFromCache(url);

											handler.post(new Runnable() {
												@Override
												public void run() {
													callback.refresh(bitmapFromUrl);
													// 下载完成,删除队列中的url
													CACHE_PIC_KEY.remove(url);
												}
											});
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							});
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	public interface ImgCallback {
		public void refresh(Bitmap bitmap);
	}
}