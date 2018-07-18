from .WifiConn import WifiConn
from .DbManage import DbManage


class Core:
    TAG = 'Core'
    wifi_conn = 0
    db_manage = 0

    def __init__(self):
        print("\n####### HungryPet ########\n")
        print("Welcome to the HungryPet raspberry system,")
        print("here you can see all the status of the process.")
        print("\n************************** \n")

    def run(self):
        self.wifi_conn = WifiConn()
        self.db_manage = DbManage(self)

        while True:
            if not self.wifi_conn.isAlive():
                self.wifi_conn = WifiConn()
                self.wifi_conn.start()

            if not self.db_manage.isAlive():
                self.db_manage = DbManage(self.wifi_conn)
                self.db_manage.start()




    def print_msg(self, msg):
        """Print class msg."""
        print(self.TAG + ' ~ ' + str(msg))
