package com.borunovv.jetpreter.interpreter.types;

/**
 * Immutable.
 * Value for type 'INT'.
 * Uses good old java's long under the hood.
 *
 * @author borunovv
 */
public class ValueLong extends ValueScalar {
    /**
     * Wrapped value
     */
    private final long value;

    /**
     * C-tor
     * @param value initial value
     */
    public ValueLong(long value) {
        super(ValueTypes.INT);
        this.value = value;
    }

    /**
     * Return wrapped value
     */
    public long getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Long.toString(value);
    }
}
