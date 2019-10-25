package com.huatuo.util;

import java.util.HashMap;

import com.umeng.analytics.MobclickAgent;

import android.content.Context;

public class UmengEventUtil {
	public static void visitor_login(Context context) {
		MobclickAgent.onEvent(context, "VISITOR_LOGIN");
	}

	public static void visitor_getcode(Context context) {
		MobclickAgent.onEvent(context, "VISITOR_GETCODE");
	}

	public static void visitor_protocol(Context context) {
		MobclickAgent.onEvent(context, "VISITOR_PROTOCOL");
	}

	public static void tab_store(Context context) {
		MobclickAgent.onEvent(context, "TAB_STORE");
	}

	public static void tab_door(Context context) {
		MobclickAgent.onEvent(context, "TAB_DOOR");
	}

	public static void tab_discover(Context context) {
		MobclickAgent.onEvent(context, "TAB_DISCOVER");
	}

	public static void tab_order(Context context) {
		MobclickAgent.onEvent(context, "TAB_ORDER");
	}

	public static void tab_my(Context context) {
		MobclickAgent.onEvent(context, "TAB_MY");
	}

	public static void store_city(Context context) {
		MobclickAgent.onEvent(context, "STORE_CITY");
	}

	public static void store_qr(Context context) {
		MobclickAgent.onEvent(context, "STORE_QR");
	}

	public static void store_search(Context context) {
		MobclickAgent.onEvent(context, "STORE_SEARCH");
	}

	public static void store_horizontal_tab(Context context, HashMap<String, String> map) {
		MobclickAgent.onEvent(context, "STORE_HORIZONTAL_TAB", map);
	}

	public static void store_screen(Context context) {
		MobclickAgent.onEvent(context, "STORE_SCREEN");
	}

	public static void store_screen_near(Context context) {
		MobclickAgent.onEvent(context, "STORE_SCREEN_NEAR");
	}

	public static void store_screen_evaluate(Context context) {
		MobclickAgent.onEvent(context, "STORE_SCREEN_EVALUATE");
	}

	public static void store_screen_price(Context context) {
		MobclickAgent.onEvent(context, "STORE_SCREEN_PRICE");
	}

	public static void store_position(Context context) {
		MobclickAgent.onEvent(context, "STORE_POSITION");
	}

	public static void store_search_choose(Context context) {
		MobclickAgent.onEvent(context, "STORE_SEARCH_CHOOSE");
	}

	public static void store_search_search(Context context) {
		MobclickAgent.onEvent(context, "STORE_SEARCH_SEARCH");
	}

	public static void store_search_delete(Context context) {
		MobclickAgent.onEvent(context, "STORE_SEARCH_DELETE");
	}

	public static void store_search_choose_store(Context context) {
		MobclickAgent.onEvent(context, "STORE_SEARCH_CHOOSE_STORE");
	}

	public static void store_search_choose_tichnician(Context context) {
		MobclickAgent.onEvent(context, "STORE_SEARCH_CHOOSE_TICHNICIAN");
	}

	public static void store_search_choose_project(Context context) {
		MobclickAgent.onEvent(context, "STORE_SEARCH_CHOOSE_PROJECT");
	}

	public static void store_storedetail(Context context) {
		MobclickAgent.onEvent(context, "STORE_STOREDETAIL");
	}

	public static void store_storedetail_collect(Context context) {
		MobclickAgent.onEvent(context, "STORE_STOREDETAIL_COLLECT");
	}

	public static void store_storedetail_share(Context context) {
		MobclickAgent.onEvent(context, "STORE_STOREDETAIL_SHARE");
	}

	public static void store_storedetail_pay(Context context) {
		MobclickAgent.onEvent(context, "STORE_STOREDETAIL_PAY");
	}

	public static void store_storedetail_coupon(Context context) {
		MobclickAgent.onEvent(context, "STORE_STOREDETAIL_COUPON");
	}

	public static void store_storedetail_phone(Context context) {
		MobclickAgent.onEvent(context, "STORE_STOREDETAIL_PHONE");
	}

	public static void store_storedetail_adress(Context context) {
		MobclickAgent.onEvent(context, "STORE_STOREDETAIL_ADRESS");
	}

