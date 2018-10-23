#ifndef  __EXCHANGEINFO__
#define __EXCHANGEINFO__

#include "MsgService.h"

#define ENTITY "entity"
#define ENTITY_BLUETOOTH  "bluetooth"
#define ENTITY_ENGINE  "engine"

class ExchangeInfo {

  private:
    static ExchangeInfo* instance;
    String json_bluetooth = "'action':'bluetooth'";
    String json_engine = "'action':'engine'";

    String currentMsgToSerial[20] = {""};
    int indexMsgToSerial;

    String msgBluetooth;
    bool availableMsgBluetooth;

    String msgEngine;
    bool availableMsgEngine;

  public:
    ExchangeInfo();
    static ExchangeInfo* getInstance();
    // Set messgaes
    void setToSerialMsg(String message);
    void setFromSerialMsg(String message);
    // Check messages
    bool isToSerialMsgAvailable();
    bool isMsgBluetoothAvailable();
    bool isMsgEngineAvailable();
    // Get messages
    String getToSerialMsg();
    String getMsgBluetooth();
    String getMsgEngine();

};

#endif
