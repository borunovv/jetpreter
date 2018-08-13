package com.borunovv.jetpreter.ast;

import com.borunovv.jetpreter.interpreter.Context;
import com.borunovv.jetpreter.javacc.generated.SimpleNode;

/**
 * @author borunovv
 */
public class ASTMulOperationNode extends ASTNode {
    public ASTMulOperationNode(SimpleNode wrappedNode) {
        super(wrappedNode);
    }

    @Override
    public void interpret(Context ctx) {
        // PowerOperation() ( MulOperator() PowerOperation() )*
        int index = 0;

        ASTNode leftOperand = children.get(index++);
        leftOperand.interpret(ctx);

        while (index < children.size()) {
            ctx.checkCancel();
            ASTNode operation = children.get(index++);
            ASTNode rightOperand = children.get(index++);
            rightOperand.interpret(ctx);
            operation.interpret(ctx);
        }
    }

    @Override
    protected ASTNode compact() {
        return (children.size() == 1) ?
                children.get(0).compact() :
                this;
    }
}
