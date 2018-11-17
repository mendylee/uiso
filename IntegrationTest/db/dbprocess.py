# coding=utf-8
__author__ = 'scg'

from abc import abstractmethod, ABCMeta

class DbProcess:
	__metaclass__ = ABCMeta

	def __init__(self, dbname, host, port):
		"""
		初始化数据库处理对象
		:param dbname 数据库名称
		:param host: 连接的服务器IP
		:param port: 连接的服务器端口
		:return:
		"""
		self.dbname = dbname
		self.host = host
		self.port = port

	@abstractmethod
	def init(self):
		"""
		初始化数据库方法，需要执行清除原有数据库，创建一个新数据库操作，
		此方法为抽像方法，需要具体实现类进行重载
		:return:
		"""
		pass

	@abstractmethod
	def clear(self):
		"""
		清除数据库操作，清除已创建的测试环境数据库名称
		:return:
		"""
		pass

if __name__ == "__main__":
	pass

