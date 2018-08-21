package com.borunovv.jetpreter.web.server;

import com.borunovv.jetpreter.web.core.wsserver.nio.RWSession;

/**
 * @author borunovv
 */
public interface IStringMessageHandler {
    void start();
    void stop();
    String handle(String message, RWSession session);
}
