#include "ExcangeInfo.h"
#include "MsgService.h"

ExcangeInfo* ExcangeInfo::instance = 0;

ExcangeInfo::ExcangeInfo() {
  this->currentBtMsg = NULL;
  this->btMsgAvailable = false;
  this->currentSerialMsg = NULL;
  this->serialMsgAvailable = false;
}

ExcangeInfo* ExcangeInfo::getInstance() {
  if (instance == 0) {
    instance = new ExcangeInfo();
  }
  return instance;
}

void ExcangeInfo::setBluetoothMsg(String msg) {
  this->currentBtMsg = new Msg(msg);
  this->btMsgAvailable = true;
}

void ExcangeInfo::setSerialMsg(String msg) {
  this->currentSerialMsg = new Msg(msg);
  this->serialMsgAvailable = true;
}

bool ExcangeInfo::isBluetoothMsgAvailable(){
  return this->btMsgAvailable;
}

bool ExcangeInfo::isSerialMsgAvailable(){
  return this->serialMsgAvailable;
}

Msg* ExcangeInfo::getBluetoothMsg() {
  if (btMsgAvailable) {
    Msg* msg = currentBtMsg;
    btMsgAvailable = false;
    currentBtMsg = NULL;
    return msg;
  } else {
    return NULL;
  }
}

Msg* ExcangeInfo::getSerialMsg() {
  if (serialMsgAvailable) {
    Msg* msg = currentSerialMsg;
    serialMsgAvailable = false;
    currentSerialMsg = NULL;
    return msg;
  } else {
    return NULL;
  }
}

