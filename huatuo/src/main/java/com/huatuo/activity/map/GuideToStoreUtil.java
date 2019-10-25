package com.huatuo.activity.map;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.huatuo.R;
import com.huatuo.adapter.Apk_Adapter;
import com.huatuo.bean.ApkInfo;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Constants;
import com.huatuo.util.CustomDialogProgressHandler;
import com.huatuo.util.Toast_Util;

public class GuideToStoreUtil {
	private static GuideToStoreUtil instance;
	public static final String BAIDU_PACKAGENAME = "com.baidu.BaiduMap";
	public static final String GAODE_PACKAGENAME = "com.autonavi.minimap";
	private String[] mapPackageNameList = new String[] { GAODE_PACKAGENAME, BAIDU_PACKAGENAME };
	public LatLng startLatLng;//
	public LatLng endLatLng;//

	private String location_city = "";
	private String location_address = "";
	private String location_lat = "";
	private String location_lng = "";

	private Activity mActivity;
	private String target_lat = "";
	private String target_lng = "";
	private String target_address = "";
	private String target_storeName = "";
	private Intent intent = null;
	private PackageManager pManager;
	private List<ApkInfo> apkList;
	private Handler hander_getInstallPackages;

	private List<String> packageNameList = new ArrayList<String>();
	private List<PackageInfo> packageInfoList = new ArrayList<PackageInfo>();
	private Dialog dialog;
	private int routeType = Constants.GUIDE_DRIVING;// 1代表驾车模式，2代表公交模式，3代表步行模式

	public static GuideToStoreUtil getInstance() {
		if (instance == null) {
			synchronized (GuideToStoreUtil.class) {
				if (instance == null) {
					instance = new GuideToStoreUtil();
				}
			}
		}

		return instance;
	}

	public void guide(Activity mActivity, String target_lat, String target_lng, String target_address,
			String target_storeName) {
		this.mActivity = mActivity;
		this.target_lat = target_lat;
		this.target_lng = target_lng;
		this.target_address = target_address;
		this.target_storeName = target_storeName;
		CommonUtil.log("target_lat" + target_lat);
		CommonUtil.log("target_lng" + target_lng);
		CommonUtil.log("target_address" + target_address);
		CommonUtil.log("target_storeName" + target_storeName);

		location_city = CommonUtil.getStringOfSharedPreferences(mActivity.getApplicationContext(), "LCITY", "");
		location_address = CommonUtil.getStringOfSharedPreferences(mActivity.getApplicationContext(), "LADDRESS", "");
		location_lng = CommonUtil.getStringOfSharedPreferences(mActivity.getApplicationContext(), "LLNG", "");
		location_lat = CommonUtil.getStringOfSharedPreferences(mActivity.getApplicationContext(), "LLAT", "");
		initLatLng();
		// 初始化
		openNaviMap();
	}

	private void initLatLng() {
		if (!TextUtils.isEmpty(target_lat) && !TextUtils.isEmpty(target_lng) && !TextUtils.isEmpty(location_lat)
				&& !TextUtils.isEmpty(location_lng)) {

			startLatLng = new LatLng(Double.parseDouble(location_lat), Double.parseDouble(location_lng));// 开始地址
			endLatLng = new LatLng(Double.parseDouble(target_lat), Double.parseDouble(target_lng));// 目标地址
		}
	}

	public void openNaviMap() {
		// 获取本地所有安装包
		// getInstalledPackages();
		guideByUri();
		// showApkInfoList(mActivity);
		// getInstalledPackagesInfo();
	}

