package com.xrk.uiac.bll.push;

/**
 * 
 * Subject: 主题接口
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：zhub<zhubin@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月12日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public interface Subject
{
	/**
	 * 
	 * 订阅观察者
	 *    
	 * @param observer
	 */
	public void regiestObserver(RemoteSubscriber observer);
	/**
	 * 
	 * 取消订阅
	 *    
	 * @param observer
	 */
	public void removeObserver(RemoteSubscriber observer);
	
	
}
