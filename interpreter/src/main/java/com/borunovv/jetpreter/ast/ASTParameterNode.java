package com.borunovv.jetpreter.ast;

import com.borunovv.jetpreter.interpreter.Context;
import com.borunovv.jetpreter.interpreter.InterpretException;
import com.borunovv.jetpreter.javacc.generated.SimpleNode;

/**
 * @author borunovv
 */
public class ASTParameterNode extends ASTNode {
    public ASTParameterNode(SimpleNode wrappedNode) {
        super(wrappedNode);
    }

    @Override
    public void interpret(Context ctx) {
        String varName = getVarName();
        if (!ctx.hasVariable(varName)) {
            throw new InterpretException("Undefined variable: '" + varName + "'. Forgot to declare ?");
        }
        ctx.push(ctx.getVariable(varName));
    }

    public String getVarName() {
        return getFirstToken().image;
    }
}
