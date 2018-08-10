package com.borunovv.jetpreter.interpreter;

/**
 * @author borunovv
 */
public class InterpretException extends RuntimeException {
    private int lineNumber;

    public InterpretException(int lineNumber, String msg, Exception e) {
        super(msg, e);
        this.lineNumber = lineNumber;
    }

    public InterpretException(String msg, Exception e) {
        super(msg, e);
    }

    public InterpretException(String msg) {
        super(msg);
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
}
