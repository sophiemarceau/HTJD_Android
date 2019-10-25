package com.huatuo.util;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.huatuo.net.http.ActionResponse;

/**
 * Json 的相关操作
 * 
 * @author ftc
 * @version 创建时间：2011-8-3 下午07:05:45
 */

public class JsonUtil {
	/**
	 * map 转换成json
	 * 
	 * @param jsobj
	 * @param map
	 * @throws JSONException
	 */
	public static JSONObject setMapInJson(JSONObject jsobj,
			HashMap<String, String> map) throws JSONException {
		Object[] key = map.keySet().toArray();
		for (int i = 0; i < key.length; i++) {
			String jsonkey = key[i].toString();
			String jsonvalue = map.get(key[i]);
			HashMap<String, String> mapvalue = String2map(jsonvalue);
			if (mapvalue.size() == 0) {
				// 非对象
				jsobj.put(jsonkey, jsonvalue);
			} else {
				// 对象
				jsobj.put(jsonkey, mapvalue);
			}

		}
		return jsobj;
	}

	/**
	 * key = values 形式的string 转换成 map 格式
	 * 
	 * @param instr
	 * @return
	 */
	public static HashMap<String, String> String2map(String instr) {
		HashMap<String, String> hasOut = new HashMap<String, String>();
		if (instr == null) {
			return hasOut;
		}
		String[] sOut = instr.split(",");
		for (int i = 0; i < sOut.length; i++) {
			String[] sOutPram = sOut[i].split("=");
			if (sOutPram.length == 2)
				hasOut.put(sOutPram[0].toLowerCase(), sOutPram[1]);

		}
		return hasOut;
	}

	/**
	 * json 对象 转换成map 对象
	 * 
	 * @param jsonString
	 * @return
	 */
	public static HashMap<String, String> Json2Map(String jsonString) {
		HashMap<String, String> jmap = new HashMap<String, String>();
		Json2Map(jsonString, jmap);
		return jmap;
	}

	public static HashMap<String, String> Json2Map(JSONObject jsonString) {

		HashMap<String, String> jmap = new HashMap<String, String>();
		if (jsonString == null) {
			return jmap;
		}
		Json2Map(jsonString.toString(), jmap);
		return jmap;
	}

	public static void Json2Map(String jsonString, Map map) {

		try {
			JSONObject json = new JSONObject(jsonString);
			Iterator keys = json.keys();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				String value = json.get(key).toString();
				if (value.startsWith("{") && value.endsWith("}")) {
					Json2Map(value, map); // 对象分解封装
				} else {
					map.put(key.toLowerCase(), value);
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	public static HashMap<String, String> Json2Map2(JSONObject jsonString) {

		HashMap<String, String> jmap = new HashMap<String, String>();
		if (jsonString == null) {
			return jmap;
		}
		Json2Map2(jsonString.toString(), jmap);
		
		return jmap;
	}
	
	
	/**
	 * 解析双层hashMap
	 * @param jsonString
	 * @return
	 */
	public static HashMap<String, HashMap<String, String>> JSon2Map_TwoLayerMap(String jsonString){
		
			HashMap<String, HashMap<String, String>> map1 = new HashMap<String, HashMap<String,String>>();
			HashMap<String, String> map2 = new HashMap<String, String>();
			try {
				JSONObject json = new JSONObject(jsonString);
				CommonUtil.log("解析双层jsonString="+jsonString);
				Iterator keys = json.keys();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					String value = json.get(key).toString();
					map2 = Json2Map_OneLayerMap(value);
					map1.put(key, map2);
				}
			} catch (Exception e) {
				// e.printStackTrace();
			}
			
			CommonUtil.log("解析双层hashMap:map1="+map1);
			return map1;
	}
	/**
	 * 解析双层hashMap
	 * @param jsonString
	 * @return
	 */
	public static HashMap<String, HashMap<String, String>> JSon2Map_TwoLayerMap(JSONObject json){
		
			HashMap<String, HashMap<String, String>> map1 = new HashMap<String, HashMap<String,String>>();
			HashMap<String, String> map2 = new HashMap<String, String>();
			try {
				CommonUtil.log("解析双层json="+json);
				Iterator keys = json.keys();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					String value = json.get(key).toString();
					map2 = Json2Map_OneLayerMap(value);
					map1.put(key, map2);
				}
			} catch (Exception e) {
				// e.printStackTrace();
			}
			
			CommonUtil.log("解析双层hashMap:map1="+map1);
			return map1;
	}
	
	
	/**
	 * 返回单层的HashMapMap<String, String>
	 * @param jsonString
	 * @param map
	 * @return
	 */
	public static HashMap<String, String> Json2Map_OneLayerMap(String jsonString) {
		HashMap<String, String> map2 = new HashMap<String, String>();
		try {
			JSONObject json = new JSONObject(jsonString);
			CommonUtil.log("单层的HashMapMap<String, String>jsonString="+jsonString);
			Iterator keys = json.keys();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				String value = json.get(key).toString();
				map2.put(key, value);
			}
			
		} catch (Exception e) {
			// e.printStackTrace();
		}
		
		return  map2;
	}

