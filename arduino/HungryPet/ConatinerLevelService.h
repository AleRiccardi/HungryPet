#ifndef __CONTAINERLEVELSERVICE__
#define __CONTAINERLEVELSERVICE__

#include "ExchangeInfo.h"
#include "MsgService.h"
#include "Task.h"

#define ARRAY_SIZE 10

class ConatinerLevelService : public Task {

  public:
    ConatinerLevelService() {}
    void init(int period);
    void tick();
    bool active;

  private:
    ExchangeInfo* exchange;
    int levelContainerPerc;
    double allLevelContainer[ARRAY_SIZE] = {0.1};
    int timeElapsed;
    void checkContainer();
    double readDistanceContainer();
    void putDistanceInArray(double distance);
    int transformDistancesToPerc();
    int transformPercByFive(int perc);
    int getTimeFromMeters(double meters);
    void sendInfoToSerial(int value);
    bool timeStabilizer();

};



#endif
