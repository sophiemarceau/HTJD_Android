package com.huatuo.adapter;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.huatuo.R;
import com.huatuo.custom_widget.ImageViewCircle;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.ImageLoader_DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SkillEvaluateListViewAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<JSONObject> arrayList;
	private Context mContext;

	public SkillEvaluateListViewAdapter(Context context) {
		this.mContext = context;
		inflater = LayoutInflater.from(mContext);
		arrayList = new ArrayList<JSONObject>();
	}

	public void add(JSONObject item) {
		arrayList.add(item);
		notifyDataSetChanged();
	}

	public void add(ArrayList<JSONObject> arrayList) {
		if (!CommonUtil.emptyListToString3(arrayList)) {

		for (int i = 0; i < arrayList.size(); i++) {
			JSONObject item = arrayList.get(i);
			this.arrayList.add(item);
		}
		}
		notifyDataSetChanged();
	}

	public void clear() {
		this.arrayList.clear();
	}

	@Override
	public int getCount() {
		return arrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return arrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(
					R.layout.activity_technician_pinglun_listview_item, null);
			holder.icon_nick = (ImageViewCircle) convertView
					.findViewById(R.id.icon_nick);
			holder.tv_nickname = (TextView) convertView
					.findViewById(R.id.tv_nickname);
			holder.tv_remark = (TextView) convertView
					.findViewById(R.id.tv_remark);
			holder.tv_addTime = (TextView) convertView
					.findViewById(R.id.tv_addTime);
			holder.ratingBar = (RatingBar) convertView
					.findViewById(R.id.ratingBar1);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// icon 用户头像 ANS 256 M 用户头像
		// name 评价用户名 ANS 64 M 评价用户名
		// score 分数 N 11 M 分数，满分为5分
		// remark 评价内容 ANS 64 M 评价内容
		JSONObject jsonObject = arrayList.get(position);
		if (null != jsonObject) {
			ImageLoader.getInstance().displayImage(
					jsonObject.optString("icon", ""),
					holder.icon_nick,
					ImageLoader_DisplayImageOptions.getInstance()
							.setDefaultImageTechImg());
			holder.tv_nickname.setText(jsonObject.optString("name", ""));
			holder.tv_addTime.setText(jsonObject.optString("evalTime", ""));
			String remark = jsonObject.optString("remark", "");
			if(!TextUtils.isEmpty(remark)){
			 holder.tv_remark.setText(remark);
			 holder.tv_remark.setVisibility(View.VISIBLE);
			}else {
				holder.tv_remark.setVisibility(View.GONE);
			}
			String score = jsonObject.optString("score", "5");
				
			float score_fl = Float.parseFloat(score);
			holder.ratingBar.setRating(score_fl);

		}

		return convertView;
	}

	static class ViewHolder {
		private TextView tv_nickname, tv_remark, tv_addTime;
		private ImageViewCircle icon_nick;
		private RatingBar ratingBar;
	}
	
	private class MyOpenTask extends AsyncTask<Integer, Integer, Integer> {
        private int[] location = new int[2];

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        public void start() {
            execute(0);
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
//            int linecount = mVideoDescription.getLineCount();
//            android.util.Log.e("MvDetail", "linecount1::"+linecount);
//            if(linecount>3){
//                openTV.setVisibility(View.VISIBLE);
//                openIV.setVisibility(View.VISIBLE);
//                openTV.setText("展开全部");
//                mVideoDescription.setClickable(true);
//                openFlag = false;
//            }else{
//                openTV.setVisibility(View.GONE);
//                openIV.setVisibility(View.GONE);
//                mVideoDescription.setClickable(false);
//            }
        }

    }
}
