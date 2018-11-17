package com.xrk.uiac.bll.cache.component;

import com.xrk.uiac.bll.cache.AbstractCache;
import com.xrk.uiac.bll.entity.VerifyRequestEntity;
import com.xrk.uiac.common.utils.BeanCopierUtils;

public class VerifyRequestCache extends AbstractCache<VerifyRequestEntity>
{
	@Override
    public VerifyRequestEntity get(Object key)
    {
		VerifyRequestEntity vre = super.get(key);
		if (vre == null)
		{
			return null;
		}
		VerifyRequestEntity cloneEntity = new VerifyRequestEntity();
		BeanCopierUtils.copy(vre, cloneEntity);
	    return cloneEntity;
    }

	@Override
    public boolean put(Object key, VerifyRequestEntity verifyRequest)
    {
		if (verifyRequest == null)
		{
			return false;
		}
		
		VerifyRequestEntity cloneEntity = new VerifyRequestEntity();
		BeanCopierUtils.copy(verifyRequest, cloneEntity);
		
	    return super.put(key, cloneEntity);
    }
}
