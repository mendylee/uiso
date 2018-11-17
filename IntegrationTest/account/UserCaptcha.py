__author__ = 'yxx'

import requests

from common.CONST import CONST


class UserCaptcha:
    def __init__(self, url):
        self.baseUrl = url + "/account/captcha"

    def send_captcha(self, mobile, check_type, token="token", app_id=CONST.APPID):
        head = CONST.getHead(token, app_id)
        params = "mobile=" + mobile + "&checktype=" + str(check_type)
        return requests.get(self.baseUrl, params=params, headers=head)

    def validate_captcha(self, mobile, captcha, check_type, token="token", app_id=CONST.APPID):
        head = CONST.getHead(token, app_id)
        data = {
            "mobile": mobile,
            "captcha": captcha,
            "checkType": check_type
        }
        return requests.put(self.baseUrl, data=data, headers=head)

if __name__ == "__main__":
    u = UserCaptcha("http://127.0.0.1:8081")
    response = u.send_captcha("13800000001", 2)
    print(response.json())
    response = u.validate_captcha("13800000001", 123456, 2)
    print(response.content)
