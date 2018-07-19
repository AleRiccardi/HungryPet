class Log:

    @staticmethod
    def i(TAG, msg):
        """Print class msg."""
        print(TAG + ' ~ ' + str(msg))

    @staticmethod
    def e(TAG, msg):
        """Print class msg."""
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
