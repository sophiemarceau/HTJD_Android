package com.huatuo.activity.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.huatuo.bean.SearchAddressObj;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Constants;

public class SearchDBUtil {
	private static SearchDBUtil instance;
	private DatabaseService ds;
	private final int SIZE = 3;

	public static SearchDBUtil getInstance() {
		if (instance == null) {
			synchronized (SearchDBUtil.class) {
				if (instance == null) {
					instance = new SearchDBUtil();
				}
			}
		}

		return instance;
	}

	/**
	 * 插入keywords
	 * 
	 * @param context
	 * @param keyWords
	 */
	public void insertDB(Context context, int tableType, String keyWords) {
		ds = new DatabaseService(context);
		String tableName = getTableName(tableType);// 获取对应的biao
		List<String> keyword_list = findDBOfKeyWords(context, tableType);
		CommonUtil.log("keyword_list:" + keyword_list);
		boolean isHas = false;
		if (!CommonUtil.emptyListToString3(keyword_list)) {
			// 避免存储重复数据
			for (int i = 0; i < keyword_list.size(); i++) {
				// 存储过
				if (keyWords.trim().equals(keyword_list.get(i).trim())) {
					isHas = true;
					break;
				} else {
					isHas = false;
				}
			}

			CommonUtil.log("==================isHas:" + isHas);
			if (!isHas) {
				ds.save_search_keyWords(tableName, keyWords.trim());
			}
		} else {
			ds.save_search_keyWords(tableName, keyWords.trim());
		}
	}

	/**
	 * 插入keywords
	 * 
	 * @param context
	 * @param keyWords
	 */
	public void insertDB(Context context, int tableType,
			SearchAddressObj searchAddressObj) {
		// 要存储的搜索区域信息
		String cityCode = searchAddressObj.getCityCode();
		String district = searchAddressObj.getDistrict();
		String areaName = searchAddressObj.getAreaName();
		String lat = searchAddressObj.getLat();
		String lng = searchAddressObj.getLng();

		ds = new DatabaseService(context);
		String tableName = getTableName(tableType);// 获取对应的biao
		List<SearchAddressObj> address_list = findDBOfSearchAddres(context,
				tableType, cityCode);
		CommonUtil.log("cityCode：" + cityCode + "====address_list:" + address_list);

		boolean isHas = false;
		if (!CommonUtil.emptyListToString3(address_list)) {
			// 避免存储重复数据并且同一城市不能超过二十条数据
			CommonUtil.log("cityCode：" + cityCode + "====address_list.size():"
					+ address_list.size());

			if (address_list.size() < SIZE) {
				for (int i = 0; i < address_list.size(); i++) {
					SearchAddressObj searchAddressObj2 = address_list.get(i);
					String cityCode2 = searchAddressObj2.getCityCode();
					String district2 = searchAddressObj2.getDistrict();
					String areaName2 = searchAddressObj2.getAreaName();
					String lat2 = searchAddressObj2.getLat();
					String lng2 = searchAddressObj2.getLng();

					// 存储过
					if (cityCode.equals(cityCode2) && district.equals(district2)
							&& areaName.equals(areaName2)) {
						isHas = true;
						break;
					} else {
						isHas = false;
						continue;
					}
				}
			} else {
				isHas = true;
				updateDataOfSelectCity(tableName, address_list,
						searchAddressObj);

			}

			CommonUtil.log("===searchAddressObj===============isHas:" + isHas);
			if (!isHas) {
				ds.save_search_address(tableName, searchAddressObj);
			}
		} else {
			ds.save_search_address(tableName, searchAddressObj);
		}
	}

	/**
	 * 获取对应列表
	 * 
	 * @param type
	 * @return
	 */
	public String getTableName(int type) {
		String tableName = "";
		switch (type) {
		case Constants.SEARCH_STORE_PRO:
			tableName = DatabaseService.search_store_pro_table;
			break;
		case Constants.SEARCH_VISIT_PRO:
			tableName = DatabaseService.search_visit_pro_table;
			break;
		case Constants.SEARCH_TECH:
			tableName = DatabaseService.search_tech_table;
			break;
		case Constants.SEARCH_STORE:
			tableName = DatabaseService.search_store_table;
			break;
		case Constants.SEARCH_ADDRESS_SELECTCITY:
			tableName = DatabaseService.search_address_selectCity_table;
			break;
		}

		return tableName;
	}

	public List<String> findDBOfKeyWords(Context context, int type) {
		ds = new DatabaseService(context);

		List<String> keyword_list = new ArrayList<String>();
		switch (type) {
		case Constants.SEARCH_STORE_PRO:
			keyword_list = ds
					.find_search_list(DatabaseService.search_store_pro_table);
			break;
		case Constants.SEARCH_VISIT_PRO:
			keyword_list = ds
					.find_search_list(DatabaseService.search_visit_pro_table);
			break;
		case Constants.SEARCH_TECH:
			keyword_list = ds
					.find_search_list(DatabaseService.search_tech_table);
			break;
		case Constants.SEARCH_STORE:
			keyword_list = ds
					.find_search_list(DatabaseService.search_store_table);
			break;
		}

		if (!CommonUtil.emptyListToString3(keyword_list)) {
			return keyword_list;
		} else {
			return null;
		}

	}

