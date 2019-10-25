package com.huatuo.util;

import java.util.Map;

import android.app.Activity;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import com.huatuo.R;
import com.huatuo.bean.ShareObj;
import com.huatuo.custom_widget.CustomShareBoard;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMEmoji;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.media.UMusic;

public class UmengShare {
	private Activity mContext;
	private static UmengShare instance;

	private UMImage umImage;
	private String imgUrl = "";
	private String targetUrl = "";
	private String title = "";
	private String content = "";
	private String id = "";
	private ShareObj mShareObj;
	private int type = -100;

	public static UmengShare getInstance() {
		if (instance == null) {
			synchronized (UmengShare.class) {
				if (instance == null) {
					instance = new UmengShare();
				}
			}
		}

		return instance;
	}

	/**
	 * 分享
	 * 
	 * @param context
	 * @param imgUrl
	 *            图片url
	 * @param targetUrl
	 *            目标链接
	 * @param title
	 *            分享title
	 * @param content
	 *            分享内容
	 */
	public ShareObj initShareParams(Activity context, String imgUrl,String title, String content, String id, int type) {
		mContext = context;
		this.imgUrl = imgUrl;
		this.title = title;
		if(TextUtils.isEmpty(content)){
			CommonUtil.log("友盟分享------content为空");
			content = "  ";
		}
		this.content = content;
		this.id = id;
		this.type = type;
		getShareImage(imgUrl);
		initShareUrlByType();
		CommonUtil.log("分享：imgUrl："+imgUrl);
		CommonUtil.log("分享：title："+title);
		CommonUtil.log("分享：content："+content);
		CommonUtil.log("分享：id："+id);
		CommonUtil.log("分享：type："+type);
		CommonUtil.log("分享：targetUrl："+targetUrl);
		
		if(mShareObj == null){
			mShareObj = new ShareObj();
		}
		
		mShareObj.setImage(umImage);
		mShareObj.setTitle(title);
		mShareObj.setContent(content);
		mShareObj.setTargetUrl(targetUrl);
		postShare();
		return mShareObj;
		

	}

	private void initShareUrlByType() {
		switch (type) {
		case Constants.SHARE_STORE:
			targetUrl = mContext.getResources().getString(R.string.share_shop)+id;
			break;
		case Constants.SHARE_PROJECT:
			targetUrl = mContext.getResources().getString(R.string.share_project)+id;
			break;
		case Constants.SHARE_TECH:
			targetUrl = mContext.getResources().getString(R.string.share_tech)+id;
			break;
		case Constants.SHARE_FIND:
			UmengEventUtil.discover_detail_share(mContext);
			targetUrl = mContext.getResources().getString(R.string.share_find)+id;
			break;
		}
		
	}

	
	/**
	 * 图片链接
	 * 
	 * @param imgUrl
	 * @return
	 */
	private void getShareImage(String imgUrl) {
		umImage = new UMImage(mContext, imgUrl);
	}

	/**
	 * 视频分享
	 */
	private void getShareVideo() {
		UMVideo video = new UMVideo(
				"http://v.youku.com/v_show/id_XNTc0ODM4OTM2.html");
		// vedio.setThumb("http://www.umeng.com/images/pic/home/social/img-1.png");
		// video.setThumb(new UMImage(mContext, BitmapFactory.decodeResource(
		// mContext.getResources(), R.drawable.ic_launcher)));
		video.setTitle("友盟社会化组件视频");
		// video.setThumb(imgUrl);
	}

	/**
	 * 表情分享
	 * 
	 * @return
	 */
	private UMEmoji getShareEmoj() {
		UMEmoji emoji = new UMEmoji(mContext,
				"http://www.pc6.com/uploadimages/2010214917283624.gif");
		// UMEmoji emoji = new UMEmoji(mContext,
		// "/storage/sdcard0/emoji.gif");
		return emoji;
	}

	/**
	 * 音乐分享
	 * 
	 * @return
	 */
	private UMusic getShareMusic() {
		UMusic uMusic = new UMusic(
				"http://music.huoxing.com/upload/20130330/1364651263157_1085.mp3");
		uMusic.setAuthor("umeng");
		uMusic.setTitle("天籁之音");
		// uMusic.setThumb(urlImage);
		uMusic.setThumb("http://www.umeng.com/images/pic/social/chart_1.png");

		return uMusic;
	}


	/** auth callback interface**/
    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {

        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText( mContext, "第三方授权失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText(mContext, "第三方授权取消", Toast.LENGTH_SHORT).show();
        }
    };
	/**
	 * 调用postShare分享。跳转至分享编辑页，然后再分享。</br> [注意]<li>
	 * 对于新浪，豆瓣，人人，腾讯微博跳转到分享编辑页，其他平台直接跳转到对应的客户端
	 */
	private void postShare() {
//		CustomShareBoard shareBoard = new CustomShareBoard(mContext, title+"\n"+content+"\n"
//				+ targetUrl);
		if(mShareObj != null){
			CustomShareBoard shareBoard = new CustomShareBoard(mContext, mShareObj);
			shareBoard.showAtLocation(mContext.getWindow().getDecorView(),
				Gravity.BOTTOM, 0, 0);
		}
	}
	
	

}
