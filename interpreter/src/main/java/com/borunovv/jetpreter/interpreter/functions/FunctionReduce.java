package com.borunovv.jetpreter.interpreter.functions;

import com.borunovv.jetpreter.interpreter.Context;
import com.borunovv.jetpreter.interpreter.InterpretException;
import com.borunovv.jetpreter.interpreter.types.*;

/**
 * @author borunovv
 */
public class FunctionReduce extends Function {
    public FunctionReduce() {
        super("reduce");
    }

    @Override
    public ParamDescription[] getParamsDescription() {
        return new ParamDescription[]{
                new ParamDescription(ValueList.class),
                new ParamDescription(ValueScalar.class),
                new ParamDescription(ValueLambda.class)
        };
    }

    @Override
    public void interpret(Context ctx) {
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
            ctx.checkCancel();
            ValueScalar item = inputList.get(i);
            // Push current item
            ctx.push(item);
            // Call lambda (it will pop 2 items from stack and push back the result)
            lambda.getLambdaNode().interpret(ctx);
            Value lambdaResult = ctx.pop();
            if (!(lambdaResult instanceof ValueScalar)) {
                throw new InterpretException("Lambda must return scalar type ("
                        + ValueTypes.INT + " or " + ValueTypes.REAL + ")."
                        + " Actual type: " + lambdaResult.getTypeName() + ".");
            }
            ctx.push(lambdaResult);
        }
    }
}
