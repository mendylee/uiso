package com.xrk.uiac.bll.cache.component;

import com.xrk.uiac.bll.cache.AbstractCache;
import com.xrk.uiac.bll.response.GetSubAccountInfoResponse;
import com.xrk.uiac.common.utils.BeanCopierUtils;

public class UserSubAccountCache extends AbstractCache<GetSubAccountInfoResponse>
{
	@Override
    public GetSubAccountInfoResponse get(Object key)
    {
		GetSubAccountInfoResponse userInfo = super.get(key);
		if (userInfo == null)
		{
			return null;
		}
		GetSubAccountInfoResponse cloneUserInfo = new GetSubAccountInfoResponse();
		BeanCopierUtils.copy(userInfo, cloneUserInfo);
		
	    return cloneUserInfo;
    }

	@Override
    public boolean put(Object key, GetSubAccountInfoResponse userInfo)
    {
		if (userInfo == null)
		{
			return false;
		}
		GetSubAccountInfoResponse cloneUserInfo = new GetSubAccountInfoResponse();
		BeanCopierUtils.copy(userInfo, cloneUserInfo);
		
	    return super.put(key, cloneUserInfo);
    }
}
