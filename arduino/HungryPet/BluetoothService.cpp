///////////////////////////
//  BluetoothService.cpp
//////////////////////////
#include "BluetoothService.h"

#define PIN_RX A0
#define PIN_TX A1

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
      Msg* msg = this->receiveMsg();
      this->checkIncomingMsg(msg->getContent());
      delete msg;
    }
  }

  // Listen data from Serial (Raspberry)
  this->listenFromSerialMsg();

}

void BluetoothService::checkIncomingMsg(String content) {
  if (content != "") {
    this->exchange->setToSerialMsg(content);
  }
}

void BluetoothService::listenFromSerialMsg() {
  if (this->exchange->isMsgBluetoothAvailable()) {
    Msg* msg = this->exchange->getMsgBluetooth();
    String message = msg->getContent();

    if (message != "") {
      this->sendMsg(message);
    }
    delete msg;
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

void BluetoothService::sendMsg(const String & msg) {
  msg += (char)4;
  blueSerial.print(msg);
}
