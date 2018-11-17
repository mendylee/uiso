package com.xrk.uiac.bll.exception;

/**
 * 
 * PushExceotion: 推送异常类
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：zhub<zhubin@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月6日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class PushExceotion extends RuntimeException
{
	
    
	/**  
	 * long.   
	 */
    private static final long serialVersionUID = -8037849425742653200L;

	public PushExceotion(String message)
	{
		super(message);
	}

	public PushExceotion(String message, Throwable cause)
	{
		super(message, cause);
	}

}
