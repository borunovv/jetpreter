package com.borunovv.jetpreter.interpreter;

import com.borunovv.jetpreter.interpreter.types.Value;
import com.borunovv.jetpreter.interpreter.types.ValueDouble;
import com.borunovv.jetpreter.interpreter.types.ValueScalar;
import com.borunovv.jetpreter.interpreter.types.ValueLong;

/**
 * Utility class to perform all supported arithmetic operations on different argument types.
 * Supported operations are: '+', '-', '*', '/', '^' (power).
 *
 * @author borunovv
 */
public class Arithmetics {

    /**
     * Perform addition.
     * Returned value type depends on input values type:
     * <pre>
     * int + int = int
     * int + real = real
     * real + int = real
     * real + real = real
     * </pre>
     *
     * @param left  left operand
     * @param right right operand
     * @return left + right
     */
    public static Value add(Value left, Value right) {
        ensureScalars(left, right);
        if (atLeastOneIsDouble(left, right)) {
            return new ValueDouble(toDouble(left) + toDouble(right));
        } else {
            return new ValueLong(toLong(left) + toLong(right));
        }
    }

    /**
     * Perform substraction.
     * Returned value type depends on input values type:
     * <pre>
     * int - int = int
     * int - real = real
     * real - int = real
     * real - real = real
     * </pre>
     *
     * @param left  left operand
     * @param right right operand
     * @return left - right
     */
    public static Value substract(Value left, Value right) {
        ensureScalars(left, right);
        if (atLeastOneIsDouble(left, right)) {
            return new ValueDouble(toDouble(left) - toDouble(right));
        } else {
            return new ValueLong(toLong(left) - toLong(right));
        }
    }

    /**
     * Perform multiplication.
     * Returned value type depends on input values type:
     * <pre>
     * int * int = int
     * int * real = real
     * real * int = real
     * real * real = real
     * </pre>
     *
     * @param left  left operand
     * @param right right operand
     * @return left * right
     */
    public static Value multiply(Value left, Value right) {
        ensureScalars(left, right);
        if (atLeastOneIsDouble(left, right)) {
            return new ValueDouble(toDouble(left) * toDouble(right));
        } else {
            return new ValueLong(toLong(left) * toLong(right));
        }
    }

    /**
     * Perform division.
     * Returned value type depends on input values type:
     * <pre>
     * int / int = int (if no fraction occurred) or real
     * int / real = real
     * real / int = real
     * real / real = real
     * </pre>
     * Can throw  {@link InterpretException} if division by zero occurred.
     *
     * @param left  left operand
     * @param right right operand
     * @return left / right
     */
    public static Value divide(Value left, Value right) {
        ensureScalars(left, right);
        if (Math.abs(toDouble(right)) == 0.0) {
            throw new InterpretException("Division by zero.");
        }
        if (atLeastOneIsDouble(left, right)) {
            return new ValueDouble(toDouble(left) / toDouble(right));
        } else {
            long result = toLong(left) / toLong(right);
            if (result * toLong(right) != toLong(left)) {
                return new ValueDouble(toDouble(left) / toDouble(right));
            } else {
                return new ValueLong(result);
            }
        }
    }

    /**
     * Perform power operation.
     * Returned value type always real. Example: 2^10 = 1024.0<br/>
     * <p>
     * Can throw  {@link InterpretException} if division by zero occurred.
     *
     * @param left  left operand
     * @param right right operand
     * @return left ^ right
     */
    public static Value power(Value left, Value right) {
        ensureScalars(left, right);
        if (Math.abs(toDouble(left)) == 0.0 && toDouble(right) < 0.0) {
            throw new InterpretException("Division by zero.");
        }

        return new ValueDouble(Math.pow(toDouble(left), toDouble(right)));
    }

    /**
     * Force convert value to double.
     *
     * @param number number value (expected scalar)
     * @return double representation of given value
     */
    private static double toDouble(Value number) {
        ensureScalar(number);
        return (number instanceof ValueDouble) ?
                ((ValueDouble) number).getValue() :
                (double) ((ValueLong) number).getValue();
    }

    /**
     * Force convert value to long.
     *
     * @param number number value (expected scalar)
     * @return long representation of given value
     */
    private static long toLong(Value number) {
        ensureScalar(number);
        return (number instanceof ValueLong) ?
                ((ValueLong) number).getValue() :
                (long) ((ValueDouble) number).getValue();
    }

    /**
     * Check if at least one of given arguments is of type ValueDouble.
     *
     * @return true if at least one of given parameters is of type ValueDouble
     */
    private static boolean atLeastOneIsDouble(Value first, Value second) {
        return first instanceof ValueDouble
                || second instanceof ValueDouble;
    }

    /**
     * Check if both of given arguments are scalars (of type ValueScalar).
     *
     * @return if both of given arguments are scalars (of type ValueScalar).
     */
    private static void ensureScalars(Value first, Value second) {
        ensureScalar(first);
        ensureScalar(second);
    }

    /**
     * Check if given value is scalar (of type ValueScalar).
     *
     * @return true, if given value is scalar (of type ValueScalar).
     */
    private static void ensureScalar(Value value) {
        if (value instanceof ValueScalar) return;
        throw new InterpretException("Expected scalar value. Actual type: " + value.getTypeName());
    }
}
