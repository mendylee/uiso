package com.xrk.uiac.bll.entity;

import java.util.List;

import com.xrk.uiac.dal.entity.UserExtendInfo;

/**
 * 
 * 用户扩展信息缓存实体
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：yexx<yexiaoxiao@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月27日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class UserExtendInfoEntity
{
	//临时实现方案
	private List<UserExtendInfo> list;

	public List<UserExtendInfo> getList()
	{
		return list;
	}

	public void setList(List<UserExtendInfo> list)
	{
		this.list = list;
	}
}
