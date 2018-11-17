package com.xrk.uiac.bll.exception;

/**
 * 如果被请求的资源已删除，返回此异常
 * GoneException: GoneException.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月8日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class GoneException extends BusinessException
{
    private static final long serialVersionUID = 2332783184191233901L;

	public GoneException(String errCode, String errMsg) {
	    super(HTTP_CODE.GONE, errCode, errMsg);
    }	
}
