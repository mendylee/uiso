package com.xrk.uiac.service.test.handler.fake;

import com.xrk.uiac.bll.exception.BadRequestException;
import com.xrk.uiac.bll.exception.BusinessException;
import com.xrk.uiac.bll.exception.ConflictException;
import com.xrk.uiac.bll.exception.ExpectionFailedException;
import com.xrk.uiac.bll.exception.ForbiddenException;
import com.xrk.uiac.bll.exception.GoneException;
import com.xrk.uiac.bll.exception.InternalServerException;
import com.xrk.uiac.bll.exception.MethodNotAllowedException;
import com.xrk.uiac.bll.exception.MovedPermanentlyException;
import com.xrk.uiac.bll.exception.NotAcceptableException;
import com.xrk.uiac.bll.exception.NotFoundException;
import com.xrk.uiac.bll.exception.NotImplementedException;
import com.xrk.uiac.bll.exception.NotModifiedException;
import com.xrk.uiac.bll.exception.PreconditionFailedException;
import com.xrk.uiac.bll.exception.SeeOtherException;
import com.xrk.uiac.bll.exception.ServiceUnavailableException;
import com.xrk.uiac.bll.exception.UnauthorizedException;
import com.xrk.uiac.bll.exception.UnsupportedMediaTypeException;
import com.xrk.uiac.bll.exception.VerifyException;
import com.xrk.uiac.service.annotation.HttpMethod;
import com.xrk.uiac.service.annotation.HttpMethod.STATUS_CODE;
import com.xrk.uiac.service.annotation.HttpRouterInfo;
import com.xrk.uiac.service.annotation.HttpMethod.METHOD;
import com.xrk.uiac.service.handler.AbstractHttpWorkerHandler;

/**
 * 模拟测试异常抛出消息的处理
 * FakeExceptionWorkHandler: FakeExceptionWorkHandler.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月26日
 * <br> JDK版本：1.7
 * <br>==========================
 */
@HttpRouterInfo(router = "exception")
public class FakeExceptionWorkHandler extends AbstractHttpWorkerHandler
{
	@HttpMethod(uri="/201", method=METHOD.GET, code=STATUS_CODE.CREATED)
	public void err_201() throws BusinessException
	{
	}
	
	@HttpMethod(uri="/202", method=METHOD.GET, code=STATUS_CODE.ACCEPTED)
	public void err_202() throws BusinessException
	{
		
	}
	
	@HttpMethod(uri="/304", method=METHOD.GET)
	public String err_304() throws BusinessException
	{
		throw new NotModifiedException("1001", "test error");
	}
	
	@HttpMethod(uri="/400", method=METHOD.GET)
	public String err_400() throws BusinessException
	{
		throw new BadRequestException("1001", "test error");
	}
	
	@HttpMethod(uri="/401", method=METHOD.GET)
	public String err_401() throws BusinessException
	{
		throw new UnauthorizedException("1001", "test error");
	}
	
	@HttpMethod(uri="/403", method=METHOD.GET)
	public String err_403() throws BusinessException
	{
		throw new ForbiddenException("1001", "test error");
	}
	
	@HttpMethod(uri="/404", method=METHOD.GET)
	public String err_404() throws BusinessException
	{
		throw new NotFoundException("1001", "test error");
	}
	
	@HttpMethod(uri="/405", method=METHOD.GET)
	public String err_405() throws BusinessException
	{
		throw new MethodNotAllowedException("1001", "test error");
	}
	
	@HttpMethod(uri="/406", method=METHOD.GET)
	public String err_406() throws BusinessException
	{
		throw new NotAcceptableException("1001", "test error");
	}
	
	@HttpMethod(uri="/409", method=METHOD.GET)
	public String err_409() throws BusinessException
	{
		throw new ConflictException("1001", "test error");
	}
	
	@HttpMethod(uri="/410", method=METHOD.GET)
	public String err_410() throws BusinessException
	{
		throw new GoneException("1001", "test error");
	}
	
	@HttpMethod(uri="/412", method=METHOD.GET)
	public String err_412() throws BusinessException
	{
		throw new PreconditionFailedException("1001", "test error");
	}
	
	@HttpMethod(uri="/415", method=METHOD.GET)
	public String err_415() throws BusinessException
	{
		throw new UnsupportedMediaTypeException("1001", "test error");
	}
	
	@HttpMethod(uri="/417", method=METHOD.GET)
	public String err_417() throws BusinessException
	{
		throw new ExpectionFailedException("1001", "test error");
	}
	
	@HttpMethod(uri="/422", method=METHOD.GET)
	public String err_422() throws BusinessException
	{
		throw new VerifyException("1001", "test error");
	}
	
	@HttpMethod(uri="/500", method=METHOD.GET)
	public String err_500() throws BusinessException
	{
		throw new InternalServerException("1001", "test error");
	}
	
	@HttpMethod(uri="/501", method=METHOD.GET)
	public String err_501() throws BusinessException
	{
		throw new NotImplementedException("1001", "test error");
	}
	
	@HttpMethod(uri="/503", method=METHOD.GET)
	public String err_503() throws BusinessException
	{
		throw new ServiceUnavailableException("1001", "test error");
	}
}
