package com.huatuo.util;

public class Constants {
	// ("北京")cityCode = "110100"; ("上海")cityCode = "310100";
	public static final String city = "北京";
	public static final String cityCode = "110100";
	public static final String phoneNumber = "4008551512";
	// 0 全部开通  1 开通到店（屏蔽上门）   2 开通上门（屏蔽到店）
	public static final String ALL_OPEN = "0";
	public static final String OPEN_VISIT = "2";
	public static final String OPEN_STORE = "1";

	// 1按评价 2 按等级 3 按性别 4按距离 5按订单量
	// 默认按距离，6按评分

	public static final String BY_APPRAISE = "1";
	public static final String BY_LEVEL = "2";
	public static final String BY_SEX = "3";
	public static final String BY_SEX_MAN = "1";
	public static final String BY_SEX_WOMAN = "2";
	public static final String BY_DISTANCE = "4";
	public static final String BY_ORDERNUM = "5";
	public static final String BY_APPRAISEGRADE = "6";
	// 1 表示升序， -1 表示降序
	public static final int BY_DESC = -1;
	public static final int BY_ASC = 1;

	public static final String IS_FINISH = "IS_FINISH";

	/** 定位时间间隔 */
	public final static int TIME_INTERVAL = 1 * 60 * 1000;
	/** 只定位一次 */
	public final static int TIME_INTERVAL_ONE = -1;

	public final static String APPOINT_VIEW = "APPOINTMENT_VIEW";// 预约界面
	public final static int APPOINT_VIEW_VISIT_WITH_TECH = 1001;// 预约上门 带技师
	public final static int APPOINT_VIEW_VISIT_NO_TECH = 1002;// 预约上门 不带技师
	public final static int APPOINT_VIEW_STORE_WITH_TECH = 1003;// 预约到店 带技师
	public final static int APPOINT_VIEW_STORE_NO_TECH = 1004;// 预约到店 不带技师

	public final static int APPOINT_VIEW_STORE_DEATAIL = 1005;// 店铺详情
	public final static int APPOINT_VIEW_FIST_FRAGMENT = 1006;// 到店列表 首页
	public final static int APPOINT_VIEW_VISIT_FRAGMENT = 1007;// 上门 列表

	public final static int RESPONE_APPOINT_SELECT_ADDRESS = 1001;
	// 传数据 到 预约页面---key
	public final static String APPOINT_TECH = "sendTechnicalObject";// 标记技师页面信息
	public final static String APPOINT_SERVICE = "jsonObject_serDetail";// 标记服务页面信息

	// 预约页面--标记取消支付和支付成功页面 上个支付页面是否是预约页面
	public final static int IS_APPOINT_VIEW = -1;

	public final static String REFRESH_LOCATION_APPLICATION = "com.huatuo.location_application";// 广播刷新定位
	public final static String REFRESH_LOCATION_STARTACTIVITY = "com.huatuo.refresh_location_startactivity";// startactivity广播刷新定位
	public final static String REFRESH_LOCATION_SELECTADDRESS = "com.huatuo.refresh_location_selectaddress";// 选择城市广播刷新定位
	public final static String REFRESH_LOCATION_SEARCH_PERSON = "com.huatuo.refreshlocation_search_personal";// 我的华佗地址管理--广播刷新定位
	public final static String REFRESH_CHANGETAB = "com.huatuo.changeTab";// HOMETAB刷新
	public final static String REFRESH_UNPAIDCOUNT = "com.huatuo.refresh_unpaidCount";// 未支付订单数
	public final static String REFRESH_DELADDRESS = "com.huatuo.deladdress";// 地址列表刷新
	public final static String REFRESH_ORDERLIST = "com.huatuo.orderList";// 订单列表刷新
	public final static String EXIT_APP = "com.huatuo.eixtapp";// HOMETAB刷新
	// 友盟分享
	public static final String DESCRIPTOR = "com.umeng.share";

	private static final String TIPS = "请移步官方网站 ";
	private static final String END_TIPS = ", 查看相关说明.";
	public static final String TENCENT_OPEN_URL = TIPS + "http://wiki.connect.qq.com/android_sdk使用说明" + END_TIPS;
	public static final String PERMISSION_URL = TIPS + "http://wiki.connect.qq.com/openapi权限申请" + END_TIPS;

	public static final String SOCIAL_LINK = "http://www.umeng.com/social";
	public static final String SOCIAL_TITLE = "友盟社会化组件帮助应用快速整合分享功能";
	public static final String SOCIAL_IMAGE = "http://www.umeng.com/images/pic/banner_module_social.png";

	public static final String SOCIAL_CONTENT = "友盟社会化组件（SDK）让移动应用快速整合社交分享功能，我们简化了社交平台的接入，为开发者提供坚实的基础服务：（一）支持各大主流社交平台，"
			+ "（二）支持图片、文字、gif动图、音频、视频；@好友，关注官方微博等功能" + "（三）提供详尽的后台用户社交行为分析。http://www.umeng.com/social";
	// 地图
	public static final String ISEMULATOR = "isemulator";
	public static final String ACTIVITYINDEX = "activityindex";

	// 搜索
	public static final int SEARCH_STORE_PRO = 1;// 到店项目
	public static final int SEARCH_VISIT_PRO = 2;// 上门项目
	public static final int SEARCH_TECH = 3;// 上门技师
	public static final int SEARCH_STORE = 4;// 店铺

