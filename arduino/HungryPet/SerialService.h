#ifndef __SERIALSERVICE__
#define __SERIALSERVICE__

#include "ExchangeInfo.h"
#include "MsgService.h"

class SerialService : public MsgServiceTask {

  public:
    SerialService() {}
    void init(int period);
    void tick();


  private:
    ExchangeInfo* exchange;
    void startSerial();
    bool availableSerial();
    char readChar();
    void sendMsg(const String & msg);
    void listenToSerialMsg();
    void checkIncomingMsg(String content);
};

#endif
