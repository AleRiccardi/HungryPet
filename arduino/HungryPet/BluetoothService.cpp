///////////////////////////
//  BluetoothService.cpp
//////////////////////////
#include "BluetoothService.h"

#define PIN_TX A0
#define PIN_RX A1

SoftwareSerial blueSerial(PIN_TX, PIN_RX);

void BluetoothService::init(int period) {
  MsgServiceTask::init(period);
  this->exchange = ExchangeInfo::getInstance();
  this->active = false;
}

void BluetoothService::tick() {

  // Read data from Bluetooth (Phone App)
  if (this->availableSerial()) {
    this->serialEvent();
    if (this->isMsgAvailable()) {
      String message = this->receiveMsg();
      this->checkIncomingMsg(message);
    }
  }

  // Listen data from Serial (Raspberry)
  this->listenFromSerialMsg();
}

void BluetoothService::checkIncomingMsg(String message) {
  if (message != "") {
    this->exchange->setToSerialMsg(message);
  }
}

void BluetoothService::listenFromSerialMsg() {
  if (this->exchange->isMsgBluetoothAvailable()) {
    String message = this->exchange->getMsgBluetooth();

    this->sendMsg("lenght: " + String(message.length()));
  }
}


// ### Default functions ###

void BluetoothService::startSerial() {
  blueSerial.begin(9600);
}
bool BluetoothService::availableSerial() {
  return blueSerial.available();
}
char BluetoothService::readChar() {
  return blueSerial.read();
}
void BluetoothService::sendMsg(const String & message) {
  message += (char)4;
  blueSerial.print(message);
}
