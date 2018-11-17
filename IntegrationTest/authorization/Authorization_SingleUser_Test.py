__author__ = 'scg'

from common.CONST import CONST
from common.Logger import Logger
from db.mongo.mongo_process import MongoProcess

from authorization.AuthorizationTestBase import AuthorizationTestBase

class SingleUserTest(AuthorizationTestBase):
	"""
	单个用户登录测试
	"""
	def __init__(self, svc_url):
		AuthorizationTestBase.__init__(self, svc_url)
		# 初始化数据库
		# db = MongoProcess("uiac")
		# db.init()

	def test_single(self):
		user_code = "13911223344"
		user_code2 = "13911223345"
		pwd = "123456"

		# 注册用户
		req = self.user.register(user_code, pwd, "", "")
		if not req.ok:
			Logger.error("register user failed!user_code=%s,pwd=%s, msg=%s", user_code, pwd, req.content)
			return

		tmp_user = req.json()
		self.context.add_register_user(tmp_user)

		req = self.user.register(user_code2, pwd, "", "")
		if not req.ok:
			Logger.error("register user2 failed!user_code=%s,pwd=%s, msg=%s", user_code2, pwd, req.content)
			return

		tmp_user = req.json()
		self.context.add_register_user(tmp_user)

		# 尝试登录
		rtn = self.login(user_code, pwd)
		if rtn is None:
			Logger.error("login failed!user=%s, pwd=%s", user_code, pwd)
			return
		login_user = rtn

		# 重复登录
		rtn = self.login(user_code, pwd)
		if rtn is None:
			Logger.error("login failed!user=%s, pwd=%s", user_code, pwd)
			return
		if rtn["accessToken"] != login_user["accessToken"] or rtn["refreshToken"] != login_user["refreshToken"]:
			Logger.error("repeat login failed!newUser=%s, oldUser=%s", rtn, login_user)
			return

		# 尝试验证获取到的授权码
		rtn = self.verify_token(login_user["accessToken"])
		if not rtn:
			Logger.error("verify token failed!user=%s", login_user)
			return

		# 尝试验证查询登录用户信息
		rtn = self.verify_user(login_user["uid"], login_user["accessToken"])
		if rtn is None:
			Logger.error("verify user failed!user=%s", login_user)
			return

		if len(rtn) != 1:
			Logger.error("query user token failed!expect=%s, actual=%s", 1, len(rtn))
			print("query_user failed!%s" % rtn)
			return

		# 更新授权码
		rtn = self.update(login_user["uid"], login_user["accessToken"], login_user["refreshToken"])
		if rtn is None:
			Logger.error("update token failed!user=%s", login_user)
			return

		old_access_token = login_user["accessToken"]
		old_refresh_token = login_user["refreshToken"]
		login_user = rtn
		new_access_token = login_user["accessToken"]
		new_refresh_token = login_user["refreshToken"]
		if old_access_token == new_access_token or old_refresh_token == new_refresh_token:
			Logger.error("update token failed!token not refresh.")
			print("update_token failed!%s" % (req.json()))
			return

		# 验证旧token
		rtn = self.verify_token(old_access_token)
		if rtn:
			Logger.error("verify old token failed!old_token=%s, new_token=%s", old_access_token, new_access_token)
			return

		# 验证新token
		rtn = self.verify_token(new_access_token)
		if not rtn:
			Logger.error("verify new token failed!user=%s", login_user)
			return

		# 更新用户密码，不会影响当次登录
		new_pwd = "new_pass_word_123456"
		# print(login_user)
		req = self.password.update_password(login_user["uid"], pwd, new_pwd, login_user["accessToken"], login_user["appId"])
		if not req.ok:
			Logger.error("update user password failed!user_code=%s,pwd=%s, msg=%s", user_code, pwd, req.content)
			return

		# print("update result:status code=%s,ok=%s, content=%s" % (req.status_code, req.ok, req.json()))

		# 验证新token
		rtn = self.verify_token(new_access_token)
		if not rtn:
			Logger.error("verify new token failed!user=%s", login_user)
			return

		# 注销
		rtn = self.logout(login_user["uid"], login_user["accessToken"])
		if rtn is None:
			Logger.error("logout failed!user=%s", login_user)
			return

		# 再次注销应该找不到了
		rtn = self.logout(login_user["uid"], login_user["accessToken"])
		if rtn is not None:
			Logger.error("logout check failed!user=%s", login_user)

		# 尝试登录
		rtn = self.login(user_code, pwd)
		if rtn is not None:
			Logger.error("login failed, password update, but login success of the old password!user=%s, pwd=%s", user_code, pwd)
			return

		rtn = self.login(user_code, new_pwd)
		if rtn is None:
			Logger.error("login failed of the new password!user=%s, pwd=%s", user_code, new_pwd)
			return
		login_user = rtn

		# 验证新token
		rtn = self.verify_token(login_user["accessToken"])
		if not rtn:
			Logger.error("verify new token failed!user=%s", login_user)
			return

		# 更新用户密码为旧密码
		# print(login_user)
		req = self.password.update_password(login_user["uid"], new_pwd, pwd, login_user["accessToken"], login_user["appId"])
		if not req.ok:
			Logger.error("update user password failed!user_code=%s,pwd=%s, msg=%s", user_code, pwd, req.content)
			return

		# 注销
		rtn = self.logout(login_user["uid"], login_user["accessToken"])
		if rtn is None:
			Logger.error("logout failed!user=%s", login_user)
			return

		# 尝试登录
		rtn = self.login(user_code, pwd)
		if rtn is None:
			Logger.error("login old password failed!user=%s, pwd=%s", user_code, pwd)
			return
		login_user = rtn

		rtn = self.login(user_code2, pwd)
		if rtn is None:
			Logger.error("login old password failed!user=%s, pwd=%s", user_code2, pwd)
			return
		login_user2 = rtn

		# 修改用户的状态为禁用
		rtn = self.userStatus.disable_user(login_user2["uid"], login_user["uid"], login_user2["accessToken"], login_user2["appId"])
		if not rtn.ok:
			Logger.error("update user status failed!user_code=%s,pwd=%s, msg=%s", user_code2, pwd, rtn.json())
			return

		# 再验证用户的Token应该会失败
		rtn = self.verify_token(login_user["accessToken"])
		if rtn:
			Logger.error("verify update user status token failed!user=%s", login_user)
			return

		# 尝试登录也会失败
		rtn = self.login(user_code, pwd)
		if rtn is not None:
			Logger.error("login disable user is success!user=%s, pwd=%s", user_code, pwd)
			return

	def test_single_mul_app(self):
		appid1 = "1"
		appid2 = "2"
		user_code = "13311223355"
		pwd = "123456"

		# 注册用户
		req = self.user.register(user_code, pwd, "", "")
		if not req.ok:
			Logger.error("register user failed!user_code=%s,pwd=%s, msg=%s", user_code, pwd, req.content)
			return

		tmp_user = req.json()
		self.context.add_register_user(tmp_user)

		# 尝试登录 appId1
		rtn = self.login(user_code, pwd, appid1)
		if rtn is None:
			Logger.error("login failed!user=%s, pwd=%s, appId=%s", user_code, pwd, appid1)
			return
		login_user1 = rtn

		# 尝试登录 appId2
		rtn = self.login(user_code, pwd, appid2)
		if rtn is None:
			Logger.error("login failed!user=%s, pwd=%s, appId=%s", user_code, pwd, appid2)
			return
		login_user2 = rtn

		app1_token = login_user1["accessToken"]
		app2_token = login_user2["accessToken"]
		if app1_token == app2_token:
			Logger.error("login multi app failed!app1User=%s, app2User=%s", login_user1, login_user2)
			return

		# 尝试验证查询登录用户信息
		rtn = self.verify_user(login_user1["uid"], app1_token)
		if rtn is None:
			Logger.error("verify user failed!user=%s", login_user1)
			return

		if len(rtn) != 2:
			Logger.error("query user token failed!expect=%s, actual=%s", 2, len(rtn))
			return

		rtn2 = self.verify_user(login_user2["uid"], app2_token)
		if rtn2 is None:
			Logger.error("verify user failed!user=%s", login_user2)
			return

		if len(rtn2) != 2:
			Logger.error("query user token failed!expect=%s, actual=%s", 2, len(rtn2))
			return

		# 注销用户
		rtn = self.logout(login_user1["uid"], app1_token)
		if rtn is None:
			Logger.error("logout failed!user=%s", login_user1)
			return

		rtn = self.verify_user(login_user2["uid"], app2_token)
		if rtn is None:
			Logger.error("verify user failed!user=%s", login_user2)
			return

		if len(rtn) != 1:
			Logger.error("query user token failed!expect=%s, actual=%s", 1, len(rtn))
			return

		rtn = self.logout(login_user2["uid"], app2_token)
		if rtn is None:
			Logger.error("logout failed!user=%s", login_user2)
			return

	def run(self):
		self.recycle()
		self.test_single()
		self.recycle()

		self.test_single_mul_app()
		self.recycle()

if __name__ == "__main__":
	db = MongoProcess("uiac", CONST.MONGO_HOST)
	#db.init()
	Logger.init()
	test = SingleUserTest(CONST.SERVICE_URL)
	test.run()

	if Logger.is_failed():
		print("Test failed!")
	else:
		print("Test success!")

