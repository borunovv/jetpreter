package com.borunovv.jetpreter.interpreter;

import com.borunovv.jetpreter.ast.ASTNode;
import com.borunovv.jetpreter.ast.ASTNodeFactory;
import com.borunovv.jetpreter.javacc.generated.Node;
import com.borunovv.jetpreter.javacc.generated.ParseException;
import com.borunovv.jetpreter.javacc.generated.ProgramParser;

import java.io.StringReader;

/**
 * @author borunovv
 */
public class Interpreter {
    /**
     * Return syntax error description or null is syntax is fine.
     *
     * @param program program source code
     * @return syntax error description or null is syntax is fine.
     */
    public String verify(String program) {
        try {
            parseToNativeTree(program);
        } catch (ParseException e) {
            return e.getMessage();
        }
        return null;
    }

    public void run(String program, Context ctx) throws ParseException, InterpretException {
        ASTNode astRoot = ASTNodeFactory.buildTree(parseToNativeTree(program));
        astRoot.interpret(ctx);
    }

    private Node parseToNativeTree(String program) throws ParseException {
        ProgramParser parser = new ProgramParser(new StringReader(program));
        parser.Program();
        return parser.rootNode();
    }
}