	public static void store_storedetail_comment(Context context) {
		MobclickAgent.onEvent(context, "STORE_STOREDETAIL_COMMENT");
	}

	public static void store_storedetail_projectdetail(Context context) {
		MobclickAgent.onEvent(context, "STORE_STOREDETAIL_PROJECTDETAIL");
	}

	public static void store_storedetail_projectdetail_collect(Context context) {
		MobclickAgent.onEvent(context, "STORE_STOREDETAIL_PROJECTDETAIL_COLLECT");
	}

	public static void store_storedetail_projectdetail_share(Context context) {
		MobclickAgent.onEvent(context, "STORE_STOREDETAIL_PROJECTDETAIL_SHARE");
	}

	public static void store_storedetail_projectdetail_storedetail(Context context) {
		MobclickAgent.onEvent(context, "STORE_STOREDETAIL_PROJECTDETAIL_STOREDETAIL");
	}

	public static void store_storedetail_projectdetail_adress(Context context) {
		MobclickAgent.onEvent(context, "STORE_STOREDETAIL_PROJECTDETAIL_ADRESS");
	}

	public static void store_storedetail_projectdetail_comment(Context context) {
		MobclickAgent.onEvent(context, "STORE_STOREDETAIL_PROJECTDETAIL_COMMENT");
	}

	public static void store_storedetail_projectdetail_placeorder(Context context) {
		MobclickAgent.onEvent(context, "STORE_STOREDETAIL_PROJECTDETAIL_PLACEORDER");
	}

	public static void store_storedetail_projectdetail_placeorder_addtime(Context context) {
		MobclickAgent.onEvent(context, "STORE_STOREDETAIL_PROJECTDETAIL_PLACEORDER_ADDTIME");
	}

	public static void store_storedetail_projectdetail_placeorder_reducetime(Context context) {
		MobclickAgent.onEvent(context, "STORE_STOREDETAIL_PROJECTDETAIL_PLACEORDER_REDUCETIME");
	}

	public static void store_storedetail_projectdetail_placeorder_time(Context context) {
		MobclickAgent.onEvent(context, "STORE_STOREDETAIL_PROJECTDETAIL_PLACEORDER_TIME");
	}

	public static void store_storedetail_projectdetail_placeorder_coupon(Context context) {
		MobclickAgent.onEvent(context, "STORE_STOREDETAIL_PROJECTDETAIL_PLACEORDER_COUPON");
	}

	public static void store_storedetail_projectdetail_placeorder_pay(Context context) {
		MobclickAgent.onEvent(context, "STORE_STOREDETAIL_PROJECTDETAIL_PLACEORDER_PAY");
	}

	public static void store_storedetail_projectdetail_placeorder_account(Context context) {
		MobclickAgent.onEvent(context, "STORE_STOREDETAIL_PROJECTDETAIL_PLACEORDER_ACCOUNT");
	}

	public static void store_storedetail_projectdetail_placeorder_zhifubao(Context context) {
		MobclickAgent.onEvent(context, "STORE_STOREDETAIL_PROJECTDETAIL_PLACEORDER_ZHIFUBAO");
	}

	public static void store_storedetail_projectdetail_placeorder_weixin(Context context) {
		MobclickAgent.onEvent(context, "STORE_STOREDETAIL_PROJECTDETAIL_PLACEORDER_WEIXIN");
	}

	public static void door_city(Context context) {
		MobclickAgent.onEvent(context, "DOOR_CITY");
	}

	public static void door_search(Context context) {
		MobclickAgent.onEvent(context, "DOOR_SEARCH");
	}
	
	public static void door_horizontal_tab(Context context, HashMap<String, String> map) {
		MobclickAgent.onEvent(context, "DOOR_HORIZONTAL_TAB", map);
	}

	public static void door_screen(Context context) {
		MobclickAgent.onEvent(context, "DOOR_SCREEN");
	}

	public static void door_photo(Context context) {
		MobclickAgent.onEvent(context, "DOOR_PHOTO");
	}

