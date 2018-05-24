import bluetooth
import time
import os
import json
import RPi.GPIO as GPIO
import subprocess
from .WifiScan import WifiScan

class BluetoothConn:
    TAG = 'BlueWifiConf - '
    PATH_WIFI = '/etc/wpa_supplicant/wpa_supplicant.conf'
    CMD_SUDO = 'sudo '

    NOT_SET = '<Not Set>'

    A_WIFI_GET = 'wifi-get'
    A_WIFI_SET = 'wifi-set'
    A_BT_DISCONNECT = 'bt-quit'
    S_WIFI_CONNECTION = 'e_no_wifi_connection'
    E_NO_WIFI_CONNECTION = 'e_no_wifi_connection'
    JS_WIFI_CONNECTED = '{ "action": "' + S_WIFI_CONNECTION + '", "content": "none" }'
    JS_NO_WIFI_CONNECTED = '{ "action": "' + E_NO_WIFI_CONNECTION + '", "content": "none" }'

    bluectl = 0
    networks = 0
    socket = 0
    connection = 0
    port = 0
    address = 0
    work = True

    def bt_connection(self):
        """Connection to bluetooth device."""
        GPIO.setmode(GPIO.BOARD)
        GPIO.setup(40, GPIO.OUT)
        GPIO.setwarnings(False)
        self.socket = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
        self.port = 1
        self.socket.bind(('', self.port))
        self.socket.listen(1)
        self.print_msg('Listening fot connection')
        self.connection, self.address = self.socket.accept()
        # CONNECTED #
        self.print_msg('Accepted connection from ' + str(self.address))

    def run(self):
        """Detect data from paired device."""
        try:
            while self.work:
                data = self.connection.recv(1024)
                data = data.decode()
                data = str(data.replace('\\r\\n', ''))
                print(data)

                if self.is_json(str(data)):
                    data = json.loads(str(data))
                    self.print_msg(data)
                    # selection of action
                    try:
                        if data['action'] == self.A_WIFI_GET:
                            # @todo Send back a list of wifi
                            self.send_wifi_to_bt()

                        elif data['action'] == self.A_WIFI_SET:
                            ssid = data['content']['ssid']
                            pswd = data['content']['pswd']
                            ip_address = self.connect_to_wifi(ssid, pswd)
                            if ip_address != self.E_NO_WIFI_CONNECTION:
                                self.print_msg('Connected to SSID: ' + ssid)
                                self.connection.send(self.JS_WIFI_CONNECTED)
                            else:
                                self.print_msg('Couldn\'t connect to SSID: ' + ssid)
                                self.connection.send(self.JS_NO_WIFI_CONNECTED)

                        elif data['action'] == self.A_BT_DISCONNECT:
                            # @todo Close the connection
                            self.print_msg('Connection closed')

                    except KeyError as err:
                        self.print_msg('Wrong key access: ' + str(err))

        except bluetooth.btcommon.BluetoothError as err:
            self.print_msg('error: ' + str(err))
            return False
        return True

    def is_json(self, json_p):
        """Check if it is a Json file."""

        try:
            json_object = json.loads(json_p)
            if isinstance(json_object, int):
                return False

            if len(json_object) == 0:
                return False
        except ValueError as err:
            self.print_msg(err)
            return False
        return True

    def send_wifi_to_bt(self):
        wifi_sc = WifiScan()
        connect = wifi_sc.scan()
        result = wifi_sc.parse(connect)
        js_response = '{ "action": "' + self.A_WIFI_GET + '", "content": ['
        for wifi in result:
            js_wifi = '{ "ssid": "' + wifi['essid'] + '", "encryption": "' + wifi['encryption'] + '"}'
            js_response += ' ' + js_wifi + ','

        js_response = js_response[:-1]
        js_response += ']}'
        print(js_response)
        self.connection.send(str(js_response))

    def connect_to_wifi(self, ssid, psk):
        """
        Execute the wifi connection writing the wifi configuration to file
        and trying to establish a connection.
        Return the IP address if success or
        """

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

        cmd = self.CMD_SUDO + ' mv wifi.conf ' + self.PATH_WIFI
        cmd_result = os.system(cmd)
        self.print_msg('Wifi file placed:' + str(cmd_result))
        time.sleep(1)

        cmd = 'wpa_cli -i wlan0 reconfigure'
        cmd_result = os.system(cmd)
        self.print_msg('Wifi activation ...' + str(cmd_result))
        time.sleep(15)

        p = subprocess.Popen(['ifconfig', 'wlan0'], stdout=subprocess.PIPE,
                             stderr=subprocess.PIPE)

        out, err = p.communicate()
        ip_address = self.NOT_SET

        for l in out.split(b'\n'):
            if l.strip().startswith(b'inet '):
                ip_address = l.strip().split(b'inet ')[1].split(b' ')[0]

        return ip_address

    def print_msg(self, msg):
        """Print class msg."""
        print(self.TAG + str(msg))