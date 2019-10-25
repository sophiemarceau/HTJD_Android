package com.huatuo.activity.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
/**
 * 默认就在数据库里创建4张表
 * 
 *
 */
public class DBOpenHelper extends SQLiteOpenHelper {
    private static final String name = "huatuojiadao.db";//数据库名称
    private static final int version = 1;//数据库版本

    public DBOpenHelper(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        Log.e("DBOpenHelper", "DBOpenHelper-------------onCreate------------");
        db.execSQL("CREATE TABLE IF NOT EXISTS "+DatabaseService.search_store_pro_table+
        		" (id integer primary key autoincrement,"+ DatabaseService.s_search_sql+")");
        
        db.execSQL("CREATE TABLE IF NOT EXISTS "+DatabaseService.search_visit_pro_table+
        		" (id integer primary key autoincrement,"+ DatabaseService.s_search_sql+")");
        
        db.execSQL("CREATE TABLE IF NOT EXISTS "+DatabaseService.search_tech_table+
        		" (id integer primary key autoincrement,"+ DatabaseService.s_search_sql+")");
        db.execSQL("CREATE TABLE IF NOT EXISTS "+DatabaseService.search_store_table+
        		" (id integer primary key autoincrement,"+ DatabaseService.s_search_sql+")");
        
    }

  
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
			db.execSQL("drop table if exists "+DatabaseService.search_store_pro_table);
			db.execSQL("drop table if exists "+DatabaseService.search_visit_pro_table);
			db.execSQL("drop table if exists "+DatabaseService.search_tech_table);
			db.execSQL("drop table if exists "+DatabaseService.search_store_table);
			onCreate(db);
		}
        onCreate(db);
        
    }
    


}