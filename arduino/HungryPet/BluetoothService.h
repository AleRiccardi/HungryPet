#ifndef __BLUETOOTHSERVICE__
#define __BLUETOOTHSERVICE__

#include "ExcangeInfo.h"
#include "MsgService.h"

class BluetoothService : public MsgServiceTask {

  public:
    BluetoothService() {}
    void init(int period);
    void tick();


  private:
    ExcangeInfo* excange;
    void startSerial();
    bool availableSerial();
    char readChar();
    void sendMsg(const String& msg);
};

#endif
