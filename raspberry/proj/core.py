from .bt import BlueWifiConf


class Core:

    def __init__(self):
        print("Starting ...")

    def run(self):
        val = False
        btConf = BlueWifiConf()

        while not val:
            btConf.connect()
            val = btConf.listen_data()

        print("Finish")
