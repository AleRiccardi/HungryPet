#ifndef  __EXCHANGEINFO__
#define __EXCHANGEINFO__

#include "MsgService.h"

#define ENTITY "entity"
#define ENTITY_BLUETOOTH  "bluetooth"
#define ENTITY_ENGINE  "engine"
#define ACTION_ENGINE_START  "engine_start"

class ExchangeInfo {

  private:
    static ExchangeInfo* instance;
    String json_bluetooth = "'action':'bluetooth'";
    String json_engine = "'action':'engine'";

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
