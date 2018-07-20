from inc.util.MsgExchange import MsgExchange
from inc.util.data import FoodLevel
from inc.util.log import Log
import urllib.request
import threading
import MySQLdb
from datetime import datetime
import time
import json


class FoodLevelDbManage(threading.Thread):
    # Main
    TAG = "FoodLevelDbManage"
    UPDATE_URL = 'http://hungrypet.altervista.org/upload_data.php?table=food_level'
    REQUEST_URL = 'http://hungrypet.altervista.org/request_data.php?table=food_level&mac='
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
        """ Method triggered from thread """
        Log.i(self.TAG, 'Thread started')

        while self.loop:
            self.check_message_from_serial()
            self.check_update_to_remote()

            # Sleeping time
            time.sleep(self.TIME)

    def check_message_from_serial(self):
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

    def check_update_to_remote(self):
        foods_remote = self.get_remote()
        foods_local = self.get_local()
        to_remote = []
        to_local = []

        # loop local
        for local in foods_local:
            if local in foods_remote:
                # loop remote
                for remote in foods_remote:
                    if local.get_type() == remote.get_type():

                        data_local = datetime.strptime(local.get_date_update(), '%Y-%m-%d %H:%M:%S')
                        data_remote = datetime.strptime(remote.get_date_update(), '%Y-%m-%d %H:%M:%S')
                        if data_remote > data_local:
                            # Check if remote is more recent of local, then upload on local
                            to_local += [remote]
                        elif data_remote < data_local:
                            to_remote += [local]
            else:
                to_remote += [local]

        # Check if we have to insert a remote level
        for remote in foods_remote:
            if remote not in foods_local:
                to_local += remote

        if len(to_local) > 0:
            Log.i(self.TAG, "Upload " + str(len(to_local)) + " food level to local")
            for local in to_local:
                self.update_local(local.get_type(), local.get_level())

        if len(to_remote) > 0:
            Log.i(self.TAG, "Upload " + str(len(to_remote)) + " food level to remote")
            for remote in to_remote:
                self.update_remote(remote)

    def update_remote(self, food_level):
        if self.wifi_conn.is_connected():
            url = (self.UPDATE_URL + "&mac=" + food_level.get_mac() + "&type=" + food_level.get_type() + "&level=" +
                   str(food_level.get_level()) + "&date_create=" + food_level.get_date_create() + "&date_update=" +
                   food_level.get_date_update())
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

    def update_local(self, type, level):
        mac = self.wifi_conn.get_mac()
        food_levels = self.get_local()
        date_now = datetime.today().strftime('%Y-%m-%d %H:%M:%S')

        try:
            for food_level in food_levels:
                if food_level.get_mac() == mac and food_level.get_type() == type and \
                        food_level.get_level() == int(level):
                    # UPDATE
                    sql = ("UPDATE food_level SET level=" + str(level) + ", date_update='" + date_now +
                           "' WHERE mac='" + mac + "'and type='" + type + "'")
                    print(sql)
                    self.cursor.execute(sql)

                    return True

            # INSERT
            sql = ("INSERT INTO food_level(mac, type, level, date_create, date_update) VALUES ('" + mac + "', '" +
                   type + "', " + str(level) + ", '" + date_now + "', '" + date_now + "')")
            print(sql)
            self.cursor.execute(sql)

        except Exception as err:
            Log.e(self.TAG, "Update local: " + str(err))
            return False

        return True

    def get_local(self):
        food_levels = []

        self.cursor.execute("SELECT * FROM food_level WHERE mac='" + self.wifi_conn.get_mac() + "'")
        for db_schedule in self.cursor.fetchall():
            food_level = FoodLevel(
                db_schedule[0],
                db_schedule[1],
                db_schedule[2],
                db_schedule[3],
                db_schedule[4],
            )
            food_levels += [food_level]

        return food_levels

    def get_remote(self):
        food_levels = []
        if self.wifi_conn.is_connected():
            url = self.REQUEST_URL + "&mac=" + self.wifi_conn.get_mac()
            try:
                contents = urllib.request.urlopen(url).read()
                js_cont = json.loads(contents.decode("utf-8"))

                for js_food_level in js_cont['data']:
                    food_level = FoodLevel(
                        js_food_level['mac'],
                        js_food_level['type'],
                        js_food_level['level'],
                        js_food_level['date_create'],
                        js_food_level['date_update'],
                    )
                    food_levels += [food_level]
            except Exception as e:
                Log.e(self.TAG, "Get remote: " + str(e))
                return None

        return food_levels
