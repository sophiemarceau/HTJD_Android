package com.huatuo.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HandleDistrictInfo_test {
	/** 所有省 */
	public static String[] mAreaDatas;
	public static String[] mAreaCodes;

	/** key - area value - Districts */
	public static Map<String, String[]> mDistrictNamesMap;
	public static Map<String, String[]> mDistrictCodeMap;
	// /** 当前区域的名称 */
	// public static String mCurrentAreaName;
	// public static String mCurrentAreaCode;
	// /** 当前商圈的名称 */
	// public static String mCurrentDistrictsName;
	// public static String mCurrentDistrictsCode;
	private static JSONArray jsonArray = null;

	/**
	 * 初始化地址
	 */
	public static void initData(ArrayList<JSONObject> distictList) {
		// 清空数据
		mAreaDatas = null;
		mAreaCodes = null;
		// mCurrentAreaName = null;
		// mCurrentAreaCode = null;
		// mCurrentDistrictsName = null;
		// mCurrentDistrictsCode = null;
		mDistrictCodeMap = new HashMap<String, String[]>();
		mDistrictNamesMap = new HashMap<String, String[]>();

		CommonUtil.log("商圈--distictList---------------------->" + distictList);
		try {
			if (null != jsonArray) {
				return;
			}

			if (!CommonUtil.emptyListToString3(distictList)) {

				mAreaCodes = new String[distictList.size()];
				mAreaDatas = new String[distictList.size()];
				for (int i = 0; i < distictList.size(); i++) {
					JSONObject jsonP = distictList.get(i);// 每个省的json对象
					String areaCode = jsonP.getString("areaCode");// 省名字
					String areaName = jsonP.getString("areaName");// 省代码
					mAreaCodes[i] = areaCode;
					mAreaDatas[i] = areaName;
					JSONArray jsonCs = null;
					ArrayList<JSONObject> jsonCs_list = new ArrayList<JSONObject>();
					try {
						/**
						 * Throws JSONException if the mapping doesn't exist or
						 * is not a JSONArray.
						 */
						
						  jsonCs = jsonP.getJSONArray("districtList");
						
						
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("districtName","全部");
						jsonObject.put("districtCode","");
						
						jsonCs_list.add(jsonObject);
						jsonCs_list.addAll(JsonUtil.jsonArray2List(jsonCs));
						
						
						CommonUtil.log("获取的商圈列表：jsonCs_list：" + jsonCs_list);
					} catch (Exception e1) {
						continue;
					}
					
					
					String[] mDistrictNames = new String[jsonCs_list.size()];
					String[] mDistrictCodes = new String[jsonCs_list.size() ];
					String districtName = "";
					String districtCode = "";
					
					if (!("[]").equals(jsonCs_list) && !("").equals(jsonCs_list)
							&& !("null").equals(jsonCs_list) && null != jsonCs_list) {
						if (jsonCs_list.size() != 0) {
							for (int j = 0; j < jsonCs_list.size(); j++) {
								
									JSONObject jsonCity = jsonCs_list
											.get(j);
									districtName = jsonCity
											.getString("districtName");
									districtCode = jsonCity
											.getString("districtCode");
								mDistrictNames[j] = districtName;
								mDistrictCodes[j] = districtCode;

							}

							mDistrictNamesMap.put(areaName, mDistrictNames);
							mDistrictCodeMap.put(areaCode, mDistrictCodes);
						}

						CommonUtil
								.log("mDistrictNamesMap:" + mDistrictNamesMap);
						CommonUtil.log("mDistrictCodeMap:" + mDistrictCodeMap);
					}
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	

}
