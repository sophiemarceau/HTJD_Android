package com.huatuo.util;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.huatuo.base.MyApplication;

public class HandleAddress {
	/** 所有省 */
	public static String[] mProvinceDatas;
	public static String[] mProvinceCodes;
	
	
	/** key - 省 value - 市s */
	public static Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
	public static Map<String, String[]> mCitisCodesMap = new HashMap<String, String[]>();
	/** key - 市 values - 区s */
	public static Map<String, String[]> mAreaDatasMap = new HashMap<String, String[]>();
	public static Map<String, String[]> mAreaCodesMap = new HashMap<String, String[]>();
	/** 当前省的名称 */
	public static String mCurrentProviceName;
	public static String mCurrentProviceCode;
	/** 当前市的名称 */
	public static String mCurrentCityName;
	public static String mCurrentCityCode;
	/** 当前区的名称 */
	public static String mCurrentAreaName = "";
	public static String mCurrentAreaCode = "";
	private static JSONArray jsonArray = null;
	public static JSONObject mJsonObj;
	
	

	/**
	 * 初始化地址
	 */
	public static void initData() {

		mJsonObj = MyApplication.outObj;
		CommonUtil.log("CitiesActivity--mJsonObj---------------------->"
				+ mJsonObj);
		try {
			if (null != jsonArray) {
				return;
			}

			if (null != mJsonObj && !("").equals(mJsonObj)) {

				jsonArray = mJsonObj.getJSONArray("proList");
				// Log.e("CitiesActivity", "jsonArray---------------------->"
				// + jsonArray);
				mProvinceDatas = new String[jsonArray.length()];
				mProvinceCodes = new String[jsonArray.length()];
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonP = jsonArray.getJSONObject(i);// 每个省的json对象
					String province = jsonP.getString("proName");// 省名字
					String proCode = jsonP.getString("proCode");// 省代码
					mProvinceDatas[i] = province;
					mProvinceCodes[i] = proCode;
					JSONArray jsonCs = null;
					try {
						/**
						 * Throws JSONException if the mapping doesn't exist or
						 * is not a JSONArray.
						 */
						jsonCs = jsonP.getJSONArray("cityList");
					} catch (Exception e1) {
						continue;
					}
					String[] mCitiesDatas = new String[jsonCs.length()];
					String[] mCitiesCodes = new String[jsonCs.length()];
					for (int j = 0; j < jsonCs.length(); j++) {
						JSONObject jsonCity = jsonCs.getJSONObject(j);
						String city = jsonCity.getString("cityName");
						String cityCode = jsonCity.getString("cityCode");// 市名字
						mCitiesDatas[j] = city;
						mCitiesCodes[j] = cityCode;
						JSONArray jsonAreas = null;
						try {
							/**
							 * Throws JSONException if the mapping doesn't exist
							 * or is not a JSONArray.
							 */
							jsonAreas = jsonCity.getJSONArray("areaList");
						} catch (Exception e) {
							continue;
						}

						String[] mAreasDatas = new String[jsonAreas.length()];// 当前市的所有区
						String[] mAreaCodes = new String[jsonAreas.length()];// 当前市的所有区
						for (int k = 0; k < jsonAreas.length(); k++) {
							String area = jsonAreas.getJSONObject(k).getString(
									"areaName");// 区域的名称
							String areaCode = jsonAreas.getJSONObject(k)
									.getString("areaCode");
							mAreasDatas[k] = area;
							mAreaCodes[k] = areaCode;
						}
						mAreaDatasMap.put(city, mAreasDatas);
						mAreaCodesMap.put(cityCode, mAreaCodes);
					}

					mCitisDatasMap.put(province, mCitiesDatas);
					mCitisCodesMap.put(proCode, mCitiesCodes);
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		mJsonObj = null;
	}
}
