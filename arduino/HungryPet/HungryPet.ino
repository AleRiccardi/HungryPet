#include "Task.h"
#include "Scheduler.h"
#include "ExchangeInfo.h"
#include "MsgService.h"
#include "BluetoothService.h"
#include "SerialService.h"
#include "EngineService.h"
#include "FoodLevelService.h"


Scheduler sched;

void setup() {

  sched.init(20);

  Task* btServ = new BluetoothService();
  Task* serServ = new SerialService();
  Task* engineServ = new EngineService();
  Task* levelServ = new FoodLevelService();

  btServ->init(20);
  serServ->init(20);
  engineServ->init(20);
  levelServ->init(20);
  sched.addTask(btServ);
  sched.addTask(serServ);
  sched.addTask(engineServ);
  sched.addTask(levelServ);
}

void loop() {
  sched.run();
}

