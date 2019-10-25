package com.huatuo.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HandleDistrictInfo {
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
					try {
						/**
						 * Throws JSONException if the mapping doesn't exist or
						 * is not a JSONArray.
						 */
						jsonCs = jsonP.getJSONArray("districtList");
						CommonUtil.log("获取的商圈列表：jsonCs：" + jsonCs);
					} catch (Exception e1) {
						continue;
					}
					String[] mDistrictNames = new String[jsonCs.length() + 1];
					String[] mDistrictCodes = new String[jsonCs.length() + 1];
					String districtName = "";
					String districtCode = "";
					
					if (!("[]").equals(jsonCs) && !("").equals(jsonCs)
							&& !("null").equals(jsonCs) && null != jsonCs) {
						if (jsonCs.length() != 0) {
							for (int j = -1; j < jsonCs.length(); j++) {

								if (j == -1) {
									districtName = "全部";
									districtCode = "";
								} else {
									JSONObject jsonCity = jsonCs
											.getJSONObject(j);
									districtName = jsonCity
											.getString("districtName");
									districtCode = jsonCity
											.getString("districtCode");
								}
								mDistrictNames[j + 1] = districtName;
								mDistrictCodes[j + 1] = districtCode;

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
