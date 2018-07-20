from inc.util.MsgExchange import MsgExchange
from inc.util.data import FoodLevel
from inc.util.log import Log
import urllib.request
import threading
import MySQLdb
import time
import json


class FoodLevelDbManage(threading.Thread):
    # Main
    TAG = "FoodLevelDbManage"
    REQUEST_URL = 'http://hungrypet.altervista.org/upload_data.php?table=food_level'
    TIME = 1  # seconds
    # Json action
    A_CONTAINER_LEVEL = "container_level"
    A_BOWL_LEVEL = "bowl_level"
    # Type
    CONTAINER = "container"
    BOWL = "bowl"

    # class var
    loop = True
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

    def run(self):
        Log.i(self.TAG, 'Thread started')

        while self.loop:

            data = self.msg_exc.pop_from_serial(self.TAG)
            if data:
                data = json.loads(str(data))
                # selection of action
                try:
                    if data['action'] == self.A_CONTAINER_LEVEL:
                        """ Request of wifi """
                        content = data['content']
                        level = int(content)
                        self.update_local(self.CONTAINER, level)

                    elif data['action'] == self.A_BOWL_LEVEL:
                        """ Set wifi """
                        content = data['content']
                        level = content
                        self.update_local(self.BOWL, level)

                except KeyError as err:
                    Log.e(self.TAG, 'Wrong json access: ' + str(err))

            # Sleeping time
            time.sleep(self.TIME)

    def update_remote(self, type, level):
        if self.wifi_conn.is_connected():
            url = self.REQUEST_URL + "&mac=" + self.wifi_conn.get_mac() + "&type=" + type + "&level=" + str(level)
            Log.i(self.TAG, url)
            try:
                contents = urllib.request.urlopen(url).read()
                js_cont = json.loads(contents.decode("utf-8"))

                Log.g(self.TAG, js_cont)
                return True
            except Exception as e:
                Log.e(self.TAG, "Update to remote error: " + str(e))
                return None

    def update_local(self, type, level):
        mac = self.wifi_conn.get_mac()
        food_levels = self.get_local()

        self.cursor.execute("INSERT INTO food_level(mac, type, level, date_create, date_update) VALUES ("+mac+","+type+","+level+",[value-4],[value-5])")
        for db_schedule in self.cursor.fetchall():
            food_level = FoodLevel(
                db_schedule[0],
                db_schedule[1],
                db_schedule[2],
                db_schedule[3],
            )
            food_levels += [food_level]

        return food_levels

    def get_local(self):
        food_levels = []

        self.cursor.execute("SELECT * FROM food_level WHERE mac=" + self.wifi_conn.get_mac())
        for db_schedule in self.cursor.fetchall():
            food_level = FoodLevel(
                db_schedule[0],
                db_schedule[1],
                db_schedule[2],
                db_schedule[3],
            )
            food_levels += [food_level]

        return food_levels
