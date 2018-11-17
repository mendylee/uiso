__author__ = 'yxx'

import requests

from authorization.Authorization import Authorization
from common.CONST import CONST


class UserSubAccount:
    def __init__(self, url):
        self.baseUrl = url + "/account/subAccount"

    def get_sub_account_list(self, uid, token="token", app_id=CONST.APPID):
        head = CONST.getHead(token, app_id)
        # params = "uid=" + str(uid)
        url = self.baseUrl + "/" + str(uid)
        return requests.get(url, headers=head)

    def get_sub_account(self, sub_account, sub_app_id, app_id=CONST.APPID):
        head = CONST.getHead("", app_id)
        params = "subAccount=" + str(sub_account)+"&subAppId="+str(sub_app_id)
        url = self.baseUrl
        return requests.get(url, params=params, headers=head)

    def bind_sub_account(self, uid, temp_id, sub_app_id, token="token", app_id=CONST.APPID):
        head = CONST.getHead(token, app_id)
        data = {
            "uid": uid,
            "tempId": str(temp_id),
            "subAppId": sub_app_id
        }
        url = self.baseUrl + "/" + str(uid)
        return requests.post(url, data=data, headers=head)

    def unbind_sub_account(self, uid, sub_app_id, sub_account, token="token", app_id=CONST.APPID):
        head = CONST.getHead(token, app_id)
        params = "subAppId=" + str(sub_app_id)+"&subAccount="+sub_account
        url = self.baseUrl + "/" + str(uid)
        return requests.delete(url, params=params, headers=head)

if __name__ == "__main__":
    svcUrl = "http://127.0.0.1:8081"
    u = UserSubAccount(svcUrl)
    user_code = "17900000000"
    test_uid = 1000
    sub_appid1 = 3001
    sub_account1 = "wx000001"
    sub_appid2 = 2001
    sub_account2 = "wx000002"

    auth = Authorization(svcUrl)
    rtn = auth.login(user_code, "123456")
    if rtn is None:
        print("login failed!user=%s, pwd=%s", user_code, "123456")
    print(rtn.json())
    token = rtn.json()["accessToken"]

    response = u.bind_sub_account(test_uid, sub_account1, sub_appid1, token)
    print("bind_sub_account:"+str(response.content))
    response = u.bind_sub_account(test_uid, sub_account2, sub_appid2, token)
    print("bind_sub_account:"+str(response.content))
    response = u.get_sub_account_list(test_uid, token)
    print("get_sub_account_list:", response.json())
    response = u.get_sub_account(sub_account1, sub_appid1)
    print("get_sub_account:", response.json())
    response = u.get_sub_account(sub_account2, sub_appid2)
    print("get_sub_account:", response.json())
    response = u.get_sub_account(sub_account1, sub_appid2)
    print("get_sub_account:", response.json())
    response = auth.login_subaccount(CONST.APPID, sub_appid1, sub_account1)
    print("login_subaccount:"+str(response.content))
    response = auth.login_subaccount(CONST.APPID, sub_appid2, sub_account2)
    print("login_subaccount:"+str(response.content))
    response = auth.login_subaccount(CONST.APPID, sub_appid1, sub_account2)
    print("login_subaccount:"+str(response.content))
    response = u.unbind_sub_account(test_uid, sub_appid1, sub_account1, token)
    print("unbind_sub_account:"+str(response.content))
    response = u.unbind_sub_account(test_uid, sub_appid2, sub_account2, token)
    print("unbind_sub_account:"+str(response.content))
    response = u.get_sub_account(sub_account1, sub_appid1)
    print("get_sub_account:"+str(response.content))


