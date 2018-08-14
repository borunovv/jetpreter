package com.borunovv.jetpreter.ast;

import com.borunovv.jetpreter.interpreter.Context;
import com.borunovv.jetpreter.javacc.generated.SimpleNode;

/**
 * AST tree node for PowerOperation() (see grammar.jjt)
 *
 * @author borunovv
 */
public class ASTPowerOperationNode extends ASTNode {
    public ASTPowerOperationNode(SimpleNode wrappedNode) {
        super(wrappedNode);
    }

    /**
     * Interpret the current node. Put result onto the stack.
     *
     * @param ctx Interpretation context.
     */
    @Override
    public void interpret(Context ctx) {
        int index = 0;
        ASTNode leftOperand = children.get(index++);
        leftOperand.interpret(ctx);

        if (index < children.size()) {
            ASTNode operation = children.get(index++);
            ASTNode rightOperand = children.get(index++);
            rightOperand.interpret(ctx);
            operation.interpret(ctx);
        }
    }

    /**
     * Used to exclude redundant nodes from AST.
     * For example consider simplest expression: '5' (just integer).
     * Here the AST can look like: 'Expression->..->PowerOperation->Factor->IntNumber'.
     * This AST can be compacted to just 'IntNumber' since it is special case and we have no operations like '5^6' etc.
     *
     * @return child node, if this node can be rejected from the tree. Otherwise return self.
     */
    @Override
    protected ASTNode compact() {
        return (children.size() == 1) ?
                children.get(0).compact() :
                this;
    }
}
