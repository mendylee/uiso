package com.xrk.uiac.bll.exception;

/**
 * 预处理失败，如：检查请求条件时发现资源冲突等
 * PreconditionFailedException: PreconditionFailedException.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月8日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class PreconditionFailedException extends BusinessException
{
	private static final long serialVersionUID = 1L;

	public PreconditionFailedException(String errCode, String errMsg) {
	    super(HTTP_CODE.PRECONDITION_FAILED, errCode, errMsg);
    }
}
