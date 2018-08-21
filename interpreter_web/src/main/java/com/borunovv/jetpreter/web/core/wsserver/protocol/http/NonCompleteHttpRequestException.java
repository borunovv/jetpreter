package com.borunovv.jetpreter.web.core.wsserver.protocol.http;


public class NonCompleteHttpRequestException extends Exception {
    NonCompleteHttpRequestException(String message) {
        super(message);
    }
}