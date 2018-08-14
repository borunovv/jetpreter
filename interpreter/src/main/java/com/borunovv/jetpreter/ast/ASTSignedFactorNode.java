package com.borunovv.jetpreter.ast;

import com.borunovv.jetpreter.interpreter.Arithmetics;
import com.borunovv.jetpreter.interpreter.Context;
import com.borunovv.jetpreter.interpreter.types.ValueLong;
import com.borunovv.jetpreter.javacc.generated.SimpleNode;

/**
 * AST tree node for SignedFactor() (see grammar.jjt)
 *
 * @author borunovv
 */
public class ASTSignedFactorNode extends ASTNode {
    public ASTSignedFactorNode(SimpleNode wrappedNode) {
        super(wrappedNode);
    }

    /**
     * Interpret the current node. Put result onto the stack.
     *
     * @param ctx Interpretation context.
     */
    @Override
    public void interpret(Context ctx) {
        children.get(0).interpret(ctx);
        if (needNegate()) {
            ctx.push(Arithmetics.substract(new ValueLong(0), ctx.pop()));
        }
    }

    /**
     * Return true if this factor has preceding minus sign '-'.
     *
     * @return true if this factor has preceding minus sign '-'.
     */
    private boolean needNegate() {
        return getFirstToken().image.equals("-");
    }

    /**
     * Used to exclude redundant nodes from AST.
     * For example consider simplest expression: '5' (just integer).
     * Here the AST can look like: 'SignedFactor->IntNumber'.
     * This AST can be compacted to just 'IntNumber' since it is special case and we have no minus sign like '-5' etc.
     *
     * @return child node, if this node can be rejected from the tree. Otherwise return self.
     */
    @Override
    protected ASTNode compact() {
        return needNegate() ?
                this :
                children.get(0).compact();
    }
}
