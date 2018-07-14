#include "Task.h"
#include "Scheduler.h"
#include "ExcangeInfo.h"
#include "MsgService.h"
#include "BluetoothService.h"
#include "SerialService.h"


Scheduler sched;

void setup() {

  sched.init(20);

  Task* btServ = new BluetoothService();
  Task* serServ = new SerialService();

  btServ->init(20);
  serServ->init(20);
  sched.addTask(btServ);
  sched.addTask(serServ);

}

void loop() {
  sched.run();
}

