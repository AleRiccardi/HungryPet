///////////////////////////
//  EngineService.cpp
//////////////////////////
#include "EngineService.h"

#define PIN_ENGINE 9

void EngineService::init(int period) {
  Task::init(period);
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
    String message = this->exchange->getMsgEngine();

    int existAction = message.indexOf(ACTION);
    int existEngineStart = message.indexOf(ACTION_ENGINE_START);
    int existEngineStop = -1;
    // In the case the entity name has a distance of more than 11
    // to the entity field, it will considered something not usefull.
    int offset = existAction + 11;

    if (existAction != -1 && existEngineStart != -1) {
      engineOn = true;
    }

    if (existAction != -1 && existEngineStop != -1) {
      engineOn = false;
    }
  }

  this->engineManage();

}

void EngineService::engineManage() {
  if (this->engineOn && this->engineRun == false) {
    this->previousMillis = millis();
    this->engineRun = true;
    analogWrite(PIN_ENGINE, 255);

  }

  if (this->engineOn == false) {
    analogWrite(PIN_ENGINE, 0);
    this->engineRun = false;
  }

  if (this->engineOn) {
    unsigned long currentMillis = millis();

    if ((currentMillis - this->previousMillis >= this->defaultTimeFood) ) {
      // save the last time you blinked the LED
      this->engineOn = false;
    }
  }
}

