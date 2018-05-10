import bluetooth
import time
import os
import json
import RPi.GPIO as GPIO
import re
import subprocess


class BlueWifiConf:
    TAG = "BlueWifiConf - "
    MSG_VALID = ['wifi', 'disconnect']
    PATH_WIFI = "/etc/wpa_supplicant/wpa_supplicant.conf"
    CMD_SUDO = "sudo "

    A_WIFI_GET = 'wifi-get'
    A_WIFI_CONNECTION = "wifi-connection";
    A_BT_DISCONNECT = 'bt-quit'

    bluectl = 0
    networks = 0
    socket = 0
    connection = 0
    port = 0
    address = 0

    work = True

    def __init__(self):
        self.print_msg("BlueWifiConf class instanced ...")

    def connect(self):
        """Connection to bluetooth device."""
        GPIO.setmode(GPIO.BOARD)
        GPIO.setup(40, GPIO.OUT)
        GPIO.setwarnings(False)
        self.socket = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
        self.port = 1
        self.socket.bind(("", self.port))
        self.socket.listen(1)
        self.connection, self.address = self.socket.accept()
        self.print_msg("Accepted connection from " + str(self.address))
        self.networks

    def listen_data(self):
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
                            self.send_wifi()
                        elif data['action'] == self.A_WIFI_CONNECTION:
                            ssid = data['content']['ssid']
                            pswd = data['content']['pswd']
                            self.connect_wifi(ssid, pswd)
                        elif data['action'] == self.A_BT_DISCONNECT:
                            # @todo Close the connection
                            self.print_msg("Connection closed")
                    except KeyError as err:
                        self.print_msg("Couldn't access to json key: " + str(err))

        except bluetooth.btcommon.BluetoothError as err:
            self.print_msg("error: " + str(err))
            return False
        return True

    def is_json(self, mJson):
        """Check if it is a Json file."""

        try:
            json_object = json.loads(mJson)
            if isinstance(json_object, int):
                return False

            if len(json_object) == 0:
                return False
        except ValueError as err:
            self.print_msg(err)
            return False
        return True

    def send_wifi(self):
        wifiSc = WifiScan()
        contect = wifiSc.scan()
        result = wifiSc.parse(contect)
        js_response = "{ \"action\": \"" + self.A_WIFI_GET + "\", \"content\": ["
        for wifi in result:
            js_wifi = "{ \"ssid\": \"" + wifi['essid'] + "\", \"encryption\": \"" + wifi['encryption'] + "\"}"
            js_response += " " + js_wifi + ","

        js_response = js_response[:-1]
        js_response += "]}"
        print(js_response)
        self.connection.send(str(js_response))

    def connect_wifi(self, ssid, psk):
        """Execute the wifi connection."""
        cmd_result = ""

        # write wifi config to file
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
        self.print_msg("Wifi file placed:" + str(cmd_result))
        time.sleep(1)

        cmd = 'wpa_cli -i wlan0 reconfigure'
        cmd_result = os.system(cmd)
        self.print_msg("Wifi activation ..." + str(cmd_result))
        time.sleep(15)

        p = subprocess.Popen(['ifconfig', 'wlan0'], stdout=subprocess.PIPE,
                             stderr=subprocess.PIPE)

        out, err = p.communicate()
        ip_address = '<Not Set>'

        for l in out.split(b'\n'):
            if l.strip().startswith(b'inet '):
                ip_address = l.strip().split(b'inet ')[1].split(b' ')[0]

        return ip_address

    def is_json(self, mJson):
        """Check if it is a Json file."""

        try:
            json_object = json.loads(mJson)
            if isinstance(json_object, int):
                return False

            if len(json_object) == 0:
                return False
        except ValueError as e:
            return False
        return True

    def is_valid_command(self, command):
        """Check if it is a valid command."""
        if command not in self.CMD_INVALID:
            if re.match("^[a-zA-Z0-9. -]+$", command):
                return True

        return False

    def print_msg(self, msg):
        """Print class msg."""
        print(self.TAG + str(msg))


class WifiScan:
    cellNumberRe = 0
    regexps = 0
    wpaRe = 0
    wpa2Re = 0

    def __init__(self):
        self.cellNumberRe = re.compile(r"^Cell\s+(?P<cellnumber>.+)\s+-\s+Address:\s(?P<mac>.+)$")
        self.regexps = [
            re.compile(r"^ESSID:\"(?P<essid>.*)\"$"),
            re.compile(r"^Protocol:(?P<protocol>.+)$"),
            re.compile(r"^Mode:(?P<mode>.+)$"),
            re.compile(r"^Frequency:(?P<frequency>[\d.]+) (?P<frequency_units>.+) \(Channel (?P<channel>\d+)\)$"),
            re.compile(r"^Encryption key:(?P<encryption>.+)$"),
            re.compile(
                r"^Quality=(?P<signal_quality>\d+)/(?P<signal_total>\d+)\s+Signal level=(?P<signal_level_dBm>.+) d.+$"),
            re.compile(r"^Signal level=(?P<signal_quality>\d+)/(?P<signal_total>\d+).*$"),
        ]
        # Detect encryption type
        self.wpaRe = re.compile(r"IE:\ WPA\ Version\ 1$")
        self.wpa2Re = re.compile(r"IE:\ IEEE\ 802\.11i/WPA2\ Version\ 1$")

    # Runs the comnmand to scan the list of networks.
    # Must run as super user.
    # Does not specify a particular device, so will scan all network devices.
    def scan(self, interface='wlan0'):
        cmd = ["iwlist", interface, "scan"]
        proc = subprocess.Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        points = proc.stdout.read().decode('utf-8')
        return points

    # Parses the response from the command "iwlist scan"
    def parse(self, content):
        cells = []
        lines = content.split('\n')
        for line in lines:
            line = line.strip()
            cellNumber = self.cellNumberRe.search(line)
            if cellNumber is not None:
                cells.append(cellNumber.groupdict())
                continue
            wpa = self.wpaRe.search(line)
            if wpa is not None:
                cells[-1].update({'encryption': 'wpa'})
            wpa2 = self.wpa2Re.search(line)
            if wpa2 is not None:
                cells[-1].update({'encryption': 'wpa2'})
            for expression in self.regexps:
                result = expression.search(line)
                if result is not None:
                    if 'encryption' in result.groupdict():
                        if result.groupdict()['encryption'] == 'on':
                            cells[-1].update({'encryption': 'wep'})
                        else:
                            cells[-1].update({'encryption': 'off'})
                    else:
                        cells[-1].update(result.groupdict())
                    continue
        return cells
