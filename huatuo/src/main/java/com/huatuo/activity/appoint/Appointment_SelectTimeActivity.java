package com.huatuo.activity.appoint;

import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huatuo.R;
import com.huatuo.adapter.Lv_selectTime_ClockAdapter;
import com.huatuo.adapter.Lv_selectTime_DayAdapter;
import com.huatuo.base.BaseActivity;
import com.huatuo.custom_widget.CustomGridView;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.thread.GetServicesTimeListInfo_Store;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.DialogUtils;
import com.huatuo.util.HandleServiceTimeUtil;
import com.huatuo.util.Toast_Util;

public class Appointment_SelectTimeActivity extends BaseActivity implements OnClickListener{
	private Context mContext;
	private TextView tv_selectAppointTime_commit;
	private CustomGridView lv_selectTime_day;
	private Lv_selectTime_DayAdapter lv_selectTime_DayAdapter;
	private GetServicesTimeListInfo_Store getServicesTimeListInfo;
	private JSONObject serviceTime_jsonObj = null;

	private GridView gv_selectTime_clock;
	private Lv_selectTime_ClockAdapter lv_selectTime_ClockAdapter;

	private String workerID = "";
	private String serviceID = "";
	private String amount = "";
	private String dayCount = "";
	private Handler handler_serviceTime;
	private String oldDay = "";
	private String newDay = "";

	private String memoryDay = "";
	private String memoryClock = "";

