# coding=utf-8
__author__ = 'scg'

import io
import sys
import os
import subprocess
import time

from common.CONST import CONST

from db.mongo.mongo_process import MongoProcess
from common.Logger import Logger

from account.AccountAdmin import AccountAdmin
from account.AccountBase import AccountBase
from account.AccountSubAccount import AccountSubAccount
from authorization.Authorization_SingleUser_Test import SingleUserTest
from authorization.Authorization_Mul_User_Test import MultiUserTest
from authorization.Authorization_Token_Expire_Test import TokenExpireTest
from push.PushObserverTest import PushObserverTest
from debug.Cache import Cache

class Main:
    def __init__(self, base_url, db_name="uiac"):
        self.baseUrl = base_url
        self.db_name = db_name
        Logger.init()

    def reset_db(self):
        # 初始化数据库
        db = MongoProcess(self.db_name, host=CONST.MONGO_HOST)
        db.init()

        # 清空服务端缓存
        cache = Cache(self.baseUrl)
        cache.clean()

    def test(self):
        """
        执行测试请求，根据测试顺序编写测试流程
        :return:
       """
        print("test AccountAdmin!")
        self.reset_db()
        account = AccountAdmin(self.baseUrl)
        account.run()

        print("test AccountBase!")
        self.reset_db()
        account_base = AccountBase(self.baseUrl)
        account_base.run()

        print("test AccountSubAccount!")
        self.reset_db()
        sub_account = AccountSubAccount(self.baseUrl)
        sub_account.run()

        print("test authorization SingleUserTest!")
        self.reset_db()
        single_user = SingleUserTest(self.baseUrl)
        single_user.run()

        print("test authorization MultiUserTest!")
        self.reset_db()
        multi_user = MultiUserTest(self.baseUrl)
        multi_user.run()

        print("test authorization TokenExpireTest!")
        self.reset_db()
        token_expire = TokenExpireTest(self.baseUrl)
        token_expire.run()

        print("test push server PushObserverTest!")
        self.reset_db()
        push = PushObserverTest(self.baseUrl)
        push.run()

        if Logger.is_failed():
            print("test failed! please view 'log.txt'!")
        else:
            print("Test success!")

    def run(self):
        print("build uiac service.")
        os.chdir("../")
        print("CURRENT WORK PATH: %s" % os.getcwd())
        #os.system("mvn package -DskipTests")
        print("run mongodb server")

        print("run uiac service")
        try:
            from subprocess import DEVNULL
        except ImportError:
            DEVNULL = open(os.devnull, 'wb')
        jar_common = "java -Dfile.encoding=utf-8 -jar uiac.service/target/xrk.uiac.service-1.0-SNAPSHOT.jar"
        #svc = subprocess.Popen(jar_common, stdout=DEVNULL, stderr=DEVNULL)

        os.chdir("IntegrationTest")
        print("run integration test")
        time.sleep(3)
        try:
            self.test()
        except:
            print("Unexpected error:", sys.exc_info()[0])
            raise
        finally:
            pass
            time.sleep(3)
            #svc.kill()


if __name__ == "__main__":
    db_name = "uiac"
    m = Main(CONST.SERVICE_URL, db_name)
    m.run()

