from .process.ProcessView import ProcessView
from .process.ScheduleController import ScheduleController
from .process.SerialReader import SerialReader
from .process.WifiConn import WifiConn
from .process.ScheduleDbManage import ScheduleDbManage
from .process.FoodLevelDbManage import FoodLevelDbManage
from .process.InstantFoodDbManage import InstantFoodDbManage
from .util.log import Log

import time
import os
import sys


class Core:
    TAG = 'Core'
    TIME = 0.1  # seconds
    TIME_CLOSING = 1  # seconds

    # Processes
    loop = True
    closing = False
    reboot = False
    process_view = 0
    serial_reader = 0
    wifi_conn = 0
    schedule_db = 0
    schedule_cont = 0
    food_level = 0
    instant_food = 0

    def __init__(self):
        print("\n#### HungryPet #####################################\n")
        print("Welcome to the HungryPet raspberry system,")
        print("here you can see all the status of the process.")
        print("\n**************************************************** \n")

    def run(self):
        self.process_view = ProcessView()
        self.serial_reader = SerialReader()
        self.wifi_conn = WifiConn()
        self.schedule_db = ScheduleDbManage(self.wifi_conn)
        self.schedule_cont = ScheduleController()
        self.food_level = FoodLevelDbManage(self.wifi_conn)
        self.instant_food = InstantFoodDbManage(self.wifi_conn)

        while self.loop:

            # ProcessView
            if not self.process_view.isAlive() and not self.closing:
                self.process_view = ProcessView()
                self.process_view.start()
            elif self.closing is True:
                self.process_view.close()

            if self.process_view.shall_reboot():
                self.closing = True
                self.reboot = True

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
            if not self.schedule_db.isAlive() and not self.closing:
                self.schedule_db = ScheduleDbManage(self.wifi_conn)
                self.schedule_db.start()
            elif self.closing is True:
                self.schedule_db.close()

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
                        self.schedule_db.is_running or \
                        self.schedule_cont.is_running or \
                        self.food_level.is_running or \
                        self.instant_food.is_running:
                    time.sleep(self.TIME_CLOSING)

                self.loop = False
            else:
                # Sleeping time
                time.sleep(self.TIME)

        if self.reboot:
            Log.g(self.TAG, 'System reboot')
            os.system('sudo shutdown -r now')
        else:
            Log.g(self.TAG, 'System exit')
            sys.exit()

    def print_msg(self, msg):
        """Print class msg."""
        print(self.TAG + ' ~ ' + str(msg))

    def close(self, sig, frame):
        Log.e(self.TAG, 'Closing threads ...')
        self.closing = True
