#include "EngineService.h"
#include "JsonConstant.h"

#define PIN_ENGINE 9

void EngineService::init(int period) {
  this->exchange = ExchangeInfo::getInstance();
  this->active = false;
  pinMode(PIN_ENGINE, OUTPUT);
}

void EngineService::tick() {
  // Listen data from Serial (Raspberry)
  this->listenFromSerialMsg();
}

void EngineService::listenFromSerialMsg() {
  if (this->exchange->isMsgEngineAvailable()) {
    Msg* msg = this->exchange->getMsgEngine();
    String message = msg->getContent();

    int existAction = message.indexOf(ACTION);
    int existEngineStart = message.indexOf(ACTION_ENGINE_START);
    // In the case the entity name has a distance of more than 11 
    // to the entity field, it will considered something not usefull.
    int offset = existAction + 11;
   
    if (existAction != -1 && existEngineStart != -1) {
      this->startEngine(message);
    }
    delete msg;
  }
}

void EngineService::startEngine(String msg){
  analogWrite(PIN_ENGINE, 255);
  delay(5000);  
  analogWrite(PIN_ENGINE, 0);
}