	public static void door_tichniciandetail(Context context) {
		MobclickAgent.onEvent(context, "DOOR_TICHNICIANDETAIL");
	}

	public static void door_position(Context context) {
		MobclickAgent.onEvent(context, "DOOR_POSITION");
	}

	public static void door_screen_near(Context context) {
		MobclickAgent.onEvent(context, "DOOR_SCREEN_NEAR");
	}

	public static void door_screen_evaluate(Context context) {
		MobclickAgent.onEvent(context, "DOOR_SCREEN_EVALUATE");
	}

	public static void door_screen_order(Context context) {
		MobclickAgent.onEvent(context, "DOOR_SCREEN_ORDER");
	}

	public static void door_search_choose(Context context) {
		MobclickAgent.onEvent(context, "DOOR_SEARCH_CHOOSE");
	}

	public static void door_search_choose_store(Context context) {
		MobclickAgent.onEvent(context, "DOOR_SEARCH_CHOOSE_STORE");
	}

	public static void door_search_choose_tichnician(Context context) {
		MobclickAgent.onEvent(context, "DOOR_SEARCH_CHOOSE_TICHNICIAN");
	}

	public static void door_search_choose_project(Context context) {
		MobclickAgent.onEvent(context, "DOOR_SEARCH_CHOOSE_PROJECT");
	}

	public static void door_search_search(Context context) {
		MobclickAgent.onEvent(context, "DOOR_SEARCH_SEARCH");
	}

	public static void door_search_delete(Context context) {
		MobclickAgent.onEvent(context, "DOOR_SEARCH_DELETE");
	}

	public static void door_tichniciandetail_collect(Context context) {
		MobclickAgent.onEvent(context, "DOOR_TICHNICIANDETAIL_COLLECT");
	}

	public static void door_tichniciandetail_share(Context context) {
		MobclickAgent.onEvent(context, "DOOR_TICHNICIANDETAIL_SHARE");
	}

	public static void door_tichniciandetail_more(Context context) {
		MobclickAgent.onEvent(context, "DOOR_TICHNICIANDETAIL_MORE");
	}

	public static void door_tichniciandetail_comment(Context context) {
		MobclickAgent.onEvent(context, "DOOR_TICHNICIANDETAIL_COMMENT");
	}

	public static void door_tichniciandetail_projectdetail(Context context) {
		MobclickAgent.onEvent(context, "DOOR_TICHNICIANDETAIL_PROJECTDETAIL");
	}

	public static void door_tichniciandetail_projectdetail_collect(Context context) {
		MobclickAgent.onEvent(context, "DOOR_TICHNICIANDETAIL_PROJECTDETAIL_COLLECT");
	}

	public static void door_tichniciandetail_projectdetail_share(Context context) {
		MobclickAgent.onEvent(context, "DOOR_TICHNICIANDETAIL_PROJECTDETAIL_SHARE");
	}

	public static void door_tichniciandetail_projectdetail_comment(Context context) {
		MobclickAgent.onEvent(context, "DOOR_TICHNICIANDETAIL_PROJECTDETAIL_COMMENT");
	}

	public static void door_tichniciandetail_projectdetail_tichniciandetail(Context context) {
		MobclickAgent.onEvent(context, "DOOR_TICHNICIANDETAIL_PROJECTDETAIL_TICHNICIANDETAIL");
	}

	public static void door_tichniciandetail_projectdetail_placeorder(Context context) {
		MobclickAgent.onEvent(context, "DOOR_TICHNICIANDETAIL_PROJECTDETAIL_PLACEORDER");
	}

	public static void door_tichniciandetail_projectdetail_placeorder_reducetime(Context context) {
		MobclickAgent.onEvent(context, "DOOR_TICHNICIANDETAIL_PROJECTDETAIL_PLACEORDER_REDUCETIME");
	}

	public static void door_tichniciandetail_projectdetail_placeorder_addtime(Context context) {
		MobclickAgent.onEvent(context, "DOOR_TICHNICIANDETAIL_PROJECTDETAIL_PLACEORDER_ADDTIME");
	}

