package com.borunovv.jetpreter.ast;

import com.borunovv.jetpreter.interpreter.Context;
import com.borunovv.jetpreter.javacc.generated.SimpleNode;

/**
 * @author borunovv
 */
public class ASTAddOperationNode extends ASTNode {
    public ASTAddOperationNode(SimpleNode wrappedNode) {
        super(wrappedNode);
    }

    @Override
    public void interpret(Context ctx) {
        // MulOperation() ( AddOperator() MulOperation() )*
        int index = 0;

        ASTNode leftOperand = children.get(index++);
        leftOperand.interpret(ctx);

        while (index < children.size()) {
            ASTNode operation = children.get(index++);
            ASTNode rightOperand = children.get(index++);
            rightOperand.interpret(ctx);
            operation.interpret(ctx);
        }
    }
}
