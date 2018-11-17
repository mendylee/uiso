package com.xrk.uiac.bll.exception;

/**
 * 禁止访问方法异常，一般指登录后无权限访问特定功能时的异常
 * MethodNotAllowedException: MethodNotAllowedException.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月8日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class MethodNotAllowedException extends BusinessException
{
    private static final long serialVersionUID = -4683262103935654728L;

	public MethodNotAllowedException(String errCode, String errMsg) {
	    super(HTTP_CODE.METHOD_NOT_ALLOWED, errCode, errMsg);
    }	
}
