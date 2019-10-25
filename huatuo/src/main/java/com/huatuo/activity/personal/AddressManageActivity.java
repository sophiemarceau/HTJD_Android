package com.huatuo.activity.personal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
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

import com.huatuo.R;
import com.huatuo.base.BaseNetActivity;
import com.huatuo.base.MyApplication;
import com.huatuo.custom_widget.CustomDialog;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Toast_Util;

/**
 * 修改地址
 * @author Android开发工程师
 *
 */
public class AddressManageActivity extends BaseNetActivity {
	private Context mContext;
	private LinearLayout ll_back;
	private TextView tv_area, tv_sex_man, tv_sex_woman, tv_complete,
			tv_complete2, bt_shezhimoren;
	private EditText et_name, et_mobile, et_address;
	private RelativeLayout rl_shezhimoren, rl_delete, rl_sex, rl_area;
	private LinearLayout add_address_manager;
	private JSONObject addressJsonObject;

	public JSONObject mJsonObj;

	/** 类型: 0 添加。1 编辑 */
	private int type;
	private ImageView iv_sexnext, iv_areanext;

	private JSONArray jsonArray = null;

	private String s_name, s_mobile, s_address, s_area;
	private String s_sex = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_personal_address_manage);
		mContext = this;
		String addressString = getIntent().getStringExtra("addressJsonObject");
		if (addressString != null) {
			try {
				addressJsonObject = new JSONObject(addressString);
				// Log.e("AddressManageActivity",
				// "addressJsonObject------------------------>"
				// + addressJsonObject);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		initWidget();
		bindListener();

		// updateAreas();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == AddressSearchAreaActivity.REQUEST_SELECT_ADDRESS_MANAGER) {
				String areaName = data.getStringExtra("areaName");
				String district = data.getStringExtra("district");
				String adcode = data.getStringExtra("adcode");
				CommonUtil.log("AddressManageActivity————选择服务地址返回的的areaName:"
						+ areaName);
				CommonUtil.log("AddressManageActivity————选择服务地址返回的的district:"
						+ district);
				CommonUtil
						.log("AddressManageActivity————————选择服务地址返回的的district-------------选择服务地址返回的的adcode:"
								+ adcode);
				tv_area.setText(district + areaName);

			}

		}
	}


	/** 在这里获取到每个需要用到的控件的实例，并给它们设置好必要的点击事件 */
	private void initWidget() {
		ll_back = (LinearLayout) findViewById(R.id.ll_back1);
		tv_sex_man = (TextView) findViewById(R.id.tv_sex_man);
		tv_sex_woman = (TextView) findViewById(R.id.tv_sex_woman);
		tv_area = (TextView) findViewById(R.id.tv_area);

		et_name = (EditText) findViewById(R.id.et_name);
		et_mobile = (EditText) findViewById(R.id.et_mobile);
		et_address = (EditText) findViewById(R.id.et_address);
		rl_shezhimoren = (RelativeLayout) findViewById(R.id.rl_shezhimoren);
		rl_delete = (RelativeLayout) findViewById(R.id.rl_delete);
		rl_area = (RelativeLayout) findViewById(R.id.rl_area);

		et_name.setText(addressJsonObject.optString("name", ""));
		et_mobile.setText(addressJsonObject.optString("mobile", ""));
		et_address.setText(addressJsonObject.optString("address", ""));
		tv_area.setText(addressJsonObject.optString("userArea", ""));

		tv_complete = (TextView) findViewById(R.id.tv_complete);
		tv_complete2 = (TextView) findViewById(R.id.tv_complete2);
		bt_shezhimoren = (TextView) findViewById(R.id.bt_shezhimoren);

		add_address_manager = (LinearLayout) findViewById(R.id.add_address_mannager);
		bt_shezhimoren.setVisibility(View.GONE);

		String isDefault = addressJsonObject.optString("isDefault", "");
//		if (isDefault.equals("1")) {
			bt_shezhimoren.setVisibility(View.GONE);
			tv_complete.setVisibility(View.GONE);
			tv_complete2.setVisibility(View.VISIBLE);
//		} else {
//			bt_shezhimoren.setVisibility(View.VISIBLE);
//			tv_complete.setVisibility(View.VISIBLE);
//			tv_complete2.setVisibility(View.GONE);
//		}

		s_sex = addressJsonObject.optString("gender", "");
		CommonUtil.log("sex:" + s_sex);

		if (s_sex.equals("男")) {
			changeSexType(0);
		} else if (s_sex.equals("女")) {
			changeSexType(1);
		}

	}

	protected void bindListener() {
		// super.bindListener();
		MyOnClickListener myOnClickListener = new MyOnClickListener();
		ll_back.setOnClickListener(myOnClickListener);
		rl_delete.setOnClickListener(myOnClickListener);
		rl_area.setOnClickListener(myOnClickListener);

		bt_shezhimoren.setOnClickListener(myOnClickListener);
		tv_complete.setOnClickListener(myOnClickListener);
		tv_complete2.setOnClickListener(myOnClickListener);
		// tv_area.setOnClickListener(myOnClickListener);
		tv_complete.setOnClickListener(myOnClickListener);
		tv_sex_man.setOnClickListener(myOnClickListener);
		tv_sex_woman.setOnClickListener(myOnClickListener);
		// 点击屏幕空白处隐藏键盘
		add_address_manager.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				return imm.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), 0);
			}
		});
	}

	private boolean isEmpty() {
		if (null == et_name.getText().toString()
				|| null == et_mobile.getText().toString()
				|| null == et_address.getText().toString()
				|| null == tv_area.getText().toString()
				|| "".equals(et_name.getText().toString())
				|| "".equals(et_mobile.getText().toString())
				|| "".equals(et_address.getText().toString())
				|| "".equals(tv_area.getText().toString())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 提交我的地址参数
	 */
	private void commitAddressParams(boolean isSetDefault) {
		if (!isEmpty()) {

			try {
				JSONObject opj = new JSONObject();
				opj.put("userID", MyApplication.getUserID());
				opj.put("addressID",
						addressJsonObject.optString("ID", ""));
				opj.put("userName", et_name.getText().toString());
				opj.put("gender", s_sex);
				opj.put("mobile", et_mobile.getText().toString());
				opj.put("address", et_address.getText().toString());
				opj.put("userArea", tv_area.getText().toString());
				String city = CommonUtil.getStringOfSharedPreferences(mContext.getApplicationContext(), "NOW_CITY", "");
				opj.put("cityName", city);
				if (!isSetDefault) {
					// 根据返回的状态
					opj.put("isDefault",
							addressJsonObject.optString("isDefault", ""));
				} else {
					// 设置为默认地址
					opj.put("isDefault", "1");
				}
				// 请求网络
				netCall(opj);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			Toast_Util.showToast(mContext, "请您完整填写联系信息，谢谢。");
		}
	}

	private void showDelAddressDialog() {
		
		CustomDialog.Builder builder = new CustomDialog.Builder(mContext);  
		builder.setTitle("提示");
        builder.setMessage("您确定要删除该地址吗？");  
        builder.setPositiveButton(mContext.getResources().getString(R.string.common_confirm), new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int which) {  
                dialog.dismiss();  
                //设置你的操作事项  
                try {
					JSONObject opj = new JSONObject();
					opj.put("addressID",
							addressJsonObject.optString("addressID", "0"));
					netCall2(opj);
				} catch (JSONException e) {
					e.printStackTrace();
				}
            }
        });  
  
        builder.setNegativeButton(mContext.getResources().getString(R.string.common_cancel),  
                new android.content.DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int which) {  
                        dialog.dismiss();  
                    }  
                });  
  
        builder.create().show();  
        
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
				hideKeyBoard();
				finish();
				break;
			case R.id.tv_complete:// 完成1
				hideKeyBoard();
				commitAddressParams(false);
				break;

			case R.id.tv_complete2:// 完成2
				hideKeyBoard();
				commitAddressParams(false);
				break;

			case R.id.rl_delete:// 删除地址
				showDelAddressDialog();
				break;
			case R.id.rl_area:// 选择服务区域
				hideKeyBoard();

				Intent intent = new Intent();
				intent.setClass(AddressManageActivity.this,
						AddressSearchAreaActivity.class);
				startActivityForResult(
						intent,
						AddressSearchAreaActivity.REQUEST_SELECT_ADDRESS_MANAGER);
				// rl_wheel.setVisibility(View.VISIBLE);
				break;

			case R.id.tv_sex_man:// 男士
				changeSexType(0);
				break;

			case R.id.tv_sex_woman:// 女士
				changeSexType(1);
				break;
			case R.id.bt_shezhimoren:// 设为默认地址
				hideKeyBoard();
				commitAddressParams(true);
				break;

			default:
				break;
			}
		}
	}

	public void netCall(JSONObject inJson) {
		try {
			CommonUtil.logE("保存地址上传参数：inJson：" + inJson);
			super.netCall(inJson, "user/address/save");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void netCall2(JSONObject inJson) {
		try {
			super.netCall(inJson, "user/address/del");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void netCallBack(Message msg) {
		super.netCallBack(msg);
		if (outJson != null) {
			finish();
		}
	}

	/**
	 * 隐藏输入键盘
	 */
	private void hideKeyBoard() {
		// Toast_Util.showToast(mContext, "决定是否接受的");
		CommonUtil.hideKeyboard(mContext, et_name);
		CommonUtil.hideKeyboard(mContext, et_mobile);
		CommonUtil.hideKeyboard(mContext, et_address);

	}

}