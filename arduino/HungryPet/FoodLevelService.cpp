#include "FoodLevelService.h"
#include "JsonConstant.h"

#define PIN_TRIG 10
#define PIN_ECHO 11
#define MAX_DIST 1
#define MAX_DIST_CONT 0.3
#define MIN_DIST_CONT 0.0

void FoodLevelService::init(int period) {
  this->exchange = ExchangeInfo::getInstance();
  this->active = false;
  pinMode(PIN_TRIG, OUTPUT);
  pinMode(PIN_ECHO, INPUT);
}

void FoodLevelService::tick() {
  // Listen data from Serial (Raspberry)
  //this->checkBowl();
  this->checkContainer();
}

void FoodLevelService::checkBowl() {}

void FoodLevelService::checkContainer() {
  String content = "";
  double distanceNow;
  int distancePerc , distancePercAppr, first, second;

  distanceNow = getDistanceContainer();
  putDistanceInArray(distanceNow);
  distancePerc = trasformDistancesToLevelPerc();
  distancePercAppr = 0;
  first = distancePerc / 10;
  second = distancePerc % 10;
  this->exchange->setToSerialMsg(String(distanceNow));

  // Make number changing by 5 in 5
  if (second > 5) {
    distancePercAppr = (++first * 10) + 0;
  } else {
    distancePercAppr = (first * 10) + 5;
  }
  distancePercAppr += 5; // Adjust value

  // If there is a real changing of level, then it's time to update
  if ((int)(distancePercAppr / 5) != (int)(this->levelContainerPerc / 5)) {
    this->levelContainerPerc = distancePercAppr;
    content = J_HEAD_CONT + String(this->levelContainerPerc) + J_TAIL_CONT;
    //this->exchange->setToSerialMsg(content);
  }
}

double FoodLevelService::getDistanceBowl() {}

double FoodLevelService::getDistanceContainer() {
  int timeSignal = 0;
  double distance = 0;
  digitalWrite(PIN_TRIG, LOW);
  delayMicroseconds(2);
  digitalWrite(PIN_TRIG, HIGH);
  delayMicroseconds(10);
  digitalWrite(PIN_TRIG, LOW);
  timeSignal = pulseIn(PIN_ECHO, HIGH, this->getTimeFromMeters(MAX_DIST));
  distance = ((timeSignal * .0343) / 2) / 100;
  return distance;
}

void FoodLevelService::putDistanceInArray(double distance) {
  int i = 0;
  double tmp = 0, tmp2 = 0;
  double avarage = 0;

  for (i = 0; i < ARRAY_SIZE; i++) {
    tmp2 = this->allLevelContainer[i];
    this->allLevelContainer[i] = tmp;
    tmp = tmp2;
  }

  this->allLevelContainer[0] = distance;
}

int FoodLevelService::trasformDistancesToLevelPerc() {
  double sum = 0, avg = 0;
  int i, perc = 0, validDenominator = 0;
  for (i = 0; i < ARRAY_SIZE; i++) {
    if (this->allLevelContainer[i] != 0) {
      sum += this->allLevelContainer[i];
      validDenominator++;
    }
  }

  avg = sum / validDenominator;
  avg = avg > MAX_DIST_CONT ? MAX_DIST_CONT : avg;
  perc = (avg - MIN_DIST_CONT) / (MAX_DIST_CONT - MIN_DIST_CONT) * 100;
  return 100 - perc;
}

int FoodLevelService::getTimeFromMeters(double meters) {
  return ((meters * 2) / 0.0343) * 100;
}

