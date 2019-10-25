/*
 * Copyright (c) 2000 - 2006 The Legion Of The Bouncy Castle (http://www.bouncycastle.org)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files 
 * (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, 
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is 
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES 
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE 
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR 
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */

package com.huatuo.util;

import java.io.UnsupportedEncodingException;

/**
 * base64 编码工具
 * 
 * @author admin
 * 
 */
public class Base64 {
	/**
	 * utf-8 串 base64编码
	 * 
	 * @param in
	 * @return
	 */
	public static String string2Base64(String in) {
		try {
			byte[] inbayt = in.getBytes("utf-8");
			byte[] ebas64baty = encodeNOW(inbayt);
			return new String(ebas64baty, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		return "";
	}

	public static String base642String(String in) {
		try {
			byte[] inbayt = in.getBytes("utf-8");
			byte[] dbas64baty = decodeNOW(inbayt);
			return new String(dbas64baty, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		return "";
	}

	/**
	 * byte[] 编码
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] encode(byte[] data) {
		return (encodeNOW(data));
	}

	/**
	 * byte[] 编码成string
	 * 
	 * @param data
	 * @return
	 */
	public static String encodeBytes(byte[] data) {
		return new String(encodeNOW(data));
	}

	/**
	 * String 解码
	 * 
	 * @param data
	 * @return
	 */
	public static String decodeBytes(String data) {
		return new String(decodeNOW(data.getBytes()));
	}

	public static byte[] decodeData(String data) throws UnsupportedEncodingException {
		return decodeNOW(data.getBytes("utf-8"));
	}

	public static byte[] decode(byte[] data) {
		return (decodeNOW(data));
	}

	private static byte[] encodingTable = { (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F', (byte) 'G', (byte) 'H', (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N', (byte) 'O', (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U', (byte) 'V', (byte) 'W', (byte) 'X', (byte) 'Y', (byte) 'Z', (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f', (byte) 'g', (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l',
			(byte) 'm', (byte) 'n', (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's', (byte) 't', (byte) 'u', (byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z', (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) '+', (byte) '/' };

	/**
	 * 编码
	 * 
	 * @param data
	 * @return
	 */
	private static byte[] encodeNOW(byte[] data) {
		byte[] bytes;

		if ((data.length % 3) == 0) {
			bytes = new byte[4 * data.length / 3];
		} else {
			bytes = new byte[4 * ((data.length / 3) + 1)];
		}

		for (int i = 0, j = 0; i < ((data.length / 3) * 3); i += 3, j += 4) {
			int b1, b2, b3, b4;
			int d1, d2, d3;

			d1 = data[i] & 0xff;
			d2 = data[i + 1] & 0xff;
			d3 = data[i + 2] & 0xff;

			b1 = (d1 >>> 2) & 0x3f;
			b2 = ((d1 << 4) | (d2 >>> 4)) & 0x3f;
			b3 = ((d2 << 2) | (d3 >>> 6)) & 0x3f;
			b4 = d3 & 0x3f;

			bytes[j] = encodingTable[b1];
			bytes[j + 1] = encodingTable[b2];
			bytes[j + 2] = encodingTable[b3];
			bytes[j + 3] = encodingTable[b4];
		}

		/*
		 * process the tail end.
		 */
		int b1, b2, b3;
		int d1, d2;

		switch (data.length % 3) {
		case 0: /* nothing left to do */
			break;
		case 1:
			d1 = data[data.length - 1] & 0xff;
			b1 = (d1 >>> 2) & 0x3f;
			b2 = (d1 << 4) & 0x3f;

			bytes[bytes.length - 4] = encodingTable[b1];
			bytes[bytes.length - 3] = encodingTable[b2];
			bytes[bytes.length - 2] = (byte) '=';
			bytes[bytes.length - 1] = (byte) '=';
			break;
		case 2:
			d1 = data[data.length - 2] & 0xff;
			d2 = data[data.length - 1] & 0xff;

			b1 = (d1 >>> 2) & 0x3f;
			b2 = ((d1 << 4) | (d2 >>> 4)) & 0x3f;
			b3 = (d2 << 2) & 0x3f;

			bytes[bytes.length - 4] = encodingTable[b1];
			bytes[bytes.length - 3] = encodingTable[b2];
			bytes[bytes.length - 2] = encodingTable[b3];
			bytes[bytes.length - 1] = (byte) '=';
			break;
		}

		return bytes;
	}

	/*
	 * set up the decoding table.
	 */
	private static byte[] decodingTable;

	static {
		decodingTable = new byte[128];

		for (int i = 'A'; i <= 'Z'; i++) {
			decodingTable[i] = (byte) (i - 'A');
		}

		for (int i = 'a'; i <= 'z'; i++) {
			decodingTable[i] = (byte) (i - 'a' + 26);
		}

		for (int i = '0'; i <= '9'; i++) {
			decodingTable[i] = (byte) (i - '0' + 52);
		}

		decodingTable['+'] = 62;
		decodingTable['/'] = 63;
	}

	/**
	 * 解码
	 * 
	 * @param data
	 * @return
	 */
	private static byte[] decodeNOW(byte[] data) {
		byte[] bytes;
		byte b1, b2, b3, b4;

		if (data[data.length - 2] == '=') {
			bytes = new byte[(((data.length / 4) - 1) * 3) + 1];
		} else if (data[data.length - 1] == '=') {
			bytes = new byte[(((data.length / 4) - 1) * 3) + 2];
		} else {
			bytes = new byte[((data.length / 4) * 3)];
		}

		for (int i = 0, j = 0; i < data.length - 4; i += 4, j += 3) {
			b1 = decodingTable[data[i]];
			b2 = decodingTable[data[i + 1]];
			b3 = decodingTable[data[i + 2]];
			b4 = decodingTable[data[i + 3]];

			bytes[j] = (byte) ((b1 << 2) | (b2 >> 4));
			bytes[j + 1] = (byte) ((b2 << 4) | (b3 >> 2));
			bytes[j + 2] = (byte) ((b3 << 6) | b4);
		}

		if (data[data.length - 2] == '=') {
			b1 = decodingTable[data[data.length - 4]];
			b2 = decodingTable[data[data.length - 3]];

			bytes[bytes.length - 1] = (byte) ((b1 << 2) | (b2 >> 4));
		} else if (data[data.length - 1] == '=') {
			b1 = decodingTable[data[data.length - 4]];
			b2 = decodingTable[data[data.length - 3]];
			b3 = decodingTable[data[data.length - 2]];

			bytes[bytes.length - 2] = (byte) ((b1 << 2) | (b2 >> 4));
			bytes[bytes.length - 1] = (byte) ((b2 << 4) | (b3 >> 2));
		} else {
			b1 = decodingTable[data[data.length - 4]];
			b2 = decodingTable[data[data.length - 3]];
			b3 = decodingTable[data[data.length - 2]];
			b4 = decodingTable[data[data.length - 1]];

			bytes[bytes.length - 3] = (byte) ((b1 << 2) | (b2 >> 4));
			bytes[bytes.length - 2] = (byte) ((b2 << 4) | (b3 >> 2));
			bytes[bytes.length - 1] = (byte) ((b3 << 6) | b4);
		}

		return bytes;
	}
}
