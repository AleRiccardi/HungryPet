///////////////////////////
//  SerialService.cpp
//////////////////////////
#include "SerialService.h"

String cmd2 = "ping";

void SerialService::init(int period) {
  MsgServiceTask::init(period);
  this->exchange = ExchangeInfo::getInstance();
}


void SerialService::tick() {
  // Read incoming msg from Serial (Raspberry)
  if (this->availableSerial()) {
    this->serialEvent();
    if (this->isMsgAvailable()) {
      Msg* msg = this->receiveMsg();
      this->checkIncomingMsg(msg->getContent());
      delete msg;
    }
  }
  
  // Listen data from Android (Phone App)
  this->listenToSerialMsg();

}

void SerialService::checkIncomingMsg(String content) {
  if (content != "") {
    this->exchange->setFromSerialMsg(content);
  }
}

void SerialService::listenToSerialMsg() {
  if(this->exchange->isToSerialMsgAvailable()){
    Msg* msg = this->exchange->getToSerialMsg();
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

