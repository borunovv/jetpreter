package com.borunovv.jetpreter.ast;

import com.borunovv.jetpreter.interpreter.Context;
import com.borunovv.jetpreter.interpreter.InterpretException;
import com.borunovv.jetpreter.interpreter.functions.Function;
import com.borunovv.jetpreter.interpreter.types.ValueLambda;
import com.borunovv.jetpreter.javacc.generated.SimpleNode;

/**
 * @author borunovv
 */
public class ASTFunctionCallNode extends ASTNode {
    public ASTFunctionCallNode(SimpleNode wrappedNode) {
        super(wrappedNode);
    }

    @Override
    public void interpret(Context ctx) {
        String funcName = getFirstToken().image;

        Function func = ctx.getFunction(funcName);
        if (func == null) {
            throw new InterpretException("Undefined function: '" + funcName + "'.");
        }

        int actualArguments = children.size();
        int expectedArguments = func.getParamsDescription().length;

        if (actualArguments != expectedArguments) {
            throw new InterpretException("Function '" + funcName + "' arguments count mismatch: expected "
                    + expectedArguments + ", but actual is " + actualArguments + ".");
        }

        // Put every argument onto the stack (from first to last).
        for (ASTNode child : children) {
            if (child instanceof ASTLambdaNode) {
                ctx.push(new ValueLambda((ASTLambdaNode) child));
            } else {
                child.interpret(ctx);
            }
        }

        // Call function
        func.interpret(ctx);
    }
}
