///////////////////////////
//  ExchangeInfo.cpp
//////////////////////////
#include "ExchangeInfo.h"

ExchangeInfo* ExchangeInfo::instance = 0;

ExchangeInfo::ExchangeInfo() {
  //this->currentMsgToSerial = {NULL};
  this->indexMsgToSerial = -1;
  this->msgBluetooth = "";
  this->availableMsgBluetooth = false;
  this->msgEngine = "";
  this->availableMsgEngine = false;
}

ExchangeInfo* ExchangeInfo::getInstance() {
  if (instance == 0) {
    instance = new ExchangeInfo();
  }
  return instance;
}

//////////////////////////////
//  Set messages
//////////////////////////////

void ExchangeInfo::setToSerialMsg(String message) {
  this->currentMsgToSerial[++this->indexMsgToSerial] = message;
}

void ExchangeInfo::setFromSerialMsg(String message) {
  int existEntity = message.indexOf(ENTITY);
  int existBluetooth = message.indexOf(ENTITY_BLUETOOTH);
  int existEngine = message.indexOf(ENTITY_ENGINE);

  // In the case the entity name has a distance of more than 11
  // to the entity field, it will considered something not usefull.
  int offset = existEntity + 11;

  if (existEntity != -1 && (existBluetooth < offset && existBluetooth != -1)) {
    // BLUETOOTH message stored
    this->msgBluetooth = message;
    this->availableMsgBluetooth = true;
  } else if (existEntity != -1 && (existEngine < offset && existEngine != -1) ) {
    // ENGINE message stored
    this->msgEngine = message;
    this->availableMsgEngine = true;
  }
  
}

//////////////////////////////
//  Check messages
//////////////////////////////

bool ExchangeInfo::isToSerialMsgAvailable() {
  return this->indexMsgToSerial != -1;
}

bool ExchangeInfo::isMsgBluetoothAvailable() {
  return this->availableMsgBluetooth;
}

bool ExchangeInfo::isMsgEngineAvailable() {
  return this->availableMsgEngine;
}

//////////////////////////////
//  Get messages
//////////////////////////////

String ExchangeInfo::getToSerialMsg() {
  if (this->indexMsgToSerial == -1) {
    return "";
  } else {
    String message = currentMsgToSerial[this->indexMsgToSerial--];
    return message;
  }
}

String ExchangeInfo::getMsgBluetooth() {
  if (this->availableMsgBluetooth) {
    String message = this->msgBluetooth;
    this->msgBluetooth = "";
    this->availableMsgBluetooth = false;

    return message;
  } else {
    return "";
  }
}

String ExchangeInfo::getMsgEngine() {
  if (this->availableMsgEngine) {
    String message = this->msgEngine;
    this->msgEngine = "";
    this->availableMsgEngine = false;

    return message;
  } else {
    return "";
  }
}

