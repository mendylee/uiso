package com.xrk.uiac.service.test.handler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.gson.Gson;
import com.xrk.uiac.bll.vo.UserAuthorizationVO;
import com.xrk.uiac.common.utils.http.HTTP;
import com.xrk.uiac.common.utils.http.Request;
import com.xrk.uiac.common.utils.http.Response;
import com.xrk.uiac.common.utils.http.ResponseHandler;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore( {"javax.management.*"}) 
public class PushCallBackTest
{

	@Test
	public void testCallBack(){
		Request request = new Request("http://127.0.0.1:8081/Authorize/accept");
		UserAuthorizationVO authorizationVO = new UserAuthorizationVO();
		authorizationVO.setAppId("123");
		authorizationVO.setUid(222);
		authorizationVO.setAccessToken("34432143214321");
		String message = new Gson().toJson(authorizationVO);
		try 
		{
			byte[] body = message.getBytes("UTF-8");
			request.setBody(body);
			request.addHeader("Content-Length", String.valueOf(body.length));
		} 
		catch (Exception e) 
		{
			throw new RuntimeException(e);
		}
		//开始推送
		HTTP.asyncPOST(request, 10, new ResponseHandler() {
			
			@Override
			public void handle(Response response) throws Exception
			{
				// TODO Auto-generated method stub
				if(response ==null){
					
				}
				if(response.getStatusCode()==200){
					System.out.println("push seccuess");
				}
				
			}
		});
	}
}
