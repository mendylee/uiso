package com.xrk.uiac.bll.exception;

/**
 * 参看其它链接，一般用于负载均衡上的通知
 * SeeOtherException: SeeOtherException.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月8日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class SeeOtherException extends AbstractRedirectException
{
	private static final long serialVersionUID = 1L;

	public SeeOtherException(String errCode, String errMsg) {
	    super(HTTP_CODE.SEE_OTHER, errCode, errMsg);
    }
}
