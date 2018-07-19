from inc.util.MsgExchange import MsgExchange
from inc.util.log import Log
from builtins import chr

import re
import subprocess
import threading
import serial
import time
import json


class WifiConn(threading.Thread):
    """ Permit to connect to the wifi exchanging
    json information with the phone device """

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
    msg_exc = 0

    def __init__(self):
        threading.Thread.__init__(self)
        self.msg_exc = MsgExchange.get_instance()

    def close_phone_connection(self):
        if self.connected:
            Log.i('Connection closed')
            self.msg_exc.put_to_serial(self.JS_CONN_OFF)
            self.connected = False

        return True

    def run(self):
        """Detect data from paired device."""
        Log.i(self.TAG, 'Thread started')

        # Start the loop
        while self.loop:
            # Read data
            data = self.msg_exc.pop_from_serial(self.TAG)
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
                        Log.i(self.TAG, 'Request to set wifi: ' + ssid)
                        ip_address = self.connect_to_wifi(ssid, pswd)  # Connection

                        if ip_address == self.NOT_SET:
                            Log.i(self.TAG, 'Couldn\'t connect to wifi: ' + ssid )
                            data['action'] = self.A_WIFI_SET
                            data['content'] = {'status': "fail"}
                            json_data = json.dumps(data)
                            self.msg_exc.put_to_serial(json_data)

                        else:
                            self.connected = True
                            Log.i(self.TAG, 'Connected to wifi: ' + ssid)
                            self.send_device_info_to_bt(ip_address)

                    elif data['action'] == self.A_BT_DISCONNECT:
                        """ Closing connection """
                        Log.i(self.TAG, "End of comunication")

                except KeyError as err:
                    Log.e(self.TAG, 'Wrong key access: ' + str(err))
            time.sleep(0.3)
        else:
            Log.i(self.TAG, "Arduino not connected, waiting 10 sec and check again the wired connection")
            time.sleep(10)

    def is_json(self, js_data):
        """Check if it is a Json file."""
        if js_data:
            try:
                json_object = json.loads(js_data)
                if isinstance(json_object, int):
                    return False

                if len(json_object) == 0:
                    return False

            except ValueError as err:
                Log.e(self.TAG, err)
                return False

            return True
        return False

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

        Log.i(self.TAG, 'Scanned wifi: ' + str(all_wifi))
        if all_wifi:
            self.msg_exc.put_to_serial(str(js_response))

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
            self.msg_exc.put_to_serial(json_data)

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

        Log.i(self.TAG, 'Wifi file placed')
        time.sleep(1)

        # Configure the new wifi
        args = ["wpa_cli", "-i", "wlan0", "reconfigure"]
        subprocess.run(args)
        if len(res.decode()) > 0:
            return ip_address
        Log.i(self.TAG, 'Wifi activating ...')
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


class WifiScan:
    """ Permit to scan the wifi """
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
