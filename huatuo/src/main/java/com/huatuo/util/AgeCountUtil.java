package com.huatuo.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * zuoluo ：2014/8/16
 * 
 * @author Administrator 计算宝贝年龄的工具类 String babyBirthday:应为日期格是yyyy-MM-dd"
 *         返回为xx岁xx个月
 */
public class AgeCountUtil {

	public static String age(String babyBirthday) {
		if (babyBirthday == null || babyBirthday.length() == 0) {
			return "（0岁）";
		}
		long babyBirthdayMilliseconds = DateUtil.string2Date(babyBirthday, "yyyy-MM-dd").getTime();
		long currentMilliseconds = new Date().getTime();
		if (babyBirthdayMilliseconds - currentMilliseconds > 0) {
			return "（0岁）";
		}

		int year = 0;
		int month = 0;
		int day = 0;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar currentDateCalendar = Calendar.getInstance();
		currentDateCalendar.setTime(new Date());

		Calendar birthdayCalendar = Calendar.getInstance();
		try {
			birthdayCalendar.setTime(dateFormat.parse(babyBirthday));
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		year = (currentDateCalendar.get(Calendar.YEAR) - birthdayCalendar.get(Calendar.YEAR)) < 0 ? 0 : currentDateCalendar.get(Calendar.YEAR) - birthdayCalendar.get(Calendar.YEAR);
		month = (currentDateCalendar.get(Calendar.MONTH) - birthdayCalendar.get(Calendar.MONTH)) < 0 ? 0 : currentDateCalendar.get(Calendar.MONTH) - birthdayCalendar.get(Calendar.MONTH);
		day = (currentDateCalendar.get(Calendar.DAY_OF_MONTH) - birthdayCalendar.get(Calendar.DAY_OF_MONTH)) < 0 ? 0 : currentDateCalendar.get(Calendar.DAY_OF_MONTH) - birthdayCalendar.get(Calendar.DAY_OF_MONTH);
		StringBuffer sb = new StringBuffer();
		sb.append("（");
		if (year != 0 || month != 0) {
			if (year != 0) {
				sb.append(year + "岁");
			}
			if (month != 0) {
				sb.append(month + "个月");
			}
		} else if (year == 0 && month == 0) {
			if (day != 0) {
				sb.append(day + "天");
			} else {
				sb.append("0天");
			}
		}
		sb.append("）");
		return sb.toString();
	}

	/**
	 * 
	 * @param babyBirthday
	 * @return不带括号
	 */
	public static String ageString(String babyBirthday) {
		if (babyBirthday == null || babyBirthday.length() == 0) {
			return "0岁";
		}
		long babyBirthdayMilliseconds = DateUtil.string2Date(babyBirthday, "yyyy-MM-dd").getTime();
		long currentMilliseconds = new Date().getTime();
		if (babyBirthdayMilliseconds - currentMilliseconds > 0) {
			return "0岁";
		}

		int year = 0;
		int month = 0;
		int day = 0;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar currentDateCalendar = Calendar.getInstance();
		currentDateCalendar.setTime(new Date());
		Calendar birthdayCalendar = Calendar.getInstance();
		try {
			birthdayCalendar.setTime(dateFormat.parse(babyBirthday));
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		year = (currentDateCalendar.get(Calendar.YEAR) - birthdayCalendar.get(Calendar.YEAR)) < 0 ? 0 : currentDateCalendar.get(Calendar.YEAR) - birthdayCalendar.get(Calendar.YEAR);
		month = (currentDateCalendar.get(Calendar.MONTH) - birthdayCalendar.get(Calendar.MONTH)) < 0 ? 0 : currentDateCalendar.get(Calendar.MONTH) - birthdayCalendar.get(Calendar.MONTH);
		day = (currentDateCalendar.get(Calendar.DAY_OF_MONTH) - birthdayCalendar.get(Calendar.DAY_OF_MONTH)) < 0 ? 0 : currentDateCalendar.get(Calendar.DAY_OF_MONTH) - birthdayCalendar.get(Calendar.DAY_OF_MONTH);
		StringBuffer sb = new StringBuffer();
		if (year != 0 || month != 0) {
			if (year != 0) {
				sb.append(year + "岁");
			}
			if (month != 0) {
				sb.append(month + "个月");
			}
		} else if (year == 0 && month == 0) {
			if (day != 0) {
				sb.append(day + "天");
			} else {
				sb.append("0天");
			}
		}
		return sb.toString();
	}

	/**
	 * 获取俩个时间 格式的字符之间的 时间间隔
	 * 
	 * @param begintimestr
	 *            开始时间
	 * @param endtimestring
	 *            结束时间
	 * @return
	 * @throws ParseException
	 */
	public static String getBetweenForAge(String begintimestr) {
		StringBuffer stringBuffer = new StringBuffer();
		if (begintimestr == null) {
			return "";
		}
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date begin_time = df.parse(begintimestr);
			long millisecond = new Date().getTime() - begin_time.getTime();
			if (millisecond < 0) {
				stringBuffer.append("未出生");
				return stringBuffer.toString();
			}
			long day = millisecond / (24 * 60 * 60 * 1000);
			long month = day / 30;
			long year = month / 12;
			long monthResidual = month % 12;

			if (year != 0) {
				stringBuffer.append(year + "岁");
			}
			if (monthResidual != 0) {
				stringBuffer.append(monthResidual + "个月");
			}
			if (year == 0 && monthResidual == 0) {
				stringBuffer.append(day + "天");
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return stringBuffer.toString();
	}
}
