package com.borunovv.jetpreter.interpreter;

public class CancelException extends InterpretException {

    public CancelException(int lineNumber, String msg, Exception e) {
        super(lineNumber, msg, e);
    }

    public CancelException(int lineNumber, String msg) {
        super(lineNumber, msg, null);
    }

    public CancelException(String msg, Exception e) {
        super(msg, e);
    }

    public CancelException(String msg) {
        super(msg);
    }
}
