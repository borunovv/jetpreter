package com.borunovv.jetpreter.interpreter;

/**
 * Special exception indicating cancellation during interpretation process.
 */
public class CancelException extends InterpretException {

    public CancelException(int lineNumber, String msg) {
        super(lineNumber, msg, null);
    }

    public CancelException(String msg) {
        super(msg);
    }
}
