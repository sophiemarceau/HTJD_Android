package com.huatuo.activity.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.huatuo.bean.SearchAddressObj;
import com.huatuo.util.CommonUtil;

/**
 * 数据库方法封装，创建表，删除表，数据（增删该查）...
 * 
 */
public class DatabaseService {
	private DBOpenHelper dbOpenHelper;
	public SQLiteDatabase dbconn;
	
	public static String search_tech_table = "search_tech";
	public static String search_store_pro_table = "search_store_pro";
	public static String search_visit_pro_table = "search_visit_pro";
	public static String search_store_table = "search_store";
	
	public static String s_search_sql = "KEYWORDS";
	//选择城市 搜索区域表
	public static String search_address_selectCity_table = "search_address_select_city";
	public static String search_address_selectCity_sql = "cityCode,district,areaName,lat,lng";

	/**
	 * 通过构造方法，实例化DBOpenHelper
	 * */
	public DatabaseService(Context context) {
		dbOpenHelper = new DBOpenHelper(context);
		dbconn = dbOpenHelper.getReadableDatabase();
	}

	public void dropTable(String taleName) {
		dbconn.execSQL("DROP TABLE IF EXISTS " + taleName);

	}

	/**
	 * 关闭指定的表
	 * */
	public void closeDatabase(String DatabaseName) {
		dbconn.close();

	}

	/**
	 * 创建search_store_pro_table 到店项目表
	 * */
	public void create_Search_StorePro_Table() {
		if(!dbconn.isOpen()){
			dbconn = dbOpenHelper.getReadableDatabase();
		}
		String sql = "CREATE TABLE IF NOT EXISTS " + search_store_pro_table
				+ " (id integer primary key autoincrement, " + s_search_sql
				+ ")";
		dbconn.execSQL(sql);
	}
	
	/**
	 * 创建search_visit_pro_table 上门项目表
	 * */
	public void create_Search_VisitPro_Table() {
		if(!dbconn.isOpen()){
			dbconn = dbOpenHelper.getReadableDatabase();
		}
		String sql = "CREATE TABLE IF NOT EXISTS " + search_visit_pro_table
				+ " (id integer primary key autoincrement, " + s_search_sql
				+ ")";
		dbconn.execSQL(sql);
	}
	
	/**
	 * 创建search_tech_table 上门技师表
	 * */
	public void create_Search_Tech_Table() {
		if(!dbconn.isOpen()){
			dbconn = dbOpenHelper.getReadableDatabase();
		}
		String sql = "CREATE TABLE IF NOT EXISTS " + search_tech_table
				+ " (id integer primary key autoincrement, " + s_search_sql
				+ ")";
		dbconn.execSQL(sql);
	}
	
	/**
	 * 创建search_store_table 店铺表
	 * */
	public void create_Search_Store_Table() {
		if(!dbconn.isOpen()){
			dbconn = dbOpenHelper.getReadableDatabase();
		}
		String sql = "CREATE TABLE IF NOT EXISTS " + search_store_table
				+ " (id integer primary key autoincrement, " + s_search_sql
				+ ")";
		dbconn.execSQL(sql);
	}
	
	
	/**
	 * 创建search_address_selectCity_table 选择城市城市区域
	 * */
	public void create_search_area_selectCity_Table() {
		if(!dbconn.isOpen()){
			dbconn = dbOpenHelper.getReadableDatabase();
		}
		String sql = "CREATE TABLE IF NOT EXISTS " + search_address_selectCity_table
				+ " (id integer primary key autoincrement, " + search_address_selectCity_sql
				+ ")";
		dbconn.execSQL(sql);
	}
	
	/**
	 * 插入信息到对应的地址搜索搜索表中
	 * */
	public void save_search_address(String tableName,SearchAddressObj obj) {
		if(null == obj){
			return;
		}
		CommonUtil.log("obj:"+obj);
		if(!dbconn.isOpen()){
			dbconn = dbOpenHelper.getReadableDatabase();
		}
		dbconn.execSQL("insert into " + tableName + " (" + search_address_selectCity_sql
				+ ") values(?,?,?,?,?)", 
				new String[] { obj.getCityCode(),
				obj.getDistrict(),
				obj.getAreaName(),
				obj.getLat(),
				obj.getLng() });
	}

	

