package com.huatuo.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HandleServiceTimeUtil {
	private static ArrayList<JSONObject> serviceTimeList1;
	private static ArrayList<JSONObject> serviceTimeList2;
	private static ArrayList<JSONObject> serviceTimeList3;
	private static ArrayList<JSONObject> serviceTimeList4;

	public static String[] arr_new_days;// 存储转型后的日
	public static String[] arr_old_days;// 存储获取日
	public static String[] arr_clock1;
	public static String[] arr_clock2;
	public static String[] arr_clock3;
	public static String[] arr_clock4;
	public static Map<String, String[]> map_day_Clocks;
	public static Map<String, String[]> map_day_canService;
	public static Map<String, String[]> map_day_trafficFee;

	public static String current_old_day = "";// 原始类型
	public static String current_new_day = "";// 今天/明天类型
	public static String current_clock = "";
	public static String current_canService = "";
	public static String current_trafficFee = "0";
	
	public static final String UNAPPOINT = "不可约";
	public static final String FLAG_UNAPPOINT = "0";//不可约
	public static final String FLAG_CANAPPOINT = "1";//可约
	public static void getDayArray(JSONObject jsonObject) {
		
		init();
		CommonUtil
				.log("---------返回服务时间列表receiveServiceTimeList(jsonObject, serviceTimeList1");
		receiveServiceTimeList(jsonObject, "serviceTimeList1");
		receiveServiceTimeList(jsonObject, "serviceTimeList2");
		receiveServiceTimeList(jsonObject, "serviceTimeList3");
		receiveServiceTimeList(jsonObject, "serviceTimeList4");
		CommonUtil.log("---------返回服务时间列表arr_new_days:"+arr_new_days);
		CommonUtil.log("---------返回服务时间列表arr_old_days:"+arr_old_days);
		// 服务时间列表
		// CommonUtil.log("---------返回服务时间列表serviceTimeList1是否为空："
		// + serviceTimeList1);
		// CommonUtil.log("---------返回服务时间列表serviceTimeList2是否为空："
		// + serviceTimeList2);
		// CommonUtil.log("---------返回服务时间列表serviceTimeList3是否为空："
		// + serviceTimeList3);
		// CommonUtil.log("---------返回服务时间列表serviceTimeList4是否为空："
		// + serviceTimeList4);
		// CommonUtil.log("---------返回服务时间列表arr_old_days是否为空：" + arr_old_days);
		// CommonUtil.log("---------返回服务时间列表map_day_Clocks是否为空：" +
		// map_day_Clocks);
		// CommonUtil.log("---------返回服务时间列表map_day_canService是否为空：" +
		// map_day_canService);
		// CommonUtil.log("---------返回服务时间列表map_day_trafficFee是否为空：" +
		// map_day_trafficFee);

	}

	public static void init() {
		
		//初始化--避免残留上次数据
		current_old_day = "";// 原始类型
		current_new_day = "";// 今天/明天类型
		current_clock = "";
		current_canService = "";
		current_trafficFee = "0";
		
		arr_new_days = new String[4];// 存储转型后的日
		arr_old_days = new String[4];// 存储获取日
		
		map_day_Clocks = new HashMap<String, String[]>();
		map_day_canService = new HashMap<String, String[]>();
		map_day_trafficFee = new HashMap<String, String[]>();
		
		serviceTimeList1 = new ArrayList<JSONObject>();
		serviceTimeList2 = new ArrayList<JSONObject>();
		serviceTimeList3 = new ArrayList<JSONObject>();
		serviceTimeList4 = new ArrayList<JSONObject>();
	}

	/**
	 * 封装服务时间对象列表
	 * 
	 * @param jsonObject
	 * @param serviceTimeList
	 */
	public static void receiveServiceTimeList(JSONObject jsonObject,
			String serviceTimeList) {

		// CommonUtil.log("String----serviceTimeList------------------->"+
		// serviceTimeList);
		// 服务时间列表

		if (null != jsonObject && !("").equals(jsonObject)) {
			JSONArray serviceTimeListArray = jsonObject
					.optJSONArray(serviceTimeList);
			 CommonUtil.log("serviceTimeListArray------------------->"+
			 serviceTimeListArray);
			JSONObject json;
			String day = "";
			String clock = "";
			String canService = "";
			String trafficFee = "";
			if (serviceTimeListArray == null
					|| serviceTimeListArray.length() == 0) {
				return;
			} else {
				// 每个天对应的时刻表数组
				String[] arr_clock = new String[serviceTimeListArray.length()];
				String[] arr_canService = new String[serviceTimeListArray
						.length()];
				String[] arr_trafficFee = new String[serviceTimeListArray
						.length()];
				for (int i = 0; i < serviceTimeListArray.length(); i++) {
					try {
						json = serviceTimeListArray.getJSONObject(i);
//						date	日期	ANS	16	M	日期
//						timeSlot	时间点	ANS	16	M	时间点
//						isAvailable	能否服务	ANS	16	M	0表示不能服务，1表示可以服务
//						transportationFee	交通费	ANS	16	M	0表示没有交通费，如果根据钟数算出技师需要打车，则此项不为0
						// 只存储列表一次天
						if (i == 0) {
							day = json.optString("date", "");
							CommonUtil.log("获取到的天：" + day);
						}
						// 存储这一天对应的时刻列表
						clock = json.optString("timeSlot", "");

						// 存储能否服务
						canService = json.optString("isAvailable", "");
						// CommonUtil.log("获取到的天："+day);
						arr_canService[i] = canService;

						//不可约
						if ((FLAG_UNAPPOINT).equals(canService)) {
//							clock = json.optString("timeSlot", "") + UNAPPOINT;
							clock = json.optString("timeSlot", "") ;
//							clock = " ";
						}

						CommonUtil.log("获取到的clock：" + clock);
						arr_clock[i] = clock;

						// 存储交通费
						trafficFee = json.optString("transportationFee", "0");//交通费没有赋值为 0
						// CommonUtil.log("获取到的天："+day);
						arr_trafficFee[i] = trafficFee;

						
						
						// 分别封装 每一天对应的 那个时间列表
						if (serviceTimeList.equals("serviceTimeList1")) {
							serviceTimeList1.add(json);
							if (i == 0) {
								arr_old_days[0] = day;
								arr_new_days[0] = "今天";
							}

						} else if (serviceTimeList.equals("serviceTimeList2")) {
							serviceTimeList2.add(json);
							if (i == 0) {
								arr_old_days[1] = day;
								arr_new_days[1] = "明天";
							}
						} else if (serviceTimeList.equals("serviceTimeList3")) {
							serviceTimeList3.add(json);
							if (i == 0) {
								arr_old_days[2] = day;
								arr_new_days[2] = day;
							}
						} else {
							if (serviceTimeList.equals("serviceTimeList4")) {
								serviceTimeList4.add(json);
								if (i == 0) {
									arr_old_days[3] = day;
									arr_new_days[3] = day;
								}
							}

						}

					} catch (JSONException e) {
						e.printStackTrace();
						continue;
					}
				}

				// CommonUtil.log("arr_clock------------------->"+ arr_clock);
				// CommonUtil.log("arr_clock.length------------------->"+
				// arr_clock.length);
				map_day_Clocks.put(day, arr_clock);
				map_day_canService.put(day, arr_canService);
				map_day_trafficFee.put(day, arr_trafficFee);
			}
		}
		
	}
}
