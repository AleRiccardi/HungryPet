#ifndef __FOODLEVELSERVICE__
#define __FOODLEVELSERVICE__

#include "ExchangeInfo.h"
#include "MsgService.h"
#include "Task.h"
#include <AFMotor.h>

#define ACTION "action"
#define ACTION_ENGINE_START  "engine_start"
#define J_HEAD_BOWL "{'action':'bowl_level', 'content':'"
#define J_TAIL_BOWL "'}"
#define J_HEAD_CONT "{'action':'container_level', 'content':'"
#define J_TAIL_CONT "'}"
#define ARRAY_SIZE 10

class FoodLevelService : public Task {

  public:
    FoodLevelService() {}
    void init(int period);
    void tick();
    bool active;

  private:
    ExchangeInfo* exchange;
    int levelBowlPerc = 0;
    int levelContainerPerc = 0;
    double allLevelBowl[ARRAY_SIZE] = {0.1};
    double allLevelContainer[ARRAY_SIZE] = {0.1};
    void checkBowl();
    void checkContainer();
    double getDistanceBowl();
    double getDistanceContainer();
    void putDistanceInArray(double distance);
    int trasformDistancesToLevelPerc();
    int getTimeFromMeters(double meters);
};



#endif
