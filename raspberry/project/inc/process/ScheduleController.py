from inc.util.MsgExchange import MsgExchange
from inc.util.log import Log
import threading
import serial


class ScheduleController(threading.Thread):
    TAG = 'ScheduleController'
    loop = True

    def __init__(self):
        threading.Thread.__init__(self)

    def run(self):
        """Detect data from paired device."""
        Log.i(self.TAG, 'Thread started')
        msg_exc = MsgExchange.get_instance()

        while self.loop:
            data = msg_exc.pop_from_serial(self.TAG)
            if data:
                Log.i(self.TAG, data)
