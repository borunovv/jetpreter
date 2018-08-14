package com.borunovv.jetpreter.ast;

import com.borunovv.jetpreter.interpreter.Context;
import com.borunovv.jetpreter.interpreter.InterpretException;
import com.borunovv.jetpreter.interpreter.types.ValueDouble;
import com.borunovv.jetpreter.javacc.generated.SimpleNode;
import com.borunovv.jetpreter.javacc.generated.Token;

/**
 * AST tree node for FloatNumber() (see grammar.jjt)
 *
 * @author borunovv
 */
public class ASTFloatNumberNode extends ASTNode {
    public ASTFloatNumberNode(SimpleNode wrappedNode) {
        super(wrappedNode);
    }

    /**
     * Interpret the current node. Put result onto the stack.
     *
     * @param ctx Interpretation context.
     */
    @Override
    public void interpret(Context ctx) {
        Token t = getFirstToken();
        try {
            double value = Double.parseDouble(t.image);
            ctx.push(new ValueDouble(value));
        } catch (NumberFormatException e) {
            throw new InterpretException("Bad float number: '" + t.image + "'", e);
        }
    }
}
