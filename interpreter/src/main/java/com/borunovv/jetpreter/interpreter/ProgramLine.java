package com.borunovv.jetpreter.interpreter;

import com.borunovv.jetpreter.ast.ASTNode;
import com.borunovv.jetpreter.ast.ASTNodeFactory;
import com.borunovv.jetpreter.javacc.generated.ProgramParser;

import java.io.StringReader;

/**
 * Program line.
 * Holds AST for program line.
 * Used in caching and in interpretation line by line.
 *
 * @author borunovv
 */
public class ProgramLine {
    /**
     * Program code for this line.
     */
    private String code;

    /**
     * AST node for this line of code.
     */
    private ASTNode node;

    /**
     * Last interpretation error.
     */
    private String lastError;

    /**
     * Line number in source code. This is needed because we do skip empty lines.
     */
    private int lineNumberInSourceCode;

    /**
     * C-tor.
     *
     * @param code                   program code for the line.
     * @param lineNumberInSourceCode line number in original program source code.
     */
    public ProgramLine(String code, int lineNumberInSourceCode) {
        this.lineNumberInSourceCode = lineNumberInSourceCode;
        this.node = null;
        this.lastError = null;
        setCode(code);
    }

    /**
     * Set the program line source code.
     *
     * @param code program line source code.
     */
    private void setCode(String code) {
        // Ensure line source code ends with line separator (needed for proper grammar parsing).
        this.code = code.trim().isEmpty() ?
                "" :
                code.endsWith("\n") ?
                        code :
                        code + "\n";
    }

    public void setLineNumberInSourceCode(int lineNumber) {
        lineNumberInSourceCode = lineNumber;
    }

    public int getLineNumberInSourceCode() {
        return lineNumberInSourceCode;
    }

    /**
     * Verify syntax and build AST for the line source code.
     * Only once.
     *
     * @return parsing or building AST error (if so). On success return null.
     */
    private String verify() {
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

    /**
     * Interpret the line.
     *
     * @param ctx Interpretation context
     * @return Interpretation error (if so) or null on success.
     * @throws InterpretException
     */
    public String interpret(Context ctx) throws InterpretException {
        String error = verify();
        if (error == null && !code.isEmpty()) {
            try {
                node.interpret(ctx);
            } catch (CancelException e) {
                throw e;
            } catch (Exception e) {
                error = e.getMessage();
            }
        }
        return error;
    }
}
