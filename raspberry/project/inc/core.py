from .process.ScheduleController import ScheduleController
from .process.SerialReader import SerialReader
from .process.WifiConn import WifiConn
from .process.ScheduleDbManage import ScheduleDbManage
from .process.FoodLevelDbManage import FoodLevelDbManage
from .process.InstantFoodDbManage import InstantFoodDbManage
from .util.log import Log

import time
import signal
import sys


class Core:
    TAG = 'Core'
    TIME = 0.1  # seconds
    TIME_CLOSING = 1  # seconds

    # Processes
    loop = True
    closing = False
    serial_reader = 0
    wifi_conn = 0
    db_manage = 0
    schedule_cont = 0
    food_level = 0
    instant_food = 0

    def __init__(self):
        print("\n#### HungryPet #####################################\n")
        print("Welcome to the HungryPet raspberry system,")
        print("here you can see all the status of the process.")
        print("\n**************************************************** \n")

    def run(self):
        self.serial_reader = SerialReader()
        self.wifi_conn = WifiConn()
        self.db_manage = ScheduleDbManage(self.wifi_conn)
        self.schedule_cont = ScheduleController()
        self.food_level = FoodLevelDbManage(self.wifi_conn)
        self.instant_food = InstantFoodDbManage(self.wifi_conn)

        while self.loop:

            # SerialReader
            if not self.serial_reader.isAlive() and not self.closing:
                self.serial_reader = SerialReader()
                self.serial_reader.start()
            elif self.closing is True:
                self.serial_reader.close()

            # WifiConn
            if not self.wifi_conn.isAlive() and not self.closing:
                self.wifi_conn = WifiConn()
                self.wifi_conn.start()
            elif self.closing is True:
                self.wifi_conn.close()

            # DbManage
            if not self.db_manage.isAlive() and not self.closing:
                self.db_manage = ScheduleDbManage(self.wifi_conn)
                self.db_manage.start()
            elif self.closing is True:
                self.db_manage.close()

            # ScheduleController
            if not self.schedule_cont.isAlive() and not self.closing:
                self.schedule_cont = ScheduleController()
                self.schedule_cont.start()
            elif self.closing is True:
                self.schedule_cont.close()

            # FoodLevelDbManage
            if not self.food_level.isAlive() and not self.closing:
                self.food_level = FoodLevelDbManage(self.wifi_conn)
                self.food_level.start()
            elif self.closing is True:
                self.food_level.close()

            # InstantFoodDbManage
            if not self.instant_food.isAlive() and not self.closing:
                self.instant_food = InstantFoodDbManage(self.wifi_conn)
                self.instant_food.start()
            elif self.closing is True:
                self.instant_food.close()

            if self.closing is True:
                # Waiting that all the thread close
                while self.serial_reader.is_running or \
                        self.wifi_conn.is_running or \
                        self.db_manage.is_running or \
                        self.schedule_cont.is_running or \
                        self.food_level.is_running or \
                        self.instant_food.is_running:
                    time.sleep(self.TIME_CLOSING)

                self.loop = False
            else:
                # Sleeping time
                time.sleep(self.TIME)

        Log.g(self.TAG, 'System exit')
        sys.exit(0)

    def print_msg(self, msg):
        """Print class msg."""
        print(self.TAG + ' ~ ' + str(msg))

    def close(self, sig, frame):
        Log.e(self.TAG, 'Closing threads ...')
        self.closing = True
