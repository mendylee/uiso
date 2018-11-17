# coding=utf-8
__author__ = 'scg'

from common.Context import Context
from common.Logger import Logger
from account.User import User
from account.UserPassword import UserPassword
from account.UserStatus import UserStatus
from authorization.Authorization import Authorization
from debug.Cache import Cache

class AuthorizationTestBase:
    def __init__(self, url):
        self.context = Context()
        self.svcUrl = url

        self.user = User(self.svcUrl)
        self.auth = Authorization(self.svcUrl)
        self.password = UserPassword(self.svcUrl)
        self.userStatus = UserStatus(self.svcUrl)

    def verify_token(self, token):
        req = self.auth.query(token)
        Logger.info("code=%s, content=%s", req.status_code, req.content)
        if not req.ok:
            #Logger.error("verify user token failed!token=%s", token)
            print("query failed!%s" % req.json())
            return False
        return True

    def verify_user(self, uid, token):
        req = self.auth.query_user(uid, token)
        Logger.info("code=%s, content=%s", req.status_code, req.json())
        if not req.ok:
            #Logger.error("query user all token failed!uid=%s, token=%s, content=%s", uid, token, req.content)
            print("query_user failed!%s" % req.json())
            return None

        return req.json()

    def login(self, user_code, pwd, app_id=''):
        req = self.auth.login(user_code, pwd, app_id)
        Logger.info("code=%s, content=%s", req.status_code, req.json())
        if not req.ok:
            #Logger.error("user login failed!user_code=%s,pwd=%s, msg=%s", user_code, pwd, req.content)
            print("login failed!%s" % req.json())
            return None
        self.context.add_login_user(req.json())
        return req.json()

    def update(self, uid, token, refresh_token, appId=""):
        req = self.auth.update_token(uid, token, refresh_token, appId)
        Logger.info("code=%s, content=%s", req.status_code, req.json())
        if not req.ok:
            #Logger.error("update token failed!uid=%s, token=%s, refreshToken=%s, content=%s", uid, token, refresh_token, req.content)
            print("update_token failed!%s" % req.json())
            return None
        return req.json()

    def logout(self, uid, token):
        req = self.auth.logout(token)
        Logger.info("code=%s, content=%s", req.status_code, req.json())
        if not req.ok:
            #Logger.error("user logout failed!token=%s, msg=%s", token, req.content)
            print("login failed!%s" % req.json())
            return None
        self.context.del_login_user(uid)
        return req.json()

    def recycle(self):
        users = self.context.get_login_all_user()
        # ִ��ע������
        for user in users:
            self.logout(user['uid'], user['accessToken'])

        # ��շ���˻���
        cache = Cache(self.svcUrl)
        cache.clean()