	/**
	 * 插入信息到对应的搜索表中
	 * */
	public void save_search_keyWords(String tableName,String keyWord) {
		CommonUtil.log("tableName:"+tableName);
		CommonUtil.log("keyWord:"+keyWord);
		if(!dbconn.isOpen()){
			dbconn = dbOpenHelper.getReadableDatabase();
		}
		dbconn.execSQL("insert into " + tableName + " (" + s_search_sql
				+ ") values(?)", new String[] { keyWord });
	}



	/**
	 * 修改搜索记录中指定行的信息
	 * */
	public void updateSearchTable(String tableName, String keyword, int id) {
		if(!dbconn.isOpen()){
			dbconn = dbOpenHelper.getReadableDatabase();
		}
		dbconn.execSQL("update " + tableName + " set KEYWORD=? where id = ? ",
				new Object[] { keyword, id });
	}


	/**
	 * 修改搜索记录中指定行的信息
	 * */
	public void updateSearch_address_selectCityTable(String tableName, SearchAddressObj obj,int id) {
		if(!dbconn.isOpen()){
			dbconn = dbOpenHelper.getReadableDatabase();
		}
		CommonUtil.log("updateSearch_address_selectCityTable=====tableName:"+tableName);
		dbconn.execSQL("update " + tableName + " set cityCode=?,district=?,areaName=?,lat=?,lng=?  where id = ? ",
				new Object[] { obj.getCityCode(),
				obj.getDistrict(),
				obj.getAreaName(),
				obj.getLat(),
				obj.getLng() , 
				id });
	}

	/**
	 * 查搜索列表,有则返回该表，没有返回null
	 * */
	public List<String> find_search_list(String tableName) {
		CommonUtil.log("tableName:"+tableName);
		if(!dbconn.isOpen()){
			dbconn = dbOpenHelper.getReadableDatabase();
		}
		Cursor cursor = dbconn.rawQuery("select * from " + tableName,
				null);
		return cursorToList_search(cursor);
	}
	
	
	/**
	 * 查询数据封装到list集合当中
	 * 
	 * @param sql
	 * @param selectionArgs
	 * @return
	 */

	private List<String> cursorToList_search(Cursor cursor) {
		if(!dbconn.isOpen()){
			dbconn = dbOpenHelper.getReadableDatabase();
		}
		// TODO Auto-generated method stub
		List<String> list = new ArrayList<String>();
		while (cursor.moveToNext()) {
//			for (int i = 0; i < cursor.getColumnCount(); i++) {
				// Common.log(cursor.getString(i));
			list.add(cursor.getString(1));
//			}

		}

		CommonUtil.log("数据库查询到的：list：" + list);
		cursor.close();
		return list;
	}
	
	

	/**
	 * 查询数据封装到list集合当中
	 * 
	 * @param sql
	 * @param selectionArgs
	 * @return
	 */

	private List<Map<String, Object>> cursorToList(Cursor cursor) {
		if(!dbconn.isOpen()){
			dbconn = dbOpenHelper.getReadableDatabase();
		}
		// TODO Auto-generated method stub
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		while (cursor.moveToNext()) {
			Map<String, Object> map = new HashMap<String, Object>();
			for (int i = 0; i < cursor.getColumnCount(); i++) {
				// Common.log(cursor.getString(i));
				map.put(cursor.getColumnName(i), cursor.getString(i));
			}
			list.add(map);

		}

		CommonUtil.log("数据库查询到的：list：" + list);
		cursor.close();
		return list;
	}

	/**
	 * 查搜索列表,有则返回该表，没有返回null
	 * */
	public List<SearchAddressObj> find_searchAddress_list(String tableName) {
		CommonUtil.log("tableName:"+tableName);
		if(!dbconn.isOpen()){
			dbconn = dbOpenHelper.getReadableDatabase();
		}
		Cursor cursor = dbconn.rawQuery("select * from " + tableName,
				null);
		return cursorToList_searchAddress(cursor);
	}
	
	/**
	 * 查搜索列表,有则返回该表，没有返回null
	 * */
	public List<SearchAddressObj> find_searchAddress_list(String tableName,String keywords) {
		CommonUtil.log("tableName:"+tableName);
		CommonUtil.log("keywords:"+keywords);
		if(!dbconn.isOpen()){
			dbconn = dbOpenHelper.getReadableDatabase();
		}
		
		Cursor cursor = dbconn.rawQuery("select * from " + tableName
				+ " where cityCode=?", new String[] { keywords },
				null);
		return cursorToList_searchAddress(cursor);
	}
	
	
	/**
	 * 查询数据封装到list集合当中
	 * 
	 * @param sql
	 * @param selectionArgs
	 * @return
	 */

