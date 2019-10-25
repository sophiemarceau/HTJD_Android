package com.huatuo.activity.find;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huatuo.R;
import com.huatuo.activity.find.FindDetail_StoreListActivity.HandlerAddCollect;
import com.huatuo.activity.find.FindDetail_StoreListActivity.HandlerCancelCollect;
import com.huatuo.activity.find.FindDetail_StoreListActivity.StoreList_Handler;
import com.huatuo.activity.login.LoginActivity;
import com.huatuo.adapter.Technician_ListViewAdapter;
import com.huatuo.base.BaseActivity;
import com.huatuo.base.MyApplication;
import com.huatuo.custom_widget.CustomListView;
import com.huatuo.custom_widget.ObservableScrollView;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.thread.AddCollect;
import com.huatuo.net.thread.CancelCollect;
import com.huatuo.net.thread.GetFindDetail_List;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Constants;
import com.huatuo.util.Custom_Toast_Collect;
import com.huatuo.util.DialogUtils;
import com.huatuo.util.JumpTargetActivityUtil;
import com.huatuo.util.StringUtil;
import com.huatuo.util.Toast_Util;
import com.huatuo.util.UmengEventUtil;
import com.huatuo.util.UmengShare;
import com.nostra13.universalimageloader.core.ImageLoader;

