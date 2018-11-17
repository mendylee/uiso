package com.xrk.uiac.bll.exception;
/**
 * 业务异常抽像类
 * BusinessException: BusinessException.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月8日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public abstract class BusinessException extends Exception
{
    private static final long serialVersionUID = -4632539867780934306L;
	private String errCode;
	private int httpCode;
	public BusinessException(int httpCode, String errCode, String errMsg)
	{
		super(errMsg);
		setErrCode(errCode);
		setHttpCode(httpCode);
	}
	public String getErrCode()
    {
	    return errCode;
    }
	public void setErrCode(String errCode)
    {
	    this.errCode = errCode;
    }
	public int getHttpCode()
    {
	    return httpCode;
    }
	public void setHttpCode(int httpCode)
    {
	    this.httpCode = httpCode;
    }
}
