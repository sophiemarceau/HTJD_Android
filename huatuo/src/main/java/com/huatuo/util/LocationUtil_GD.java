package com.huatuo.util;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

/**
 * 定位-高德
 * 
 * @author Android开发工程师 wrz
 * 
 */
public class LocationUtil_GD {
	private Context mContext;
	private static LocationUtil_GD instance;
	private double lat;
	private double lng;
	private String city = "";
	private String address = "";
	private int type;
	// 高德
	GeocodeSearch geocoderSearch = null;
	private AMapLocationClient mLocationClient;
	private AMapLocationClientOption mLocationOption;
	private AMapLocationListener aMapLocationListener;

	public static LocationUtil_GD getInstance() {
		if (instance == null) {
			synchronized (LocationUtil_GD.class) {
				if (instance == null) {
					instance = new LocationUtil_GD();
				}
			}
		}

		return instance;
	}

	public void startLocation(Context context, int type) {
		this.type = type;
		mContext = context;
		init();
	}

	private void init() {
		// 初始化定位
		mLocationClient = new AMapLocationClient(mContext);
		// 设置定位回调监听
		aMapLocationListener = new MyAMapLocationListener();
		mLocationClient.setLocationListener(aMapLocationListener);

		// 初始化定位参数
		mLocationOption = new AMapLocationClientOption();
		// 设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
		mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
		// 设置是否返回地址信息（默认返回地址信息）
		mLocationOption.setNeedAddress(true);
		// 设置是否只定位一次,默认为false
		mLocationOption.setOnceLocation(true);
		// 设置是否强制刷新WIFI，默认为强制刷新
		mLocationOption.setWifiActiveScan(true);
		// 设置是否允许模拟位置,默认为false，不允许模拟位置
		mLocationOption.setMockEnable(false);
		// 设置定位间隔,单位毫秒,默认为2000ms
		mLocationOption.setInterval(2000);
		// 给定位客户端对象设置定位参数
		mLocationClient.setLocationOption(mLocationOption);
		// 启动定位
		mLocationClient.startLocation();
	}

	/**
	 * 高德定位
	 * 
	 * @author Android开发工程师 wrz
	 * 
	 */
	class MyAMapLocationListener implements AMapLocationListener {

		@Override
		public void onLocationChanged(AMapLocation amapLocation) {
			if (amapLocation == null) {
				// 显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
				CommonUtil
						.log("---------------------定位开始结束--------失败------------");
				sendBroadCartByType();
				return;
			}
			if (amapLocation.getErrorCode() != 0) {
				// 显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
				CommonUtil
						.log("---------------------定位开始结束--------失败------------");
				sendBroadCartByType();
				CommonUtil.logE("AmapError:location Error, ErrCode:"
						+ amapLocation.getErrorCode() + ", errInfo:"
						+ amapLocation.getErrorInfo());
				return;
			}

			// 获取位置信息
			lat = amapLocation.getLatitude();
			lng = amapLocation.getLongitude();
			CommonUtil.log("LocationUtil---：得到的getLatitude:" + lat);
			CommonUtil.log("LocationUtil----：得到的Longitude:" + lng);

			// 测试恩施
			// lng = 109.48578;
			// lat = 30.298428;
			// // 测试新疆
			// lng = 87.633574d;
			// lat = 43.839431d;

			// AMapLocation 的 getProvider()
			// 返回定位位置的提供者名称。网络定位时，返回 “lbs”；GPS定位时，返回“gps”。
			String provider = amapLocation.getProvider();
			CommonUtil.log("LocationUtil-----高德地图定位：provider：" + provider);
			/*
			 * getAdCode() // 返回定位信息中的区域编码，只有在网络定位时才返回值。 getAddress() //
			 * 返回地址的详细描述，包括省、市、区和街道。 double getAltitude() //
			 * 返回海拔高度，如果返回0.0，说明没有返回海拔高度。 getCity()
			 * 返回定位信息中所属城市名称，如“北京市”，只有在网络定位时才返回值。 getCityCode()
			 * 返回定位信息中的城市编码，如北京市为“010”，只有在网络定位时才返回值。 java.lang.String
			 * getDistrict() 返回定位信息中所属区（县）名称，如“朝阳区”，只有在网络定位时才返回值。 Bundle
			 * getExtras() 返回Bundle类型的定位描述信息，只有在网络定位时才返回值。 java.lang.String
			 * getFloor() 返回定位到的室内地图的楼层，如果不在室内或者无数据，则返回默认值null。 double
			 * getLatitude() 返回定位位置的纬度坐标。 double getLongitude() 返回定位位置的经度坐标。
			 * java.lang.String getPoiId()
			 * 返回定位到的室内地图POI的id，如果不在室内或者无此数据，默认为null。 java.lang.String
			 * getProvider() 返回定位位置的提供者名称。 java.lang.String getProvince()
			 * 返回定位信息中所属省名称，如“河北省”，直辖市城市为空，只有在网络定位时才返回值。 float getSpeed() 返回定位速度
			 * ，单位：米/秒，如果此位置不具有速度，则返回0.0 。 java.lang.String getStreet()
			 * 返回街道和门牌号，只有在网络定位才返回值
			 */
			if (TextUtils.isEmpty(provider)) {
				sendBroadCartByType();
				return;
			}

			if (provider.equals("lbs")) {
				CommonUtil
						.log("-----------lbs--------------------定位城市成功-------------------------");

				city = amapLocation.getCity();
				String province = amapLocation.getProvince();
				String district = amapLocation.getDistrict();
				address = amapLocation.getAddress();
				CommonUtil.log("定位到的city:" + city);
				CommonUtil.log("定位到的province:" + province);
				CommonUtil.log("定位到的district:" + district);
				CommonUtil.log("定位到的addr:" + address);
				CommonUtil.log("定位到的lng:" + lng);
				CommonUtil.log("定位到的lat:" + lat);
				CommonUtil.saveStringOfSharedPreferences(mContext,
						"LOCALTION_CITY", city);
				sendBroadCartByType();// 通知定位结果
			} else if (provider.equals("gps")) {
				CommonUtil
						.log("-----------gps--------------------定位城市成功-------------------------");
				CommonUtil
						.log("--------------------------反编译经纬度获取城市-------------------------");
				if (CommonUtil.isNetworkAvailable(mContext)) {
					getAddressOfGeocoderSearch();
				} else {
					sendBroadCartByType();
				}

			}

		}
	};

