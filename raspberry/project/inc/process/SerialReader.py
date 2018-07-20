from inc.util.MsgExchange import MsgExchange
from inc.util.log import Log
import threading
import serial
import time
import json


class SerialReader(threading.Thread):
    """ Listen for all the messages the arrive from serial
        and write all the messages that read from the class
        MsgExchange """

    TAG = 'SerialReader'
    TIME = 0.1  # seconds
    loop = True
    serial_conn = 0
    read_thread = 0

    def __init__(self):
        threading.Thread.__init__(self)
        self.read_thread = threading.Thread(target=self.serial_read)

    def run(self):
        """ Core of the thread """
        Log.i(self.TAG, 'Thread started')
        msg_exc = MsgExchange.get_instance()
        self.read_thread.start()  # Thread reader started
        while self.loop:
            msg = msg_exc.pop_to_serial()
            if msg:
                self.serial_send(msg)

            # Sleeping time
            time.sleep(self.TIME)

    def set_connection_with_arduino(self):
        try:
            self.serial_conn = serial.Serial('/dev/ttyACM0', 9600)  # Enable the serial port
            return True
        except:
            return False

    def serial_send(self, msg):
        """ Send messages to serial """
        msg += chr(13)
        msg = msg.replace(" ", "")
        Log.g(self.TAG, msg)
        self.serial_conn.write(msg.encode())

    def serial_read(self):
        """ Read messages from serial (secondary thread) """
        msg_exc = MsgExchange.get_instance()
        while True:
            ready = self.set_connection_with_arduino()
            if ready:
                msg = self.serial_conn.readline()
                if msg is not "".encode():
                    try:
                        msg = bytes(msg)
                        if isinstance(msg, bytes):
                            msg = msg.decode()
                        if msg[-1:] == '\n':
                            msg = msg[:-1]

                        if self.is_json(msg):
                            Log.b(self.TAG, msg)
                            msg_exc.put_from_serial(msg)
                    except Exception as e:
                        Log.e(self.TAG, e)

    def is_json(self, js_data):
        """Check if it is a Json file."""
        if js_data:
            try:
                json_object = json.loads(js_data)
                if isinstance(json_object, int):
                    return False

                if len(json_object) == 0:
                    return False

            except ValueError as err:
                Log.e(self.TAG, "Json error: " + str(err) + " \n  Msg: " + str(js_data))
                return False

            return True
        return False
