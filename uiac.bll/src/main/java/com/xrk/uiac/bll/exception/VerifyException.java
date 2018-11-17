package com.xrk.uiac.bll.exception;

/**
 * 无法处理的请求实体,一般指客户端送入的请求内容校验异常
 * VerifyException: VerifyException.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月8日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class VerifyException extends BusinessException
{
    private static final long serialVersionUID = 6767770230321772372L;
	public VerifyException(String errCode, String errMsg) {
	    super(HTTP_CODE.UNPROCESSABLE_ENTITY, errCode, errMsg);
    }
}