public class FindDetail_TechListActivity extends BaseActivity implements
		OnClickListener {
	private Context mContext;
	private ImageView iv_find_icon;
	private TextView tv_find_content, tv_find_info;
	private ObservableScrollView scrollView1;

	private RelativeLayout rl_find_icon,rl_find_icon_bg;
	private ImageView iv_top;
	private TextView tv_name;
	private LinearLayout ll_back, ll_share, ll_collect;
	private CustomListView lv_find_list_tech;
	// 获取项目列表
	private GetFindDetail_List getFindDetail_TechList;
	private Handler techList_Handler;
	private Technician_ListViewAdapter adapter_tech;
	private ArrayList<JSONObject> techlist;

	private String ID = "";//发现ID
	private int pageNo = 1;// 第几页
	private String pageSize = Constants.PAGE_SIZE_LIST;
	private int pageCount;// 总共页数
	private int totalCount;
	
	private String publishDate = "";
	private String image = "";
	private String title = "";
	private String description = "";
	private String isFavorite = "";
	// 收藏、分享
		private ImageView iv_share, iv_collect;
		private AddCollect addCollect;// 收藏
		private CancelCollect cancelCollect;// 取消收藏
		private boolean isCollect = false;
		public Handler handlerAddCollect, handlerCancelCollect;
		private boolean push = false;//是否是推送
		
		private RelativeLayout rl_loadData_error, rl_loadData_empty;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		mContext = this;
		setContentView(R.layout.activity_find_detail_tech_list);
		initHandler();
		receiveExtras();
		findViewById();
		setOnClickListener();
		getTechList();
	}
	/***
	 * 将activity 的创建模式设置为singletask,
	 * 使用这个方法可以再其他activity想这个activity发送Intent时，这个Intent能够及时更新
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		CommonUtil.logE("--------------------------------------------onNewIntent-----------------------------" );
		super.onNewIntent(intent);
		setIntent(intent); // 这一句必须的，否则Intent无法获得最新的数据
		receiveExtras();
	}
	private void initHandler() {
		techList_Handler = new TechList_Handler();
		handlerAddCollect = new HandlerAddCollect();
		handlerCancelCollect = new HandlerCancelCollect();
	}
	private void receiveExtras(){
		Bundle bundle = getIntent().getExtras();
		if (null != bundle) {
			ID = bundle.getString("ID");
			push = bundle.getBoolean("push");
			CommonUtil.log("ID" + ID);
		}
	}
	
	private void findViewById() {

		iv_find_icon = (ImageView) findViewById(R.id.iv_find_icon);
		//设置图片的比例
		CommonUtil.initScreen(this);
		LayoutParams layoutParams = iv_find_icon.getLayoutParams();
		layoutParams.width = (CommonUtil.WIDTH_SCREEN-CommonUtil.dip2px(mContext, 0));
		layoutParams.height = layoutParams.width/4*3;
		iv_find_icon.setLayoutParams(layoutParams);
		
		rl_find_icon_bg = (RelativeLayout) findViewById(R.id.rl_find_icon_bg);
		LayoutParams layoutParams2 = rl_find_icon_bg.getLayoutParams();
		layoutParams2.width = (CommonUtil.WIDTH_SCREEN-CommonUtil.dip2px(mContext, 0));
		layoutParams2.height = layoutParams2.width/4*3;
		rl_find_icon_bg.setLayoutParams(layoutParams2);
		
		tv_find_content = (TextView) findViewById(R.id.tv_find_content);
		tv_find_info = (TextView) findViewById(R.id.tv_find_info);

		scrollView1 = (ObservableScrollView) findViewById(R.id.scrollView1);
		
		rl_find_icon = (RelativeLayout) findViewById(R.id.rl_find_icon);

		iv_top = (ImageView) findViewById(R.id.iv_top);
		tv_name = (TextView) findViewById(R.id.tv_name);

		iv_top.setFocusable(true);
		iv_top.setFocusableInTouchMode(true);
		iv_top.requestFocus();

		
		ll_back = (LinearLayout) findViewById(R.id.ll_back1);
		ll_share = (LinearLayout) findViewById(R.id.ll_share);
		ll_collect = (LinearLayout) findViewById(R.id.ll_collect);
		iv_share = (ImageView) findViewById(R.id.iv_share);// 分享
		iv_collect = (ImageView) findViewById(R.id.iv_collect);// 收藏
		
		lv_find_list_tech = (CustomListView) findViewById(R.id.lv_find_list_tech);
		adapter_tech = new Technician_ListViewAdapter(this);
		lv_find_list_tech.setAdapter(adapter_tech);
		
		// 加载数据失败页面
		rl_loadData_error = (RelativeLayout) findViewById(R.id.rl_loadData_error);
		rl_loadData_error.setOnClickListener(this);
		// 加载数据前的白界面
		rl_loadData_empty = (RelativeLayout) findViewById(R.id.rl_loadData_empty);

	}

	private void setOnClickListener() {
		LinearLayout ll_back = (LinearLayout) findViewById(R.id.ll_back1);
		ll_back.setOnClickListener(this);
		iv_share.setOnClickListener(this);
		iv_collect.setOnClickListener(this);
		scrollListener();
	}

	private void scrollListener() {
		scrollView1
				.setScrollViewListener(new com.huatuo.custom_widget.ScrollViewListener() {

					@SuppressLint("NewApi")
					@Override
					public void onScrollChanged(
							ObservableScrollView scrollView, int x, int y,
							int oldx, int oldy) {
						rl_find_icon.setY(oldy / 2);
						if (0 != y && 0 != oldy) {
							float alpha = (float) 2 * y
									/ (float) rl_find_icon.getHeight();
							iv_top.setAlpha(alpha);
							tv_name.setAlpha(alpha);
						}
						ll_back.setBackgroundColor(Color.argb(0, 0, 0, 0));
						ll_share.setBackgroundColor(Color.argb(0, 0, 0, 0));
						ll_collect.setBackgroundColor(Color.argb(0, 0, 0, 0));

					}
				});

		scrollView1
				.setOnBorderListener(new com.huatuo.custom_widget.OnBorderListener() {

					@SuppressLint("NewApi")
					@Override
					public void onTop() {
						// TODO Auto-generated method stub
						rl_find_icon.setY(0);
						ll_back.setBackgroundResource(R.drawable.icon_bg);
						ll_share.setBackgroundResource(R.drawable.icon_bg);
						ll_collect.setBackgroundResource(R.drawable.icon_bg);
					}

					@SuppressLint("NewApi")
					@Override
					public void onBottom() {
						CommonUtil.log("pageNo:" + pageNo);
						CommonUtil.log("pageCount:" + pageCount);

						if (pageNo < pageCount) {
							++pageNo;
							getTechList();
						}else {
							if(pageNo > 1){
//								 Toast_Util.showToastOnlyOne(mContext, mContext.getResources().getString(R.string.load_no_more_data));
								}
						}
					}
				});

	}

	/**
	 * 获取服务列表
	 */
	private void getTechList() {
		showCustomCircleProgressDialog(
				null,
				mContext.getResources().getString(
						R.string.common_toast_net_prompt_submit));
		HashMap<String, String> inJsonObject = new HashMap<String, String>();
		
//		ID	发现ID
//		latitude	纬度
//		longitude	经度
//		pageStart	当前页数
//		pageOffset	获取数据条数
		inJsonObject.put("ID", ID);
		inJsonObject.put("userID", MyApplication.getUserID());
		inJsonObject.put("pageStart", pageNo+"");
		inJsonObject.put("pageOffset", pageSize+"");
		
		String lng = CommonUtil.getStringOfSharedPreferences(mContext.getApplicationContext(), "NOW_LNG", "");
		String lat = CommonUtil.getStringOfSharedPreferences(mContext.getApplicationContext(), "NOW_LAT", "");
		inJsonObject.put("longitude", lng);
		inJsonObject.put("latitude", lat);
		getFindDetail_TechList = new GetFindDetail_List(mContext,
				techList_Handler, inJsonObject);
		Thread thread = new Thread(getFindDetail_TechList);
		thread.start();
	}


	class TechList_Handler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			rl_loadData_empty.setVisibility(View.GONE);
			rl_loadData_error.setVisibility(View.GONE);
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
//				pullToRefreshGridView.onRefreshComplete();
				// 获取返回数据的页数
				JSONObject jsonObject = getFindDetail_TechList
						.getOutJson();
				techlist = getFindDetail_TechList.getSkillList();
				if (null != jsonObject) {
					String totalPages = jsonObject.optString("pageCount", "0");
					String totalCounts = jsonObject
							.optString("tupleCount", "0");
					CommonUtil.log("发现项目搜索总共页数：pageCount：" + pageCount);
					CommonUtil.log("发现项目搜索总共条数：totalCounts：" + totalCounts);
					if (!TextUtils.isEmpty(totalPages)) {
						pageCount = Integer.parseInt(totalPages);
					}

					if (!TextUtils.isEmpty(totalCounts)) {
						totalCount = Integer.parseInt(totalCounts);

					}
					handleData(jsonObject);
				}


				
				break;
			case MsgId.DOWN_DATA_F:
				// 加载数据失败
				if (pageNo > 1) {
					pageNo--;
				}
				if (pageNo == 1) {
					rl_loadData_error.setVisibility(View.VISIBLE);
				} else {
					rl_loadData_error.setVisibility(View.GONE);
				}
				closeCustomCircleProgressDialog();
				DialogUtils.showToastMsg(mContext, mContext.getResources()
						.getString(R.string.common_toast_net_down_data_fail),
						Toast.LENGTH_SHORT);
				break;
			case MsgId.NET_NOT_CONNECT:
				// 加载数据失败
				if (pageNo > 1) {
					pageNo--;
				}
				if (pageNo == 1) {
					rl_loadData_error.setVisibility(View.VISIBLE);
				} else {
					rl_loadData_error.setVisibility(View.GONE);
				}
				closeCustomCircleProgressDialog();
				break;
			}
		}
	}
	
	private void handleData(JSONObject jsonObject){
//		pageCount	总共页数	N	10	O	总数量
//		tupleCount	总记录数	N	10	O	总页数
//		publishDate	发布日期	ANS	64	M	发布日期
//		image	大图标题	ANS	64	M	展示图片
//		title	标题	String	64	M	标题
//		description	描述	String	512	M	描述
//		type	发现类型	ANS	4	M	0 门店 1 服务 2 技师 3 图文
//		serviceList	服务列表	JSONArray		M	服务列表
		
		 publishDate = jsonObject.optString("publishDate", "");
		 image = jsonObject.optString("image", "");
		 title = jsonObject.optString("title", "");
		 description = jsonObject.optString("description", "");
		 isFavorite = jsonObject.optString("isFavorite", "");// 是否收藏
		// 收藏类型 0：否，1：是，
			CommonUtil.log("isFavorite:" + isFavorite);
			if (!TextUtils.isEmpty(isFavorite)) {
				if (("0").equals(isFavorite.trim())) {
					isCollect = false;
				} else {
					isCollect = true;
				}
			}

			changeCollectIcon(isCollect);
		ImageLoader.getInstance().displayImage(image, iv_find_icon);
		tv_find_content.setText(title);
		if(!TextUtils.isEmpty(description) && !TextUtils.isEmpty(StringUtil.replaceBlank(description))){
			tv_find_info.setVisibility(View.VISIBLE);
			tv_find_info.setText(description);
		}else {
			tv_find_info.setVisibility(View.GONE);
		}
		tv_name.setText(title);
		if (pageNo == 1) {
			adapter_tech.clear();
		}
		adapter_tech.add(techlist);
	}


	private void collectHandle() {
		if (!isCollect) {
			// 收藏
			addCollect();
		} else {
			// 取消收藏
			cancelCollect();
		}

	}

	/**
	 * 收藏
	 * 
	 * @param skillWorkerID
	 */
	private void addCollect() {
		showCustomCircleProgressDialog(null,
				getString(R.string.common_toast_net_prompt_submit));

//		userID	用户ID
//		itemType	收藏内容类型
//		itemID	内容id
		HashMap<String, String> inJsonMap = new HashMap<String, String>();
		inJsonMap.put("userID", MyApplication.getUserID());
		inJsonMap.put("itemType", "3");
		inJsonMap.put("itemID", ID);
		addCollect = new AddCollect(mContext, handlerAddCollect, inJsonMap);
		Thread thread = new Thread(addCollect);
		thread.start();
	}

	class HandlerAddCollect extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				changeCollectType(true);
				break;
			case MsgId.DOWN_DATA_F:
				closeCustomCircleProgressDialog();
				DialogUtils.showToastMsg(mContext,
						getString(R.string.common_toast_net_down_data_fail),
						Toast.LENGTH_SHORT);
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
	 * 收藏
	 * 
	 * @param skillWorkerID
	 */
	private void cancelCollect() {
		showCustomCircleProgressDialog(null,
				getString(R.string.common_toast_net_prompt_submit));
//
//		userID	用户ID	ANS	64	M	用户ID
//		itemID	项目Id	ANS	11	M	项目Id
//		itemType	类型	ANS	11	M	0 门店 1 服务 2 技师 3 发现
		HashMap<String, String> inJsonMap = new HashMap<String, String>();
		inJsonMap.put("userID", MyApplication.getUserID());
		inJsonMap.put("itemID", ID);
		inJsonMap.put("itemType", "3");// itemType 类型 ANS 11 M 0 门店 1 服务 2 技师 3
										// 发现

		cancelCollect = new CancelCollect(mContext, handlerCancelCollect,
				inJsonMap);
		Thread thread = new Thread(cancelCollect);
		thread.start();
	}

	class HandlerCancelCollect extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				changeCollectType(false);
				break;
			case MsgId.DOWN_DATA_F:
				closeCustomCircleProgressDialog();
				DialogUtils.showToastMsg(mContext,
						getString(R.string.common_toast_net_down_data_fail),
						Toast.LENGTH_SHORT);
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
	 * 改变收藏参数和提示语 状态
	 * 
	 * @param type
	 */
	private void changeCollectType(boolean type) {
		isCollect = type;
		changeCollectIcon(type);
		Custom_Toast_Collect.getInstance().handleCollect(this, type, false);
	}

	/**
	 * 改变收藏按钮样式
	 * 
	 * @param type
	 */
	private void changeCollectIcon(boolean type) {
		Custom_Toast_Collect.getInstance().handleCollectIcon(this, type,
				iv_collect);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_collect:// 收藏
			Intent intent = new Intent();
			if (!MyApplication.getLoginFlag()) {
				// showCustomCircleProgressDialog("请登录", "你尚未登录,请登录!");
				DialogUtils.showToastMsg(mContext, "你尚未登录,请登录!",
						Toast.LENGTH_SHORT);
				intent.setClass(mContext, LoginActivity.class);
				startActivity(intent);
				return;
			}
			UmengEventUtil.discover_detail_collect(mContext);
			collectHandle();
			break;
		case R.id.iv_share://分享
			UmengShare.getInstance().initShareParams(FindDetail_TechListActivity.this, image, title,
					description,ID,Constants.SHARE_FIND);
			break;
		case R.id.rl_loadData_error:// 重新加载数据
			pageNo = 1;
			getTechList();
			break;
		case R.id.ll_back1:// 处理返回键
			handleOnBackPress();
			break;
		}

	}
	
	/**
	 * 处理返回键
	 */
	private void handleOnBackPress(){
		boolean has_open_app = CommonUtil.getBooleanOfSharedPreferences(
				this, "HAS_OPEN_APP", false);
		CommonUtil.logE("是否已经打开app：has_open_app：" + has_open_app);
		// 点击通知后或者首图--app未打开状态 -返回时回到主页面home
		if (push && !has_open_app) {
			CommonUtil.saveBooleanOfSharedPreferences(this, "HAS_OPEN_APP",true);
			JumpTargetActivityUtil.getInstance().jumpToHomeActivity(this, 0);
		} else {
			finish();
		}
	}

	@Override
	public void onBackPressed() {
		handleOnBackPress();
	}


}
