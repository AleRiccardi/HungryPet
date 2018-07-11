#ifndef  __EXCANGEINFO__
#define __EXCANGEINFO__

#include "MsgService.h"

class ExcangeInfo {
  
  private:
    static ExcangeInfo* instance;

    Msg* currentBtMsg;
    bool btMsgAvailable;

    Msg* currentSerialMsg;
    bool serialMsgAvailable;

  public:
    ExcangeInfo();
    static ExcangeInfo* getInstance();
    void setBluetoothMsg(String msg);
    void setSerialMsg(String msg);
    bool isBluetoothMsgAvailable();
    bool isSerialMsgAvailable();
    Msg* getBluetoothMsg();
    Msg* getSerialMsg();

};

#endif
