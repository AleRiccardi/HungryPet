#include "Arduino.h"
#include "MsgService.h"

String cmd = "ping";

void MsgServiceTask::init(int period) {
  Task::init(period);

  this->startSerial();
  this->content = "";
  this->currentMsg = NULL;
  this->msgAvailable = false;
}

void MsgServiceTask::tick() {
  this->serialEvent();

  if (this->isMsgAvailable()) {
    Msg* msg = this->receiveMsg();
    String mes = msg->getContent();

    if (mes == cmd) {
      this->sendMsg("pong");

    }
    delete msg;
  }
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

bool MsgServiceTask::isMsgAvailable(Pattern& pattern) {
  return (msgAvailable && pattern.match(*currentMsg));
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

Msg* MsgServiceTask::receiveMsg(Pattern& pattern) {
  if (msgAvailable && pattern.match(*currentMsg)) {
    Msg* msg = currentMsg;
    msgAvailable = false;
    currentMsg = NULL;
    content = "";
    return msg;
  } else {
    return NULL;
  }

}

