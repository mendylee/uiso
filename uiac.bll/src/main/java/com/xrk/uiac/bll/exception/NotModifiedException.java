package com.xrk.uiac.bll.exception;

/**
 * 请求的资源未变更，一般用于缓存情况，可以结合Cache-Control,ETag使用
 * NotModifiedException: NotModifiedException.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月8日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class NotModifiedException extends AbstractRedirectException
{
	private static final long serialVersionUID = 1L;

	public NotModifiedException(String errCode, String errMsg) {
	    super(HTTP_CODE.NOT_MODIFIED, errCode, errMsg);
    }
}