	/**
	 * // 根据经纬度获取地理位置
	 */
	public void getAddressOfGeocoderSearch() {
		LatLonPoint latLonPoint = new LatLonPoint(lat, lng);
		geocoderSearch = new GeocodeSearch(mContext);
		geocoderSearch
				.setOnGeocodeSearchListener(new OnGeocodeSearchListener() {
					/**
					 * 逆地理编码回调 ----经纬度转换成具体位置信息
					 */
					@Override
					public void onRegeocodeSearched(RegeocodeResult result,
							int rCode) {
						if (rCode == 0) {
							if (result != null
									&& result.getRegeocodeAddress() != null
									&& result.getRegeocodeAddress()
											.getFormatAddress() != null) {
								String localtion_city = result
										.getRegeocodeAddress().getCity();
								String localtion_district = result
										.getRegeocodeAddress().getDistrict();
								String localtion_province = result
										.getRegeocodeAddress().getProvince();
								String localtion_address = result
										.getRegeocodeAddress()
										.getFormatAddress();

								city = localtion_city;
								address = localtion_address;
								CommonUtil.saveStringOfSharedPreferences(
										mContext, "LOCALTION_CITY", city);
								CommonUtil
										.log("LocationUtil-----高德地图反编译经纬度的：province："
												+ localtion_province
												+ "---city:"
												+ city
												+ "--district:"
												+ localtion_district
												+ "----address:" + address);
								sendBroadCartByType();

							} else if (rCode == 27) {
								Toast_Util.showToast(mContext, "搜索失败,请检查网络连接！");
								sendBroadCartByType();
							} else if (rCode == 32) {
								Toast_Util.showToast(mContext, "key验证无效！");
								sendBroadCartByType();
							} else {
								Toast_Util.showToast(mContext,
										"未知错误，请稍后重试!错误码为" + rCode);
								sendBroadCartByType();
							}
						}

					}

					@Override
					public void onGeocodeSearched(GeocodeResult result,
							int rCode) {

					}

				});

		RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
				GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
		geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
	}

	private void sendBroadCartByType() {
		switch (type) {
		case Constants.SENDBROACAST_LOCATION_APPLICATION:// application
			sendBroadcastOfApplication();
			break;
		case Constants.SENDBROACAST_LOCATION_STARTACTIVITY:// startAcitivty
			sendBroadcastOfStartActivity();
			break;
		case Constants.SENDBROACAST_LOCATION_SELECTADDRESS:// 选择城市页面的
			sendBroadcastOfSelectAddress();
			break;
		case Constants.SENDBROACAST_LOCATION_PERSONAL:// 创建联系地址页面
			sendBroadcast_userAddress();
			break;
			default :
				saveLocationInfo(city,address,String.valueOf(lat),String.valueOf(lng));
			break;
		}

		stopLocation();// 关闭定位
	}

