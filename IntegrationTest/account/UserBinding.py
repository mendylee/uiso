__author__ = 'yxx'

import requests

from common.CONST import CONST


class UserBinding:
    def __init__(self, url):
        self.baseUrl = url + "/account/bind"

    def get_binding_status(self, uid, token="token", app_id=CONST.APPID):
        head = CONST.getHead(token, app_id)
        # params = "uid=" + str(uid)
        url = self.baseUrl + "/" + str(uid)
        return requests.get(url, headers=head)

    def bind_mobile(self, uid, mobile, token="token", app_id=CONST.APPID, unverified="true"):
        head = CONST.getHead(token, app_id)
        data = {
            "mobile": mobile,
            "unverified": unverified
        }
        url = self.baseUrl + "/" + str(uid)
        return requests.put(url, data=data, headers=head)

    def unbind_mobile(self, uid, token="token", app_id=CONST.APPID, unverified="true"):
        head = CONST.getHead(token, app_id)
        params = "unverified=" + str(unverified)
        url = self.baseUrl + "/" + str(uid)
        return requests.delete(url, headers=head, params=params)

if __name__ == "__main__":
    u = UserBinding("http://127.0.0.1:8081")
    response = u.get_binding_status(100000009)
    print(response.json())
    response = u.bind_mobile(100000009, "13800000001")
    print(response.content)
    response = u.get_binding_status(100000009)
    print(response.json())
    response = u.unbind_mobile(100000009)
    print(response.content)
