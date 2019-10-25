package com.huatuo.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import android.text.TextUtils;

/**
 * 保留小数点位数
 * 
 * @author wrz
 * 
 */
public class NumFormatUtil {

	/**
	 * 保留两位小数
	 * 
	 * @param price
	 * @return
	 */
	public static String saveTwoPointOfString(String price) {
		if (TextUtils.isEmpty(price)) {
			price = "0";
		}
		BigDecimal bd = new BigDecimal(price);
		// 保留两位小数
//		DecimalFormat df = new DecimalFormat("0.00");
		double result = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return Double.toString(result);
	}

	/**
	 * 保留两位小数
	 * 
	 * @param price
	 * @return
	 */
	public static String saveTwoPointOfDouble(Double price) {
		CommonUtil.log("saveTwoPointOfDouble:price:"+price);

		BigDecimal bd = new BigDecimal(Double.toString(price));
		CommonUtil.log("saveTwoPointOfDouble:bd:"+bd);
		// 保留两位小数
//		DecimalFormat df = new DecimalFormat("0.00");
//		return df.format(bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
		double result = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		
		return Double.toString(result);
		
	}

	/**
	 * 保留两位小数
	 * 
	 * @param price
	 * @return
	 */
	public static String saveTwoPointOfFloat(Float price) {
		BigDecimal bd = new BigDecimal(Double.toString(price));
		// 保留两位小数
//		DecimalFormat df = new DecimalFormat("0.00");
		double result = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return Double.toString(result);
	}

	/**
	 * 保留一位小数
	 * 
	 * @param price
	 * @return
	 */
	public static String saveOnePointOfFloat(Float price) {
		BigDecimal bd = new BigDecimal(Double.toString(price));
		// 保留一位小数
//		DecimalFormat df = new DecimalFormat("0.0");
		double result = bd.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
		return Double.toString(result);

	}

	/**
	 * 保留1位小数
	 * 
	 * @param price
	 * @return
	 */
	public static String saveOnePoint(String price) {
		if (TextUtils.isEmpty(price)) {
			price = "0";
		}
		BigDecimal bd = new BigDecimal(price);
		// 保留一位小数
//		DecimalFormat df = new DecimalFormat("0.0");
//		return df
//				.format(bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
		double result = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return Double.toString(result);
	}

	/**
	 * 保留3位小数
	 * 
	 * @param price
	 * @return
	 */
	public static String saveThreePoint(String price) {
		if (TextUtils.isEmpty(price)) {
			price = "0";
		}
		BigDecimal bd = new BigDecimal(price);
		// 保留两位小数
//		DecimalFormat df = new DecimalFormat("0.000");
//		return df.format(bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
		double result = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return Double.toString(result);
		
	}

	/**
	 * 保留4位小数
	 * 
	 * @param price
	 * @return
	 */
	public static String saveFourPoint(String price) {
		if (TextUtils.isEmpty(price)) {
			price = "0";
		}
		BigDecimal bd = new BigDecimal(price);
		// 保留两位小数
		DecimalFormat df = new DecimalFormat("0.0000");
		return df.format(bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
	}

	/**
	 * 将分转换成元--此方法用于计算
	 * 
	 * @param price
	 * @return
	 */
	public static double centFormatYuanTodouble(String price) {
		Double db_price = 0d;
		if (!TextUtils.isEmpty(price)) {
			db_price = Double.parseDouble(price) / 100d;
		}

		return db_price;
	}

	/**
	 * 注意 price必须是整数 不能用于小数处理 此方法仅用于展示价格-不能用于展示 计算后的价格 将分转换成元
	 * 
	 * @param price
	 * @return
	 */
	public static String centFormatYuanToString(String price) {
		CommonUtil.log("centFormatYuanToString:price:" + price);
		String formatPrice = "0";
		if (!TextUtils.isEmpty(price)) {

			int remainder = (int) Double.parseDouble(price);
			int remainder2 = remainder % 100;
			// 判断remainder2 转换成元后 是否存在 小数
			if (remainder2 == 0) {
				// 不存在小数 保留整数
				formatPrice = String.valueOf(remainder / 100);
			} else {
				//存在小数位--判断小数位是几位
				int remainder3 = remainder % 10;
				if(remainder3 == 0){
					// 存在小数 保留1位
					formatPrice = savePointOfDouble(
							Double.parseDouble(price) / 100d, 1);
				}else {
					// 存在小数 保留两位
					formatPrice = savePointOfDouble(
							Double.parseDouble(price) / 100d, 2);
				}
			}
		}
		return formatPrice;
	}
	
	/**
	 * 保留小数
	 * 
	 * @param price
	 * @param pointNum
	 *            小数点保留位数
	 * @return
	 */
	public static String savePointOfDouble(Double price, int pointNum) {

		BigDecimal bd = new BigDecimal(Double.toString(price));
		double result = bd.setScale(pointNum, BigDecimal.ROUND_HALF_UP).doubleValue();

		return Double.toString(result);

	}
	public static double stringToDouble(String str) {
		double db = 0d;
		if (!TextUtils.isEmpty(str)) {
			db = Double.parseDouble(str);
		}

		return db;
	}

	public static float stringToFloat(String str) {
		float fl = 0f;
		if (!TextUtils.isEmpty(str)) {
			fl = Float.parseFloat(str);
		}

		return fl;
	}
}
