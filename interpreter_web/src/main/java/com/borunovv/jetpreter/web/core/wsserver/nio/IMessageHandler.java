package com.borunovv.jetpreter.web.core.wsserver.nio;

public interface IMessageHandler<T> {
    void handle(T message);
    void onReject(T message);
    void onError(T message, Exception cause);
    void onError(Exception cause);
}