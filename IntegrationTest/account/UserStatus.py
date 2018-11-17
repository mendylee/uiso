__author__ = 'yxx'

import requests

from common.CONST import CONST


class UserStatus:
    def __init__(self, url):
        self.baseUrl = url + "/account/status"

    def disable_user(self, uid, target_id, token="token", app_id=CONST.APPID):
        head = CONST.getHead(token, app_id)
        url = self.baseUrl+"/"+str(uid)
        data = {
            "targetId": target_id
        }
        return requests.put(url, data=data, headers=head)

    def enable_user(self, uid, target_id, token="token", app_id=CONST.APPID):
        head = CONST.getHead(token, app_id)
        url = self.baseUrl+"/"+str(uid)
        params = "uid=" + str(uid) + "&targetId=" + str(target_id)
        return requests.delete(url, params=params, headers=head)

    def get_status(self, uid, target_id, token="token", app_id=CONST.APPID):
        head = CONST.getHead(token, app_id)
        url = self.baseUrl+"/"+str(uid)
        params = "targetId=" + str(target_id)
        return requests.get(url, params=params, headers=head)

if __name__ == "__main__":
    u = UserStatus("http://127.0.0.1:8081")
    response = u.disable_user(100000009)
    print(response.content)
    response = u.enable_user(100000009)
    print(response.content)
