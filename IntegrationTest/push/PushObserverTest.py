__author__ = 'scg'

import os
import time
import socket

from common.CONST import CONST
from common.Logger import Logger
from db.mongo.mongo_process import MongoProcess
from authorization.AuthorizationTestBase import AuthorizationTestBase
from push.PushObserver import PushObserver
from push.PushObserverClientServer import PushObserverClientServer

class PushObserverTest(AuthorizationTestBase):
	def __init__(self, svc_url):
		AuthorizationTestBase.__init__(self, svc_url)

		self.http_svc1 = None
		self.http_svc2 = None
		self.push_obj = PushObserver(self.svcUrl)
		self.proc_import = False

	def get_interface_ip(self, ifname):
		if not self.proc_import and os.name != "nt":
			import fcntl
			import struct
			self.proc_import = True

		s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
		return socket.inet_ntoa(fcntl.ioctl(s.fileno(), 0x8915, struct.pack('256s', ifname[:15]))[20:24])

	def get_lan_ip(self):
		ip = socket.gethostbyname(socket.gethostname())
		if ip.startswith("127.") and os.name != "nt":
			interfaces = ["eth0", "eth1", "eth2", "wlan0", "wlan1", "wifi0", "ath0", "ath1", "ppp0"]
			for ifname in interfaces:
				try:
					ip = self.get_interface_ip(ifname)
					break
				except IOError:
					pass
		return ip

	def test(self):
		appid1 = "1"
		appid2 = "2"
		user_code1 = "13311224400"
		user_code2 = "13311225500"
		pwd = "123456"
		port1 = 9527
		port2 = 9528

		local_url1 = "http://%s:%s" % (self.get_lan_ip(), port1)
		local_url2 = "http://%s:%s" % (self.get_lan_ip(), port1)

		self.http_svc1 = PushObserverClientServer("", port1, appid1)
		self.http_svc2 = PushObserverClientServer("", port2, appid2)

		# 注册用户
		print("register user!")
		req = self.user.register(user_code1, pwd, "", "")
		if not req.ok:
			Logger.error("register user failed!user_code=%s,pwd=%s, msg=%s", user_code1, pwd, req.content)
			return

		req = self.user.register(user_code2, pwd, "", "")
		if not req.ok:
			Logger.error("register user failed!user_code=%s,pwd=%s, msg=%s", user_code1, pwd, req.content)
			return

		# 未注册监听请求前，先发起一次登录请求
		print("user login for app1!")
		rtn = self.login(user_code1, pwd, appid1)
		if rtn is None:
			Logger.error("login failed!user=%s, pwd=%s, appId=%s", user_code1, pwd, appid1)
			return
		login_user1 = rtn

		rtn = self.login(user_code2, pwd, appid1)
		if rtn is None:
			Logger.error("login failed!user=%s, pwd=%s, appId=%s", user_code2, pwd, appid1)
			return
		login_user2 = rtn

		time.sleep(8)

		app1_login_count = 0
		app1_update_count = 0
		app1_logout_count = 0
		app2_login_count = 0
		app2_update_count = 0
		app2_logout_count = 0
		# 开始客户端的HTTP服务
		print("start local http server!")
		self.http_svc1.start()
		self.http_svc2.start()

		#注册监听
		print("register lcoal observer for app1 and app2!")
		req = self.push_obj.register(local_url1, appid1)
		if not req.ok:
			Logger.error("register observer failed!callback_url=%s,appid=%s, msg=%s", local_url1, appid1, req.content)
			return

		req = self.push_obj.register(local_url2, appid2)
		if not req.ok:
			Logger.error("register observer failed!callback_url=%s,appid=%s, msg=%s", local_url2, appid2, req.content)
			return

		# 登录app2
		print("login user for app2")
		rtn = self.login(user_code1, pwd, appid2)
		if rtn is None:
			Logger.error("login failed!user=%s, pwd=%s, appId=%s", user_code1, pwd, appid2)
			return
		login_user1_1 = rtn
		app2_login_count += 1

		rtn = self.login(user_code2, pwd, appid2)
		if rtn is None:
			Logger.error("login failed!user=%s, pwd=%s, appId=%s", user_code2, pwd, appid2)
			return
		login_user2_1 = rtn
		app2_login_count += 1

		print("waiting message callback!")
		time.sleep(8)

		msg1 = self.http_svc1.get_message()
		tmpMsg = msg1.get("Authroize_login")
		if tmpMsg is None:
			tmpMsg = []

		if len(tmpMsg) != app1_login_count:
			Logger.error("check Authroize_login message count failed!except=%s, actual=%s, appId=%s, msg=%s", app1_login_count, len(msg1), appid1, msg1)
			return

		msg2 = self.http_svc2.get_message()
		tmpMsg = msg2.get("Authroize_login")
		if tmpMsg is None:
			tmpMsg = []
		if len(tmpMsg) != app2_login_count:
			Logger.error("check Authroize_login message count failed!except=%s, actual=%s, appId=%s, msg=%s", app2_login_count, len(msg2), appid2, msg2)
			return

		# 用户注销
		print("logout for app1")
		rtn = self.logout(login_user1["uid"], login_user1["accessToken"])
		if rtn is None:
			Logger.error("logout failed!user=%s, appId=%s, user=%s", user_code1, appid1, login_user1)
			return
		app1_logout_count += 1

		rtn = self.logout(login_user2["uid"], login_user2["accessToken"])
		if rtn is None:
			Logger.error("logout failed!user=%s, appId=%s, user=%s", user_code1, appid1, login_user2)
			return
		app1_logout_count += 1

		# 重复注销，通知应该是不会累加1的
		rtn = self.logout(login_user2["uid"], login_user2["accessToken"])
		if rtn is not None:
			Logger.error("logout failed!user=%s, appId=%s, user=%s", user_code1, appid1, login_user2)
			return

		rtn = self.logout(login_user1["uid"], login_user1["accessToken"])
		if rtn is not None:
			Logger.error("logout failed!user=%s, appId=%s, user=%s", user_code1, appid1, login_user1)
			return

		print("waiting message receive!")
		time.sleep(8)

		# 判断注销请求数量
		msg1 = self.http_svc1.get_message()
		tmpMsg = msg1.get("Authroize_Logout")
		if tmpMsg is None:
			tmpMsg = []
		if len(tmpMsg) != app1_logout_count:
			Logger.error("check Authroize_Logout message count failed!except=%s, actual=%s, appId=%s, msg=%s", app1_logout_count, len(msg1), appid1, msg1)
			return

		msg2 = self.http_svc2.get_message()
		tmpMsg = msg2.get("Authroize_Logout")
		if tmpMsg is None:
			tmpMsg = []
		if len(tmpMsg) != app2_logout_count:
			Logger.error("check Authroize_Logout message count failed!except=%s, actual=%s, appId=%s, msg=%s", app2_logout_count, len(msg2), appid2, msg2)
			return

		# 注销App2登录
		print("logout for app2")
		rtn = self.logout(login_user1_1["uid"], login_user1_1["accessToken"])
		if rtn is None:
			Logger.error("logout failed!user=%s, appId=%s, user=%s", user_code1, appid2, login_user1_1)
			return
		app2_logout_count += 1

		rtn = self.logout(login_user2_1["uid"], login_user2_1["accessToken"])
		if rtn is None:
			Logger.error("logout failed!user=%s, appId=%s, user=%s", user_code1, appid2, login_user2_1)
			return
		app2_logout_count += 1

		print("waiting app2 logout message!")
		time.sleep(8)

		# 判断注销请求数量
		msg1 = self.http_svc1.get_message()
		tmpMsg = msg1.get("Authroize_Logout")
		if tmpMsg is None:
			tmpMsg = []
		if len(tmpMsg) != app1_logout_count:
			Logger.error("check Authroize_Logout message count failed!except=%s, actual=%s, appId=%s, msg=%s", app1_logout_count, len(msg1), appid1, msg1)
			return

		msg2 = self.http_svc2.get_message()
		tmpMsg = msg2.get("Authroize_Logout")
		if tmpMsg is None:
			tmpMsg = []
		if len(tmpMsg) != app2_logout_count:
			Logger.error("check Authroize_Logout message count failed!except=%s, actual=%s, appId=%s, msg=%s", app2_logout_count, len(msg2), appid2, msg2)
			return

		# 重新登录
		print("user login for app1 and app2!")
		rtn = self.login(user_code1, pwd, appid1)
		if rtn is None:
			Logger.error("login failed!user=%s, pwd=%s, appId=%s", user_code1, pwd, appid1)
			return
		print("user1 app1 login:%s" % rtn)
		login_user1 = rtn
		app1_login_count += 1

		rtn = self.login(user_code2, pwd, appid1)
		if rtn is None:
			Logger.error("login failed!user=%s, pwd=%s, appId=%s", user_code2, pwd, appid1)
			return
		print("user2 app1 login:%s" % rtn)
		login_user2 = rtn
		app1_login_count += 1

		rtn = self.login(user_code1, pwd, appid2)
		if rtn is None:
			Logger.error("login failed!user=%s, pwd=%s, appId=%s", user_code1, pwd, appid2)
			return
		print("user1 app2 login:%s" % rtn)
		login_user1_1 = rtn
		app2_login_count += 1

		rtn = self.login(user_code2, pwd, appid2)
		if rtn is None:
			Logger.error("login failed!user=%s, pwd=%s, appId=%s", user_code2, pwd, appid2)
			return
		print("user2 app2 login:%s" % rtn)
		login_user2_1 = rtn
		app2_login_count += 1

		time.sleep(6)
		rtn = self.login(user_code2, pwd, appid2)
		if rtn is None:
			Logger.error("login failed!user=%s, pwd=%s, appId=%s", user_code2, pwd, appid2)
			return
		login_user2_1 = rtn
		app2_login_count += 1

		print("waiting login message callback!")
		time.sleep(8)

		msg1 = self.http_svc1.get_message()
		tmpMsg = msg1.get("Authroize_login")
		if tmpMsg is None:
			tmpMsg = []

		if len(tmpMsg) != app1_login_count:
			Logger.error("check Authroize_login message count failed!except=%s, actual=%s, appId=%s, msg=%s", app1_login_count, len(msg1), appid1, msg1)
			return

		msg2 = self.http_svc2.get_message()
		tmpMsg = msg2.get("Authroize_login")
		if tmpMsg is None:
			tmpMsg = []
		if len(tmpMsg) != app2_login_count:
			Logger.error("check Authroize_login message count failed!except=%s, actual=%s, appId=%s, msg=%s", app2_login_count, len(msg2), appid2, msg2)
			return

		# 更新授权码
		print("update token message test for app1 and app2")
		rtn = self.update(login_user1["uid"], login_user1["accessToken"], login_user1["refreshToken"], login_user1["appId"])
		if rtn is None:
			Logger.error("update user1 app1 token failed!user=%s", login_user1)
			return
		login_user1 = rtn
		app1_update_count += 1

		rtn = self.update(login_user2["uid"], login_user2["accessToken"], login_user2["refreshToken"], login_user2["appId"])
		if rtn is None:
			Logger.error("update user2 app1 token failed!user=%s", login_user2)
			return
		login_user2 = rtn
		app1_update_count += 1

		rtn = self.update(login_user1_1["uid"], login_user1_1["accessToken"], login_user1_1["refreshToken"], login_user1_1["appId"])
		if rtn is None:
			Logger.error("update user1 app2 token failed!user=%s", login_user1_1)
			return
		login_user1_1 = rtn
		app2_update_count += 1

		rtn = self.update(login_user2_1["uid"], login_user2_1["accessToken"], login_user2_1["refreshToken"], login_user2_1["appId"])
		if rtn is None:
			Logger.error("update user2 app2 token failed!user=%s", login_user2_1)
			return
		login_user2_1 = rtn
		app2_update_count += 1

		print("waiting update message callback!")
		time.sleep(8)

		msg1 = self.http_svc1.get_message()
		tmpMsg = msg1.get("Authorize_Update")
		if tmpMsg is None:
			tmpMsg = []

		if len(tmpMsg) != app1_update_count:
			Logger.error("check Authorize_Update message count failed!except=%s, actual=%s, appId=%s, msg=%s", app1_update_count, len(msg1), appid1, msg1)
			return

		msg2 = self.http_svc2.get_message()
		tmpMsg = msg2.get("Authorize_Update")
		if tmpMsg is None:
			tmpMsg = []
		if len(tmpMsg) != app2_update_count:
			Logger.error("check Authorize_Update message count failed!except=%s, actual=%s, appId=%s, msg=%s", app2_update_count, len(msg2), appid2, msg2)
			return

		# 删除监听
		print("unregister observer!")
		req = self.push_obj.un_register(appid1)

		print("logout for app1 and app2")
		rtn = self.logout(login_user1["uid"], login_user1["accessToken"])
		if rtn is None:
			Logger.error("logout failed!user=%s, appId=%s, user=%s", user_code1, appid1, login_user1)
			return

		rtn = self.logout(login_user2["uid"], login_user2["accessToken"])
		if rtn is None:
			Logger.error("logout failed!user=%s, appId=%s, user=%s", user_code1, appid1, login_user2)
			return

		rtn = self.logout(login_user1_1["uid"], login_user1_1["accessToken"])
		if rtn is None:
			Logger.error("logout failed!user=%s, appId=%s, user=%s", user_code1, appid2, login_user1_1)
			return
		app2_logout_count += 1

		rtn = self.logout(login_user2_1["uid"], login_user2_1["accessToken"])
		if rtn is None:
			Logger.error("logout failed!user=%s, appId=%s, user=%s", user_code1, appid2, login_user2_1)
			return
		app2_logout_count += 1

		print("waiting app2 logout message callback!")
		time.sleep(8)
		# 判断注销请求数量
		msg1 = self.http_svc1.get_message()
		tmpMsg = msg1.get("Authroize_Logout")
		if tmpMsg is None:
			tmpMsg = []
		if len(tmpMsg) != app1_logout_count:
			Logger.error("check Authroize_Logout message count failed!except=%s, actual=%s, appId=%s, msg=%s", app1_logout_count, len(msg1), appid1, msg1)
			return

		msg2 = self.http_svc2.get_message()
		tmpMsg = msg2.get("Authroize_Logout")
		if tmpMsg is None:
			tmpMsg = []
		if len(tmpMsg) != app2_logout_count:
			Logger.error("check Authroize_Logout message count failed!except=%s, actual=%s, appId=%s, msg=%s", app2_logout_count, len(msg2), appid2, msg2)
			return

		req = self.push_obj.un_register(appid2)

		# 停止HTTP服务
		self.http_svc1.stop()
		self.http_svc2.stop()
		self.http_svc1 = None
		self.http_svc2 = None

	def run(self):
		appid1 = "1"
		appid2 = "2"

		self.test()
		self.recycle()

		# 删除监听
		self.push_obj.un_register(appid1)
		self.push_obj.un_register(appid2)

		if self.http_svc1 is not None:
			self.http_svc1.stop()

		if self.http_svc2 is not None:
			self.http_svc2.stop()



if __name__ == "__main__":
	db = MongoProcess("uiac", CONST.MONGO_HOST)
	db.init()
	Logger.init()
	test = PushObserverTest(CONST.SERVICE_URL)
	test.run()

	if Logger.is_failed():
		print("Test failed!")
	else:
		print("Test success!")