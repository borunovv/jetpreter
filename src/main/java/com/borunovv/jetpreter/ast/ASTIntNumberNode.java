package com.borunovv.jetpreter.ast;

import com.borunovv.jetpreter.interpreter.Context;
import com.borunovv.jetpreter.interpreter.InterpretException;
import com.borunovv.jetpreter.javacc.generated.SimpleNode;
import com.borunovv.jetpreter.javacc.generated.Token;

/**
 * @author borunovv
 */
public class ASTIntNumberNode extends ASTNode {
    public ASTIntNumberNode(SimpleNode wrappedNode) {
        super(wrappedNode);
    }

    @Override
    public void interpret(Context ctx) {
        Token t = getFirstToken();
        try {
            Long value = Long.parseLong(t.image);
            ctx.push(value);
        } catch (NumberFormatException e) {
            throw new InterpretException("Bad int number: '" + t.image + "'", e);
        }
    }
}
