__author__ = 'yxx'

import requests
import hashlib

from common.CONST import CONST
from account.AccountUtils import AccountUtils


class User:
    def __init__(self, url):
        self.baseUrl = url + "/account/user"

    def register(self, user_code, password, user_info, extend_info, app_id=CONST.APPID, unverified="true"):
        head = CONST.getHead("token", app_id)
        if password != "":
            password = hashlib.md5(password.encode("utf8")).hexdigest()
        data = {
            "mobile": user_code,
            "password": password,
            "userInfo": AccountUtils.get_user_info(),
            "extendInfo": AccountUtils.get_extend_info(),
            "unverified": unverified
        }
        if user_info != "":
            data["userInfo"] = user_info
        if extend_info != "":
            data["extendInfo"] = extend_info
        return requests.post(self.baseUrl, data=data, headers=head)

    def get_user_info(self, uid, token="token", app_id=CONST.APPID):
        head = CONST.getHead(token, app_id)
        # params = "uid=" + str(uid)
        url = self.baseUrl + "/" + str(uid)
        return requests.get(url, headers=head)

    def update_user_info(self, uid, user_info, extend_info, token="token", app_id=CONST.APPID):
        head = CONST.getHead(token, app_id)
        data = {
            "userInfo": AccountUtils.get_up_user_info(),
            "extendInfo": AccountUtils.get_up_extend_info()
        }
        if user_info != "":
            data["userInfo"] = user_info
        if extend_info != "":
            data["extendInfo"] = extend_info

        url = self.baseUrl + "/" + str(uid)
        return requests.put(url, data=data, headers=head)

if __name__ == "__main__":
    u = User("http://127.0.0.1:8081")
    # response = u.register("13800000001", "12345678", "", "")
    response = u.update_user_info(100000009, "", "")
    response = u.get_user_info(100000009)

    print(response.url)
    rtnJson = response.json()
    print(rtnJson)
