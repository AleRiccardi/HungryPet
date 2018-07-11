from .WifiConn import WifiConn


class Core:

    def __init__(self):
        print("\n####### HungryPet ########\n")
        print("Welcome to the HungryPet raspberry system,")
        print("here you can see all the status of the process.")
        print("\n************************** \n")

    def run(self):
        val = False
        wifi_conn = WifiConn()

        while not val:
            wifi_conn.bt_connection()
            val = wifi_conn.run()

        print("Finish")
