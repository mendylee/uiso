# coding=utf-8
__author__ = 'scg'

from common.CONST import CONST
from common.Logger import Logger
from db.mongo.mongo_process import MongoProcess
from debug.Cache import Cache

from authorization.AuthorizationTestBase import AuthorizationTestBase

class MultiUserTest(AuthorizationTestBase):
    """
    多个用户登录测试
    """
    def __init__(self, svc_url):
        AuthorizationTestBase.__init__(self, svc_url)
        # 初始化数据库
        # db = MongoProcess("uiac")
        # db.init()

    def test(self):
        appid1 = "1001"
        appid2 = "1002"
        user_code = 13311224000
        user_code_end = 13311225000
        pwd = "123456"

        # 注册用户
        print("register user call!")
        for num in range(user_code, user_code_end):
            req = self.user.register(str(num), pwd, "", "")
            if not req.ok:
                Logger.error("register user failed!user_code=%s,pwd=%s, msg=%s", user_code, pwd, req.content)
                return

        # 多用户登录
        print("test user login call!")
        login_user = []
        uids = []
        for num in range(user_code, user_code_end):
            rtn = self.login(str(num), pwd, appid1)
            if rtn is None:
                Logger.error("login failed!user=%s, pwd=%s, appId=%s", num, pwd, appid1)
                return
            login_user.append(rtn)

            rtn = self.login(str(num), pwd, appid2)
            if rtn is None:
                Logger.error("login failed!user=%s, pwd=%s, appId=%s", num, pwd, appid2)
                return
            login_user.append(rtn)

            uids.append(rtn)

        # 验证多用户的授权码是否有效
        print("test access token call!")
        for login_obj in login_user:
            rtn = self.verify_token(login_obj["accessToken"])
            if not rtn:
                Logger.error("verify new token failed!user=%s", login_obj)
                return

        # 验证登录数量
        print("test user login number call!")
        for login_obj in uids:
            rtn = self.verify_user(login_obj["uid"], login_obj["accessToken"])
            if rtn is None:
                Logger.error("verify user failed!user=%s", login_obj)
                return

            if len(rtn) != 2:
                Logger.error("query user token failed!expect=%s, actual=%s", 2, len(rtn))
                return

        # 注销
        print("test user logout call!")
        for login_obj in login_user:
            rtn = self.logout(login_obj["uid"], login_obj["accessToken"])
            if rtn is None:
                Logger.error("logout failed!user=%s", login_obj)

    def run(self):
        self.test()
        self.recycle()

if __name__ == "__main__":
    db = MongoProcess("uiac", "127.0.0.1", 27017)
    db.clear()
    db.init()

    cache = Cache("http://127.0.0.1:8081")
    cache.clean()

    Logger.init()
    test = MultiUserTest(CONST.SERVICE_URL)
    test.run()

    if Logger.is_failed():
        print("Test failed!")
    else:
        print("Test success!")