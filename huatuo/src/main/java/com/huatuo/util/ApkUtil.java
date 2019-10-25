package com.huatuo.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

/**
 * <br/>
 * 安装工具类 <br/>
 * PluginUtil.java
 * 
 * @author ftc <br/>
 *         2013-3-5
 * 
 */
public class ApkUtil {
	/**
	 * 检查apk是否安装
	 * 
	 * @param context
	 *            上下文
	 * @param packageName
	 *            apk包名
	 * @return
	 */
	public static boolean checkApkExist(Context context, String packageName) {
		if (packageName == null || "".equals(packageName))
			return false;
		try {
			context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	/**
	 * 从assets资源中获取apk
	 * 
	 * @param context
	 *            上下文
	 * @param srcfileName
	 *            assets中的文件名
	 * @param desFileName
	 *            保存路径
	 * @return
	 */
	public static boolean retrieveApkFromAssets(Context context, String srcfileName, String desFileName) {
		boolean bRet = false;
		InputStream is = null;
		FileOutputStream fos = null;
		// LogUtil.e("apk", "srcfileName="+srcfileName);
		// LogUtil.e("apk", "desFileName="+desFileName);
		File file = new File(desFileName);
		try {
			is = context.getAssets().open(srcfileName);
			fos = new FileOutputStream(file);
			byte[] temp = new byte[1024];
			int i = 0;
			while ((i = is.read(temp)) > 0) {
				fos.write(temp, 0, i);
			}
			bRet = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null)
					fos.close();
				if (is != null)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bRet;
	}

	/**
	 * 
	 * @param  mContext
	 *            上下文
	 * @param url
	 *            下载地址
	 * @param apk_name
	 *            下载包名
	 * @param callback
	 *            下载成功后回调接口
	 * */
	public void down_apk(final Context mContext, final String url, final String apk_name, final ApkDownloadCallback callback) {
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				callback.apkDownloadSucceed(msg.what);
			}
		};
		new Thread() {
			public void run() {
				HttpClient client = getHttpClient(mContext);
				// params[0]代表连接的url
				HttpGet get = new HttpGet(url);
				HttpResponse response;
				FileOutputStream fileOutputStream = null;
				try {
					response = client.execute(get);
					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						HttpEntity entity = response.getEntity();
						// long length = entity.getContentLength();
						InputStream is = entity.getContent();
						fileOutputStream = null;

						if (is != null) {
							File file = new File(FileUtil.getMD5FilePath(mContext, apk_name));

							fileOutputStream = new FileOutputStream(file);
							byte[] buf = new byte[1024];
							int ch = -1;
							while ((ch = is.read(buf)) != -1) {
								fileOutputStream.write(buf, 0, ch);
							}
							fileOutputStream.flush();
							// 下载成功
							handler.sendEmptyMessage(0);
						}
					}// 下载不成功直接返回
					else {
						handler.sendEmptyMessage(1);
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (fileOutputStream != null) {
						try {
							fileOutputStream.close();
						} catch (Exception e) {
						}
					}
				}
			}
		}.start();
	}

	/**
	 * 重载方法
	 * 
	 * @param mContext
	 *            上下文
	 * @param url
	 *            下载地址
	 * @param apk_name
	 *            下载包名
	 * @param handler
	 *            下载成功后处理消息Handler
	 * */
	public void down_apk(final Context mContext, final String url, final String apk_name, final Handler handler) {
		new Thread() {
			public void run() {
				HttpClient client = getHttpClient(mContext);
				// params[0]代表连接的url
				HttpGet get = new HttpGet(url);
				HttpResponse response;
				FileOutputStream fileOutputStream = null;
				InputStream is = null;
				try {
					response = client.execute(get);
					// LogUtil.e("down",
					// "response.getStatusLine().getStatusCode()="+response.getStatusLine().getStatusCode());
					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						HttpEntity entity = response.getEntity();

						long file_net_length = entity.getContentLength();
						// LogUtil.e("down",
						// "long_file_net_length="+file_net_length);
						long downLoadFileSize = 0L;
						is = entity.getContent();
						// 初始化进度条
						Message msg = handler.obtainMessage();
						msg.obj = file_net_length;
						// LogUtil.e("down",
						// "int_file_net_length="+file_net_length);
						msg.what = 9003;
						handler.sendMessage(msg);

						if (is != null) {
							File file = new File(FileUtil.getApkPath(mContext, apk_name));

							fileOutputStream = new FileOutputStream(file);
							byte[] buf = new byte[1024];

							while (true) {
								// 循环读取
								int numread = is.read(buf);

								if (numread == -1) {
									break;
								}

								fileOutputStream.write(buf, 0, numread);

								downLoadFileSize += numread;
								// LogUtil.e("down",
								// "downLoadFileSize="+downLoadFileSize+"--numread="+numread);
								Message msg1 = handler.obtainMessage();
								msg1.obj = downLoadFileSize;
								msg1.what = 9004;
								handler.sendMessage(msg1);// 更新进度条
							}
							fileOutputStream.flush();
							// handler.sendEmptyMessage(9005);
							long file_local_length = file.length();
							// 下载成功
							if (file_local_length == file_net_length) {
								handler.sendEmptyMessage(9005);
								// LogUtil.e("down", "90059005900590059005");
							} else {
								handler.sendEmptyMessage(9006);
							}
						}
					}// 下载不成功直接返回
					else {
						handler.sendEmptyMessage(9006);
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(9006);
				} catch (IOException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(9006);
				} finally {
					if (fileOutputStream != null) {
						try {
							fileOutputStream.close();
						} catch (Exception e) {
						}
					}
					if (is != null) {
						try {
							is.close();
						} catch (Exception e2) {
							// TODO: handle exception
						}
					}
				}
			}
		}.start();
	}

	/**
	 * 安装apk
	 * 
	 * @param context
	 *            上下文
	 * @param filePath
	 *            文件路径
	 */
	public static void installApp(Context context, String filePath) {
		if (filePath == null) {
			return;
		}
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse("file:" + filePath), "application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	public void install_apk(Context mContext, String apk_name) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		// 在android4.0以前的系统中没有什么问题，但是在android4.0及其以后的系统中就不显示安装成功的界面，应该添加一句
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(new File(FileUtil.getApkPath(mContext, apk_name))), "application/vnd.android.package-archive");
		mContext.startActivity(intent);
	}

	public static HttpClient getHttpClient(Context context) {
		// 创建 HttpParams 以用来设置 HTTP 参数(这一部分不是必需的)
		HttpParams httpParams = new BasicHttpParams();

		// 设置连接超时和 Socket超时，以及 Socket 缓存大小
		HttpConnectionParams.setConnectionTimeout(httpParams, 40 * 1000);
		HttpConnectionParams.setSoTimeout(httpParams, 40 * 1000);
		HttpConnectionParams.setSocketBufferSize(httpParams, 8192);

		// 设置重定向，缺省为 false
		HttpClientParams.setRedirecting(httpParams, false);

		// 创建一个 HttpClient 实例
		// 注意 HttpClient httpClient = new HttpClient(); 是Commons HttpClient
		// 中的用法，在 Android 1.5 中我们需要使用 Apache 的缺省实现 DefaultHttpClient
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);

		return httpClient;
	}

	// 下载成功回调接口
	public interface ApkDownloadCallback {
		public void apkDownloadSucceed(int status);
	}
}