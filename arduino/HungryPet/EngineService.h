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
    void listenFromSerialMsg();
    void startEngine(String msg);
};

#endif
