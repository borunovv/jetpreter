package com.borunovv.jetpreter.ast;

import com.borunovv.jetpreter.interpreter.Arithmetics;
import com.borunovv.jetpreter.interpreter.Context;
import com.borunovv.jetpreter.interpreter.types.ValueLong;
import com.borunovv.jetpreter.javacc.generated.SimpleNode;

/**
 * @author borunovv
 */
public class ASTSignedFactorNode extends ASTNode {
    public ASTSignedFactorNode(SimpleNode wrappedNode) {
        super(wrappedNode);
    }

    @Override
    public void interpret(Context ctx) {
        children.get(0).interpret(ctx);
        if (needNegate()) {
            ctx.push(Arithmetics.substract(new ValueLong(0), ctx.pop()));
        }
    }

    private boolean needNegate() {
        return getFirstToken().image.equals("-");
    }

    @Override
    protected ASTNode compact() {
        return needNegate() ?
                this :
                children.get(0).compact();
    }
}