	public static void Json2Map2(String jsonString, Map map) {

		try {
			JSONObject json = new JSONObject(jsonString);
			Iterator keys = json.keys();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				String value = json.get(key).toString();
				if (value.startsWith("{") && value.endsWith("}")) {
					Json2Map2(value, map); // 对象分解封装
				} else {
					 map.put(key, value);
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	/**
	 * 从json 对象里面获取 数组信息,返回 list
	 * 
	 * @param inputjson
	 * @return
	 */
	public static List<HashMap<String, String>> getListFromResultJson(
			JSONObject inputjson) {

		List<HashMap<String, String>> relist = new ArrayList<HashMap<String, String>>();
		if (inputjson == null) {
			return relist;
		}
		JSONArray data;
		try {
			JSONObject body = inputjson.getJSONObject("body");
			data = body.getJSONArray("list");
			for (int i = 0; i < data.length(); i++) {

				// 取一行数据
				JSONObject rowObject = data.getJSONObject(i);

				HashMap<String, String> map = Json2Map(rowObject.toString());
				relist.add(map);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

		return relist;
	}

	public static JSONObject String2Json(String jsonstr) {
		if (jsonstr == null) {
			return null;
		}
		jsonstr = jsonstr.trim();
		try {
			JSONObject rsjson = new JSONObject(jsonstr);
			return rsjson;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param list
	 *            list对象
	 * @return String
	 */
	public static String listToJson(List<?> list) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (list != null && list.size() > 0) {
			for (Object obj : list) {
				json.append(objectToJson(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	/**
	 * jsonArray 转换为 List
	 * */
	public static ArrayList<JSONObject> jsonArray2List(JSONArray jsonArray) {
		CommonUtil.log("jsonArray 转换为 List----jsonArray:"+jsonArray);
		ArrayList<JSONObject> arrayList = new ArrayList<JSONObject>();
		jsonArray2List(jsonArray, arrayList);
		CommonUtil.log("jsonArray 转换为 List----arrayList:"+arrayList);
		return arrayList;
		/*
		 * try { // 类别对象列表 JSONObject json; if (jsonArray == null ||
		 * jsonArray.length() == 0) { return arrayList; } else { for (int i = 0;
		 * i < jsonArray.length(); i++) { json = jsonArray.getJSONObject(i);
		 * arrayList.add(json); } } return arrayList; } catch (Exception e) { //
		 * TODO: handle exception return arrayList; }
		 */
	}

	public static void jsonArray2List(JSONArray jsonArray,
			ArrayList<JSONObject> arrayList) {
		try {
			// 类别对象列表
			JSONObject json;
			if (jsonArray == null || jsonArray.length() == 0) {
				return;
			} else {
				for (int i = 0; i < jsonArray.length(); i++) {
					json = jsonArray.getJSONObject(i);
					arrayList.add(json);
				}
			}
			return;
		} catch (Exception e) {
			// TODO: handle exception
			return;
		}
	}

	/**
	 * List 转换为 jsonArray
	 * */
	public static JSONArray list2jsonArray(ArrayList<JSONObject> arrayList) {
		JSONArray jsonArray = new JSONArray();
		for (JSONObject jsonObject : arrayList) {
			jsonArray.put(jsonObject);
		}
		return jsonArray;
	}

	public static String objectToJson(Object obj) {
		StringBuilder json = new StringBuilder();
		if (obj != null) {
			json.append(listToJson((List<?>) obj));
		}
		return json.toString();
	}

	public static String ProcessJsonFallbackString(String jsString) {
		if ("null".equals(jsString)) {
			return "";
		} else {
			return jsString;
		}
	}

	/**
	 * 排序
	 * 
	 * @param arry
	 * @param key
	 * @param s
	 * @return
	 */

	public static JSONArray orderBy(JSONArray arry, String key, int s) {
		JSONArray worketA = new JSONArray();
		try {
			for (int i = 0; i < arry.length(); i++) {

				worketA.put(arry.get(i));
			}

			for (int i = 0; i < worketA.length() - 1; i++) {
				for (int j = i + 1; j < worketA.length(); j++) {
					JSONObject oi = (JSONObject) worketA.get(i);
					JSONObject oj = (JSONObject) worketA.get(j);
					if (Float.parseFloat(oi.optString(key, "0")) > Float
							.parseFloat(oj.optString(key, "0"))) {
						if (s == 1) {
							worketA.put(i, oj);
							worketA.put(j, oi);
						}
					} else {
						if (s != 1) {
							worketA.put(i, oj);
							worketA.put(j, oi);
						}
					}
				}
			}
		} catch (JSONException e) {
		}
		return worketA;
	}

	/**
	 * 过滤器
	 * 
	 * @param arry
	 * @param key
	 * @param v
	 * @return
	 */

	public static JSONArray fileter(JSONArray arry, String key, String v) {
		// JsonUtil.fileter(ServerArry, "skillGrade", "高级");
		JSONArray worketA = new JSONArray();
		try {
			for (int i = 0; i < arry.length(); i++) {
				JSONObject o = (JSONObject) arry.get(i);
				if (o.optString(key, "").equalsIgnoreCase(v))
					worketA.put(o);
			}

		} catch (JSONException e) {
		}
		return worketA;
	}

	/**
	 * 在请求参数列表中,除去 sign、sign_type 两个参数外,其他需要使用到的非空参数皆是要签名的参数
	 * @param sArray
	 * @return
	 */
	public static Map<String, String> DoingFilter(Map<String, String> sArray) {// 过滤

		Map<String, String> result = new HashMap<String, String>();

		if (sArray == null || sArray.size() <= 0) {
			return result;
		}

		for (String key : sArray.keySet()) {
			String value = sArray.get(key);
			if (value == null || value.equals("")
					|| key.equalsIgnoreCase("sign")
					|| key.equalsIgnoreCase("sign_type")) {
				continue;
			}
			result.put(key, value);
		}

		return result;
	}

	/**
	 * 对数组里的每一个值从 a 到 z 的顺序排序,若遇到相同首字母,则看第二个字母, 
	 *	以此类推。 排序完成之后,再把所有数组值以“&”字符连接起来
	 * @param params
	 * @return
	 */
	public static String AppendString(Map<String, String> params) {// 排序+拼接

		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);// 排序

		String prestr = "";

		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);

			if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}

		return prestr;
	}

	public static String encryptMd5(String str) {

		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = str.getBytes();
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char c[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				c[k++] = hexDigits[byte0 >>> 4 & 0xf];
				c[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(c);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
	
	
	/** 获取List */
	public static ArrayList<JSONObject> getListFromJsonArray(JSONObject jsonObject,String key) {
		ArrayList<JSONObject> arrayList = new ArrayList<JSONObject>();
		// 地址列表
		JSONArray addressListArray = jsonObject.optJSONArray(key);
		JSONObject json;
		if (addressListArray == null || addressListArray.length() == 0) {
			return null;
		} else {
			for (int i = 0; i < addressListArray.length(); i++) {
				try {
					json = addressListArray.getJSONObject(i);
				} catch (JSONException e) {
					e.printStackTrace();
					continue;
				}
				arrayList.add(json);
			}
			
			return arrayList;
		}
	}


}
