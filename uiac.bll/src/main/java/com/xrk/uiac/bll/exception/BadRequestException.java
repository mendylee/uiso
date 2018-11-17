package com.xrk.uiac.bll.exception;

/**
 * 不正确的请求异常
 * BadRequestException: BadRequestException.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月8日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class BadRequestException extends BusinessException
{
    private static final long serialVersionUID = -7076431809994688306L;
	public BadRequestException(String errCode, String errMsg) {
	    super(HTTP_CODE.BAD_REQUEST, errCode, errMsg);
    }	
}
