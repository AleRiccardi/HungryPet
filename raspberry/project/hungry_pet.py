#!/usr/bin/env python3
from inc.core import Core
import signal

if __name__ == '__main__':
    core = Core()
    signal.signal(signal.SIGINT, core.close)
    core.run()
