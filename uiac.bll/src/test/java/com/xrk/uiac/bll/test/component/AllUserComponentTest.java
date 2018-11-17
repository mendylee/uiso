package com.xrk.uiac.bll.test.component;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

//@Ignore
@RunWith(Suite.class)
@Suite.SuiteClasses({
	UserCaptchaComponentTest.class,
	UserComponentTest.class, 
	UserPasswordComponentTest.class,
	UserBindingComponentTest.class,
	UserStatusComponentTest.class,
	UserSubAccountComponentTest.class
})
public class AllUserComponentTest
{
	
}
