package com.huatuo.activity.personal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.huatuo.R;
import com.huatuo.base.BaseNetActivity;
import com.huatuo.base.MyApplication;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.DialogUtils;
import com.huatuo.util.Toast_Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 新建地址
 * @author Android开发工程师
 *
 */
public class AddressEditActivity extends BaseNetActivity {
	private Context mContext;
	private LinearLayout ll_back;
	private EditText et_name, et_mobile, et_address;
	private TextView tv_sex_man, tv_sex_woman, tv_area, tv_complete;
	private RelativeLayout rl_sex, rl_area;
	private LinearLayout add_address;

	public JSONObject mJsonObj;
	private String areaName = "";
	private String district = "";
	/** 类型: 0 添加。1 编辑 */
	private int type;
	private ImageView iv_sexnext, iv_areanext;

	/** 把全国的省市区的信息以json的格式保存，解析完成后赋值为null */
	/** 省的WheelView控件 */
	// private WheelView mProvince;
	// /** 市的WheelView控件 */
	// private WheelView mCity;
	// /** 区的WheelView控件 */
	// private WheelView mArea;

	private JSONArray jsonArray = null;

	private String s_name, s_mobile, s_address, s_area;
	private String s_sex = "男";

	public static int FLAG_VIEW = -100;// 标记界面，用来区别保存地址后返回到哪个界面

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_personal_address_edit1);
		mContext = this;
		// mHandler = new MyHandler();
		type = getIntent().getIntExtra("type", 0); // 如果没有取到type的值，默认返回0
		// initJsonData();
		initWidget();
		bindListener();

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == AddressSearchAreaActivity.REQUEST_SELECT_ADDRESS_EDIT) {
				areaName = data.getStringExtra("areaName");
				district = data.getStringExtra("district");
				String adcode = data.getStringExtra("adcode");
				CommonUtil
						.log("AddressManageActivity-------------选择服务地址返回的的areaName:"
								+ areaName);
				CommonUtil
						.log("AddressManageActivity-------------选择服务地址返回的的district:"
								+ district);
				CommonUtil
						.log("AddressManageActivity-------------选择服务地址返回的的adcode:"
								+ adcode);
				tv_area.setText(district + areaName);
			}
		}
	}


	/** 在这里获取到每个需要用到的控件的实例，并给它们设置好必要的点击事件 */
	private void initWidget() {
		ll_back = (LinearLayout) findViewById(R.id.ll_back1);

		et_name = (EditText) findViewById(R.id.et_name);
		tv_sex_man = (TextView) findViewById(R.id.tv_sex_man);
		tv_sex_woman = (TextView) findViewById(R.id.tv_sex_woman);

		et_mobile = (EditText) findViewById(R.id.et_mobile);
		tv_area = (TextView) findViewById(R.id.tv_area);
		et_address = (EditText) findViewById(R.id.et_address);
		tv_complete = (TextView) findViewById(R.id.tv_complete1);

		rl_sex = (RelativeLayout) findViewById(R.id.rl_sex);
		rl_area = (RelativeLayout) findViewById(R.id.rl_area);
		iv_areanext = (ImageView) findViewById(R.id.iv_areanext);

		add_address = (LinearLayout) findViewById(R.id.add_address);
		changeSexType(0);

		// 点击屏幕空白处隐藏键盘
		add_address.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				return imm.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), 0);
			}
		});

	}

	protected void bindListener() {
		// super.bindListener();
		MyOnClickListener myOnClickListener = new MyOnClickListener();
		ll_back.setOnClickListener(myOnClickListener);
		
		tv_complete.setOnClickListener(myOnClickListener);
		tv_sex_man.setOnClickListener(myOnClickListener);
		tv_sex_woman.setOnClickListener(myOnClickListener);
		rl_area.setOnClickListener(myOnClickListener);
		iv_areanext.setOnClickListener(myOnClickListener);
	}

	public void netCall2(JSONObject inJson) {

		try {
			inJson = new JSONObject();

			inJson.put("actionType", "add");
			s_name = et_name.getText().toString();
			s_mobile = et_mobile.getText().toString();
			s_address = et_address.getText().toString();

			s_area = tv_area.getText().toString();

			if (s_name.length() == 0) {
				DialogUtils
						.showToastMsg(mContext, "请您输入姓名", Toast.LENGTH_SHORT);
				return;
			}
			if (s_mobile.length() == 0) {
				DialogUtils.showToastMsg(mContext, "请您输入手机号码",
						Toast.LENGTH_SHORT);
				return;
			}
			if (s_address.length() == 0) {
				DialogUtils
						.showToastMsg(mContext, "请您输入地址", Toast.LENGTH_SHORT);
				return;
			}

			inJson.put("userID", MyApplication.getUserID());
			// inJson.put("mobileNo", s_mobile);
			// inJson.put("name", s_name);
			inJson.put("userName", s_name);
			inJson.put("sex", s_sex);
			inJson.put("mobile", s_mobile);
			inJson.put("address", s_address);
			inJson.put("proCode", 0);
			inJson.put("cityCode", "027");
			inJson.put("areaCode", "08");
			if (type == 0) {// 0表示新增地址，新增时设置地址为非默认-0

				inJson.put("isDefault", "0");
			}

			// inJson.put("status", "1");
			// Log.e("AddressEditActivity",
			// "AddressEditActivity--------------->"
			// + inJson);
			// inJson.put("userID", MyApplication.getUserID());
			if (et_mobile.getText().toString().length() == 11
					&& et_mobile.getText().toString().startsWith("1")) {
				/* super.netCall(inJson, "user_EditAddress"); */
				super.netCall(inJson, "user/updateAddress");
			} else {
				Toast.makeText(mContext, "请您填写正确的手机号码以便我们与您联系，谢谢。",
						Toast.LENGTH_LONG).show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void netCall(JSONObject inJson) {
		try {
			CommonUtil.logE("新增地址上传参数：inJson：" + inJson);
			super.netCall(inJson, "user/address/save");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void netCallBack(Message msg) {
		super.netCallBack(msg);
		if (outJson != null) {
			Intent intent = new Intent();
			setResult(Activity.RESULT_OK, intent);
			finish();
		}
	};

	private boolean isEmpty() {
		if (null == et_name.getText().toString()
				|| null == et_mobile.getText().toString()
				|| null == et_address.getText().toString()
				|| "".equals(et_name.getText().toString())
				|| "".equals(et_mobile.getText().toString())
				|| "".equals(et_address.getText().toString())
				|| (TextUtils.isEmpty(district + areaName))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 隐藏输入键盘
	 */
	private void hideKeyBoard() {
		// Toast_Util.showToast(mContext, "决定是否接受的");
		CommonUtil.hideKeyboard(AddressEditActivity.this, et_name);
		CommonUtil.hideKeyboard(AddressEditActivity.this, et_mobile);
		CommonUtil.hideKeyboard(AddressEditActivity.this, et_address);

	}

	/**
	 * 改变选中的性别样式
	 * @param type
	 */
	private void changeSexType(int type) {
		tv_sex_man.setCompoundDrawablesWithIntrinsicBounds(getResources()
				.getDrawable(R.drawable.icon_address_nodef), null, null, null);
		tv_sex_man.setCompoundDrawablePadding(CommonUtil.dip2px(mContext, 8));
		tv_sex_woman.setCompoundDrawablesWithIntrinsicBounds(getResources()
				.getDrawable(R.drawable.icon_address_nodef), null, null, null);
		tv_sex_woman.setCompoundDrawablePadding(CommonUtil.dip2px(mContext, 8));
		switch (type) {
		case 0:// 男
			s_sex = "男";
			tv_sex_man.setCompoundDrawablesWithIntrinsicBounds(getResources()
					.getDrawable(R.drawable.icon_address_def), null, null, null);
//			tv_sex_man.setCompoundDrawablePadding(CommonUtil
//					.dip2px(mContext, 8));
			break;

		case 1:// 女
			s_sex = "女";
			tv_sex_woman.setCompoundDrawablesWithIntrinsicBounds(getResources()
					.getDrawable(R.drawable.icon_address_def), null, null, null);
//			tv_sex_woman.setCompoundDrawablePadding(CommonUtil.dip2px(mContext,
//					8));
			break;
		}

	}

	class MyOnClickListener implements OnClickListener {
		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			switch (view.getId()) {
			case R.id.ll_back1:
				finish();
				break;
			case R.id.tv_complete1:// 保存地址

				if (!isEmpty()) {
					try {
						
//						userID	用户ID	ANS	32	M	
//						addressID	地址ID	ANS	32	C	如果存在为更新地址，如果为空为新增地址
//						userName	姓名	ANS	128	C	如果新增必填
//						gender	性别	ANS	2	C	如果新增必填
//						mobile	电话	N	11	C	如果新增必填
//						cityName	市名称	N	16	C	如果新增必填
//						userArea	地区全称	N	16	C	如果新增必填
//						address	楼层、门牌	ANS	64	C	如果新增必填
//						isDefault	是否默认地址	N	1	C	如果新增必填
//						非默认0  默认1
//						longitude	经度	N	16	C	客户端调用地图API
//						latitude	纬度	N	16	C	客户端调用地图API
						JSONObject opj = new JSONObject();
						opj.put("userID", MyApplication.getUserID());
						opj.put("addressID", "");
						opj.put("userName", et_name.getText().toString());
						opj.put("gender", s_sex);
						opj.put("mobile", et_mobile.getText().toString());
						opj.put("address", et_address.getText().toString());
						opj.put("isDefault", "0");
						String city = CommonUtil.getStringOfSharedPreferences(mContext.getApplicationContext(), "NOW_CITY", "");
						opj.put("cityName", city);
						opj.put("userArea", district + areaName);
						// opj.put("lng", MyApplication.longitude);
						// opj.put("lat", MyApplication.latitude);
						netCall(opj);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Toast_Util.showToast(mContext, "请您完整填写联系信息，谢谢。");
				}

				break;
			case R.id.tv_sex_man:// 男士
				changeSexType(0);
				break;

			case R.id.tv_sex_woman:// 女士
				changeSexType(1);
				break;
			case R.id.rl_area:// 输入服务地址
				hideKeyBoard();

				Intent intent = new Intent();
				intent.setClass(AddressEditActivity.this,
						AddressSearchAreaActivity.class);
				startActivityForResult(intent,
						AddressSearchAreaActivity.REQUEST_SELECT_ADDRESS_EDIT);
				// rl_wheel.setVisibility(View.VISIBLE);
				// Intent intent = new Intent(mContext, CitiesActivity.class);
				// startActivity(intent);

				break;
			default:
				break;
			}
		}
	}

}