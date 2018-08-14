package com.borunovv.jetpreter.ast;

import com.borunovv.jetpreter.interpreter.Context;
import com.borunovv.jetpreter.interpreter.InterpretException;
import com.borunovv.jetpreter.interpreter.types.Value;
import com.borunovv.jetpreter.javacc.generated.SimpleNode;
import com.borunovv.jetpreter.javacc.generated.Token;

/**
 * AST tree node for VarAssignment() (see grammar.jjt)
 *
 * @author borunovv
 */
public class ASTVarAssignmentNode extends ASTNode {
    public ASTVarAssignmentNode(SimpleNode wrappedNode) {
        super(wrappedNode);
    }

    /**
     * Interpret the current node.
     *
     * @param ctx Interpretation context.
     */
    @Override
    public void interpret(Context ctx) {
        // <IDENTIFIER> <EQUALS> Expression()
        Token identifierToken = getFirstToken();
        ASTNode expr = children.get(0);

        String varName = identifierToken.image;
        if (!ctx.hasVariable(varName)) {
            throw new InterpretException("Undefined variable: '" + varName + "'. Forgot to declare ?");
        }

        expr.interpret(ctx);
        Value value = ctx.pop();
        ctx.setVariable(varName, value);
    }
}
