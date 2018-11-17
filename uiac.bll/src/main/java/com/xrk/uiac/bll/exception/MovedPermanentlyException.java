package com.xrk.uiac.bll.exception;

/**
 * 资源URI已变更，常用于资源标志被修改的情况
 * MovedPermanentlyException: MovedPermanentlyException.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月8日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class MovedPermanentlyException extends AbstractRedirectException
{
    private static final long serialVersionUID = 1L;

	public MovedPermanentlyException(String errCode, String errMsg) {
	    super(HTTP_CODE.MOVED_PERMANENTLY, errCode, errMsg);
    }	
}
