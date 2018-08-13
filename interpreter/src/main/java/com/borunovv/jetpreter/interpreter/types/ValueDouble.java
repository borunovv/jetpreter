package com.borunovv.jetpreter.interpreter.types;

/**
 * @author borunovv
 */
public class ValueDouble extends ValueScalar {
    private final double value;

    public ValueDouble(double value){
        super(ValueTypes.REAL);
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        String str = String.format("%.12f", value).replace(",", ".");
        if (str.contains(".") && !str.contains("e") && !str.contains("E")) {
            while (str.charAt(str.length() - 1) == '0' && str.charAt(str.length() - 2) != '.') {
                str = str.substring(0, str.length() - 1);
            }
        }
        return str;
    }
}
