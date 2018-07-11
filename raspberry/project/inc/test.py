import subprocess

ip_address = "none"
args = ["dig", "+short", "myip.opendns.com", "@resolver1.opendns.com"]
# args = ["curl", "-s", "checkip.dyndns.org", "|", "sed", "-e", ",'s/.*Current IP Address: //' -e 's/<.*$//'"]
p = subprocess.Popen(args, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
out, err = p.communicate()
if not len(err) > 0:
    ip_address = out.strip().split(b'<')[0]
print(ip_address)
