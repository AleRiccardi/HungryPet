#ifndef __ENGINESERVICE__
#define __ENGINESERVICE__

#include "ExchangeInfo.h"
#include "MsgService.h"
#include "Task.h"
#include <AFMotor.h>

#define ACTION "action"
#define ACTION_ENGINE_START  "engine_start"

class EngineService : public Task {

  public:
    EngineService() {}
    void init(int period);
    void tick();
    bool active;

  private:
    ExchangeInfo* exchange;
    bool engineOn = false;
    bool engineRun = false;
    int defaultTimeFood = 3000; // 3 seconds
    long previousMillis = 0;
    void listenFromSerialMsg();
    void engineManage();
};

#endif
