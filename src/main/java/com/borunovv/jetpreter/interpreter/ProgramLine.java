package com.borunovv.jetpreter.interpreter;

import com.borunovv.jetpreter.ast.ASTNode;
import com.borunovv.jetpreter.ast.ASTNodeFactory;
import com.borunovv.jetpreter.javacc.generated.ProgramParser;

import java.io.StringReader;

/**
 * @author borunovv
 */
public class ProgramLine {
    private String code;
    private ASTNode node;
    private String lastError;

    public ProgramLine(String code) {
        update(code);
    }

    public void update(String code) {
        if (code.equals(this.code)) {
            return;
        }

        this.node = null;
        this.lastError = null;

        this.code = code.trim().isEmpty() ?
                "" :
                code.endsWith("\n") ?
                        code :
                        code + "\n";
    }

    public String verify() {
        if (node == null && this.lastError == null && !code.isEmpty()) {
            try {
                ProgramParser parser = new ProgramParser(new StringReader(code));
                parser.Line();
                node = ASTNodeFactory.buildTree(parser.rootNode());
            } catch (Exception e) {
                this.lastError = e.getMessage().replace(" at line 1, column", ", column");
            }
        }
        return this.lastError;
    }

    public void interpret(Context ctx) throws InterpretException {
        String error = verify();
        if (error != null) {
            throw new InterpretException(error);
        }
        if (!code.isEmpty()) {
            node.interpret(ctx);
        }
    }
}
