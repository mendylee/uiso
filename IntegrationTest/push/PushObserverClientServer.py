__author__ = 'scg'

from http.server import SimpleHTTPRequestHandler, BaseHTTPRequestHandler
from threading import Thread
import socketserver
import time
import json

class RecvMessageHandler(BaseHTTPRequestHandler):
	"""
	自定义消息接收处理函数
	"""
	RecvMsg = []

	def do_GET(self):
		len = int(self.headers.get('content-length', 0))
		print("recv request:len=%s" % len)
		if len > 0:
			body = self.rfile.read(len).decode()
			print("recv content:%s" % body)
			recv_msg = json.loads(body)
			for msg in recv_msg:
				RecvMessageHandler.RecvMsg.append(msg)
				#print("recv message:%s" % msg)

		buf = "ok"
		self.protocol_version = "HTTP/1.1"
		self.send_response(200)
		self.end_headers()
		self.wfile.write(buf.encode())

	def do_POST(self):
		self.do_GET()

class PushObserverClientServer:
	def __init__(self, url, port, app_id):
		self.url = url
		self.port = port
		self.app_id = app_id
		self.http_svc = None

	def start_no_thread(self):
		handler = RecvMessageHandler
		self.http_svc = socketserver.TCPServer(("", self.port), handler)

		print("Start http server at port", self.port)
		self.http_svc.serve_forever()

	def start(self):
		thread = Thread(target=self.start_no_thread)
		thread.start()
		start_time = int(time.time())
		while not self.http_svc:
			if int(time.time()) > start_time + 60:
				print("Time out")
				break

		return self.http_svc

	def get_message(self):
		msg = RecvMessageHandler.RecvMsg.copy()
		match_msg = {}
		for m in msg:
			m = json.loads(m)
			push_type = m.get("pushType")
			message = m.get("message")
			if message is not None and message != "":
				message = json.loads(message)
				app_id = message.get("appId")
				if app_id == self.app_id:
					if match_msg.get(push_type) is None:
						match_msg[push_type] = []

					match_msg[push_type].append(message)

		return match_msg

	def stop(self):
		if self.http_svc:
			self.http_svc.shutdown()
			print("http server stop")

if __name__ == "__main__":
	svc = PushObserverClientServer("", 9527, "1")
	svc.start()
	time.sleep(30)
	svc.stop()
	msg = svc.get_message()
	for m in msg:
		print("get message: %s" % m)

