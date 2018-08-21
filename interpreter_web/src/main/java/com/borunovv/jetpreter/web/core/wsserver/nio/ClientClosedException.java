package com.borunovv.jetpreter.web.core.wsserver.nio;

import java.io.IOException;

class ClientClosedException extends IOException {

    ClientClosedException(String message) {
        super(message);
    }

    ClientClosedException(String message, Throwable cause) {
        super(message, cause);
    }
}