	/**
	 * //发送定位更新广播
	 * 
	 * @param city
	 * @param lng
	 * @param lat
	 */
	private void sendBroadcastOfStartActivity() {

		// Intent intent = new Intent();
		// intent.setAction(Constants.REFRESH_LOCATION);
		// mContext.sendBroadcast(intent);
		CommonUtil.log("REFRESH_LOCATION_SEARCH:_localtion_city" + city);
		CommonUtil.log("REFRESH_LOCATION_SEARCH:_localtion_lat:" + lat + "");
		CommonUtil.log("REFRESH_LOCATION_SEARCH:_localtion_lng:" + lng + "");
		CommonUtil.log("REFRESH_LOCATION_SEARCH" + address);
		Intent intent_search = new Intent();
		intent_search.setAction(Constants.REFRESH_LOCATION_STARTACTIVITY);
		intent_search.putExtra("lat", lat + "");
		intent_search.putExtra("lng", lng + "");
		intent_search.putExtra("city", city);
		intent_search.putExtra("address", address);
		mContext.sendBroadcast(intent_search);
	}

	/**
	 * //发送定位更新广播
	 * 
	 * @param city
	 * @param lng
	 * @param lat
	 */
	private void sendBroadcastOfSelectAddress() {

		// Intent intent = new Intent();
		// intent.setAction(Constants.REFRESH_LOCATION);
		// mContext.sendBroadcast(intent);
		CommonUtil.log("REFRESH_LOCATION_SEARCH:_localtion_city" + city);
		CommonUtil.log("REFRESH_LOCATION_SEARCH:_localtion_lat:" + lat + "");
		CommonUtil.log("REFRESH_LOCATION_SEARCH:_localtion_lng:" + lng + "");
		CommonUtil.log("REFRESH_LOCATION_SEARCH" + address);
		Intent intent_search = new Intent();
		intent_search.setAction(Constants.REFRESH_LOCATION_SELECTADDRESS);
		intent_search.putExtra("lat", lat + "");
		intent_search.putExtra("lng", lng + "");
		intent_search.putExtra("city", city);
		intent_search.putExtra("address", address);
		mContext.sendBroadcast(intent_search);
	}

	/**
	 * //发送定位更新广播
	 * 
	 * @param city
	 * @param lng
	 * @param lat
	 */
	private void sendBroadcastOfApplication() {

		// Intent intent = new Intent();
		// intent.setAction(Constants.REFRESH_LOCATION);
		// mContext.sendBroadcast(intent);
		CommonUtil.log("REFRESH_LOCATION_SEARCH:_localtion_city" + city);
		CommonUtil.log("REFRESH_LOCATION_SEARCH:_localtion_lat:" + lat + "");
		CommonUtil.log("REFRESH_LOCATION_SEARCH:_localtion_lng:" + lng + "");
		CommonUtil.log("REFRESH_LOCATION_SEARCH" + address);
		Intent intent_search = new Intent();
		intent_search.setAction(Constants.REFRESH_LOCATION_APPLICATION);
		intent_search.putExtra("lat", lat + "");
		intent_search.putExtra("lng", lng + "");
		intent_search.putExtra("city", city);
		intent_search.putExtra("address", address);
		mContext.sendBroadcast(intent_search);
	}

	/**
	 * //发送定位更新广播
	 * 
	 * @param city
	 * @param lng
	 * @param lat
	 */
	private void sendBroadcast_userAddress() {

		// Intent intent = new Intent();
		// intent.setAction(Constants.REFRESH_LOCATION);
		// mContext.sendBroadcast(intent);

		Intent intent_search = new Intent();
		intent_search.setAction(Constants.REFRESH_LOCATION_SEARCH_PERSON);
		intent_search.putExtra("lat", lat + "");
		intent_search.putExtra("lng", lng + "");
		intent_search.putExtra("city", city);
		intent_search.putExtra("address", address);
		mContext.sendBroadcast(intent_search);
	}
	
	
	private void saveLocationInfo(String cityName,String address,String lat, String lng){

			CommonUtil.saveStringOfSharedPreferences(mContext,
					"LCITY", cityName);
			CommonUtil.saveStringOfSharedPreferences(mContext,
					"LCITYCODE", address);
			CommonUtil.saveStringOfSharedPreferences(mContext,
					"LLNG", lng);
			CommonUtil.saveStringOfSharedPreferences(mContext,
					"LLAT", lat);
	}

	/**
	 * 停止定位
	 */
	public void stopLocation() {
		CommonUtil.log("locationUtil------stopLocation-----");

		if (mLocationClient != null) {
			// 停止定位：
			mLocationClient.stopLocation();
			// 销毁定位客户端：销毁定位客户端之后，若要重新开启定位请重新New一个AMapLocationClient对象。
			mLocationClient.onDestroy();
		}
		mLocationClient = null;
	}

}
