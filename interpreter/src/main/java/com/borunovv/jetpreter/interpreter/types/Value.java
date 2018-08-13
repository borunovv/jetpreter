package com.borunovv.jetpreter.interpreter.types;

/**
 * @author borunovv
 */
public abstract class Value {
    private String typeName;

    public Value(String typeName) {
        this.typeName = typeName;
    }
    public String getTypeName() {
        return typeName;
    }
}
