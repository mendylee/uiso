package com.xrk.uiac.bll.component;

import com.xrk.uiac.bll.exception.BusinessException;
import com.xrk.uiac.bll.response.PushResponse;

/**
 * 
 * IPushObserverCompont: 授权推送观察者接口组件类
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：zhub<zhubin@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月15日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public interface IPushObserverCompont {

	/**
	 * 
	 * 注册授权信息观察者
	 *    
	 * @param appId			应用ID
	 * @param callBackUrl	回调地址
	 * @return
	 */
	PushResponse regiestObserver(String appId,String callBackUrl) throws BusinessException;
	
	/**
	 * 
	 * 解除授权信息观察者  
	 *    
	 * @param appId		应用ID
	 * @return
	 */
	PushResponse removeObserver(String appId) throws BusinessException;
}
