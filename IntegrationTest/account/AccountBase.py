# coding=utf-8
__author__ = 'yxx'

import json

from account.AccountUtils import AccountUtils
from account.UserBinding import UserBinding
from account.UserCaptcha import UserCaptcha
from account.UserPassword import UserPassword
from account.User import User
from authorization.Authorization import Authorization
from db.mongo.mongo_process import MongoProcess
from common.Logger import Logger
from debug.Cache import Cache


class AccountBase:
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
        u_bind = UserBinding(base_url)
        u_captcha = UserCaptcha(base_url)
        u_password = UserPassword(base_url)
        a_auth = Authorization(base_url)

        user_code = "15400000001"
        user_code_2 = "15400000002"
        user_code_3 = "15400000003"
        user_code_err = "1540000"
        mobile_1 = "15400000003"
        mobile_2 = "15400000004"
        mobile_err = "1340000"
        qq_err = "384328d"
        postcode_err = "3432"
        sex_err = "5"
        email_err = "yexiaoxiao.xiangrikui.fuck"
        password = "88888888"
        password_new = "11111111"
        password_err = ""
        user_info = AccountUtils.get_user_info()
        user_info_err = "\"userName\":\"wrongName"
        user_info_up = AccountUtils.get_up_user_info()
        extend_info = AccountUtils.get_extend_info()
        extend_info_err = "\"a\":\"av"
        extend_info_up = AccountUtils.get_up_extend_info()
        uid_1 = 0
        uid_err = 100
        token_err = "token"
        token_1 = ""
        token_2 = ""
        app_id_err = -1
        app_id_1 = 1001
        app_id_2 = 1002
        app_id_3 = 3

        # 注册用户 ************************************************************************************
        Logger.info("# 注册用户 ************************************************************************************")

        # 用户编码出错
        response = u.register(user_code_err, password, user_info, extend_info)
        if response.json()["code"] != "10020400":
            Logger.error("create user, expected code is 10020400")
            Logger.error(str(response.json()))
        # 密码不合法
        response = u.register(user_code, password_err, user_info, extend_info)
        if response.json()["code"] != "10020401":
            Logger.error("create user, expected code is 10020401")
            Logger.error(str(response.json()))
        # 用户基本资料不合法
        response = u.register(user_code, password, user_info_err, extend_info)
        if response.json()["code"] != "10020402":
            Logger.error("create user, expected code is 10020402")
            Logger.error(str(response.json()))
        # 用户扩展资料不合法
        response = u.register(user_code, password, user_info, extend_info_err)
        if response.json()["code"] != "10020403":
            Logger.error("create user, expected code is 10020403")
            Logger.error(str(response.json()))
        # app_id不合法
        response = u.register(user_code, password, user_info, extend_info, app_id_err)
        if response.json()["code"] != "10000003":
            Logger.error("create user, expected code is 10000003")
            Logger.error(str(response.json()))
        # 个人基本信息手机号不合法
        response = u.register(user_code, password, "{\"mobile\":" + mobile_err + "}", extend_info)
        if response.json()["code"] != "10020405":
            Logger.error("create user, expected code is 10020405")
            Logger.error(str(response.json()))
        # 个人信息性别参数不合法
        response = u.register(user_code, password, "{\"sex\":" + sex_err + "}", extend_info)
        if response.json()["code"] != "10020404":
            Logger.error("create user, expected code is 10020404")
            Logger.error(str(response.json()))
        # 个人信息qq参数不合法
        response = u.register(user_code, password, "{\"qq\":" + qq_err + "}", extend_info)
        if response.json()["code"] != "10020407":
            Logger.error("create user, expected code is 10020407")
            Logger.error(str(response.json()))
        # 个人信息邮箱参数不合法
        response = u.register(user_code, password, "{\"email\":" + email_err + "}", extend_info)
        if response.json()["code"] != "10020406":
            Logger.error("create user, expected code is 10020406")
            Logger.error(str(response.json()))
        # 个人信息邮编参数不合法
        response = u.register(user_code, password, "{\"postcode\":" + postcode_err + "}", extend_info)
        if response.json()["code"] != "10020408":
            Logger.error("create user, expected code is 10020408")
            Logger.error(str(response.json()))
        # 正确注册
        response = u.register(user_code, password, user_info, extend_info)
        if response.json()["userCode"] != user_code:
            Logger.error("create user, expected result is success")
            Logger.error(str(response.json()))
        uid_1 = response.json()["uid"]

        response = u.register(user_code_2, password, user_info, extend_info, app_id_2)
        uid_2 = response.json()["uid"]
        # 用户已存在
        response = u.register(user_code, password, user_info, extend_info)
        if response.json()["code"] != "10020499":
            Logger.error("create user, expected code is 10020499")
            Logger.error(str(response.json()))

        # 登录 ************************************************************************************
        Logger.info("# 登录 ************************************************************************************")

        # 未找到用户
        response = a_auth.login(user_code_3, password)
        if response.json()["code"] != "10000005":
            Logger.error("create user and login, expected code is 10000005")
            Logger.error(str(response.json()))
        # 密码错误
        response = a_auth.login(user_code, password_new)
        if response.json()["code"] != "10010101":
            Logger.error("create user and login, expected code is 10010101")
            Logger.error(str(response.json()))
        # 登录成功
        response = a_auth.login(user_code, password, app_id_1)
        token_1 = response.json()["accessToken"]
        response = a_auth.login(user_code_2, password, app_id_2)
        token_2 = response.json()["accessToken"]

        # 查看用户信息 ************************************************************************************
        Logger.info("# 查看用户信息 ************************************************************************************")

        # uid无效
        response = u.get_user_info(0, token_1)
        if response.json()["code"] != "10000023":
            Logger.error("get user info, expected code is 10000023")
            Logger.error(str(response.json()))
        # token失效
        # response = u.get_user_info(uid_1, token_err)
        # if response.json()["code"] != "10000002":
        #     Logger.error("get user info, expected code is 10000002")
        #     Logger.error(str(response.json()))
        # 查看成功
        response = u.get_user_info(uid_1, token_1)
        if response.json()["uid"] != uid_1:
            Logger.error("get user info error")
            Logger.error(str(response.json()))

        # 更新用户信息 ************************************************************************************
        Logger.info("# 更新用户信息 ************************************************************************************")

        # uid无效
        response = u.update_user_info(0, user_info, extend_info, token_1)
        if response.json()["code"] != "10000023":
            Logger.error("update user info, expected code is 10000023")
            Logger.error(str(response.json()))
        # token失效
        # response = u.update_user_info(uid_1, user_info, extend_info, token_err)
        # if response.json()["code"] != "10000002":
        #     Logger.error("update user info, expected code is 10000002")
        #     Logger.error(str(response.json()))
        # app_id不合法
        response = u.update_user_info(uid_1, user_info, extend_info, token_1, app_id_err)
        if response.json()["code"] != "10000003":
            Logger.error("update user info, expected code is 10000003")
            Logger.error(str(response.json()))
        # 用户基本信息格式不合法
        response = u.update_user_info(uid_1, user_info_err, extend_info, token_1)
        if response.json()["code"] != "10020601":
            Logger.error("update user info, expected code is 10020601")
            Logger.error(str(response.json()))
        # 用户扩展信息不合法
        response = u.update_user_info(uid_1, user_info, extend_info_err, token_1)
        if response.json()["code"] != "10020602":
            Logger.error("update user info, expected code is 10020602")
            Logger.error(str(response.json()))
        # 用户信息手机参数不合法
        response = u.update_user_info(uid_1, "{\"mobile\":" + mobile_err + "}", extend_info_up, token_1)
        if response.json()["code"] != "10020605":
            Logger.error("update user info, expected code is 10020605")
            Logger.error(str(response.json()))
        # 用户信息性别参数不合法
        response = u.update_user_info(uid_1, "{\"sex\":" + sex_err + "}", extend_info_up, token_1)
        if response.json()["code"] != "10020604":
            Logger.error("update user info, expected code is 10020605")
            Logger.error(str(response.json()))
        # 用户信息qq参数不合法
        response = u.update_user_info(uid_1, "{\"qq\":" + qq_err + "}", extend_info_up, token_1)
        if response.json()["code"] != "10020607":
            Logger.error("update user info, expected code is 10020607")
            Logger.error(str(response.json()))
        # 用户信息邮箱参数不合法
        response = u.update_user_info(uid_1, "{\"email\":" + email_err + "}", extend_info_up, token_1)
        if response.json()["code"] != "10020606":
            Logger.error("update user info, expected code is 10020606")
            Logger.error(str(response.json()))
        # 用户信息邮编参数不合法
        response = u.update_user_info(uid_1, "{\"postcode\":" + postcode_err + "}", extend_info_up, token_1)
        if response.json()["code"] != "10020608":
            Logger.error("update user info, expected code is 10020608")
            Logger.error(str(response.json()))
        # 更新成功
        response = u.update_user_info(uid_1, user_info_up, extend_info_up, token_1)
        if str(response.json()["result"]) != "True":
            Logger.error("update user info, expected result is \"True\"")
            Logger.error(str(response.json()))
        response = u.get_user_info(uid_1, token_1)
        if response.json()["extendInfo"]["a"] != "newAv":
            Logger.error("update user info, fail to update")
            Logger.error(str(response.json()))

        # 修改用户密码 ************************************************************************************
        Logger.info("# 修改用户密码 ************************************************************************************")

        # uid无效
        response = u_password.update_password(0, password, password_new, token_1)
        if response.json()["code"] != "10000023":
            Logger.error("update user password, expected code is 10000023")
            Logger.error(str(response.json()))
        # token失效
        # response = u_password.update_password(uid_1, password, password_new, token_err)
        # if response.json()["code"] != "10000002":
        #     Logger.error("update user password, expected code is 10000002")
        #     Logger.error(str(response.json()))
        # app_id不合法
        response = u_password.update_password(uid_1, password, password_new, token_1, app_id_err)
        if response.json()["code"] != "10000003":
            Logger.error("update user password, expected code is 10000003")
            Logger.error(str(response.json()))
        # 密码格式错误
        response = u_password.update_password(uid_1, "", password_new, token_1)
        if response.json()["code"] != "10020701":
            Logger.error("update user password, expected code is 10020701")
            Logger.error(str(response.json()))
        # 原始密码错误
        response = u_password.update_password(uid_1, password_new, password_new, token_1)
        if response.json()["code"] != "10020702":
            Logger.error("update user password, expected code is 10020702")
            Logger.error(str(response.json()))
        # 修改成功
        response = u_password.update_password(uid_1, password, password_new, token_1)
        if str(response.json()["result"]) != "True":
            Logger.error("update user password, expected result is \"True\"")
            Logger.error(str(response.json()))
        # 密码修改成功，token不失效
        response = u.get_user_info(uid_1, token_1)
        if response.json()["uid"] != uid_1:
            Logger.error("update user password, but token is invalid")
            Logger.error(str(response.json()))

        # 退出登录 ************************************************************************************
        Logger.info("# 退出登录 ************************************************************************************")
        a_auth.logout(token_1)
        # 退出登录后不能进行用户操作
        # response = u.get_user_info(uid_1, token_1)
        # if response.json()["code"] != "10000002":
        #     Logger.error("logout but token is valid")
        #     Logger.error(str(response.json()))

        # 重置用户密码 ************************************************************************************
        Logger.info("# 重置用户密码 ************************************************************************************")

        # uid无效
        response = u_password.reset_password(0, password_new)
        if response.json()["code"] != "10000023":
            Logger.error("reset user password, expected code is 10000023")
            Logger.error(str(response.json()))
        # 用户不存在
        response = u_password.reset_password(uid_err, password_new)
        if response.json()["code"] != "10000005":
            Logger.error("reset user password, expected code is 10000005")
            Logger.error(str(response.json()))
        # 密码格式错误
        response = u_password.reset_password(uid_1, "")
        if response.json()["code"] != "10020801":
            Logger.error("reset user password, expected code is 10020801")
            Logger.error(str(response.json()))
        # app_id不合法
        response = u_password.reset_password(uid_1, password_new, app_id_err)
        if response.json()["code"] != "10000003":
            Logger.error("reset user password, expected code is 10000003")
            Logger.error(str(response.json()))
        # 修改成功
        response = u_password.reset_password(uid_1, password_new)
        if str(response.json()["result"]) != "True":
            Logger.error("reset user password, expected result is \"True\"")
            Logger.error(str(response.json()))

        # 使用新密码登录 ************************************************************************************
        Logger.info("# 使用新密码登录 ************************************************************************************")
        response = a_auth.login(user_code, password_new)
        token_1 = response.json()["accessToken"]
        # 确认登录成功，能进行基本操作
        response = u.get_user_info(uid_1, token_1)
        if response.json()["uid"] != uid_1:
            Logger.error("fail to login with new password")
            Logger.error(str(response.json()))

        # 绑定手机 ************************************************************************************
        Logger.info("# 绑定手机 ************************************************************************************")

        # uid无效
        response = u_bind.bind_mobile(0, mobile_1, token_1, app_id_1)
        if response.json()["code"] != "10000023":
            Logger.error("bind mobile, expected code is 10000023")
            Logger.error(str(response.json()))
        # 手机号码格式错误
        response = u_bind.bind_mobile(uid_1, mobile_err, token_1, app_id_1)
        if response.json()["code"] != "10000024":
            Logger.error("bind mobile, expected code is 10000024")
            Logger.error(str(response.json()))
        # token失效
        # response = u_bind.bind_mobile(uid_1, mobile_1, token_err, app_id_1)
        # if response.json()["code"] != "10000002":
        #     Logger.error("bind mobile, expected code is 10000002")
        #     Logger.error(str(response.json()))
        # app_id不合法
        response = u_bind.bind_mobile(uid_1, mobile_1, token_1, app_id_err)
        if response.json()["code"] != "10000003":
            Logger.error("bind mobile, expected code is 10000003")
            Logger.error(str(response.json()))
        # 绑定成功
        response = u_bind.bind_mobile(uid_1, mobile_2, token_1, app_id_1)
        if str(response.json()["result"]) != "True":
            Logger.error("bind mobile, expected result is \"True\"")
            Logger.error(str(response.json()))
        # 重复操作
        response = u_bind.bind_mobile(uid_1, mobile_2, token_1, app_id_1)
        if response.json()["code"] != "10021103":
            Logger.error("bind mobile, expected code is 10021103")
            Logger.error(str(response.json()))
        # 手机号已被占用
        response = u_bind.bind_mobile(uid_2, mobile_2, token_2, app_id_2)
        if response.json()["code"] != "10021102":
            Logger.error("bind mobile, expected code is 10021102")
            Logger.error(str(response.json()))

        # 查看手机绑定状态 ************************************************************************************
        Logger.info("# 查看手机绑定状态 ************************************************************************************")

        # uid无效
        response = u_bind.get_binding_status(0, token_1, app_id_1)
        if response.json()["code"] != "10000023":
            Logger.error("get binding status, expected code is 10000023")
            Logger.error(str(response.json()))
        # token失效
        # response = u_bind.get_binding_status(uid_1, token_err, app_id_1)
        # if response.json()["code"] != "10000002":
        #     Logger.error("get binding status, expected code is 10000002")
        #     Logger.error(str(response.json()))
        # app_id不合法
        response = u_bind.get_binding_status(uid_1, token_1, app_id_err)
        if response.json()["code"] != "10000003":
            Logger.error("get binding status, expected code is 10000003")
            Logger.error(str(response.json()))
        # 获取成功
        response = u_bind.get_binding_status(uid_1, token_1, app_id_1)
        if not response.json()["mobile"]:
            Logger.error("get uid_1 binding status, expected result is \"True\"")
            Logger.error(str(response.json()))
        response = u_bind.get_binding_status(uid_2, token_2, app_id_2)
        if not response.json()["mobile"]:
            Logger.error("get uid_2 binding status, expected result is \"True\"")
            Logger.error(str(response.json()))

        # 解绑手机 ************************************************************************************
        Logger.info("# 解绑手机 ************************************************************************************")

        # uid无效
        response = u_bind.unbind_mobile(0, token_1, app_id_1)
        if response.json()["code"] != "10000023":
            Logger.error("unbind mobile, expected code is 10000023")
            Logger.error(str(response.json()))
        # token失效
        # response = u_bind.unbind_mobile(uid_1, token_err, app_id_1)
        # if response.json()["code"] != "10000002":
        #     Logger.error("unbind mobile, expected code is 10000002")
        #     Logger.error(str(response.json()))
        # app_id失效
        response = u_bind.unbind_mobile(uid_1, token_1, app_id_err)
        if response.json()["code"] != "10000003":
            Logger.error("unbind mobile, expected code is 10000003")
            Logger.error(str(response.json()))
        # 不允许操作
        response = u_bind.unbind_mobile(uid_1, token_1, app_id_1)
        if response.json()["code"] != "10021201":
            Logger.error("unbind mobile, expected code is 10021101")
            Logger.error(str(response.json()))
        # 用户未绑定手机
        # response = u_bind.unbind_mobile(uid_2, token_2, app_id_2)
        # if response.json()["code"] != "10021100":
        #    Logger.error("unbind mobile, expected code is 10021100")
        #    Logger.error(str(response.json()))


if __name__ == "__main__":
    db = MongoProcess("uiac", "127.0.0.1", 27017)
    db.clear()
    db.init()

    cache = Cache("http://127.0.0.1:8081")
    cache.clean()

    a_b = AccountBase("http://127.0.0.1:8081")
    a_b.run()
