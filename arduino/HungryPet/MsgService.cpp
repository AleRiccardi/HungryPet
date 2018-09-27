///////////////////////////
//  MsgService.cpp
//////////////////////////
#include "MsgService.h"

void MsgServiceTask::init(int period) {
  Task::init(period);

  this->startSerial();
  this->content = "";
  this->currentMsg = NULL;
  this->msgAvailable = false;
}

void MsgServiceTask::serialEvent() {
  /* reading the content */
  while (this->availableSerial()) {
    char ch = (char) this->readChar();
    if (ch == '\n' || ch == '\r' || ch == 13) {
      this->currentMsg = new Msg(content);
      this->msgAvailable = true;
    } else {
      content += ch;
    }
  }
}

bool MsgServiceTask::isMsgAvailable() {
  return msgAvailable;
}

Msg* MsgServiceTask::receiveMsg() {
  if (msgAvailable) {
    Msg* msg = currentMsg;
    msgAvailable = false;
    currentMsg = NULL;
    content = "";
    return msg;
  } else {
    return NULL;
  }
}

