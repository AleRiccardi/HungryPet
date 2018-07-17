#ifndef  __EXCANGEINFO__
#define __EXCANGEINFO__

#include "MsgService.h"

class ExcangeInfo {
  
  private:
    static ExcangeInfo* instance;

    Msg* currentMsgToSerial[20] = {NULL};
    int indexMsgToSerial;

    Msg* currentMsgFromSerial;
    bool availableMsgFromSerial;

  public:
    ExcangeInfo();
    static ExcangeInfo* getInstance();
    void setToSerialMsg(String msg);
    void setFromSerialMsg(String msg);
    bool isToSerialMsgAvailable();
    bool isFromSerialMsgAvailable();
    Msg* getToSerialMsg();
    Msg* getFromSerialMsg();

};

#endif