	public static final int SEARCH_ADDRESS_SELECTCITY = 5;// 选择城市时搜索区域


	// 预约的服务类型：1 到店， 2上门，3 自营的上门
	public static final int SERVTYPE_STORE = 1;
	public static final int SERVTYPE_STORE_VISIT = 2;
	public static final int SERVTYPE_VISIT = 3;

	// 预约来源：1：店铺；2上门
	public static final int APPOINT_FROM_STORE = 1;
	public static final int APPOINT_FROM_VISIT = 2;

	// 预约下单
	public static final String ISACCOUNT = "1";// 账户余额支付：1
	public static final String NOT_ACCOUNT = "";// 账户余额支付：1
	public static final String PAYCHANNELCODE_ALIPAY = "alipay_sdk";
	public static final String PAYCHANNELCODE_WXPAY = "tenpay_app";
	public static final String PAYCHANNELCODE_NO = "";

	/** 支付方式 */
	public final static int PAY_SELECTED_ACCOUNT = 0;// 账户
	public final static int PAY_SELECTED_ALI = 1;// 支付宝
	public final static int PAY_SELECTED_WX = 2;// 微信
	public final static int PAY_SELECTED_ACCOUNT_ALI = 3;// 账户+支付宝支付
	public final static int PAY_SELECTED_ACCOUNT_WX = 4;// 账户+微信支付

	public static final int FLAG_PAYTYPE_ACCOUNT = 0;// -1:代表账户余额
	public static final int FLAG_PAYTYPE_ALIPAY = 1;// 1：表示支付宝--调用支付宝
	public static final int FLAG_PAYTYPE_WXPAY = 2;
	public static final int FLAG_PAYTYPE_ACCOUNT_ALIPAY = 3;
	public static final int FLAG_PAYTYPE_ACCOUNT_WXPAY = 4;

	// 查看评价列表 类型 0 是门店，1是 项目，2是技师
	public static final String APPRAISE_TYPE_STORE = "0";
	public static final String APPRAISE_TYPE_PROJECT = "1";
	public static final String APPRAISE_TYPE_TECH = "2";

	// 列表类型--0:店铺 1：项目 2：技师 3：发现
	public static final int TYPE_STORE = 0;
	public static final int TYPE_PROJECT = 1;
	public static final int TYPE_TECH = 2;
	public static final int TYPE_FINND = 3;

	// 导航模式 1：公交地铁 2:自驾；3：步行
	public static final int GUIDE_TRANSIT = 1;
	public static final int GUIDE_DRIVING = 2;
	public static final int GUIDE_WALK = 3;
	// 地图缩放级别
	public static final int MAP_ZOOM_LEVEL = 19;

	// 定位发送通知 类型

	public static final int SENDBROACAST_LOCATION_SELECTADDRESS = 1;// SelectAddress_SearchAreaActivity_BD页面
	public static final int SENDBROACAST_LOCATION_PERSONAL = 2;// AddressSearchAreaActivity页面
	public static final int SENDBROACAST_LOCATION_APPLICATION = 3;// application
	public static final int SENDBROACAST_LOCATION_STARTACTIVITY = 4;// startyActivity页面
	// 分页加载
	public static final String PAGE_SIZE_LIST = "15";
	public static final String PAGE_SIZE_GRID = "20";

	// 推送
	public static final String PUSH_OPEN_TYPE = "open_type";
	public static final String PUSH_OPEN_APP = "app";
	public static final String PUSH_OPEN_STORE = "store";// 打开门店页:
	public static final String PUSH_OPEN_SERVICE = "service";// 打开服务页
	public static final String PUSH_OPEN_SKILLWORKER = "skillWorker";// 打开服务页
	public static final String PUSH_OPEN_FIND = "find";// 打开发现页
	public static final String PUSH_OPEN_FIND_TYPE = "type";// 打开发现详情页面类型
	public static final String PUSH_OPEN_ORDER = "order";// 打开订单详情页

	// 优惠券是否使用
	public static final String ISUSE_CUNPON = "isUse";// 打开发现页
	public static final int USE_CUNPON = 0;// 打开发现页
	public static final int CANCEL_CUNPON = 1;// 打开发现页

	// tab订单状态
	public static final String TAB_ORDER_STATUS = "order_status";// 订单状态
	public static final String TAB_ORDER_STATUS_WAITING_PAY = "1";// 待支付
	public static final String TAB_ORDER_STATUS_WAITING_SERVICE = "2";// 带服务
	public static final String TAB_ORDER_STATUS_WAITING_APPRAISE = "3";// 带评价
	public static final String TAB_ORDER_STATUS_ALL = "4";// 全部
	public static final String TAB_ORDER_STATUS_UNCOMPLETE = "5";// 未完成
	public static final String TAB_ORDER_STATUS_COMPLETE = "6";// 已完成

	// 分享
	public static final int SHARE_STORE = 1;
	public static final int SHARE_PROJECT = 2;
	public static final int SHARE_TECH = 3;
	public static final int SHARE_FIND = 4;
	// 友盟统计 后台停留时间
	public static final long INTERVAL = 60 * 1000;

	public static final String NOPAY = "NOPAY";// 非支付页面

	public static final String EXITAPP = "exitApp";// 退出程序

}
