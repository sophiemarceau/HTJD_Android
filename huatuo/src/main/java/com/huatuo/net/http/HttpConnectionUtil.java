package com.huatuo.net.http;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.graphics.Bitmap;
import android.os.Handler;

import com.huatuo.base.MyApplication;
import com.huatuo.dictionary.MsgId;
import com.huatuo.util.BitmapUtil;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.FileUtil;
import com.huatuo.util.LogUtil;

/**
 * 
 * @author ftc 网络应用 同步 异步调用HTTP。通过 HttpConnectionCallback 回调函数返回
 * 
 *         调用代码实例： HttpConnectionUtil httputil =new HttpConnectionUtil();
 *         HashMap<String, String> params = new HashMap() ; params("param1",
 *         "var1"); params("param2", "param2");
 *         httputil.syncConnect("http://www.sina.com.cn", msg,
 *         HttpConnectionUtil.HttpMethod.POST, new
 *         HttpConnectionUtil.HttpConnectionCallback() {
 * @Override public void HttpRequest( String retstr) { 巨麦社i(TAG, "geturlrequest"
 *           + retstr ) ; } });
 */
public class HttpConnectionUtil {
	// 链接超时设置
	private static final int TIME_OUT = 1 * 10 * 1000;
	// 请求超时(读超时)设置
	private static final int READ_OUT = 2 * 10 * 1000;

	public static enum HttpMethod {
		GET, POST
	}

	/**
	 * 异步连接 HTTP
	 * 
	 * @param url
	 * @param params
	 * @param method
	 * @param callback
	 */
	public void asyncConnect(final String url, final Map<String, String> params, final HttpMethod method, final HttpConnectionCallback callback) {
		Handler handler = new Handler();
		Runnable runnable = new Runnable() {
			public void run() {
				syncHttpClientConnect(url, params, method, callback);
			}
		};
		handler.post(runnable);
	}

	/**
	 * 异步连接
	 * 
	 * @param url
	 *            网址
	 * @param method
	 *            Http方法,POST跟GET
	 * @param callback
	 *            回调方法,返回给页面或其他的数据
	 */
	public void asyncConnect(final String url, final HttpMethod method, final HttpConnectionCallback callback) {
		asyncConnect(url, null, method, callback);
	}

