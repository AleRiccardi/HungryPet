class Log:

    @staticmethod
    def i(TAG, msg):
        """ Print information log """
        print(TAG + ' ~ ' + str(msg))

    @staticmethod
    def g(TAG, msg):
        """ Print green log """
        print(Colors.OKGREEN + TAG + ' ~ ' + str(msg) + Colors.ENDC)

    @staticmethod
    def b(TAG, msg):
        """ Print blue log """
        print(Colors.OKBLUE + TAG + ' ~ ' + str(msg) + Colors.ENDC)

    @staticmethod
    def w(TAG, msg):
        """ Print warming msg """
        print(Colors.WARNING + TAG + ' ~ ' + str(msg) + Colors.ENDC)

    @staticmethod
    def e(TAG, msg):
        """Print error msg."""
        print(Colors.FAIL + TAG + ' ~ ' + str(msg) + Colors.ENDC)


class Colors:
    HEADER = '\033[95m'
    OKBLUE = '\033[94m'
    OKGREEN = '\033[92m'
    WARNING = '\033[93m'
    FAIL = '\033[91m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'
    UNDERLINE = '\033[4m'
