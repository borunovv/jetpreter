package com.borunovv.jetpreter.ast;

import com.borunovv.jetpreter.core.util.SystemConstants;
import com.borunovv.jetpreter.interpreter.Context;
import com.borunovv.jetpreter.interpreter.types.Value;
import com.borunovv.jetpreter.javacc.generated.SimpleNode;

/**
 * AST tree node for OutStatement() (see grammar.jjt)
 *
 * @author borunovv
 */
public class ASTOutStatementNode extends ASTNode {
    public ASTOutStatementNode(SimpleNode wrappedNode) {
        super(wrappedNode);
    }

    /**
     * Interpret the current node's child expression.
     * Then get result from stack and print it to output.
     *
     * @param ctx Interpretation context.
     */
    @Override
    public void interpret(Context ctx) {
        children.get(0).interpret(ctx);
        Value value = ctx.pop();
        ctx.writeToOutput(value.toString());
        ctx.writeToOutput(SystemConstants.LINE_SEPARATOR);
    }
}
