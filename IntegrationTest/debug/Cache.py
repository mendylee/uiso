__author__ = 'yxx'

import requests

from common.CONST import CONST


class Cache:
    def __init__(self, url):
        self.baseUrl = url + "/debug/cache"

    def clean(self):
        head = CONST.getHead("token")
        requests.delete(self.baseUrl, headers=head)
