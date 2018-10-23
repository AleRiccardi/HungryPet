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
  // Read incoming message from Serial (Raspberry)
  if (this->availableSerial()) {
    this->serialEvent();
    if (this->isMsgAvailable()) {
      String message = this->receiveMsg();
      this->checkIncomingMsg(message);
    }
  }
  
  // Listen data from Android (Phone App)
  this->listenToSerialMsg();

}

void SerialService::checkIncomingMsg(String message) {
  if (message != "") {
    this->exchange->setFromSerialMsg(message);
    this->sendMsg("length: " + String(message.length()));

  }
}

void SerialService::listenToSerialMsg() {
  if(this->exchange->isToSerialMsgAvailable()){
    String message = this->exchange->getToSerialMsg();

    if (message != "") {
      this->sendMsg(message);
    }
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

void SerialService::sendMsg(const String& message) {
  Serial.println(message);
}

