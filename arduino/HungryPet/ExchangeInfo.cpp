///////////////////////////
//  ExchangeInfo.cpp
//////////////////////////
#include "ExchangeInfo.h"

ExchangeInfo* ExchangeInfo::instance = 0;

ExchangeInfo::ExchangeInfo() {
  //this->currentMsgToSerial = {NULL};
  this->indexMsgToSerial = -1;
  this->msgBluetooth = NULL;
  this->availableMsgBluetooth = false;
  this->msgEngine = NULL;
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

void ExchangeInfo::setToSerialMsg(String msg) {
  this->indexMsgToSerial += 1;
  this->currentMsgToSerial[this->indexMsgToSerial] = new Msg(msg);
}

void ExchangeInfo::setFromSerialMsg(String msg) {
  int existEntity = msg.indexOf(ENTITY);
  int existBluetooth = msg.indexOf(ENTITY_BLUETOOTH);
  int existEngine = msg.indexOf(ENTITY_ENGINE);

  // In the case the entity name has a distance of more than 11 
  // to the entity field, it will considered something not usefull.
  int offset = existEntity + 11;
 
  if (existEntity != -1 && (existBluetooth < offset && existBluetooth != -1)) {
    // BLUETOOTH message stored
    this->msgBluetooth = new Msg(msg);
    this->availableMsgBluetooth = true;
  } else if (existEntity != -1 && (existEngine < offset && existEngine != -1) ) {
    // ENGINE message stored
    this->msgEngine = new Msg(msg);
    this->availableMsgEngine = true;
  }
}

//////////////////////////////
//  Check messages
//////////////////////////////

bool ExchangeInfo::isToSerialMsgAvailable() {
  return this->indexMsgToSerial > -1;
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

Msg* ExchangeInfo::getToSerialMsg() {
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

Msg* ExchangeInfo::getMsgBluetooth() {
  if (this->availableMsgBluetooth) {
    Msg* msg = this->msgBluetooth;
    this->msgBluetooth = NULL;
    this->availableMsgBluetooth = false;

    return msg;
  } else {
    return NULL;
  }
}

Msg* ExchangeInfo::getMsgEngine() {
  if (this->availableMsgEngine) {
    Msg* msg = this->msgEngine;
    this->msgEngine = NULL;
    this->availableMsgEngine = false;

    return msg;
  } else {
    return NULL;
  }
}

