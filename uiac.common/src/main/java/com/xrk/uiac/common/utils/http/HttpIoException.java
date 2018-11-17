package com.xrk.uiac.common.utils.http;

/**
 * 类：Http IO异常处理
 * 
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015-4-12
 * <br>==========================
 */
public class HttpIoException extends RuntimeException 
{
    private static final long serialVersionUID = 5456613924966363824L;

    public HttpIoException(Exception e) {
        super(e);
    }

}
