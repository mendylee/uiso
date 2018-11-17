package com.xrk.uiac.bll.test.component;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * 消息内容的自定义匹配器，用于异常消息判断
 * MessageMatchesPattern: MessageMatchesPattern.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：shunchiguo<shunchiguo@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月19日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class ExceptionMessageMatches extends TypeSafeMatcher<String>
{
	private String pattern;
    public ExceptionMessageMatches(String pattern) {
        this.pattern = pattern;
    }
    @Override
    protected boolean matchesSafely(String item) {
        return item.matches(pattern);
    }
    @Override
    public void describeTo(Description description) {
        description.appendText("matches pattern ")
            .appendValue(pattern);
    }
    @Override
    protected void describeMismatchSafely(String item, Description mismatchDescription) {
        mismatchDescription.appendText("does not match");
    }
}
