package com.huatuo.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

public class BitmapUtil {
	/**
	 * 质量压缩 ,缩放比例
	 * */
	private static final int QUALITY_QUALITY = 90;
	/**
	 * 比例压缩 ,宽度
	 * */
	private static final float MAX_WIDTH = 480f;
	/**
	 * 比例压缩 ,缩放比例
	 * */
	private static final float MAX_HEIGHT = 800f;

	/**
	 * <b>图片按比例大小压缩方法（根据路径获取图片并压缩）</b><br/>
	 * 该方法就是对Bitmap形式的图片进行压缩, 也就是通过设置采样率, 减少Bitmap的像素, 从而减少了它所占用的内存
	 * 
	 * @param filePath
	 *            图片文件路径
	 * @return byte[] 经过压缩之后的字节数组
	 * */
	public static byte[] compressImageByScaleReturnByte(String filePath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(filePath, newOpts);// 此时返回bm为空

		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// Log.e("deesha", "开始压缩图片"+filePath
		// +"\n文件大小="+new File(filePath).length()/1024 +"KB");
		//
		// Log.e("deesha", "压缩前宽="+w
		// +"---压缩前高="+h);
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float ww = MAX_WIDTH;// 这里设置宽度为480f
		float hh = MAX_HEIGHT;// 这里设置高度为800f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		// if (w > ww)
		// {//如果宽度大的话根据宽度固定大小缩放
		// Log.e(TAG, "原始缩放比=" + Math.ceil(newOpts.outWidth / ww));
		// be = (int) Math.ceil(newOpts.outWidth / ww);
		// }

		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0) {
			be = 1;
		}
		// Log.e("deesha", "缩放比="+be);
		// 不需要缩放,则直接得到图片文件的输入流
		if (be == 1) {
			byte[] bytes = null;
			File file = new File(filePath);
			long fileLength = file.length();
			if (fileLength / 1024 < 100) {
				bytes = readFile2ByteArray(filePath);
				// long millisecond = new Date().getTime();
				// File file = new File(filePath);
				// writeByte2File(file.getPath(), bytes);
				// Log.e("deesha","文件不需要压缩,"
				// +"文件大小="+bytes.length/1024 + "KB");
			} else {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				Bitmap bitmap2 = BitmapFactory.decodeFile(filePath, null);// 此时返回bm为空
				bitmap2.compress(Bitmap.CompressFormat.JPEG, 80, baos);
				bytes = baos.toByteArray();
				// Log.e("deesha", "文件不需要比例压缩,质量压缩后大小=" + bytes.length/1024 +
				// "KB");
			}
			// Log.e("deesha", "-----------压缩完成--------------");
			return bytes;
		}
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		newOpts.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(filePath, newOpts);
		// long millisecond = new Date().getTime();
		// File file2 = new File(getFileSaveDir()+"/"+millisecond+".jpg");
		byte[] bytes = bitmap2Byte(bitmap);
		// Log.e("deesha", "压缩后宽="+bitmap.getWidth()
		// +"---压缩后高="+bitmap.getHeight());
		// Log.e("deesha", "压缩后大小="+bytes.length/1024 + "KB");
		// Log.e("deesha", "-----------比例大小压缩完成--------------");
		// writeByte2File(file2.getPath(),bytes);
		// Log.e("deesha", "-----------开始质量压缩--------------");
		// ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
		// bytes = baos.toByteArray();
		//
		// Log.e("deesha", "质量压缩后宽=" + bitmap.getWidth()
		// +"---质量压缩后高=" + bitmap.getHeight());
		// Log.e("deesha", "质量压缩后大小=" + bytes.length/1024 + "KB");
		// Log.e("deesha", "-----------质量压缩完成--------------");
		// file2 = new File(getFileSaveDir()+"/"+millisecond+"_small.jpg");
		// writeByte2File(file2.getPath(),baos.toByteArray());
		return bytes;
		// return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
	}

	/**
	 * <b>图片按比例大小压缩方法（根据路径获取图片并压缩）</b><br/>
	 * 该方法就是对Bitmap形式的图片进行压缩, 也就是通过设置采样率, 减少Bitmap的像素, 从而减少了它所占用的内存
	 * 
	 * @param filePath
	 *            图片文件路径
	 * @return byte[] 经过压缩之后的字节数组
	 * */
	public static Bitmap compressImageByScaleReturnBitmap(String filePath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(filePath, newOpts);// 此时返回bm为空

		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// Log.e("deesha", "开始压缩图片"+filePath
		// +"\n文件大小="+new File(filePath).length()/1024 +"KB");
		// Log.e("deesha", "压缩前宽="+w+"---压缩前高="+h);
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float ww = MAX_WIDTH;// 这里设置宽度为480f
		float hh = MAX_HEIGHT;// 这里设置高度为800f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		// if (w > ww)
		// {//如果宽度大的话根据宽度固定大小缩放
		// Log.e(TAG, "原始缩放比=" + Math.ceil(newOpts.outWidth / ww));
		// be = (int) Math.ceil(newOpts.outWidth / ww);
		// }

		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据高度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0) {
			be = 1;
		}
		// Log.e("deesha", "缩放比="+be);
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		newOpts.inJustDecodeBounds = false;
		newOpts.inPreferredConfig = Bitmap.Config.RGB_565;// 降低图片从ARGB888到RGB565
		bitmap = BitmapFactory.decodeFile(filePath, newOpts);
		// long millisecond = new Date().getTime();
		// File file2 = new File(getFileSaveDir()+"/"+millisecond+".jpg");

		// byte[] bytes = bitmap2Byte(bitmap);
		// Log.e("deesha", "压缩后宽="+bitmap.getWidth()
		// +"---压缩后高="+bitmap.getHeight());
		// Log.e("deesha", "压缩后大小="+bytes.length/1024 + "KB");
		// Log.e("deesha", "-----------比例大小压缩完成--------------");

		return bitmap;
		// return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
	}

	/**
	 * <b>图片质量压缩方法</b><br/>
	 * 把图片直接读入内存,然后进行质量压缩(如果图片过大,进行质量压缩会导致OOM)
	 * 
	 * @param imageBitmap
	 *            图片在内存中以Bitmap形式存在
	 * @return bitmap
	 * */
	public static Bitmap compressImageByQualityReturnBitmap(Bitmap imageBitmap) {
		// Log.e("deesha", "-----------开始质量压缩--------------");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		imageBitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY_QUALITY, baos);

		// byte[] bytes = baos.toByteArray();
		// Log.e("deesha", "质量压缩后宽=" + imageBitmap.getWidth()
		// +"---质量压缩后高=" + imageBitmap.getHeight());
		// Log.e("deesha", "质量压缩后大小=" + bytes.length/1024 + "KB");
		// Log.e("deesha", "-----------质量压缩完成--------------");
		return imageBitmap;
	}

	/**
	 * <b>图片质量压缩方法</b><br/>
	 * 把图片直接读入内存,然后进行质量压缩(如果图片过大,进行质量压缩会导致OOM)
	 * 
	 * @param imageBitmap
	 *            图片在内存中以Bitmap形式存在
	 * @return 经过压缩之后的字节数组
	 * */
	public static byte[] compressImageByQualityReturnByte(Bitmap imageBitmap) {
		// Log.e("deesha", "-----------开始质量压缩--------------");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		imageBitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY_QUALITY, baos);
		byte[] bytes = baos.toByteArray();

		// Log.e("deesha", "质量压缩后宽=" + imageBitmap.getWidth()
		// +"---质量压缩后高=" + imageBitmap.getHeight());
		// Log.e("deesha", "质量压缩后大小=" + bytes.length/1024 + "KB");
		// Log.e("deesha", "-----------质量压缩完成--------------");
		return bytes;
	}

	/**
	 * <b>图片质量压缩方法</b><br/>
	 * 把图片直接读入内存,然后进行质量压缩(如果图片过大,进行质量压缩会导致OOM)
	 * 
	 * @param imageBitmap
	 *            图片在内存中以Bitmap形式存在
	 * @return 经过压缩之后的字节数组
	 * */
	public static byte[] compressImageByQualityReturnByte(Bitmap imageBitmap, String filePath) {
		// Log.e("deesha", "-----------开始质量压缩--------------");
		int degree = readPictureDegree(filePath);

		if (degree != 0) {// 旋转照片角度
			imageBitmap = rotateBitmap(imageBitmap, degree);
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		imageBitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY_QUALITY, baos);
		byte[] bytes = baos.toByteArray();

		// Log.e("deesha", "质量压缩后宽=" + imageBitmap.getWidth()
		// +"---质量压缩后高=" + imageBitmap.getHeight());
		// Log.e("deesha", "质量压缩后大小=" + bytes.length/1024 + "KB");
		// Log.e("deesha", "-----------质量压缩完成--------------");

		// File sdDir = Environment.getExternalStorageDirectory();// 获取SD卡根目录
		// File saveDir = new File(sdDir.getPath() + "/deeshaCommunity/");
		// long millisecond = new Date().getTime();
		// String saveFilePath = saveDir+"/"+millisecond+"_small.jpg";
		// FileUtil.writeFileFromBytes(baos.toByteArray(),saveFilePath);

		return bytes;
	}

	/**
	 * 判断照片角度
	 * */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * 旋转照片
	 * */
	public static Bitmap rotateBitmap(Bitmap bitmap, int degress) {
		if (bitmap != null) {
			Matrix m = new Matrix();
			m.postRotate(degress);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
			return bitmap;
		}
		return bitmap;
	}

	/**
	 * <b>读取文件到字节数组</b><br/>
	 * 该方法把文件读取到一个字节数组当中,如果找不到文件则返回null
	 * 
	 * @param filePath
	 *            图片文件路径
	 * @author ftc
	 * */
	public static byte[] readFile2ByteArray(String filePath) {
		FileInputStream fis = null;
		ByteArrayOutputStream baos = null;
		byte[] bytes = null;
		try {
			fis = new FileInputStream(filePath);

			baos = new ByteArrayOutputStream(1024);

			byte[] temp = new byte[1024];

			// 每次读取的字节大小
			int size = 0;

			while ((size = fis.read(temp)) != -1) {
				baos.write(temp, 0, size);
			}

			fis.close();

			bytes = baos.toByteArray();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return bytes;
	}

	public static Bitmap getCompressImage(String imgFilePath, int imageCompressSize) throws Exception {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 获取这个图片的宽和高
		Bitmap bitmap = BitmapFactory.decodeFile(imgFilePath, options); // 此时返回bm为空
		int outWidth = options.outWidth;
		int outHeight = options.outHeight;
		// LogUtil.e("oom", "outWidth="+outWidth+"---outHeight="+outHeight);
		if (imageCompressSize == -1) {
			options.inSampleSize = 0;
		} else if (outWidth * outHeight > imageCompressSize) {
			// android 动态计算缩放比例
			options.inSampleSize = computeSampleSize(options, -1, imageCompressSize);
		}
		// 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false
		options.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(imgFilePath, options);
		// LogUtil.e("oom", "bitmap="+bitmap);

		// LogUtil.e("oom", "options.inSampleSize="+options.inSampleSize);
		// if(bitmap != null)
		// {
		// //LogUtil.e("oom",
		// "CompressWidth="+bitmap.getWidth()+"---CompressHeight="+bitmap.getHeight());
		// }
		return bitmap;
	}

	// android 动态计算缩放比例
	public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	public static byte[] bitmap2Byte(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	private static String transactString(String imageUrl) {
		// 去除参数列表
		String imageUrl_true = null;
		// 存在?号
		if (imageUrl.indexOf('?') != -1) {
			imageUrl_true = imageUrl.substring(0, imageUrl.indexOf('?'));

		} else {
			imageUrl_true = imageUrl;
		}
		// 一定要去除文件名后面空格
		return imageUrl_true.trim();
		// return imageUrl_true;
	}

	/**
	 * 释放bitmapCache所有bitmap资源
	 * 
	 * @param
	 */
	public static void freeAllBitmap(Map<String, SoftReference<Bitmap>> bitmapCache) {
		if (bitmapCache == null || bitmapCache.size() == 0) {
			return;
		}
		SoftReference<Bitmap> delBitmap;
		// LogUtil.e("indexActivity", "bitmapCache.size()="+bitmapCache.size());
		Iterator<String> iterator = bitmapCache.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			delBitmap = bitmapCache.get(key);
			if (delBitmap != null) {
				if (delBitmap.get() != null && !delBitmap.get().isRecycled()) {
					iterator.remove();
					bitmapCache.remove(key);
					delBitmap.get().recycle();
					delBitmap = null;
				} else {
					iterator.remove();
					bitmapCache.remove(key);
				}
			}
		}
		System.gc();
		// LogUtil.e("indexActivity",
		// "----清理后---bitmapCache.size()="+bitmapCache.size());
	}

	/**
	 * 释放listView指定个数的bitmap资源
	 * 
	 * @param
	 */
	public static void freeListViewBitmap(LinkedList<String> cache_pic_key, Map<String, SoftReference<Bitmap>> cache_pic_bitmap, int start, int end) {
		int delCount = end - start;
		// LogUtil.e("indexActivity",
		// "start="+start+"---end="+end+"--delCount="+delCount);
		// LogUtil.e("indexActivity",
		// "cache_pic_key.size()="+cache_pic_key.size());
		delCount = cache_pic_key.size() - delCount - 2;
		// LogUtil.e("indexActivity", "delCount="+delCount);
		for (int i = 0; i < delCount; i++) {
			String key = cache_pic_key.removeLast();
			if (key.length() > 0) {
				// LogUtil.e("indexActivity",
				// "cache_pic_key.size()="+cache_pic_key.size());
				// Bitmap bitMap = cache_pic_bitmap.remove(key);
				SoftReference<Bitmap> bitmapReference = cache_pic_bitmap.remove(key);
				if (bitmapReference != null && !bitmapReference.get().isRecycled()) {
					bitmapReference.get().recycle();
					bitmapReference = null;
				}
			}
		}
		System.gc();
	}
}