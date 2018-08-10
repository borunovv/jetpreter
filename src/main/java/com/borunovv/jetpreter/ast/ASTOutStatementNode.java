package com.borunovv.jetpreter.ast;

import com.borunovv.jetpreter.interpreter.Arithmetics;
import com.borunovv.jetpreter.interpreter.Context;
import com.borunovv.jetpreter.javacc.generated.SimpleNode;

/**
 * @author borunovv
 */
public class ASTOutStatementNode extends ASTNode {
    public ASTOutStatementNode(SimpleNode wrappedNode) {
        super(wrappedNode);
    }

    @Override
    public void interpret(Context ctx) {
        // <KW_OUT> Expression()
        children.get(0).interpret(ctx);
        Object value = ctx.pop();
        String valueStr = Arithmetics.toString(value);
        ctx.writeToOutput(valueStr);
        ctx.writeToOutput("\n");
    }
}
