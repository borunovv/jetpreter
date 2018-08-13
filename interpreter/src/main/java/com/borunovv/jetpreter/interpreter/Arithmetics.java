package com.borunovv.jetpreter.interpreter;

import com.borunovv.jetpreter.interpreter.types.Value;
import com.borunovv.jetpreter.interpreter.types.ValueDouble;
import com.borunovv.jetpreter.interpreter.types.ValueScalar;
import com.borunovv.jetpreter.interpreter.types.ValueLong;

/**
 * @author borunovv
 */
public class Arithmetics {

    public static Value add(Value left, Value right) {
        ensureScalars(left, right);
        if (atLeastOneIsDouble(left, right)) {
            return new ValueDouble(toDouble(left) + toDouble(right));
        } else {
            return new ValueLong(toLong(left) + toLong(right));
        }
    }

    public static Value substract(Value left, Value right) {
        ensureScalars(left, right);
        if (atLeastOneIsDouble(left, right)) {
            return new ValueDouble(toDouble(left) - toDouble(right));
        } else {
            return new ValueLong(toLong(left) - toLong(right));
        }
    }

    public static Value multiply(Value left, Value right) {
        ensureScalars(left, right);
        if (atLeastOneIsDouble(left, right)) {
            return new ValueDouble(toDouble(left) * toDouble(right));
        } else {
            return new ValueLong(toLong(left) * toLong(right));
        }
    }

    public static Value divide(Value left, Value right) {
        ensureScalars(left, right);
        if (Math.abs(toDouble(right)) == 0.0) {
            throw new InterpretException("Division by zero");
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

    public static Value power(Value left, Value right) {
        ensureScalars(left, right);
        if (Math.abs(toDouble(left)) == 0.0 && toDouble(right) < 0.0) {
            throw new InterpretException("Division by zero");
        }

        return new ValueDouble(Math.pow(toDouble(left), toDouble(right)));
    }

    private static double toDouble(Value number) {
        ensureScalar(number);
        return (number instanceof ValueDouble) ?
                ((ValueDouble) number).getValue() :
                (double) ((ValueLong) number).getValue();
    }

    private static long toLong(Value number) {
        ensureScalar(number);
        return (number instanceof ValueLong) ?
                ((ValueLong) number).getValue() :
                (long) ((ValueDouble) number).getValue();
    }

    private static boolean atLeastOneIsDouble(Value left, Value right) {
        return left instanceof ValueDouble || right instanceof ValueDouble;
    }

    private static void ensureScalars(Value num1, Value num2) {
        ensureScalar(num1);
        ensureScalar(num2);
    }

    private static void ensureScalar(Value value) {
        if (value instanceof ValueScalar) return;
        throw new InterpretException("Expected scalar value. Actual type: " + value.getTypeName());
    }
}
