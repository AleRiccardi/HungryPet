from .WifiScan import WifiScan
from .Colors import Colors
from builtins import chr
import subprocess
import threading
import serial
import time
import json


class WifiConn(threading.Thread):
    # ___General strings___
    TAG = 'WifiConn'
    PATH_WIFI = '/etc/wpa_supplicant/wpa_supplicant.conf'
    CMD_SUDO = 'sudo'
    NOT_SET = '<Not Set>'
    REMOTE_SERVER = "www.google.com"

    # ___STATUS___
    A_WIFI_ON = 'conn-on'
    A_WIFI_OFF = 'conn-off'
    A_WIFI_GET = 'wifi-get'
    A_WIFI_SET = 'wifi-set'
    A_BT_DISCONNECT = 'bt-quit'

    # ____JSON____
    JS_CONN_ON = '{ "action":"' + A_WIFI_ON + '"}'
    JS_CONN_OFF = '{ "action":"' + A_WIFI_OFF + '"}'
    JS_WIFI_CONNECTED = '{ "action":"' + A_WIFI_SET + '", "content": "success"}'
    JS_NO_WIFI_CONNECTED = '{ "action":"' + A_WIFI_SET + '", "content": "fail"}'

    # ___VARIABLES___
    # Boolean
    loop = True
    connected = False

    # Other
    serial_ard = 0

    def __init__(self):
        threading.Thread.__init__(self)
        # self.print_msg("Thread initialized")

    def set_connection_with_arduino(self):
        try:
            self.serial_ard = serial.Serial('/dev/ttyACM0', 9600)  # enable the serial port
            return True
        except:
            return False

    def close_phone_connection(self):
        if self.connected:
            self.print_msg('Connection closed')
            self.send_to_serial(self.JS_CONN_OFF)
            self.connected = False

        return True

    def run(self):
        """Detect data from paired device."""
        self.print_msg('Thread started')
        # self.print_msg("Thread started")
        # Check if the wired connection with arduino is available.
        ready = self.set_connection_with_arduino()
        if ready:

            # Start the loop
            while self.loop:
                # Read data
                time.sleep(0.3)
                data = self.read_from_serial()
                if self.is_json(str(data)):
                    data = json.loads(str(data))
                    # selection of action
                    try:
                        if data['action'] == self.A_WIFI_GET:
                            """ Request of wifi """
                            self.send_wifi_to_bt()

                        elif data['action'] == self.A_WIFI_SET:
                            """ Set wifi """
                            ssid = data['content']['ssid']
                            pswd = data['content']['pswd']
                            self.print_msg('Request to set wifi: ' + ssid)
                            ip_address = self.connect_to_wifi(ssid, pswd)  # Connection

                            if ip_address == self.NOT_SET:
                                self.print_msg('Couldn\'t connect to wifi: ' + ssid + '\n')
                                data['action'] = self.A_WIFI_SET
                                data['content'] = {'status': "fail"}
                                json_data = json.dumps(data)
                                self.send_to_serial(json_data)

                            else:
                                self.connected = True
                                self.print_msg('Connected to wifi: ' + ssid + '\n')
                                self.send_device_info_to_bt(ip_address)

                        elif data['action'] == self.A_BT_DISCONNECT:
                            """ Closing connection """
                            self.print_msg("End of comunication")

                    except KeyError as err:
                        self.print_e('Wrong key access: ' + str(err))
                time.sleep(0.2)
        else:
            self.print_msg("Arduino not connected, waiting 10 sec and check again the wired connection")
            time.sleep(10)

    def send_to_serial(self, msg):
        msg += chr(13)
        msg = msg.replace(" ", "")
        self.print_msg(msg)
        self.serial_ard.write(msg.encode())

    def read_from_serial(self):
        msg = self.serial_ard.readline()
        if msg is not "".encode():
            return msg.rstrip().decode()
        else:
            return ""

    def is_json(self, json_p):
        """Check if it is a Json file."""

        try:
            json_object = json.loads(json_p)
            if isinstance(json_object, int):
                return False

            if len(json_object) == 0:
                return False

        except ValueError as err:
            self.print_e(err)
            return False

        return True

    def send_wifi_to_bt(self):
        all_wifi = []
        wifi_sc = WifiScan()
        connect = wifi_sc.scan()
        result = wifi_sc.parse(connect)
        js_response = '{"action":"' + self.A_WIFI_GET + '","content":['
        for wifi in result:
            if wifi['essid'] != "":
                all_wifi += [wifi['essid']]
                js_wifi = '{"ssid":"' + wifi['essid'] + '","encryption":"' + wifi['encryption'] + '"}'
                js_response += '' + js_wifi + ','

        js_response = js_response[:-1]
        js_response += ']}'

        self.print_msg('Scanned wifi: ' + str(all_wifi))
        if all_wifi:
            self.send_to_serial(str(js_response))

    def send_device_info_to_bt(self, ip_address):
        mac_address = self.NOT_SET
        p = subprocess.Popen(['ifconfig', 'eth0'], stdout=subprocess.PIPE,
                             stderr=subprocess.PIPE)

        out, err = p.communicate()

        for l in out.split(b'\n'):
            if l.strip().startswith(b'ether '):
                mac_address = l.strip().split(b'ether ')[1].split(b' ')[0]

        if mac_address != self.NOT_SET and ip_address != self.NOT_SET:
            data = {'action': self.A_WIFI_SET,
                    'content': {'status': "success",
                                'mac': mac_address.decode(),
                                'ip': ip_address.decode()}}
            json_data = json.dumps(data)
            self.send_to_serial(json_data)

    def connect_to_wifi(self, ssid, psk):
        """
        Execute the wifi connection writing the wifi configuration to file
        and trying to establish a connection.
        Return the IP address if success or
        """
        ip_address = self.NOT_SET

        if len(psk) < 8:
            return ip_address

        f = open('wifi.conf', 'w')
        f.write('ctrl_interface=DIR=/var/run/wpa_supplicant GROUP=netdev\n')
        f.write('update_config=1\n')
        f.write('country=US\n')
        f.write('\n')
        f.write('network={\n')
        f.write('    ssid="' + ssid + '"\n')
        f.write('    psk="' + psk + '"\n')
        f.write('    key_mgmt=WPA-PSK\n')
        f.write('}\n')
        f.close()
        time.sleep(1)

        # Place the file
        args = [self.CMD_SUDO, "mv", "-f", "wifi.conf", self.PATH_WIFI]
        res = subprocess.check_output(args)
        if len(res.decode()) > 0:
            return ip_address

        self.print_msg('Wifi file placed')
        time.sleep(1)

        # Configure the new wifi
        args = ["wpa_cli", "-i", "wlan0", "reconfigure"]
        subprocess.run(args)
        if len(res.decode()) > 0:
            return ip_address
        self.print_msg('Wifi activating ...')
        time.sleep(11)

        # Get the new configuration
        args = ["dig", "+short", "myip.opendns.com", "@resolver1.opendns.com"]
        # args = ["curl", "-s", "checkip.dyndns.org", "|", "sed", "-e", ",'s/.*Current IP Address: //' -e 's/<.*$//'"]
        p = subprocess.Popen(args, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        out, err = p.communicate()
        if not len(err) > 0:
            ip_address = out.strip().split(b'<')[0]

        return ip_address

    def is_connected(self):
        import socket
        try:
            # see if we can resolve the host name -- tells us if there is
            # a DNS listening
            host = socket.gethostbyname(self.REMOTE_SERVER)
            # connect to the host -- tells us if the host is actually
            # reachable
            s = socket.create_connection((host, 80), 2)
            self.connected = True
        except:
            self.connected = False

        return self.connected

    def print_msg(self, msg):
        """Print class msg."""
        print(self.TAG + ' ~ ' + str(msg))

    def print_e(self, msg):
        """Print class msg."""
        print(Colors.FAIL + self.TAG + ' ~ ' + str(msg) + Colors.ENDC)
