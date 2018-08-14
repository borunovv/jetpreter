package com.borunovv.jetpreter.ast;

import com.borunovv.jetpreter.core.util.SystemConstants;
import com.borunovv.jetpreter.interpreter.Context;
import com.borunovv.jetpreter.javacc.generated.SimpleNode;
import com.borunovv.jetpreter.javacc.generated.Token;

/**
 * AST tree node for PrintStatement() (see grammar.jjt)
 *
 * @author borunovv
 */
public class ASTPrintStatementNode extends ASTNode {
    public ASTPrintStatementNode(SimpleNode wrappedNode) {
        super(wrappedNode);
    }

    /**
     * Interpret the current node.
     * Then get result from stack and print it to output.
     *
     * @param ctx Interpretation context.
     */
    @Override
    public void interpret(Context ctx) {
        // (see grammar.jjt for PrintStatement())
        //
        // first_token second_token
        //      /         \
        // <KW_PRINT> <QUOTED_STR>
        Token quotedString = getFirstToken().next;
        String unquotedString = quotedString.image.substring(1)
                .substring(0, quotedString.image.length() - 2);
        String unEscaped = unEscape(unquotedString);
        ctx.writeToOutput(unEscaped);
    }

    private String unEscape(String str) {
        return str.replaceAll("\\\\n", SystemConstants.LINE_SEPARATOR)
                .replaceAll("\\\\t", "\t");
    }
}
