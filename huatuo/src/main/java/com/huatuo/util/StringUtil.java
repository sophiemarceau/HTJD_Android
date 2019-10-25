package com.huatuo.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class StringUtil {
	public static String[] stringSplitToArray(String str) {
		String[] strArray = str.split(",");
		return strArray;
	}

	public static int string2int(String s) {
		int i = 0;
		try {
			i = Integer.parseInt(s);
		} catch (Exception e) {
			i = 0;
		}
		return i;
	}

	/**
	 * 
	 * @param str
	 *            需要过滤的字符串
	 * @return
	 * @Description:过滤数字以外的字符
	 */
	public static String filterUnNumber(String str) {
		// 只允许数字
		String regEx = "[^0-9]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		// 替换与模式匹配的所有字符（即非数字的字符将被""替换）
		return m.replaceAll("").trim();
	}

	/**
	 * 匹配英文
	 * */
	public static boolean matcherEnglishCharacter(String str) {
		// 匹配英文
		String regEx = "[a-zA-Z]+";
		return str.matches(regEx);
	}

	/**
	 * 匹配中文
	 * */
	public static boolean matcherChineseCharacter(String str) {
		// 匹配中文
		String regEx = " [\u4e00-\u9fa5]+";
		return str.matches(regEx);
	}

	/**
	 * 截断字符串(只保留前面4个字符)
	 * */
	public static String substringRetain4(String string) {
		if (string.length() > 4) {
			string = string.substring(0, 4) + "...";
		}
		return string;
	}

	/**
	 * 去掉最后的句号
	 * */
	public static String truncationStr(String str) {
		int str_length = str.length();
		String str1 = str.substring(0, str_length - 1);
		return str1;
	}

	/**
	 * 半角转为全角
	 * */
	public static String toDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}

	// 替换、过滤特殊字符
	public static String stringFilter(String str) throws PatternSyntaxException {
		str = str.replaceAll("【", "[").replaceAll("】", "]").replaceAll("！", "!");// 替换中文标号
		String regEx = "[『』]"; // 清除掉特殊字符
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}
	
	 /**
	  * 去除 空格/回车/制表符/换行符
	 * @param str
	 * @return
	 */
	public static String replaceBlank(String str) {
		  String dest = "";
		  if (str!=null) {
//			  空格\s、回车\n、换行符\r、制表符\t 
		   Pattern p = Pattern.compile("\\s*|\t|\r|\n");
		   Matcher m = p.matcher(str);
		   dest = m.replaceAll("");
		  }
		  return dest;
		 }

}