	// /**
	// *
	// * @param url
	// * @param params
	// * @param method
	// * @param callback
	// */
	// public void syncConnect(final String url, final String jsondata,
	// final HttpMethod method, final HttpConnectionCallback callback) {
	// String json = null;
	// JmsSocketClient client = new JmsSocketClient();
	// try {
	// String result = client.sendmsg(jsondata).trim();
	// if(result!=null && result.length()>0){
	// json = Base64.base642String(result);
	// callback.HttpRequest(json);
	// }else{
	// callback.HttpRequest("#errer");
	// }
	//
	// } catch (IOException e) {
	//
	// // e.printStackTrace();
	// callback.HttpRequest(null);
	// }finally{
	// client =null;
	// }
	// }
	/**
	 * 同步连接HTTP
	 * 
	 * @param url
	 * @param params
	 * @param method
	 * @param httpCallback
	 *            网络回调接口
	 */
	public void syncHttpClientConnect(final String url, final Map<String, String> params, final HttpMethod method, final HttpConnectionCallback httpCallback) {
		BufferedReader reader = null;
		try {
			HttpParams httpParams = new BasicHttpParams();
			// 默认不让重定向,这样就能拿到Location头了
			httpParams.setParameter("http.protocol.handle-redirects", false);
			// // 设置 user agent
			// String userAgent =
			// "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.55 Safari/537.36";
			// HttpProtocolParams.setUserAgent(httpParams, userAgent);
			// 设置连接超时时间和数据读取超时时间
			HttpConnectionParams.setConnectionTimeout(httpParams, TIME_OUT);
			HttpConnectionParams.setSoTimeout(httpParams, READ_OUT);
			// 新建HttpClient对象
			HttpClient httpClient = new DefaultHttpClient(httpParams);
			HttpUriRequest request = getRequest(url, params, method);
			HttpResponse response = httpClient.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			CommonUtil.logE("HTTP---statusCode=" + statusCode);
			if (statusCode == HttpStatus.SC_OK) {
				reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
				StringBuilder sb = new StringBuilder();
				for (String s = reader.readLine(); s != null; s = reader.readLine()) {
					sb.append(s);
				}
				String responseResult = sb.toString();
				
				httpCallback.httpHandler(responseResult);
			} else if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
				Header locationHeader = response.getFirstHeader("Location");
				if (locationHeader != null) {
					String location = locationHeader.getValue();
					LogUtil.e("HTTP", "statusCode=" + statusCode + ",请求重定向location=" + location);
					httpCallback.httpHandler(null);
					return;
				}
			} else {
				LogUtil.e("HTTP", "statusCode=" + statusCode + ",返回状态码异常");
				httpCallback.httpHandler(null);
			}
		}// 连接超时异常
		catch (ConnectTimeoutException e) {
			LogUtil.e("HTTP", "连接超时异常");
			httpCallback.httpHandler(null);
		}// 请求超时异常
		catch (InterruptedIOException e) {
			LogUtil.e("HTTP", "请求超时异常(读超时异常)");
			httpCallback.httpHandler(null);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			LogUtil.e("HTTP", "网络连接未知异常");
			httpCallback.httpHandler(null);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 同步连接HTTP
	 * 
	 * @param url
	 * @param params
	 * @param method
	 * @param httpCallback
	 *            网络回调接口
	 */
	public void syncHttpURLConnection(final String url, final Map<String, String> params, final HttpMethod method, final HttpConnectionCallback httpCallback) {
		BufferedReader reader = null;
		try {

			StringBuffer buffer = new StringBuffer();
			// 遍历bundle
			if (params != null) {
				Set<String> set = params.keySet();
				Iterator<String> it = set.iterator();
				while (it.hasNext()) {
					String key = it.next();
					buffer.append(key).append("=").append(params.get(key));
				}
			}
			// LogUtil.e("deesha", "buffer="+buffer+"---url="+url);
			URL realurl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) realurl.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setDefaultUseCaches(false);
			conn.setRequestMethod("GET");
			conn.setInstanceFollowRedirects(true);
			// 设置Content-type
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.connect();
			int statusCode = conn.getResponseCode();
			// LogUtil.e("HTTP", "statusCode="+statusCode);
			if (statusCode == HttpStatus.SC_OK) {
				reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
				StringBuilder sb = new StringBuilder();

				String fileNameString = FileUtil.getFileSaveDirPath(MyApplication.getAppContext());
				FileWriter writer = new FileWriter(fileNameString + "/test.jpg");

				for (String s = reader.readLine(); s != null; s = reader.readLine()) {
					sb.append(s);
					try {
						writer.write(s);
						writer.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				writer.close();
				String responseResult = sb.toString();

				httpCallback.httpHandler(responseResult);
			} else if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
			} else {
				httpCallback.httpHandler(null);
			}
		}// 连接超时异常
		catch (ConnectTimeoutException e) {
			httpCallback.httpHandler(null);
		}// 请求超时异常
		catch (InterruptedIOException e) {
			httpCallback.httpHandler(null);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			httpCallback.httpHandler(null);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				// ignore me
			}
		}
	}

