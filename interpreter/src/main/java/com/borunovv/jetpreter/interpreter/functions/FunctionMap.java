package com.borunovv.jetpreter.interpreter.functions;

import com.borunovv.jetpreter.interpreter.Context;
import com.borunovv.jetpreter.interpreter.InterpretException;
import com.borunovv.jetpreter.interpreter.types.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Function 'map' implementation.
 * Example: map({1,10}, a -> a * 2) will produce list [2,4,6,8,10,12,14,16,18,20]
 *
 * @author borunovv
 */
public class FunctionMap extends Function {
    public FunctionMap() {
        super("map");
    }

    /**
     * Return parameters description.
     */
    @Override
    public ParamDescription[] getParamsDescription() {
        return new ParamDescription[]{
                new ParamDescription(ValueList.class), // 1st param must be a list of scalar values
                new ParamDescription(ValueLambda.class) // 2nd param must be a lambda
        };
    }

    /**
     * Interpret the function.
     * Example: map({1,10}, a -> a * 2) will produce list [2,4,6,8,10,12,14,16,18,20]
     *
     * @param ctx interpretation context
     */
    @Override
    public void interpret(Context ctx) {
        // Since params passed to stack from first to last order,
        // we will 'pop' them in back order (i.e. from last to first).
        Value secondArgument = ctx.pop();
        if (!(secondArgument instanceof ValueLambda)) {
            throw new InterpretException(
                    String.format("Function 'map': expected 2nd argument of type %s. Actual type: %s."
                            , ValueTypes.LAMBDA, secondArgument.getTypeName()));
        }

        Value firstArgument = ctx.pop();
        if (!(firstArgument instanceof ValueList)) {
            throw new InterpretException(
                    String.format("Function 'map': expected 1st argument of type %s. Actual type: %s."
                            , ValueTypes.LIST, firstArgument.getTypeName()));
        }

        ValueLambda lambda = (ValueLambda) secondArgument;

        int expectedParamsByLambda = lambda.getLambdaNode().getParamsCount();
        if (expectedParamsByLambda != 1) {
            throw new InterpretException("Function 'map': 2nd argument lambda must have exactly 1 argument.");
        }

        ValueList inputList = (ValueList) firstArgument;
        List<ValueScalar> resultList = new ArrayList<>(inputList.size());

        // Now we do apply lambda to each item in the list.
        for (int i = 0; i < inputList.size(); ++i) {
            // Inside loops we do check cancellation to speed up the interpreter reaction.
            ctx.checkCancel();
            ValueScalar item = inputList.get(i);
            ctx.push(item);
            lambda.getLambdaNode().interpret(ctx);
            Value lambdaResult = ctx.pop();
            // Ensure lambda returned scalar type.
            if (lambdaResult instanceof ValueScalar) {
                resultList.add((ValueScalar) lambdaResult);
            } else {
                throw new InterpretException("Lambda must return scalar type ("
                        + ValueTypes.INT + " or " + ValueTypes.REAL + ")."
                        + " Actual type: " + lambdaResult.getTypeName() + ".");
            }
        }
        // Push the result list onto the stack.
        ctx.push(new ValueList(resultList));
    }
}
