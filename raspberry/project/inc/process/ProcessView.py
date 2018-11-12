from ..util.log import Log
import threading
import MySQLdb
import RPi.GPIO as GPIO
import time
import os


class ProcessView(threading.Thread):
    TAG = 'ProcessView'
    TIME = 0.2  # seconds
    LED = 17
    BUTTON = 23
    loop = True
    is_running = True
    wifi_conn = 0
    cursor = 0
    reboot = False

    def __init__(self):
        threading.Thread.__init__(self)
        GPIO.setmode(GPIO.BCM)  # Set the board mode to numbers pins by physical location
        GPIO.setup(self.LED, GPIO.OUT)  # Set pin mode as output
        GPIO.output(self.LED, GPIO.HIGH)  # Set pin to high(+3.3V) to
        GPIO.setup(self.BUTTON, GPIO.IN, pull_up_down=GPIO.PUD_UP)

    def close(self):
        self.loop = False
        GPIO.output(self.LED, GPIO.HIGH)  # led off
        GPIO.remove_event_detect(self.BUTTON)
        GPIO.cleanup()

    def run(self):
        Log.i(self.TAG, "Thread started")
        while self.loop:
            # set an interrupt on a falling edge and wait for it to happen
            GPIO.wait_for_edge(self.BUTTON, GPIO.FALLING)
            time.sleep(0.005)  # debounce for 5mSec
            # only show valid edges
            if GPIO.input(self.BUTTON) == 0:
                Log.i(self.TAG, 'Button pressed')
                self.reboot = True

        Log.i(self.TAG, 'Thread closed')
        self.is_running = False

    def shall_reboot(self):
        return self.reboot
