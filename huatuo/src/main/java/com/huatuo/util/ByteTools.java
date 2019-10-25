package com.huatuo.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * byte相关操作
 * 
 * @author ftc
 * @createTime 2013-5-19下午10:16:19
 */
public class ByteTools {
	/**
	 * byte 转换成16进制字符串
	 * 
	 * @param src
	 * @return
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * 16 进制串 转换成byte 数组
	 * 
	 * @param hexString
	 * @return
	 */
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * 16进制字符转换成byte
	 * 
	 * @param c
	 * @return
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	// 将指定byte数组以16进制的形式打印到控制台
	public static void printHexString(byte[] b) {
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			System.out.print(hex.toUpperCase());
		}

	}

	/**
	 * 十进制转化为十六进制，结果为C8。
	 * 
	 * @param hexstr
	 * @return
	 */
	public static String int2Hex(int hexstr) {
		return Integer.toHexString(hexstr);
	}

	/**
	 * 十六进制转化为十进制，结果140。
	 * 
	 * @return
	 */
	public static Integer hex2Int() {
		return Integer.parseInt("8C", 16);
	}

	public static byte[] intToByte(int i) {

		byte[] abyte0 = new byte[4];

		abyte0[0] = (byte) (0xff & i);

		abyte0[1] = (byte) ((0xff00 & i) >> 8);

		abyte0[2] = (byte) ((0xff0000 & i) >> 16);

		abyte0[3] = (byte) ((0xff000000 & i) >> 24);

		return abyte0;

	}

	public static int bytesToInt(byte[] bytes) {

		int addr = bytes[0] & 0xFF;

		addr |= ((bytes[1] << 8) & 0xFF00);

		addr |= ((bytes[2] << 16) & 0xFF0000);

		addr |= ((bytes[3] << 24) & 0xFF000000);

		return addr;

	}

	/**
	 * byte gzip 压缩
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] gZip(byte[] data) {
		byte[] b = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			GZIPOutputStream gzip = new GZIPOutputStream(bos);
			gzip.write(data);
			gzip.finish();
			gzip.close();
			b = bos.toByteArray();
			bos.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return b;
	}

	/**
	 * byte gzip 解压
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] unGZip(byte[] data) {
		byte[] b = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			GZIPInputStream gzip = new GZIPInputStream(bis);
			byte[] buf = new byte[1024];
			int num = -1;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while ((num = gzip.read(buf, 0, buf.length)) != -1) {
				baos.write(buf, 0, num);
			}
			b = baos.toByteArray();
			baos.flush();
			baos.close();
			gzip.close();
			bis.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return b;
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		String s = "{\"a\":123, \"b\":\"abc\", \"c\":[456, \"hello\"], \"d\":{\"name\":\"张三\", \"age\":\"32\"}}";
		int i = 142;
//		System.out.println(i);
		byte[] b = intToByte(i);
		b[0] = (byte) 142;
//		System.out.println(b[0]);
		for (int j = 0; j < b.length; j++) {
//			System.out.println(b[j]);
		}
//		System.out.println(bytesToInt(b));

	}
}
