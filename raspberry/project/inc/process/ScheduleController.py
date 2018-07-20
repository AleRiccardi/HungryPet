from inc.util.MsgExchange import MsgExchange
from inc.util.data import Schedule
from inc.util.log import Log
import threading
import datetime
import MySQLdb
import time
import serial


class ScheduleController(threading.Thread):
    TAG = 'ScheduleController'
    TIME = 5  # seconds
    loop = True
    wifi_conn = 0
    cursor = 0

    def __init__(self):
        threading.Thread.__init__(self)
        db = MySQLdb.connect(host="localhost", user="root", passwd="", db="my_hungrypet")
        self.cursor = db.cursor()

    def run(self):
        """Detect data from paired device."""
        Log.i(self.TAG, 'Thread started')
        msg_exc = MsgExchange.get_instance()
        food_done = False
        min_old = 0

        while self.loop:
            date_now = datetime.datetime.now()
            week_day_now = int(date_now.weekday())
            hour_now = int(date_now.hour)
            min_now = int(date_now.minute)

            if min_old != min_now:
                min_old = min_now
                food_done = False

            # print("Now: " + str(week_day_now) + " " + str(hour_now) + " " + str(minutes_now))
            schedules = self.get_local_schedules()
            for schedule in schedules:
                schedule_week_day = int(schedule.get_week_day())
                schedule_hour = int(schedule.get_hour() / 100)
                schedule_min = int(schedule.get_hour() % 100)

                # print("Schedule: " + str(schedule_week_day) + " " + str(schedule_hour) + " " + str(schedule_minutes))

                if (schedule_week_day == week_day_now and schedule_hour == hour_now
                        and schedule_min == min_now and not food_done):
                    # Give the food
                    food_done = True
                    msg_exc.put_to_serial("{'action': 'food-time'}")

            # Sleeping time
            time.sleep(self.TIME)

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
