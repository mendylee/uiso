# coding=utf-8
__author__ = 'Administrator'

class Context:
    """
    会话信息内，保存测试过程中的一些公共信息
    """
    def __init__(self):
        self.RegisterUser = {}
        self.LoginUser = {}

    def get_login_user(self, uid):
        """
        获取指定UID的登录用户信息，如果不存在，则返回None
        :param uid:
        :return:
        """
        return self.LoginUser.get(uid)

    def get_login_all_user(self):
        """
        获取所有的登录用户信息
        :return:
        """
        return self.LoginUser.copy().values()

    def add_login_user(self, user):
        """
        添加一个登录用户
        :param user:
        :return:
        """
        self.LoginUser[user["uid"]] = user

    def del_login_user(self, uid):
        """
        删除一个已登录用户
        :param uid:
        :return:
        """
        if self.LoginUser.get(uid) is not None:
            del self.LoginUser[uid]

    def get_register_user(self, uid):
        """
        获取一个指定ID的已注册用户，如果不存在，返回None
        :param uid:
        :return:
        """
        return self.RegisterUser.get(uid)

    def get_register_all_user(self):
        """
        获取所有的已注册用户
        :return:
        """
        return self.RegisterUser.copy().values()

    def add_register_user(self, user):
        """
        添加一个注册用户
        :param user:
        :return:
        """
        self.RegisterUser[user["uid"]] = user

    def del_register_user(self, uid):
        """
        删除一个已注册用户
        :param uid:
        :return:
        """
        if self.RegisterUser.get(uid) is not None:
            del self.RegisterUser[uid]

