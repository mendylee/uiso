package com.xrk.uiac.bll.test.push;

import com.google.gson.Gson;
import com.xrk.uiac.bll.vo.UserAuthorizationVO;
import com.xrk.uiac.common.utils.http.HTTP;
import com.xrk.uiac.common.utils.http.Request;
import com.xrk.uiac.common.utils.http.Response;
import com.xrk.uiac.common.utils.http.ResponseHandler;

public class PushCallBackUrl
{
	public static void main(String[] args)
    {
		Request request = new Request("http://192.168.9.93:8081/Account/test");
		UserAuthorizationVO authorizationVO = new UserAuthorizationVO();
		authorizationVO.setAppId("123");
		authorizationVO.setUid(222);
		authorizationVO.setAccessToken("34432143214321");
		String message = "param="+new Gson().toJson(authorizationVO);
		System.out.println(message);
		try 
		{
			
			byte[] body = message.getBytes("UTF-8");
			request.setBody(body);
			System.out.println("length:"+body.length);
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
