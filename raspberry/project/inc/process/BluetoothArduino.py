# Code for Raspberry PI
import serial

ser = serial.Serial('/dev/ttyACM0', 9600)  # enable the serial port
while 1:  # execute the loop forever
    msg = ser.readline()
    msg = msg.rstrip().decode()

    if msg == "ping":
        ser.write("pong\n\r".encode())
    else:
        print(msg)  # print the serial data sent by UNO
