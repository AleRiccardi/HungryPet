from .process.ScheduleController import ScheduleController
from .process.SerialReader import SerialReader
from .process.WifiConn import WifiConn
from .process.database.ScheduleDbManage import ScheduleDbManage
from .process.database.FoodLevelDbManage import FoodLevelDbManage
from .process.database.InstantFoodDbManage import InstantFoodDbManage
import time


class Core:
    TAG = 'Core'
    TIME = 0.1  # seconds

    # Processes
    serial_reader = 0
    wifi_conn = 0
    db_manage = 0
    schedule_cont = 0
    food_level = 0
    instant_food = 0

    def __init__(self):
        print("\n####### HungryPet ########\n")
        print("Welcome to the HungryPet raspberry system,")
        print("here you can see all the status of the process.")
        print("\n************************** \n")

    def run(self):
        self.serial_reader = SerialReader()
        self.wifi_conn = WifiConn()
        self.db_manage = ScheduleDbManage(self.wifi_conn)
        self.schedule_cont = ScheduleController()
        self.food_level = FoodLevelDbManage(self.wifi_conn)
        self.instant_food = InstantFoodDbManage(self.wifi_conn)

        while True:

            # SerialReader
            if not self.serial_reader.isAlive():
                self.serial_reader = SerialReader()
                self.serial_reader.start()

            # WifiConn
            if not self.wifi_conn.isAlive():
                self.wifi_conn = WifiConn()
                self.wifi_conn.start()

            # DbManage
            if not self.db_manage.isAlive():
                self.db_manage = ScheduleDbManage(self.wifi_conn)
                self.db_manage.start()

            # ScheduleController
            if not self.schedule_cont.isAlive():
                self.schedule_cont = ScheduleController()
                self.schedule_cont.start()

            # FoodLevelDbManage
            if not self.food_level.isAlive():
                self.food_level = FoodLevelDbManage(self.wifi_conn)
                self.food_level.start()

            # InstantFoodDbManage
            if not self.instant_food.isAlive():
                self.instant_food = InstantFoodDbManage(self.wifi_conn)
                self.instant_food.start()
            # Sleeping time
            time.sleep(self.TIME)

    def print_msg(self, msg):
        """Print class msg."""
        print(self.TAG + ' ~ ' + str(msg))
