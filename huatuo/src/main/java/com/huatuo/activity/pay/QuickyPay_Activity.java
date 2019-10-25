package com.huatuo.activity.pay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.huatuo.R;
import com.huatuo.activity.appoint.Appointment_selectCunpon_Activity;
import com.huatuo.activity.order.OrderDetailActivity;
import com.huatuo.adapter.Store_quickPay_plat_amount_Adapter;
import com.huatuo.adapter.Store_quickPay_store_amount_Adapter;
import com.huatuo.base.BaseActivity;
import com.huatuo.base.MyApplication;
import com.huatuo.custom_widget.CustomDialog;
import com.huatuo.custom_widget.CustomListView;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.thread.CommitOrder_Thread_StoreQuickPay;
import com.huatuo.net.thread.GetQuickPayList;
import com.huatuo.net.thread.GetUserInfo;
import com.huatuo.net.thread.UseableCouponList;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Constants;
import com.huatuo.util.DialogSizeUtil;
import com.huatuo.util.DialogUtils;
import com.huatuo.util.JsonUtil;
import com.huatuo.util.NumFormatUtil;
import com.huatuo.util.Toast_Util;

public class QuickyPay_Activity extends BaseActivity implements OnClickListener {
	private Context mContext;
	private Intent intent = new Intent();
	private EditText edt_serviceFee;
	private TextView tv_serviceFee, tv_quick_pay_header;
	private CustomListView lv_store_quickPay_storeAty,
			lv_store_quickPay_platAty;
	private TextView tv_realPay;
	private Button bt_pay_commit;
	private String storeName = "";
	private Store_quickPay_store_amount_Adapter store_quickPay_Adapter;
	private Store_quickPay_plat_amount_Adapter plat_quickPay_Adapter;
	private GetQuickPayList getQuickPayList;
	private Handler quick_handler;
	private ArrayList<JSONObject> storeActList;
	private ArrayList<JSONObject> platActList;

	private Handler commitQucik_Handler;
	private CommitOrder_Thread_StoreQuickPay commitOrder_Thread_StoreQuickPay;

	// 提交订单成后返回支付
	private JSONObject orderObj = null;
	private String alipayData;
	private JSONObject tenpayData;
	private String new_orderID = "";

	private String storeID = "";
	private String inputPrice = "0";
	private String remark = "";
	// 账户余额
	private GetUserInfo getUserInfo;
	private JSONObject userObj;
	private Handler handler_getUserInfo;
	// 代金券
	private final int REQUEST_SELECT_COUPONS = 400;
	private TextView tv_quanPrice;// 代金券金额
	private RelativeLayout layout_selectDaijinquan;// 选择代金券
	private String couponID;// 代金券
	private String couponPrice = "0";
	// 优惠券 减免
	double coupon_db = 0d;
	private double inputPrice_double = 0d;
	// 可用优惠券
	private Handler handler_cunPonList;
	private UseableCouponList useableCouponList = null;
	public ArrayList<JSONObject> cunponList = new ArrayList<JSONObject>();
	private JSONObject cunponJsonObject = null;

	// 计算费用
	private TextView tv_countFee;
	private double db_serviceFee = 0d;// 记录服务单价
	private double totalFee;
	// 支付选择
	private RelativeLayout layout_wxPay, layout_aliPay, layout_accountPay,
			rl_accountPay_line;
	private ImageView iv_alipay_select, iv_wxPay_select, iv_accountPay_select;
	private TextView tx_accountPay;// 账户余额
	// 支付
	private String accountBalance = "0";
	private double accountBalance_double;// 账户余额
	private double stillPay;// 最终需要支付金额
	private boolean ISEnoughOfAccountPay = true;// 账户余额是否充足
	private int FLAG_PAYTYPE = -1;// 0:代表账户余额1：表示支付宝--调用支付宝；2：表示微信支付--调用微信APP
	private String isAccount;// 账户余额支付：1
	private String payChannelCode;// 支付渠道编号

	private boolean account_pay_isSelected = true;
	private boolean ali_pay_isSelected = false;
	private boolean wx_pay_isSelected = false;

	private double store_activityAmount = 0d;
	private String store_activityID = "";

