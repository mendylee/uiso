package com.xrk.uiac.bll.exception;

/**
 * 不支持客户端请求的媒体格式
 * UnsupportedMediaTypeException: UnsupportedMediaTypeException.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月8日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class UnsupportedMediaTypeException extends BusinessException
{
	private static final long serialVersionUID = 1L;

	public UnsupportedMediaTypeException(String errCode, String errMsg) {
	    super(HTTP_CODE.UNSUPPORTED_MEDIA_TYPE, errCode, errMsg);
    }
}
