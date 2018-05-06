import bluetooth
import RPi.GPIO as GPIO

GPIO.setmode(GPIO.BOARD)
GPIO.setup(40, GPIO.OUT)
GPIO.setwarnings(False)

server_socket = bluetooth.BluetoothSocket(bluetooth.RFCOMM)

port = 1
server_socket.bind(("", port))
server_socket.listen(1)

client_socket, address = server_socket.accept()
print("Accepted connection from " + str(address))
try:
    while 1:
        data = client_socket.recv(1024)
        print("Received: " + str(data))
        if data == "0":
            GPIO.output(40, 0)
        if data == "1":
            GPIO.output(40, 0)

finally:
    print("Cleaning Up!")
    GPIO.cleanup()
    client_socket.close()
    server_socket.close()
