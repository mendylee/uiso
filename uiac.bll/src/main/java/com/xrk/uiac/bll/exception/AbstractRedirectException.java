package com.xrk.uiac.bll.exception;

/**
 * 重定向异常的抽像父类
 * AbstractRedirectException: AbstractRedirectException.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月8日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public abstract class AbstractRedirectException extends BusinessException
{
    private static final long serialVersionUID = 1L;

	public AbstractRedirectException(int httpCode, String errCode, String errMsg) {
	    super(httpCode, errCode, errMsg);
    }

}
