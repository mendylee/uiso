package com.xrk.uiac.bll.exception;

/**
 * 禁止访问异常，如：不能访问某些资源的时候
 * ForbiddenException: ForbiddenException.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月8日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class ForbiddenException extends BusinessException
{
    private static final long serialVersionUID = 6500308615913861874L;

	public ForbiddenException(String errCode, String errMsg) {
	    super(HTTP_CODE.FORBIDDEN, errCode, errMsg);
    }	
}
