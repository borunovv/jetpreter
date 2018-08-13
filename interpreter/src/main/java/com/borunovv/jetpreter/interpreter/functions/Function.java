package com.borunovv.jetpreter.interpreter.functions;

import com.borunovv.jetpreter.interpreter.Context;

/**
 * @author borunovv
 */
public abstract class Function {
    private String name;
    public Function(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public abstract ParamDescription[] getParamsDescription();
    public abstract void interpret(Context ctx);
}
