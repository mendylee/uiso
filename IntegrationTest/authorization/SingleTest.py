# coding=utf-8
__author__ = 'Administrator'

from common.CONST import CONST
from db.mongo.mongo_process import MongoProcess
from common.Logger import Logger
from debug.Cache import Cache

from authorization.AuthorizationTestBase import AuthorizationTestBase

class SingleTest(AuthorizationTestBase):
    """
    单个用户登录测试
    """
    def __init__(self, svc_url):
        AuthorizationTestBase.__init__(self, svc_url)

    def add(self, mobile, pwd):
        # 注册用户
        req = self.user.register(mobile, pwd, "", "", app_id="2001")
        if not req.ok:
            Logger.error("register user failed!user_code=%s,pwd=%s, msg=%s", mobile, pwd, req.content)
            return

    def updatepwd(self, uid, pwd):
        req = self.password.reset_password(uid, pwd, app_id=2901)
        if not req.ok:
            Logger.error("reset user password failed!user_code=%s,pwd=%s, msg=%s", uid, pwd, req.content)
            return

    def login_test(self, mobile, pwd):
        # 尝试登录
        rtn = self.login(mobile, pwd, app_id=1001)
        if rtn is None:
            Logger.error("login failed!user=%s, pwd=%s", mobile, pwd)
            return
        login_user = rtn
        Logger.info(rtn)

    def run(self, user_code, pwd):
        # 尝试登录
        rtn = self.login(user_code, pwd, app_id=1001)
        if rtn is None:
            Logger.error("login failed!user=%s, pwd=%s", user_code, pwd)
            return
        login_user = rtn

        # 重复登录
        rtn = self.login(user_code, pwd)
        if rtn is None:
            Logger.error("login failed!user=%s, pwd=%s", user_code, pwd)
            return
        if rtn["accessToken"] != login_user["accessToken"] or rtn["refreshToken"] != login_user["refreshToken"]:
            Logger.error("repeat login failed!newUser=%s, oldUser=%s", rtn, login_user)
            return

        # 尝试验证获取到的授权码
        rtn = self.verify_token(login_user["accessToken"])
        if not rtn:
            Logger.error("verify token failed!user=%s", login_user)
            return
        print("verify token:", rtn)

        # 尝试验证查询登录用户信息
        rtn = self.verify_user(login_user["uid"], login_user["accessToken"])
        if rtn is None:
            Logger.error("verify user failed!user=%s", login_user)
            return
        print("verify user:", rtn)

        if len(rtn) != 1:
            Logger.error("query user token failed!expect=%s, actual=%s", 1, len(rtn))
            print("query_user failed!%s" % rtn)
            return

        # 更新授权码
        rtn = self.update(login_user["uid"], login_user["accessToken"], login_user["refreshToken"])
        if rtn is None:
            Logger.error("update token failed!user=%s", login_user)
            return

        old_access_token = login_user["accessToken"]
        old_refresh_token = login_user["refreshToken"]
        login_user = rtn
        new_access_token = login_user["accessToken"]
        new_refresh_token = login_user["refreshToken"]
        if old_access_token == new_access_token or old_refresh_token == new_refresh_token:
            Logger.error("update token failed!token not refresh.")
            print("update_token failed!%s" % (rtn.json()))
            return

        # 验证旧token
        rtn = self.verify_token(old_access_token)
        if rtn:
            Logger.error("verify old token failed!old_token=%s, new_token=%s", old_access_token, new_access_token)
            return
        print("verify old token:", rtn)

        # 验证新token
        rtn = self.verify_token(new_access_token)
        if not rtn:
            Logger.error("verify new token failed!user=%s", login_user)
            return
        print("verify new token:", rtn)

        # 更新用户密码，不会影响当次登录
        new_pwd = "new_pass_word_123456"
        # print(login_user)
        req = self.password.update_password(login_user["uid"], pwd, new_pwd, login_user["accessToken"], login_user["appId"])
        if not req.ok:
            Logger.error("update user password failed!user_code=%s,pwd=%s, msg=%s", user_code, pwd, req.content)
            return

        # 更新用户密码为旧密码
        # print(login_user)
        req = self.password.update_password(login_user["uid"], new_pwd, pwd, login_user["accessToken"], login_user["appId"])
        if not req.ok:
            Logger.error("update user password failed!user_code=%s,pwd=%s, msg=%s", user_code, pwd, req.content)
            return

if __name__ == "__main__":
    db = MongoProcess("uiac", "127.0.0.1", 27017)
    db.clear()
    db.init()

    cache = Cache("http://127.0.0.1:8081")
    cache.clean()

    test = SingleTest(CONST.SERVICE_URL)
    Logger.init()
    test.add("14400000001", "123456")
    test.run("14400000001", "123456")
