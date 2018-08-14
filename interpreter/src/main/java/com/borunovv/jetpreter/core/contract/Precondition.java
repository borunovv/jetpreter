package com.borunovv.jetpreter.core.contract;

/**
 * Precondition checker
 *
 * @author borunovv
 */
public final class Precondition {

    /**
     * Checks the given condition and throws the {@link PreconditionException} if condition == false.
     *
     * @param condition   condition to check
     * @param description message to pass into exception if condition == false
     */
    public static void expected(boolean condition, String description) {
        if (!condition) {
            throw new PreconditionException(description);
        }
    }

    /**
     * Checks the given object for null, throws the {@link PreconditionException} if obj == null.
     *
     * @param obj         object to check
     * @param description message to pass into exception if obj == null
     */
    public static void notNull(Object obj, String description) {
        if (obj == null) {
            throw new PreconditionException(description);
        }
    }
}
