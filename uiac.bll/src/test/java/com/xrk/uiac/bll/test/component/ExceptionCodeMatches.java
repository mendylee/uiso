package com.xrk.uiac.bll.test.component;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import com.xrk.uiac.bll.exception.BusinessException;

/**
 * 业务类异常码判断匹配类
 * ExceptionCodeMatches: ExceptionCodeMatches.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月19日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class ExceptionCodeMatches extends TypeSafeMatcher<BusinessException>
{
	 	private String errCode;
	    public ExceptionCodeMatches(String errCode) {
	        this.errCode = errCode;
	    }
	    @Override
	    protected boolean matchesSafely(BusinessException item) {
	        return item.getErrCode() == errCode;
	    }
	    @Override
	    public void describeTo(Description description) {
	        description.appendText(" expects error code ")
	                .appendValue(errCode);
	    }
	    @Override
	    protected void describeMismatchSafely(BusinessException item, Description mismatchDescription) {
	        mismatchDescription.appendText("was ")
	                .appendValue(item.getErrCode());
	    }
}
