package com.borunovv.jetpreter.interpreter.types;

/**
 * Base class for all possible value types (int, real, list, lambda).
 *
 * @author borunovv
 */
public abstract class Value {
    /**
     * Human readable type name (like INT, REAL, LIST, LAMBDA).
     * Used in error messages.
     */
    private String typeName;

    /**
     * C-tor
     * @param typeName Human readable type name (like INT, REAL, LIST, LAMBDA). Used in error messages.
     */
    public Value(String typeName) {
        this.typeName = typeName;
    }

    /**
     * Return human readable type name (like INT, REAL, LIST, LAMBDA). Used in error messages.
     */
    public String getTypeName() {
        return typeName;
    }
}
