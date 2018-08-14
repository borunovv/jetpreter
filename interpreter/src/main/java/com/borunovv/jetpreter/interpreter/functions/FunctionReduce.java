package com.borunovv.jetpreter.interpreter.functions;

import com.borunovv.jetpreter.interpreter.Context;
import com.borunovv.jetpreter.interpreter.InterpretException;
import com.borunovv.jetpreter.interpreter.types.*;

/**
 * Function 'reduce' implementation.
 * Example: reduce({1,10}, 0, sum next -> sum + next) will produce scalar integer value 55.
 *
 * @author borunovv
 */
public class FunctionReduce extends Function {
    public FunctionReduce() {
        super("reduce");
    }

    /**
     * Return parameters description.
     */
    @Override
    public ParamDescription[] getParamsDescription() {
        return new ParamDescription[]{
                new ParamDescription(ValueList.class), // 1st param must be a list of scalar values
                new ParamDescription(ValueScalar.class), // 2nd param must be scalar (initial accumulator value)
                new ParamDescription(ValueLambda.class) // 3rd param must be a lambda
        };
    }

    /**
     * Interpret the function.
     * Example: reduce({1,10}, 0, sum next -> sum + next) will produce scalar integer value 55.
     *
     * @param ctx interpretation context
     */
    @Override
    public void interpret(Context ctx) {
        // Since params passed to stack from first to last order,
        // we will 'pop' them in back order (i.e. from last to first).
        Value thirdArgument = ctx.pop();
        if (!(thirdArgument instanceof ValueLambda)) {
            throw new InterpretException(
                    String.format("Function 'reduce': expected 3nd argument of type %s. Actual type: %s."
                            , ValueTypes.LAMBDA, thirdArgument.getTypeName()));
        }

        Value secondArgument = ctx.pop();
        if (!(secondArgument instanceof ValueScalar)) {
            throw new InterpretException(
                    String.format("Function 'reduce': expected 2nd argument of type %s or %s. Actual type: %s."
                            , ValueTypes.INT, ValueTypes.REAL, secondArgument.getTypeName()));
        }

        Value firstArgument = ctx.pop();
        if (!(firstArgument instanceof ValueList)) {
            throw new InterpretException(
                    String.format("Function 'reduce': expected 1st argument of type %s. Actual type: %s."
                            , ValueTypes.LIST, firstArgument.getTypeName()));
        }

        ValueLambda lambda = (ValueLambda) thirdArgument;

        int expectedParamsByLambda = lambda.getLambdaNode().getParamsCount();
        if (expectedParamsByLambda != 2) {
            throw new InterpretException("Function 'reduce': 3nd argument lambda must have exactly 2 arguments.");
        }

        ValueList inputList = (ValueList) firstArgument;
        // Push initial value (accumulator)
        ctx.push(secondArgument);

        for (int i = 0; i < inputList.size(); ++i) {
            // Inside loops we do check cancellation to speed up the interpreter reaction.
            ctx.checkCancel();
            ValueScalar item = inputList.get(i);
            // Push current item
            ctx.push(item);
            // Call lambda (it will pop 2 items from stack and push back the result)
            lambda.getLambdaNode().interpret(ctx);
            Value lambdaResult = ctx.pop();
            // Ensure lambda returned scalar type.
            if (!(lambdaResult instanceof ValueScalar)) {
                throw new InterpretException("Lambda must return scalar type ("
                        + ValueTypes.INT + " or " + ValueTypes.REAL + ")."
                        + " Actual type: " + lambdaResult.getTypeName() + ".");
            }
            // Push the result onto the stack.
            ctx.push(lambdaResult);
        }
    }
}
