package com.borunovv.jetpreter.ast;

import com.borunovv.jetpreter.interpreter.Context;
import com.borunovv.jetpreter.interpreter.InterpretException;
import com.borunovv.jetpreter.javacc.generated.SimpleNode;

/**
 * AST tree node for Program() (see grammar.jjt)
 *
 * @author borunovv
 */
public class ASTProgramNode extends ASTNode {
    public ASTProgramNode(SimpleNode wrappedNode) {
        super(wrappedNode);
    }

    /**
     * Interpret the current node. Put result onto the stack.
     *
     * @param ctx Interpretation context.
     */
    @Override
    public void interpret(Context ctx) {
        int lineNumber = 0;
        for (ASTNode line : children) {
            lineNumber++;
            interpretLine(ctx, lineNumber, line);
        }
    }

    /**
     * Interpret one line of program code.
     *
     * @param ctx        Interpretation context
     * @param lineNumber line number in source code
     * @param line AST line node
     */
    private void interpretLine(Context ctx, int lineNumber, ASTNode line) {
        try {
            line.interpret(ctx);
        } catch (InterpretException e) {
            e.setLineNumber(lineNumber);
            throw e;
        }
    }
}
