// System
#include "Task.h"
#include "Scheduler.h"
// Project
#include "BluetoothService.h"
#include "SerialService.h"
#include "EngineService.h"
#include "ConatinerLevelService.h"
#include "BowlLevelService.h"

Scheduler sched;

void setup() {

  sched.init(20);

  Task* btServ = new BluetoothService();
  Task* serServ = new SerialService();
  Task* engineServ = new EngineService();
  Task* containerServ = new ConatinerLevelService();
  Task* bowlServ = new BowlLevelService();

  btServ->init(20);
  serServ->init(20);
  engineServ->init(20);
  containerServ->init(20);
  bowlServ->init(20);
  
  sched.addTask(btServ);
  sched.addTask(serServ);
  sched.addTask(engineServ);
  sched.addTask(containerServ);
  sched.addTask(bowlServ);
}

void loop() {
  sched.run();
}

