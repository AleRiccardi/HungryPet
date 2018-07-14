#ifndef __BLUETOOTHSERVICE__
#define __BLUETOOTHSERVICE__

#include "ExcangeInfo.h"
#include "MsgService.h"

class BluetoothService : public MsgServiceTask {

  public:
    BluetoothService() {}
    void init(int period);
    void tick();
    bool active;

  private:
    ExcangeInfo* excange;
    void startSerial();
    bool availableSerial();
    char readChar();
    void sendMsg(const String& msg);
    void listenSerialMsg();
    void checkAction(String content);
};

#endif