	/**
	 * 同步连接HTTP
	 * 
	 * @param url
	 * @param params
	 * @param method
	 * @param httpCallback
	 *            网络回调接口
	 */
	public void syncConnectUploadFile(final String url, Map<Integer, Bitmap> fileParas, final HttpMethod method, final HttpConnectionCallback httpCallback) {
		BufferedReader reader = null;
		try {
			HttpParams httpParams = new BasicHttpParams();
			// 默认不让重定向,这样就能拿到Location头了
			httpParams.setParameter("http.protocol.handle-redirects", false);
			// // 设置 user agent
			// String userAgent =
			// "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.55 Safari/537.36";
			// HttpProtocolParams.setUserAgent(httpParams, userAgent);
			// 设置连接超时时间和数据读取超时时间
			HttpConnectionParams.setConnectionTimeout(httpParams, TIME_OUT);
			HttpConnectionParams.setSoTimeout(httpParams, TIME_OUT);
			// 新建HttpClient对象
			HttpClient httpClient = new DefaultHttpClient(httpParams);
			// HttpUriRequest request = getRequest(url, params, method);
			HttpPost httppost = new HttpPost(url);
			// 添加 post的String 和File数据
			MultipartEntity mEntity = new MultipartEntity();
			// 添加文件
			if (fileParas != null && !fileParas.isEmpty()) {
				for (Map.Entry<Integer, Bitmap> item : fileParas.entrySet()) {
					Bitmap bitmap = item.getValue();
					if (bitmap != null) {
						byte[] compresBytes = BitmapUtil.compressImageByQualityReturnByte(bitmap);
						mEntity.addPart("file_" + (item.getKey()), new ByteArrayBody(compresBytes, "temp_" + (item.getKey()) + ".jpg"));
					} else {
					}
				}
			}
			httppost.setEntity(mEntity);

			HttpResponse response = httpClient.execute(httppost);

			int statusCode = response.getStatusLine().getStatusCode();
			// LogUtil.e("HTTP", "statusCode="+statusCode);
			if (statusCode == HttpStatus.SC_OK) {
				reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
				StringBuilder sb = new StringBuilder();
				for (String s = reader.readLine(); s != null; s = reader.readLine()) {
					sb.append(s);
				}
				String responseResult = sb.toString();
				httpCallback.httpHandler(responseResult);
			} else if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
				Header locationHeader = response.getFirstHeader("Location");
				if (locationHeader != null) {
					String location = locationHeader.getValue();
					// LogUtil.e("http", "location="+location);
					String resultString = "{\"code\":" + MsgId.HTTP_TEST_ACCESS_REDIRECT + ",\"message\":\"" + location + "\"}";
					httpCallback.httpHandler(resultString);
					return;
				}
			} else {
				httpCallback.httpHandler(null);
			}
		}// 连接超时异常
		catch (ConnectTimeoutException e) {
			String resultString = "{\"code\":" + MsgId.HTTP_CONNECTTIMEOUTEXCEPTION + ",\"message\":\"连接超时异常\"}";
			httpCallback.httpHandler(resultString);
		}// 请求超时异常
		catch (InterruptedIOException e) {
			String resultString = "{\"code\":" + MsgId.HTTP_INTERRUPTEDIOEXCEPTION + ",\"message\":\"请求超时异常\"}";
			httpCallback.httpHandler(resultString);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			httpCallback.httpHandler(null);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 同步连接HTTP
	 * 
	 * @param url
	 * @param params
	 * @param method
	 * @param httpCallback
	 *            网络回调接口
	 */
	public void syncConnectUploadFile(final String url, ArrayList<String> filePathStringList, final HttpMethod method, final HttpConnectionCallback httpCallback) {
		BufferedReader reader = null;
		try {
			HttpParams httpParams = new BasicHttpParams();
			// 默认不让重定向,这样就能拿到Location头了
			httpParams.setParameter("http.protocol.handle-redirects", false);
			// // 设置 user agent
			// String userAgent =
			// "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.55 Safari/537.36";
			// HttpProtocolParams.setUserAgent(httpParams, userAgent);
			// 设置连接超时时间和数据读取超时时间
			HttpConnectionParams.setConnectionTimeout(httpParams, TIME_OUT);
			HttpConnectionParams.setSoTimeout(httpParams, TIME_OUT);
			// 新建HttpClient对象
			HttpClient httpClient = new DefaultHttpClient(httpParams);
			// HttpUriRequest request = getRequest(url, params, method);
			HttpPost httppost = new HttpPost(url);
			// 添加 post的String 和File数据
			MultipartEntity mEntity = new MultipartEntity();
			int key = 0;
			for (String filePath : filePathStringList) {
				Bitmap bitmap = BitmapUtil.compressImageByScaleReturnBitmap(filePath);
				byte[] compresBytes = BitmapUtil.compressImageByQualityReturnByte(bitmap, filePath);
				mEntity.addPart("file_" + (key++), new ByteArrayBody(compresBytes, "temp_" + (key++) + ".jpg"));
				if (!bitmap.isRecycled()) {
					bitmap.recycle();
					bitmap = null;
				}
				if (compresBytes != null) {
					compresBytes = null;
				}
			}
			httppost.setEntity(mEntity);

			HttpResponse response = httpClient.execute(httppost);

			int statusCode = response.getStatusLine().getStatusCode();
			// LogUtil.e("HTTP", "statusCode="+statusCode);
			if (statusCode == HttpStatus.SC_OK) {
				reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
				StringBuilder sb = new StringBuilder();
				for (String s = reader.readLine(); s != null; s = reader.readLine()) {
					sb.append(s);
				}
				String responseResult = sb.toString();
				httpCallback.httpHandler(responseResult);
			} else if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
				Header locationHeader = response.getFirstHeader("Location");
				if (locationHeader != null) {
					String location = locationHeader.getValue();
					// LogUtil.e("http", "location="+location);
					String resultString = "{\"code\":" + MsgId.HTTP_TEST_ACCESS_REDIRECT + ",\"message\":\"" + location + "\"}";
					httpCallback.httpHandler(resultString);
					return;
				}
			} else {
				// callback.httpRequest("#" +
				// response.getStatusLine().getStatusCode());
				httpCallback.httpHandler(null);
			}
		}// 连接超时异常
		catch (ConnectTimeoutException e) {
			String resultString = "{\"code\":" + MsgId.HTTP_CONNECTTIMEOUTEXCEPTION + ",\"message\":\"连接超时异常\"}";
			httpCallback.httpHandler(resultString);
		}// 请求超时异常
		catch (InterruptedIOException e) {
			String resultString = "{\"code\":" + MsgId.HTTP_INTERRUPTEDIOEXCEPTION + ",\"message\":\"请求超时异常\"}";
			httpCallback.httpHandler(resultString);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			httpCallback.httpHandler(null);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				// ignore me
			}
		}
	}

	/**
	 * 同步连接HTTP
	 * 
	 * @param url
	 * @param params
	 * @param method
	 * @param httpCallback
	 *            网络回调接口
	 */
	public void syncConnectUploadFile(final String url, byte[] bytes, final HttpMethod method, final HttpConnectionCallback httpCallback) {
		BufferedReader reader = null;
		try {
			HttpParams httpParams = new BasicHttpParams();
			// 默认不让重定向,这样就能拿到Location头了
			httpParams.setParameter("http.protocol.handle-redirects", false);
			// // 设置 user agent
			// String userAgent =
			// "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.55 Safari/537.36";
			// HttpProtocolParams.setUserAgent(httpParams, userAgent);
			// 设置连接超时时间和数据读取超时时间
			HttpConnectionParams.setConnectionTimeout(httpParams, TIME_OUT);
			HttpConnectionParams.setSoTimeout(httpParams, TIME_OUT);
			// 新建HttpClient对象
			HttpClient httpClient = new DefaultHttpClient(httpParams);
			// HttpUriRequest request = getRequest(url, params, method);
			HttpPost httppost = new HttpPost(url);
			// 添加 post的String 和File数据
			MultipartEntity mEntity = new MultipartEntity();
			mEntity.addPart("myImage", new ByteArrayBody(bytes, "temp.jpg"));
			httppost.setEntity(mEntity);

			HttpResponse response = httpClient.execute(httppost);

			int statusCode = response.getStatusLine().getStatusCode();
			// LogUtil.e("HTTP", "statusCode="+statusCode);
			if (statusCode == HttpStatus.SC_OK) {
				reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
				StringBuilder sb = new StringBuilder();
				for (String s = reader.readLine(); s != null; s = reader.readLine()) {
					sb.append(s);
				}
				String responseResult = sb.toString();
				httpCallback.httpHandler(responseResult);
			} else if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
				Header locationHeader = response.getFirstHeader("Location");
				if (locationHeader != null) {
					String location = locationHeader.getValue();
					String resultString = "{\"code\":" + MsgId.HTTP_TEST_ACCESS_REDIRECT + ",\"message\":\"" + location + "\"}";
					httpCallback.httpHandler(resultString);
					return;
				}
			} else {
				httpCallback.httpHandler(null);
			}
		}// 连接超时异常
		catch (ConnectTimeoutException e) {
			String resultString = "{\"code\":" + MsgId.HTTP_CONNECTTIMEOUTEXCEPTION + ",\"message\":\"连接超时异常\"}";
			httpCallback.httpHandler(resultString);
		}// 请求超时异常
		catch (InterruptedIOException e) {
			String resultString = "{\"code\":" + MsgId.HTTP_INTERRUPTEDIOEXCEPTION + ",\"message\":\"请求超时异常\"}";
			httpCallback.httpHandler(resultString);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			httpCallback.httpHandler(null);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				// ignore me
			}
		}
	}

	/**
	 * 根据POST GET 返回 HttpUriRequest 对象
	 * 
	 * @param url
	 * @param params
	 * @param method
	 * @return HttpUriRequest
	 */
	private HttpUriRequest getRequest(String url, Map<String, String> params, HttpMethod method) {
		if (method.equals(HttpMethod.POST)) {
			List<NameValuePair> listParams = new ArrayList<NameValuePair>();
			if (params != null) {
				for (String name : params.keySet()) {
					listParams.add(new BasicNameValuePair(name, params.get(name)));
				}
			}
			try {
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(listParams, "utf-8");

				HttpPost request = new HttpPost(url);
				request.setEntity(entity);
				return request;
			} catch (UnsupportedEncodingException e) {
				// Should not come here, ignore me.
				throw new java.lang.RuntimeException(e.getMessage(), e);
			}
		} else {
			if (url.indexOf("?") < 0) {
				url += "?";
			}
			if (params != null) {
				for (String name : params.keySet()) {
					url += "&" + name + "=" + URLEncoder.encode(params.get(name));
				}
			}
			// LogUtil.e("http", "url_get="+url);
			HttpGet request = new HttpGet(url);
			return request;
		}
	}

	public interface HttpConnectionCallback {
		public void httpHandler(String responseStr);
	}
}