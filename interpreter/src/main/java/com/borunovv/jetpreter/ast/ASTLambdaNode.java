package com.borunovv.jetpreter.ast;

import com.borunovv.jetpreter.interpreter.Context;
import com.borunovv.jetpreter.interpreter.InterpretException;
import com.borunovv.jetpreter.javacc.generated.SimpleNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author borunovv
 */
public class ASTLambdaNode extends ASTNode {
    public ASTLambdaNode(SimpleNode wrappedNode) {
        super(wrappedNode);
    }

    @Override
    public void interpret(Context ctx) {
        Context lambdaCtx = ctx.cloneForLambda();

        // We expected all lambda params values on the stack (form last to first).
        List<String> paramNames = getParamNames();
        for (int i = paramNames.size() - 1; i >= 0; i--) {
            ctx.checkCancel();
            String paramName = paramNames.get(i);
            if (lambdaCtx.hasVariable(paramName)) {
                throw new InterpretException("Lambda parameter names must be unique. Duplicated parameter name is '" + paramName + "'.");
            }
            lambdaCtx.setVariable(paramNames.get(i), ctx.pop());
        }
        // Call lambda expression. Result will be on the stack of lambdaCtx
        children.get(children.size() - 1).interpret(lambdaCtx);

        // Copy result to parent context's stack
        ctx.push(lambdaCtx.pop());
    }

    public int getParamsCount() {
        return getParamNames().size();
    }

    public List<String> getParamNames() {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < children.size() - 1; ++i) {
            names.add(((ASTVarIdNode) children.get(i)).getVarName());
        }
        return names;
    }
}
