package com.huatuo.util;

import android.text.TextUtils;

public class FormatDistanceUtil {
	public static String formatDistance(String dis) {
		String FormatDistance = "";
		if(!TextUtils.isEmpty(dis)){
			String distance = dis.trim();
			CommonUtil.log("distance:"+distance);
			int  index = distance.indexOf(".");
			CommonUtil.log("index:"+index);
			if(index > 0){
				String intNumber = distance.substring(0, index);
		//		Common.log("---------------截取小数点之前的数字intNumber：" + intNumber);
			
				if (intNumber.equals("0")) {
					String str = NumFormatUtil.saveThreePoint(distance);
					String str2 = str.replace(str.substring(0, str.indexOf(".") + 1),
							"");
					//截取小数点后面两位数字
					if (!str2.equals("000")) {
						FormatDistance = Integer.parseInt(str2.trim()) + "m";
					} else {
						FormatDistance = "0" + "m";
					}
		
				} else {
					FormatDistance = dis+"km";
				}
			}else {
				FormatDistance = dis+"km";
			}
		}
		
		return FormatDistance;
	}
}
