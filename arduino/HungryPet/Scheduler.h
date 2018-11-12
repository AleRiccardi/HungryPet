#ifndef __SCHEDULER__
#define __SCHEDULER__

#include "Arduino.h"
#include "Timer.h"
#include "Task.h"
#include "Runnable.h"
#include <avr/sleep.h>
#include <avr/power.h>

#define MAX_TASKS 10

class Scheduler: public Runnable {
  
  int basePeriod;
  int nTasks;
  Task* taskList[MAX_TASKS];  
  Timer* timer;

public:  
  void init(int basePeriod);  
  virtual bool addTask(Task* task);
  virtual void run();
private:
  void sleep();
};

#endif

