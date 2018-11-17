# coding=utf-8
__author__ = 'scg'

from datetime import datetime

from pymongo import MongoClient
from pymongo import ASCENDING, DESCENDING

class MongoInit:

    def __init__(self, dbname, host="127.0.0.1", port=27017):
        self.dbname = dbname
        self.client = MongoClient(host, port)

    def init(self):
        # self.clear()

        config = [{
            "param_key": "CAPTCHA_EXPIRE_TIME",
            "param_value": "120"
        }, {
            "param_key": "VALIDATION_EXPIRE_TIME",
            "param_value": "1800"
        }, {
            "param_key": "ACCESS_TOKEN_EXPIRE_TIME",
            "param_value": "20"
        }, {
            "param_key": "HTTP_LISTEN_PORT",
            "param_value": "8081"
        }, {
            "param_key": "CACHE_CLASS",
            "param_value": "com.xrk.uiac.bll.cache.external.redis.RedisCache"
        }, {
            "param_key": "MEMCACHED_SERVER_ADDR",
            "param_value": "127.0.0.1:11211"
        }, {
            "param_key": "REDIS_SERVER_ADDR",
            "param_value": "127.0.0.1:6379"
        }, {
            "param_key": "REDIS_SERVER_IS_CLUSTER",
            "param_value": "false"
        }, {
            "param_key": "PUSH_ENABLE",
            "param_value": "true"
        }, {
            "param_key": "PUSH_INTERVAL",
            "param_value": "5000"
        }, {
            "param_key": "PUSH_QUEUE_SIZE",
            "param_value": "1000"
        }, {
            "param_key": "SMS_HOST",
            "param_value": "http://192.168.30.10:3008/"
        }, {
            "param_key": "REQUIRE_AUTH",
            "param_value": "false"
        }]
        db = self.client.get_database(self.dbname)
        app_setting = db.get_collection("uiac_app_setting")
        app_setting.insert_many(config)

        dt = datetime.now()

        app_info_list = [{
            "app_id": 1001,
            "app_name": "向日葵主站",
            "is_thirdparty": 0,
            "remark": "substation",
            "add_date": dt,
            "is_del": 0
        }, {
            "app_id": 1002,
            "app_name": "我的向日葵（移动版）",
            "is_thirdparty": 0,
            "remark": "vip_dashboard",
            "add_date": dt,
            "is_del": 0
        }, {
            "app_id": 1003,
            "app_name": "微站",
            "is_thirdparty": 0,
            "remark": "brx",
            "add_date": dt,
            "is_del": 0
        }, {
            "app_id": 1004,
            "app_name": "问吧",
            "is_thirdparty": 0,
            "remark": "iask",
            "add_date": dt,
            "is_del": 0
        }, {
            "app_id": 1005,
            "app_name": "社区",
            "is_thirdparty": 0,
            "remark": "community",
            "add_date": dt,
            "is_del": 0
        }, {
            "app_id": 1006,
            "app_name": "微聊",
            "is_thirdparty": 0,
            "remark": "wl",
            "add_date": dt,
            "is_del": 0
        }, {
            "app_id": 1007,
            "app_name": "展业号",
            "is_thirdparty": 0,
            "remark": "zyh",
            "add_date": dt,
            "is_del": 0
        }, {
            "app_id": 1008,
            "app_name": "V计划",
            "is_thirdparty": 0,
            "remark": "vplan",
            "add_date": dt,
            "is_del": 0
        }, {
            "app_id": 2001,
            "app_name": "统一认证站点",
            "is_thirdparty": 0,
            "remark": "sso_web",
            "add_date": dt,
            "is_del": 0
        }, {
            "app_id": 2002,
            "app_name": "向日葵后台管理系统",
            "is_thirdparty": 0,
            "remark": "xrk_admin",
            "add_date": dt,
            "is_del": 0
        }, {
            "app_id": 2003,
            "app_name": "口碑",
            "is_thirdparty": 0,
            "remark": "koubei",
            "add_date": dt,
            "is_del": 0
        }, {
            "app_id": 2004,
            "app_name": "微站后台",
            "is_thirdparty": 0,
            "remark": "bxr_admin",
            "add_date": dt,
            "is_del": 0
        }, {
            "app_id": 2005,
            "app_name": "消息API",
            "is_thirdparty": 0,
            "remark": "im_api",
            "add_date": dt,
            "is_del": 0
        }, {
            "app_id": 2006,
            "app_name": "微站API",
            "is_thirdparty": 0,
            "remark": "bxr_api",
            "add_date": dt,
            "is_del": 0
        }, {
            "app_id": 2007,
            "app_name": "数据交换中心(Oracle)",
            "is_thirdparty": 0,
            "remark": "datahub",
            "add_date": dt,
            "is_del": 0
        }, {
            "app_id": 2008,
            "app_name": "账号API接口",
            "is_thirdparty": 0,
            "remark": "account_api",
            "add_date": dt,
            "is_del": 0
        }, {
            "app_id": 2901,
            "app_name": "SSO认证中心",
            "is_thirdparty": 0,
            "remark": "mysso",
            "add_date": dt,
            "is_del": 0
        }, {
            "app_id": 3001,
            "app_name": "微信",
            "is_thirdparty": 1,
            "remark": "weixin",
            "add_date": dt,
            "is_del": 0
        }
        ]
        app_info = db.get_collection("uiac_app_info")
        app_info.insert_many(app_info_list)

        # 增加表流水号
        seq_info = db.get_collection("uiac_seq")
        seq_info.insert_one({"tab_name": "uiac_user", "sid": 3000001})

        # 增加初始化用户
        user_info = db.get_collection("uiac_user")
        init_user = {
            "uid": 3000001,
            "account": "17900000000",
            "password": "b74aab3989e38a1f2270e5762d3be873",
            "status": 1,
            "add_date": dt,
            "is_del": 0
        }
        user_info.insert_one(init_user)

        # 创建索引
        user = db.get_collection("uiac_user")
        user.create_index([("uid", DESCENDING)], unique=True)
        user.create_index([("account", ASCENDING), ("is_del", ASCENDING)])

        user_info = db.get_collection("uiac_user_info")
        user_info.create_index([("uid", DESCENDING)], unique=True)
        user_info.create_index([("mobile", ASCENDING)])

        user_extend_info = db.get_collection("uiac_user_extend_info")
        user_extend_info.create_index([("uid", DESCENDING), ("ext_key", ASCENDING)], unique=True)

        user_stat = db.get_collection("uiac_user_stat")
        user_stat.create_index([("uid", DESCENDING)], unique=True)

        user_sub_account = db.get_collection("uiac_user_sub_account")
        user_sub_account.create_index([("uid", DESCENDING), ("app_id", ASCENDING)])

        verify_request = db.get_collection("uiac_verify_request")
        verify_request.create_index([("mobile", ASCENDING)])

    def clear(self):
        self.client.drop_database(self.dbname)

if __name__ == "__main__":
    mongo = MongoInit("uiac", "192.168.6.200", 28017)
    mongo.init()

