package com.borunovv.jetpreter.interpreter.functions;

import com.borunovv.jetpreter.interpreter.types.Value;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Function parameter description.
 *
 * @author borunovv
 */
public class ParamDescription {
    /**
     * Set of allowed types of param.
     */
    private Set<Class<? extends Value>> allowedTypes = new HashSet<>();

    /**
     * C-tor.
     *
     * @param allowedTypes list of allowed types of param.
     */
    @SafeVarargs
    public ParamDescription(Class<? extends Value>... allowedTypes) {
        this.allowedTypes.addAll(Arrays.asList(allowedTypes));
    }

    /**
     * Checks if given value has allowed type for this parameter (to be assigned).
     *
     * @param value value to check
     * @return true, if given value has allowed type for this parameter (to be assigned).
     */
    public boolean allowsValue(Value value) {
        return allowedTypes.contains(value.getClass());
    }
}