	private String currentDay = "";
	private String currentDayAfterFormat = "";
	private String currentClock = "";
	// 时刻
	private String timeSlot = "";
	private String isAvailable = "1";
	private String transportationFee = "";
	private RelativeLayout rl_loadData_error,rl_loadData_empty;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_appoint_time);
		mContext = this;
		findViewById();
		initHandler();
		setListener();
		getExtras();
	}

	// /***
	// * 将activity 的创建模式设置为singletask,
	// * 使用这个方法可以再其他activity想这个activity发送Intent时，这个Intent能够及时更新
	// */
	// @Override
	// protected void onNewIntent(Intent intent) {
	// CommonUtil.logE("--------------------------------------------onNewIntent-----------------------------"
	// );
	// super.onNewIntent(intent);
	// setIntent(intent); // 这一句必须的，否则Intent无法获得最新的数据
	// getExtras();
	// }

	private void initHandler() {
		handler_serviceTime = new Handler_serviceTime();

	}

	private void findViewById() {
		tv_selectAppointTime_commit = (TextView) findViewById(R.id.tv_selectAppointTime_commit);

		lv_selectTime_day = (CustomGridView) findViewById(R.id.lv_selectTime_day);
		lv_selectTime_DayAdapter = new Lv_selectTime_DayAdapter(mContext);
		lv_selectTime_day.setAdapter(lv_selectTime_DayAdapter);

		gv_selectTime_clock = (GridView) findViewById(R.id.gv_selectTime_clock);
		lv_selectTime_ClockAdapter = new Lv_selectTime_ClockAdapter(mContext);
		gv_selectTime_clock.setAdapter(lv_selectTime_ClockAdapter);
		//加载数据失败页面
				rl_loadData_error = (RelativeLayout) findViewById(R.id.rl_loadData_error);
				rl_loadData_error.setOnClickListener(this);
				//加载数据前的白界面
				rl_loadData_empty = (RelativeLayout) findViewById(R.id.rl_loadData_empty);
	}

	private void setListener() {
		bindListener();
		tv_selectAppointTime_commit.setOnClickListener(this);
		lv_selectTime_day.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				lv_selectTime_DayAdapter.setSeclection(position);
				oldDay = (String) parent.getAdapter().getItem(position);
				newDay = (String) HandleServiceTimeUtil.arr_new_days[position];
				getClockList(oldDay);

			}
		});

	}

	private void getClockList(final String day) {

		// isAvailable = click_isAvailable;
		// lv_selectTime_ClockAdapter.setSeclection(position);
		// timeSlot = HandleServiceTimeUtil.map_day_Clocks.get(day)[position];
		// transportationFee =
		// HandleServiceTimeUtil.map_day_trafficFee.get(day)[position];

		String[] arrClock = HandleServiceTimeUtil.map_day_Clocks.get(day);
		lv_selectTime_ClockAdapter.add(arrClock, day);
		getMemoryColock();
		gv_selectTime_clock.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String click_isAvailable = HandleServiceTimeUtil.map_day_canService.get(day)[position];
				if (("1").equals(click_isAvailable)) {
					// 可约时间
					lv_selectTime_ClockAdapter.setSeclection(position);
					
					isAvailable = click_isAvailable;
					timeSlot = HandleServiceTimeUtil.map_day_Clocks.get(day)[position];
					transportationFee = HandleServiceTimeUtil.map_day_trafficFee.get(day)[position];
					currentDay = oldDay;
					currentDayAfterFormat = newDay;
					currentClock = timeSlot;
				} else {
					Toast_Util.showToastOnlyOne(mContext, "该时间段不能预约，请选择其他时间", Gravity.CENTER, false);
				}

			}
		});
	}

	private void getExtras() {
		Bundle bundle = getIntent().getExtras();
		if (null != bundle) {
			workerID = bundle.getString("workerID", "");
			serviceID = bundle.getString("serviceID", "");
			amount = bundle.getString("amount", "");
			dayCount = bundle.getString("dayCount", "");
			memoryDay = bundle.getString("memoryDay", "");
			memoryClock = bundle.getString("memoryClock", "");
		}

		getServiceTimeList();
	}

	/**
	 * 获取服务时间列表
	 */
	private void getServiceTimeList() {
		showCustomCircleProgressDialog(null, mContext.getResources().getString(R.string.common_toast_net_prompt_submit));

		HashMap<String, String> inJson = new HashMap<String, String>();
		// workerID 技师ID ANS 64 O 技师ID
		// serviceID 服务项目ID ANS 64 M 项目id
		// amount 选择的钟数 ANS 16 M 选择的钟数
		// dayCount 返回的天数 ANS 16 O 不填默认查询出4天的数据
		inJson.put("workerID", workerID);// 技师ID
		inJson.put("serviceID", serviceID);// 服务项目ID
		inJson.put("amount", amount + "");// 选择的钟数
		inJson.put("dayCount", dayCount);// 返回的天数

		getServicesTimeListInfo = new GetServicesTimeListInfo_Store(mContext, handler_serviceTime, inJson);
		Thread thread = new Thread(getServicesTimeListInfo);
		thread.start();

	}

	/**
	 * 处理服务时间列表数据
	 * 
	 * @author Android开发工程师
	 * 
	 */
	class Handler_serviceTime extends Handler {
		@Override
		public void handleMessage(Message msg) {
			rl_loadData_empty.setVisibility(View.GONE);
			rl_loadData_error.setVisibility(View.GONE);
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				serviceTime_jsonObj = getServicesTimeListInfo.getOutJson();
				// 处理服务时间列表
				HandleServiceTimeUtil.getDayArray(serviceTime_jsonObj);
				lv_selectTime_DayAdapter.add(HandleServiceTimeUtil.arr_old_days, HandleServiceTimeUtil.arr_new_days);
				getMemoryDay();
				getClockList(oldDay);
				break;
			case MsgId.DOWN_DATA_F:
				closeCustomCircleProgressDialog();

				String MSG = getServicesTimeListInfo.getOutMsg();
				int code = getServicesTimeListInfo.getOutCode();
				CommonUtil.log("获取时间表返回的错误code：" + code);

				if (code < MsgId.HTTP_APPOINTMENT_ERRORCODE_MIN || code > MsgId.HTTP_APPOINTMENT_ERRORCODE_MAX) {
					// DialogUtils.showToastMsg(mContext, MSG,
					// Toast.LENGTH_SHORT);
					Toast_Util.showToast(mContext, MSG);
				} else {
					DialogUtils.showToastMsg(mContext, mContext.getResources().getString(R.string.common_toast_net_down_data_fail), Toast.LENGTH_SHORT);
				}
				
				rl_loadData_error.setVisibility(View.VISIBLE);
				closeCustomCircleProgressDialog();
				break;
			case MsgId.NET_NOT_CONNECT:
				// 加载数据失败
				rl_loadData_error.setVisibility(View.VISIBLE);
				closeCustomCircleProgressDialog();
				break;

			}
		}
	}

	/**
	 * 获取之前选中的天
	 */
	private void getMemoryDay() {
		int position = 0;
		for (int i = 0; i < HandleServiceTimeUtil.arr_old_days.length; i++) {
			String nd = HandleServiceTimeUtil.arr_old_days[i];
			CommonUtil.log("nd:" + nd + "=================memoryDay:" + memoryDay);
			if (nd.equals(memoryDay)) {
				position = i;
				break;
			}
		}
		oldDay = (String) HandleServiceTimeUtil.arr_old_days[position];
		newDay = (String) HandleServiceTimeUtil.arr_new_days[position];
		lv_selectTime_DayAdapter.setSeclection(position);
	}

	/**
	 * 获取之前选中的时钟
	 */
	private void getMemoryColock() {

		// 设置默认选中的时间段
		if (!TextUtils.isEmpty(memoryDay)) {
			if (memoryDay.equals(oldDay)) {
				if (TextUtils.isEmpty(timeSlot)) {
					int position = getCurrentClockPostion(memoryDay, memoryClock);
					lv_selectTime_ClockAdapter.setSeclection(position);
				} else {
					if (!currentDay.equals(oldDay)) {
						CommonUtil.log("---------------------------lv_selectTime_ClockAdapter.setSeclection(-1)-------------------------");
						lv_selectTime_ClockAdapter.setSeclection(-1);
					} else {
						int position = getCurrentClockPostion(currentDay, timeSlot);
						lv_selectTime_ClockAdapter.setSeclection(position);
					}
				}

			} else {
				if (TextUtils.isEmpty(timeSlot)) {
					lv_selectTime_ClockAdapter.setSeclection(-1);
				} else {
					if (!currentDay.equals(oldDay)) {
						CommonUtil.log("---------------------------lv_selectTime_ClockAdapter.setSeclection(-1)-------------------------");
						lv_selectTime_ClockAdapter.setSeclection(-1);
					} else {
						int position = getCurrentClockPostion(currentDay, timeSlot);
						lv_selectTime_ClockAdapter.setSeclection(position);
					}
				}
			}
		} else {
			if (!TextUtils.isEmpty(timeSlot)) {
				if (!currentDay.equals(oldDay)) {
					CommonUtil.log("---------------------------lv_selectTime_ClockAdapter.setSeclection(-1)-------------------------");
					lv_selectTime_ClockAdapter.setSeclection(-1);
				} else {
					int position = getCurrentClockPostion(currentDay, timeSlot);
					lv_selectTime_ClockAdapter.setSeclection(position);
				}
			}
		}
	}

	/**
	 * 获取时刻所在的位置
	 * 
	 * @param day
	 * @param memoryClock
	 * @return
	 */
	private int getCurrentClockPostion(String day, String memoryClock) {
		int position = 0;
		String[] arrClock = HandleServiceTimeUtil.map_day_Clocks.get(day);
		for (int i = 0; i < arrClock.length; i++) {
			String clock = arrClock[i];
			CommonUtil.log("clock:" + clock + "=================memoryClock:" + memoryClock);
			if (clock.equals(memoryClock)) {
				position = i;
				break;
			}
		}

		return position;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_selectAppointTime_commit:// 确认
			if (("1").equals(isAvailable)) {
				if (!TextUtils.isEmpty(timeSlot)) {
					Intent intent = new Intent();
					intent.putExtra("memoryDay", currentDay);
					intent.putExtra("memoryClock", timeSlot);
					intent.putExtra("old_clock", currentDay + " " + timeSlot);
					intent.putExtra("new_clock", currentDayAfterFormat + " " + timeSlot);
					intent.putExtra("transportationFee", transportationFee);
					setResult(Activity.RESULT_OK, intent);
					finish();
				} else {
					if (!TextUtils.isEmpty(memoryDay) && !TextUtils.isEmpty(memoryClock)) {
						finish();
					} else {
						Toast_Util.showToast(mContext, "您还未选择时间");
					}
				}

			} else {
				Toast_Util.showToast(mContext, "该时间段不能预约，请选择其他时间");
			}
			break;
		case R.id.rl_loadData_error://重新加载数据
			getServiceTimeList();
			break;
		}
		
	}

}