	private List<SearchAddressObj> cursorToList_searchAddress(Cursor cursor) {
		if(!dbconn.isOpen()){
			dbconn = dbOpenHelper.getReadableDatabase();
		}
		// TODO Auto-generated method stub
		List<SearchAddressObj> list = new ArrayList<SearchAddressObj>();
		while (cursor.moveToNext()) {
//			for (int i = 0; i < cursor.getColumnCount(); i++) {
				
			SearchAddressObj searchAddressObj = new SearchAddressObj();
			String id = cursor.getString(0);
			String cityCode = cursor.getString(1);
			String district = cursor.getString(2);
			String areaName = cursor.getString(3);
			String lat = cursor.getString(4);
			String lng = cursor.getString(5);
			CommonUtil.log("数据库查询到的：id：" + id);
			CommonUtil.log("数据库查询到的：cityCode：" + cityCode);
			CommonUtil.log("数据库查询到的：district：" + district);
			CommonUtil.log("数据库查询到的：areaName：" + areaName);
			CommonUtil.log("数据库查询到的：lat：" + lat);
			CommonUtil.log("数据库查询到的：lng：" + lng);
			
			searchAddressObj.setId(id);
			searchAddressObj.setCityCode(cityCode);
			searchAddressObj.setDistrict(district);
			searchAddressObj.setAreaName(areaName);
			searchAddressObj.setLat(lat);
			searchAddressObj.setLng(lng);
			
//			}
			list.add(searchAddressObj);

		}

		CommonUtil.log("数据库查询到的：list：" + list);
		cursor.close();
		return list;
	}
	

	/**
	 * 删除指定表中的指定行信息
	 * */
	public Boolean deleteItemData(String tableName, Integer id) {
		if(!dbconn.isOpen()){
			dbconn = dbOpenHelper.getReadableDatabase();
		}
		try {
			dbconn.execSQL("delete from " + tableName + " where id=?",
					new Object[] { id });
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 删除指定表中的地址搜索指定行信息
	 * */
	public Boolean deleteItemDataFromSearchAddress(String tableName, String keyWords) {
		if(!dbconn.isOpen()){
			dbconn = dbOpenHelper.getReadableDatabase();
		}
		try {
			dbconn.execSQL("delete from " + tableName + " where cityCode=?",
					new Object[] { keyWords });
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}


	/**
	 * 查找search表中是否包含指定id的信息,有则返回该表，没有返回null
	 * */
	public String findBySearchKeyWords(String tableName, String keywords) {
		CommonUtil.log("==========================查询关键字："+keywords);
		if(!dbconn.isOpen()){
			dbconn = dbOpenHelper.getReadableDatabase();
		}
		Cursor cursor = dbconn.rawQuery("select KEYWORD from " + tableName
				+ " where KEYWORD=?", new String[] { keywords });
		if (cursor.moveToNext()) {

			CommonUtil.log("========================根据关键字查询到的记录条数："+cursor.getCount());
			String string = cursor.getString(0);
			CommonUtil.log("========================根据关键字查询到的记录string："+string);
			
			cursor.close();
			return string;
		}else {
			CommonUtil.log("========================根据关键字查询到的记录条数："+cursor.getCount());
		}
		cursor.close();
		return null;
	}


	/**
	 * 计算指定表的大小
	 * */
	public long getDataCount(String tableName) {
		Cursor cursor = dbOpenHelper.getReadableDatabase().rawQuery(
				"select count(*) from " + tableName, null);
		cursor.moveToFirst();
		return cursor.getLong(0);
	}

	/**
	 * 清空表的时候，并把自增列归零
	 * 
	 * @param tbName
	 * @return
	 */
	public boolean dbClear(String tbName) {
		if(!dbconn.isOpen()){
			dbconn = dbOpenHelper.getReadableDatabase();
		}
		try {
			String sql = "delete from " + tbName
					+ "; update sqlite_sequence SET seq = 0 where name = "
					+ tbName;
			dbconn.execSQL(sql);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 关闭数据库
	 */
	public void dbClose() {
		if (dbconn != null) {
			dbconn.close();
		}
	}

}