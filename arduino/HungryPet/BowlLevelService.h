#ifndef __BOWLLEVELSERVICE__
#define __BOWLLEVELSERVICE__

#include "ExchangeInfo.h"
#include "MsgService.h"
#include "Task.h"
#include <AFMotor.h>

#define ARRAY_SIZE 10

class BowlLevelService : public Task {

  public:
    BowlLevelService() {}
    void init(int period);
    void tick();
    bool active;

  private:
    ExchangeInfo* exchange;
    int levelBowlPerc = -1;
    int allLevelBowl[ARRAY_SIZE] = {0.1};
    int timeElapsed;
    int timeUpdate;
    void checkBowl();
    int readPressureBowl();
    void putPressureInArray(int distance);
    int transformPressureToPerc();
    int transformPercByFive(int perc);
    void sendInfoToSerial(int value);
    bool timeStabilizer();
    bool timeToUpdate();
};



#endif