	/**
	 * 通过Uri进行调用第三方地图
	 */
	private void guideByUri() {
		// geo:latitude,longitude
		// geo:latitude,longitude?z=zoom，z表示zoom级别，值为数字1到23
		// geo:0,0?q=my+street+address
		// geo:0,0?q=business+near+city
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			Uri uri = Uri.parse("geo:" + target_lat + "," + target_lng + "?q=" + target_address);
			intent.setData(uri);
			mActivity.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			CommonUtil.logE("Exception:" + e);
			String msg = mActivity.getResources().getString(R.string.guide_no_activity_tip);
			showCustomDialog(msg);
		}

	}

	/**
	 * 指定地图
	 */
	private void judgeMapType() {

		// 高德地图
		if (isAvilible(mActivity, "com.baidu.BaiduMap")) {// 传入指定应用包名
			guideOfBaidu();
		} else if (isAvilible(mActivity, "com.autonavi.minimap")) {//
			guideOfGaoDe();
		} else {// 未安装
			String msg = mActivity.getResources().getString(R.string.guide_error_tip);
			showCustomDialog(msg);
		}
	}

	private void showCustomDialog(String msg) {
		if (dialog != null) {
			try {
				dialog.cancel();
				dialog = null;
			} catch (Exception e) {
			}
		}
		dialog = new Dialog(mActivity, R.style.my_dialog);
		// 自定义AlertDialog
		View view = LayoutInflater.from(mActivity).inflate(R.layout.alertdialog_one_btn, null);

		TextView pc_confirm = (TextView) view.findViewById(R.id.pc_confirm);
		TextView tv_message = (TextView) view.findViewById(R.id.tv_message);
		tv_message.setText(msg);
		pc_confirm.setText("取消");
		dialog.setContentView(view);
		// 确认
		pc_confirm.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	/**
	 * 检查手机上是否安装了指定的软件
	 * 
	 * @param context
	 * @param packageName
	 *            ：应用包名
	 * @return
	 */
	public boolean isAvilible(Activity context, String packageName) {
		// 判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
		return packageNameList.contains(packageName);
	}

	/**
	 * @param context
	 * @param packageName
	 * @return
	 */
	public void showApkInfoList(Activity context) {
		apkList = new ArrayList<ApkInfo>();
		if (pManager == null) {
			pManager = context.getPackageManager();
		}
		getApkInfoList();
		// 获取到指定的地图apk
		if (!CommonUtil.emptyListToString3(apkList)) {
			// 如果手机上安装多个地图展示列表选择
			if (apkList.size() > 1) {
				chooseMap(context, apkList);
			} else {
				// 如果手机上只有一个直接去导航
				judgeMapType(0);
			}
		} else {
			String msg = mActivity.getResources().getString(R.string.guide_error_tip);
			showCustomDialog(msg);
		}
	}

	/**
	 * 获取指定地图信息列表
	 * 
	 */
	private void getApkInfoList() {
		String packageName = null;
		for (int i = 0; i < mapPackageNameList.length; i++) {
			packageName = mapPackageNameList[i];
			ApkInfo apkInfo = new ApkInfo();
			try {
				PackageInfo pi = pManager.getPackageInfo(packageName, 0);
				apkInfo.setApkIcon(pManager.getApplicationIcon(pi.applicationInfo));
				apkInfo.setApkLabel(pManager.getApplicationLabel(pi.applicationInfo).toString());

				CommonUtil.logE("packageName:" + packageName);
				apkInfo.setApkPackageName(packageName);
				if (apkInfo != null) {
					apkList.add(apkInfo);
				}
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 判断去哪个地图
	 * 
	 * @param which
	 *            apkList的第几个map
	 */
	private void judgeMapType(int which) {
		String apkName = apkList.get(which).getApkPackageName();
		if (!TextUtils.isEmpty(apkName)) {
			if ((BAIDU_PACKAGENAME).equals(apkName)) {
				guideOfBaidu();
			} else if ((GAODE_PACKAGENAME).equals(apkName)) {
				guideOfGaoDe();
			}
		}
	}

	/**
	 * 获取本地所有安装包--当获取的结果数量比较多的时候，在某些机型上面调用它花费的时间可能秒级的，所以尽量在子线程中使用。
	 */
	private void getInstalledPackages() {
		hander_getInstallPackages = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				CustomDialogProgressHandler.getInstance().closeCustomCircleProgressDialog();
				switch (msg.what) {
				case 0: // 成功
					packageNameList = (List<String>) msg.obj;
					judgeMapType();
					break;
				case 1: // 失败
					Toast_Util.showToastOnlyOne(mActivity, "获取应用信息失败", Gravity.CENTER, false);
					break;
				}
				super.handleMessage(msg);

			}
		};
		CustomDialogProgressHandler.getInstance().showCustomCircleProgressDialog(mActivity, null, "获取地图信息...", true);
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = Message.obtain();
				if (null == pManager) {
					pManager = mActivity.getPackageManager();
				}
				List<PackageInfo> appList = pManager.getInstalledPackages(0);
				List<String> list = getPackageNameList(appList);
				if (list != null) {
					msg.what = 0;
					msg.obj = list;
				} else {
					msg.what = 1;
				}
				hander_getInstallPackages.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 获取本机安装所有app名字
	 * 
	 * @param appList
	 * @return
	 */
	private List<String> getPackageNameList(List<PackageInfo> appList) {
		// 用于存储所有已安装程序的包名
		List<String> packageNames = new ArrayList<String>();
		if (!CommonUtil.emptyListToString3(appList)) {
			for (int i = 0; i < appList.size(); i++) {
				String packName = appList.get(i).packageName;
				CommonUtil.log("packName:" + packName);
				packageNames.add(packName);
			}

			return packageNames;
		}

		return null;

	}

	/**
	 * 选择地图
	 * 
	 * @param mActivity
	 * @param apkList
	 */
	private void chooseMap(Activity mActivity, final List<ApkInfo> apkList) {

		// dialog参数设置
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity); // 先得到构造器
		builder.setTitle("请选择地图"); // 设置标题
		Apk_Adapter adapter;
		adapter = new Apk_Adapter(mActivity, apkList);
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int which) {
				judgeMapType(which);
			}
		};

		builder.setAdapter(adapter, listener);

		builder.create().show();
	}

	/**
	 * 百度导航
	 */
	private void guideOfBaidu() {
		String bd_routeType = "";// 固定为transit、driving、walking，
		switch (routeType) {// 1代表公交模式，2代表驾车模式，4代表步行模式
		case Constants.GUIDE_TRANSIT://
			bd_routeType = "transit";
			break;
		case Constants.GUIDE_DRIVING:
			bd_routeType = "driving";
			break;
		case Constants.GUIDE_WALK:
			bd_routeType = "walking";
			break;
		default:
			break;
		}
		try {

			intent = Intent.getIntent("intent://map/direction?" + "origin=latlng:" + location_lat + "," + location_lng
					+ "|name:" + location_address + "&destination=latlng:" + target_lat + "," + target_lng + "|name:"
					+ target_address + "&mode=" + bd_routeType + "&origin_region=" + location_city
					+ "&destination_region=" + location_city + "&src=" + "com.huatuo"
					+ "#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
			mActivity.startActivity(intent); // 启动调用
		} catch (URISyntaxException e) {
			Log.e("intent", e.getMessage());
		}
	}

	/**
	 * 高德导航
	 */
	@SuppressWarnings("deprecation")
	private void guideOfGaoDe() {
		try {
			intent = Intent.getIntent("androidamap://route?sourceApplication=softname" + "&slat=" + location_lat
					+ "&slon=" + location_lng + "&sname=" + location_address + "&dlat=" + target_lat + "&dlon="
					+ target_lng + "&dname=" + target_address + "&dev=0" + "&m=0" + "&t=" + routeType);
			// startActivity(intent); //启动调用
			mActivity.startActivity(intent); // 启动调用
		} catch (URISyntaxException e) {
			Log.e("intent", e.getMessage());
		}
	}

	/**
	 * 去往导航页面
	 * 
	 * @param context
	 * @param target_lat
	 * @param target_lng
	 * @param target_address
	 * @param target_storeName
	 */
	public void jumpToGuideActivity(Context context, String target_lat, String target_lng, String target_address,
			String target_storeName) {
		Intent intent = new Intent();
		intent.setClass(context, GuideMapActivity.class);
		intent.putExtra("lat", target_lat);
		intent.putExtra("lng", target_lng);
		intent.putExtra("address", target_address);
		intent.putExtra("storeName", target_storeName);
		context.startActivity(intent);
	}

}
