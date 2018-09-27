///////////////////////////
//  ConatinerLevelService.cpp
//////////////////////////
#include "ConatinerLevelService.h"

#define ACTION "action"
#define ACTION_ENGINE_START  "engine_start"
#define J_HEAD_BOWL "{'action':'bowl_level', 'content':'"
#define J_TAIL_BOWL "'}"
#define J_HEAD_CONT "{'action':'container_level', 'content':'"
#define J_TAIL_CONT "'}"

#define PIN_TRIG 10
#define PIN_ECHO 11
#define MAX_DIST 1.0
#define MAX_DIST_CONT 0.3
#define MIN_DIST_CONT 0.0

void ConatinerLevelService::init(int period) {
  Task::init(period);
  this->exchange = ExchangeInfo::getInstance();
  this->active = false;

  pinMode(PIN_TRIG, OUTPUT);
  pinMode(PIN_ECHO, INPUT);
}

void ConatinerLevelService::tick() {
  this->checkContainer();
}

void ConatinerLevelService::checkContainer() {
  double distanceNow = 0;
  int distancePerc = 0;
  int first = 0;
  int second = 0;

  distanceNow = readDistanceContainer();
  putDistanceInArray(distanceNow);
  distancePerc = transformDistancesToPerc();
  distancePerc = transformPercByFive(distancePerc);
  sendInfoToSerial(distancePerc);
}

double ConatinerLevelService::readDistanceContainer() {
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

void ConatinerLevelService::putDistanceInArray(double distance) {
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

int ConatinerLevelService::transformDistancesToPerc() {
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

int ConatinerLevelService::transformPercByFive(int perc) {
  int percTransformed = 0, first = 0, second = 0;

  if (perc == 100) {
    // 100 correspond to infinte
    percTransformed = 0;
  } else {
    // For the others cases:
    first = perc / 10;
    second = perc % 10;

    // Make number changing by 5 in 5
    if (second > 5) {
      percTransformed = (++first * 10) + 0;
    } else {
      percTransformed = (first * 10) + 5;
    }
    percTransformed += 5; // Adjust value
  }
  return percTransformed;
}

int ConatinerLevelService::getTimeFromMeters(double meters) {
  return ((meters * 2) / 0.0343) * 100;
}

void ConatinerLevelService::sendInfoToSerial(int value) {
  if (value != levelContainerPerc) {
    levelContainerPerc = value;
    this->exchange->setToSerialMsg("{'action':'container_level', 'content':'" + String(value) + "'}");
  }
}

