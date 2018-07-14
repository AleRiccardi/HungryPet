#include "BluetoothService.h"
#include <SoftwareSerial.h>
#include "JsonConstant.h"

#define rxPin A0
#define txPin A1

SoftwareSerial blueSerial(rxPin, txPin);

void BluetoothService::init(int period) {
  MsgServiceTask::init(period);
  this->excange = ExcangeInfo::getInstance();
  this->active = false;
}

void BluetoothService::tick() {

  // Read data from Bluetooth (Phone App)
  if (this->availableSerial()) {
    this->serialEvent();
    if (this->isMsgAvailable()) {
      Msg* msg = this->receiveMsg();
      this->checkAction(msg->getContent());
      delete msg;
    }
  }

  // Listen data from Serial (Raspberry)
  this->listenSerialMsg();

}

void BluetoothService::checkAction(String content) {
  if (content != "") {
    this->excange->setBluetoothMsg(content);
  }
}

void BluetoothService::listenSerialMsg() {
  if (this->excange->isSerialMsgAvailable()) {
    Msg* msg = this->excange->getSerialMsg();
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

