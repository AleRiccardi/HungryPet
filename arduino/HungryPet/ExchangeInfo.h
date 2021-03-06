#ifndef  __EXCHANGEINFO__
#define __EXCHANGEINFO__

#include "MsgService.h"

#define ENTITY "en"
#define ENTITY_BLUETOOTH "bt"
#define ENTITY_ENGINE "eng"

class ExchangeInfo {

  private:
    static ExchangeInfo* instance;
    String json_bluetooth = "'ac':'bt'";
    String json_engine = "'ac':'eng'";

    Msg* currentMsgToSerial[20] = {NULL};
    int indexMsgToSerial;

    Msg* msgBluetooth;
    bool availableMsgBluetooth;

    Msg* msgEngine;
    bool availableMsgEngine;

  public:
    ExchangeInfo();
    static ExchangeInfo* getInstance();
    // Set messgaes
    void setToSerialMsg(String msg);
    void setFromSerialMsg(String msg);
    // Check messages
    bool isToSerialMsgAvailable();
    bool isMsgBluetoothAvailable();
    bool isMsgEngineAvailable();
    // Get messages
    Msg* getToSerialMsg();
    Msg* getMsgBluetooth();
    Msg* getMsgEngine();

};

#endif
