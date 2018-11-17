__author__ = 'Administrator'

import hashlib

class Md5:
	@staticmethod
	def md5(str_input):
		return hashlib.md5(str_input.encode("utf8")).hexdigest()

	@staticmethod
	def encoder_pwd(pwd, account, timestamp):
		return Md5.md5(Md5.md5(Md5.md5(pwd)+account)+timestamp)

if __name__ == "__main__":
	pwd = "123456"
	t = "1433487867974"
	a = "13311223344"
	print("1:%s"%(pwd))
	pwd = Md5.md5(pwd)
	print("2:%s"%(pwd))
	pwd = Md5.md5(pwd+a)
	print("3:%s"%(pwd))
	pwd = Md5.md5(pwd+t)
	print("4:%s"%(pwd))