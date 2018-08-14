package com.borunovv.jetpreter.interpreter.types;

/**
 * Immutable.
 * Value for type 'REAL'.
 * Uses good old java's double under the hood.
 *
 * @author borunovv
 */
public class ValueDouble extends ValueScalar {
    /**
     * Wrapped value
     */
    private final double value;

    /**
     * C-tor
     * @param value initial value
     */
    public ValueDouble(double value) {
        super(ValueTypes.REAL);
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    /**
     * Return human readable representation for stored float point number.
     * Used in operations like 'out x' to print value into program output.
     *
     * @return human readable representation for stored float point number.
     */
    @Override
    public String toString() {
        // Let the fraction separator be always 'dot'.
        String str = String.format("%.12f", value).replace(",", ".");
        // Truncate redundant zeroes from the end.
        if (str.contains(".") && !str.contains("e") && !str.contains("E")) {
            while (str.charAt(str.length() - 1) == '0' && str.charAt(str.length() - 2) != '.') {
                str = str.substring(0, str.length() - 1);
            }
        }
        return str;
    }
}
