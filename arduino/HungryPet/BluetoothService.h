#ifndef __BLUETOOTHSERVICE__
#define __BLUETOOTHSERVICE__

#include "ExchangeInfo.h"
#include "MsgService.h"

class BluetoothService : public MsgServiceTask {

  public:
    BluetoothService() {}
    void init(int period);
    void tick();
    bool active;

  private:
    ExchangeInfo* exchange;
    void startSerial();
    bool availableSerial();
    char readChar();
    void sendMsg(const String& msg);
    void listenFromSerialMsg();
    void checkAction(String content);
};

#endif
