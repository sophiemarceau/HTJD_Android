package com.huatuo.util;

/**
 * 单位转换
 */
public class UnitConversionUtil {
	/**
	 * 文件大小 单位转换
	 */
	public static String formatSize(float size) {
		long kb = 1024;
		long mb = (kb * 1024);
		long gb = (mb * 1024);
		if (size < kb) {
			return String.format("%d B", (int) size);
		} else if (size < mb) {
			return String.format("%.2f KB", size / kb); // 保留两位小数
		} else if (size < gb) {
			return String.format("%.2f MB", size / mb);
		} else {
			return String.format("%.2f GB", size / gb);
		}
	}

	/**
	 * 距离转换小于1000m的显示？m大于1000m的显示？km
	 * 
	 * @param size
	 * @return
	 */
	public static String formatDistance(double size) {
		long m = 1000;
		if (size < m) {
			return String.format("%.2f m", size);
		} else {
			return String.format("%.2f km", size / m); // 保留两位小数
		}

	}
}
