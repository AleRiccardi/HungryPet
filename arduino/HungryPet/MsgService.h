#ifndef __MSGSERVICE__
#define __MSGSERVICE__

#include "Arduino.h"
#include "Task.h"

class Msg {
    String content;

  public:
    Msg(String content) {
      this->content = content;
    }

    String getContent() {
      return content;
    }
};

class Pattern {
  public:
    virtual boolean match(const Msg& m) = 0;
};

class MsgServiceTask : public Task {
  private:
    String content;

  public:

    Msg* currentMsg;
    bool msgAvailable;

    MsgServiceTask() {}
    virtual void init(int period);

  protected:
    virtual void startSerial() = 0;
    virtual bool availableSerial() = 0;
    virtual char readChar() = 0;
    virtual void sendMsg(const String& msg) = 0;

    void serialEvent();
    bool isMsgAvailable();
    Msg* receiveMsg();
};

#endif

