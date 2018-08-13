package com.borunovv.jetpreter.ast;

import com.borunovv.jetpreter.core.util.SystemConstants;
import com.borunovv.jetpreter.interpreter.Context;
import com.borunovv.jetpreter.interpreter.types.Value;
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
        Value value = ctx.pop();
        ctx.writeToOutput(value.toString());
        ctx.writeToOutput(SystemConstants.LINE_SEPARATOR);
    }
}
