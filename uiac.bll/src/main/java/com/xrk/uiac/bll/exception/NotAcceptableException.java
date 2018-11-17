package com.xrk.uiac.bll.exception;

/**
 * 服务端不支持客户端的请求返回格式
 * NotAcceptableException: NotAcceptableException.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月8日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class NotAcceptableException extends BusinessException
{
    private static final long serialVersionUID = 5092980261388836570L;

	public NotAcceptableException(String errCode, String errMsg) {
	    super(HTTP_CODE.NOT_ACCEPTABLE, errCode, errMsg);
    }	
}
