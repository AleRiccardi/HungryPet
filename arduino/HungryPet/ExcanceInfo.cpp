#include "ExcangeInfo.h"
#include "MsgService.h"

ExcangeInfo* ExcangeInfo::instance = 0;

ExcangeInfo::ExcangeInfo() {
  //this->currentMsgToSerial = {NULL};
  this->indexMsgToSerial = -1;
  this->currentMsgFromSerial = NULL;
  this->availableMsgFromSerial = false;
}

ExcangeInfo* ExcangeInfo::getInstance() {
  if (instance == 0) {
    instance = new ExcangeInfo();
  }
  return instance;
}

void ExcangeInfo::setToSerialMsg(String msg) {
  this->indexMsgToSerial += 1;
  this->currentMsgToSerial[this->indexMsgToSerial] = new Msg(msg);
}

void ExcangeInfo::setFromSerialMsg(String msg) {
  this->currentMsgFromSerial = new Msg(msg);
  this->availableMsgFromSerial = true;
}

bool ExcangeInfo::isToSerialMsgAvailable() {
  return this->indexMsgToSerial > -1;
}

bool ExcangeInfo::isFromSerialMsgAvailable() {
  return this->availableMsgFromSerial;
}

Msg* ExcangeInfo::getToSerialMsg() {
  if (this->indexMsgToSerial > -1) {
    Msg* msg = currentMsgToSerial[0];

    if (this->indexMsgToSerial > 0) {
      int i = 0;
      Msg* tmp = NULL;
      for (i = indexMsgToSerial; i < 0; i--) {
        tmp = currentMsgToSerial[i - 1];
        currentMsgToSerial[i - 1] = currentMsgToSerial[i];
      }
      this->indexMsgToSerial--;
    } else {
      this->indexMsgToSerial = -1;
    }
    return msg;
  } else {
    return NULL;
  }
}


Msg* ExcangeInfo::getFromSerialMsg() {
  if (availableMsgFromSerial) {
    Msg* msg = currentMsgFromSerial;
    currentMsgFromSerial = NULL;
    availableMsgFromSerial = false;

    return msg;
  } else {
    return NULL;
  }
}

