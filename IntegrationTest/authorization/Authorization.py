# coding=utf-8
__author__ = 'scg'
import requests
import time

from common.CONST import CONST
from common.Md5Helper import Md5

class Authorization:
	def __init__(self, url):
		self.baseURL = url+"/accesstoken"

	def login(self, account, pwd, app_id=''):
		head = CONST.getHead("", app_id)

		timestamp = str(int(time.time()*1000))
		pwd = Md5.encoder_pwd(pwd, account, timestamp)

		data = {
			"account": account,
			"password": pwd,
			"timestamp": timestamp,
			"scope": ""
		}
		#print(data)
		#print(head)
		return requests.post(self.baseURL, data=data, headers=head)

	def login_subaccount(self, app_id, sub_app_id, sub_account):
		head = CONST.getHead("", app_id)

		timestamp = str(int(time.time()*1000))
		url = self.baseURL+"/subaccount"

		data = {
			"subaccount": sub_account,
			"subAppId": sub_app_id,
			"scope": ""
		}
		return requests.post(url, data=data, headers=head)

	def update_token(self, uid, token, refresh_token, appId=""):
		head = CONST.getHead(token, appId)
		data = {
			"uid" : uid,
			"refreshToken" : refresh_token
		}
		return requests.put(self.baseURL, params=data, headers=head)

	def query(self, token):
		return requests.get(self.baseURL, headers=CONST.getHead(token))

	def query_user(self, uid, token):
		tmp_url = "%s/%s" % (self.baseURL, uid)
		head = CONST.getHead(token)
		return requests.get(tmp_url, headers=head)

	def logout(self, token):
		return requests.delete(self.baseURL, headers=CONST.getHead(token))

	def test(self):
		# 写上模拟测试的一些运作。检测过程等

		pass

if __name__ == "__main__":
	c = Authorization(CONST.SERVICE_URL)
	resp = c.login("13311223344", "123456")
	print(resp.url)
	print(resp.status_code)
	print(resp.headers)
	print(resp.content)
	if resp.status_code > 300:
		rtnJson = resp.json()
		print(rtnJson["message"])
		print(rtnJson["code"])

