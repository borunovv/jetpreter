package com.borunovv.jetpreter.ast;

import com.borunovv.jetpreter.interpreter.Context;
import com.borunovv.jetpreter.interpreter.InterpretException;
import com.borunovv.jetpreter.javacc.generated.SimpleNode;

/**
 * AST tree node for VarId() (see grammar.jjt)
 *
 * @author borunovv
 */
public class ASTVarIdNode extends ASTNode {
    public ASTVarIdNode(SimpleNode wrappedNode) {
        super(wrappedNode);
    }

    /**
     * Interpret the current node.
     * Takes value from context and push it onto the stack.
     *
     * @param ctx Interpretation context.
     */
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
