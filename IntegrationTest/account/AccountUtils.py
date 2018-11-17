__author__ = 'yxx'

import hashlib

class AccountUtils:
    @staticmethod
    def get_user_info():
        return "{\"userName\":\"test\",\"sex\":1}"

    @staticmethod
    def get_extend_info():
        return "{\"a\":\"av\",\"b\":\"bv\"}"

    @staticmethod
    def get_up_user_info():
        return "{\"userName\":\"updateTest\",\"address\":\"addressTest\"}"

    @staticmethod
    def get_up_extend_info():
        return "{\"a\":\"newAv\",\"c\":\"cv\"}"
