package com.huatuo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间相关操作
 * 
 * @author ftc
 * @version 创建时间：2011-8-3 下午05:23:41
 */
public class DateUtil {
	private static long l_timeBwt = 0;

	/*
	 * 根据服务端和客户端时间差估算出服务器当前时间
	 */
	public static String getNow(String pattern) {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.MILLISECOND, (int) l_timeBwt);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return (String) df.format(now.getTimeInMillis());
	}

	/**
	 * 取得当前时间,以时间格式返回
	 * 
	 * @return
	 */
	public static String getDateTimeNow() {
		return getNow("yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 取得当前时间,以串格式返回
	 * 
	 * @return
	 */
	public static String getStringNow() {
		return getNow("yyyyMMddHHmmss");
	}

	/**
	 * 取时间 差
	 * 
	 * @param begintimestr
	 * @param endtimestring
	 * @return
	 */
	public static long Comparison(String begintimestr, String endtimestring) {
		if (begintimestr == null || endtimestring == null) {
			return 0;
		}
		long out = 0;
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			java.util.Date now = df.parse(endtimestring);

			java.util.Date date = df.parse(begintimestr);

			out = now.getTime() - date.getTime();

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		return out;
	}

	public static String Date2String(Date now, String format) {
		if (format == null) {
			format = "yyyy-MM-dd HH:mm:ss";
		}
		java.text.DateFormat format1 = new java.text.SimpleDateFormat(format);
		String datestring = format1.format(now);
		return datestring;
	}

	public static Date string2Date(String datestring, String format) {
		if (format == null) {
			format = "yyyy-MM-dd HH:mm:ss";
		}
		java.util.Date now = null;
		try {
			SimpleDateFormat df = new SimpleDateFormat(format);
			now = df.parse(datestring);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		return now;
	}

	/**
	 * 
	 * @param datestring
	 * @return将格式"yyyy-MM-dd HH:mm"时间字符串转为"yyyy-MM-dd HH:mm:ss"
	 */
	public static String string2DateString(String datestring) {
		java.util.Date now = null;
		String dateString = null;
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			now = df.parse(datestring);

			java.text.DateFormat format1 = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			dateString = format1.format(now);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dateString;
	}

	public static String string2DateString(String datestring, String sourceFormat, String targetFormat) {
		if (sourceFormat == null) {
			sourceFormat = "yyyy-MM-dd HH:mm:ss";
		}
		java.util.Date now = null;
		String dateString = null;
		try {
			SimpleDateFormat df = new SimpleDateFormat(sourceFormat);
			now = df.parse(datestring);

			java.text.DateFormat format1 = new java.text.SimpleDateFormat(targetFormat);
			dateString = format1.format(now);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dateString;
	}

	public static String string2DateString(String datestring, String targetFormat) {
		java.text.DateFormat format1 = new java.text.SimpleDateFormat(targetFormat);
		String dateString = format1.format(string2Date(datestring, null));
		return dateString;
	}

	public static String string2DateStringForChinese(String datestring, String sourceFormat, String targetFormat) {
		String dateString = string2DateString(datestring, sourceFormat, targetFormat);
		// LogUtil.e("test","dateString="+dateString);
		String[] splitStringArray = dateString.split("-", 3);
		StringBuffer stringBuffer = new StringBuffer();
		int length = splitStringArray.length;
		if (length == 3) {
			stringBuffer.append(splitStringArray[0] + "年");
			stringBuffer.append(splitStringArray[1] + "月");
			stringBuffer.append(splitStringArray[2] + "日");
		} else if (length == 2) {
			stringBuffer.append(splitStringArray[0] + "月");
			stringBuffer.append(splitStringArray[1] + "日");
		}

		return stringBuffer.toString();
	}

	/**
	 * 获取两个时间格式的字符之间的 时间差
	 * 
	 * @param serverCacheUpdateTimeString
	 *            服务端缓存时间
	 * @param localCacheupdateTimeString
	 *            本地缓存时间
	 * @return
	 */
	public static long getDifferenceBetweenDate(String serverCacheUpdateTimeString, String localCacheupdateTimeString) {
		long differenceDount = 0;
		long serverCacheUpdateTimeCount = 0;
		long localCacheupdateTimeCount = 0;
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (serverCacheUpdateTimeString.length() != 0) {
				java.util.Date serverCacheUpdateTime = df.parse(serverCacheUpdateTimeString);
				serverCacheUpdateTimeCount = serverCacheUpdateTime.getTime();
			}
			if (localCacheupdateTimeString.length() != 0) {
				java.util.Date localCacheupdateTime = df.parse(localCacheupdateTimeString);
				localCacheupdateTimeCount = localCacheupdateTime.getTime();
			}
			// LogUtil.i("pay",
			// "serverCacheUpdateTimeCount="+serverCacheUpdateTimeCount+"----localCacheupdateTimeCount="+localCacheupdateTimeCount);
			differenceDount = localCacheupdateTimeCount - serverCacheUpdateTimeCount;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return differenceDount;
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
	public static String getBetween(String begintimestr, String endtimestring) {
		String out = "";

		if (begintimestr == null || endtimestring == null) {
			return "";
		}
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			java.util.Date begin_time = df.parse(begintimestr);
			java.util.Date end_time = df.parse(endtimestring);
			java.util.Date now_time = new java.util.Date();
			// 计算时差
			if (l_timeBwt == 0) {
				l_timeBwt = begin_time.getTime() - now_time.getTime();
			}
			long millisecond = end_time.getTime() - now_time.getTime() - l_timeBwt;
			if (millisecond < 0) {
				out = "0";
				return out;
			}

			// long day = millisecond/(24*60*60*1000);
			// long hour = (millisecond/(60*60*1000)-day*24);
			// long min = ((millisecond/(60*1000))-day*24*60-hour*60);
			// long s = (millisecond/1000-day*24*60*60-hour*60*60-min*60);
			// out = day+"天"+hour+"小时"+min+"分"+s+"秒";
			long day = millisecond / (24 * 60 * 60 * 1000);
			long hour = millisecond / (60 * 60 * 1000);
			long min = ((millisecond / (60 * 1000)) - hour * 60);
			long s = (millisecond / 1000 - hour * 60 * 60 - min * 60);
			out = day + "-" + hour + "-" + min + "-" + s;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out;
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
	public static String getBetweenForPrint(String begintimestr, String endtimestring) {
		String out = "";

		if (begintimestr == null || endtimestring == null) {
			return "";
		}
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			java.util.Date begin_time = df.parse(begintimestr);
			java.util.Date end_time = df.parse(endtimestring);

			long millisecond = end_time.getTime() - begin_time.getTime();
			if (millisecond < 0) {
				out = string2DateString(begintimestr, "yyyy-MM-dd HH:mm");
				return out;
			}

			// long day = millisecond/(24*60*60*1000);
			// long hour = (millisecond/(60*60*1000)-day*24);
			// long min = ((millisecond/(60*1000))-day*24*60-hour*60);
			// long s = (millisecond/1000-day*24*60*60-hour*60*60-min*60);
			// out = day+"天"+hour+"小时"+min+"分"+s+"秒";
			long day = millisecond / (24 * 60 * 60 * 1000);
			long hour = millisecond / (60 * 60 * 1000);
			long min = ((millisecond / (60 * 1000)) - hour * 60);
			long s = (millisecond / 1000 - hour * 60 * 60 - min * 60);
			// out = day+"-"+hour+"-"+min+"-"+s;
			// 24小时之前 显示 月-日 时:分
			if (day > 0) {
				out = string2DateString(begintimestr, "yyyy-MM-dd HH:mm");
			} else if (hour > 0) {
				out = hour + " 小时前";
			} else if (min > 0) {
				out = min + " 分钟前";
			} else if (s > 0) {
				out = s + " 秒前";
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out;
	}

	/**
	 * 获取俩个时间 超过一天用月日表达
	 * 
	 * @param begintimestr
	 *            开始时间
	 * @param endtimestring
	 *            结束时间
	 * @return
	 * @throws ParseException
	 */
	public static String getBetweenForPrintTime(String begintimestr, String endtimestring) {
		String out = "";

		if (begintimestr == null || endtimestring == null) {
			return "";
		}
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			java.util.Date begin_time = df.parse(begintimestr);
			java.util.Date end_time = df.parse(endtimestring);

			long millisecond = end_time.getTime() - begin_time.getTime();
			if (millisecond < 0) {
				out = string2DateString(begintimestr, "yyyy-MM-dd HH:mm");
				return out;
			}

			// long day = millisecond/(24*60*60*1000);
			// long hour = (millisecond/(60*60*1000)-day*24);
			// long min = ((millisecond/(60*1000))-day*24*60-hour*60);
			// long s = (millisecond/1000-day*24*60*60-hour*60*60-min*60);
			// out = day+"天"+hour+"小时"+min+"分"+s+"秒";
			long day = millisecond / (24 * 60 * 60 * 1000);
			long hour = millisecond / (60 * 60 * 1000);
			long min = ((millisecond / (60 * 1000)) - hour * 60);
			long s = (millisecond / 1000 - hour * 60 * 60 - min * 60);
			// out = day+"-"+hour+"-"+min+"-"+s;
			// 24小时之前 显示 月-日 时:分
			if (day > 0) {
				Date timeDate = string2Date(begintimestr, "yyyy-MM-dd HH:mm");
				out = timeDate.getMonth() + "月" + timeDate.getDay() + "日";
			} else if (hour > 0) {
				out = hour + " 小时前";
			} else if (min > 0) {
				out = min + " 分钟前";
			} else if (s > 0) {
				out = s + " 秒前";
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out;
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
	public static String getBetweenForAge(String begintimestr, String endtimestring) {
		String out = "";

		if (begintimestr == null || endtimestring == null) {
			return "";
		}
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date begin_time = df.parse(begintimestr);
			java.util.Date end_time = df.parse(endtimestring);

			long millisecond = end_time.getTime() - begin_time.getTime();
			if (millisecond < 0) {
				out = string2DateString(begintimestr, "yyyy-MM-dd HH:mm");
				return out;
			}

			// long day = millisecond/(24*60*60*1000);
			// long hour = (millisecond/(60*60*1000)-day*24);
			// long min = ((millisecond/(60*1000))-day*24*60-hour*60);
			// long s = (millisecond/1000-day*24*60*60-hour*60*60-min*60);
			// out = day+"天"+hour+"小时"+min+"分"+s+"秒";
			long day = millisecond / (24 * 60 * 60 * 1000);
			long month = day / 30;
			long year = month / 12;
			long monthResidual = month % 12;
			if (monthResidual == 0) {
				out = year + "岁";
			} else {
				out = year + "岁" + monthResidual + "个月";
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out;
	}

	/**
	 * 获取两个日期之间的间隔天数
	 * 
	 * @return
	 */
	public static int getIntervalDays(Date startDate, Date endDate) {
		Calendar fromCalendar = Calendar.getInstance();
		fromCalendar.setTime(startDate);
		fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
		fromCalendar.set(Calendar.MINUTE, 0);
		fromCalendar.set(Calendar.SECOND, 0);
		fromCalendar.set(Calendar.MILLISECOND, 0);

		Calendar toCalendar = Calendar.getInstance();
		toCalendar.setTime(endDate);
		toCalendar.set(Calendar.HOUR_OF_DAY, 0);
		toCalendar.set(Calendar.MINUTE, 0);
		toCalendar.set(Calendar.SECOND, 0);
		toCalendar.set(Calendar.MILLISECOND, 0);

		return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
	}

	/**
	 * 获取俩个时间 的间隔
	 * 
	 * @param begintimestr
	 * @param endtimestring
	 * @return
	 * @throws ParseException
	 */
	public static String getBetween(Date begintimestr, Date endtimestring) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date now = endtimestring;
		Date date = begintimestr;
		long l = now.getTime() - date.getTime();
		long day = l / (24 * 60 * 60 * 1000);
		long hour = (l / (60 * 60 * 1000) - day * 24);
		long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
		long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		String out = day + "天" + hour + "小时" + min + "分" + s + "秒";
		return out;
	}

	/**
	 * 时间格式字符串转换成时间 对象
	 * 
	 * @param timeString
	 * @param fomat
	 * @return
	 * @throws ParseException
	 */
	public static Date getDateByTimeString(String timeString, String format) throws ParseException {
		if (format == null) {
			format = "yyyy-MM-dd HH:mm:ss";
		}
		SimpleDateFormat df = new SimpleDateFormat(format);
		Date date;

		date = df.parse(timeString);
		return date;

	}

	/**
	 * 获取 减去 向应 数量(日,小时,分,妙)后 的时间
	 * 
	 * @param now
	 * @param subtime
	 * @param format
	 * @return
	 */
	public static Date getSubtdate(Date now, int subtime, String format) {
		if (format == null) {
			format = "ss";
		}
		long longsubtime = 0;
		if ("ss".equals(format)) {
			longsubtime = subtime * 1000;
		}
		if ("mm".equals(format)) {
			longsubtime = subtime * 1000 * 60;
		}
		if ("HH".equals(format)) {
			longsubtime = subtime * 1000 * 60 * 60;
		}
		if ("dd".equals(format)) {
			longsubtime = subtime * 1000 * 60 * 60 * 24;
		}

		return new Date(now.getTime() - longsubtime);
	}

	public static void main(String[] args) {
		// String time =
		// getBetween("2011-08-23 11:23:40","2011-08-23 14:00:00");
		// System.out.println("time="+time);
		System.out.println("aaaaaa");
	}
}