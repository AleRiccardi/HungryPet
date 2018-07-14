import sqlite3
from sqlite3 import Error


class DbManage:
    TAG = 'DbManage'
    core = 0

    def __init__(self, core):
        self.core = core

    def create_local_connection(self):
        """ create a database connection to a database that resides
            in the memory
        """
        try:
            conn = sqlite3.connect(':memory:')
            print(sqlite3.version)
        except Error as e:
            print(e)
        finally:
            conn.close()

    def print_msg(self, msg):
        """Print class msg."""
        print(self.TAG + ' ~ ' + str(msg))
