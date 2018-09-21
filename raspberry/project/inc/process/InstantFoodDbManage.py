from ..util.MsgExchange import MsgExchange
from ..util.data import InstantFood
from ..util.log import Log
import urllib.request
import threading
import MySQLdb
from datetime import datetime
import time
import json


class InstantFoodDbManage(threading.Thread):
    # Main
    TAG = "InstantFoodDbManage"
    UPDATE_URL = 'http://hungrypet.altervista.org/upload_data.php?table=instant_food'
    REQUEST_URL = 'http://hungrypet.altervista.org/request_data.php?table=instant_food&mac='
    TIME = 5  # seconds
    TIME_INSTANT_AVAR = 60 * 2  # seconds

    # Json message
    JS_INSTANT_FOOD = "{'action': 'instant_food'}"

    # class var
    loop = True
    is_running = True
    cursor = 0

    # external class
    wifi_conn = 0
    msg_exc = 0

    def __init__(self, wifi_conn):
        threading.Thread.__init__(self)
        db = MySQLdb.connect(host="localhost", user="root", passwd="", db="my_hungrypet")
        self.cursor = db.cursor()
        self.msg_exc = MsgExchange.get_instance()
        self.wifi_conn = wifi_conn

    def close(self):
        self.loop = False

    def run(self):
        """ Method triggered from thread """
        Log.i(self.TAG, 'Thread started')

        while self.loop:
            self.check_remote()
            # Sleeping time
            time.sleep(self.TIME)

        Log.i(self.TAG, 'Thread closed')
        self.is_running = False

    def check_remote(self):
        instant_remote = self.get_remote()
        if len(instant_remote) > 0:
            self.msg_exc.put_to_serial(self.JS_INSTANT_FOOD)

        for instant in instant_remote:
            self.update_remote(instant)

    def update_remote(self, instant_food):
        if self.wifi_conn.is_connected():
            date_now = datetime.today().strftime('%Y-%m-%d %H:%M:%S')

            url = (self.UPDATE_URL + "&id=" + instant_food.get_id() + "&date_update=" + date_now)
            url = url.replace(" ", "+")

            try:
                contents = urllib.request.urlopen(url).read()
                js_cont = json.loads(contents.decode("utf-8"))

                if js_cont["action"] == "upload" and js_cont["success"]:
                    return True
                else:
                    return False

            except Exception as e:
                Log.e(self.TAG, "Update remote: " + str(e))

        return False

    def get_remote(self):
        instant_foods = []
        if self.wifi_conn.is_connected():
            url = self.REQUEST_URL + self.wifi_conn.get_mac()

            try:
                contents = urllib.request.urlopen(url).read()
                js_cont = json.loads(contents.decode("utf-8"))

                for js_instant in js_cont['data']:
                    instant_food = InstantFood(
                        js_instant['id'],
                        js_instant['mac'],
                        js_instant['done'],
                        js_instant['date_create'],
                        js_instant['date_update'],
                    )
                    instant_foods += [instant_food]
            except Exception as e:
                Log.e(self.TAG, "Get remote: " + str(e))
                return None

        return instant_foods