	public static void door_tichniciandetail_projectdetail_placeorder_time(Context context) {
		MobclickAgent.onEvent(context, "DOOR_TICHNICIANDETAIL_PROJECTDETAIL_PLACEORDER_TIME");
	}

	public static void door_tichniciandetail_projectdetail_placeorder_adress(Context context) {
		MobclickAgent.onEvent(context, "DOOR_TICHNICIANDETAIL_PROJECTDETAIL_PLACEORDER_ADRESS");
	}

	public static void door_tichniciandetail_projectdetail_placeorder_coupon(Context context) {
		MobclickAgent.onEvent(context, "DOOR_TICHNICIANDETAIL_PROJECTDETAIL_PLACEORDER_COUPON");
	}

	public static void door_tichniciandetail_projectdetail_placeorder_pay(Context context) {
		MobclickAgent.onEvent(context, "DOOR_TICHNICIANDETAIL_PROJECTDETAIL_PLACEORDER_PAY");
	}

	public static void door_tichniciandetail_projectdetail_placeorder_account(Context context) {
		MobclickAgent.onEvent(context, "DOOR_TICHNICIANDETAIL_PROJECTDETAIL_PLACEORDER_ACCOUNT");
	}

	public static void door_tichniciandetail_projectdetail_placeorder_zhifubao(Context context) {
		MobclickAgent.onEvent(context, "DOOR_TICHNICIANDETAIL_PROJECTDETAIL_PLACEORDER_ZHIFUBAO");
	}

	public static void door_tichniciandetail_projectdetail_placeorder_weixin(Context context) {
		MobclickAgent.onEvent(context, "DOOR_TICHNICIANDETAIL_PROJECTDETAIL_PLACEORDER_WEIXIN");
	}

	public static void discover_detail(Context context) {
		MobclickAgent.onEvent(context, "DISCOVER_DETAIL");
	}

	public static void discover_detail_collect(Context context) {
		MobclickAgent.onEvent(context, "DISCOVER_DETAIL_COLLECT");
	}

	public static void discover_detail_share(Context context) {
		MobclickAgent.onEvent(context, "DISCOVER_DETAIL_SHARE");
	}

	public static void order_nopay(Context context) {
		MobclickAgent.onEvent(context, "ORDER_NOPAY");
	}

	public static void order_waitserve(Context context) {
		MobclickAgent.onEvent(context, "ORDER_WAITSERVE");
	}

	public static void order_waitcommet(Context context) {
		MobclickAgent.onEvent(context, "ORDER_WAITCOMMET");
	}

	public static void order_all(Context context) {
		MobclickAgent.onEvent(context, "ORDER_ALL");
	}

	public static void order_pay(Context context) {
		MobclickAgent.onEvent(context, "ORDER_PAY");
	}

	public static void order_deleteorder(Context context) {
		MobclickAgent.onEvent(context, "ORDER_DELETEORDER");
	}

	public static void order_storedetail(Context context) {
		MobclickAgent.onEvent(context, "ORDER_STOREDETAIL");
	}

	public static void order_orderdetail(Context context) {
		MobclickAgent.onEvent(context, "ORDER_ORDERDETAIL");
	}

	public static void order_comment(Context context) {
		MobclickAgent.onEvent(context, "ORDER_COMMENT");
	}

	public static void order_orderdetail_again(Context context) {
		MobclickAgent.onEvent(context, "ORDER_ORDERDETAIL_AGAIN");
	}

	public static void my_logout(Context context) {
		MobclickAgent.onEvent(context, "MY_LOGOUT");
	}

	public static void my_adress(Context context) {
		MobclickAgent.onEvent(context, "MY_ADRESS");
	}

	public static void my_collect(Context context) {
		MobclickAgent.onEvent(context, "MY_COLLECT");
	}

	public static void my_coupon(Context context) {
		MobclickAgent.onEvent(context, "MY_COUPON");
	}

	public static void my_code(Context context) {
		MobclickAgent.onEvent(context, "MY_CODE");
	}

	public static void my_qa(Context context) {
		MobclickAgent.onEvent(context, "MY_QA");
	}

	public static void my_aboutus(Context context) {
		MobclickAgent.onEvent(context, "MY_ABOUTUS");
	}

}
