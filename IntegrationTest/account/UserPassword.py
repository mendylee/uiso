__author__ = 'yxx'

import requests
import hashlib

from common.CONST import CONST


class UserPassword:
    def __init__(self, url):
        self.baseUrl = url + "/account/password"

    def update_password(self, uid, old_pwd, password, token="token", app_id=CONST.APPID, unverified="true"):
        head = CONST.getHead(token, app_id)
        if old_pwd != "":
            old_pwd = hashlib.md5(old_pwd.encode("utf8")).hexdigest()
        if password != "":
            password = hashlib.md5(password.encode("utf8")).hexdigest()
        data = {
            "oldPwd": old_pwd,
            "password": password,
            "unverified": unverified
        }
        url = self.baseUrl + "/" + str(uid)
        return requests.put(url, data=data, headers=head)

    def reset_password(self, uid, password, app_id=CONST.APPID, unverified="true"):
        head = CONST.getHead("token", app_id)
        if password != "":
            password = hashlib.md5(password.encode("utf8")).hexdigest()
        data = {
            "password": password,
            "unverified": unverified
        }
        url = self.baseUrl + "/" + str(uid)
        return requests.post(url, data=data, headers=head)

if __name__ == "__main__":
    u = UserPassword("http://127.0.0.1:8081")
    # response = u.update_password(100000009, "12345678", "87654321")
    response = u.reset_password(100000009, "new_password")
    print(response.url)
    rtnJson = response.json()
    print(rtnJson)
