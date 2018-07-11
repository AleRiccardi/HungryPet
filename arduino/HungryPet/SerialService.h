#ifndef __SERIALSERVICE__
#define __SERIALSERVICE__

#include "ExcangeInfo.h"
#include "MsgService.h"

class SerialService : public MsgServiceTask {

  public:
    SerialService() {}
    void init(int period);
    void tick();


  private:
    ExcangeInfo* excange;
    void startSerial();
    bool availableSerial();
    char readChar();
    void sendMsg(const String & msg);
};

#endif
