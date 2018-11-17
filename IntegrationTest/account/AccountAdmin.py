# coding=utf-8
__author__ = 'yxx'

from account.AccountUtils import AccountUtils
from account.User import User
from account.UserStatus import UserStatus
from authorization.Authorization import Authorization
from db.mongo.mongo_process import MongoProcess
from common.Logger import Logger
from common.CONST import CONST
from debug.Cache import Cache


class AccountAdmin:
    def __init__(self, url):
        self.baseUrl = url

    def run(self):
        # db = MongoProcess("uiac")
        # db.init()
        #
        # Logger.init()

        base_url = self.baseUrl
        cache = Cache(base_url)
        cache.clean()

        u = User(base_url)
        u_status = UserStatus(base_url)
        a_auth = Authorization(base_url)

        user_code = "13400000001"
        user_code_2 = "13400000002"
        user_code_3 = "13400000003"
        password = "88888888"
        user_info = AccountUtils.get_user_info()
        extend_info = AccountUtils.get_extend_info()
        token_err = "token"
        token_1 = ""
        app_id_err = -1
        app_id_1 = 1001

        # 注册三个测试账号， 其中uid_1为有操作权限的管理员账号
        response = u.register(user_code, password, user_info, extend_info)
        uid_1 = response.json()["uid"]

        response = a_auth.login(user_code, password, app_id_1)
        token_1 = response.json()["accessToken"]

        response = u.register(user_code_2, password, user_info, extend_info)
        uid_2 = response.json()["uid"]
        response = u.register(user_code_3, password, user_info, extend_info)
        uid_3 = response.json()["uid"]

        # 禁用账号 ************************************************************************************
        Logger.info("# 禁用账号 ************************************************************************************")

        # uid不合法
        response = u_status.disable_user(0, uid_2, token_1, app_id_1)
        if response.json()["code"] != "10000023":
            Logger.error("disable user, expected code is 10000023")
            Logger.error(str(response.json()))
        # token失效
        # response = u_status.disable_user(uid_1, uid_2, token_err, app_id_1)
        # if response.json()["code"] != "10000002":
        #     Logger.error("disable user, expected code is 10000002")
        #     Logger.error(str(response.json()))
        # app_id不合法
        response = u_status.disable_user(uid_1, uid_2, token_1, app_id_err)
        if response.json()["code"] != "10000003":
            Logger.error("disable user, expected code is 10000003")
            Logger.error(str(response.json()))
        # 禁用成功
        response = u_status.disable_user(uid_1, uid_2, token_1, app_id_1)
        if str(response.json()["result"]) != "True":
            Logger.error("disable user, expected result is \"True\"")
            Logger.error(str(response.json()))
        # 用户被禁用后，不允许登录
        response = a_auth.login(user_code_2, password, app_id_1)
        if response.json()["code"] != "10000005":
            Logger.error("disable user, expected code is 10000005")
            Logger.error(str(response.json()))

        # 查看禁用状态 ************************************************************************************
        Logger.info("# 查看禁用状态 ************************************************************************************")

        # uid不合法
        response = u_status.get_status(0, uid_2, token_1, app_id_1)
        if response.json()["code"] != "10000023":
            Logger.error("get user_status, expected code is 10000023")
            Logger.error(str(response.json()))
        # token失效
        # response = u_status.get_status(uid_1, uid_2, token_err, app_id_1)
        # if response.json()["code"] != "10000002":
        #     Logger.error("get user_status, expected code is 10000002")
        #     Logger.error(str(response.json()))
        # app_id不合法
        response = u_status.get_status(uid_1, uid_2, token_1, app_id_err)
        if response.json()["code"] != "10000003":
            Logger.error("get user_status, expected code is 10000003")
            Logger.error(str(response.json()))
        # 获取成功
        response = u_status.get_status(uid_1, uid_2, token_1, app_id_1)
        if str(response.json()["result"]) != "False":
            Logger.error("get user_status, expected result is \"False\"")
            Logger.error(str(response.json()))
        response = u_status.get_status(uid_1, uid_3, token_1, app_id_1)
        if str(response.json()["result"]) != "True":
            Logger.error("enable user, expected result is \"True\"")
            Logger.error(str(response.json()))

        # 用户解禁 ************************************************************************************
        Logger.info("# 用户解禁 ************************************************************************************")

        # uid不合法
        response = u_status.enable_user(0, uid_2, token_1, app_id_1)
        if response.json()["code"] != "10000023":
            Logger.error("enable user, expected code is 10000023")
            Logger.error(str(response.json()))
        # token失效
        # response = u_status.enable_user(uid_1, uid_2, token_err, app_id_1)
        # if response.json()["code"] != "10000002":
        #     Logger.error("enable user, expected code is 10000002")
        #     Logger.error(str(response.json()))
        # app_id不合法
        response = u_status.enable_user(uid_1, uid_2, token_1, app_id_err)
        if response.json()["code"] != "10000003":
            Logger.error("enable user, expected code is 10000003")
            Logger.error(str(response.json()))
        # 解禁成功
        response = u_status.enable_user(uid_1, uid_2, token_1, app_id_1)
        if str(response.json()["result"]) != "True":
            Logger.error("enable user, expected result is \"True\"")
            Logger.error(str(response.json()))
        # 解禁成功后，能正常登陆
        response = a_auth.login(user_code_2, password, app_id_1)
        if response.json()["uid"] != uid_2:
            Logger.error("enable user, fail to login")
            Logger.error(str(response.json()))


if __name__ == "__main__":
    db = MongoProcess("uiac", "127.0.0.1", 27017)
    db.clear()
    db.init()

    cache = Cache("http://127.0.0.1:8081")
    cache.clean()

    Logger.init()
    admin = AccountAdmin(CONST.SERVICE_URL)
    admin.run()
