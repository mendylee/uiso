__author__ = 'scg'

import requests

from common.CONST import CONST

class PushObserver:
	def __init__(self, url):
		self.baseURL = url+"/Authorize/push"

	def register(self, callback_url, app_id):
		head = CONST.getHead("", app_id)
		data = {
			"callBackUrl": callback_url
		}
		return requests.post(self.baseURL, data=data, headers=head)

	def un_register(self, app_id):
		head = CONST.getHead("", app_id)
		return requests.delete(self.baseURL, headers=head)

if __name__ == "__main__":
	c = PushObserver(CONST.SERVICE_URL)
	url = "http://127.0.0.1:8091/recv"
	resp = c.register(url, 1)
	if resp.status_code > 300:
		rtnJson = resp.json()
		print(rtnJson["message"])
		print(rtnJson["code"])
	else:
		print(resp.status_code, resp.json())

	resp = c.register(url, 1)
	if resp.status_code > 300:
		rtnJson = resp.json()
		print(rtnJson["message"])
		print(rtnJson["code"])
	else:
		print(resp.status_code, resp.json())

	resp = c.un_register(1)
	if resp.status_code > 300:
		rtnJson = resp.json()
		print(rtnJson["message"])
		print(rtnJson["code"])
	else:
		print(resp.status_code, resp.json())

	resp = c.un_register(1)
	if resp.status_code > 300:
		rtnJson = resp.json()
		print(rtnJson["message"])
		print(rtnJson["code"])
	else:
		print(resp.status_code, resp.json())


