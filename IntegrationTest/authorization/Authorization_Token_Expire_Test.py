# coding=utf-8
__author__ = 'scg'

import time

from common.CONST import CONST
from common.Logger import Logger
from db.mongo.mongo_process import MongoProcess
from debug.Cache import Cache

from authorization.AuthorizationTestBase import AuthorizationTestBase

class TokenExpireTest(AuthorizationTestBase):
    """
    测试Token过期信息
    """
    def __init__(self, svc_url):
        AuthorizationTestBase.__init__(self, svc_url)

        # 初始化数据库
        # db = MongoProcess("uiac")
        # db.init()
        # db.set_expire_time(1)

    def test(self):
        appid1 = "1001"
        appid2 = "1002"
        user_code1 = "13311224400"
        user_code2 = "13311225500"
        pwd = "123456"

        # 注册用户
        req = self.user.register(user_code1, pwd, "", "")
        if not req.ok:
            Logger.error("register user failed!user_code=%s,pwd=%s, msg=%s", user_code1, pwd, req.content)
            return

        req = self.user.register(user_code2, pwd, "", "")
        if not req.ok:
            Logger.error("register user failed!user_code=%s,pwd=%s, msg=%s", user_code1, pwd, req.content)
            return

        # 先登录一次
        rtn = self.login(user_code1, pwd, appid1)
        if rtn is None:
            Logger.error("login failed!user=%s, pwd=%s, appId=%s", user_code1, pwd, appid1)
            return
        login_user1 = rtn

        rtn = self.login(user_code2, pwd, appid1)
        if rtn is None:
            Logger.error("login failed!user=%s, pwd=%s, appId=%s", user_code2, pwd, appid1)
            return
        login_user2 = rtn

        # 休眠30秒
        time.sleep(30)
        # 再次登录app2
        rtn = self.login(user_code1, pwd, appid2)
        if rtn is None:
            Logger.error("login failed!user=%s, pwd=%s, appId=%s", user_code1, pwd, appid2)
            return
        login_user1_1 = rtn

        rtn = self.login(user_code2, pwd, appid2)
        if rtn is None:
            Logger.error("login failed!user=%s, pwd=%s, appId=%s", user_code2, pwd, appid2)
            return
        login_user2_1 = rtn

        # 休眠35秒,等待第一次登录过期
        time.sleep(35)

        # 尝试验证查询登录用户信息
        rtn = self.verify_user(login_user1_1["uid"], login_user1_1["accessToken"])
        if rtn is None:
            Logger.error("verify user failed!user=%s", login_user1_1)
            return

        if len(rtn) != 1:
            Logger.error("query user token failed!expect=%s, actual=%s, rtn=%s", 1, len(rtn), rtn)
            return

        # 验证用户的Token应该会失败
        rtn = self.verify_token(login_user1["accessToken"])
        if rtn:
            Logger.error("verify update user1 status token failed!user=%s", login_user1)
            return

        rtn = self.verify_token(login_user2["accessToken"])
        if rtn:
            Logger.error("verify update user2 status token failed!user=%s", login_user2)
            return

        rtn = self.verify_token(login_user1_1["accessToken"])
        if not rtn:
            Logger.error("verify update user1_1 status token failed!user=%s", login_user1_1)
            return

        rtn = self.verify_token(login_user2_1["accessToken"])
        if not rtn:
            Logger.error("verify update user2_1 status token failed!user=%s", login_user2_1)
            return

        # 休眠35秒,等待第二次登录过期
        time.sleep(30)

        # 验证用户的Token应该会失败
        rtn = self.verify_token(login_user1_1["accessToken"])
        if rtn:
            Logger.error("verify update user1_1 status token failed!user=%s", login_user1_1)
            return

        rtn = self.verify_token(login_user2_1["accessToken"])
        if rtn:
            Logger.error("verify update user2_1 status token failed!user=%s", login_user2_1)
            return

    def run(self):
        # 将过期时间设置为1分钟
        db = MongoProcess("uiac", host=CONST.MONGO_HOST)
        db.set_expire_time("1")

        self.recycle()
        self.test()

        db.set_expire_time("20")
        self.recycle()

if __name__ == "__main__":
    db = MongoProcess("uiac", "127.0.0.1", 27017)
    db.clear()
    db.init()

    cache = Cache("http://127.0.0.1:8081")
    cache.clean()

    Logger.init()
    test = TokenExpireTest(CONST.SERVICE_URL)
    test.run()

    if Logger.is_failed():
        print("Test failed!")
    else:
        print("Test success!")

