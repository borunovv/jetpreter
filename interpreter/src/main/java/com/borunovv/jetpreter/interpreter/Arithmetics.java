package com.borunovv.jetpreter.interpreter;

import java.util.List;

/**
 * @author borunovv
 */
public class Arithmetics {

    public static Object add(Object left, Object right) {
        ensureScalars(left, right);
        if (atLeastOneIsDouble(left, right)) {
            return toDouble(left) + toDouble(right);
        } else {
            return toLong(left) + toLong(right);
        }
    }

    public static Object substract(Object left, Object right) {
        ensureScalars(left, right);
        if (atLeastOneIsDouble(left, right)) {
            return toDouble(left) - toDouble(right);
        } else {
            return toLong(left) - toLong(right);
        }
    }

    public static Object multiply(Object left, Object right) {
        ensureScalars(left, right);
        if (atLeastOneIsDouble(left, right)) {
            return toDouble(left) * toDouble(right);
        } else {
            return toLong(left) * toLong(right);
        }
    }

    public static Object divide(Object left, Object right) {
        ensureScalars(left, right);
        if (Math.abs(toDouble(right)) == 0.0) {
            throw new InterpretException("Division by zero");
        }
        if (atLeastOneIsDouble(left, right)) {
            return toDouble(left) / toDouble(right);
        } else {
            return toLong(left) / toLong(right);
        }
    }

    public static Object power(Object left, Object right) {
        ensureScalars(left, right);
        if (Math.abs(toDouble(left)) == 0.0 && toDouble(right) < 0.0) {
            throw new InterpretException("Division by zero");
        }

        return Math.pow(toDouble(left), toDouble(right));
    }

    public static String toString(Object value) {
        if (value instanceof Double) {
            return formatDouble(toDouble(value));
        } else if (value instanceof Long) {
            return value.toString();
        } else if (value instanceof List) {
            List list = (List) value;
            StringBuilder sb = new StringBuilder();
            int maxCount = Math.min(list.size(), 100);

            sb.append("[");
            for (int i = 0; i < maxCount; ++i) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(toString(list.get(i))); // recursion
            }

            boolean isTruncated = maxCount < list.size();
            if (isTruncated) {
                sb.append(", ...");
            }
            sb.append("]");

            return sb.toString();
        } else {
            throw new InterpretException("Unexpected value type: " + value.getClass().getSimpleName() + ".");
        }
    }

    private static String formatDouble(Double value) {
        String str = String.format("%.12f", value).replace(",", ".");
        if (str.contains(".")) {
            while (str.charAt(str.length() - 1) == '0' && str.charAt(str.length() - 2) != '.') {
                str = str.substring(0, str.length() - 1);
            }
        }
        return str;
    }

    private static Double toDouble(Object number) {
        return ((Number) number).doubleValue();
    }

    private static Long toLong(Object number) {
        return ((Number) number).longValue();
    }

    private static boolean atLeastOneIsDouble(Object left, Object right) {
        return left instanceof Double || right instanceof Double;
    }

    private static void ensureScalars(Object num1, Object num2) {
        ensureScalar(num1);
        ensureScalar(num2);
    }

    private static void ensureScalar(Object obj) {
        if (obj instanceof Double || obj instanceof Long) return;
        throw new InterpretException("Expected scalar value but list found.");
    }
}
