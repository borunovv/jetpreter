package com.borunovv.jetpreter.ast;

import com.borunovv.jetpreter.interpreter.Context;
import com.borunovv.jetpreter.interpreter.InterpretException;
import com.borunovv.jetpreter.javacc.generated.SimpleNode;

/**
 * @author borunovv
 */
public class ASTVarIdNode extends ASTNode {
    public ASTVarIdNode(SimpleNode wrappedNode) {
        super(wrappedNode);
    }

    @Override
    public void interpret(Context ctx) {
        String varName = getFirstToken().image;
        if (!ctx.hasVariable(varName)) {
            throw new InterpretException("Undefined variable: '" + varName + "'. Forgot to declare ?");
        }
        ctx.push(ctx.getVariable(varName));
    }
}
