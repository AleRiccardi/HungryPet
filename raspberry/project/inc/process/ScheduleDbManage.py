from ..util.models import Schedule
from ..util.log import Log
from datetime import datetime
import urllib.request
import MySQLdb
import threading
import subprocess
import json
import time
import uuid


class ScheduleDbManage(threading.Thread):
    # Main
    TAG = 'ScheduleDbManage'
    REQUEST_URL = 'http://hungrypet.altervista.org/request_data.php?table=schedule&mac='
    TIME = 3  # seconds

    loop = True
    is_running = True
    wifi_conn = 0
    cursor = 0

    def __init__(self, wifi_conn):
        threading.Thread.__init__(self)
        self.wifi_conn = wifi_conn
        db = MySQLdb.connect(host="localhost", user="root", passwd="", db="my_hungrypet")
        self.cursor = db.cursor()

    def close(self):
        self.loop = False

    def run(self):
        Log.i(self.TAG, "Thread started")
        while self.loop:

            to_insert = []
            to_update = []
            schedules_remote = self.get_remote()
            schedules_local = self.get_local()

            if schedules_remote:
                for schedule_r in schedules_remote:
                    if schedule_r in schedules_local:
                        for schedule_l in schedules_local:
                            if schedule_r.get_id() == schedule_l.get_id():
                                if self.check_data(schedule_r, schedule_l) == 1:
                                    to_update += [schedule_r]

                    else:
                        to_insert += [schedule_r]

            if len(to_insert) > 0:
                Log.i(self.TAG, "Inserted " + str(len(to_insert)) + " schedule")
            if len(to_update) > 0:
                Log.i(self.TAG, "Updated " + str(len(to_update)) + " schedule")

            self.insert_local(to_insert)
            self.update_local(to_update)

            # Sleeping time
            time.sleep(self.TIME)

        Log.i(self.TAG, 'Thread closed')
        self.is_running = False

    def get_remote(self):
        schedules = []
        if self.wifi_conn.is_connected():
            url = self.REQUEST_URL + self.wifi_conn.get_mac()
            try:
                contents = urllib.request.urlopen(url).read()
                js_cont = json.loads(contents.decode("utf-8"))

                for js_schedule in js_cont['data']:
                    schedule = Schedule(
                        js_schedule['id'],
                        js_schedule['mac'],
                        js_schedule['week_day'],
                        js_schedule['hour'],
                        js_schedule['date_create'],
                        js_schedule['date_update'],
                        js_schedule['deleted']
                    )
                    schedules += [schedule]

                return schedules
            except Exception as e:
                Log.e(self.TAG, e)
                return None

    def get_local(self):
        schedules = []

        self.cursor.execute("SELECT * FROM schedule")
        for db_schedule in self.cursor.fetchall():
            schedule = Schedule(
                db_schedule[0],
                db_schedule[1],
                db_schedule[2],
                db_schedule[3],
                db_schedule[4],
                db_schedule[5],
                db_schedule[6]
            )
            schedules += [schedule]

        return schedules

    @staticmethod
    def check_data(schedule1, schedule2):
        """ Check the date of two Schedule
            return 1 if the first is newer of the second
        """
        data1 = datetime.strptime(schedule1.get_date_update(), '%Y-%m-%d %H:%M:%S')
        data2 = datetime.strptime(schedule2.get_date_update(), '%Y-%m-%d %H:%M:%S')

        if data1 > data2:
            return 1
        elif data1 < data2:
            return -1
        else:
            return 0

    def insert_local(self, schedules):
        for schedule in schedules:
            self.cursor.execute(
                "INSERT INTO schedule(id, mac, week_day, hour, date_create, date_update, deleted) "
                "VALUES ('" +
                schedule.get_id() + "', '" +
                schedule.get_mac() + "', '" +
                str(schedule.get_week_day()) + "','" +
                str(schedule.get_hour()) + "','" +
                schedule.get_date_create() + "','" +
                schedule.get_date_update() + "','" +
                str(schedule.get_deleted()) + "')"
            )

    def update_local(self, schedules):
        for schedule in schedules:
            self.cursor.execute(
                "UPDATE schedule SET "
                "id='" + schedule.get_id() + "', " +
                "mac='" + schedule.get_mac() + "', " +
                "week_day='" + str(schedule.get_week_day()) + "', " +
                "hour='" + str(schedule.get_hour()) + "', " +
                "date_create='" + schedule.get_date_create() + "', " +
                "date_update='" + schedule.get_date_update() + "', " +
                "deleted='" + schedule.get_deleted() + "' " +
                "WHERE id='" + str(schedule.get_id()) + "'"
            )

    def print_msg(self, msg):
        """Print class msg."""
        print(self.TAG + ' ~ ' + str(msg))

    def print_e(self, msg):
        """Print class msg."""
        print(Colors.FAIL + self.TAG + ' ~ ' + str(msg) + Colors.ENDC)
