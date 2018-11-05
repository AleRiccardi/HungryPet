from ..util.MsgExchange import MsgExchange
from ..util.models import Schedule
from ..util.log import Log
from ..util.variables import JsonVar
import threading
import datetime
import MySQLdb
import time


class ScheduleController(threading.Thread):
    TAG = 'ScheduleController'
    TIME = 5  # seconds
    loop = True
    is_running = True
    wifi_conn = 0
    cursor = 0

    # ____JSON____
    JS_ENGINE_START = "{'en':'" + JsonVar.ENTITY_ENGINE + "','ac':'" + JsonVar.ACTION_ENGINE_START + "'}"

    def __init__(self):
        threading.Thread.__init__(self)
        db = MySQLdb.connect(host="localhost", user="root", passwd="", db="my_hungrypet")
        self.cursor = db.cursor()

    def close(self):
        self.loop = False

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

            schedules = self.get_local_schedules()
            for schedule in schedules:
                # If the schedule was deleted, it has to be ignored
                if schedule.deleted is True:
                    continue
                schedule_week_day = int(schedule.get_week_day())
                schedule_hour = int(schedule.get_hour() / 100)
                schedule_min = int(schedule.get_hour() % 100)

                if (schedule_week_day == week_day_now and schedule_hour == hour_now
                        and schedule_min == min_now and not food_done):
                    # Give the food
                    food_done = True
                    msg_exc.put_to_serial(self.JS_ENGINE_START)

            # Sleeping time
            time.sleep(self.TIME)

        Log.i(self.TAG, 'Thread closed')
        self.is_running = False

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
