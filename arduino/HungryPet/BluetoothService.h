#ifndef __BLUETOOTHSERVICE__
#define __BLUETOOTHSERVICE__

#include "ExchangeInfo.h"
#include "MsgService.h"
#include <SoftwareSerial.h>

class BluetoothService : public MsgServiceTask {

  public:
    BluetoothService() {}
    void init(int period);
    void tick();
    bool active;

  private:
    ExchangeInfo* exchange;
    void startSerial(); // from MsgServiceTask
    bool availableSerial(); // from MsgServiceTask
    char readChar(); // from MsgServiceTask
    void sendMsg(const String& msg); // from MsgServiceTask
    void listenFromSerialMsg();
    void checkIncomingMsg(String content);
};

#endif
