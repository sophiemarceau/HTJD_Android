package com.huatuo.dictionary;

public class RequestUrl {
	/**
	 * 查询手机客户端版本信息
	 */
	public static final String GET_VERSIONINFO = "cms/getVersionInfo";
	/**
	 * 客户端首图(用户版)
	 */
	public static final String GET_FIRSTFIGURE = "prop/firstFigure";
	/**
	 * 获取城市列表
	 */
	public static final String GET_CITYLIST = "cms/getCityList";
	/**
	 * 查询分类导航
	 */
	public static final String GET_NAVIGATIONLIST = "navigation/navigationList";

	/**
	 * 查询分类导航内容
	 */
	public static final String GET_NAVCONTENT = "publicorder/getNavContent";
	/**
	 * 查询广告列表
	 */
	public static final String GET_ADLIST = "commercial/adList";
	/**
	 * 查询秒杀时间轴列表
	 */
	public static final String GET_TIMEZONELIST = "spike/queryTimeZoneList";
	/**
	 * 查询秒杀活动列表
	 */
	public static final String GET_TIMEZONECONTEXT = "spike/queryTimeZoneContext";
	/**
	 * 查询秒杀专题内容
	 */
	public static final String GET_SPECIALCONTEXT = "spike/querySpecialContext";

	/**
	 * 查询门店列表
	 */
	public static final String SEARCH_STORELIST = "search/searchStoreList";

	/**
	 * 查询项目列表
	 */
	public static final String SEARCH_SERVICELIST = "search/searchServiceList";

	/**
	 * 查询技师列表
	 */
	public static final String SEARCH_SKILLERLIST = "search/searchSkillerList";

	/**
	 * 查询门店详情
	 */
	public static final String GET_STOREDETAIL = "store/getSysStoreDetail";

	/**
	 * 查询项目详情
	 */
	public static final String GET_SERVICEDETAIL = "serv/getSysServiceDetail";

	/**
	 * 查询技师详情
	 */
	public static final String GET_SKILLDETAILINFO = "skill/getSysSkillDetailInfo";

	/**
	 * 发现列表
	 */
	public static final String GET_DISCOVERLIST = "discover/list";

	/**
	 * 查询发现内容
	 */
	public static final String GET_DISCOVER_ITEM = "discover/item";

	/**
	 * 查询预约时间
	 */
	public static final String GET_SERVICETIME = "publicorder/getServiceTime";
	/**
	 * 查询预约技师
	 */
	public static final String GET_SKILLLISTBYSERVID = "publicorder/storeSkillListByServID";

	/**
	 * 预约下单
	 */
	public static final String GENSERVICEORDER = "publicorder/genServiceOrder";

	/**
	 * 闪付下单
	 */
	public static final String GENQUICKORDER = "quickorder/genQuickOrder";

	/**
	 * 秒杀下单
	 */
	public static final String GENSPIKEORDER = "spikeorder/genSpikeOrder";

	/**
	 * 加钟下单
	 */
	public static final String ADDBELTORDER = "order/addBeltOrder";
	/**
	 * 取消订单
	 */
	public static final String CANCEL_ORDER = "order/cancelOrder";

	/**
	 * 取消秒杀订单
	 */
	public static final String CANCEL_SPIKEORDER = "spikeorder/cancelOrder";

	/**
	 * 删除订单
	 */
	public static final String DELETEORDER = "order/deleteOrder";

	/**
	 * 评价订单
	 */
	public static final String EVALUATE_ORDER = "evaluate/userEvaluate";

	/**
	 * 查询订单数量
	 */
	public static final String GET_ORDERCOUNT = "publicorder/getOrderCount";

	/**
	 * 查询订单列表
	 */
	public static final String GET_USERORDERLIST = "publicorder/userOrderList";

	/**
	 * 查询订单详情
	 */
	public static final String GET_USERORDERDETAIL = "publicorder/userOrderDetail";

	/**
	 * 查询秒杀订单详情
	 */
	public static final String GET_SPIKEORDERDETAIL = "spikeorder/spikeOrderDetail";

	/**
	 * 支付请求
	 */
	public static final String PAY_ORDERPAY = "pay/orderPay";

	/**
	 * 加钟预约
	 */
	public static final String PUBLICORDER_EXTENTION = "publicorder/extention";

	/**
	 * 查询评价列表
	 */
	public static final String GET_EVALUATELIST = "evaluate/evaluateList";

	/**
	 * 登陆
	 */
	public static final String LOGIN = "user/login/new";

	/**
	 * 查询用户信息
	 */
	public static final String get_user_info = "user/info/get";
	/**
	 * 查询账户明细
	 */
	public static final String get_bill = "user/bill/get";

	/**
	 * 查询卡券列表
	 */
	public static final String GET_USERCOUPONLIST = "market/userCouponList";
	/**
	 * 查询消费码
	 */
	public static final String get_ecode = "storeorder/ecode/get";

	/**
	 * 更新地址信息
	 */
	public static final String save_address = "user/address/save";
	/**
	 * 查询地址列表
	 */
	public static final String get_addressList = "user/address/get";

	/**
	 * 查询地址详情
	 */
	public static final String GET_ADDRESS_DETAIL = "user/address/detail";

	/**
	 * 删除地址信息
	 */
	public static final String DEL_ADDRESS = "user/address/del";
	/**
	 * 更新收藏
	 */
	public static final String ADDCOLLECT = "collect/addCollect";
	/**
	 * 删除收藏
	 */
	public static final String CANCELCOLLECT = "collect/cancelCollect";
	/**
	 * 查询收藏列表
	 */
	public static final String GET_COLLECTIONSLIST = "collect/userCollections";

	/**
	 * 用户意见反馈
	 */
	public static final String FEEDBACK_ADD = "user/feedback/add";

	/**
	 * 领取卡券
	 */
	public static final String USERGETCOUPON = "market/userGetCoupon";

	/**
	 * 查询闪付优惠
	 */
	public static final String GETFASTPAYACTIVITY = "activity/getFastPayActivity";

	/**
	 * 添加优惠券
	 */
	public static final String ADDCOUPON = "market/addCoupon";

	/**
	 * 发送短信
	 */
	public static final String SENDSMS = "cms/sendSms";

	/**
	 * 城市热门商圈
	 */
	public static final String CITYAREA_BUSINESS = "cityarea/business";

	/**
	 * 查询评价标签
	 */
	public static final String GETTAG = "tag/getTag";

}
