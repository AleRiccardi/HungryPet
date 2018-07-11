#include "BluetoothService.h"
#include <SoftwareSerial.h>

#define rxPin A0
#define txPin A1

SoftwareSerial blueSerial(rxPin, txPin);

void BluetoothService::init(int period) {
  MsgServiceTask::init(period);
  this->excange = ExcangeInfo::getInstance();
}

void BluetoothService::tick() {
  this->serialEvent();
  
  if (this->isMsgAvailable()) {
    Msg* msg = this->receiveMsg();
    String message = msg->getContent();

    if (message != "") {
      this->excange->setBluetoothMsg(message);
    }
    delete msg;
  }

  if(this->excange->isSerialMsgAvailable()){
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
  blueSerial.println("Hello Bluetooth");
}
bool BluetoothService::availableSerial() {
  return blueSerial.available();
}
char BluetoothService::readChar() {
  return blueSerial.read();
}

void BluetoothService::sendMsg(const String& msg) {
  blueSerial.println(msg);
}

