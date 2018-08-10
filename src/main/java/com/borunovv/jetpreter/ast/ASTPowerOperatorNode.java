package com.borunovv.jetpreter.ast;

import com.borunovv.jetpreter.interpreter.Arithmetics;
import com.borunovv.jetpreter.interpreter.Context;
import com.borunovv.jetpreter.javacc.generated.SimpleNode;

/**
 * @author borunovv
 */
public class ASTPowerOperatorNode extends ASTNode {
    public ASTPowerOperatorNode(SimpleNode wrappedNode) {
        super(wrappedNode);
    }

    @Override
    public void interpret(Context ctx) {
        Object rightOperand = ctx.pop();
        Object leftOperand = ctx.pop();
        ctx.push(Arithmetics.power(leftOperand, rightOperand));
    }
}
