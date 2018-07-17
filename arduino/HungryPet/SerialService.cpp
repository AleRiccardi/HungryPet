#include "Arduino.h"
#include "SerialService.h"

String cmd2 = "ping";

void SerialService::init(int period) {
  MsgServiceTask::init(period);
  this->excange = ExcangeInfo::getInstance();
}


void SerialService::tick() {
   
  // Read data from Serial (Raspberry)
  if (this->availableSerial()) {
    this->serialEvent();
    if (this->isMsgAvailable()) {
      Msg* msg = this->receiveMsg();
      this->checkAction(msg->getContent());
      delete msg;
    }
  }
  
  // Listen data from Android (Phone App)
  this->listenBluetoothMsg();

}

void SerialService::checkAction(String content) {
  if (content != "") {
    this->excange->setFromSerialMsg(content);
  }
}

void SerialService::listenBluetoothMsg() {
  if(this->excange->isToSerialMsgAvailable()){
    Msg* msg = this->excange->getToSerialMsg();
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

