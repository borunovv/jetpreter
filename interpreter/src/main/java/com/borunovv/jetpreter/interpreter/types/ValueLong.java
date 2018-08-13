package com.borunovv.jetpreter.interpreter.types;

/**
 * @author borunovv
 */
public class ValueLong extends ValueScalar {
    private final long value;

    public ValueLong(long value) {
        super(ValueTypes.INT);
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Long.toString(value);
    }
}
