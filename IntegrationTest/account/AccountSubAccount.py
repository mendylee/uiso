# coding=utf-8
__author__ = 'yxx'

from account.AccountUtils import AccountUtils
from account.User import User
from account.UserSubAccount import UserSubAccount
from authorization.Authorization import Authorization
from db.mongo.mongo_process import MongoProcess
from common.Logger import Logger
from debug.Cache import Cache


class AccountSubAccount:
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
        u_sub = UserSubAccount(base_url)
        a_auth = Authorization(base_url)

        user_code = "13400000001"
        user_code_2 = "13400000002"
        user_code_3 = "13400000003"
        user_code_err = "1340000"
        mobile_1 = "13400000003"
        mobile_2 = "13400000004"
        mobile_err = "1340000"
        password = "88888888"
        password_new = "11111111"
        password_err = ""
        user_info = AccountUtils.get_user_info()
        extend_info = AccountUtils.get_extend_info()
        uid_err = 100
        token_err = "token"
        token_1 = ""
        token_2 = ""
        app_id_err = -1
        app_id_1 = 1001
        sub_app_id_1 = 1002
        sub_app_id_2 = 1003
        sub_account_1 = "sub_account_1"
        sub_account_2 = "sub_account_2"

        # 注册账号
        response = u.register(user_code, password, user_info, extend_info, app_id_1)
        uid_1 = response.json()["uid"]
        response = a_auth.login(user_code, password, app_id_1)
        token_1 = response.json()["accessToken"]

        # 子账号绑定 ************************************************************************
        Logger.info("# 子账号绑定 ************************************************************************")

        # uid不合法
        response = u_sub.bind_sub_account(0, sub_account_1, sub_app_id_1, token_1, app_id_1)
        if response.json()["code"] != "10000023":
            Logger.error("binding sub_account, expected code is 10000023")
            Logger.error(str(response.json()))
        # token失效
        # response = u_sub.bind_sub_account(uid_1, sub_account_1, sub_app_id_1, token_err, app_id_1)
        # if response.json()["code"] != "10000002":
        #     Logger.error("binding sub_account, expected code is 10000002")
        #     Logger.error(str(response.json()))
        # app_id不合法
        response = u_sub.bind_sub_account(uid_1, sub_account_1, sub_app_id_1, token_1, app_id_err)
        if response.json()["code"] != "10000003":
            Logger.error("binding sub_account, expected code is 10000003")
            Logger.error(str(response.json()))
        # sub_app_id不合法
        response = u_sub.bind_sub_account(uid_1, sub_account_1, app_id_err, token_1, app_id_1)
        if response.json()["code"] != "10021303":
            Logger.error("binding sub_account, expected code is 10021303")
            Logger.error(str(response.json()))
        # 绑定成功
        response = u_sub.bind_sub_account(uid_1, sub_account_1, sub_app_id_1, token_1, app_id_1)
        if str(response.json()["result"]) != "True":
            Logger.error("binding sub_account, expected result is \"True\"")
            Logger.error(str(response.json()))
        # 绑定第二个账号
        response = u_sub.bind_sub_account(uid_1, sub_account_2, sub_app_id_2, token_1, app_id_1)
        if str(response.json()["result"]) != "True":
            Logger.error("binding sub_account, expected result is \"True\"")
            Logger.error(str(response.json()))
        # 重复绑定
        response = u_sub.bind_sub_account(uid_1, sub_account_1, sub_app_id_1, token_1, app_id_1)
        if response.json()["code"] != "10021302":
            Logger.error("binding sub_account, expected code is 10021302")
            Logger.error(str(response.json()))

        # 查看子账号列表 ************************************************************************
        Logger.info("# 查看子账号列表 ************************************************************************")

        # uid不合法
        response = u_sub.get_sub_account_list(0, token_1, app_id_1)
        if response.json()["code"] != "10000023":
            Logger.error("get sub_account list, expected code is 10000023")
            Logger.error(str(response.json()))
        # token失效
        # response = u_sub.get_sub_account_list(uid_1, token_err, app_id_1)
        # if response.json()["code"] != "10000002":
        #     Logger.error("get sub_account list, expected code is 10000002")
        #     Logger.error(str(response.json()))
        # app_id不合法
        response = u_sub.get_sub_account_list(uid_1, token_1, app_id_err)
        if response.json()["code"] != "10000003":
            Logger.error("get sub_account list, expected code is 10000003")
            Logger.error(str(response.json()))
        # 查询成功
        response = u_sub.get_sub_account_list(uid_1, token_1, app_id_1)
        if len(response.json()) != 2:
            Logger.error("get sub_account list, expected length is 2")
            Logger.error(str(response.json()))

        # 解绑子账号 ************************************************************************
        Logger.info("# 解绑子账号 ************************************************************************")

        # uid不合法
        response = u_sub.unbind_sub_account(0, sub_app_id_1, token_1, app_id_1)
        if response.json()["code"] != "10000023":
            Logger.error("unbind sub_account, expected code is 10000023")
            Logger.error(str(response.json()))
        # token失效
        # response = u_sub.unbind_sub_account(uid_1, sub_app_id_1, token_err, app_id_1)
        # if response.json()["code"] != "10000002":
        #     Logger.error("unbind sub_account, expected code is 10000002")
        #     Logger.error(str(response.json()))
        # app_id不合法
        response = u_sub.unbind_sub_account(uid_1, sub_app_id_1, token_1, app_id_err)
        if response.json()["code"] != "10000003":
            Logger.error("unbind sub_account, expected code is 10000002")
            Logger.error(str(response.json()))
        # sub_app_id不合法
        response = u_sub.unbind_sub_account(uid_1, app_id_err, token_1, app_id_1)
        if response.json()["code"] != "10021501":
            Logger.error("unbind sub_account, expected code is 10021501")
            Logger.error(str(response.json()))
        # 解绑成功
        response = u_sub.unbind_sub_account(uid_1, sub_app_id_1, token_1, app_id_1)
        if str(response.json()["result"]) != "True":
            Logger.error("unbind sub_account, expected result is \"True")
            Logger.error(str(response.json()))
        # 子账号不存在
        response = u_sub.unbind_sub_account(uid_1, sub_app_id_1, token_1, app_id_1)
        if response.json()["code"] != "10021502":
            Logger.error("unbind sub_account, expected code is 10021502")
            Logger.error(str(response.json()))


if __name__ == "__main__":
    db = MongoProcess("uiac", "127.0.0.1", 27017)
    db.clear()
    db.init()

    cache = Cache("http://127.0.0.1:8081")
    cache.clean()

    sa = AccountSubAccount("http://127.0.0.1:8081")
    sa.run()
