#ifndef __MSGSERVICE__
#define __MSGSERVICE__

#include "Arduino.h"
#include "Task.h"

class MsgServiceTask : public Task {
  private:
    String content;

  public:

    String currentMsg;
    bool msgAvailable;

    MsgServiceTask() {}
    virtual void init(int period);

  protected:
    virtual void startSerial() = 0;
    virtual bool availableSerial() = 0;
    virtual char readChar() = 0;
    virtual void sendMsg(const String& message) = 0;

    void serialEvent();
    bool isMsgAvailable();
    String receiveMsg();
};

#endif

