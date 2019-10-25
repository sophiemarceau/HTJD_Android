package com.huatuo.citylist;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.huatuo.util.CommonUtil;
import com.huatuo.util.JsonUtil;


public class GetCityListUtil {
	private static GetCityListUtil instance;
//	private static List<String> fisrtCharOfCityList ;
	private ArrayList<JSONObject> cityList;
	public static GetCityListUtil getInstance() {
		if (instance == null) {
			synchronized (GetCityListUtil.class) {
				if (instance == null) {
					instance = new GetCityListUtil();
				}
			}
		}

		return instance;
	}
	
	public List<Content> addCityToList(String cityListStr) {
//		fisrtCharOfCityList = new ArrayList<String>();
		
		if (!TextUtils.isEmpty(cityListStr)) {
			JSONObject jsonObject = null;
			try {
				jsonObject = new JSONObject(cityListStr);
				if (null != jsonObject) {
					// 城市列表
					JSONArray cityListArray = jsonObject
							.optJSONArray("cityList");
					cityList = JsonUtil.jsonArray2List(cityListArray);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		
		// 初始化数据
		List<Content> list = new ArrayList<Content>();

		if (!CommonUtil.emptyListToString3(cityList)) {
			Content content = null;
			for (int j = 0; j < cityList.size(); j++) {
				JSONObject jsonObject = cityList.get(j);

				// "groupList": [],
				// "initialCharacter": "B"
				JSONArray cityListArray = jsonObject.optJSONArray("groupList");
				String initialCharacter = jsonObject.optString(
						"initialCharacter", "");
//				fisrtCharOfCityList.add(initialCharacter);
				ArrayList<JSONObject> area_list = JsonUtil
						.jsonArray2List(cityListArray);

				if (!CommonUtil.emptyListToString3(area_list)) {
					for (int k = 0; k < area_list.size(); k++) {
						JSONObject jsonObject2 = area_list.get(k);
						String name = jsonObject2.optString("name", "");
						String code = jsonObject2.optString("code", "");
						String servingStatus = jsonObject2.optString(
								"servingStatus", "");
//						CommonUtil.log("------------------------name:" + name);
						content = new Content(initialCharacter, name, code,
								servingStatus);
						list.add(content);
					}
				}
			}
			// }
//			CommonUtil.log("------------------------list:" + list);
			
//			HashSet h  =   new  HashSet(fisrtCharOfCityList);
//			fisrtCharOfCityList.clear();
//			fisrtCharOfCityList.addAll(h);
//			CommonUtil.log("城市列表首字母集合：fisrtCharOfCityList："+fisrtCharOfCityList);
			return list;
		}

		return null;

	}
}
