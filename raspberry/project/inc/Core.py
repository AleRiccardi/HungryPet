from .process.SerialReader import SerialReader
from .process.WifiConn import WifiConn
from .process.DbManage import DbManage
from .process.ScheduleController import ScheduleController
import time


class Core:
    TAG = 'Core'
    TIME = 0.1  # seconds
    serial_reader = 0
    wifi_conn = 0
    db_manage = 0
    schedule_cont = 0

    def __init__(self):
        print("\n####### HungryPet ########\n")
        print("Welcome to the HungryPet raspberry system,")
        print("here you can see all the status of the process.")
        print("\n************************** \n")

    def run(self):
        self.serial_reader = SerialReader()
        self.wifi_conn = WifiConn()
        self.db_manage = DbManage(self)
        self.schedule_cont = ScheduleController()

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
                self.db_manage = DbManage(self.wifi_conn)
                self.db_manage.start()

            # ScheduleController
            if not self.schedule_cont.isAlive():
                self.schedule_cont = ScheduleController()
                self.schedule_cont.start()

            # Sleeping time
            time.sleep(self.TIME)

    def print_msg(self, msg):
        """Print class msg."""
        print(self.TAG + ' ~ ' + str(msg))
