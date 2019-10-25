package com.huatuo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.text.TextUtils;

/**
 * 时间转换的工具类
 * 
 */
public class DateTimeUtility {
	private static final long MILLISECONDS_PER_DAY = 1000 * 60 * 60 * 24;
	private static int _localTimeZoneOffsetInMilliseconds = TimeZone.getDefault().getRawOffset();
	private static final String _standardFormat = "yyyy-MM-dd HH:mm:ss";
	private static final String standardFormat = "yyyy-MM-dd HH:mm";

	/**
	 * 
	 * 将string（2014-6-6 16:31:50）转换成date
	 */
	public static Date covertStringToDate(String date) throws ParseException {
		return covertStringToDate(date, _standardFormat);
	}

	/**
	 * 
	 * 将string（2014-6-6 16:31）转换成date
	 */
	public static Date covertTimeStringToDate(String date) throws ParseException {
		return covertStringToDate(date, standardFormat);
	}

	/**
	 * 
	 * 将string（2014-6-6 16:31:50）转换成date
	 */
	public static Date covertStringToDate(String date, String format) throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		if (TextUtils.isEmpty(date)) {
			return null;
		} else {
			return simpleDateFormat.parse(date);
		}
	}

	public static Calendar convertDateToCalendar(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

	public static Date convertUtcToLocal(Date utcDate) {
		return new Date(utcDate.getTime() + _localTimeZoneOffsetInMilliseconds);
	}

	public static Date convertLocalToUtc(Date localDate) {
		return new Date(localDate.getTime() - _localTimeZoneOffsetInMilliseconds);
	}

	public static String getDateTimeString(Date date) {
		return getDateTimeString(date, _standardFormat);
	}

	public static String getDateTimeString(Date date, String format) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		return simpleDateFormat.format(date);
	}

	public static long getDateDiffInDays(Date date1, Date date2) {
		return getDateDiffInMilliSeconds(date1, date2) / MILLISECONDS_PER_DAY;
	}

	public static long getDateDiffInMilliSeconds(Date date1, Date date2) {
		Calendar day1 = convertDateToCalendar(date1);
		Calendar day2 = convertDateToCalendar(date2);
		return (day1.getTimeInMillis() - day2.getTimeInMillis());
	}
}
