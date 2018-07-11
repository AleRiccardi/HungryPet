#include "Arduino.h"
#include "SerialService.h"

String cmd2 = "ping";

void SerialService::init(int period) {
  MsgServiceTask::init(period);
  this->excange = ExcangeInfo::getInstance();
}

void SerialService::tick() {
  this->serialEvent();
  
  if (this->isMsgAvailable()) {
    Msg* msg = this->receiveMsg();
    String message = msg->getContent();

    if (message != "") {
      this->excange->setSerialMsg(message);
    }
    delete msg;
  }

  if(this->excange->isBluetoothMsgAvailable()){
    Msg* msg = this->excange->getBluetoothMsg();
    String message = msg->getContent();

    if (message != "") {
      this->sendMsg(message);
    }
    delete msg;
  }
}


// ### Default functions ###

void SerialService::startSerial() {
  Serial.begin(9600);
  Serial.println("Hello Raspberry");
}
bool SerialService::availableSerial() {
  return Serial.available();
}
char SerialService::readChar() {
  return Serial.read();
}

void SerialService::sendMsg(const String& msg) {
  Serial.println(msg);
}

