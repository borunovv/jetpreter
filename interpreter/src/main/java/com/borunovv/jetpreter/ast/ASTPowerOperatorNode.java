package com.borunovv.jetpreter.ast;

import com.borunovv.jetpreter.interpreter.Arithmetics;
import com.borunovv.jetpreter.interpreter.Context;
import com.borunovv.jetpreter.interpreter.types.Value;
import com.borunovv.jetpreter.javacc.generated.SimpleNode;

/**
 * AST tree node for PowerOperator() (see grammar.jjt)
 *
 * @author borunovv
 */
public class ASTPowerOperatorNode extends ASTNode {
    public ASTPowerOperatorNode(SimpleNode wrappedNode) {
        super(wrappedNode);
    }

    /**
     * Interpret the current node. Put result onto the stack.
     *
     * @param ctx Interpretation context.
     */
    @Override
    public void interpret(Context ctx) {
        Value rightOperand = ctx.pop();
        Value leftOperand = ctx.pop();
        ctx.push(Arithmetics.power(leftOperand, rightOperand));
    }
}
