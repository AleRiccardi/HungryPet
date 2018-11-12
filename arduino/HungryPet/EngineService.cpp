///////////////////////////
//  EngineService.cpp
//////////////////////////
#include "EngineService.h"

#define PIN_ENGINE 9

void EngineService::init(int period) {
  Task::init(period);
  this->exchange = ExchangeInfo::getInstance();
  this->active = false;
  this->engineOn = false;
  this->engineRunning = false;
  this->engineOff = false;
  pinMode(PIN_ENGINE, OUTPUT);
}

void EngineService::tick() {
  // Listen data from Serial (Raspberry)
  this->listenFromSerialMsg();
  this->checkEngine();
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
      engineOn = true;
    }
    delete msg;
  }
}

void EngineService::checkEngine() {
  if (this->engineOn) {
    analogWrite(PIN_ENGINE, 255);
    previousMillis = millis();

    this->engineRunning = true; // it has to run
    this->engineOn = false;
    this->engineOff = false;
  }

  if (this->engineRunning) {
    unsigned long currentMillis = millis();
    if (currentMillis - previousMillis >= interval) {
      this->engineRunning = false;
      this->engineOn = false;
      this->engineOff = true; // it has to stop
    }
  }

  if (this->engineOff) {
    analogWrite(PIN_ENGINE, 0); // it stopped
    this->engineRunning = false;
    this->engineOn = false;
    this->engineOff = false;
  }
}
