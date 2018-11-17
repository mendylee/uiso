package com.xrk.uiac.bll.exception;

/**
 * 客户端请求的资源不存在
 * NotFoundException: NotFoundException.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月8日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class NotFoundException extends BusinessException
{
    private static final long serialVersionUID = -5205504080631320534L;
	public NotFoundException(String errCode, String errMsg) {
	    super(HTTP_CODE.NOT_FOUND, errCode, errMsg);
    }
}
