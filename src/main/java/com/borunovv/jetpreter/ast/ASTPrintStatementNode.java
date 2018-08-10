package com.borunovv.jetpreter.ast;

import com.borunovv.jetpreter.interpreter.Context;
import com.borunovv.jetpreter.javacc.generated.SimpleNode;
import com.borunovv.jetpreter.javacc.generated.Token;

/**
 * @author borunovv
 */
public class ASTPrintStatementNode extends ASTNode {
    public ASTPrintStatementNode(SimpleNode wrappedNode) {
        super(wrappedNode);
    }

    @Override
    public void interpret(Context ctx) {
        // <KW_PRINT> <QUOTED_STR>
        Token quotedString = getFirstToken().next;
        String unquotedString = quotedString.image.substring(1)
                .substring(0, quotedString.image.length() - 2);

        String unEscaped = unEscape(unquotedString);
        ctx.writeToOutput(unEscaped);
    }

    private String unEscape(String str) {
        return str.replaceAll("\\\\n", "\n")
                .replaceAll("\\\\t", "\t");
    }
}