	private double plat_activityAmount = 0d;
	private String plat_activityID = "";
	private RelativeLayout rl_commit;
	private RelativeLayout rl_loadData_error, rl_loadData_empty;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		mContext = this;
		setContentView(R.layout.activity_quicky_pay);
		initHandler();
		initWidget();
		setOnClickListener();
		getData();
		getQuickPayList();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Bundle bundle;
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case REQUEST_SELECT_COUPONS:// 选择优惠券
				bundle = data.getExtras();
				if (null != bundle) {
					String couponsInfo = bundle.getString("COUPONS");
					int isUse_Cunpon = bundle.getInt(Constants.ISUSE_CUNPON);
					switch (isUse_Cunpon) {
					case Constants.CANCEL_CUNPON:// 不使用优惠券
						cancelCunpon();
						break;
					case Constants.USE_CUNPON:// 使用优惠券'
						if (!TextUtils.isEmpty(couponsInfo)) {
							try {
								initCunpon(new JSONObject(couponsInfo));
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
						break;
					}

				}
				break;
			}
		}

	}

	private void initHandler() {
		quick_handler = new QucikList_Handler();
		commitQucik_Handler = new CommitQucik_Handler();
		handler_cunPonList = new Handler_CunPonList();
		handler_getUserInfo = new Handler_getUserInfo();
	}

	private void initWidget() {
		tv_quick_pay_header = (TextView) findViewById(R.id.tv_quick_pay_header);
		edt_serviceFee = (EditText) findViewById(R.id.edt_serviceFee);
		tv_serviceFee = (TextView) findViewById(R.id.tv_serviceFee);
		lv_store_quickPay_storeAty = (CustomListView) findViewById(R.id.lv_store_quickPay_storeAty);
		lv_store_quickPay_platAty = (CustomListView) findViewById(R.id.lv_store_quickPay_platAty);
		tv_realPay = (TextView) findViewById(R.id.tv_realFee);
		bt_pay_commit = (Button) findViewById(R.id.bt_appoint_commit);
		bt_pay_commit.setText("确认支付");
		rl_commit = (RelativeLayout) findViewById(R.id.rl_commit);
		// 选择代金券
		layout_selectDaijinquan = (RelativeLayout) findViewById(R.id.layout_selectDaijinquan);
		tv_quanPrice = (TextView) findViewById(R.id.tv_quanPrice);
		// 支付
		layout_accountPay = (RelativeLayout) findViewById(R.id.layout_accountPay);
		rl_accountPay_line = (RelativeLayout) findViewById(R.id.rl_accountPay_line);
		layout_aliPay = (RelativeLayout) findViewById(R.id.layout_alipay);
		layout_wxPay = (RelativeLayout) findViewById(R.id.layout_wxPay);

		iv_accountPay_select = (ImageView) findViewById(R.id.iv_accountPay_select);
		iv_alipay_select = (ImageView) findViewById(R.id.iv_alipay_select);
		iv_wxPay_select = (ImageView) findViewById(R.id.iv_wxPay_select);

		// 支付
		tx_accountPay = (TextView) findViewById(R.id.tx_accountPay);
		// 加载数据失败页面
		rl_loadData_error = (RelativeLayout) findViewById(R.id.rl_loadData_error);
		rl_loadData_error.setOnClickListener(this);
		// 加载数据前的白界面
		rl_loadData_empty = (RelativeLayout) findViewById(R.id.rl_loadData_empty);
	}

	/**
	 * 快捷支付 提交支付：自定义Dialog
	 */
	private void showInputDialog() {
		final Dialog dialog = new Dialog(this, R.style.my_dialog);

		// 自定义AlertDialog
		View view = LayoutInflater.from(this).inflate(
				R.layout.alertdialog_one_btn, null);

		TextView pc_confirm = (TextView) view.findViewById(R.id.pc_confirm);
		TextView tv_message = (TextView) view.findViewById(R.id.tv_message);
		tv_message.setText("你输出的整型数字有误，请改正");
		dialog.setContentView(view);
		// 确认
		pc_confirm.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				edt_serviceFee.getText().clear();
			}
		});

		DialogSizeUtil.setDialogSize3(mContext, dialog);
		dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
		dialog.show();
	}

	private void setOnClickListener() {
		bindListener();
		// 支付方式
		layout_accountPay.setOnClickListener(this);
		layout_aliPay.setOnClickListener(this);
		layout_wxPay.setOnClickListener(this);

		layout_selectDaijinquan.setOnClickListener(this);
		bt_pay_commit.setOnClickListener(this);

		lv_store_quickPay_platAty
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// "minNeedPrice": 10000,
						// "type": "0",
						// "activityName": "圣诞优惠大派送",
						// "favourList": [],
						// "activityDesc": "为圣诞"

						double thresholdAmount_db = 0d;
						double activityAmount_db = 0d;
						String acticityID = "";
						JSONObject jsonObject = (JSONObject) parent
								.getAdapter().getItem(position);
						if (null != jsonObject) {
							String minNeedPrice = jsonObject.optString(
									"minNeedPrice", "0");
							ArrayList<JSONObject> favourList = JsonUtil
									.jsonArray2List(jsonObject
											.optJSONArray("favourList"));

							if (!TextUtils.isEmpty(minNeedPrice)) {
								double minNeddPrice_db = NumFormatUtil
										.centFormatYuanTodouble(minNeedPrice);
								CommonUtil.log("minNeddPrice_db:"
										+ minNeddPrice_db);
								CommonUtil.log("inputPrice_double:"
										+ inputPrice_double);

								if (inputPrice_double >= minNeddPrice_db) {
									boolean isChecked = plat_quickPay_Adapter.selected_map
											.get(position);
									// CommonUtil
									// .log("-------------------------store_quickPay_Adapter.selected_map:"
									// + store_quickPay_Adapter.selected_map);
									// CommonUtil
									// .log("-------------------------plat_quickPay_Adapter.selected_map:"
									// + plat_quickPay_Adapter.selected_map);
									// CommonUtil
									// .log("-------------------------isChecked:"
									// + isChecked);
									if (!isChecked) {
										plat_quickPay_Adapter.setSelection(
												position, true);
										for (int i = 0; i < favourList.size(); i++) {
											// "thresholdAmount": 10000,
											// "activityAmount": 1500
											JSONObject favourList_item = favourList
													.get(i);
											if (null != favourList_item) {
												String thresholdAmount = favourList_item
														.optString(
																"thresholdAmount",
																"0");
												thresholdAmount_db = NumFormatUtil
														.centFormatYuanTodouble(thresholdAmount);

												if (inputPrice_double >= thresholdAmount_db) {
													String activityAmount = favourList_item
															.optString(
																	"activityAmount",
																	"0");
													activityAmount_db = NumFormatUtil
															.centFormatYuanTodouble(activityAmount);
													acticityID = jsonObject
															.optString(
																	"acticityID",
																	"0");

													cancelCunpon();
												}
											}
										}
									} else {
										plat_quickPay_Adapter.setSelection(
												position, false);
									}

									plat_activityAmount = activityAmount_db;
									plat_activityID = acticityID;
									countTotalFee();

								}
							}
						}

					}

				});
		lv_store_quickPay_storeAty
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// "minNeedPrice": 10000,
						// "type": "0",
						// "activityName": "圣诞优惠大派送",
						// "favourList": [],
						// "activityDesc": "为圣诞"
						double thresholdAmount_db = 0d;
						double activityAmount_db = 0d;
						String acticityID = "";
						JSONObject jsonObject = (JSONObject) parent
								.getAdapter().getItem(position);
						if (null != jsonObject) {
							String minNeedPrice = jsonObject.optString(
									"minNeedPrice", "0");
							ArrayList<JSONObject> favourList = JsonUtil
									.jsonArray2List(jsonObject
											.optJSONArray("favourList"));
							if (!TextUtils.isEmpty(minNeedPrice)) {
								double minNeddPrice_db = NumFormatUtil
										.centFormatYuanTodouble(minNeedPrice);

								CommonUtil.log("minNeddPrice_db:"
										+ minNeddPrice_db);
								CommonUtil.log("inputPrice_double:"
										+ inputPrice_double);
								if (inputPrice_double >= minNeddPrice_db) {
									boolean isChecked = store_quickPay_Adapter.selected_map
											.get(position);
									CommonUtil
											.log("-------------------------store_quickPay_Adapter.selected_map:"
													+ store_quickPay_Adapter.selected_map);
									CommonUtil
											.log("-------------------------plat_quickPay_Adapter.selected_map:"
													+ plat_quickPay_Adapter.selected_map);
									CommonUtil
											.log("-------------------------isChecked:"
													+ isChecked);
									if (!isChecked) {
										store_quickPay_Adapter.setSelection(
												position, true);
										for (int i = 0; i < favourList.size(); i++) {
											// "thresholdAmount": 10000,
											// "activityAmount": 1500
											JSONObject favourList_item = favourList
													.get(i);
											if (null != favourList_item) {
												String thresholdAmount = favourList_item
														.optString(
																"thresholdAmount",
																"0");
												thresholdAmount_db = NumFormatUtil
														.centFormatYuanTodouble(thresholdAmount);

												if (inputPrice_double >= thresholdAmount_db) {
													String activityAmount = favourList_item
															.optString(
																	"activityAmount",
																	"0");
													activityAmount_db = NumFormatUtil
															.centFormatYuanTodouble(activityAmount);
													acticityID = jsonObject
															.optString(
																	"acticityID",
																	"0");

													cancelCunpon();
												}
											}
										}
									} else {
										store_quickPay_Adapter.setSelection(
												position, false);
									}

									store_activityAmount = activityAmount_db;
									store_activityID = acticityID;
									countTotalFee();
								}
							}
						}

					}

				});
		edt_serviceFee.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				inputPrice = s.toString();

				if (!TextUtils.isEmpty(inputPrice)) {
					String firstChar = inputPrice.substring(0, 1);
					// 首位数字为 0----判断是否输入第二个数字
					Pattern p = Pattern.compile("\\d");
					Matcher m = p.matcher(firstChar);
					CommonUtil.log("firstChar:" + firstChar);
					boolean isNum = m.matches();
					CommonUtil.log("isNum:" + isNum);
					if (!isNum) {
						// showInputDialog();
					} else {
						clearActivity();
						cancelCunpon();
						countTotalFee();
					}
				} else {
					clearActivity();
					cancelCunpon();
					countTotalFee();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (!TextUtils.isEmpty(s)) {
					String money = s.toString();// 记录原始数据

					String dValue = s.toString();
					String dValue2 = s.toString();
					String firstChar = dValue.substring(0, 1);
					String secondChar = "";
					if (dValue2.length() > 1) {
						secondChar = dValue2.substring(1, 2);
					}
					CommonUtil.log("firstChar:" + firstChar);
					CommonUtil.log("money:" + money);

					// 第一层判断首位数字是否为 0
					if (firstChar.equals("0")) {

						// 首位数字为 0----判断是否输入第二个数字
						Pattern p = Pattern.compile("\\d");
						Matcher m = p.matcher(secondChar);
						boolean isNum = m.matches();
						CommonUtil.log("isNum:" + isNum);
						if (isNum) {

							// 不是小数点---删除输入的数字
							int nSelStart = 0;
							int nSelEnd = 0;
							boolean nOverMaxLength = false;

							nSelStart = edt_serviceFee.getSelectionStart();
							nSelEnd = edt_serviceFee.getSelectionEnd();

							Double dValue_double = Double
									.parseDouble(secondChar);
							nOverMaxLength = (dValue_double < 10) ? true
									: false;
							// nOverMaxLength = (dValue.length() > 4) ? true
							// : false;
							if (nOverMaxLength) {
								s.delete(nSelStart - 1, nSelEnd);
								// 请读者注意这一行，保持光标原先的位置，而
								// mEditText.setText(s)会让光标跑到最前面，就算是再加mEditText.setSelection(nSelStart)
								// 也不起作用
								edt_serviceFee.setTextKeepState(s);
							}

						} else {
							// 是小数点---判断小数点后面只能两位
							if (secondChar.equals(".")) {

								int nSelStart = 0;
								int nSelEnd = 0;
								boolean nOverMaxLength = false;

								nSelStart = edt_serviceFee.getSelectionStart();
								nSelEnd = edt_serviceFee.getSelectionEnd();

								String[] strs = money.split("\\.");
								if (strs.length > 1) {
									CommonUtil.log("strs[1]:" + strs[1]);

									nOverMaxLength = (strs[1].length() > 2) ? true
											: false;
									if (nOverMaxLength) {
										// Toast_Util.showToast(mContext,
										// "最多输入两位小数");
										s.delete(nSelStart - 1, nSelEnd);
										// 请读者注意这一行，保持光标原先的位置，而
										// mEditText.setText(s)会让光标跑到最前面，就算是再加mEditText.setSelection(nSelStart)
										// 也不起作用
										edt_serviceFee.setTextKeepState(s);
									}

								}
							}
						}

					} else if (firstChar.equals(".")) {
						// 存在小数点----判断是否大于10000且小数点只能两位
						int nSelStart = 0;
						int nSelEnd = 0;
						nSelStart = edt_serviceFee.getSelectionStart();
						nSelEnd = edt_serviceFee.getSelectionEnd();
						s.delete(nSelStart - 1, nSelEnd);
						edt_serviceFee.setTextKeepState(s);
					} else {

						// 首位非0
						Pattern p = Pattern.compile("\\.");
						Matcher m = p.matcher(money);

						CommonUtil.log("money:" + money);
						boolean isFind = m.find();
						CommonUtil.log("m.find():" + isFind);
						if (isFind) {
							// 存在小数点----判断是否大于10000且小数点只能两位
							int nSelStart = 0;
							int nSelEnd = 0;
							boolean nOverMaxLength = false;

							nSelStart = edt_serviceFee.getSelectionStart();
							nSelEnd = edt_serviceFee.getSelectionEnd();

							String[] strs = money.split("\\.");
							if (strs.length > 1) {

								Double dValue_double = Double
										.parseDouble(money);
								// 判断 数字不能大于10000且小数点后面只能为两位
								nOverMaxLength = (strs[1].length() > 2 || dValue_double >= 10000) ? true
										: false;
								if (nOverMaxLength) {
									// Toast_Util.showToast(mContext,
									// "最多输入两位小数");
									s.delete(nSelStart - 1, nSelEnd);
									// 请读者注意这一行，保持光标原先的位置，而
									// mEditText.setText(s)会让光标跑到最前面，就算是再加mEditText.setSelection(nSelStart)
									// 也不起作用
									edt_serviceFee.setTextKeepState(s);
								}

							}

						} else {
							// 不存在小数点--判断是否大于10000
							int nSelStart = 0;
							int nSelEnd = 0;
							boolean nOverMaxLength = false;

							nSelStart = edt_serviceFee.getSelectionStart();
							nSelEnd = edt_serviceFee.getSelectionEnd();

							Double dValue_double = Double.parseDouble(money);
							nOverMaxLength = (dValue_double >= 10000) ? true
									: false;
							// nOverMaxLength = (dValue.length() > 4) ? true :
							// false;
							if (nOverMaxLength) {
								// Toast_Util.showToast(mContext, "最多输入四位整数");
								s.delete(nSelStart - 1, nSelEnd);
								// 请读者注意这一行，保持光标原先的位置，而
								// mEditText.setText(s)会让光标跑到最前面，就算是再加mEditText.setSelection(nSelStart)
								// 也不起作用
								edt_serviceFee.setTextKeepState(s);
							}

						}
					}
				}

			}
		});
		// 输入金额
		edt_serviceFee.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// if (actionId == EditorInfo.IME_ACTION_DONE || (event != null
				// && event.getKeyCode() == KeyEvent.KEYCODE_D)) {
				// 先隐藏键盘
				CommonUtil.hideKeyboard(QuickyPay_Activity.this);
				if (!TextUtils.isEmpty(inputPrice)) {
					CommonUtil.log("------------------inputPrice2:"
							+ inputPrice);
				}
				return true;

				// }

				// return false;

			}
		});
	}

	private void getData() {
		Bundle bundle = getIntent().getExtras();
		if (null != bundle) {
			storeID = bundle.getString("ID", "");
			storeName = bundle.getString("storeName", "");
			// 赋值
			setDataToView();
		}
	}

	private void setDataToView() {
		tv_quick_pay_header.setText(storeName);
	}

	/**
	 * 获取数据后展示
	 */
	private void initAtyList() {
		lv_store_quickPay_storeAty.setVisibility(View.GONE);
		lv_store_quickPay_storeAty.setVisibility(View.GONE);

		if (!CommonUtil.emptyListToString3(storeActList)) {
			lv_store_quickPay_storeAty.setVisibility(View.VISIBLE);
			store_quickPay_Adapter = new Store_quickPay_store_amount_Adapter(
					mContext, storeActList);
			lv_store_quickPay_storeAty.setAdapter(store_quickPay_Adapter);
		}
		if (!CommonUtil.emptyListToString3(platActList)) {
			lv_store_quickPay_platAty.setVisibility(View.VISIBLE);
			// String thresholdAmount = jsonObject.optString("thresholdAmount");
			// String activityAmount = jsonObject.optString("activityAmount");
			// holder.tv_thresholdAmount.setText(NumFormatUtil.saveTwoPointOfDouble(NumFormatUtil.centFormatYuanTodouble(thresholdAmount)));
			// holder.tv_activityAmount.setText(NumFormatUtil.saveTwoPointOfDouble(NumFormatUtil.centFormatYuanTodouble(activityAmount)));
			plat_quickPay_Adapter = new Store_quickPay_plat_amount_Adapter(
					mContext, platActList);
			lv_store_quickPay_platAty.setAdapter(plat_quickPay_Adapter);
		}

	}

	public void getQuickPayList() {

		showCustomCircleProgressDialog(
				null,
				mContext.getResources().getString(
						R.string.common_toast_net_prompt_submit));
		HashMap<String, String> inJsonMap = new HashMap<String, String>();
		inJsonMap.put("storeID", storeID);
		getQuickPayList = new GetQuickPayList(mContext, quick_handler,
				inJsonMap);
		Thread thread = new Thread(getQuickPayList);
		thread.start();
	}

	class QucikList_Handler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				// 导航内容
				storeActList = getQuickPayList.getStoreActList();
				platActList = getQuickPayList.getPlatActList();
				CommonUtil.log("storeActList:" + storeActList);
				CommonUtil.log("platActList:" + platActList);
				initAtyList();
				break;
			case MsgId.DOWN_DATA_F:
				rl_loadData_error.setVisibility(View.VISIBLE);
				rl_commit.setVisibility(View.GONE);
				closeCustomCircleProgressDialog();
				DialogUtils.showToastMsg(mContext, mContext.getResources()
						.getString(R.string.common_toast_net_down_data_fail),
						Toast.LENGTH_SHORT);
				break;
			case MsgId.NET_NOT_CONNECT:
				rl_loadData_error.setVisibility(View.VISIBLE);
				rl_commit.setVisibility(View.GONE);
				closeCustomCircleProgressDialog();
				break;
			default:
				break;
			}
			getUserInfo();
		}
	}

	/**
	 * 提交闪付订单
	 */
	private void commitQucikOrder() {

		// storeID 门店id
		// userID 用户id
		// payment 实付金额
		// totalPrice 总金额
		// activityCouponID 优惠券ID
		// platformDiscountID 平台优惠 ID
		// storeDiscountID 门店优惠ID
		// isPaidByDeposit 余额支付
		// payChannelCode 支付渠道编号
		// remark 备注
		showCustomCircleProgressDialog(null,
				getString(R.string.common_toast_net_prompt_submit));
		final HashMap<String, String> inJsonObject = new HashMap<String, String>();
		inJsonObject.put("storeID", storeID);
		inJsonObject.put("userID", MyApplication.getUserID());
		inJsonObject.put("payment", totalFee * 100 + "");
		inJsonObject.put("totalPrice", inputPrice_double * 100 + "");
		inJsonObject.put("isPaidByDeposit", isAccount);
		inJsonObject.put("activityCouponID", couponID);//优惠券
		inJsonObject.put("storeDiscountID", store_activityID);//店铺券
		inJsonObject.put("platformDiscountID", plat_activityID);//平台券
		inJsonObject.put("payChannelCode", payChannelCode);
		inJsonObject.put("remark", "");
				commitOrder_Thread_StoreQuickPay = new CommitOrder_Thread_StoreQuickPay(
						mContext, commitQucik_Handler, inJsonObject);
				Thread thread = new Thread(commitOrder_Thread_StoreQuickPay);
				thread.start();

	}

	class CommitQucik_Handler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				orderObj = commitOrder_Thread_StoreQuickPay.getOutJsonObject();
				new_orderID = orderObj.optString("orderID", "");
				CommonUtil.log("new_orderID:" + new_orderID);
				jumpAfterCommitOrderSuccess();
				break;
			case MsgId.DOWN_DATA_F:
				closeCustomCircleProgressDialog();
				String MSG = commitOrder_Thread_StoreQuickPay.getOutMsg();
				int code = commitOrder_Thread_StoreQuickPay.getOutCode();
				CommonUtil.log("快捷支付订单返回的错误code：" + code);

				if (code < MsgId.HTTP_APPOINTMENT_ERRORCODE_MIN
						|| code > MsgId.HTTP_APPOINTMENT_ERRORCODE_MAX) {
					// DialogUtils.showToastMsg(mContext, MSG,
					// Toast.LENGTH_SHORT);
					Toast_Util.showToast(mContext, MSG);
				} else {
					DialogUtils
							.showToastMsg(
									mContext,
									getString(R.string.common_toast_net_down_data_fail),
									Toast.LENGTH_SHORT);
				}
				break;
			case MsgId.NET_NOT_CONNECT:
				setCustomDialog(
						getString(R.string.common_toast_net_not_connect), true);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 支付接口成功后的跳转
	 */
	private void jumpAfterCommitOrderSuccess() {
		// 支付实际金额大于0
		if (totalFee > 0d) {
			switch (FLAG_PAYTYPE) {
			case Constants.FLAG_PAYTYPE_ACCOUNT:// 账户余额支付成功
				Toast_Util.showToast(this, "支付成功");
				/**
				 * 调往订单详情
				 */
				Intent intent = new Intent(mContext, OrderDetailActivity.class);
				intent.putExtra("orderID", new_orderID);
				startActivity(intent);
				finish();
				break;
			case Constants.FLAG_PAYTYPE_ALIPAY:// 支付宝支付成功
				// Toast_Util.showToast(this, "支付宝支付");
				// 初始化支付宝参数
				initAlipayData();
				if (null != alipayData) {
					PayQuickUtil.getInstance().aliPay(this, alipayData,
							new_orderID);
				} else {
					CommonUtil.logE("支付宝alipayData为空");
				}
				break;
			case Constants.FLAG_PAYTYPE_WXPAY:// 微信支付成功
				// Toast_Util.showToast(this, "微信支付");
				// 初始化微信支付参数
				initTenpayData();
				if (null != tenpayData) {
					PayQuickUtil.getInstance().wxPay(this, tenpayData,
							new_orderID);
				} else {
					CommonUtil.logE("微信tenpayData为空");
				}
			}
		} else {
			/**
			 * 调往订单详情
			 */
			Intent intent = new Intent(mContext, OrderDetailActivity.class);
			intent.putExtra("orderID", new_orderID);
			startActivity(intent);
			finish();
			// Toast_Util.showToastOnlyOne(mContext, mContext.getResources()
			// .getString(R.string.cannot_wx_ali_pay), Gravity.CENTER,
			// true);
		}
	}

	/**
	 * 处理支付宝返回数据
	 */
	private void initAlipayData() {
		// CommonUtil.log("alipay_jsonObject-------------->" + pay_jsonObject);
		if (null != orderObj) {
			alipayData = orderObj.optString("alipayData", "");
			new_orderID = orderObj.optString("orderID", "");
		}
		CommonUtil.log("alipayData-------------->" + alipayData);
	}

	/**
	 * 处理微信支付返回数据
	 */
	private void initTenpayData() {
		CommonUtil.log("pay_jsonObject-------------->" + "开始初始化微信支付");
		if (null != orderObj) {
			String tenpayData_str = orderObj.optString("tenpayData", "");
			new_orderID = orderObj.optString("orderID", "");
			if (!TextUtils.isEmpty(tenpayData_str)) {
				try {
					tenpayData = new JSONObject(tenpayData_str);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		CommonUtil.log("tenpayData-------------->" + tenpayData);
	}

	/**
	 * 快捷支付 提交支付：自定义Dialog
	 */
	private void showCustomDialog() {
		CustomDialog.Builder builder = new CustomDialog.Builder(this);
		builder.setTitle("提示");
		builder.setMessage("您正在向“" + storeName + "”进行支付操作，请与门店确认");
		builder.setPositiveButton(getString(R.string.common_confirm),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 设置你的操作事项
						// 去支付页面
						CommonUtil.hideKeyboard(QuickyPay_Activity.this);
						commitOrderIsShowTip();
					}
				});

		builder.setNegativeButton(getString(R.string.common_cancel),
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();
	}

	/**
	 * 账户支付弹框提示
	 */
	private void commitOrderIsShowTip() {

		switch (FLAG_PAYTYPE) {
		case Constants.FLAG_PAYTYPE_ACCOUNT:// 账户余额支付成功
			showPayTipsDialog();
			break;
		case Constants.FLAG_PAYTYPE_ALIPAY:// 支付宝支付成功
		case Constants.FLAG_PAYTYPE_WXPAY:// 微信支付成功
			commitQucikOrder();

			break;
		}
	}

	private void showPayTipsDialog() {
		CustomDialog.Builder builder = new CustomDialog.Builder(this);
		builder.setTitle("提示");
		builder.setMessage(getString(R.string.appoint_dialog_tips) + "￥"
				+ NumFormatUtil.saveTwoPointOfDouble(totalFee));
		builder.setPositiveButton(getString(R.string.common_confirm),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 设置你的操作事项
						commitQucikOrder();
					}
				});

		builder.setNegativeButton(getString(R.string.common_cancel),
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();
	}

	/**
	 * 获取账户信息
	 */
	private void getUserInfo() {
		// showCustomCircleProgressDialog(null,
		// getString(R.string.common_toast_net_prompt_submit));
		getUserInfo = new GetUserInfo(mContext, handler_getUserInfo);
		Thread thread = new Thread(getUserInfo);
		thread.start();
	}

	/**
	 * 获取用户信息：---账户余额
	 * 
	 * @author Android开发工程师
	 * 
	 */
	class Handler_getUserInfo extends Handler {
		@Override
		public void handleMessage(Message msg) {
			rl_loadData_empty.setVisibility(View.GONE);
			rl_loadData_error.setVisibility(View.GONE);
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				rl_commit.setVisibility(View.VISIBLE);
				closeCustomCircleProgressDialog();
				userObj = getUserInfo.getUserJson();
				if (userObj == null || ("").equals(userObj)) {
					break;
				}
				accountBalance = userObj.optString("deposit", "0");// 账户余额
				countTotalFee();
				break;
			case MsgId.DOWN_DATA_F:
				rl_loadData_error.setVisibility(View.VISIBLE);
				rl_commit.setVisibility(View.GONE);
				closeCustomCircleProgressDialog();
				DialogUtils.showToastMsg(mContext,
						getString(R.string.common_toast_net_down_data_fail),
						Toast.LENGTH_SHORT);
				break;
			case MsgId.NET_NOT_CONNECT:
				rl_loadData_error.setVisibility(View.VISIBLE);
				rl_commit.setVisibility(View.GONE);
				closeCustomCircleProgressDialog();
				break;
			default:
				break;
			}

		}
	}

	/**
	 * 计算还需支付金额
	 */
	private void accountStillPay() {

		// 还需支付
		stillPay = totalFee - accountBalance_double;

		CommonUtil.log("订单金额：totalFee：" + totalFee);
		CommonUtil.log("账户金额：accountBalance_double：" + accountBalance_double);
		if (ISEnoughOfAccountPay) {
			layout_accountPay.setClickable(true);
			// 账户余额足
			intPayParamsByEnoughOfAcccount(Constants.PAY_SELECTED_ACCOUNT);
		} else {
			layout_accountPay.setClickable(false);
			// 账户余额不足--默认选中账户
			initDefaultSelectedAccountOfNotEnough();

		}

	}

	/**
	 * 判断月是否充足
	 */
	private void judgeAccountIsEnough() {
		CommonUtil.log("账户金额：accountBalance" + accountBalance);
		if (!TextUtils.isEmpty(accountBalance)) {
			accountBalance_double = NumFormatUtil
					.centFormatYuanTodouble(accountBalance);

			if (accountBalance_double == 0d) {
				rl_accountPay_line.setVisibility(View.GONE);
				layout_accountPay.setVisibility(View.GONE);
			} else {
				rl_accountPay_line.setVisibility(View.VISIBLE);
				layout_accountPay.setVisibility(View.VISIBLE);
			}

			if (totalFee <= accountBalance_double) {
				// 账户余额足
				ISEnoughOfAccountPay = true;
			} else {
				// 账户余额不足
				ISEnoughOfAccountPay = false;
			}

			tx_accountPay.setText(NumFormatUtil
					.saveTwoPointOfDouble(accountBalance_double));
		}
	}

	/**
	 * 初始化账户不充足情况下 默认选中账户
	 */
	private void initDefaultSelectedAccountOfNotEnough() {
		isAccount = Constants.ISACCOUNT;
		payChannelCode = Constants.PAYCHANNELCODE_NO;
		FLAG_PAYTYPE = Constants.FLAG_PAYTYPE_ACCOUNT;
		// 选中了
		iv_accountPay_select.setVisibility(View.VISIBLE);
		iv_alipay_select.setVisibility(View.GONE);
		iv_wxPay_select.setVisibility(View.GONE);
		account_pay_isSelected = true;
		ali_pay_isSelected = false;
		wx_pay_isSelected = false;
	}

	/**
	 * 判断账户余额不充足,并设置选中的支付方式的变量和参数
	 */
	private void intPayParamsByNotEnoughOfAcccount(int flag) {
		switch (flag) {
		case Constants.PAY_SELECTED_ACCOUNT:// 账户支付
			if (account_pay_isSelected) {

				isAccount = Constants.ISACCOUNT;
				payChannelCode = Constants.PAYCHANNELCODE_NO;
			} else {
				isAccount = Constants.NOT_ACCOUNT;
				payChannelCode = Constants.PAYCHANNELCODE_NO;
				// 记录支付方式都未选中
			}
			changePayTypeByNotEnoughOfAccountPay(Constants.FLAG_PAYTYPE_ACCOUNT);
			break;
		case Constants.PAY_SELECTED_ALI:// 支付宝支付
			isAccount = Constants.NOT_ACCOUNT;
			payChannelCode = Constants.PAYCHANNELCODE_ALIPAY;// 支付宝支付渠道
			FLAG_PAYTYPE = Constants.FLAG_PAYTYPE_ALIPAY;
			changePayTypeByNotEnoughOfAccountPay(Constants.FLAG_PAYTYPE_ALIPAY);
			break;
		case Constants.PAY_SELECTED_WX:// 微信支付

			isAccount = Constants.NOT_ACCOUNT;
			payChannelCode = Constants.PAYCHANNELCODE_WXPAY;
			FLAG_PAYTYPE = Constants.FLAG_PAYTYPE_WXPAY;
			changePayTypeByNotEnoughOfAccountPay(Constants.FLAG_PAYTYPE_WXPAY);

			break;
		case Constants.PAY_SELECTED_ACCOUNT_ALI:// 支付宝+账户（选中）
			isAccount = Constants.ISACCOUNT;
			payChannelCode = Constants.PAYCHANNELCODE_ALIPAY;
			FLAG_PAYTYPE = Constants.FLAG_PAYTYPE_ALIPAY;
			changePayTypeByNotEnoughOfAccountPay(Constants.FLAG_PAYTYPE_ACCOUNT_ALIPAY);

			break;
		case Constants.PAY_SELECTED_ACCOUNT_WX:// 微信+账户（选中）
			isAccount = Constants.ISACCOUNT;
			payChannelCode = Constants.PAYCHANNELCODE_WXPAY;
			FLAG_PAYTYPE = Constants.FLAG_PAYTYPE_WXPAY;
			changePayTypeByNotEnoughOfAccountPay(Constants.FLAG_PAYTYPE_ACCOUNT_WXPAY);
			break;
		}
	}

	/**
	 * 判断账户余额充足,并设置选中的支付方式的变量和参数
	 */
	private void intPayParamsByEnoughOfAcccount(int flag) {
		switch (flag) {
		case Constants.PAY_SELECTED_ACCOUNT:// 账户支付
			// 账户余额足
			isAccount = Constants.ISACCOUNT;
			payChannelCode = Constants.PAYCHANNELCODE_NO;
			FLAG_PAYTYPE = Constants.FLAG_PAYTYPE_ACCOUNT;
			changePayTypeByEnoughOfAccountPay(Constants.FLAG_PAYTYPE_ACCOUNT);
			break;
		case Constants.PAY_SELECTED_ALI:// 支付宝支付
			isAccount = Constants.NOT_ACCOUNT;
			payChannelCode = Constants.PAYCHANNELCODE_ALIPAY;// 支付宝支付渠道
			FLAG_PAYTYPE = Constants.FLAG_PAYTYPE_ALIPAY;
			changePayTypeByEnoughOfAccountPay(Constants.FLAG_PAYTYPE_ALIPAY);
			break;
		case Constants.PAY_SELECTED_WX:// 微信支付

			isAccount = Constants.NOT_ACCOUNT;
			payChannelCode = Constants.PAYCHANNELCODE_WXPAY;
			FLAG_PAYTYPE = Constants.FLAG_PAYTYPE_WXPAY;
			changePayTypeByEnoughOfAccountPay(Constants.FLAG_PAYTYPE_WXPAY);
			break;
		}
	}

	/**
	 * 改变支付宝、微信
	 * 
	 * @param flag
	 */
	private void changePayTypeByNotEnoughOfAccountPay(int flag) {
		CommonUtil.log("account_pay_isSelected:" + account_pay_isSelected);
		if (account_pay_isSelected) {
			// 选中了
			iv_accountPay_select.setVisibility(View.VISIBLE);
		} else {
			// 取消选中了
			iv_accountPay_select.setVisibility(View.GONE);
		}
		switch (flag) {
		case Constants.FLAG_PAYTYPE_ACCOUNT:// 账户支付

			break;
		case Constants.FLAG_PAYTYPE_ALIPAY:// 支付宝
			iv_wxPay_select.setVisibility(View.GONE);

			iv_alipay_select.setVisibility(View.VISIBLE);

			break;
		case Constants.FLAG_PAYTYPE_WXPAY:// 微信
			iv_alipay_select.setVisibility(View.GONE);

			iv_wxPay_select.setVisibility(View.VISIBLE);

			break;
		case Constants.FLAG_PAYTYPE_ACCOUNT_ALIPAY:// 账户(是否选中)+支付宝）
			iv_alipay_select.setVisibility(View.VISIBLE);

			iv_wxPay_select.setVisibility(View.GONE);

			break;
		case Constants.FLAG_PAYTYPE_ACCOUNT_WXPAY:// // 账户(是否选中)+微信
			iv_alipay_select.setVisibility(View.GONE);

			iv_wxPay_select.setVisibility(View.VISIBLE);

			break;
		}
	}

	/**
	 * 改变支付宝、微信
	 * 
	 * @param flag
	 */
	private void changePayTypeByEnoughOfAccountPay(int flag) {
		iv_accountPay_select.setVisibility(View.GONE);
		iv_alipay_select.setVisibility(View.GONE);
		iv_wxPay_select.setVisibility(View.GONE);
		switch (flag) {
		case Constants.FLAG_PAYTYPE_ACCOUNT:// 账户支付
			iv_accountPay_select.setVisibility(View.VISIBLE);
			break;
		case Constants.FLAG_PAYTYPE_ALIPAY:// 支付宝
			iv_alipay_select.setVisibility(View.VISIBLE);
			break;
		case Constants.FLAG_PAYTYPE_WXPAY:// 微信
			iv_wxPay_select.setVisibility(View.VISIBLE);
			break;
		}
	}

	/**
	 * 获取可用优惠券
	 */
	private void getUseableCunponList() {
		HashMap<String, String> inJsonMap = new HashMap<String, String>();
		// userID 用户ID ANS 32 M 用户ID
		// status 状态 N 4 O 0 未使用1 已使用
		// 2 已过期
		// serviceId 项目ID ANS 64 O 项目ID
		// workerId 技师 ID ANS 64 O 技师ID
		inJsonMap.put("userID", MyApplication.getUserID());
		inJsonMap.put("status", "0");
		inJsonMap.put("serviceId", "");
		inJsonMap.put("workerId", "");
		inJsonMap.put("storeID", storeID);
		inJsonMap.put("payment", countTotalPrice_NoCunpon() * 100 + "");
		showCustomCircleProgressDialog(
				null,
				mContext.getResources().getString(
						R.string.common_toast_net_prompt_submit));
		useableCouponList = new UseableCouponList(mContext, handler_cunPonList,
				inJsonMap);
		Thread thread = new Thread(useableCouponList);
		thread.start();
	}

	/**
	 * 处理 可用优惠券
	 * 
	 * @author Android开发工程师
	 * 
	 */
	class Handler_CunPonList extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				cunponList = useableCouponList.getCouponList();
				cunponJsonObject = useableCouponList.getCouponObjecct();
				CommonUtil.log("可用优惠券：CouponList" + cunponList);

				// 判断有无代金券
				judgeIsHasCunpon();
				break;
			case MsgId.DOWN_DATA_F:
				closeCustomCircleProgressDialog();
				String MSG = useableCouponList.getOutMsg();
				int code = useableCouponList.getOutCode();
				CommonUtil.log("获取可用优惠券返回的错误code：" + code);

				if (code < MsgId.HTTP_APPOINTMENT_ERRORCODE_MIN
						|| code > MsgId.HTTP_APPOINTMENT_ERRORCODE_MAX) {
					// DialogUtils.showToastMsg(mContext, MSG,
					// Toast.LENGTH_SHORT);
					Toast_Util.showToast(mContext, MSG);
				} else {
					DialogUtils.showToastMsg(
							mContext,
							mContext.getResources().getString(
									R.string.common_toast_net_down_data_fail),
							Toast.LENGTH_SHORT);
				}
				break;
			case MsgId.NET_NOT_CONNECT:
				setCustomDialog(
						mContext.getResources().getString(
								R.string.common_toast_net_not_connect), true);
				break;

			}
		}

	}

	/**
	 * 选择可用的代金券
	 */
	private void chooseUsableCunpon() {
		if (intent == null) {
			intent = new Intent();
		}

		intent.setClass(mContext, Appointment_selectCunpon_Activity.class);
		intent.putExtra("cunponList", cunponJsonObject.toString());
		// intent.putExtra(Constants.APPOINT_VIEW,
		// Constants.APPOINT_VIEW_STORE_ONLYVISIT);
		startActivityForResult(intent, REQUEST_SELECT_COUPONS);
	}

	/**
	 * 处理服务券
	 * 
	 * @param jsonObject
	 */
	private void initCunpon(JSONObject jsonObject) {
		// ID 劵ID ANS 32 M 劵ID
		// name 优惠券名称 ANS 64 M
		// type 优惠券类型 N 4 O
		// price 面额 N 12 M 面额
		// expiryDate 失效日期 ANS 20 M 失效日期
		// startTime 有效开始时间 AN 20 M 有效开始时间
		// endTime 有效结束时间 AN 20 M 有效结束时间
		// icon 图片地址 ANS 256 M 图片地址
		couponID = jsonObject.optString("ID", "");
		couponPrice = jsonObject.optString("price", "");

		CommonUtil.log("处理服务券couponID:" + couponID);
		CommonUtil.log("处理服务券couponPrice:" + couponPrice);
		countTotalFee();
		Toast_Util.showToast(mContext,
				"总价减免 ￥" + NumFormatUtil.centFormatYuanToString(couponPrice));
	}

	/**
	 * 判断是否优惠券可用
	 */
	private void judgeIsHasCunpon() {

		// 判断有无代金券
		if (CommonUtil.emptyListToString3(cunponList)) {
			couponPrice = "0";
			couponID = "";// 清空优惠券
			tv_quanPrice.setText(getResources().getString(R.string.no_cunpon2));
			Toast_Util.showToast(mContext,
					getResources().getString(R.string.no_cunpon));
		} else {
			chooseUsableCunpon();// 去优惠券列表选择可用优惠券
		}

	}

	/**
	 * 清空活动id和选中状态
	 */
	private void clearActivity() {
		store_activityAmount = 0d;
		plat_activityAmount = 0d;
		initAtyList();
	}

	/**
	 * 取消优惠券
	 */
	private void cancelCunpon() {
		couponID = "";
		couponPrice = "0";
		CommonUtil.log("处理服务券couponID:" + couponID);
		CommonUtil.log("处理服务券couponPrice:" + couponPrice);
		countTotalFee();
	}

	/**
	 * 计算总费用
	 */
	private void countTotalFee() {

		if (TextUtils.isEmpty(inputPrice)) {
			inputPrice = "0";
		}
		inputPrice_double = Double.parseDouble(inputPrice);
		CommonUtil.log("inputPrice_double:" + inputPrice_double);
		CommonUtil.log("couponPrice:" + couponPrice);
		CommonUtil.log("store_activityAmount:" + store_activityAmount);
		CommonUtil.log("plat_activityAmount:" + plat_activityAmount);
		// 优惠券 减免
		double coupon_db = 0d;
		if (!TextUtils.isEmpty(couponPrice)) {
			coupon_db = NumFormatUtil
					.centFormatYuanTodouble(couponPrice.trim());
		}

		totalFee = inputPrice_double - coupon_db - store_activityAmount
				- plat_activityAmount;

		if (totalFee < 0) {// 当订单金额小于0
			totalFee = 0;

		}

		if (coupon_db > 0d) {
			tv_quanPrice.setText("-" + "￥"
					+ NumFormatUtil.saveTwoPointOfDouble(coupon_db));
		} else {
			tv_quanPrice.setText("");
		}
		tv_realPay.setText(NumFormatUtil.saveTwoPointOfDouble(totalFee));

		// 账户余额是否充足
		judgeAccountIsEnough();
		accountStillPay();
	}

	/**
	 * 计算无优惠券的实际价格
	 * 
	 * @param couponPrice
	 */
	private double countTotalPrice_NoCunpon() {
		if (TextUtils.isEmpty(inputPrice)) {
			inputPrice = "0";
		}
		inputPrice_double = Double.parseDouble(inputPrice);
		CommonUtil.log("inputPrice_double:" + inputPrice_double);
		CommonUtil.log("store_activityAmount:" + store_activityAmount);
		CommonUtil.log("plat_activityAmount:" + plat_activityAmount);
		// 优惠券 减免
		double coupon_db = 0d;
		if (!TextUtils.isEmpty(couponPrice)) {
			coupon_db = NumFormatUtil
					.centFormatYuanTodouble(couponPrice.trim());
		}

		totalFee = inputPrice_double - store_activityAmount
				- plat_activityAmount;

		if (totalFee < 0) {// 当订单金额小于0
			totalFee = 0;

		}

		return totalFee;

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.layout_selectDaijinquan:// 选择代金券
			getUseableCunponList();// 获取可用优惠券
			break;

		case R.id.layout_accountPay:// 账户支付
			if (account_pay_isSelected) {
				account_pay_isSelected = false;
			} else {
				account_pay_isSelected = true;
			}
			CommonUtil.log("account_pay_isSelected:" + account_pay_isSelected);
			CommonUtil.log("ali_pay_isSelected:" + ali_pay_isSelected);
			CommonUtil.log("wx_pay_isSelected:" + wx_pay_isSelected);
			CommonUtil.log("ISEnoughOfAccountPay:" + ISEnoughOfAccountPay);
			if (ISEnoughOfAccountPay) {
				intPayParamsByEnoughOfAcccount(Constants.PAY_SELECTED_ACCOUNT);
			} else {
				if (account_pay_isSelected && !ali_pay_isSelected
						&& !wx_pay_isSelected) {
					intPayParamsByNotEnoughOfAcccount(Constants.PAY_SELECTED_ACCOUNT);

				} else if (account_pay_isSelected && ali_pay_isSelected
						&& !wx_pay_isSelected) {// 账户+支付宝
					intPayParamsByNotEnoughOfAcccount(Constants.PAY_SELECTED_ACCOUNT_ALI);
				} else if (account_pay_isSelected && !ali_pay_isSelected
						&& wx_pay_isSelected) {// 账户+微信
					intPayParamsByNotEnoughOfAcccount(Constants.PAY_SELECTED_ACCOUNT_WX);
				} else if (!account_pay_isSelected && ali_pay_isSelected
						&& !wx_pay_isSelected) {// 支付宝
					intPayParamsByNotEnoughOfAcccount(Constants.PAY_SELECTED_ALI);
				} else if (!account_pay_isSelected && !ali_pay_isSelected
						&& wx_pay_isSelected) {// 微信
					intPayParamsByNotEnoughOfAcccount(Constants.PAY_SELECTED_WX);
				} else if (!account_pay_isSelected && !ali_pay_isSelected
						&& !wx_pay_isSelected) {// 无支付方式
					intPayParamsByNotEnoughOfAcccount(Constants.PAY_SELECTED_ACCOUNT);
				}
			}
			break;
		case R.id.layout_alipay:// 支付宝支付

			if (ISEnoughOfAccountPay) {
				intPayParamsByEnoughOfAcccount(Constants.PAY_SELECTED_ALI);
			} else {

				// 账户不充足--第三方支付 二选一--该点击：选中 支付宝
				ali_pay_isSelected = true;
				wx_pay_isSelected = false;

				CommonUtil.log("account_pay_isSelected:"
						+ account_pay_isSelected);
				if (account_pay_isSelected) {

					intPayParamsByNotEnoughOfAcccount(Constants.PAY_SELECTED_ACCOUNT_ALI);
				} else {
					intPayParamsByNotEnoughOfAcccount(Constants.PAY_SELECTED_ALI);
				}
			}
			break;
		case R.id.layout_wxPay:// 微信支付
			if (ISEnoughOfAccountPay) {
				intPayParamsByEnoughOfAcccount(Constants.PAY_SELECTED_WX);
			} else {
				// 账户不充足--第三方支付 二选一--该点击：选中 微信
				ali_pay_isSelected = false;
				wx_pay_isSelected = true;
				CommonUtil.log("account_pay_isSelected:"
						+ account_pay_isSelected);
				if (account_pay_isSelected) {
					intPayParamsByNotEnoughOfAcccount(Constants.PAY_SELECTED_ACCOUNT_WX);
				} else {
					intPayParamsByNotEnoughOfAcccount(Constants.PAY_SELECTED_WX);
				}

			}
			break;

		case R.id.bt_appoint_commit:
			CommonUtil.log("------------------inputPrice:" + inputPrice);
			if (TextUtils.isEmpty(edt_serviceFee.getText().toString())) {
				Toast_Util.showToast(mContext, "请您输入消费金额");
			} else {
				// if (!("0").equals(inputPrice) && !("0.00").equals(totalFee))
				// {
				// CommonUtil.log("------------------inputPrice2:"
				// + inputPrice);
				if (!ISEnoughOfAccountPay) {
					// 账户余额不足
					if (!ali_pay_isSelected && !wx_pay_isSelected) {
						if (accountBalance_double > 0d) {
							Toast_Util.showToast(
									mContext,
									mContext.getResources().getString(
											R.string.pay_no_enough_tips));
						} else {
							Toast_Util.showToast(
									mContext,
									mContext.getResources().getString(
											R.string.pay_no_choice_payType));
						}
					} else {
						showCustomDialog();
					}
				}else {
					showCustomDialog();
				}

			}
			break;
		case R.id.rl_loadData_error:// 重新加载数据
			getQuickPayList();
			break;
		}

	}
}
