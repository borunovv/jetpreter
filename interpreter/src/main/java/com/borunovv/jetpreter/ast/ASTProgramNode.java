package com.borunovv.jetpreter.ast;

import com.borunovv.jetpreter.interpreter.Context;
import com.borunovv.jetpreter.interpreter.InterpretException;
import com.borunovv.jetpreter.javacc.generated.SimpleNode;

/**
 * @author borunovv
 */
public class ASTProgramNode extends ASTNode {
    public ASTProgramNode(SimpleNode wrappedNode) {
        super(wrappedNode);
    }

    @Override
    public void interpret(Context ctx) {
        int lineNumber = 0;
        for (ASTNode line : children) {
            lineNumber++;
            interpretLine(ctx, lineNumber, line);
        }
    }

    private void interpretLine(Context ctx, int lineNumber, ASTNode line) {
        try {
            line.interpret(ctx);
        } catch (InterpretException e) {
            e.setLineNumber(lineNumber);
            throw e;
        }
    }
}
