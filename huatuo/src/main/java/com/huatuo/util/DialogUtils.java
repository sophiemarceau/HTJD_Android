package com.huatuo.util;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.widget.Toast;

import com.huatuo.R;
import com.huatuo.custom_widget.ProgressBarDialog;

/**
 * 系统对话框和Toast的提示信息 简便工具类
 * 
 * @author ftc
 */
public class DialogUtils {
	public static ProgressDialog circleProgressDialog, horizontalProgressDialog;
	public static ProgressBarDialog progressBarDialog;
	protected static Toast toast = null;
	private static String oldMsg;
	private static long oneTime = 0;
	private static long twoTime = 0;
	/**
	 * 解析Json对象,显示对话框或者Toast
	 * 
	 * @param context
	 * @param title
	 * @param msg
	 */
	public static void showAlertMsg(final Context mContext, JSONObject jsonObject) {
		if (jsonObject == null) {
			return;
		}
		String type = jsonObject.optString("type", "");

		if (type.equals("group")) {
			// handleTypeIsBack(mContext);
			JSONArray jsonArray = jsonObject.optJSONArray("array");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject childJsonObject = jsonArray.optJSONObject(i);
				String childType = childJsonObject.optString("type", "");
				if (childType.equals("alert")) {
					handleTypeIsAlert(mContext.getApplicationContext(), childJsonObject);
				} else if (childType.equals("back")) {
					handleTypeIsBack(mContext);
				} else if (childType.equals("page")) {
					handleTypeIsPage(mContext, childJsonObject);
				} else if (childType.equals("web")) {
					handleTypeIsWeb(mContext, childJsonObject);
				} else if (type.equals("phone")) {
					handleTypeIsPhone(mContext, childJsonObject);
				}
			}
		} else if (type.equals("alert")) {
			handleTypeIsAlert(mContext, jsonObject);
		} else if (type.equals("back")) {
			handleTypeIsBack(mContext);
		} else if (type.equals("page")) {
			handleTypeIsPage(mContext, jsonObject);
		} else if (type.equals("web")) {
			handleTypeIsWeb(mContext, jsonObject);
		} else if (type.equals("phone")) {
			handleTypeIsPhone(mContext, jsonObject);
		}
	}

	private static void handleTypeIsAlert(final Context mContext, JSONObject jsonObject) {
		try {
			String title = jsonObject.optString("title", "");
			String content = jsonObject.optString("content", "");
			JSONArray buttonsArray = jsonObject.optJSONArray("buttons");
			if (buttonsArray == null) {
				// DialogUtils.showAlertMsg(mContext, title, content);
				DialogUtils.showToastMsg(mContext, content, Toast.LENGTH_LONG);
			} else {
				if (buttonsArray.length() == 1) {
					final JSONObject confirmButtonJsonObject = buttonsArray.getJSONObject(0);
					String confirmTextString = confirmButtonJsonObject.optString("str", "");

					DialogUtils.showAlertMsg(mContext, title, content, confirmTextString, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							JSONObject jsonObject = confirmButtonJsonObject.optJSONObject("msg2");
							if (jsonObject != null) {
								String type = jsonObject.optString("type", "");
								// LogUtil.e("test", "type="+type);
								if (type.equals("back")) {
									handleTypeIsBack(mContext);
								} else if (type.equals("page")) {
									handleTypeIsPage(mContext, jsonObject);
								} else if (type.equals("web")) {
									handleTypeIsWeb(mContext, jsonObject);
								} else if (type.equals("phone")) {
									handleTypeIsPhone(mContext, jsonObject);
								}
							}
						}

					});
				} else {
					final JSONObject confirmButtonJsonObject = buttonsArray.getJSONObject(0);
					String confirmTextString = confirmButtonJsonObject.optString("str", "");

					final JSONObject cancelButtonJsonObject = buttonsArray.getJSONObject(1);
					String cancelTextString = cancelButtonJsonObject.optString("str", "");
					// DialogUtils.showAlertMsg(mContext, "11111111");
					DialogUtils.showAlertMsg(mContext, title, content, confirmTextString, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							JSONObject jsonObject = confirmButtonJsonObject.optJSONObject("msg2");
							if (jsonObject != null) {
								String type = jsonObject.optString("type", "");
								// LogUtil.e("test", "type="+type);
								if (type.equals("back")) {
									handleTypeIsBack(mContext);
								} else if (type.equals("page")) {
									handleTypeIsPage(mContext, jsonObject);
								} else if (type.equals("web")) {
									handleTypeIsWeb(mContext, jsonObject);
								} else if (type.equals("phone")) {
									handleTypeIsPhone(mContext, jsonObject);
								}
							}
						}
					}, cancelTextString, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							JSONObject jsonObject = cancelButtonJsonObject.optJSONObject("msg2");
							if (jsonObject != null) {
								String type = jsonObject.optString("type", "");
								// LogUtil.e("test", "type="+type);
								if (type.equals("back")) {
									handleTypeIsBack(mContext);
								} else if (type.equals("page")) {
									handleTypeIsPage(mContext, jsonObject);
								} else if (type.equals("web")) {
									handleTypeIsWeb(mContext, jsonObject);
								} else if (type.equals("phone")) {
									handleTypeIsPhone(mContext, jsonObject);
								}
							}
						}

					});
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private static void handleTypeIsPage(Context mContext, JSONObject jsonObject) {
		try {
			String classStr = jsonObject.optString("class", "");
			// LogUtil.e("test", "classStr="+classStr);
			String method = jsonObject.optString("method", "");

			Class<?> activityClassName = Class.forName(classStr);

			Intent intent = new Intent();
			intent.setClass(mContext, activityClassName);

			String[] paramsNameArray = method.split(",");
			// Log.e("test", "paramsNameArray.length="+paramsNameArray.length);
			if (paramsNameArray.length != 0) {
				JSONArray paramsValueArray = jsonObject.optJSONArray("params");
				for (int i = 0; i < paramsNameArray.length; i++) {
					String paramsValue = "";
					try {
						paramsValue = paramsValueArray.getString(i);
					} catch (Exception e) {
						// TODO: handle exception
					}

					intent.putExtra(paramsNameArray[i], paramsValue);
				}
			}
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}

	private static void handleTypeIsWeb(Context mContext, JSONObject jsonObject) {
		String urlString = jsonObject.optString("url", "");
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri content_url = Uri.parse(urlString);
		intent.setData(content_url);
		mContext.startActivity(intent);
	}

	private static void handleTypeIsPhone(final Context mContext, JSONObject jsonObject) {
		final String phoneNumber = jsonObject.optString("number", "");
		// Intent phoneIntent = new
		// Intent("android.intent.action.CALL",Uri.parse("tel:" + phoneNumber));
		// mContext.startActivity(phoneIntent);
		showAlertMsg(mContext, mContext.getString(R.string.common_prompt_title), mContext.getString(R.string.viewpager_traffic_bus_line_detail_prompt_call), mContext.getString(R.string.common_confirm), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent phoneIntent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + phoneNumber));
				// 启动
				mContext.startActivity(phoneIntent);
			}
		}, mContext.getString(R.string.common_cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});

	}

	private static void handleTypeIsBack(Context mContext) {
		((Activity) mContext).finish();
	}

	/**
	 * 简单提示框，只有一个确定按钮
	 * 
	 * @param context
	 * @param title
	 * @param msg
	 */
	public static void showAlertMsg(Context context, String title, String msg) {
		showAlertMsg(context, title, msg, context.getString(R.string.common_confirm), null, null, null);
	}

	/**
	 * 简单提示框，只有一个确定按钮,标题为固定字符串 提示
	 * 
	 * @param context
	 * @param title
	 * @param msg
	 */
	public static void showAlertMsg(Context context, String msg) {
		showAlertMsg(context, context.getString(R.string.common_prompt_title), msg, context.getString(R.string.common_confirm), null, null, null);
	}

	/**
	 * 简单提示框，只有一个确定按钮，包含简单按钮的确定事件
	 * 
	 * @param context
	 * @param title
	 * @param msg
	 * @param positiveButton
	 */
	public static void showAlertMsg(Context context, String title, String msg, DialogInterface.OnClickListener positiveButton) {
		showAlertMsg(context, title, msg, context.getString(R.string.common_confirm), positiveButton, null, null);
	}

	/**
	 * 简单提示框，只有一个确定按钮，包含确定按钮的确定事件，和确定按钮文字内容
	 * 
	 * @param context
	 * @param title
	 * @param msg
	 * @param positiveButton
	 */
	public static void showAlertMsg(Context context, String title, String msg, String positiveText, DialogInterface.OnClickListener positiveButton) {
		showAlertMsg(context, title, msg, positiveText, positiveButton, null, null);
	}

	/**
	 * 简单提示框，只有一个确定按钮，包含简单按钮的确定事件，和取消按钮文字内容
	 * 
	 * @param context
	 * @param title
	 * @param msg
	 * @param positiveButton
	 */
	public static void showAlertMsg(Context context, String title, String msg, String positiveText, DialogInterface.OnClickListener positiveButton, String canleText) {
		showAlertMsg(context, title, msg, positiveText, positiveButton, canleText, null);
	}

	/**
	 * 简单提示框，只包含确定按钮以及按钮的事件,按钮不可取消
	 * 
	 * @param context
	 * @param title
	 * @param msg
	 * @param positiveText
	 * @param positiveButton
	 * @param canleText
	 * @param canleButton
	 */
	public static void showAlertMsg(Context context, String title, String msg, String positiveText, DialogInterface.OnClickListener positiveButton, boolean isCancelable) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setCancelable(isCancelable);
		builder.setPositiveButton(positiveText, positiveButton);

		// if(context instanceof Activity)
		// {
		// if(((Activity)context).isFinishing())
		// {
		// return;
		// }
		// }
		builder.show();
	}

	/**
	 * 简单提示框，包含确定按钮以及取消按钮的事件,按钮不可取消
	 * 
	 * @param context
	 * @param title
	 * @param msg
	 * @param positiveText
	 * @param positiveButton
	 * @param canleText
	 * @param canleButton
	 */
	public static void showAlertMsg(Context context, String title, String msg, String positiveText, DialogInterface.OnClickListener positiveButton, String canleText, DialogInterface.OnClickListener canleButton, boolean isCancelable) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setCancelable(isCancelable);
		builder.setPositiveButton(positiveText, positiveButton);
		builder.setNegativeButton(canleText, canleButton);

		if (context instanceof Activity) {
			if (((Activity) context).isFinishing()) {
				return;
			}
		}
		builder.show();
	}

	/**
	 * 简单提示框，包含确定按钮以及取消按钮的事件
	 * 
	 * @param context
	 * @param title
	 * @param msg
	 * @param positiveText
	 * @param positiveButton
	 * @param canleText
	 * @param canleButton
	 */
	public static void showAlertMsg(Context context, String title, String msg, String positiveText, DialogInterface.OnClickListener positiveButton, String canleText, DialogInterface.OnClickListener canleButton) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		// AlertDialog.Builder builder = new AlertDialog.Builder(new
		// ContextThemeWrapper(context, R.style.AlertDialogCustom));
		// AlertDialog.Builder builder = new AlertDialog.Builder(new
		// ContextThemeWrapper(this, R.style.AlertDialogCustom));

		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setCancelable(false);
		builder.setPositiveButton(positiveText, positiveButton);
		builder.setNegativeButton(canleText, canleButton);

		if (context instanceof Activity) {
			if (((Activity) context).isFinishing()) {
				return;
			}
		}
		builder.show();
		// AlertDialog mDialog = builder.create();
		// mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//设定为系统级警告，关键
		// mDialog.show();
	}

	/**
	 * 等待窗口对话框
	 * 
	 * @param context
	 * @param title
	 * @param msg
	 * @return
	 */
	public static ProgressDialog showProgressDialog(Context context, String title, String msg) {
		ProgressDialog dialog = new ProgressDialog(context);
		dialog.setTitle(title);
		dialog.setMessage(msg);
		dialog.setIcon(R.drawable.ic_launcher);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		if (context instanceof Activity) {
			if (((Activity) context).isFinishing()) {
				return null;
			}
		}
		dialog.show();
		return dialog;
	}

	/**
	 * 显示Toast提示信息
	 * 
	 * @param context
	 * @param msg
	 * @param show_time
	 *            : 显示时间。Toast.LENGTH_SHORT：短时间显示;Toast.LENGTH_LONG：长时间显示
	 */
	public static void showToastMsg(Context context, String msg, int show_time) {
		
		int time = 0;
		time = show_time;
		
		if (toast == null) {
			toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			oneTime = System.currentTimeMillis();
		} else {
			twoTime = System.currentTimeMillis();
			if (msg.equals(oldMsg)) {
				if (twoTime - oneTime > time) {
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
			} else {
				oldMsg = msg;
				toast.setText(msg);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
		}
		oneTime = twoTime;
		
	}

	/**
	 * 显示系统滚动的圆圈的进度条
	 * 
	 * @param context
	 * @param msg
	 */
	public static ProgressDialog showCircleProgressDialog(Context context, String title, String msg, boolean isCancelable) {
		circleProgressDialog = new ProgressDialog(context);
		circleProgressDialog.setIndeterminate(false);
		circleProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		circleProgressDialog.setTitle(title);
		circleProgressDialog.setCancelable(isCancelable);
		circleProgressDialog.setMessage(msg);
		try {
			circleProgressDialog.show();
		} catch (Exception e) {
		}

		return circleProgressDialog;
	}

	/** 关闭系统滚动的圆圈的进度条 */
	public static void closeCircleProgressDialog() {
		if (circleProgressDialog != null) {
			try {
				circleProgressDialog.dismiss();
				circleProgressDialog = null;
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 显示系统长形下载进度条
	 * 
	 * @param context
	 * @param title
	 *            : 标题
	 * @param max_number
	 *            : 进度条最大长度
	 */
	public static ProgressDialog showHorizontalProgressDialog(Context context, String title, int max_number) {
		// 创建ProgressDialog对象
		horizontalProgressDialog = new ProgressDialog(context);
		// 设置进度条风格STYLE_HORIZONTAL长形
		horizontalProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		// 设置ProgressDialog 标题
		horizontalProgressDialog.setTitle(title);
		// 设置ProgressDialog 标题图标
		horizontalProgressDialog.setIcon(R.drawable.ic_launcher);
		// 设置ProgressDialog 的进度条是否不明确
		horizontalProgressDialog.setIndeterminate(false);
		// 设置ProgressDialog 是否可以按退回按键取消
		// horizontalProgressDialog.setCancelable(true);
		// 设置ProgressDialog最大值
		horizontalProgressDialog.setMax(max_number);
		try {
			horizontalProgressDialog.show();
		} catch (Exception e) {
		}

		return horizontalProgressDialog;
	}

	/** 关闭系统长形下载进度条 */
	public static void closeHorizontalProgressDialog() {
		if (horizontalProgressDialog != null) {
			try {
				horizontalProgressDialog.dismiss();
				horizontalProgressDialog = null;
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 显示自定义长形下载进度条
	 * 
	 * @param context
	 * @param title
	 *            : 标题
	 * @param max_number
	 *            : 进度条最大长度
	 */
	public static ProgressBarDialog showProgressBarDialog(Context context, String title, boolean isDisplayProgressNumber, long max_number) {
		if (progressBarDialog != null) {
			return progressBarDialog;
		}
		// 创建ProgressDialog对象
		progressBarDialog = new ProgressBarDialog(context);
		// 设置ProgressDialog 标题
		progressBarDialog.setTitle(title);
		// 设置ProgressDialog 标题图标
		progressBarDialog.setIcon(R.drawable.ic_launcher);
		// 设置ProgressDialog 是否可以按退回按键取消
		progressBarDialog.setCancelable(false);
		// 设置ProgressDialog 是否显示已经下载的字节数
		progressBarDialog.setDisplayProgressNumber(isDisplayProgressNumber);
		// 设置ProgressDialog最大值
		progressBarDialog.setDMax(max_number);
		try {
			progressBarDialog.show();
		} catch (Exception e) {
		}

		return progressBarDialog;
	}

	/** 关闭自定义长形下载进度条 */
	public static void closeProgressBarDialog() {
		if (progressBarDialog != null) {
			try {
				progressBarDialog.dismiss();
				progressBarDialog = null;
			} catch (Exception e) {
			}
		}
	}
}