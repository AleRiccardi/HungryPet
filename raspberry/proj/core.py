from .BluetoothConn import BluetoothConn


class Core:

    def __init__(self):
        print("Starting ...")

    def run(self):
        val = False
        btConf = BluetoothConn()

        while not val:
            btConf.bt_connection()
            val = btConf.run()

        print("Finish")