	public List<SearchAddressObj> findDBOfSearchAddres(Context context, int type) {
		ds = new DatabaseService(context);

		List<SearchAddressObj> address_list = new ArrayList<SearchAddressObj>();
		switch (type) {
		case Constants.SEARCH_ADDRESS_SELECTCITY:
			address_list = ds
					.find_searchAddress_list(DatabaseService.search_address_selectCity_table);
			break;
		}

		if (!CommonUtil.emptyListToString3(address_list)) {
			return address_list;
		} else {
			return null;
		}

	}

	/**
	 * 根据城市查询
	 * 
	 * @param context
	 * @param type
	 * @param city
	 * @return
	 */
	public List<SearchAddressObj> findDBOfSearchAddres(Context context,
			int type, String cityCode) {
		ds = new DatabaseService(context);
		List<SearchAddressObj> address_list = new ArrayList<SearchAddressObj>();
		switch (type) {
		case Constants.SEARCH_ADDRESS_SELECTCITY:
			address_list = ds.find_searchAddress_list(
					DatabaseService.search_address_selectCity_table, cityCode);
			break;
		}

		if (!CommonUtil.emptyListToString3(address_list)) {
			return address_list;
		} else {
			return null;
		}

	}

	/**
	 * 更新数据库列表数据
	 * 
	 * @param TBNAME
	 * @param list
	 * @param keyword
	 */
	private void updateDataOfSelectCity(String TBNAME,
			List<SearchAddressObj> list, SearchAddressObj obj) {
		String id= "";
		int ID = 0;
		for (int i = 1; i < SIZE; i++) {
			//根据id进行跟新 将最早的一个更新掉
			
			id = list.get(i-1).getId();
			ID = Integer.parseInt(id);
			ds.updateSearch_address_selectCityTable(TBNAME, list.get(i), ID);
		}
		
		id = list.get(SIZE - 1).getId();
		ID = Integer.parseInt(id);
		ds.updateSearch_address_selectCityTable(TBNAME, obj, ID);
	}

	/**
	 * 清空对应搜索记录
	 * 
	 * @param context
	 * @param type
	 */
	public boolean clearSearchHistory(Context context, int type) {
		ds = new DatabaseService(context);
		boolean isClearSuccess = false;
		switch (type) {
		case Constants.SEARCH_STORE_PRO:
			isClearSuccess = ds.dbClear(DatabaseService.search_store_pro_table);
			break;
		case Constants.SEARCH_VISIT_PRO:
			isClearSuccess = ds.dbClear(DatabaseService.search_visit_pro_table);
			break;
		case Constants.SEARCH_TECH:
			isClearSuccess = ds.dbClear(DatabaseService.search_tech_table);
			break;
		case Constants.SEARCH_STORE:
			isClearSuccess = ds.dbClear(DatabaseService.search_store_table);
			break;
		case Constants.SEARCH_ADDRESS_SELECTCITY:
			isClearSuccess = ds.dbClear(DatabaseService.search_address_selectCity_table);
			break;

		}

		return isClearSuccess;
	}
	/**
	 * 清空对应搜索记录
	 * 
	 * @param context
	 * @param type
	 */
	public boolean clearSearchHistoryOfSearchAddress(Context context, int type,String cityCode) {
		ds = new DatabaseService(context);
		boolean isClearSuccess = false;
		switch (type) {
		case Constants.SEARCH_ADDRESS_SELECTCITY:
			isClearSuccess = ds
					.deleteItemDataFromSearchAddress(DatabaseService.search_address_selectCity_table,cityCode);
			break;

		}

		return isClearSuccess;
	}
	/**
	 * 清空对应搜索记录
	 * 
	 * @param context
	 * @param type
	 */
	public void closeTable(Context context, int type) {
		ds = new DatabaseService(context);
		switch (type) {
		case Constants.SEARCH_STORE_PRO:
			ds.closeDatabase(DatabaseService.search_store_pro_table);
			break;
		case Constants.SEARCH_VISIT_PRO:
			ds.closeDatabase(DatabaseService.search_visit_pro_table);
			break;
		case Constants.SEARCH_TECH:
			ds.closeDatabase(DatabaseService.search_tech_table);
			break;
		case Constants.SEARCH_STORE:
			ds.closeDatabase(DatabaseService.search_store_table);
			break;
		case Constants.SEARCH_ADDRESS_SELECTCITY:
			ds.closeDatabase(DatabaseService.search_address_selectCity_table);
			break;

		}
	}

}
