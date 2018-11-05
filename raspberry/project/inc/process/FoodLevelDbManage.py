from ..util.MsgExchange import MsgExchange
from ..util.models import FoodLevel
from ..util.log import Log
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
    A_CONTAINER_LEVEL = "cnl"
    A_BOWL_LEVEL = "bwl"
    # Type
    CONTAINER = "container"
    BOWL = "bowl"

    # class var
    loop = True
    is_running = True
    cursor = 0

    # external class
    wifi_conn = 0
    msg_exc = 0

    def __init__(self, wifi_conn):
        """
        Init function.
        :param wifi_conn: WifiConn class.
        """
        threading.Thread.__init__(self)
        db = MySQLdb.connect(host="localhost", user="root", passwd="", db="my_hungrypet")
        self.cursor = db.cursor()
        self.msg_exc = MsgExchange.get_instance()
        self.wifi_conn = wifi_conn

    def close(self):
        self.loop = False

    def run(self):
        """
        Method triggered from thread.
        """
        Log.i(self.TAG, 'Thread started')

        while self.loop:
            self.check_message_from_serial()
            self.sync_remote_to_local()

            # Sleeping time
            time.sleep(self.TIME)

        Log.i(self.TAG, 'Thread closed')
        self.is_running = False

    def check_message_from_serial(self):
        """
        Listen for message that comes from the serial.
        TODO when arrive two messages from serial, only one is read from this class.
        """
        data = self.msg_exc.pop_from_serial(self.TAG)
        if data:
            data = json.loads(str(data))
            # selection of action
            try:
                if data['ac'] == self.A_CONTAINER_LEVEL:
                    """ Request of wifi """
                    content = data['cn']
                    level = int(content)
                    self.update_local(self.CONTAINER, level)

                elif data['ac'] == self.A_BOWL_LEVEL:
                    """ Set wifi """
                    content = data['cn']
                    level = content
                    self.update_local(self.BOWL, level)

            except KeyError as err:
                Log.e(self.TAG, 'Wrong json access: ' + str(err))

    def sync_remote_to_local(self):
        """
        Sync remote information with local.
        """
        if self.wifi_conn.is_connected():
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

            # Log.w(self.TAG, "local: " + str(to_local))

            if len(to_local) > 0:
                Log.i(self.TAG, "Upload " + str(len(to_local)) + " food level to local")
                for local in to_local:
                    self.update_local(local.get_type(), local.get_level())

            if len(to_remote) > 0:
                Log.i(self.TAG, "Upload " + str(len(to_remote)) + " food level to remote")
                for remote in to_remote:
                    self.update_remote(remote)

    def update_remote(self, food_level):
        """
        Send and update information to remote database.
        :param food_level:
        :return:
        """
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

    def update_local(self, type_container, level):
        """
        Update on own local database.
        :param type_container: contain a string where specify the kind of container.
        :param level: contain the value of the level.
        :return: True if worked, False if not.
        """
        mac = self.wifi_conn.get_mac()
        food_levels = self.get_local()
        date_now = datetime.today().strftime('%Y-%m-%d %H:%M:%S')

        try:
            if not food_levels:
                # INSERT
                sql = ("INSERT INTO food_level(mac, type, level, date_create, date_update) VALUES ('" + mac + "', '" +
                       type_container + "', " + str(level) + ", '" + date_now + "', '" + date_now + "')")
                self.cursor.execute(sql)
            else:
                for food_level in food_levels:
                    if food_level.get_mac() == mac and food_level.get_type() == type_container and \
                            food_level.get_level() != int(level):
                        # UPDATE
                        sql = ("UPDATE food_level SET level=" + str(level) + ", date_update='" + date_now +
                               "' WHERE mac='" + mac + "'and type='" + type_container + "'")
                        self.cursor.execute(sql)

                        return True

        except Exception as err:
            Log.e(self.TAG, "Update local: " + str(err))
            return False

        return True

    def get_local(self):
        """
        Get local food level.
        :return: a list of food level.
        """
        food_levels = []
        exit_loop = False
        while not exit_loop:
            try:
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
                    exit_loop = True
            except Warning as warn:
                Log.e(self.TAG, str(warn))
                self.cursor.execute("REPAIR TABLE food_level")
                exit_loop = False

        return food_levels

    def get_remote(self):
        """
        Get remote food level.
        :return: a list of food level.
        """
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
