package com.huatuo.activity.map;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.CamcorderProfile;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Text;
import com.amap.api.maps.model.TextOptions;
import com.huatuo.R;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Constants;
import com.huatuo.util.LocationUtil_GD;

/**
 * AMapV2地图中简单介绍route搜索
 */
public class GuideMapActivity extends Activity implements OnMapClickListener,
		OnInfoWindowClickListener, InfoWindowAdapter {

	public LatLng startLatLng;//
	public LatLng endLatLng;//
	private String location_city = "";
	private String localtion_address = "";
	private String location_lat;
	private String location_lng;

	private String target_lat = "";
	private String target_lng = "";
	private String target_address = "";
	private String target_storeName = "";

	private int routeType = Constants.GUIDE_DRIVING;// 1代表驾车模式，2代表公交模式，3代表步行模式

	public ArrayAdapter<String> aAdapter;

	// 百度地图
	boolean useDefaultIcon = false;
	// 地图相关，使用继承MapView的MyRouteMapView目的是重写touch事件实现泡泡处理
	// 如果不处理touch事件，则无需继承，直接使用MapView即可
	private MapView mMapView = null; // 地图View
	private AMap aMap;
	private MarkerOptions markerOption;
	// 初始化全局 bitmap 信息，不用时及时 recycle
	private BitmapDescriptor markerIcon = null;
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.route_activity_bd);
		receiveExtras();
		initLatLng();
		// 初始化地图
		mMapView = (MapView) findViewById(R.id.map);
		mMapView.onCreate(bundle);// 此方法必须重写
		LocationUtil_GD.getInstance().startLocation(getApplicationContext(), -100);
		initMap();
		setListener();
	}

	private void setListener() {
		// 返回键
		findViewById(R.id.ll_back1).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	/***
	 * 将activity 的创建模式设置为singletask,
	 * 使用这个方法可以再其他activity想这个activity发送Intent时，这个Intent能够及时更新
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		CommonUtil
				.logE("--------------------------------------------onNewIntent-----------------------------");
		super.onNewIntent(intent);
		setIntent(intent); // 这一句必须的，否则Intent无法获得最新的数据
		receiveExtras();
		initLatLng();
		initMap();
	}

	private void receiveExtras() {
		Bundle bundle = getIntent().getExtras();
		if (null != bundle) {
			target_lat = bundle.getString("lat");
			target_lng = bundle.getString("lng");
			target_address = bundle.getString("address");
			target_storeName = bundle.getString("storeName");

			CommonUtil.log("target_lat" + target_lat);
			CommonUtil.log("target_lng" + target_lng);
			CommonUtil.log("target_address" + target_address);
			CommonUtil.log("target_storeName" + target_storeName);
		}
	}

	private void initLatLng() {
		location_city = CommonUtil.getStringOfSharedPreferences(
				getApplicationContext(), "NOW_CITY", "");
		location_lng = CommonUtil.getStringOfSharedPreferences(
				getApplicationContext(), "NOW_LNG", "");
		location_lat = CommonUtil.getStringOfSharedPreferences(
				getApplicationContext(), "NOW_LAT", "");

		if (!TextUtils.isEmpty(target_lat) && !TextUtils.isEmpty(target_lng)
				&& !TextUtils.isEmpty(location_lat)
				&& !TextUtils.isEmpty(location_lng)) {

			startLatLng = new LatLng(Double.parseDouble(location_lat),
					Double.parseDouble(location_lng));// 开始地址
			endLatLng = new LatLng(Double.parseDouble(target_lat),
					Double.parseDouble(target_lng));// 目标地址
		}

	}

	private void initMap() {
		if (aMap == null) {
			aMap = mMapView.getMap();
			registerListener();
		}
		// 初始化窗口
		addMarkersToMap_GD();
	}

	/**
	 * 注册监听
	 */
	private void registerListener() {
		aMap.setOnMapClickListener(this);
		aMap.setOnInfoWindowClickListener(this);
		aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
	}

	/**
	 * 在地图上添加marker
	 */
	private void addMarkersToMap_GD() {
		markerIcon = BitmapDescriptorFactory
				.fromResource(R.drawable.end);
//		position(Required) 在地图上标记位置的经纬度值。参数不能为空。
//		title 当用户点击标记，在信息窗口上显示的字符串。
//		snippet 附加文本，显示在标题下方。
//		draggable 如果您允许用户可以自由移动标记，设置为“ true ”。默认情况下为“ false ”。
//		visible 设置“ false ”，标记不可见。默认情况下为“ true ”。
//		anchor图标摆放在地图上的基准点。默认情况下，锚点是从图片下沿的中间处。
//		perspective设置 true，标记有近大远小效果。默认情况下为 false。
//		可以通过Marker.setRotateAngle() 方法设置标记的旋转角度，从正北开始，逆时针计算。如设置旋转90度，Marker.setRotateAngle(90)
//		通过setFlat() 方法设置标志是否贴地显示。默认情况下为“false”，不贴地显示。Marker.setFlat(true)
		markerOption = new MarkerOptions();
		markerOption.anchor(0.5f, 0.5f);
		markerOption.position(endLatLng);
		markerOption.icon(markerIcon);
		markerOption.title(target_storeName);
		markerOption.snippet(target_address);
		markerOption.position(endLatLng);
		Marker marker = aMap.addMarker(markerOption);
		marker.showInfoWindow();// 设置默认显示一个infowinfow
		aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(endLatLng, 18));
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		// MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
		if (mMapView != null) {
			mMapView.onDestroy();
		}

		// 回收 bitmap 资源
		if (markerIcon != null) {
			markerIcon.recycle();
		}
		super.onDestroy();

	}

	@Override
	public void onInfoWindowClick(Marker arg0) {
		// TODO Auto-generated method stub
		GuideToStoreUtil.getInstance().guide(GuideMapActivity.this, target_lat,
				target_lng, target_address, target_storeName);
	}

	@Override
	public void onMapClick(LatLng arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public View getInfoContents(Marker marker) {
		// TODO Auto-generated method stub
		return null;
	}

	// 当使用接口 AMap.InfoWindowAdapter（显示信息窗口）的 getInfoWindow(Marker)
	// 方法时，请确认标记（Marker）的 title 或 snippet 已赋值。
	@Override
	public View getInfoWindow(Marker marker) {
		return initInfoWindow(marker);
	}

	/**
	 * 自定义infowinfow窗口----
	 */
	private View initInfoWindow(Marker marker) {
		if (null != endLatLng) {
			String storeName = marker.getTitle();
			String address = marker.getSnippet();
			View view = LayoutInflater.from(this).inflate(R.layout.guide_pop,
					null);
			TextView tv_guide_storeName = (TextView) view
					.findViewById(R.id.tv_guide_storeName);
			TextView tv_guide_storeAddress = (TextView) view
					.findViewById(R.id.tv_guide_storeAddress);
			if (TextUtils.isEmpty(storeName)) {
				storeName = "";
			}
			if (TextUtils.isEmpty(address)) {
				address = "";
			}
			tv_guide_storeName.setText(storeName);
			tv_guide_storeAddress.setText(address);
			return view;
		}
		return null;
	}

}
