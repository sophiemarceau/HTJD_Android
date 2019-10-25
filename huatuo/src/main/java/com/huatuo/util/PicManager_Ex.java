package com.huatuo.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;

public class PicManager_Ex {
	// private LinkedList<String> cache_pic_key; //
	// 此对象用来保持Bitmap的回收顺序,保证最后使用的图片被回收
	private Map<String, SoftReference<Bitmap>> cache_pic_bitmap; // 缓存Bitmap,通过图片路径
	private static final byte[] LOCKED = new byte[0];
	// private static int CACHE_SIZE = 50; // 缓存图片数量
	private Context mContext;
	private int imageCompressSize;
	private int counter = 0;
	private Handler handler;

	public PicManager_Ex(Context mContext, Handler handler, int imageCompressSize, Map<String, SoftReference<Bitmap>> cache_pic_bitmap) {
		this.mContext = mContext;
		if (cache_pic_bitmap != null) {
			this.cache_pic_bitmap = cache_pic_bitmap;
		} else {
			this.cache_pic_bitmap = new LinkedHashMap<String, SoftReference<Bitmap>>();
		}
		this.handler = handler;
		// 如果图片压缩尺寸没有提供,则为默认
		if (imageCompressSize == 0) {
			// 计算图片压缩尺寸
			// int imageWidth =
			// mContext.getResources().getDimensionPixelSize(R.dimen.listView_width);
			this.imageCompressSize = 300 * 300;
		} else {
			this.imageCompressSize = imageCompressSize;
		}

	}

	public PicManager_Ex(Context mContext, int imageCompressSize, Map<String, SoftReference<Bitmap>> cache_pic_bitmap) {
		this.mContext = mContext;
		this.cache_pic_bitmap = cache_pic_bitmap;
		// 如果图片压缩尺寸没有提供,则为默认
		if (imageCompressSize == 0) {
			// 计算图片压缩尺寸
			// int imageWidth =
			// mContext.getResources().getDimensionPixelSize(R.dimen.listView_width);
			this.imageCompressSize = 300 * 300;
		} else {
			this.imageCompressSize = imageCompressSize;
		}

	}

	public PicManager_Ex(Context mContext, Handler handler, int imageCompressSize) {
		this.mContext = mContext;
		this.handler = handler;
		// 如果图片压缩尺寸没有提供,则为默认
		if (imageCompressSize == 0) {
			// 计算图片压缩尺寸
			// int imageWidth =
			// mContext.getResources().getDimensionPixelSize(R.dimen.listView_width);
			this.imageCompressSize = 300 * 300;
		} else {
			this.imageCompressSize = imageCompressSize;
		}

	}

