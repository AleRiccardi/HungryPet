from ..util.log import Log


class MsgExchange:
    TAG = 'MsgExchange'
    # Here will be the instance stored.
    __instance = None
    reader = ['WifiConn', 'ScheduleDbManage', 'ScheduleController', 'FoodLevelDbManage', 'InstantFoodDbManage']
    messages_from_serial = []
    messages_to_serial = []

    def __init__(self):
        """ Virtually private constructor. """
        if MsgExchange.__instance is None:
            MsgExchange.__instance = self

        else:
            raise Exception("This class is a singleton!")

    @staticmethod
    def get_instance():
        """ Static access method. """
        if MsgExchange.__instance is None:
            MsgExchange()
        return MsgExchange.__instance

    def put_to_serial(self, msg):
        self.messages_to_serial += [msg]

    def pop_to_serial(self):
        msg_ret = ''
        if self.messages_to_serial:
            msg = self.messages_to_serial.pop(0)
            if msg:
                msg_ret = msg
        return msg_ret

    def put_from_serial(self, msg):
        """
        Put the message from serial to the array "messages_from_serial"
        that keep stored all the messages.
        :param msg: the message the has to be stored
        :return: no return
        """
        if msg:
            dic = dict()
            # insert in the dictionary the message for every class.
            for num_r in range(len(self.reader)):
                dic[self.reader[num_r]] = msg
            # now add to the main array.
            self.messages_from_serial += [dic]

    def pop_from_serial(self, tag_cass):
        """
        Retrieve the last message received from the serial for the right
        destination (class).
        :param tag_cass: the name of the class that want to retrieve the
            message.
        :return: the string message
        """
        msg_received = ''
        if tag_cass in self.reader:
            for i in range(len(self.messages_from_serial)):
                try:
                    msg_cur = self.messages_from_serial[i][tag_cass]
                    if msg_cur:
                        msg_received = msg_cur
                        del self.messages_from_serial[i][tag_cass]
                except:
                    pass
        else:
            Log.w(self.TAG, tag_cass + " not allowed to listen for messages")

        self.messages_from_serial = [i for i in self.messages_from_serial if i]
        return msg_received
