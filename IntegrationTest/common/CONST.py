__author__ = 'scg'

class CONST:
	VERSION_NAME = "XRK-CLIENT-VERSION"
	APPID_NAME = "XRK-APPID"
	ACCESS_TOKEN_NAME = "XRK-ACCESS-TOKEN"
	APPID = 1001
	VERSION = "1.0.0.1"

	MONGO_HOST = "127.0.0.1"
	SERVICE_URL = "http://127.0.0.1:8081"

	@staticmethod
	def getHead(token, app_id=APPID, version=VERSION):
		if app_id == '':
			app_id = CONST.APPID

		if version == '':
			version = CONST.VERSION

		return {
			CONST.ACCESS_TOKEN_NAME: token,
			CONST.APPID_NAME: app_id,
			CONST.VERSION_NAME: version
		}

if __name__ == "__main__":
	head = CONST.getHead("")
	print(head)
	head = CONST.getHead("token", 222, "2.00.2")
	print(head)
