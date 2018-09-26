#ifndef __FOODLEVELSERVICE__
#define __FOODLEVELSERVICE__

#include "ExchangeInfo.h"
#include "MsgService.h"
#include "Task.h"
#include <AFMotor.h>

#define ARRAY_SIZE 10

class FoodLevelService : public Task {

  public:
    FoodLevelService() {}
    void init(int period);
    void tick();
    bool active;

  private:
    ExchangeInfo* exchange;
    int levelBowlPerc;
    int levelContainerPerc;
    double allLevelBowl[ARRAY_SIZE] = {0.1};
    double allLevelContainer[ARRAY_SIZE] = {0.1};
    void checkBowl();
    void checkContainer();
    double getDistanceBowl();
    double getDistanceContainer();
    void putDistanceInArray(double distance);
    int transformDistancesToLevelPerc();
    int transformPercByFive(int perc);
    int getTimeFromMeters(double meters);
};



#endif
