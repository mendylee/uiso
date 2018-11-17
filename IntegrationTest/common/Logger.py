__author__ = 'scg'
import logging
import os

class Logger:
	count = 0

	@staticmethod
	def init():
		fname = os.path.join(os.getcwd(), 'log.txt')
		logging.basicConfig(filename=fname, level=logging.DEBUG, filemode='w', format='%(asctime)s - %(levelname)s: %(message)s')
		Logger.count = 0

	@staticmethod
	def error(msg, *args, **kwargs):
		logging.error(msg, *args, **kwargs)
		msg = "ERROR:"+msg
		print(msg % args)
		Logger.count += 1

	@staticmethod
	def info(msg, *args, **kwargs):
		logging.info(msg, *args, **kwargs)

	@staticmethod
	def warn(msg, *args, **kwargs):
		logging.warning(msg, *args, **kwargs)

	@staticmethod
	def debug(msg, *args, **kwargs):
		logging.debug(msg, *args, **kwargs)

	@staticmethod
	def is_failed():
		return Logger.count > 0

if __name__ == "__main__":
	Logger.init()
	Logger.error("bbbbbbbb=%s", 123455666)
	print(Logger.is_failed())

