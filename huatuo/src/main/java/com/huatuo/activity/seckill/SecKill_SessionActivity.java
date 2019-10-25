package com.huatuo.activity.seckill;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huatuo.R;
import com.huatuo.adapter.SecKill_Session_detail_list_Adapter;
import com.huatuo.adapter.SecKill_Session_detail_list_servInfo_list_Adapter;
import com.huatuo.base.BaseActivity;
import com.huatuo.base.MyApplication;
import com.huatuo.bean.SecKilSpecialBean;
import com.huatuo.bean.SecKillActivityListItemBean;
import com.huatuo.bean.SecKillActivitydescBean;
import com.huatuo.custom_widget.CustomListView;
import com.huatuo.custom_widget.StickyNavLayout;
import com.huatuo.custom_widget.StickyNavLayout.MyOnScrollListener;
import com.huatuo.dictionary.MsgId;
import com.huatuo.dictionary.RequestUrl;
import com.huatuo.net.http.RequestData;
import com.huatuo.net.http.RequestRunnable;
import com.huatuo.net.thread.GetSecKillSessionOrAdvanceNoticeList;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.ImageLoader_DisplayImageOptions;
import com.huatuo.util.JsonUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SecKill_SessionActivity extends BaseActivity implements
		OnClickListener {
	private Context mContext;
	private StickyNavLayout stickynavlayout;
	// 顶部布局Icon和活动介绍
	private ImageView iv_secskill_project_icon;
	private CustomListView lv_secskill_project_top_info;
	private TextView tv_name;
	private RelativeLayout rl_top;
	private Drawable drawable_title, drawable_back;
	private int priceType = 2;// 2：价格升序,3:价格降序
	// 标签和导航条
	private TextView tv_tab_default, tv_tab_saleVolume, tv_tab_price;
	private RelativeLayout tab_default, tab_saleVolume, tab_price;
	private View view_tab_default, view_tab_saleVolume, view_tab_price;
	private ImageView iv_tab_price;
	private ListView mListView;

	// 数据加载前 和 失败页面布局
	private RelativeLayout rl_loadData_error, rl_loadData_empty;
	// 返回键
	private LinearLayout ll_back;

	// 秒杀专项ID
	private String ID = "";
	private boolean isSecKill;// 是否可以秒杀
	private String orderBy = "0";
	private SecKill_Session_detail_list_servInfo_list_Adapter servInfo_list_Adapter;// 服务介绍
	private SecKill_Session_detail_list_Adapter mSecSkill_Session_Adapter;// 秒杀活动列表
	// 秒杀活动
	private ArrayList<SecKillActivityListItemBean> atyList_seckill;
	private RequestRunnable getSecKillSessionOrAdvanceNoticeList;
	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		mContext = this;
		setContentView(R.layout.activity_seckill);
		findViewById();
		setOnClickListener();
		getBundle();
		changeTab(0);// 默认选中第一个标签
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
		getBundle();
		changeTab(0);// 默认选中第一个标签
	}

	/**
	 * 页面接受传值
	 */
	private void getBundle() {
		Bundle bundle = getIntent().getExtras();
		if (null != bundle) {
			ID = bundle.getString("ID");
			isSecKill = bundle.getBoolean("isSecKill");
			// 获取秒杀id
			CommonUtil.log("ID:" + ID);
			CommonUtil.log("isSecKill:" + isSecKill);
		}
	}

	/**
	 * view初始化
	 */
	private void findViewById() {
		// 标题名字
		tv_name = (TextView) findViewById(R.id.tv_name);
		// 头部服务图片和服务介绍
		iv_secskill_project_icon = (ImageView) findViewById(R.id.iv_secskill_project_icon);
		// 设置图片的为4:3
		LayoutParams layoutParams = iv_secskill_project_icon.getLayoutParams();
		layoutParams.width = CommonUtil.WIDTH_SCREEN;
		layoutParams.height = layoutParams.width / 4 * 3;
		iv_secskill_project_icon.setLayoutParams(layoutParams);
		// 服务介绍列表
		lv_secskill_project_top_info = (CustomListView) findViewById(R.id.lv_secskill_project_top_info);
		servInfo_list_Adapter = new SecKill_Session_detail_list_servInfo_list_Adapter(
				this);
		lv_secskill_project_top_info.setAdapter(servInfo_list_Adapter);

		// 返回键
		ll_back = (LinearLayout) findViewById(R.id.ll_back1);
		drawable_back = getResources().getDrawable(R.drawable.icon_bg);
		ll_back.setBackground(drawable_back);
		// title栏
		rl_top = (RelativeLayout) findViewById(R.id.rl_top);
		// 得到背景
		drawable_title = getResources().getDrawable(R.drawable.title_red_bg);
		// 设置alpha
		drawable_title.setAlpha(0);
		// 为title设置背景
		rl_top.setBackground(drawable_title);
		stickynavlayout = (StickyNavLayout) findViewById(R.id.id_stickynavlayout);

		// 初始化标签名
		tv_tab_default = (TextView) findViewById(R.id.tv_tab_default);
		tv_tab_saleVolume = (TextView) findViewById(R.id.tv_tab_saleVolume);
		tv_tab_price = (TextView) findViewById(R.id.tv_tab_price);

		view_tab_default = (View) findViewById(R.id.view_tab_default);
		view_tab_saleVolume = (View) findViewById(R.id.view_tab_saleVolume);
		view_tab_price = (View) findViewById(R.id.view_tab_price);
		tab_default = (RelativeLayout) findViewById(R.id.tab_default);
		tab_saleVolume = (RelativeLayout) findViewById(R.id.tab_saleVolume);
		tab_price = (RelativeLayout) findViewById(R.id.tab_price);
		iv_tab_price = (ImageView) findViewById(R.id.iv_tab_price);

		// 秒杀活动列表
		mListView = (ListView) findViewById(R.id.id_stickynavlayout_listview);
		mSecSkill_Session_Adapter = new SecKill_Session_detail_list_Adapter(
				this);
		mListView.setAdapter(mSecSkill_Session_Adapter);

		// 加载数据失败页面
		rl_loadData_error = (RelativeLayout) findViewById(R.id.rl_loadData_error);
		rl_loadData_error.setOnClickListener(this);
		// 加载数据前的白界面
		rl_loadData_empty = (RelativeLayout) findViewById(R.id.rl_loadData_empty);

	}

	/**
	 * 设置监听事件
	 */
	private void setOnClickListener() {
		// 标签
		tab_default.setOnClickListener(this);
		tab_saleVolume.setOnClickListener(this);
		tab_price.setOnClickListener(this);
		ll_back.setOnClickListener(this);
		// 动态改变title背景色
		changeTitleAlpha();
	}

	/**
	 * title渐变
	 */
	private void changeTitleAlpha() {
		stickynavlayout.setMyOnScrollListener(new MyOnScrollListener() {

			@Override
			public void onScroll() {
				drawable_title.setAlpha((int) ((1 - stickynavlayout
						.getTopShowScale()) * 255));// title背景色
				drawable_back.setAlpha((int) ((stickynavlayout
						.getTopShowScale()) * 255));// 返回键背景色
				tv_name.setAlpha((int) ((1 - stickynavlayout.getTopShowScale()) * 255));// 标题
			}
		});
	}

	/**
	 * 改变标签属性
	 * 
	 * @param potion
	 */
	private void changeTab(int potion) {
		// 初始化标签样式
		tv_tab_default.setTextColor(mContext.getResources()
				.getColor(R.color.c5));
		tv_tab_saleVolume.setTextColor(mContext.getResources().getColor(
				R.color.c5));
		tv_tab_price.setTextColor(mContext.getResources().getColor(R.color.c5));
		view_tab_default.setVisibility(View.GONE);
		view_tab_saleVolume.setVisibility(View.GONE);
		view_tab_price.setVisibility(View.GONE);
		iv_tab_price.setImageResource(R.drawable.icon_screen);

		// 设置选中标签的样式
		switch (potion) {
		case 0:// 默认
			orderBy = "0";
			tv_tab_default.setTextColor(mContext.getResources().getColor(
					R.color.tv_secSkillSession_checked_color));
			view_tab_default.setVisibility(View.VISIBLE);
			break;
		case 1:// 销量
			orderBy = "1";
			tv_tab_saleVolume.setTextColor(mContext.getResources().getColor(
					R.color.tv_secSkillSession_checked_color));
			view_tab_saleVolume.setVisibility(View.VISIBLE);
			break;
		case 2:// 价格
			tv_tab_price.setTextColor(mContext.getResources().getColor(
					R.color.tv_secSkillSession_checked_color));
			view_tab_price.setVisibility(View.VISIBLE);
			if (priceType == 3) {
				// 2：价格升序,
				priceType = 2;
				orderBy = "3";
				iv_tab_price.setImageResource(R.drawable.icon_screenr2);
			} else {
				// 3:价格降序
				priceType = 3;
				orderBy = "2";
				iv_tab_price.setImageResource(R.drawable.icon_screenr1);
			}

			break;
		default:
			break;
		}
		// 获取选中标签对应的内容
		getSecKillSessionOrAdvanceNoticeList();
	}

	/**
	 * 获取专项列表
	 */
	public void getSecKillSessionOrAdvanceNoticeList() {
		if (mHandler == null) {
			mHandler = new Handler_List();
		}
		showCustomCircleProgressDialog(null,
				getString(R.string.common_toast_net_prompt_submit));
		// ID 分类导航ID ANS 64 M 分类导航ID
		HashMap<String, String> inJson = new HashMap<String, String>();
		inJson.put("ID", ID);
		inJson.put("orderBy", orderBy);
		getSecKillSessionOrAdvanceNoticeList = RequestData.getInstance()
				.request(mContext, mHandler, RequestUrl.GET_SPECIALCONTEXT,
						inJson);
	}

	/**
	 * 处理秒杀列表内容
	 */
	class Handler_List extends Handler {
		@Override
		public void handleMessage(Message msg) {
			rl_loadData_empty.setVisibility(View.GONE);
			rl_loadData_error.setVisibility(View.GONE);
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				JSONObject jsonObject = getSecKillSessionOrAdvanceNoticeList
						.getOutJson();
				// CommonUtil.logE("jsonObject:" + jsonObject);
				if (jsonObject != null) {
					handleData(jsonObject);
				}
				break;
			case MsgId.DOWN_DATA_F:
				closeCustomCircleProgressDialog();
				rl_loadData_error.setVisibility(View.VISIBLE);
				// DialogUtils.showToastMsg(mContext, mContext.getResources()
				// .getString(R.string.common_toast_net_down_data_fail),
				// Toast.LENGTH_SHORT);
				break;
			case MsgId.NET_NOT_CONNECT:
				closeCustomCircleProgressDialog();
				rl_loadData_error.setVisibility(View.VISIBLE);
				// setCustomDialog(
				// mContext.getResources().getString(
				// R.string.common_toast_net_not_connect), true);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 处理返回数据
	 * 
	 * @param jsonObject
	 */
	private void handleData(JSONObject jsonObject) {
		// ID 专题ID ANS 64 M 专题ID
		// name 专题名称 ANS 64 M 专题名称
		// icon 专题Icon ANS 256 M 专题图片
		// featureDesc 专题说明 JSONArray 64 C 专题说明
		// 头部图片

		SecKilSpecialBean secKilSpecialBean = JSON.parseObject(
				jsonObject.toString(), SecKilSpecialBean.class);

		ImageLoader.getInstance().displayImage(
				secKilSpecialBean.icon,
				iv_secskill_project_icon,
				ImageLoader_DisplayImageOptions.getInstance()
						.setDefaultImageBigImg());
		// 标题名字
		tv_name.setText(secKilSpecialBean.name);
		// 服务介绍
		ArrayList<SecKillActivitydescBean> servInfoList = new ArrayList<SecKillActivitydescBean>();
		servInfoList = secKilSpecialBean.featureDesc;
		// 服务介绍列表
		lv_secskill_project_top_info.setAdapter(servInfo_list_Adapter);
		servInfo_list_Adapter.clear();
		servInfo_list_Adapter.add(servInfoList);
		// 处理秒杀活动列表
		atyList_seckill = secKilSpecialBean.activityList;
		handleSecKillList();
	}

	/**
	 * 处理秒杀列表数据
	 */
	private void handleSecKillList() {
		// 秒杀活动
		CommonUtil.logE("atyList_seckill:" + atyList_seckill);
		mSecSkill_Session_Adapter.clear();
		mSecSkill_Session_Adapter.add(atyList_seckill, isSecKill);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_back1:// 处理返回键
			super.onBackPressed();
			break;
		case R.id.tab_default:// 按默认排序
			changeTab(0);
			priceType = 2;// //初始化为升序
			break;
		case R.id.tab_saleVolume:// 按销量优先排序
			changeTab(1);
			priceType = 2;// 初始化为升序
			break;
		case R.id.tab_price:// 按价格排序
			changeTab(2);
			break;
		}
	}

}
