package com.borunovv.jetpreter.web.core.wsserver.nio;

import com.borunovv.jetpreter.web.core.wsserver.protocol.websocket.WSMessage;

public interface IWSMessageHandler {
    void handle(WSMessage msg);
}
