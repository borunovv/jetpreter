package com.borunovv.jetpreter.interpreter.functions;

import com.borunovv.jetpreter.interpreter.Context;
import com.borunovv.jetpreter.interpreter.InterpretException;
import com.borunovv.jetpreter.interpreter.types.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author borunovv
 */
public class FunctionMap extends Function {
    public FunctionMap() {
        super("map");
    }

    @Override
    public ParamDescription[] getParamsDescription() {
        return new ParamDescription[]{
                new ParamDescription(ValueList.class),
                new ParamDescription(ValueLambda.class)
        };
    }

    @Override
    public void interpret(Context ctx) {
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
            throw new InterpretException("Function 'map': 2nd argument lambda must have only 1 argument.");
        }

        ValueList inputList = (ValueList) firstArgument;
        List<ValueScalar> resultList = new ArrayList<>(inputList.size());

        for (int i = 0; i < inputList.size(); ++i) {
            ValueScalar item = inputList.get(i);
            ctx.push(item);
            lambda.getLambdaNode().interpret(ctx);
            Value lambdaResult = ctx.pop();
            if (lambdaResult instanceof ValueScalar) {
                resultList.add((ValueScalar) lambdaResult);
            } else {
                throw new InterpretException("Lambda must return scalar type ("
                        + ValueTypes.INT + " or " + ValueTypes.REAL + ")."
                        + " Actual type: " + lambdaResult.getTypeName() + ".");
            }
        }

        ctx.push(new ValueList(resultList));
    }
}
