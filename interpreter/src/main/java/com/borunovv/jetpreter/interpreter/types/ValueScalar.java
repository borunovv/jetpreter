package com.borunovv.jetpreter.interpreter.types;

/**
 * Base class for scalar values: INT and REAL.
 *
 * @author borunovv
 */
public abstract class ValueScalar extends Value {
    /**
     * C-tor.
     *
     * @param typeName Human readable type name (like INT, REAL, LIST, LAMBDA).
     *                 Used in error messages.
     */
    public ValueScalar(String typeName) {
        super(typeName);
    }
}