	/**
	 * 从缓存中读取(内存+SD卡)
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public Bitmap getImgFromCache(String url) throws Exception {
		Bitmap bitmap = null;
		String key = url;
		try {
			// LogUtil.e("oom", "缓存大小=" + cache_pic_bitmap.size());
			Iterator iterator = cache_pic_bitmap.keySet().iterator();
			int iHave = 0;
			while (iterator.hasNext()) {
				String keyString = (String) iterator.next();
				SoftReference<Bitmap> sr = cache_pic_bitmap.get(keyString);
				if (sr.get() != null) {
					iHave++;
					// //LogUtil.e("oom", "有效缓存"+iHave+"---url"+keyString);
				}
			}
			// LogUtil.e("oom", "有效缓存大小=" + iHave);
			// if(cache_pic_bitmap.size() > 50)
			// {
			// Iterator iterator = cache_pic_bitmap.keySet().iterator();
			// while(iterator.hasNext())
			// {
			// String set_key = (String)iterator.next();
			// SoftReference<Bitmap> sr = cache_pic_bitmap.get(set_key);
			// if(sr.get() == null )
			// {
			// //LogUtil.e("oom", "缓存已经清除" + cache_pic_bitmap.size() );
			// cache_pic_bitmap.remove(set_key);
			// }
			// }
			// }
			// 从内存中读取
			if (cache_pic_bitmap.containsKey(key)) {
				synchronized (LOCKED) {
					SoftReference<Bitmap> sr = cache_pic_bitmap.get(key);
					if (sr.get() != null) {
						bitmap = (Bitmap) sr.get();
						if (bitmap != null && !bitmap.isRecycled()) {
							// LogUtil.e("oom", "内存读取 OK " + key );
							return bitmap;
						}
					} else {
						// LogUtil.e("oom", "内存清空，重新加载  " + key );
					}
				}
			} else {
				// LogUtil.e("oom", "没有加载，现加载  " + key );
			}
			bitmap = getBitMapFromSDCard(url);
			if (bitmap != null) {
				// cache_pic_key.addFirst(key);
				// 将图片保存进内存中
				cache_pic_bitmap.put(key, new SoftReference<Bitmap>(bitmap));
			}
		} catch (OutOfMemoryError e) {
			// 异常处理
			handler.sendEmptyMessage(8888);
		}
		return bitmap;
	}

	/**
	 * 从网络上下载
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	// public Bitmap getBitMapFromUrl(String url) throws IOException
	// {
	// Bitmap bitmap = null;
	// InputStream is = null;
	//
	// HttpURLConnection conn = (HttpURLConnection)new
	// URL(url).openConnection();
	// conn.setConnectTimeout(5 * 1000);
	// conn.setRequestMethod("GET");
	// if(conn.getResponseCode() == 200)
	// {
	// //LogUtil.e("oom", "下载开始执行" + url);
	// is = conn.getInputStream();
	// bitmap = BitmapFactory.decodeStream(is);
	// return bitmap;
	// }
	// return bitmap;
	// }
	/**
	 * 从网络上下载
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public Bitmap getBitMapFromUrl(String url) {
		// LogUtil.e("loading", "url="+url);
		Bitmap bitmap = null;
		File tmpFile = null;
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setConnectTimeout(5 * 1000);
			conn.setReadTimeout(5 * 1000);
			conn.setRequestMethod("GET");
			if (conn.getResponseCode() == 200) {
				tmpFile = new File(FileUtil.getMD5ImageFilePath(mContext, url) + ".tmp");
				// 根据文件路径名(包括临时文件名)创建输出流
				fos = new FileOutputStream(tmpFile);
				is = conn.getInputStream();
				byte[] buf = new byte[1024];
				int ch = -1;
				while ((ch = is.read(buf)) != -1) {
					fos.write(buf, 0, ch);
				}
				// 如果图片完全下载完成,则重命名图片文件名

				tmpFile.renameTo(new File(FileUtil.getMD5ImageFilePath(mContext, url) + ".pix"));
				// 从SD卡中读取图片
				// bitmap = getBitMapFromSDCard(url);
				bitmap = getImgFromCache(url);
			}
		}// 文件下载失败,删除临时文件(文件不完整)
		catch (Exception e) {
			e.printStackTrace();
			if (tmpFile != null && tmpFile.exists()) {
				tmpFile.delete();
			}
			return null;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return bitmap;
	}

	/**
	 * 从文件中读取,返回的bitmap对象为压缩后的对象
	 * 
	 * @return
	 * @throws Exception
	 */
	private Bitmap getBitMapFromSDCard(String url) throws Exception {
		String filePath = FileUtil.getMD5ImageFilePath(mContext, url) + ".pix";
		// 压缩图片,-1代表不压缩图片
		Bitmap bitmap = BitmapUtil.getCompressImage(filePath, imageCompressSize);
		// FileInputStream fis = mContext.openFileInput(filename);
		// bitmap = BitmapFactory.decodeFile(filePath);
		if (bitmap != null) {
			// LogUtil.e("oom", "SD卡读取" + counter++ );
		}
		return bitmap;
	}

	/**
	 * 将图片写入sdcard中
	 * 
	 * @param bitmap
	 * @param url
	 */
	public void writePic2SDCard(Bitmap bitmap, String url) {
		String filename = FileUtil.getMD5ImageFilePath(mContext, url);

		FileOutputStream fos = null;
		ByteArrayInputStream bis = null;
		try {
			fos = new FileOutputStream(filename);
			byte[] bitmapByte = BitmapUtil.bitmap2Byte(bitmap);
			bis = new ByteArrayInputStream(bitmapByte);
			int len = 0;
			byte[] b = new byte[bis.available()];
			while ((len = bis.read(b)) != -1) {
				fos.write(b, 0, len);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 释放bitmapCache所有bitmap资源
	 * 
	 * @param
	 */
	public void freeAllBitmap() {
		if (cache_pic_bitmap == null || cache_pic_bitmap.size() == 0) {
			return;
		}

		SoftReference<Bitmap> delBitmap;
		Iterator<String> iterator = cache_pic_bitmap.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			delBitmap = cache_pic_bitmap.get(key);
			if (delBitmap != null && !delBitmap.get().isRecycled()) {
				iterator.remove();
				cache_pic_bitmap.remove(key);
				delBitmap.get().recycle();
				delBitmap = null;
			} else {
				iterator.remove();
			}
		}
	}
}