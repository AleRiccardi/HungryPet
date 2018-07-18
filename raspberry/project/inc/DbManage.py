from .Colors import Colors
from datetime import datetime
from .data import Schedule
import urllib.request
import MySQLdb
import threading
import json
import time


class DbManage(threading.Thread):
    TAG = 'DbManage'
    REQUEST_URL = 'http://hungrypet.altervista.org/request_data.php?table=schedule&mac=b8:27:eb:44:56:55'
    loop = True
    wifi_conn = 0
    cursor = 0

    def __init__(self, wifi_conn):
        threading.Thread.__init__(self)
        self.wifi_conn = wifi_conn
        db = MySQLdb.connect(host="localhost", user="root", passwd="", db="my_hungrypet")
        self.cursor = db.cursor()

    def create_local_connection(self):
        """ create a database connection to a database that resides
            in the memory
        """

    def run(self):
        self.print_msg("Thread started")
        while self.loop:

            to_insert = []
            to_update = []
            schedules_remote = self.get_remote_schedules()
            schedules_local = self.get_local_schedules()

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
                self.print_msg("Inserted " + str(len(to_insert)) + " schedule")
            if len(to_update) > 0:
                self.print_msg("Updated " + str(len(to_update)) + " schedule")

            self.insert_to_local(to_insert)
            self.update_to_local(to_update)
            time.sleep(3)

    def get_remote_schedules(self):
        schedules = []
        if self.wifi_conn.is_connected():
            try:
                contents = urllib.request.urlopen(self.REQUEST_URL).read()
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
                self.print_e(e)
                return None

    def get_local_schedules(self):
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

    def check_data(self, schedule1, schedule2):
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

    def insert_to_local(self, schedules):
        for schedule in schedules:
            self.cursor.execute(
                "INSERT INTO schedule(id, mac, week_day, hour, date_create, date_update, deleted) "
                "VALUES ('" +
                schedule.get_id() + "', '" +
                schedule.get_mac() + "','" +
                schedule.get_week_day() + "','" +
                schedule.get_hour() + "','" +
                schedule.get_date_create() + "','" +
                schedule.get_date_update() + "','" +
                schedule.get_deleted() + "')"
            )

    def update_to_local(self, schedules):
        for schedule in schedules:
            self.cursor.execute(
                "UPDATE schedule SET "
                "id='" + schedule.get_id() + "', " +
                "mac='" + schedule.get_mac() + "', " +
                "week_day='" + schedule.get_week_day() + "', " +
                "hour='" + schedule.get_hour() + "', " +
                "date_create='" + schedule.get_date_create() + "', " +
                "date_update='" + schedule.get_date_update() + "', " +
                "deleted='" + schedule.get_deleted() + "' " +
                "WHERE id='" + schedule.get_id() + "'"
            )

    def print_msg(self, msg):
        """Print class msg."""
        print(self.TAG + ' ~ ' + str(msg))

    def print_e(self, msg):
        """Print class msg."""
        print(Colors.FAIL + self.TAG + ' ~ ' + str(msg) + Colors.ENDC)
