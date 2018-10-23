///////////////////////////
//  MsgService.cpp
//////////////////////////
#include "MsgService.h"

void MsgServiceTask::init(int period) {
  Task::init(period);

  this->startSerial();
  this->content = "";
  this->currentMsg = "";
  this->msgAvailable = false;
}

void MsgServiceTask::serialEvent() {
  /* reading the content */
  while (this->availableSerial()) {
    char ch = (char) this->readChar();
    if (ch == '\n' || ch == '\r' || ch == 13) {
      this->currentMsg = content;
      this->msgAvailable = true;
    } else {
      content += ch;
    }
  }
}

bool MsgServiceTask::isMsgAvailable() {
  return msgAvailable;
}

String MsgServiceTask::receiveMsg() {
  if (msgAvailable) {
    String message = currentMsg;
    msgAvailable = false;
    currentMsg = "";
    content = "";
    return message;
  } else {
    return "";
  }
}

