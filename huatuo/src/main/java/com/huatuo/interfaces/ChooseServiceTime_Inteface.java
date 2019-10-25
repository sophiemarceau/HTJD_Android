package com.huatuo.interfaces;

public interface ChooseServiceTime_Inteface {
	
	/**
	 * 刷新逾预约界面的服务时间 和时长
	 * @param oldServiceTime ：YYYY-MM—dd:HH:mm
	 * @param newServiceTime: 今天 ：HH：mm
	 */
	public void refreshDataOfAppointView(String oldServiceTime,String newServiceTime);

}
