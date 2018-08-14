package com.borunovv.jetpreter.ast;

import com.borunovv.jetpreter.interpreter.Context;
import com.borunovv.jetpreter.interpreter.InterpretException;
import com.borunovv.jetpreter.interpreter.types.Value;
import com.borunovv.jetpreter.javacc.generated.SimpleNode;
import com.borunovv.jetpreter.javacc.generated.Token;

/**
 * AST tree node for VarDeclaration() (see grammar.jjt)
 *
 * @author borunovv
 */
public class ASTVarDeclarationNode extends ASTNode {
    public ASTVarDeclarationNode(SimpleNode wrappedNode) {
        super(wrappedNode);
    }

    /**
     * Interpret the current node.
     *
     * @param ctx Interpretation context.
     */
    @Override
    public void interpret(Context ctx) {
        //  <KW_VAR> <IDENTIFIER> <EQUALS> Expression()
        Token kwToken = getFirstToken();
        Token identifierToken = kwToken.next;

        String varName = identifierToken.image;
        if (ctx.isKeyWord(varName)) {
            throw new InterpretException("Bad variable name: '" + varName + "' (reserved keyword).");
        }

        if (ctx.hasVariable(varName)) {
            throw new InterpretException("Variable '" + varName + "' already declared.");
        }

        ASTNode expr = children.get(0);
        expr.interpret(ctx);
        Value value = ctx.pop();

        ctx.setVariable(varName, value);
    }
}
