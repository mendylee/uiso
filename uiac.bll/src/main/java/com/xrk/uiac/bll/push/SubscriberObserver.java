package com.xrk.uiac.bll.push;

import java.util.List;

/**
 * 
 * SubscriberObserver:  订阅者接口
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：zhub<zhubin@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月12日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public interface SubscriberObserver
{
	public void push(List<String> messages);
}
