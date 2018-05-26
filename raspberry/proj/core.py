from .BluetoothConn import BluetoothConn


class Core:

    def __init__(self):
        print("\n####### HungryPet ########\n")
        print("Welcome to the HungryPet raspberry system,")
        print("here you can see all the status of the process.")
        print("\n************************** \n")


    def run(self):
        val = False
        btConf = BluetoothConn()

        while not val:
            btConf.bt_connection()
            val = btConf.run()

        print("Finish")
