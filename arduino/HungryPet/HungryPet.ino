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

  sched.init(50);

  Task* btServ = new BluetoothService();
  Task* serServ = new SerialService();
  Task* engineServ = new EngineService();
  Task* containerServ = new ConatinerLevelService();
  Task* bowlServ = new BowlLevelService();

  btServ->init(50);
  serServ->init(50);
  engineServ->init(50);
  containerServ->init(50);
  bowlServ->init(50);
  sched.addTask(btServ);
  sched.addTask(serServ);
  sched.addTask(engineServ);
  //sched.addTask(containerServ);
  sched.addTask(bowlServ);
}

void loop() {
  sched.run();
}

