///////////////////////////
//  BowlLevelService.cpp
//////////////////////////
#include "BowlLevelService.h"

#define PIN_PRESSURE 2 // analog pin
#define MAX_PRESSURE 800
#define MIN_PRESSURE 0

void BowlLevelService::init(int period) {
  Task::init(period);
  this->exchange = ExchangeInfo::getInstance();
  this->active = false;
  this->timeElapsed = 0;
}

void BowlLevelService::tick() {
  this->checkBowl();
}

void BowlLevelService::checkBowl() {
  int pressureNow = 0;
  int pressurePerc = 0;
  int distancePercAppr = 0;
  int first = 0;
  int second = 0;

  pressureNow = this->readPressureBowl();
  this->putPressureInArray(pressureNow);
  pressurePerc = this->transformPressureToPerc();
  pressurePerc = this->transformPercByFive(pressurePerc);
  this->sendInfoToSerial(pressurePerc);
}

int BowlLevelService::readPressureBowl() {
  String message = "";
  int pressure = analogRead(PIN_PRESSURE);
  return pressure;
}

void BowlLevelService::putPressureInArray(int pressure) {
  int i = 0;
  int tmp = 0, tmp2 = 0;
  int avarage = 0;

  for (i = 0; i < ARRAY_SIZE; i++) {
    tmp2 = this->allLevelBowl[i];
    this->allLevelBowl[i] = tmp;
    tmp = tmp2;
  }

  this->allLevelBowl[0] = pressure;
}

int BowlLevelService::transformPressureToPerc() {
  int sum = 0;
  int i;
  int perc = 0;
  int validDenominator = 0;
  double avg = 0;

  for (i = 0; i < ARRAY_SIZE; i++) {
    if (this->allLevelBowl[i] != 0) {
      sum += this->allLevelBowl[i];
      validDenominator++;
    }
  }

  avg = sum / validDenominator;
  avg = avg > MAX_PRESSURE ? MAX_PRESSURE : avg;
  perc = (avg - MIN_PRESSURE) / (MAX_PRESSURE - MIN_PRESSURE) * 100;
  return perc;
}

int BowlLevelService::transformPercByFive(int perc) {
  int percTransformed = 0, first = 0, second = 0;

  // For the others cases:
  first = perc / 10;
  second = perc % 10;

  // Make number changing by 5 in 5
  if (second > 5) {
    percTransformed = (++first * 10) + 0;
  } else {
    percTransformed = (first * 10) + 5;
  }
  percTransformed -= 5; // adjust value

  return percTransformed;
}

void BowlLevelService::sendInfoToSerial(int value) {
  if (value != this->levelBowlPerc && this->timeStabilizer()) {
    this->levelBowlPerc = value;
    this->exchange->setToSerialMsg("{\"ac\":\"bwl\", \"cn\":\"" + String(value) + "\"}");
  }
}

bool BowlLevelService::timeStabilizer() {
  this->timeElapsed ++;
  // More faster than the container level, that permit to
  // retrive the weight of the food in the bowl and allow in 
  // the feature verison, to stops the engine for the food.
  if (this->timeElapsed >= 5) {
    this->timeElapsed = 0;
    return true;
  } else {
    return false;
  }
}
