package com.xrk.uiac.service.entity;

/**
 * HTTP错误响应实体类，对于所有的错误信息都用此类返回
 * ErrorResponseEntity: ErrorResponseEntity.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月13日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class ErrorResponseEntity
{
	private String code;
	private String message;
	
	public ErrorResponseEntity(String code, String message)
	{
		setCode(code);
		setMessage(message);
	}
	
	public String getCode()
    {
	    return code;
    }
	public void setCode(String code)
    {
	    this.code = code;
    }
	public String getMessage()
    {
	    return message;
    }
	public void setMessage(String message)
    {
	    this.message = message;
    }
}
