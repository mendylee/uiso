package com.xrk.uiac.bll.exception;

/**
 * 未授权，一般指未通过授权的请求访问需要授权的资源
 * UnauthorizedException: UnauthorizedException.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月8日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class UnauthorizedException extends BusinessException
{
    private static final long serialVersionUID = -7204749848086852987L;
	public UnauthorizedException(String errCode, String errMsg) {
	    super(HTTP_CODE.UNAUTHORIZED, errCode, errMsg);
    }
}
