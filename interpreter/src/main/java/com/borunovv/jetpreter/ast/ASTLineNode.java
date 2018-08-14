package com.borunovv.jetpreter.ast;

import com.borunovv.jetpreter.interpreter.Context;
import com.borunovv.jetpreter.javacc.generated.SimpleNode;

/**
 * AST tree node for Line() (see grammar.jjt)
 *
 * @author borunovv
 */
public class ASTLineNode extends ASTNode {
    public ASTLineNode(SimpleNode wrappedNode) {
        super(wrappedNode);
    }

    /**
     * Interpret the current node.
     *
     * @param ctx Interpretation context.
     */
    @Override
    public void interpret(Context ctx) {
        for (ASTNode child : children) {
            child.interpret(ctx);
        }
    }
}
