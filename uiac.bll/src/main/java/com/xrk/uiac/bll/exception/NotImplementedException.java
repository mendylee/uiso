package com.xrk.uiac.bll.exception;

/**
 * 未实现异常
 * NotImplementedException: NotImplementedException.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月26日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class NotImplementedException extends BusinessException
{
    private static final long serialVersionUID = 1L;

	public NotImplementedException(String errCode, String errMsg) {
		super(HTTP_CODE.NOT_IMPLEMENTED, errCode, errMsg);
    }

}
