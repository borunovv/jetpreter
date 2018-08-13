package com.borunovv.jetpreter.interpreter.functions;

import com.borunovv.jetpreter.interpreter.types.Value;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author borunovv
 */
public class ParamDescription {
    private Set<Class<? extends Value>> allowedTypes = new HashSet<>();

    @SafeVarargs
    public ParamDescription(Class<? extends Value> ... allowedTypes) {
        this.allowedTypes.addAll(Arrays.asList(allowedTypes));
    }

    public boolean allowsValue(Value value) {
        return allowedTypes.contains(value.getClass());
    }
}
