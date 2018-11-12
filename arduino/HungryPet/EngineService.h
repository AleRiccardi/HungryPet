#ifndef __ENGINESERVICE__
#define __ENGINESERVICE__

#include "ExchangeInfo.h"
#include "MsgService.h"
#include "Task.h"

#define ACTION "ac"
#define ACTION_ENGINE_START  "engine_start"

class EngineService : public Task {

  public:
    EngineService() {}
    void init(int period);
    void tick();
    bool active;

  private:
    bool engineOn;
    bool engineRunning;
    bool engineOff;
    unsigned long previousMillis = 0;
    const long interval = 5000; // 5 sec
    
    ExchangeInfo* exchange;
    void listenFromSerialMsg();
    void checkEngine();
};

#endif
