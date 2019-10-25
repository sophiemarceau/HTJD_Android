package com.huatuo.dictionary;

public class MsgId {
	// 数据下载成功
	public static final int DOWN_DATA_S = 100;
	// 数据下载失败
	public static final int DOWN_DATA_F = 101;
	// 获取验证码成功
	public static final int UPLOAD_ANONYMOUS_INFO_S = 102;
	// 获取验证码失败
	public static final int UPLOAD_ANONYMOUS_INFO_F = 103;
	// 获取验证码成功
	public static final int GET_IDENTIFYING_CODE_S = 201;
	// 获取验证码失败
	public static final int GET_IDENTIFYING_CODE_F = 202;
	// 获取验证码次数过多
		public static final int GET_IDENTIFYING_CODE_MORE = 6090;
	// 校验验证码成功
	public static final int CHECK_IDENTIFYING_CODE_S = 203;
	// 校验验证码失败
	public static final int CHECK_IDENTIFYING_CODE_F = 204;
	// 上传文件成功
	public static final int UPLOAD_FILE_S = 205;
	// 上传文件失败
	public static final int UPLOAD_FILE_F = 206;
	// 注册成功
	public static final int REGISTER_S = 207;
	// 注册失败
	public static final int REGISTER_F = 208;
	// 登录成功
	public static final int LOGIN_S = 209;
	// 登录失败
	public static final int LOGIN_F = 210;
	// 重置密码成功
	public static final int FORGETPASSWORD_S = 211;
	// 重置密码失败
	public static final int FORGETPASSWORD_F = 212;
	// 发表评论成功
	public static final int SUBMIT_COMMENT_S = 213;
	// 发表评论失败
	public static final int SUBMIT_COMMENT_F = 214;
	// 邀请成功
	public static final int INVITEUSERPLAY_S = 215;
	// 邀请失败
	public static final int INVITEUSERPLAY_F = 216;
	// 关注操作(添加/取消)
	public static final int ATTENTION_ADD_CANCEL = 217;
	// 关注成功
	public static final int ADD_ATTENTION_S = 218;
	// 关注失败
	public static final int ADD_ATTENTION_F = 219;
	// 赞成功
	public static final int SUBMIT_BABY_SHOW_LIKE_S = 220;
	// 赞失败
	public static final int SUBMIT_BABY_SHOW_LIKE_F = 221;
	// 取消收藏
	public static final int COLLECT_CANCEL = 222;
	// 收藏成功
	public static final int COLLECT_S = 223;
	// 收藏失败
	public static final int COLLECT_F = 224;
	// 加入主题成功
	public static final int JOIN_HELP_TOPIC = 225;
	// 加入主题成功
	public static final int JOIN_HELP_TOPIC_S = 226;
	// 加入主题失败
	public static final int JOIN_HELP_TOPIC_F = 227;
	// 发表成功
	public static final int PUBLICATION_S = 228;
	// 发表失败
	public static final int PUBLICATION_F = 229;
	// 个人中心 意见反馈成功
	public static final int PERSONAL_FEEDBACK_S = 230;
	// 个人中心 意见反馈失败
	public static final int PERSONAL_FEEDBACK_F = 231;
	// 个人中心 设置成功
	public static final int PERSONAL_SETTING_S = 232;
	// 个人中心 设置失败
	public static final int PERSONAL_SETTING_F = 233;
	// 个人中心 修改密码成功
	public static final int PERSONAL_EDIT_PASSWORD_S = 234;
	// 个人中心 修改密码失败
	public static final int PERSONAL_EDIT_PASSWORD_F = 235;
	// 个人中心 修改用户资料成功
	public static final int PERSONAL_EDIT_USERINFO_S = 236;
	// 个人中心 修改用户资料失败
	public static final int PERSONAL_EDIT_USERINFO_F = 237;
	// 评论下载成功
	public static final int DOWN_COMMENT_S = 238;
	// 评论下载失败
	public static final int DOWN_COMMENT_F = 239;
	// webView 加载超时
	public static final int WEBVIEW_LOAD_TIMEOUT = 8000;

	// client is null
	public static final int CLIENT_IS_NULL = 9995;
	// Context is null
	public static final int RESULT_IS_NULL = 9996;
	// Context is null
	public static final int CONTEXT_IS_NULL = 9997;
	// action is null
	public static final int ACTION_IS_NULL = 9998;
	// Context is null
	public static final String CONTEXT_IS_NULL_STR = "Context is null";
	// action is null
	public static final String ACTION_IS_NULL_STR = "action is null";
	// // 无网络连接
	// public static final int NET_NOT_CONNECT = 9999;//与未知错误9999冲突
	// 无网络连接
	public static final int NET_NOT_CONNECT = -99999;
	// http 请求 地址为空
	public static final int HTTP_URL_IS_NULL = 10000;
	// http 请求 网络其他异常
	public static final int HTTP_OHTER_EXCEPTION = 10001;
	// http 请求超时异常
	public static final int HTTP_INTERRUPTEDIOEXCEPTION = 10002;
	// http 连接超时异常
	public static final int HTTP_CONNECTTIMEOUTEXCEPTION = 10003;
	// http 网络测试连接成功
	public static final int HTTP_TEST_ACCESS_SUCCEED = 10004;
	// http 网络测试连接失败
	public static final int HTTP_TEST_ACCESS_FAIL = 10005;
	// http 网络测试连接重定向
	public static final int HTTP_TEST_ACCESS_REDIRECT = 10006;
	// http 网络连接成功但是返回状态码不是 HttpStatus.SC_OK
	public static final int HTTP_TEST_ACCESS_STATUSCODE_IS_NOT_OK = 10007;

	// http 网络连接成功但是返回状态码 不是提示信息，系统错误提示code大于1000，内部约束提示600到1000，小于600
	// 的提示，可以显示给用户
	public static final int HTTP_APPOINTMENT_ERRORCODE_MIN = 600;
	public static final int HTTP_APPOINTMENT_ERRORCODE_MAX = 1000;
	

}