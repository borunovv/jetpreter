package com.borunovv.jetpreter.ast;

import com.borunovv.jetpreter.interpreter.Arithmetics;
import com.borunovv.jetpreter.interpreter.Context;
import com.borunovv.jetpreter.interpreter.InterpretException;
import com.borunovv.jetpreter.javacc.generated.SimpleNode;

/**
 * @author borunovv
 */
public class ASTAddOperatorNode extends ASTNode {
    public ASTAddOperatorNode(SimpleNode wrappedNode) {
        super(wrappedNode);
    }

    @Override
    public void interpret(Context ctx) {
        Object rightOperand = ctx.pop();
        Object leftOperand = ctx.pop();

        String operationStr = getFirstToken().image;
        switch (operationStr) {
            case "+":
                ctx.push(Arithmetics.add(leftOperand, rightOperand));
                break;
            case "-":
                ctx.push(Arithmetics.substract(leftOperand, rightOperand));
                break;
            default:
                throw new InterpretException("Unexpected operation: '" + operationStr + "'");
        }
    }
}
