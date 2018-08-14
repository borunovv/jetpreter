package com.borunovv.jetpreter.interpreter.functions;

import com.borunovv.jetpreter.interpreter.Context;

/**
 * Base class for any function. For example: map, reduce.
 *
 * @author borunovv
 */
public abstract class Function {
    /**
     * Function name, like 'map', 'reduce'.
     */
    private String name;

    public Function(String name) {
        this.name = name;
    }

    /**
     * Return function name like 'map', 'reduce'.
     */
    public String getName() {
        return name;
    }

    /**
     * Return function params description.
     */
    public abstract ParamDescription[] getParamsDescription();

    /**
     * Interpret (execute) the function.
     *
     * @param ctx interpretation context
     */
    public abstract void interpret(Context ctx);
}
